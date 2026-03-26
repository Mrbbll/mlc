package com.mlc.mlc.mlcmain.crates;

import com.mlc.mlc.mlcmain.items.itemmannager.Cratesitems;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

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
        if(player.getInventory().firstEmpty() == -1){
            player.getWorld().dropItemNaturally(player.getLocation(),Cratesitems.itemsmap.get(itemid));
        }else{
            player.give(Cratesitems.itemsmap.get(itemid));
        }
        player.sendMessage(Component.text("你获得了" + Cratesitems.itemsmap.get(itemid).getItemMeta().displayName()));
    }


}
