package com.mlc.mlc.mlcmain.enchantments.enchantupgrade;

import com.mlc.mlc.mlcmain.items.itemmannager.Mlcitems;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.mlc.mlc.Mlc.instance;
import static com.mlc.mlc.Mlc.miniMessage;

/** 符文标识和破损物品的数据编码。 */
public final class UpgradeItems {
    private static final DateTimeFormatter READY_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm z");
    private static final Component BROKEN_NOTICE = Component.text("破损装备", NamedTextColor.RED)
            .decoration(TextDecoration.ITALIC, false);
    private UpgradeItems() {
    }

    private static NamespacedKey key(String name) {
        return new NamespacedKey(instance, name);
    }

    public static boolean isType(ItemStack item, String type) {
        if (item == null || item.getType().isAir() || !item.hasItemMeta()) return false;
        return type.equals(item.getItemMeta().getPersistentDataContainer()
                .get(key("enchant_upgrade_item"), PersistentDataType.STRING));
    }

    public static ItemStack breakItem(ItemStack original, int totalLevels, long nowMillis) {
        if (original.getAmount() != 1) throw new IllegalArgumentException("equipment must be unstacked");
        long readyAt = Math.addExact(nowMillis, UpgradeRules.repairDurationMillis(totalLevels));
        ItemStack broken = Mlcitems.brokenEnchantmentItem.clone();
        ItemMeta meta = broken.getItemMeta();
        meta.itemName(original.effectiveName());
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(key("enchant_upgrade_original"), PersistentDataType.BYTE_ARRAY, original.serializeAsBytes());
        data.set(key("enchant_upgrade_ready_at"), PersistentDataType.LONG, readyAt);
        meta.lore(brokenLore(readyAt, nowMillis));
        broken.setItemMeta(meta);
        return broken;
    }

    public static long readyAt(ItemStack broken) {
        if (!isType(broken, "broken_item")) return -1;
        Long ready = broken.getItemMeta().getPersistentDataContainer()
                .get(key("enchant_upgrade_ready_at"), PersistentDataType.LONG);
        return ready == null ? -1 : ready;
    }

    public static boolean refreshLore(ItemStack broken, long nowMillis) {
        long ready = readyAt(broken);
        if (ready < 0) return false;
        ItemMeta meta = broken.getItemMeta();
        List<Component> previousLore = meta.lore();
        List<Component> lore = brokenLore(ready, nowMillis);
        if (lore.equals(previousLore)) return false;
        // 旧版破损装备的名称可从保存的原装备恢复。
        if (previousLore == null || previousLore.isEmpty() || !BROKEN_NOTICE.equals(previousLore.getFirst())) {
            try {
                ItemStack original = readOriginal(meta);
                if (original != null) meta.itemName(original.effectiveName());
            } catch (IllegalArgumentException exception) {
                // 原装备数据无法读取时仍显示修复提示。
            }
        }
        meta.lore(lore);
        broken.setItemMeta(meta);
        return true;
    }

    private static List<Component> brokenLore(long ready, long nowMillis) {
        long remaining = Math.max(0, ready - nowMillis);
        long hours = (remaining + UpgradeRules.MILLIS_PER_HOUR - 1) / UpgradeRules.MILLIS_PER_HOUR;
        String readyTime = READY_TIME_FORMAT.format(Instant.ofEpochMilli(ready).atZone(ZoneId.systemDefault()));
        return List.of(
                BROKEN_NOTICE,
                miniMessage.deserialize(remaining > 0 ? "<!i><#ffd166>◆ 自然恢复中" : "<!i><#b8ffde>◆ 自然恢复完成"),
                miniMessage.deserialize(remaining > 0 ? "<!i><#aab8b0>约 <#ffd166>" + hours + " <#aab8b0>小时后可修复" : "<!i><#b8ffde>现在可以修复"),
                miniMessage.deserialize("<!i><#aab8b0>可修复时间 <#d8e3dc>" + readyTime),
                miniMessage.deserialize("<!i><#71877a>放入附魔升级界面修复"),
                miniMessage.deserialize("<!i><#71877a>也可使用修复符文提前修复"));
    }

    public static ItemStack repair(ItemStack broken, long nowMillis, boolean useRune) {
        if (!UpgradeRules.canRepair(readyAt(broken), nowMillis, useRune)) return null;
        return readOriginal(broken.getItemMeta());
    }

    private static ItemStack readOriginal(ItemMeta meta) {
        byte[] original = meta.getPersistentDataContainer()
                .get(key("enchant_upgrade_original"), PersistentDataType.BYTE_ARRAY);
        return original == null ? null : ItemStack.deserializeBytes(original);
    }
}
