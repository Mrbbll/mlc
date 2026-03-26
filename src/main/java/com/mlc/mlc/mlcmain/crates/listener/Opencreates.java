package com.mlc.mlc.mlcmain.crates.listener;

import com.mlc.mlc.mlcmain.crates.Crates;
import com.mlc.mlc.mlcmain.items.itemmannager.Mlcitems;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class Opencreates implements Listener {
    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (!event.getAction().isRightClick()) {
            return;
        }
        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
        if(item.equals(Mlcitems.crate)){
            event.setCancelled(true);
            Player player = event.getPlayer();
            Crates.getitems(player);
            player.getInventory().setItemInMainHand(null);
        }
    }
}


