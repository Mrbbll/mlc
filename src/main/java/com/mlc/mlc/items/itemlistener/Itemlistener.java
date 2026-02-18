package com.mlc.mlc.items.itemlistener;

import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Itemlistener implements Listener {
    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (!event.getAction().isRightClick()) {
            return;
        }
        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
        ItemMeta itemMeta = item.getItemMeta();
        if(itemMeta.getItemModel() == null){
            return;
        }
        NamespacedKey namespacedKey = itemMeta.getItemModel();
        switch (namespacedKey.toString()) {
            case "mlc:crates":{
                event.setCancelled(true);
            }
            break;
            default:
                throw new IllegalStateException("Unexpected value: " + itemMeta.getItemModel());
        }
    }
}
