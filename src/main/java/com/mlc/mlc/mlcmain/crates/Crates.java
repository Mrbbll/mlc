package com.mlc.mlc.mlcmain.crates;

import com.mlc.mlc.mlcmain.items.itemmannager.Cratesitems;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.mlc.mlc.Mlc.miniMessage;

public class Crates {
    public static Inventory inventory;
    public static void getitems(Player player){
        java.util.Random random = new java.util.Random();
        //随机进入3个物品池，概率为70% 27% 3%
        int randomnum = random.nextInt(100);
        if(randomnum < 70){
            randomnum = random.nextInt(Cratesitems.t3list.size());
            //判断背包满没，满了掉地上
            Integer itemid = Cratesitems.t3list.get(randomnum);
            giveitem(player, itemid);
        }else if(randomnum < 97){
            randomnum = random.nextInt(Cratesitems.t2list.size());
            Integer itemid = Cratesitems.t2list.get(randomnum);
            giveitem(player, itemid);
        }else{
            randomnum = random.nextInt(Cratesitems.t1list.size());
            Integer itemid = Cratesitems.t1list.get(randomnum);
            giveitem(player, itemid);
        }


    }

    private static void giveitem(Player player, Integer itemid) {
        ItemStack item = Cratesitems.itemsmap.get(itemid);
        if(item.effectiveName().equals(Component.text("随机附魔书"))){
            item = getrandombook();
        }else if(item.effectiveName().equals(Component.text("随机纹饰"))) {
            item = getrandompotion();
        }else if(item.effectiveName().equals(Component.text("随机陶罐碎片"))){
            item = getrandompotion();
        }else if(item.effectiveName().equals(Component.text("随机旗帜图案"))){
            item = getrandompotion();
        }

        giveitem(player, item);

        player.sendMessage(Component.text("你获得了").append(item.effectiveName()));
    }

    private static ItemStack getrandombook() {
        Random random = new Random();
        int randomlevel = random.nextInt(3);
        List<ItemStack> booklist = new ArrayList<>();
        booklist.add(new ItemStack(Material.ENCHANTED_BOOK));

        return new ItemStack(Material.BOOK);
    }

    private static void giveitem(Player player, ItemStack item) {
        if(player.getInventory().firstEmpty() == -1){
            player.getWorld().dropItemNaturally(player.getLocation(), item);
        }else{
            player.give(item);
        }
    }


}
