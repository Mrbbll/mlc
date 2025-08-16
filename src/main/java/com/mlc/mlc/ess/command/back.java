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
import java.util.UUID;

import static com.mlc.mlc.Mlc.playerfiledir;

public class back implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        Player player = (Player) commandSender;
        UUID uuid = player.getUniqueId();

        Map<String,Object> loc;
        try {
            File playerfile = new File(playerfiledir,uuid+".yml");
            if(!playerfile.exists()){
                playerfile.createNewFile();
            };
            FileConfiguration playerfileconfig = YamlConfiguration.loadConfiguration(playerfile);
            ConfigurationSection playerhome = playerfileconfig.getConfigurationSection("back");
            if (playerhome != null) {
                loc = playerhome.getValues(false);
                player.teleport(Location.deserialize(loc));
            }else {
                player.sendMessage(Component.text("没上个传送地点",TextColor.color(0xFF4213)));
                return false;
            }
            return true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
