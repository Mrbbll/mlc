package com.mlc.mlc.mlcmain.mlcitem.listener;

import com.mlc.mlc.mlcmain.menu.RecipeMenu;
import com.mlc.mlc.mlcmain.mlcitem.itemgui.Gui;
import com.mlc.mlc.mlcmain.mlcitem.itemgui.Guitype;
import com.mlc.mlc.mlcmain.mlcitem.itemgui.Openedgui;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import static com.mlc.mlc.Mlc.instance;

public class Guilistener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        Openedgui opened = Gui.openinvmap.get(player);
        Inventory inv = event.getView().getTopInventory();
        if (opened == null || !inv.equals(opened.getInv())) return;
        event.setCancelled(true);
        int slot = event.getRawSlot();
        if (slot < 0 || slot >= inv.getSize()) return;
        if (slot == 45 && opened.getNum() > 0) {
            Gui.refresh(opened.getGuitype(), player, opened.getNum() - Gui.PAGE_SIZE, opened);
        } else if (slot == 53 && Gui.hasNext(opened)) {
            Gui.refresh(opened.getGuitype(), player, opened.getNum() + Gui.PAGE_SIZE, opened);
        } else if (slot == 48 || slot == 49 || slot == 50) {
            Guitype type = switch (slot) {
                case 48 -> Guitype.MLCITEMSGUI;
                case 49 -> Guitype.FESITEMSGUI;
                default -> Guitype.CRATESITEMSGUI;
            };
            Gui.refresh(type, player, 0, opened);
        } else if (Gui.isItemSlot(slot) && (event.getClick() == ClickType.LEFT || event.getClick() == ClickType.RIGHT)) {
            ItemStack item = event.getCurrentItem();
            if (item == null || item.getType().isAir()) return;
            ItemStack selected = item.clone();
            if (player.hasPermission("mlc.op")) {
                player.give(selected);
            } else {
                Bukkit.getScheduler().runTask(instance, () -> {
                    if (player.isOnline() && player.getOpenInventory().getTopInventory().equals(inv)) {
                        RecipeMenu.open(player, selected);
                    }
                });
            }
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getWhoClicked() instanceof Player player) {
            Openedgui opened = Gui.openinvmap.get(player);
            if (opened != null && event.getView().getTopInventory().equals(opened.getInv())) event.setCancelled(true);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player player) {
            Openedgui opened = Gui.openinvmap.get(player);
            if (opened != null && event.getInventory().equals(opened.getInv())) Gui.openinvmap.remove(player);
        }
    }
}
