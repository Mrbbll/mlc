package com.mlc.mlc.Listener;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import static com.mlc.mlc.Mlc.instance;

public class unsitlistener implements Listener {
    @EventHandler
    public void onquit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        Entity entity =  player.getVehicle();
        if (entity != null) {
            PersistentDataContainer persistentDataContainer = entity.getPersistentDataContainer();
            if(persistentDataContainer.has(new NamespacedKey(instance,"sit"), PersistentDataType.STRING)){
                entity.remove();
            }
        }
    }

    @EventHandler
    public void onunsit(PlayerToggleSneakEvent event){
        Entity entity = event.getPlayer().getVehicle();
        PersistentDataContainer persistentDataContainer = null;
        if (entity != null) {
            persistentDataContainer = entity.getPersistentDataContainer();
            if(persistentDataContainer.has(new NamespacedKey(instance,"sit"), PersistentDataType.STRING)){
                entity.remove();
            }
        }
    }

    @EventHandler
    public void onteleport(PlayerTeleportEvent event){
        Entity entity = event.getPlayer().getVehicle();
        PersistentDataContainer persistentDataContainer = null;
        if (entity != null) {
            persistentDataContainer = entity.getPersistentDataContainer();
            if(persistentDataContainer.has(new NamespacedKey(instance,"sit"), PersistentDataType.STRING)){
                entity.remove();
            }
        }
    }

    @EventHandler
    public void ondeath(PlayerDeathEvent event){
        Entity entity = event.getPlayer().getVehicle();
        PersistentDataContainer persistentDataContainer = null;
        if (entity != null) {
            persistentDataContainer = entity.getPersistentDataContainer();
            if(persistentDataContainer.has(new NamespacedKey(instance,"sit"), PersistentDataType.STRING)){
                entity.remove();
            }
        }
    }

}
