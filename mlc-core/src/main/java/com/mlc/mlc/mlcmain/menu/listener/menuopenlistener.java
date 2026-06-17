package com.mlc.mlc.mlcmain.menu.listener;

import com.mlc.mlc.mlcmain.menu.Mlcmenu;
import com.mlc.mlc.mlcmain.menu.Tpagui;
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

            case Mlcmenu.SLOT_HOME:
                player.closeInventory();
                player.performCommand("home");
                break;

            case Mlcmenu.SLOT_TPA:
                player.closeInventory();
                Tpagui.open(player);
                break;

            case Mlcmenu.SLOT_BACK:
                player.closeInventory();
                player.performCommand("back");
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

            case Mlcmenu.SLOT_MLCITEM:
                player.closeInventory();
                player.performCommand("mlcitem");
                break;

            case Mlcmenu.SLOT_CRATES:
                player.closeInventory();
                player.performCommand("choujiang");
                break;

            case Mlcmenu.SLOT_ITEMSHOW:
                player.closeInventory();
                player.performCommand("item");
                break;

            case Mlcmenu.SLOT_RELOAD:
                player.closeInventory();
                player.performCommand("mlcreload");
                break;

            default:
                // 点击边框或其他区域，不做处理
                break;
        }
    }
}
