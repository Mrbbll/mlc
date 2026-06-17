package com.mlc.mlc.mlcmain.menu.listener;

import com.mlc.mlc.mlcmain.menu.Tpagui;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Tpalistener implements Listener {

    /** 所有打开的 TPA GUI 列表 */
    public static Set<Inventory> tpaInvs = new HashSet<>();

    /** 每个玩家当前查看的页码 */
    public static Map<Player, Integer> playerPageMap = new HashMap<>();

    @EventHandler
    public void onTpaGuiClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        Inventory inv = e.getInventory();

        // 只处理 TPA GUI 内的点击
        if (!tpaInvs.contains(inv)) {
            return;
        }

        // 阻止物品移动
        e.setCancelled(true);

        int slot = e.getRawSlot();
        if (slot < 0 || slot >= inv.getSize()) {
            return;
        }

        ItemStack clicked = e.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) {
            return;
        }

        // 处理翻页 - 上一页
        if (slot == 45 && clicked.getType() == Material.ARROW) {
            int currentPage = playerPageMap.getOrDefault(player, 1);
            if (currentPage > 1) {
                switchPage(player, inv, currentPage - 1);
            }
            return;
        }

        // 处理翻页 - 下一页
        if (slot == 53 && clicked.getType() == Material.ARROW) {
            int currentPage = playerPageMap.getOrDefault(player, 1);
            switchPage(player, inv, currentPage + 1);
            return;
        }

        // 处理玩家头颅点击 - 发送 TPA 请求
        if (clicked.getType() == Material.PLAYER_HEAD) {
            if (clicked.getItemMeta() instanceof SkullMeta skullMeta) {
                OfflinePlayer target = skullMeta.getOwningPlayer();
                if (target != null && target.getName() != null) {
                    String targetName = target.getName();

                    // 防止向自己发送传送请求
                    if (targetName.equalsIgnoreCase(player.getName())) {
                        return;
                    }

                    // 清理 GUI 记录
                    cleanup(player, inv);

                    // 关闭界面并执行 TPA 指令
                    player.closeInventory();
                    player.performCommand("tpa " + targetName);
                }
            }
        }
    }

    /**
     * 切换页面
     */
    private void switchPage(Player player, Inventory inv, int newPage) {
        cleanup(player, inv);
        player.closeInventory();
        Tpagui.open(player, newPage);
    }

    /**
     * 清理 GUI 追踪数据
     */
    private void cleanup(Player player, Inventory inv) {
        tpaInvs.remove(inv);
        playerPageMap.remove(player);
    }
}
