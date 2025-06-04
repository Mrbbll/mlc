package com.mlc.mlc.Listener;

import com.mlc.mlc.backpackgui.backpack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


import java.io.IOException;
import java.util.Objects;

public class backpacklistener implements Listener {
    @EventHandler
    public void onrightclik(PlayerInteractEvent event) throws IOException {
        if(event.getAction() == Action.LEFT_CLICK_AIR||event.getAction()==Action.LEFT_CLICK_BLOCK){
            return;
        }
        ItemStack itemStack = event.getPlayer().getInventory().getItemInMainHand();
        if(itemStack.isEmpty()){
            return;
        }
        ItemMeta meta = itemStack.getItemMeta();
        if(!meta.hasItemModel()){
            return;
        }else if(Objects.requireNonNull(meta.getItemModel()).toString().equals("mlc:mlc_backpack")){
            new backpack().openbackpack(event.getPlayer(),itemStack);
            event.getPlayer().swingMainHand();
        }
    }
    @EventHandler
    public void onclose(InventoryCloseEvent event) throws IOException {
        if(!event.getView().title().equals(Component.text("背包")
                        .decoration(TextDecoration.ITALIC,false).decoration(TextDecoration.BOLD,true)
                        .color(TextColor.fromHexString("#eea468")))){
            return;
        }
        ItemStack itemStack = event.getPlayer().getInventory().getItemInMainHand();
        if(itemStack.isEmpty()){
            return;
        }
        ItemMeta meta = itemStack.getItemMeta();
        if(!meta.hasItemModel()){
            return;
        }else if(Objects.requireNonNull(meta.getItemModel()).toString().equals("mlc:mlc_backpack")){
            new backpack().saveBackpack((Player) event.getPlayer(), event.getInventory());
        }

    }
}
