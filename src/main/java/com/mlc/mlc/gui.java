package com.mlc.mlc;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.EquippableComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class gui {

    public Inventory inv;
    public Player owner;

    public gui(Player player){
        inv = Bukkit.createInventory(player, 9*5, Component.text("mlc"));
        owner = player;

        //0item
        ItemStack quit = new ItemStack(Material.STICK);
        ItemMeta meta = quit.getItemMeta();
        List<Component> lore1 = new ArrayList<>();
        lore1.add(Component.text("退出"));
        meta.itemName(Component.text("exit"));
        meta.lore(lore1);
        quit.setItemMeta(meta);

        //1item
        ItemStack fes1_hat_1_item = new ItemStack(Material.STICK);
        ItemMeta meta1 = fes1_hat_1_item.getItemMeta();

        NamespacedKey fes1_hat_1 = new NamespacedKey("mlc","fes1_hat_1");
        meta1.setItemModel(fes1_hat_1);
        meta1.itemName( Component.text("周年庆帽子1"));
//        EquipmentSlot equipmentSlot;
//        EquippableComponent equippableComponent = EquipmentSlot.HEAD;
        EquippableComponent equippableComponent = meta1.getEquippable();
        equippableComponent.setSlot(EquipmentSlot.HEAD);
        meta1.setEquippable(equippableComponent);
        meta1.setRarity(ItemRarity.UNCOMMON);
        meta1.setTooltipStyle(NamespacedKey.fromString("mlc:mlc"));


        fes1_hat_1_item.setItemMeta(meta1);


        inv.setItem(0,quit);
        inv.setItem(1,fes1_hat_1_item);
    }
    public void open(){
        owner.openInventory(inv);
    }


}


