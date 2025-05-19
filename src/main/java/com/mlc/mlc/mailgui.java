package com.mlc.mlc;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.*;

import static com.mlc.mlc.Mlc.instance;

public class mailgui {

    public Inventory inv;
    public Player owner;
    public Integer slot;


    public mailgui(Player player) {
        inv = Bukkit.createInventory(player, 6 * 9, Component.text("邮箱"));
        owner = player;
        slot = 0;

        String string = player.getName() + ".yml";
        File mailDir = new File(instance.getDataFolder(), "mail");
        File file = new File(mailDir, string);


        invinit(file);
    }


    public void invinit(File file){
        FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(file);
        owner.sendMessage("c");
        ConfigurationSection configurationSection = fileConfiguration.getConfigurationSection("item");
        if (configurationSection != null) {

            Set<String> itemids = configurationSection.getKeys(false);

            owner.sendMessage("b");

            for(String itemid : itemids){

                ConfigurationSection itemConfig = configurationSection.getConfigurationSection(itemid);

                if (itemConfig == null) continue;
                int amount = itemConfig.getInt("amount",1);
                Material material = Material.getMaterial(itemConfig.getString("type","BARRIER"));
//                ItemStack itemStack = new ItemStack(material,amount);
//                ItemMeta itemMeta  = (ItemMeta) ItemStack.deserialize(itemConfig.getConfigurationSection("itemmeta").getValues(false));
//                itemStack.setItemMeta(itemMeta);
                ItemStack itemStack = ItemStack.deserialize(itemConfig.getConfigurationSection("itemmeta").getValues(false));
                itemStack.setAmount(amount);
//                itemStack.setItemMeta(itemStack1.getItemMeta());
                inv.setItem(slot,itemStack);
                owner.sendMessage("a");

                slot++;
                if(slot==inv.getSize())
                {
                    break;
                }
            }

        }
//        else {
//            inv.setItem(0,null);
//            return;
//        }

    }


    public void open(){
        owner.openInventory(inv);
    }

};


