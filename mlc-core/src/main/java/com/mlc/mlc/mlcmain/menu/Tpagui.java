package com.mlc.mlc.mlcmain.menu;

import com.mlc.mlc.mlcmain.menu.listener.Tpalistener;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;
import java.util.stream.Collectors;

public class Tpagui {

    private static final int GUI_ROWS = 6;
    private static final int GUI_SIZE = GUI_ROWS * 9;       // 54
    private static final int PLAYER_SLOTS = 9 * 5;           // 45 slots for player heads

    private static final int SLOT_PREV = 45;
    private static final int SLOT_PAGE_INFO = 49;
    private static final int SLOT_NEXT = 53;

    /**
     * 打开 TPA 玩家选择界面（默认第1页）
     */
    public static void open(Player player) {
        open(player, 1);
    }

    /**
     * 打开 TPA 玩家选择界面（指定页数）
     */
    public static void open(Player player, int page) {
        // 获取在线玩家列表（排除自己）
        List<Player> onlinePlayers = Bukkit.getOnlinePlayers().stream()
                .filter(p -> !p.equals(player))
                .collect(Collectors.toList());

        int totalPages = Math.max(1, (int) Math.ceil((double) onlinePlayers.size() / PLAYER_SLOTS));
        if (page < 1) page = 1;
        if (page > totalPages) page = totalPages;

        Inventory inv = Bukkit.createInventory(null, GUI_SIZE,
                Component.text("传送请求 - 选择玩家", TextColor.fromHexString("#f73636")));

        // 填充玻璃边框
        ItemStack border = Mlcmenu.createMenuItem(Material.BLACK_STAINED_GLASS_PANE, " ", new String[]{});
        // 最后一行全部用边框填充
        for (int i = 45; i < 54; i++) {
            inv.setItem(i, border);
        }

        // 计算当前页的玩家范围
        int start = (page - 1) * PLAYER_SLOTS;
        int end = Math.min(start + PLAYER_SLOTS, onlinePlayers.size());

        for (int i = start; i < end; i++) {
            Player target = onlinePlayers.get(i);
            int slot = i - start;
            inv.setItem(slot, createPlayerHead(target));
        }

        // 翻页按钮
        if (page > 1) {
            inv.setItem(SLOT_PREV, createPageArrow("上一页", page - 1));
        }

        inv.setItem(SLOT_PAGE_INFO, createPageInfo(page, totalPages, onlinePlayers.size()));

        if (page < totalPages) {
            inv.setItem(SLOT_NEXT, createPageArrow("下一页", page + 1));
        }

        // 记录 GUI 和玩家页码
        Tpalistener.tpaInvs.add(inv);
        Tpalistener.playerPageMap.put(player, page);

        player.openInventory(inv);
    }

    /**
     * 创建玩家头颅物品
     */
    private static ItemStack createPlayerHead(Player target) {
        ItemStack head = ItemStack.of(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setOwningPlayer(target);
        meta.displayName(Component.text("§6" + target.getName()));
        meta.lore(List.of(
                Component.text("§7点击发送传送请求"),
                Component.text("§e» 传送到 §f" + target.getName())
        ));
        head.setItemMeta(meta);
        return head;
    }

    /**
     * 创建翻页箭头
     */
    private static ItemStack createPageArrow(String label, int targetPage) {
        ItemStack arrow = ItemStack.of(Material.ARROW);
        ItemMeta meta = arrow.getItemMeta();
        meta.displayName(Component.text("§e" + label));
        meta.lore(List.of(Component.text("§7跳转到第 " + targetPage + " 页")));
        arrow.setItemMeta(meta);
        return arrow;
    }

    /**
     * 创建页码指示器
     */
    private static ItemStack createPageInfo(int current, int total, int playerCount) {
        ItemStack paper = ItemStack.of(Material.PAPER);
        ItemMeta meta = paper.getItemMeta();
        meta.displayName(Component.text("§6第 " + current + " / " + total + " 页"));
        meta.lore(List.of(
                Component.text("§7在线玩家: §f" + playerCount),
                Component.text("§7当前页显示第 §f" + ((current - 1) * PLAYER_SLOTS + 1)
                        + " - " + Math.min(current * PLAYER_SLOTS, playerCount) + " §7个玩家")
        ));
        paper.setItemMeta(meta);
        return paper;
    }
}
