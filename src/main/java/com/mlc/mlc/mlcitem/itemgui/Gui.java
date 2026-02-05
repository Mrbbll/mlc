package com.mlc.mlc.mlcitem.itemgui;


import com.mlc.mlc.items.itemmannager.Cratesitems;
import com.mlc.mlc.items.itemmannager.Fesitems;
import com.mlc.mlc.items.itemmannager.Mlcitems;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.*;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.mlc.mlc.Mlc.instance;


public class Gui {
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();
    public static Map<Player,Openedgui> openinvmap = new HashMap<>();

    public static void loadGui(Player player,Openedgui openedgui) {

        Inventory inv = openedgui.getInv();
        int num = openedgui.getNum();
        //异步加载gui
        Bukkit.getScheduler().runTaskAsynchronously(instance,()->{
        for(int i=num;i<num+9*5;i++){
            if(i-num<Mlcitems.itemsmap.size()){
                inv.setItem(i-num, Mlcitems.itemsmap.get(i));
            }
            else{
                inv.setItem(i-num, ItemStack.of(Material.AIR));
            }
        }
        });

    }


    public static void loadgui(Player player, Guitype guitype, int num, Openedgui openedgui) {
        Inventory inv = openedgui.getInv();

        //异步加载gui
        Bukkit.getScheduler().runTaskAsynchronously(instance,()->{
            Map<Integer,ItemStack> itemsmap = new HashMap<>();
            switch (guitype){
            case CRATESITEMSGUI -> itemsmap = Cratesitems.itemsmap;
            case FESITEMSGUI -> itemsmap = Fesitems.itemsmap;
            case MLCITEMSGUI -> itemsmap = Mlcitems.itemsmap;
            }

            for(int i=num;i<num+9*5;i++){
                if(i-num<itemsmap.size()){
                    inv.setItem(i-num, itemsmap.get(i));
                }
                else{
                    inv.setItem(i-num, ItemStack.of(Material.AIR));
                }
            }
        });

//
//        switch (guitype){
//            case CRATESITEMSGUI:
//                for(int i=num;i<num+9*5;i++){
//                    if(i-num<Cratesitems.itemsmap.size()){
//                        inv.setItem(i-num, Cratesitems.itemsmap.get(i));
//                    }
//                    else{
//                        inv.setItem(i-num, ItemStack.of(Material.AIR));
//                    }
//                }
//                break;
//            case FESITEMSGUI:
//                for(int i=num;i<num+9*5;i++){
//                    if(i-num<Fesitems.itemsmap.size()){
//                        inv.setItem(i-num, Fesitems.itemsmap.get(i));
//                    }
//                    else{
//                        inv.setItem(i-num, ItemStack.of(Material.AIR));
//                    }
//                }
//                break;
//            case MLCITEMSGUI:
//                for(int i=num;i<num+9*5;i++){
//                    if(i-num<Mlcitems.itemsmap.size()){
//                        inv.setItem(i-num, Mlcitems.itemsmap.get(i));
//                    }
//                    else{
//                        inv.setItem(i-num, ItemStack.of(Material.AIR));
//                    }
//                }
//                break;
//        }


        openedgui.setNum(num);
        openedgui.setGuitype(guitype);
        openinvmap.put(player,openedgui);
    }

    public static void open(Player player){
        if(openinvmap.containsKey(player)){
            player.openInventory(openinvmap.get(player).getInv());
            return;
        }
        Inventory inv = Bukkit.createInventory(player, 9 * 6, Component.text("mlc", TextColor.fromHexString("#f73636")));
        Openedgui openedgui = new Openedgui(player,1,inv,Guitype.MLCITEMSGUI);
        loadGui(player,openedgui);
        inv.setItem(45,ItemStack.of(Material.ARROW));
        inv.setItem(48,ItemStack.of(Material.RED_DYE));
        inv.setItem(49,ItemStack.of(Material.YELLOW_DYE));
        inv.setItem(50,ItemStack.of(Material.BLUE_DYE));
        inv.setItem(53,ItemStack.of(Material.ARROW));
        player.openInventory(inv);
        openinvmap.put(player,openedgui);
    }
    public static void refresh(Guitype guitype, Player player, int num, Openedgui openedgui){
        loadgui(player, guitype, num,openedgui);
    }

}


