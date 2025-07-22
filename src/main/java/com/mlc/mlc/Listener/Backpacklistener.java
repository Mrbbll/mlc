package com.mlc.mlc.Listener;

import com.mlc.mlc.backpackgui.Backpack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


import java.io.IOException;
import java.util.Objects;

public class Backpacklistener implements Listener {
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
            new Backpack().openbackpack(event.getPlayer(),itemStack);
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
            new Backpack().saveBackpack((Player) event.getPlayer(), event.getInventory());
        }
    }

    @EventHandler
    public void onclick(InventoryClickEvent event) throws IOException {
        if(!event.getView().title().equals(Component.text("背包")
                .decoration(TextDecoration.ITALIC,false).decoration(TextDecoration.BOLD,true)
                .color(TextColor.fromHexString("#eea468")))){
            return;
        }
        ItemStack itemStack = event.getWhoClicked().getInventory().getItemInMainHand();
        if(itemStack.isEmpty()){
            return;
        }
        ItemMeta meta = itemStack.getItemMeta();
        if(!meta.hasItemModel()){
            return;
        }else if(Objects.requireNonNull(meta.getItemModel()).toString().equals("mlc:mlc_backpack")){
            ItemStack itemStack1 = event.getCurrentItem();
            if(itemStack1!=null&&itemStack1.getType().equals(Material.SHULKER_BOX)){
                event.setCancelled(true);
                return;
            }

            if (itemStack1 != null) {
                ItemMeta meta1 = itemStack1.getItemMeta();
                if(meta1.hasItemModel()&& Objects.requireNonNull(meta1.getItemModel()).toString().equals("mlc:mlc_backpack")){
                    event.setCancelled(true);
                    return;
                }
            }

        }
    }
}
