package com.mlc.mlcbot;

import com.mlc.mlcbot.navigation.BotNavigator;
import com.mlc.mlcbot.nms.BotPlayer;
import com.mlc.mlcbot.practice.PracticeBotPlugin;
import com.mlc.mlcbot.combat.CombatRuntime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import io.papermc.paper.event.entity.EntityKnockbackEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

public final class BotManager implements Listener, AutoCloseable {
    private static final int MAX_BOTS = 32;
    private final JavaPlugin plugin;
    private final PracticeBotPlugin combat;
    private final Map<UUID, BotSession> sessions = new HashMap<>();
    private final Map<UUID, BotSession> entities = new HashMap<>();
    private final BukkitTask task;
    private long tick;

    public BotManager(JavaPlugin plugin) {
        this.plugin = plugin;
        combat = new PracticeBotPlugin(plugin);
        Bukkit.getPluginManager().registerEvents(this, plugin);
        task = Bukkit.getScheduler().runTaskTimer(plugin, this::tick, 1, 1);
    }

    public void spawn(Player owner, BotType type, BotStrength strength) {
        if (owner.isDead() || owner.getGameMode() == GameMode.SPECTATOR || owner.getGameMode() == GameMode.CREATIVE) {
            throw new IllegalArgumentException("请在生存或冒险模式下生成机器人。");
        }
        if (!sessions.containsKey(owner.getUniqueId()) && sessions.size() >= MAX_BOTS) {
            throw new IllegalArgumentException("服务器机器人数量已达到上限 (32)。");
        }
        Location at = findSpawn(owner);
        if (at == null) throw new IllegalArgumentException("附近没有安全的生成位置，请移动到空旷地面。");
        BotPlayer handle = new BotPlayer(plugin, at, "Bot_" + UUID.randomUUID().toString().substring(0, 8));
        BotSession session = new BotSession(owner.getUniqueId(), type, strength, handle);
        try {
            BotIdentity.mark(handle.getBukkitEntity());
            handle.getBukkitEntity().setGameMode(GameMode.SURVIVAL);
            DefaultKits.equip(handle.getBukkitEntity(), type);
            session.brain.initialize(combat, session);
            // Clients must know the profile before the world tracker sends the PLAYER spawn packet.
            for (Player viewer : Bukkit.getOnlinePlayers()) sendProfile(viewer, handle);
            handle.level().addNewPlayer(handle);
            if (!handle.getBukkitEntity().isValid()) throw new IllegalStateException("假人实体生成失败。");
        } catch (RuntimeException | LinkageError failure) {
            session.brain.close();
            handle.destroy();
            removeProfile(handle);
            throw failure;
        }
        remove(owner.getUniqueId());
        sessions.put(owner.getUniqueId(), session);
        entities.put(handle.getUUID(), session);
    }

    public boolean remove(UUID owner) {
        BotSession session = sessions.remove(owner);
        if (session == null) return false;
        entities.remove(session.handle.getUUID());
        try {
            session.brain.close();
        } finally {
            session.handle.destroy();
            removeProfile(session.handle);
        }
        return true;
    }

    private void tick() {
        ++tick;
        for (BotSession session : new ArrayList<>(sessions.values())) {
            Player owner = Bukkit.getPlayer(session.owner);
            Player bot = session.handle.getBukkitEntity();
            if (session.brain.isStopped() || owner == null || !owner.isOnline() || owner.isDead() || bot.isDead() || !bot.isValid()
                    || !owner.getWorld().equals(bot.getWorld())
                    || owner.getLocation().distanceSquared(bot.getLocation()) > 48 * 48
                    || !bot.getWorld().isChunkLoaded(bot.getLocation().getBlockX() >> 4, bot.getLocation().getBlockZ() >> 4)) {
                remove(session.owner);
                continue;
            }
            if (owner.getGameMode() == GameMode.CREATIVE || owner.getGameMode() == GameMode.SPECTATOR) {
                session.brain.pause();
                continue;
            }
            // Replenish a consumed totem from the fixed finite reserve after the native resurrection completes.
            if (session.totems > 0 && bot.getInventory().getItemInOffHand().getType().isAir()) {
                bot.getInventory().setItemInOffHand(new ItemStack(Material.TOTEM_OF_UNDYING));
            }
            try {
                session.brain.tick(session, owner, tick);
            } catch (RuntimeException | LinkageError error) {
                plugin.getLogger().log(Level.SEVERE, "mlc-bot 战斗更新失败，已清理机器人", error);
                remove(session.owner);
            }
        }
    }

    private static Location findSpawn(Player owner) {
        Vector forward = owner.getLocation().getDirection().setY(0);
        if (forward.lengthSquared() < 0.001) forward = new Vector(0, 0, 1);
        Location center = owner.getLocation().add(forward.normalize().multiply(3));
        for (int radius = 0; radius <= 2; radius++) {
            for (int x = -radius; x <= radius; x++) for (int z = -radius; z <= radius; z++) {
                if (Math.max(Math.abs(x), Math.abs(z)) != radius) continue;
                for (int y : new int[]{0, 1, -1}) {
                    Location at = center.clone().add(x, y, z);
                    at.setX(at.getBlockX() + 0.5);
                    at.setY(at.getBlockY());
                    at.setZ(at.getBlockZ() + 0.5);
                    if (!BotNavigator.canStand(at) || !at.getWorld().getWorldBorder().isInside(at)) continue;
                    BoundingBox box = new BoundingBox(at.getX() - 0.3, at.getY(), at.getZ() - 0.3,
                            at.getX() + 0.3, at.getY() + 1.8, at.getZ() + 0.3);
                    if (at.getWorld().getNearbyEntities(box).isEmpty()) return at;
                }
            }
        }
        return null;
    }

    private static void sendProfile(Player viewer, BotPlayer bot) {
        ((CraftPlayer) viewer).getHandle().connection.send(new ClientboundPlayerInfoUpdatePacket(
                EnumSet.of(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER,
                        ClientboundPlayerInfoUpdatePacket.Action.UPDATE_GAME_MODE), List.of(bot)));
    }

    private static void removeProfile(BotPlayer bot) {
        var packet = new ClientboundPlayerInfoRemovePacket(List.of(bot.getUUID()));
        for (Player viewer : Bukkit.getOnlinePlayers()) ((CraftPlayer) viewer).getHandle().connection.send(packet);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        for (BotSession session : sessions.values()) sendProfile(event.getPlayer(), session.handle);
    }

    @EventHandler public void onQuit(PlayerQuitEvent event) { remove(event.getPlayer().getUniqueId()); }
    @EventHandler public void onWorldChange(PlayerChangedWorldEvent event) { remove(event.getPlayer().getUniqueId()); }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDeath(PlayerDeathEvent event) {
        BotSession session = entities.get(event.getEntity().getUniqueId());
        if (session == null) { remove(event.getEntity().getUniqueId()); return; }
        event.getDrops().clear();
        event.setDroppedExp(0);
        event.setKeepInventory(true);
        event.deathMessage(null);
        // Defer native entity removal until its death handling has returned.
        Bukkit.getScheduler().runTask(plugin, () -> {
            if (sessions.get(session.owner) == session) remove(session.owner);
        });
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onResurrect(EntityResurrectEvent event) {
        BotSession session = entities.get(event.getEntity().getUniqueId());
        if (session != null) session.totems = Math.max(0, session.totems - 1);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        BotSession session = entities.get(event.getEntity().getUniqueId());
        if (session != null && event.getFinalDamage() > 0) {
            session.brain.hit(event.getFinalDamage(), event.getDamager() instanceof Player);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onProjectile(ProjectileLaunchEvent event) {
        CombatRuntime.trackProjectile(event.getEntity());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onKnockback(EntityKnockbackEvent event) {
        BotSession session = entities.get(event.getEntity().getUniqueId());
        if (session != null && event.getKnockback().lengthSquared() > 0.0001) session.brain.knockedBack();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        if (entities.containsKey(event.getPlayer().getUniqueId())) {
            event.setDropItems(false);
            event.setExpToDrop(0);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCrystalExplosion(EntityExplodeEvent event) {
        if (entities.containsKey(event.getEntity().getUniqueId())) {
            event.blockList().clear();
            return;
        }
        for (BotSession session : sessions.values()) {
            if (session.brain.ownsCrystal(event.getEntity().getUniqueId())) {
                // A training crystal deals native damage but leaves the arena blocks intact.
                event.blockList().clear();
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWorldUnload(WorldUnloadEvent event) {
        for (BotSession session : new ArrayList<>(sessions.values())) {
            if (session.handle.getBukkitEntity().getWorld().equals(event.getWorld())) remove(session.owner);
        }
    }

    @Override public void close() {
        task.cancel();
        for (UUID owner : new ArrayList<>(sessions.keySet())) remove(owner);
        PracticeBotPlugin.shutdown();
    }
}
