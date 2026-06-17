package com.mlc.mlc.mlcmain.menu.listener;

import com.mlc.mlc.mlcmain.menu.Mlcmenu;
import com.mlc.mlc.mlcmain.menu.Tpagui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.Inventory;

public class menuopenlistener implements Listener {

    /**
     * 玩家潜行+按F键(交换副手)打开菜单
     */
    @EventHandler
    public void onopen(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        if (player.isSneaking()) {
            Mlcmenu.open(player);
            event.setCancelled(true);
        }
    }

    /**
     * 处理菜单点击事件，执行对应的服务器指令
     */
    @EventHandler
    public void onclick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        Inventory inv = e.getInventory();

        // 检查是否是我们菜单的点击
        if (!inv.equals(Mlcmenu.menuinv)) {
            return;
        }

        // 取消点击事件，防止玩家移动物品
        e.setCancelled(true);

        // 无效槽位检查
        int slot = e.getRawSlot();
        if (slot < 0 || slot >= inv.getSize()) {
            return;
        }

        // 处理功能按钮点击
        switch (slot) {
            case Mlcmenu.SLOT_RTP:
                player.closeInventory();
                player.performCommand("rtp");
                break;

            case Mlcmenu.SLOT_TPA:
                player.closeInventory();
                Tpagui.open(player);
                break;

            case Mlcmenu.SLOT_SIT:
                player.closeInventory();
                player.performCommand("sit");
                break;

            case Mlcmenu.SLOT_MONEY:
                player.closeInventory();
                player.performCommand("money");
                break;

            case Mlcmenu.SLOT_MAIL:
                player.closeInventory();
                player.performCommand("mymail");
                break;

            case Mlcmenu.SLOT_MAP:
                player.closeInventory();
                sendUrlToChat(player,
                        "» 点击打开 网页地图",
                        "查看服务器在线地图",
                        Mlcmenu.MAP_URL);
                break;

            case Mlcmenu.SLOT_QUESTION:
                player.closeInventory();
                sendUrlToChat(player,
                        "» 点击打开 在线文档",
                        "查看服务器在线文档",
                        Mlcmenu.DOCS_URL);
                break;

            case Mlcmenu.SLOT_ITEMSHOW:
                player.closeInventory();
                player.performCommand("item");
                break;

            default:
                // 点击边框或其他区域，不做处理
                break;
        }
    }

    /**
     * 向玩家聊天栏发送可点击的超链接
     * @param player 目标玩家
     * @param mainText 主文本
     * @param hoverText 鼠标悬浮提示
     * @param url 目标链接
     */
    private void sendUrlToChat(Player player, String mainText, String hoverText, String url) {
        player.sendMessage(
                Component.text()
                        .append(Component.text("[MLC] ", TextColor.fromHexString("#f73636")))
                        .append(Component.text(mainText, TextColor.fromHexString("#e5fe67")))
                        .hoverEvent(Component.text(hoverText))
                        .clickEvent(ClickEvent.openUrl(url))
        );
    }
}
