package com.mlc.mlc.mlcitem.itemgui;


import com.mlc.mlc.items.itemmannager.Cratesitems;
import com.mlc.mlc.items.itemmannager.Fesitems;
import com.mlc.mlc.items.itemmannager.Mlcitems;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.inventory.*;
import org.bukkit.entity.Player;


public class Gui {
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();
    public static Inventory inv;
    public Player owner;
    public static int num = 0;


    public static void loadGui(Player player) {
        inv = Bukkit.createInventory(player, 9 * 5, Component.text("mlc", TextColor.fromHexString("#f73636")));
        for(int i=num;i<num+9*4;i++){
            inv.setItem(i-num, Cratesitems.itemslist.get(i-num));
        }
    }

    public static void loadgui(Player player, Guitype guitype, int num) {
        inv = Bukkit.createInventory(player, 9 * 5, Component.text("mlc", TextColor.fromHexString("#f73636")));
        switch (guitype){
            case CRATESITEMSGUI:
                for(int i=num;i<num+9*4;i++){
                    inv.setItem(i-num, Cratesitems.itemslist.get(i-num));
                }
                break;
            case FESITEMSGUI:
                for(int i=num;i<num+9*4;i++){
                    inv.setItem(i-num, Fesitems.itemslist.get(i-num));
                }
                break;
            case MLCITEMSGUI:
                for(int i=num;i<num+9*4;i++){
                    inv.setItem(i-num, Mlcitems.itemslist.get(i-num));
                }
                break;
        }
    }

    public static void open(Player player){
        loadGui(player);
        player.openInventory(inv);
    }
    public void refresh(Guitype guitype, Player player, int num){
        loadgui(player, guitype, num);
        player.updateInventory();
    }

}


