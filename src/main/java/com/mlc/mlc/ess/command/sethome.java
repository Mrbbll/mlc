package com.mlc.mlc.ess.command;

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
import java.util.Set;
import java.util.UUID;

import static com.mlc.mlc.Mlc.playerfiledir;

public class sethome implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        Player player = (Player) commandSender;
        UUID uuid = player.getUniqueId();

        //多参
        if(strings.length>=2){
            player.sendMessage(Component.text("格式：/sethome <家的名字>", TextColor.color(0xFF4213)));
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

                ConfigurationSection home = playerfileconfig.getConfigurationSection("homes");
                Set<String> homes = null;
                if (home != null) {
                    homes = home.getKeys(false);
                }
                if( homes !=null && homes.size()>=10){
                    player.sendMessage(Component.text("超过最大家数量",TextColor.color(0xFF4213)));
                    return false;
                }

                Location location = player.getLocation();
                Map<String,Object> serialized = location.serialize();
                playerfileconfig.set("homes.home",serialized);
                player.sendMessage(Component.text("成功设置默认路径点",TextColor.color(0x66EE1D)));
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

            ConfigurationSection home = playerfileconfig.getConfigurationSection("homes");
            Set<String> homes = null;
            if (home != null) {
                homes = home.getKeys(false);
            }
            if( homes !=null && homes.size()>=10){
                player.sendMessage(Component.text("超过最大家数量",TextColor.color(0xFF4213)));
                return false;
            }

            Location location = player.getLocation();
            Map<String,Object> serialized = location.serialize();
            playerfileconfig.set("homes." + strings[0] ,serialized);
            player.sendMessage(Component.text("成功设置 "+strings[0] +" 路径点",TextColor.color(0x66EE1D)));
            playerfileconfig.save(playerfile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        };

        return false;
    }
}
