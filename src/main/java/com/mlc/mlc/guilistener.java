package com.mlc.mlc;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class guilistener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        InventoryView inv = player.getOpenInventory();
        if (inv.title().equals(Component.text("mlc"))) {
            e.setCancelled(true);
            if(e.getRawSlot()<0||e.getRawSlot()>e.getInventory().getSize())
            {
                return;
            };
            ItemStack itemStack = e.getCurrentItem();
            if(itemStack == null)
            {
                return;
            }
            if(Objects.equals(itemStack.getItemMeta().itemName(), Component.text("exit")))
            {
                player.kick(Component.text("拜拜"));
            };
            if(Objects.equals(itemStack.getItemMeta().itemName(), Component.text("周年庆帽子1"))){
                player.give(itemStack);
        }

        };

        return;

    }


}
