package com.mlc.mlc.motd;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.mlc.mlc.Mlc.miniMessage;

public class Motd {
    public static List<String> players = new ArrayList<>();
    public static void setmotd(){
        Server server = Bukkit.getServer();
        String version = server.getMinecraftVersion();
//        String motd = "<gradient:#ff7dcf:#54f1ff:#f0ff1f>■■■■■■■■■■■■■■</gradient><b><color:#ffa940> MyLittleCraft </color></b><gradient:#f0ff1f:#54f1ff:#ff7dcf>■■■■■■■■■■■■■■</gradient>" +
//                "<newline><color:#ffffff>2026-" + version + "                                           <color:#ff5555>❤</color><head:ff3206cc-644e-42f3-8045-b91e7108289c></color>";
                String motd = "<gradient:#ff7dcf:#54f1ff:#f0ff1f>■■■■■■■■■■■■■■</gradient><b><color:#ffa940> MyLittleCraft </color></b><gradient:#f0ff1f:#54f1ff:#ff7dcf>■■■■■■■■■■■■■■</gradient>" +
                "<newline><color:#ffffff>2026-" + version + "                                               <color:#ff5555>❤</color>";
        server.motd(miniMessage.deserialize(motd));
    }
}
