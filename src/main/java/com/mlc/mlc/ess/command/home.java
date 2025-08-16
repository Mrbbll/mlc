package com.mlc.mlc.ess.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.mlc.mlc.Mlc.playerfiledir;

public class home implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        Player player = (Player) commandSender;
        UUID uuid = player.getUniqueId();

        //多参
        if(strings.length>=2){
            player.sendMessage(Component.text("格式：/home <家的名字>", TextColor.color(0xFF4213)));

            return false;
        }

        //无参
        if(strings.length==0){
            Map<String,Object> loc;
            try {
                File playerfile = new File(playerfiledir,uuid+".yml");
                if(!playerfile.exists()){
                    playerfile.createNewFile();
                }
                FileConfiguration playerfileconfig = YamlConfiguration.loadConfiguration(playerfile);
                ConfigurationSection playerhome = playerfileconfig.getConfigurationSection("homes.home");
                if (playerhome != null) {
                    loc = playerhome.getValues(false);
                    player.teleport(Location.deserialize(loc));
                }else {
                    player.sendMessage(Component.text("还没有设置默认的家路径点",TextColor.color(0xFF4213)));
                    return false;
                }
                return true;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        //有参
        try {
            File playerfile = new File(playerfiledir,uuid+".yml");
            Map<String,Object> loc;

            if(!playerfile.exists()){
                playerfile.createNewFile();
            }
            FileConfiguration playerfileconfig = YamlConfiguration.loadConfiguration(playerfile);
            ConfigurationSection playerhome = playerfileconfig.getConfigurationSection("homes."+strings[0]);
            if (playerhome != null) {
                loc = playerhome.getValues(false);
                player.teleport(Location.deserialize(loc));
            }else {
                player.sendMessage(Component.text("无效的路径点",TextColor.color(0xFF4213)));
                return false;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {

        Player player = (Player) commandSender;
        UUID uuid = player.getUniqueId();

        if(strings.length>1){
            return null;
        }
        try {
            File playerfile = new File(playerfiledir,uuid+".yml");
            if(!playerfile.exists()){
                playerfile.createNewFile();
            }
            FileConfiguration playerfileconfig = YamlConfiguration.loadConfiguration(playerfile);
            ConfigurationSection configurationSection = playerfileconfig.getConfigurationSection("homes.");
            if (configurationSection != null) {
                Set<String> home =  configurationSection.getKeys(false);
                return new ArrayList<>(home);
            }

            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
