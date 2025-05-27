package com.mlc.mlc;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;


public final class Mlc extends JavaPlugin {


    public static JavaPlugin instance;
    public static int wordsnum;

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("\n\nmlc核心插件加载成功\n\n");

        this.saveDefaultConfig();
        saveResource("words.txt", false);

        instance = this;
        wordsnum = this.getConfig().getInt("words");


        Bukkit.getPluginManager().registerEvents(new guilistener(), this);
        Objects.requireNonNull(Bukkit.getPluginCommand("sendmail")).setExecutor((new sendmail()));
        Objects.requireNonNull(Bukkit.getPluginCommand("mymail")).setExecutor((new mymail()));
        Objects.requireNonNull(Bukkit.getPluginCommand("mlcgui")).setExecutor(new mlcgui());
        Objects.requireNonNull(Bukkit.getPluginCommand("sendmailtoall")).setExecutor(new sendmailtoall());
    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("\n\nmlc核心插件卸载成功\n\n");
    }

    public static List<String> readwords(String resourcePath) throws IOException {
        File file = new File(resourcePath,"words.txt");
        FileInputStream fileInputStream = new FileInputStream(file.getPath()) ;
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(inputStreamReader);

        return reader.lines().toList();
    }
}

