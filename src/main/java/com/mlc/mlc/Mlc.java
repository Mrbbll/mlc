package com.mlc.mlc;

import com.mlc.mlc.hook.economy.MlcEconomy;
import com.mlc.mlc.hook.economy.Moneyfilemanager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.*;


public final class Mlc extends JavaPlugin {


    public static JavaPlugin instance;
    public static FileConfiguration fileConfiguration;
    public static int wordsnum;
    public static List<Material> crops = new ArrayList<>();
    public static NamespacedKey damagetype;
    public static NamespacedKey armortype;
    public static Map<UUID,UUID> Tpamap = new HashMap<>();
    public static Map<UUID,UUID> Tpaheremap = new HashMap<>();
    public static FileConfiguration backpackfile;
    public static File playerfiledir;
    public static MiniMessage miniMessage;

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("\n\nmlc核心插件加载成功\n\n");
        //注册经济系统
        MlcEconomy mlcEconomy = new MlcEconomy(this);
        getServer().getServicesManager().register(Economy.class, mlcEconomy, this, ServicePriority.Normal);
        // 验证注册
        RegisteredServiceProvider<Economy> rsp = getServer()
                .getServicesManager()
                .getRegistration(Economy.class);

        if (rsp != null && rsp.getProvider() == mlcEconomy) {
            getLogger().info("经济服务注册成功: " + mlcEconomy.getName());
        } else if (rsp != null) {
            getLogger().warning("有其他经济服务已注册: " + rsp.getProvider().getName());
        } else {
            getLogger().warning("经济服务注册失败");
        }

        //目前功能：邮箱，简易物品管理器，跳过睡觉
        this.saveDefaultConfig();
        saveResource("words.txt", false);
        saveResource("fesitems.yml",false);
        instance = this;
        fileConfiguration = instance.getConfig();
        wordsnum = this.getConfig().getInt("words");
        damagetype = new NamespacedKey(this,"damagetype");
        armortype = new NamespacedKey(this,"armortype");

        //minimessage初始化
        miniMessage = MiniMessage.miniMessage();

        //加右键收获作物
        crops.add(Material.CARROTS);
        crops.add(Material.POTATOES);
        crops.add(Material.BEETROOTS);
        crops.add(Material.WHEAT);

        //初始化背包文件
        backpackfile = YamlConfiguration.loadConfiguration(backpackfilecreater());
        //初始化player文件夹
        playerfiledir = playerfiledircreater();
        //初始化货币文件夹
        try {
            new Moneyfilemanager();
            getLogger().info("\n\n货币数据库建立成功\n\n");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }



        try {
            Task.task();
        } catch (URISyntaxException | NoSuchAlgorithmException | IOException e) {
            throw new RuntimeException(e);
        }
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

    public File backpackfilecreater(){
        File backpackDir = new File(instance.getDataFolder(), "backpacks");
        if(!backpackDir.exists()){
            backpackDir.mkdirs();
        }
        String string = "backpack" + ".yml";
        File file = new File(backpackDir,string);
        if(!file.exists())
        {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return file;
    }

    public File playerfiledircreater(){
        File filerdir = new File(instance.getDataFolder(),"players");
        if(!filerdir.exists()){
            filerdir.mkdirs();
        }
        return filerdir;
    }
}

