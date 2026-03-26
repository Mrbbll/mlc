package com.mlc.mlc.mlcmain.ess.listener;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import static com.mlc.mlc.Mlc.playerfiledir;

public class Tplistener implements Listener {
    @EventHandler
    public void onteleport(PlayerTeleportEvent event) throws IOException {
        PlayerTeleportEvent.TeleportCause cause = event.getCause();

        if(cause.equals(PlayerTeleportEvent.TeleportCause.PLUGIN)){
            Player player = event.getPlayer();
            UUID uuid = player.getUniqueId();
            File playerfile = new File(playerfiledir,uuid+".yml");
            if(!playerfile.exists()){
                playerfile.createNewFile();
            };
            FileConfiguration playerfileconfig = YamlConfiguration.loadConfiguration(playerfile);

            //ConfigurationSection back = playerfileconfig.getConfigurationSection("back");

            Location location = player.getLocation();
            Map<String,Object> serialized = location.serialize();
            playerfileconfig.set("back",serialized);
//        player.sendMessage(Component.text("成功回到上个路径点",TextColor.color(0x66EE1D)));
            playerfileconfig.save(playerfile);
        }
    }
    @EventHandler
    public void ondead(PlayerDeathEvent event) throws IOException {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        File playerfile = new File(playerfiledir,uuid+".yml");
        if(!playerfile.exists()){
            playerfile.createNewFile();
        };
        FileConfiguration playerfileconfig = YamlConfiguration.loadConfiguration(playerfile);
        Location location = player.getLocation();
        Map<String,Object> serialized = location.serialize();
        playerfileconfig.set("back",serialized);
        playerfileconfig.save(playerfile);
    }
}
