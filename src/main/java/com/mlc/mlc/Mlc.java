package com.mlc.mlc;


import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.bukkit.event.block.BlockPlaceEvent;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class Mlc extends JavaPlugin {


    public static JavaPlugin instance;
    public static int wordsnum;

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("mlc核心插件加载成功");

        this.saveDefaultConfig();
        saveResource("words.txt", false);

        instance = this;
        wordsnum = this.getConfig().getInt("words");


        Bukkit.getPluginManager().registerEvents(new guilistener(), this);
        Objects.requireNonNull(Bukkit.getPluginCommand("mlcgui")).setExecutor(new mlcgui());
    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("mlc核心插件卸载成功");
    }

    public static List<String> readwords(String resourcePath) throws IOException {
        File file = new File(resourcePath,"words.txt");
//        instance.getLogger().info(file.getPath());
        FileInputStream fileInputStream = new FileInputStream(file.getPath()) ;
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(inputStreamReader);

        return reader.lines().toList();
    }
}

