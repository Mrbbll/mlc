package com.mlc.mlc;

import com.mlc.mlc.Listener.backpacklistener;
import com.mlc.mlc.Listener.heavest;
import com.mlc.mlc.commands.mlcgui;
import com.mlc.mlc.commands.mymail;
import com.mlc.mlc.commands.sendmail;
import com.mlc.mlc.commands.sendmailtoall;
import com.mlc.mlc.Listener.guilistener;
import com.mlc.mlc.recipes.backpack;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public final class Mlc extends JavaPlugin {


    public static JavaPlugin instance;
    public static int wordsnum;

    public static List<Material> crops = new ArrayList<>();
    public static NamespacedKey damagetype;
    public static NamespacedKey armortype;
    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("\n\nmlc核心插件加载成功\n\n");
        //目前功能：邮箱，简易物品管理器，跳过睡觉
        this.saveDefaultConfig();
        saveResource("words.txt", false);
        saveResource("items.yml",false);
        instance = this;
        wordsnum = this.getConfig().getInt("words");
        damagetype = new NamespacedKey(this,"damagetype");
        armortype = new NamespacedKey(this,"armortype");

        crops.add(Material.CARROTS);
        crops.add(Material.POTATOES);
        crops.add(Material.BEETROOTS);
        crops.add(Material.WHEAT);

        new backpack().backpackrecipe();
        Bukkit.getPluginManager().registerEvents(new backpacklistener(),this);
        Bukkit.getPluginManager().registerEvents(new heavest(),this);
        Bukkit.getPluginManager().registerEvents(new guilistener(), this);
        Objects.requireNonNull(Bukkit.getPluginCommand("sendmail")).setExecutor((new sendmail()));
        Objects.requireNonNull(Bukkit.getPluginCommand("mymail")).setExecutor((new mymail()));
        Objects.requireNonNull(Bukkit.getPluginCommand("mlcgui")).setExecutor(new mlcgui());
        Objects.requireNonNull(Bukkit.getPluginCommand("sendmailtoall")).setExecutor(new sendmailtoall());


    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.resetRecipes();
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

