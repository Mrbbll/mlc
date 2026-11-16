package com.mlc.mlc.mlcmain.items.itemfunc.create;

import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Itemlistener implements Listener {
    private static final NamespacedKey MODEL = NamespacedKey.fromString("mlc:crates");

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (!event.getAction().isRightClick()) return;
        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
        ItemMeta meta = item.getItemMeta();
        if (meta != null && MODEL.equals(meta.getItemModel())) event.setCancelled(true);
    }
}
