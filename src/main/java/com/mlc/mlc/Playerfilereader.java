package com.mlc.mlc;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import static com.mlc.mlc.Mlc.playerfiledir;

public class Playerfilereader {

    public static FileConfiguration openplayerfile(UUID uuid) throws IOException {
        File playerfile = new File(playerfiledir,uuid.toString());
        if(!playerfile.exists()){
            playerfile.createNewFile();
        };
        FileConfiguration playerfileconfig = YamlConfiguration.loadConfiguration(playerfile);
        return playerfileconfig;
    }
    public static ConfigurationSection openplayerhome(UUID uuid) throws IOException {
        File playerfile = new File(playerfiledir, uuid.toString());
        if(!playerfile.exists()){
            playerfile.createNewFile();
        };
        FileConfiguration playerfileconfig = YamlConfiguration.loadConfiguration(playerfile);
        if(!playerfileconfig.contains("homes")){
            playerfileconfig.set("homes",null);
            playerfileconfig.save(playerfile);
        }
        ConfigurationSection playerhome = playerfileconfig.getConfigurationSection("homes");

        return playerhome;
    };

}
