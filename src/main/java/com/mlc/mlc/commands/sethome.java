package com.mlc.mlc.commands;

import com.mlc.mlc.Playerfilereader;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.mlc.mlc.Mlc.instance;
import static com.mlc.mlc.Mlc.playerfiledir;

public class sethome implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        Player player = (Player) commandSender;
        UUID uuid = player.getUniqueId();
        int homenum;

        if(strings.length==0){
            try {
                File playerfile = new File(playerfiledir,uuid.toString()+".yml");
                if(!playerfile.exists()){
                    playerfile.createNewFile();
                };
                FileConfiguration playerfileconfig = YamlConfiguration.loadConfiguration(playerfile);
                if(!playerfileconfig.contains("homes")){
                    playerfileconfig.set("homes",null);
                    playerfileconfig.save(playerfile);
                }
                Location location = player.getLocation();
                Map<String,Object> serialized = location.serialize();
                playerfileconfig.set("homes.home",serialized);
                playerfileconfig.save(playerfile);
                return true;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
        try {
            File playerfile = new File(playerfiledir,uuid.toString()+".yml");
            if(!playerfile.exists()){
                playerfile.createNewFile();
            };
            FileConfiguration playerfileconfig = YamlConfiguration.loadConfiguration(playerfile);
            if(!playerfileconfig.contains("homes")){
                playerfileconfig.set("homes",null);
                playerfileconfig.save(playerfile);
            }
            Location location = player.getLocation();
            Map<String,Object> serialized = location.serialize();
            playerfileconfig.set("homes." + strings[0] ,serialized);
            playerfileconfig.save(playerfile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        };

        return false;
    }
}
