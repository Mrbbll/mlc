package com.mlc.mlc.mlcmain.dropmoney;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import static com.mlc.mlc.mlcmain.items.itemmannager.Mlcitems.money_nugget;

public class DropmoneyListener implements Listener {
    @EventHandler
    public void onDropmoney(BlockBreakEvent event){
        switch (event.getBlock().getType()){
            case ANDESITE, DIORITE, GRANITE, TUFF:
                if(Math.random()<0.12){
                    event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(),money_nugget.clone());
                    event.setDropItems(false);
                }
                break;
        }

    }
}
