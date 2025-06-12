package com.mlc.mlc.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
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
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executor;

import static com.mlc.mlc.Mlc.playerfiledir;

public class delhome implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        Player player = (Player) commandSender;
        UUID uuid = player.getUniqueId();

        //多参
        if(strings.length>=2){
            player.sendMessage(Component.text("格式：/delhome <家的名字>", TextColor.color(0xFF4213)));
            return false;
        }

        //无参
        if(strings.length==0){
            try {
                File playerfile = new File(playerfiledir,uuid+".yml");
                if(!playerfile.exists()){
                    playerfile.createNewFile();
                };
                FileConfiguration playerfileconfig = YamlConfiguration.loadConfiguration(playerfile);
                ConfigurationSection playerhome = playerfileconfig.getConfigurationSection("homes.home");
                if(playerhome==null){
                    player.sendMessage(Component.text("成功移除默认的home路径点",TextColor.color(0xFF4213)));
                    return true;
                }
                playerfileconfig.set("homes.home",null);
                player.sendMessage(Component.text("成功移除默认的home路径点",TextColor.color(0xFF4213)));
                playerfileconfig.save(playerfile);
                return true;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
        //有参
        try {
            File playerfile = new File(playerfiledir,uuid+".yml");
            if(!playerfile.exists()){
                playerfile.createNewFile();
            };
            FileConfiguration playerfileconfig = YamlConfiguration.loadConfiguration(playerfile);
            ConfigurationSection playerhome = playerfileconfig.getConfigurationSection("homes." + strings[0]);
            if (playerhome != null) {
                playerfileconfig.set("homes." + strings[0],null);
                player.sendMessage(Component.text("成功移除 "+strings[0]+" 路径点",TextColor.color(0xFF4213)));
                playerfileconfig.save(playerfile);
            }else {
                player.sendMessage(Component.text("无效的路径点",TextColor.color(0xFF4213)));
                return false;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        };

        return false;
    }
}
