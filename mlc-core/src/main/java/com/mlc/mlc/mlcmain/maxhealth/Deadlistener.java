package com.mlc.mlc.mlcmain.maxhealth;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;


public class Deadlistener implements Listener {
    @EventHandler
    public void ondead(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        if (player.hasMetadata("mlc-bot")) {
            return;
        }

        // 假人不参与死亡扣生命
        String isFake = PlaceholderAPI.setPlaceholders(
                player, "%fakeplayer_isfake%"
        );
        if ("true".equalsIgnoreCase(isFake)) {
            Objects.requireNonNull(player.getAttribute(Attribute.MAX_HEALTH)).setBaseValue(20);
            return;
        }

        AttributeInstance maxHealth =
                player.getAttribute(Attribute.MAX_HEALTH);
        if (maxHealth == null) {
            return;
        }

        // 扣基础生命上限，避免把装备或效果加成写回基础值
        double baseHealth = maxHealth.getBaseValue();
        if (baseHealth > 6.0) {
            maxHealth.setBaseValue(Math.max(6.0, baseHealth - 2.0));
        }
    }
}
