package com.mlc.mlc.backpack.listener;

import com.mlc.mlc.backpack.backpackgui.Backpack;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;


public class Backpacklistener implements Listener {

    public static Map<UUID, ItemStack> openmap = new HashMap<>();
    //public static ItemStack itemStack;

    @EventHandler
    public void onrightclik(PlayerInteractEvent event) throws IOException {
        if(event.getAction() == Action.LEFT_CLICK_AIR||event.getAction()==Action.LEFT_CLICK_BLOCK||event.getAction()==Action.PHYSICAL){
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
            openmap.put(event.getPlayer().getUniqueId(),itemStack);
            Backpack.openbackpack(event.getPlayer(),itemStack);
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
        ItemStack itemStack = openmap.get(event.getPlayer().getUniqueId());
        Backpack.saveBackpack((Player) event.getPlayer(), event.getInventory(), itemStack);

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
            int num = event.getHotbarButton();
            if(num != -1){
                event.setCancelled(true);
            }
            ItemStack itemStack1 = event.getCurrentItem();
            if(itemStack1!=null&&itemStack1.getType().equals(Material.SHULKER_BOX)){
                event.setCancelled(true);
                return;
            }
            if(itemStack1!=null&& itemStack1.getItemMeta().hasItemModel()){
                if(Objects.requireNonNull(itemStack1.getItemMeta().getItemModel()).toString().equals("mlc:mlc_backpack")){
                    event.setCancelled(true);
                    return;
                }
            }

            if (itemStack1 != null) {
                ItemMeta meta1 = itemStack1.getItemMeta();
                if(meta1.hasItemModel()&& Objects.requireNonNull(meta1.getItemModel()).toString().equals("mlc:mlc_backpack")){
                    event.setCancelled(true);
                }
            }
        }
    }
}
