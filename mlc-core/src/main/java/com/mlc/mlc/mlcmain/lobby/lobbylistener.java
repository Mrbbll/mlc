package com.mlc.mlc.mlcmain.lobby;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class lobbylistener implements Listener {
    @EventHandler
    public static void playerjoinevent(PlayerJoinEvent event){
        Player player = event.getPlayer();
        long lastlogtime = player.getLastLogin();
        long currenttime;
    }

}
