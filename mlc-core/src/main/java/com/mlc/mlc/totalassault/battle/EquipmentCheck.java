package com.mlc.mlc.totalassault.battle;

import com.mlc.mlc.totalassault.boss.AssaultBoss;
import com.mlc.mlc.totalassault.data.Banneditem;
import com.mlc.mlc.totalassault.data.TotalAssaultStorage;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

/** 检查进场物品，并保存进场时的完整快照。 */
public final class EquipmentCheck {
    public record Result(List<String> errors, List<TotalAssaultStorage.AttemptItem> items, Set<String> identities) {}

    private EquipmentCheck() {}

    public static Result check(Player player, Set<String> used, Set<String> savedAttempts, AssaultBoss boss, JavaPlugin plugin) {
        PlayerInventory inventory = player.getInventory();
        List<String> errors = new ArrayList<>();
        List<TotalAssaultStorage.AttemptItem> items = new ArrayList<>();
        Set<String> identities = new HashSet<>();
        NamespacedKey attemptKey = new NamespacedKey(plugin, "totalassault_attempt");
        for (int slot = 5; slot < 36; slot++) {
            ItemStack item = inventory.getItem(slot);
            if (item != null && !item.getType().isAir()) errors.add("背包第 " + (slot + 1) + " 格必须为空。");
        }
        add("MAIN_HAND", "快捷栏第 1 格", inventory.getItem(0), false, used, savedAttempts, attemptKey, errors, items, identities, boss);
        add("HOTBAR_2", "快捷栏第 2 格", inventory.getItem(1), true, used, savedAttempts, attemptKey, errors, items, identities, boss);
        add("HOTBAR_3", "快捷栏第 3 格", inventory.getItem(2), true, used, savedAttempts, attemptKey, errors, items, identities, boss);
        add("HOTBAR_4", "快捷栏第 4 格", inventory.getItem(3), true, used, savedAttempts, attemptKey, errors, items, identities, boss);
        add("HOTBAR_5", "快捷栏第 5 格", inventory.getItem(4), true, used, savedAttempts, attemptKey, errors, items, identities, boss);
        add("HEAD", "头盔", inventory.getHelmet(), false, used, savedAttempts, attemptKey, errors, items, identities, boss);
        add("CHEST", "胸甲", inventory.getChestplate(), false, used, savedAttempts, attemptKey, errors, items, identities, boss);
        add("LEGS", "护腿", inventory.getLeggings(), false, used, savedAttempts, attemptKey, errors, items, identities, boss);
        add("FEET", "靴子", inventory.getBoots(), false, used, savedAttempts, attemptKey, errors, items, identities, boss);
        add("OFF_HAND", "副手", inventory.getItemInOffHand(), false, used, savedAttempts, attemptKey, errors, items, identities, boss);
        return new Result(errors, items, identities);
    }

    /** 出场清理仍按进场物品的模型或材质识别，与判重规则分开。 */
    public static String identity(ItemStack item) {
        String identity = Banneditem.identity(item);
        if (identity == null) return null;
        int separator = identity.indexOf(':');
        return identity.substring(0, separator).toUpperCase(java.util.Locale.ROOT) + identity.substring(separator);
    }

    private static void add(String slot, String label, ItemStack item, boolean auxiliary, Set<String> used,
                            Set<String> savedAttempts, NamespacedKey attemptKey, List<String> errors,
                            List<TotalAssaultStorage.AttemptItem> items, Set<String> identities, AssaultBoss boss) {
        String original = identity(item);
        if (original == null) return;
        identities.add(original);
        if (Banneditem.isitembanned(item, boss.bannedModels(), boss.bannedMaterials())) errors.add(label + "的物品已被禁止。");

        ItemMeta meta = item.getItemMeta();
        String key;
        if (auxiliary) {
            key = auxiliaryIdentity(item, meta);
        } else {
            // 没有模型的装备只按物品标记判断；随机键只用于保存快照，不按材质限制其他装备。
            key = original.startsWith("MODEL:") ? original : "MATERIAL:gear:" + UUID.randomUUID();
            String previous = meta.getPersistentDataContainer().get(attemptKey, PersistentDataType.STRING);
            if (previous != null && savedAttempts.contains(previous)) errors.add(label + "的物品已在本次挑战中使用过。");
        }
        if (used.contains(key)) errors.add(label + "的物品已在本次挑战中使用过。");
        int separator = key.indexOf(':');
        items.add(new TotalAssaultStorage.AttemptItem(slot, key.substring(0, separator), key.substring(separator + 1), item.serializeAsBytes()));
    }

    /** 药水只比较效果类型；普通辅助物品按材质比较。 */
    private static String auxiliaryIdentity(ItemStack item, ItemMeta meta) {
        if (meta instanceof PotionMeta potion) {
            Set<String> effects = new TreeSet<>();
            if (potion.getBasePotionType() != null) {
                potion.getBasePotionType().getPotionEffects().forEach(effect -> effects.add(effect.getType().getKey().toString()));
            }
            potion.getCustomEffects().forEach(effect -> effects.add(effect.getType().getKey().toString()));
            return "MATERIAL:aux:potion:" + String.join(",", effects);
        }
        return "MATERIAL:aux:material:" + item.getType().name();
    }

    /** 只给装备槽写入出战标记；数据库确认该次出战已保存后，标记才会阻止再次使用。 */
    public static void markEquipment(Player player, JavaPlugin plugin, String attemptId) {
        NamespacedKey key = new NamespacedKey(plugin, "totalassault_attempt");
        PlayerInventory inventory = player.getInventory();
        for (int slot : new int[]{0, 36, 37, 38, 39, 40}) {
            ItemStack item = inventory.getItem(slot);
            if (item == null || item.getType().isAir()) continue;
            ItemMeta meta = item.getItemMeta();
            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, attemptId);
            item.setItemMeta(meta);
            inventory.setItem(slot, item);
        }
    }
}
