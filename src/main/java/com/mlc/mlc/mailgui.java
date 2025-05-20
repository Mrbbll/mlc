package com.mlc.mlc;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
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
        ConfigurationSection configurationSection = fileConfiguration.getConfigurationSection("item");
        if (configurationSection != null) {
            Set<String> itemids = configurationSection.getKeys(false);
            //遍历获取物品
            for(String itemid : itemids){
                ConfigurationSection itemConfig = configurationSection.getConfigurationSection(itemid);
                if (itemConfig == null) continue;
                ItemStack itemStack = ItemStack.deserialize(itemConfig.getValues(false));

                inv.setItem(slot,itemStack);
                //判断格子是否超了
                slot++;
                if(slot>=inv.getSize())
                {
                    break;
                }
            }

        }
        else {
            inv.setItem(0,null);
        }
    }

    public void open(){
        owner.openInventory(inv);
    }

};


