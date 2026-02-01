package com.mlc.mlc.mlcitem.listener;

import com.mlc.mlc.items.itemmannager.Mlcitems;
import com.mlc.mlc.mlcitem.itemgui.Guitype;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.Objects;

import static com.mlc.mlc.mlcitem.itemgui.Gui.*;

public class Guilistener implements Listener {

    @EventHandler
    //mlcitem界面事件监听
    public void onClick(InventoryClickEvent e) throws IOException {
        Player player = (Player) e.getWhoClicked();
        InventoryView inv = player.getOpenInventory();
        if (inv.title().equals(Component.text("mlc", TextColor.fromHexString("#f73636")))) {
            e.setCancelled(true);
            if(e.getRawSlot()<0||e.getRawSlot()>=e.getInventory().getSize())
            {
                return;
            };
            if(e.getRawSlot()==36)
            {
                //pageup
                if(num-36>=0){
                    num-=36;
                    refresh(Guitype.MLCITEMSGUI,player,num);
                }
            } else if (e.getRawSlot()==44) {
                //pagedown
                if(num+36 < Mlcitems.itemslist.size()){
                    num+=36;
                    refresh(Guitype.MLCITEMSGUI,player,num);
                }
            } else if (e.getRawSlot()==39) {
                //changemenu
                refresh(Guitype.CRATESITEMSGUI,player,0);
            } else if (e.getRawSlot()==40) {
                refresh(Guitype.FESITEMSGUI,player,0);
            } else if (e.getRawSlot()==41) {
                refresh(Guitype.MLCITEMSGUI,player,0);
            }

            ItemStack itemStack = e.getCurrentItem();
            if(itemStack == null)
            {
                return;
            };
            if(Objects.equals(itemStack.getItemMeta().itemName(), Component.text("exit")))
            {
                player.kick(Component.text("拜拜"));
            }
            else if(Objects.equals(itemStack.getItemMeta().itemName(), Component.text("back")))
            {

            }
            else{
                player.give(itemStack);
            };
        };
    }
}
