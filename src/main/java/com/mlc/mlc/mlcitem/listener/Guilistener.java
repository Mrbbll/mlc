package com.mlc.mlc.mlcitem.listener;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.Objects;

public class Guilistener implements Listener {

    @EventHandler
    //mlcitem界面事件监听
    public void onClick(InventoryClickEvent e) throws IOException {
        Player player = (Player) e.getWhoClicked();
        InventoryView inv = player.getOpenInventory();
        if (inv.title().equals(Component.text("mlc", TextColor.fromHexString("#f73636")))) {
            e.setCancelled(true);
            if(e.getRawSlot()<0||e.getRawSlot()>=e.getInventory().getSize())
            {
                return;
            };
            ItemStack itemStack = e.getCurrentItem();
            if(itemStack == null)
            {
                return;
            };
            if(Objects.equals(itemStack.getItemMeta().itemName(), Component.text("exit")))
            {
                player.kick(Component.text("拜拜"));
            }
            else{
                player.give(itemStack);
            };
        };
    }
}
