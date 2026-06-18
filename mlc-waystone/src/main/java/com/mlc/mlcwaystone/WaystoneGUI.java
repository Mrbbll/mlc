package com.mlc.mlcwaystone;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

import static com.mlc.mlcwaystone.Mlcwaystone.waystoneDataMap;

public class WaystoneGUI {

    private static final int GUI_ROWS = 6;
    private static final int GUI_SIZE = 54;
    private static final int WAYSTONE_SLOTS = 45;  // rows 0-4
    public static final int SLOT_PREV = 45;
    public static final int SLOT_PAGE_INFO = 49;
    public static final int SLOT_NEXT = 53;

    // Per-player state tracking
    public static final Map<Player, Integer> playerPageMap = new HashMap<>();
    public static final Map<Player, List<UUID>> playerWaystoneOrder = new HashMap<>();
    public static final Set<Inventory> openInvs = new HashSet<>();

    /**
     * Open the waystone GUI for a player at page 1.
     */
    public static void open(Player player) {
        open(player, 1);
    }

    /**
     * Open the waystone GUI for a player at a specific page.
     */
    public static void open(Player player, int page) {
        // Build sorted list of waystones: private first, then public
        List<WaystoneData> sorted = waystoneDataMap.values().stream()
                .sorted(Comparator
                        .comparing(WaystoneData::isPrivate).reversed()   // true (private) before false (public)
                        .thenComparingLong(WaystoneData::getCreatedAt))
                .collect(Collectors.toList());

        List<UUID> sortedUuids = sorted.stream()
                .map(WaystoneData::getId)
                .collect(Collectors.toList());

        int totalPages = Math.max(1, (int) Math.ceil((double) sorted.size() / WAYSTONE_SLOTS));

        // Clamp page to valid range
        if (page < 1) page = 1;
        if (page > totalPages) page = totalPages;

        // Save player state
        playerPageMap.put(player, page);
        playerWaystoneOrder.put(player, sortedUuids);

        // Create inventory
        Component title = Component.text("传送点 - 第 " + page + "/" + totalPages + " 页");
        Inventory inv = Bukkit.createInventory(null, GUI_SIZE, title);

        // Fill waystone slots for current page
        int startIndex = (page - 1) * WAYSTONE_SLOTS;
        int endIndex = Math.min(startIndex + WAYSTONE_SLOTS, sorted.size());

        for (int i = startIndex; i < endIndex; i++) {
            int slot = i - startIndex;
            WaystoneData data = sorted.get(i);
            inv.setItem(slot, createWaystoneItem(data));
        }

        // Fill control row (row 5) with glass pane border
        ItemStack border = createBorderItem();
        for (int i = 45; i < 54; i++) {
            inv.setItem(i, border);
        }

        // Previous page arrow
        if (page > 1) {
            inv.setItem(SLOT_PREV, createPrevPageItem(page - 1, totalPages));
        }

        // Next page arrow
        if (page < totalPages) {
            inv.setItem(SLOT_NEXT, createNextPageItem(page + 1, totalPages));
        }

        // Page indicator
        inv.setItem(SLOT_PAGE_INFO, createPageInfoItem(page, totalPages, sorted.size()));

        // Tip items at decorative slots
        inv.setItem(47, createTipItem("Shift+右键", "手持物品可设置图标"));
        inv.setItem(51, createTipItem("私人 = 灵魂灯笼+磁石", "公共 = 信标+磁石"));

        openInvs.add(inv);
        player.openInventory(inv);
    }

    /**
     * Get the waystone UUID at a specific slot for a given player.
     * Returns null if the slot is empty or out of range.
     */
    public static UUID getWaystoneIdAtSlot(Player player, int slot) {
        List<UUID> order = playerWaystoneOrder.get(player);
        Integer page = playerPageMap.get(player);
        if (order == null || page == null) return null;
        if (slot < 0 || slot >= WAYSTONE_SLOTS) return null;

        int index = (page - 1) * WAYSTONE_SLOTS + slot;
        if (index >= order.size()) return null;

        return order.get(index);
    }

    /**
     * Clean up per-player state when they close the GUI.
     */
    public static void cleanup(Player player) {
        playerPageMap.remove(player);
        playerWaystoneOrder.remove(player);
    }

    // --- Item creation helpers ---

    private static ItemStack createWaystoneItem(WaystoneData data) {
        Material material = Material.getMaterial(data.getIconType());
        if (material == null || !material.isItem()) {
            material = Material.CAMPFIRE;
        }

        ItemStack item = ItemStack.of(material);
        ItemMeta meta = item.getItemMeta();

        // Color: gold for private, light blue for public
        TextColor nameColor = data.isPrivate()
                ? TextColor.fromHexString("#FFD700")
                : TextColor.fromHexString("#00BFFF");
        meta.itemName(Component.text(data.getIconName()).color(nameColor));

        List<Component> lore = new ArrayList<>();

        String typeLabel = data.isPrivate() ? "私人" : "公共";
        lore.add(Component.text("类型: " + typeLabel)
                .decoration(TextDecoration.ITALIC, false)
                .color(TextColor.fromHexString("#B5EE59")));

        lore.add(Component.text("所有者: " + data.getOwnerName())
                .decoration(TextDecoration.ITALIC, false)
                .color(TextColor.fromHexString("#B5EE59")));

        Location loc = data.getLocation();
        String worldName = loc.getWorld() != null ? loc.getWorld().getName() : "未知世界";
        lore.add(Component.text("世界: " + worldName)
                .decoration(TextDecoration.ITALIC, false)
                .color(TextColor.fromHexString("#B5EE59")));
        lore.add(Component.text("坐标: " + (int) loc.getX() + ", " + (int) loc.getY() + ", " + (int) loc.getZ())
                .decoration(TextDecoration.ITALIC, false)
                .color(TextColor.fromHexString("#B5EE59")));

        lore.add(Component.empty());
        lore.add(Component.text("» 点击传送")
                .decoration(TextDecoration.ITALIC, false)
                .color(TextColor.fromHexString("#FF5555")));

        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack createBorderItem() {
        ItemStack item = ItemStack.of(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        meta.itemName(Component.text(" "));
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack createPrevPageItem(int targetPage, int totalPages) {
        ItemStack item = ItemStack.of(Material.ARROW);
        ItemMeta meta = item.getItemMeta();
        meta.itemName(Component.text("« 上一页")
                .color(TextColor.fromHexString("#FFAA00")));
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("第 " + targetPage + "/" + totalPages + " 页")
                .decoration(TextDecoration.ITALIC, false)
                .color(TextColor.fromHexString("#AAAAAA")));
        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack createNextPageItem(int targetPage, int totalPages) {
        ItemStack item = ItemStack.of(Material.ARROW);
        ItemMeta meta = item.getItemMeta();
        meta.itemName(Component.text("下一页 »")
                .color(TextColor.fromHexString("#FFAA00")));
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("第 " + targetPage + "/" + totalPages + " 页")
                .decoration(TextDecoration.ITALIC, false)
                .color(TextColor.fromHexString("#AAAAAA")));
        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack createPageInfoItem(int page, int totalPages, int totalWaystones) {
        ItemStack item = ItemStack.of(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        meta.itemName(Component.text("第 " + page + "/" + totalPages + " 页")
                .color(TextColor.fromHexString("#FFFFFF")));
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("共 " + totalWaystones + " 个传送点")
                .decoration(TextDecoration.ITALIC, false)
                .color(TextColor.fromHexString("#B5EE59")));
        lore.add(Component.text("金色 = 私人 | 蓝色 = 公共")
                .decoration(TextDecoration.ITALIC, false)
                .color(TextColor.fromHexString("#AAAAAA")));
        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack createTipItem(String title, String desc) {
        ItemStack item = ItemStack.of(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        meta.itemName(Component.text(title)
                .color(TextColor.fromHexString("#CCCCCC")));
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text(desc)
                .decoration(TextDecoration.ITALIC, false)
                .color(TextColor.fromHexString("#AAAAAA")));
        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }
}
