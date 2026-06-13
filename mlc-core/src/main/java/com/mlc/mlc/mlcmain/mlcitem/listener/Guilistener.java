package com.mlc.mlc.mlcmain.mlcitem.listener;

import com.mlc.mlc.mlcmain.items.itemmannager.Cratesitems;
import com.mlc.mlc.mlcmain.items.itemmannager.Fesitems;
import com.mlc.mlc.mlcmain.items.itemmannager.Mlcitems;
import com.mlc.mlc.mlcmain.mlcitem.itemgui.Guitype;
import com.mlc.mlc.mlcmain.mlcitem.itemgui.Openedgui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;

import static com.mlc.mlc.mlcmain.mlcitem.itemgui.Gui.*;
import static com.mlc.mlc.mlcmain.mlcitem.itemgui.Guitype.CRATESITEMSGUI;

public class Guilistener implements Listener {

    @EventHandler
    //mlcitem界面事件监听
    public void onClick(InventoryClickEvent e) throws IOException {
        Player player = (Player) e.getWhoClicked();
        Inventory inv = player.getOpenInventory().getTopInventory();
        if (openinvmap.containsKey(player) && inv.equals(openinvmap.get(player).getInv())) {
            e.setCancelled(true);
            Openedgui openedgui = openinvmap.get(player);
            int num = openinvmap.get(player).getNum();
            Guitype guitype = openinvmap.get(player).getGuitype();
            if(e.getRawSlot()<0||e.getRawSlot()>=e.getInventory().getSize())
            {
                return;
            };
            if(e.getRawSlot()==45)
            {
                //pageup
                if(num-45>0){
                    num-=45;
                    refresh(guitype,player,num,openedgui);
                }
                return;
            } else if (e.getRawSlot()==53) {
                //pagedown
                player.sendMessage(Component.text("num:"+num, TextColor.fromHexString("#f73636")));

                int maxnum = switch (guitype) {
                    case CRATESITEMSGUI -> Cratesitems.itemsmap.size();
                    case FESITEMSGUI -> Fesitems.itemsmap.size();
                    case MLCITEMSGUI -> Mlcitems.itemsmap.size();
                };
                player.sendMessage(Component.text("maxnum:"+maxnum, TextColor.fromHexString("#f73636")));
                if(num+45 < maxnum){
                    num+=45;
                    refresh(guitype,player,num,openedgui);
                    player.sendMessage(Component.text("num1:"+num, TextColor.fromHexString("#f73636")));
                }
                return;
            } else if (e.getRawSlot()==50) {
                //changemenu
                refresh(CRATESITEMSGUI,player,1,openedgui);
                return;
            } else if (e.getRawSlot()==49) {
                refresh(Guitype.FESITEMSGUI,player,1,openedgui);
                return;
            } else if (e.getRawSlot()==48) {
                refresh(Guitype.MLCITEMSGUI,player,1,openedgui);
                return;
            }

            ItemStack itemStack = e.getCurrentItem();
            if(itemStack == null)
            {
                return;
            }else {
                player.give(itemStack);
            };
        };
    }
}
