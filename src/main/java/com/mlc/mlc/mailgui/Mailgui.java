package com.mlc.mlc.mailgui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
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

public class Mailgui {

    public static Integer slot;


    public Mailgui() {
//        inv = Bukkit.createInventory(player, 6 * 9, Component.text("邮箱", TextColor.fromHexString("#66ee1d"), TextDecoration.BOLD));
//        owner = player;
//        slot = 0;
//
//        String string = player.getUniqueId()+ ".yml";
//        File mailDir = new File(instance.getDataFolder(), "mail");
//        File file = new File(mailDir, string);
//
//        invinit(file);
    }

    public static void invinit(File file,Inventory inv){
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
    public static void open(Player player){
        Inventory inv = Bukkit.createInventory(player, 6 * 9, Component.text("邮箱", TextColor.fromHexString("#66ee1d"), TextDecoration.BOLD));
        slot = 0;
        String string = player.getUniqueId()+ ".yml";
        File mailDir = new File(instance.getDataFolder(), "mail");
        File file = new File(mailDir, string);

        invinit(file,inv);
        player.openInventory(inv);
    }

    public static void open(Player player, File file){
        Inventory inv = Bukkit.createInventory(player, 6 * 9, Component.text("邮箱", TextColor.fromHexString("#66ee1d"), TextDecoration.BOLD));
        slot = 0;
        invinit(file,inv);
        player.openInventory(inv);
    }
};


