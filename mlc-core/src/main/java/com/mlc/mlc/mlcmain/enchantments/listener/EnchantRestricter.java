package com.mlc.mlc.mlcmain.enchantments.listener;

import com.mlc.mlc.mlcmain.enchantments.EnchantmentMaxLevel;
import io.papermc.paper.event.entity.EntityEquipmentChangedEvent;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;

import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.mlc.mlc.Mlc.instance;

public class EnchantRestricter implements Listener {
    // 【任务合并】仅在主线程访问：每位玩家最多保留一个待执行检查任务。
    private final Map<UUID, PendingCheck> pendingChecks = new HashMap<>();

    // 【装备变化】覆盖手动穿脱、右键穿戴、发射器装备以及副手物品变化。
    // 按 E 本身不会触发 InventoryOpenEvent，因此直接监听装备状态的变化。
    @EventHandler(priority = EventPriority.MONITOR)
    public void onEquipmentChanged(EntityEquipmentChangedEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        // 【触发范围】只有四个护甲槽或副手发生变化才检查，忽略单纯主手变化。
        boolean relevant = event.getEquipmentChanges().keySet().stream().anyMatch(slot ->
                slot == EquipmentSlot.HEAD || slot == EquipmentSlot.CHEST
                        || slot == EquipmentSlot.LEGS || slot == EquipmentSlot.FEET
                        || slot == EquipmentSlot.OFF_HAND);
        if (!relevant) return;

        // 【局部扫描】玩家库存 36～39 为护甲、40 为副手；共五个槽位。
        // 与点击/拖拽任务共用去重队列，等装备变化完成后读取实际库存物品。
        PendingCheck check = queueCheck(player);
        for (int slot = 36; slot <= 40; slot++) {
            check.addSlot(player.getInventory(), slot);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerclickInventory(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        if (event.getAction() == InventoryAction.NOTHING) return;
        PendingCheck check = queueCheck(player);

        // 【普通点击】记录实际点击的库存和本地槽位，下一 tick 只检查该槽及光标。
        Inventory clicked = event.getClickedInventory();
        if (clicked != null) check.addSlot(clicked, event.getSlot());

        // 【数字键交换】额外检查参与交换的快捷栏槽位。
        if (event.getClick() == ClickType.NUMBER_KEY && event.getHotbarButton() >= 0) {
            check.addSlot(player.getInventory(), event.getHotbarButton());
        }
        // 【副手交换】玩家背包的副手槽位为 40。
        if (event.getClick() == ClickType.SWAP_OFFHAND) {
            check.addSlot(player.getInventory(), 40);
        }

        // 【批量移动】Shift 转移、双击收集及快捷栏重排可能影响多个槽位。
        // 无法可靠推断最终落点时，才扫描本次界面的上下库存；未知操作同样兜底。
        switch (event.getAction()) {
            case MOVE_TO_OTHER_INVENTORY, COLLECT_TO_CURSOR, HOTBAR_SWAP, UNKNOWN -> {
                check.addInventory(event.getView().getTopInventory());
                check.addInventory(event.getView().getBottomInventory());
            }
            default -> { /* 其他操作仅影响已记录的槽位和光标，无需全量扫描。 */ }
        }
    }

    // 【拖拽分配】只记录拖拽涉及的槽位，与本 tick 的点击检查共用任务。
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        PendingCheck check = queueCheck(player);
        int topSize = event.getView().getTopInventory().getSize();
        for (int rawSlot : event.getRawSlots()) {
            Inventory inventory = rawSlot < topSize
                    ? event.getView().getTopInventory() : event.getView().getBottomInventory();
            check.addSlot(inventory, event.getView().convertSlot(rawSlot));
        }
    }

    // 【打开容器】检查容器、玩家背包（含装备）及光标；按 E 打开自身背包不触发此事件。
    @EventHandler
    public void oninvopen(InventoryOpenEvent event) {
        restrictInventory(event.getInventory());
        restrictInventory(event.getPlayer().getInventory());
        restrictItem(event.getPlayer().getItemOnCursor());
    }

    // 【延迟执行】等待库存操作完成；合并重复槽位，并让全量检查覆盖局部检查。
    private PendingCheck queueCheck(Player player) {
        UUID uuid = player.getUniqueId();
        PendingCheck existing = pendingChecks.get(uuid);
        if (existing != null) return existing;

        PendingCheck check = new PendingCheck();
        pendingChecks.put(uuid, check);
        Bukkit.getScheduler().runTask(instance, () -> {
            pendingChecks.remove(uuid);
            for (Inventory inventory : check.inventories) restrictInventory(inventory);
            for (Map.Entry<Inventory, Set<Integer>> entry : check.slots.entrySet()) {
                if (check.inventories.contains(entry.getKey())) continue;
                for (int slot : entry.getValue()) restrictItem(entry.getKey().getItem(slot));
            }
            if (player.isOnline()) restrictItem(player.getItemOnCursor());
        });
        return check;
    }

    // 【待检查范围】按库存对象身份区分界面，用 Set 消除同一槽位的重复检查。
    private static final class PendingCheck {
        private final Set<Inventory> inventories = Collections.newSetFromMap(new IdentityHashMap<>());
        private final Map<Inventory, Set<Integer>> slots = new IdentityHashMap<>();

        private void addSlot(Inventory inventory, int slot) {
            if (slot < 0 || slot >= inventory.getSize() || inventories.contains(inventory)) return;
            slots.computeIfAbsent(inventory, ignored -> new HashSet<>()).add(slot);
        }

        private void addInventory(Inventory inventory) {
            inventories.add(inventory);
            slots.remove(inventory);
        }
    }

    // 【全量检查】仅用于打开库存和无法确定落点的批量操作。
    private void restrictInventory(Inventory inventory) {
        ItemStack[] items = inventory.getContents();
        for (ItemStack item : items) {
            restrictItem(item);
        }
    }

    // 【单个物品】保留其他物品属性，仅在附魔超限时写回元数据。
    private void restrictItem(ItemStack item) {
        if (item == null || item.getType().isAir()) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        boolean changed = false;
        // 【普通附魔】处理工具、武器、装备等物品上的直接附魔。
        for (Map.Entry<Enchantment, Integer> entry : meta.getEnchants().entrySet()) {
            int maxLevel = EnchantmentMaxLevel.getMaxLevel(entry.getKey());
            if (entry.getValue() > maxLevel) {
                // 以插件配置的上限为准，允许锋利 7 等超出原版上限的等级。
                meta.addEnchant(entry.getKey(), maxLevel, true);
                changed = true;
            }
        }

        // 【附魔书】存储附魔使用独立 API，同样按插件自定义上限降级。
        if (meta instanceof EnchantmentStorageMeta bookMeta) {
            for (Map.Entry<Enchantment, Integer> entry : bookMeta.getStoredEnchants().entrySet()) {
                int maxLevel = EnchantmentMaxLevel.getMaxLevel(entry.getKey());
                if (entry.getValue() > maxLevel) {
                    bookMeta.addStoredEnchant(entry.getKey(), maxLevel, true);
                    changed = true;
                }
            }
        }

        if (changed) item.setItemMeta(meta);
    }
}
