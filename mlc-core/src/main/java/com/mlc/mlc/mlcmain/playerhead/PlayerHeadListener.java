package com.mlc.mlc.mlcmain.playerhead;

import org.bukkit.Material;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Creeper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public final class PlayerHeadListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        DamageSource damageSource = event.getDamageSource();
        if (!(damageSource.getDirectEntity() instanceof Creeper creeper) || !creeper.isPowered()) {
            return;
        }

        DamageType damageType = damageSource.getDamageType();
        if (damageType != DamageType.EXPLOSION && damageType != DamageType.PLAYER_EXPLOSION) {
            return;
        }

        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setOwningPlayer(event.getEntity());
        head.setItemMeta(meta);
        event.getDrops().add(head);
    }
}
