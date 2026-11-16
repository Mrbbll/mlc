package com.mlc.mlc.mlcmain.mlcitem.itemgui;

import com.mlc.mlc.mlcmain.items.itemmannager.Cratesitems;
import com.mlc.mlc.mlcmain.items.itemmannager.Fesitems;
import com.mlc.mlc.mlcmain.items.itemmannager.Mlcitems;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mlc.mlc.Mlc.miniMessage;

public class Gui {
    public static final Map<Player, Openedgui> openinvmap = new HashMap<>();
    public static final int PAGE_SIZE = 28;

    public static boolean isItemSlot(int slot) {
        return slot >= 9 && slot < 45 && slot % 9 > 0 && slot % 9 < 8;
    }

    private static List<ItemStack> items(Guitype type) {
        Map<Integer, ItemStack> source = switch (type) {
            case CRATESITEMSGUI -> Cratesitems.itemsmap;
            case FESITEMSGUI -> Fesitems.itemsmap;
            case MLCITEMSGUI -> Mlcitems.itemsmap;
        };
        return source.entrySet().stream().sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue).filter(item -> item != null && !item.getType().isAir()).toList();
    }

    public static void open(Player player) {
        Inventory inv = Bukkit.createInventory(player, 54,
                miniMessage.deserialize("<#b8ffde>特殊物品 <#71877a>· Items"));
        Openedgui opened = new Openedgui(player, 0, inv, Guitype.MLCITEMSGUI);
        refresh(Guitype.MLCITEMSGUI, player, 0, opened);
        player.openInventory(inv);
        openinvmap.put(player, opened);
    }

    public static void refresh(Guitype type, Player player, int offset, Openedgui opened) {
        Inventory inv = opened.getInv();
        List<ItemStack> items = items(type);
        ItemStack frame = icon(Material.GRAY_STAINED_GLASS_PANE, " ");
        int index = offset;
        for (int slot = 0; slot < inv.getSize(); slot++) {
            if (isItemSlot(slot)) {
                inv.setItem(slot, index < items.size() ? items.get(index).clone() : null);
                index++;
            } else {
                inv.setItem(slot, frame);
            }
        }
        int pages = Math.max(1, (items.size() + PAGE_SIZE - 1) / PAGE_SIZE);
        inv.setItem(4, icon(Material.KNOWLEDGE_BOOK, "<#b8ffde>特殊物品图鉴",
                "<#aab8b0>浏览各类特殊物品并查看配方",
                "<#aab8b0>当前分类 <#d8e3dc>" + categoryName(type),
                "<#aab8b0>第 <#ffd166>" + (offset / PAGE_SIZE + 1) + " <#aab8b0>/ <#ffd166>" + pages + " <#aab8b0>页"));
        if (offset > 0) inv.setItem(45, icon(Material.ARROW, "<#b8ffde>← 上一页", "<#aab8b0>查看上一页物品"));
        inv.setItem(48, category(Material.RED_DYE, "常用物品", type == Guitype.MLCITEMSGUI));
        inv.setItem(49, category(Material.YELLOW_DYE, "节日物品", type == Guitype.FESITEMSGUI));
        inv.setItem(50, category(Material.BLUE_DYE, "宝箱物品", type == Guitype.CRATESITEMSGUI));
        if (offset + PAGE_SIZE < items.size()) inv.setItem(53, icon(Material.ARROW, "<#b8ffde>下一页 →", "<#aab8b0>查看更多物品"));
        opened.setNum(offset);
        opened.setGuitype(type);
    }

    private static String categoryName(Guitype type) {
        return switch (type) {
            case MLCITEMSGUI -> "常用物品";
            case FESITEMSGUI -> "节日物品";
            case CRATESITEMSGUI -> "宝箱物品";
        };
    }

    private static ItemStack category(Material material, String name, boolean selected) {
        return icon(material, (selected ? "<#ffd166>" : "<#b8ffde>") + name,
                selected ? "<#ffd166>◆ 当前分类" : "<#aab8b0>点击查看此分类");
    }

    private static ItemStack icon(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.itemName(miniMessage.deserialize("<!i>" + name));
        meta.lore(java.util.Arrays.stream(lore)
                .map(line -> miniMessage.deserialize("<!i>" + line)).toList());
        item.setItemMeta(meta);
        return item;
    }

    public static boolean hasNext(Openedgui opened) {
        return opened.getNum() + PAGE_SIZE < items(opened.getGuitype()).size();
    }
}

