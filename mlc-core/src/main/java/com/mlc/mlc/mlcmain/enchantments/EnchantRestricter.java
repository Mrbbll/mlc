package com.mlc.mlc.mlcmain.enchantments;

import com.mlc.mlc.mlcmain.chat.commands.Item;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import org.bukkit.event.inventory.InventoryType;
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
}
