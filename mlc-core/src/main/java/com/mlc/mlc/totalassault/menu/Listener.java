package com.mlc.mlc.totalassault.menu;

import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public class Listener implements org.bukkit.event.Listener {
    @EventHandler(ignoreCancelled = true)
    public void onClick(InventoryClickEvent event) {
        tamenu.click(event);
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        tamenu.drag(event);
    }
}
