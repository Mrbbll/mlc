package com.mlc.mlc.mlcmain.enchantments.moreenchants;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerRiptideEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.random.RandomGenerator;

/** 易损、脆弱的注册表访问与使用效果。 */
public final class NegativeEnchantments implements Listener {
    public static final int EXTRA_DURABILITY_DAMAGE = 1;
    public static final double FRAGILE_BREAK_CHANCE = 0.01;
    private final Map<UUID, Integer> lastBowShot = new HashMap<>();

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        lastBowShot.remove(event.getPlayer().getUniqueId());
    }

    private static Enchantment brittle() {
        return RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).getOrThrow(EnchantmentKeys.BRITTLE);
    }

    private static Enchantment fragile() {
        return RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).getOrThrow(EnchantmentKeys.FRAGILE);
    }

    public static boolean isNegative(Enchantment enchantment) {
        return enchantment.equals(brittle()) || enchantment.equals(fragile());
    }

    public static Enchantment randomApplicable(ItemStack item, RandomGenerator random) {
        if (!(item.getItemMeta() instanceof Damageable damageable) || damageable.isUnbreakable()) return null;
        List<Enchantment> available = List.of(brittle(), fragile()).stream()
                .filter(enchantment -> enchantment.canEnchantItem(item) && !item.containsEnchantment(enchantment))
                .toList();
        return available.isEmpty() ? null : available.get(random.nextInt(available.size()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        applyToSlot(event.getPlayer(), EquipmentSlot.HAND);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAttack(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player && event.getFinalDamage() > 0) {
            applyToSlot(player, EquipmentSlot.HAND);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onArmorDamage(PlayerItemDamageEvent event) {
        Player player = event.getPlayer();
        int durabilityDamage = event.getDamage();
        if (player.getGameMode() == GameMode.CREATIVE || durabilityDamage <= 0) return;

        ItemStack item = event.getItem();
        if (!(item.getItemMeta() instanceof Damageable meta) || meta.isUnbreakable()) return;
        boolean brittle = item.containsEnchantment(brittle());
        boolean fragile = item.containsEnchantment(fragile());
        if (!brittle && !fragile) return;

        for (ItemStack armor : player.getInventory().getArmorContents()) {
            if (!item.equals(armor)) continue;

            int maximum = meta.hasMaxDamage() ? meta.getMaxDamage() : item.getType().getMaxDurability();
            int current = meta.getDamage();
            int remaining = maximum - current;
            if (remaining <= 0 || durabilityDamage >= remaining) return;

            int damage = damageAfterUse(current + durabilityDamage, maximum, brittle, fragile,
                    ThreadLocalRandom.current().nextDouble());
            event.setDamage(damage < 0 ? remaining : damage - current);
            return;
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onShoot(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player player && event.getBow() != null && event.getHand() != null) {
            int tick = org.bukkit.Bukkit.getCurrentTick();
            if (lastBowShot.getOrDefault(player.getUniqueId(), Integer.MIN_VALUE) == tick) return;
            lastBowShot.put(player.getUniqueId(), tick);
            applyToSlot(player, event.getHand());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTrident(ProjectileLaunchEvent event) {
        if (!(event.getEntity() instanceof Trident trident) || !(trident.getShooter() instanceof Player player)
                || player.getGameMode() == GameMode.CREATIVE) return;
        ItemStack item = trident.getItemStack();
        if (item == null || (!item.containsEnchantment(brittle()) && !item.containsEnchantment(fragile()))) return;
        ItemStack result = apply(player, item);
        if (result == null) trident.remove();
        else trident.setItemStack(result);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onRiptide(PlayerRiptideEvent event) {
        Player player = event.getPlayer();
        EquipmentSlot slot = player.getInventory().getItemInMainHand().getType() == Material.TRIDENT
                ? EquipmentSlot.HAND : EquipmentSlot.OFF_HAND;
        applyToSlot(player, slot);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.FISHING) return;
        Player player = event.getPlayer();
        EquipmentSlot slot = player.getInventory().getItemInMainHand().getType() == Material.FISHING_ROD
                ? EquipmentSlot.HAND : EquipmentSlot.OFF_HAND;
        applyToSlot(player, slot);
    }

    private static void applyToSlot(Player player, EquipmentSlot slot) {
        if (player.getGameMode() == GameMode.CREATIVE) return;
        ItemStack item = player.getInventory().getItem(slot);
        ItemStack result = apply(player, item);
        if (result == null) {
            item.setAmount(0);
            player.getInventory().setItem(slot, null);
        }
    }

    /** null 表示物品损毁，原对象表示没有变化。 */
    private static ItemStack apply(Player player, ItemStack item) {
        if (item == null || item.getType().isAir() || !(item.getItemMeta() instanceof Damageable meta)
                || meta.isUnbreakable()) return item;
        boolean brittle = item.containsEnchantment(brittle());
        boolean fragile = item.containsEnchantment(fragile());
        if (!brittle && !fragile) return item;
        int maximum = meta.hasMaxDamage() ? meta.getMaxDamage() : item.getType().getMaxDurability();
        if (maximum <= 0) return item;
        int damage = damageAfterUse(meta.getDamage(), maximum, brittle, fragile,
                ThreadLocalRandom.current().nextDouble());
        if (damage < 0) {
            player.getServer().getPluginManager().callEvent(new PlayerItemBreakEvent(player, item.clone()));
            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
            return null;
        }
        if (!brittle) return item;
        meta.setDamage(damage);
        item.setItemMeta(meta);
        return item;
    }

    static int damageAfterUse(int current, int maximum, boolean brittle, boolean fragile, double roll) {
        int damage = current + (brittle ? EXTRA_DURABILITY_DAMAGE : 0);
        return (fragile && roll < FRAGILE_BREAK_CHANCE) || damage >= maximum ? -1 : damage;
    }
}
