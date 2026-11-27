package com.mlc.mlc.totalassault.battle;

import com.mlc.mlc.totalassault.data.Banneditem;
import com.mlc.mlc.totalassault.data.TotalAssaultStorage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** 检查进场槽位并保存进场时的完整物品快照。 */
public final class EquipmentCheck {
    // 测试 Boss 暂无禁用物品，后续可按 Boss 增加对应列表。
    private static final Set<String> BANNED_MODELS = Set.of();
    private static final Set<Material> BANNED_MATERIALS = Set.of();

    public record Result(List<String> errors, List<TotalAssaultStorage.AttemptItem> items, Set<String> identities) {}

    private EquipmentCheck() {}

    public static Result check(Player player, Set<String> used) {
        PlayerInventory inventory = player.getInventory();
        List<String> errors = new ArrayList<>();
        List<TotalAssaultStorage.AttemptItem> items = new ArrayList<>();
        Set<String> identities = new HashSet<>();
        for (int slot = 3; slot < 36; slot++) {
            ItemStack item = inventory.getItem(slot);
            if (item != null && !item.getType().isAir()) errors.add("背包第 " + (slot + 1) + " 格必须为空。");
        }
        add("MAIN_HAND", "快捷栏第 1 格", inventory.getItem(0), used, errors, items, identities);
        add("HOTBAR_2", "快捷栏第 2 格", inventory.getItem(1), used, errors, items, identities);
        add("HOTBAR_3", "快捷栏第 3 格", inventory.getItem(2), used, errors, items, identities);
        add("HEAD", "头盔", inventory.getHelmet(), used, errors, items, identities);
        add("CHEST", "胸甲", inventory.getChestplate(), used, errors, items, identities);
        add("LEGS", "护腿", inventory.getLeggings(), used, errors, items, identities);
        add("FEET", "靴子", inventory.getBoots(), used, errors, items, identities);
        add("OFF_HAND", "副手", inventory.getItemInOffHand(), used, errors, items, identities);
        return new Result(errors, items, identities);
    }

    /** 将模型和材质标识转为数据库使用的大写分类。 */
    public static String identity(ItemStack item) {
        String identity = Banneditem.identity(item);
        if (identity == null) return null;
        int separator = identity.indexOf(':');
        return identity.substring(0, separator).toUpperCase(java.util.Locale.ROOT) + identity.substring(separator);
    }

    private static void add(String slot, String label, ItemStack item, Set<String> used, List<String> errors,
                            List<TotalAssaultStorage.AttemptItem> items, Set<String> identities) {
        String identity = identity(item);
        if (identity == null) return;
        if (Banneditem.isitembanned(item, BANNED_MODELS, BANNED_MATERIALS)) errors.add(label + "的物品已被禁止。");
        if (used.contains(identity)) errors.add(label + "的物品已在本次挑战中使用过。");
        int separator = identity.indexOf(':');
        identities.add(identity);
        items.add(new TotalAssaultStorage.AttemptItem(slot, identity.substring(0, separator),
                identity.substring(separator + 1), item.serializeAsBytes()));
    }
}
