package com.mlc.mlc.listener;


import com.mlc.mlc.motd.Motd;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;

import static com.mlc.mlc.Mlc.instance;

public class Joinlistener implements Listener {
    @EventHandler
    public void playerjion(PlayerJoinEvent event){
        Player player = event.getPlayer();
        player.sendMessage(Component.text("服务器插件部分为腐竹自研，如果遇到bug或者功能建议请联系腐竹(。・ω・。)", TextColor.color(0x66EE1D)));
        Motd.setmotd();
    }


}
