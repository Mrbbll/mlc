package com.mlc.mlc.dropmoney;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import static com.mlc.mlc.items.itemmannager.mlcitems.money_nugget;

public class DropmoneyListener implements Listener {
    @EventHandler
    public void onDropmoney(BlockBreakEvent event){
        switch (event.getBlock().getType()){
            case ANDESITE, DIORITE, GRANITE, TUFF:
                if(Math.random()<0.1){
                    event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(),money_nugget);
                    event.setDropItems(false);
                }
                break;
        }

    }
}
