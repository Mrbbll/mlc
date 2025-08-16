package com.mlc.mlc.listener;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class Eatlistener implements Listener {
    @EventHandler
    public void oneat(PlayerItemConsumeEvent event){
        ItemStack itemStack =event.getItem();
        ItemMeta itemMeta = itemStack.getItemMeta();
        if(!itemMeta.hasItemModel()){
            return;
        }
        if(Objects.requireNonNull(itemMeta.getItemModel()).toString().equals("mlc:mlc_healfood")){
            Player player = event.getPlayer();
            @Nullable AttributeInstance maxHealth = player.getAttribute(Attribute.MAX_HEALTH);
            assert maxHealth != null;
            double healthnum = maxHealth.getBaseValue();
            if(healthnum<=14){
                maxHealth.setBaseValue(healthnum + 6);
            }
            else {
                maxHealth.setBaseValue(20);
            }
        }

    }

}
