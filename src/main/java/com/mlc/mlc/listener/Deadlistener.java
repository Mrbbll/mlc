package com.mlc.mlc.listener;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.Nullable;



public class Deadlistener implements Listener {
    @EventHandler
    public void ondead(PlayerDeathEvent event){
        Player player = event.getPlayer();
        @Nullable AttributeInstance maxHealth = player.getAttribute(Attribute.MAX_HEALTH);
        if (maxHealth != null) {
            double maxHealthnum = maxHealth.getValue();
            if(maxHealthnum > 6.0){
                maxHealth.setBaseValue(maxHealthnum - 2);
            }

        }

    }
}
