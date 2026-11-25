package com.mlc.mlc.mlcmain.mobmove;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class MobMove implements Listener {
    private final JavaPlugin plugin;
    private final Set<EntityType> allowedTypes = EnumSet.noneOf(EntityType.class);
    private final Map<UUID, Entity> carried = new HashMap<>();

    public MobMove(JavaPlugin plugin) {
        this.plugin = plugin;
        File file = new File(plugin.getDataFolder(), "mobmove/config.yml");
        if (!file.exists()) {
            if (plugin.getConfig().isSet("mobmove.allowed-entities")) {
                YamlConfiguration migrated = new YamlConfiguration();
                migrated.set("allowed-entities", plugin.getConfig().getStringList("mobmove.allowed-entities"));
                try {
                    migrated.save(file);
                    plugin.getLogger().info("已将生物搬运白名单迁移到 mobmove/config.yml");
                } catch (IOException exception) {
                    throw new IllegalStateException("无法保存生物搬运配置 " + file, exception);
                }
            } else {
                plugin.saveResource("mobmove/config.yml", false);
            }
        }
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException exception) {
            throw new IllegalStateException("无法读取生物搬运配置 " + file, exception);
        }
        for (String name : config.getStringList("allowed-entities")) {
            try {
                allowedTypes.add(EntityType.valueOf(name.toUpperCase(Locale.ROOT)));
            } catch (IllegalArgumentException ex) {
                plugin.getLogger().warning("未知的可举起实体类型: " + name);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInteract(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        Entity target = event.getRightClicked();
        if (event.getHand() != EquipmentSlot.HAND || !player.isSneaking()
                || !(target instanceof LivingEntity) || !allowedTypes.contains(target.getType())
                || player.isInsideVehicle() || !player.getPassengers().isEmpty()
                || target.isInsideVehicle() || !target.getPassengers().isEmpty()) {
            return;
        }
        // A previous passenger may have dismounted independently.
        carried.remove(player.getUniqueId());
        if (player.addPassenger(target)) {
            carried.put(player.getUniqueId(), target);
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onSneak(PlayerToggleSneakEvent event) {
        if (event.isSneaking()) {
            release(event.getPlayer());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        cleanup(event.getPlayer());
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        cleanup(event.getEntity());
    }

    private void release(Player player) {
        Entity passenger = carried.remove(player.getUniqueId());
        if (passenger != null && passenger.getVehicle() == player) {
            player.removePassenger(passenger);
        }
    }

    private void cleanup(Entity entity) {
        if (entity instanceof Player player) {
            release(player);
        }
        carried.entrySet().removeIf(entry -> entry.getValue().equals(entity));
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        if (event.getPlugin() != plugin) {
            return;
        }
        for (Map.Entry<UUID, Entity> entry : carried.entrySet()) {
            Entity passenger = entry.getValue();
            Entity vehicle = passenger.getVehicle();
            if (vehicle != null && vehicle.getUniqueId().equals(entry.getKey())) {
                vehicle.removePassenger(passenger);
            }
        }
        carried.clear();
    }
}
