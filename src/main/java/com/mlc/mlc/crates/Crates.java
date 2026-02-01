package com.mlc.mlc.crates;

import com.mlc.mlc.items.itemmannager.Cratesitems;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Random;

public class Crates {
    public static Inventory inventory;
    public static void getitems(Player player){
        java.util.Random random = new java.util.Random();
        int maxnum = Cratesitems.itemslist.size();
        int randomnum = random.nextInt(maxnum);
        player.give(Cratesitems.itemslist.get(randomnum));
        player.sendMessage(Component.text("你获得了" + Cratesitems.itemslist.get(randomnum).getItemMeta().displayName()));
    }



}
