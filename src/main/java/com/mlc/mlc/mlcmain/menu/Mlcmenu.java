package com.mlc.mlc.mlcmain.menu;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

public class Mlcmenu {
    public static Inventory menuinv;

    public static void initmenuinv(){
        menuinv = Bukkit.createInventory(null,9*6, Component.text("mlc", TextColor.fromHexString("#f73636")));
        for (int i = 0; i < 9*6; i++) {
            menuinv.setItem(i, null);
        }
    }

}
