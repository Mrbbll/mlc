package com.mlc.mlc;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.mlc.mlc.Mlc.instance;

public class mailgui {

    public Inventory inv;
    public Player owner;



    public mailgui(Player player) {
        inv = Bukkit.createInventory(player, 6 * 9, Component.text("邮箱"));
        owner = player;


        String string = player.getName() + ".yml";
        File mailDir = new File(instance.getDataFolder(), "mail");
        File file = new File(mailDir, string);
        FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(file);

        invinit();
    }


    public void invinit(){

    }


    public void open(){
        owner.openInventory(inv);
    }

};


