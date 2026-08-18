package com.mlc.mlc.mlcmain.enchantments.listener;

import com.mlc.mlc.mlcmain.enchantments.EnchantmentMaxLevel;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class EnchantRestricter implements Listener {
    @EventHandler
    public void onPlayerclickInventory(InventoryClickEvent event){
        if(event.getInventory().getType().equals(InventoryType.PLAYER)){
            ItemStack item = event.getCurrentItem();
            if(item == null){
                return;
            }
            Map<Enchantment, Integer> enchantments = item.getEnchantments();

            for(Enchantment enchantment : enchantments.keySet()){
                int level = enchantments.get(enchantment);
                int maxLevel = EnchantmentMaxLevel.getMaxLevel(enchantment);
                if(level > maxLevel){
                    item.removeEnchantment(enchantment);
                    item.addEnchantment(enchantment, maxLevel);
                }
            }
        }
    }

    @EventHandler
    public void oninvopen(InventoryOpenEvent event){
        Inventory inventory = event.getInventory();
        ItemStack[] items = inventory.getContents();
        for(ItemStack item : items){
            if(item == null){
                continue;
            }
            Map<Enchantment, Integer> enchantments = item.getEnchantments();
            for(Enchantment enchantment : enchantments.keySet()){
                int level = enchantments.get(enchantment);
                int maxLevel = EnchantmentMaxLevel.getMaxLevel(enchantment);
                if(level > maxLevel){
                    item.removeEnchantment(enchantment);
                    item.addEnchantment(enchantment, maxLevel);
                }
            }
        }
    }
}
