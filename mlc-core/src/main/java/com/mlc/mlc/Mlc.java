package com.mlc.mlc;

import com.mlc.mlc.mlcmain.hook.economy.MlcEconomy;
import com.mlc.mlc.mlcmain.hook.economy.Moneyfilemanager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
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
    public static NamespacedKey raritytier;
    public static Map<UUID,UUID> Tpamap = new HashMap<>();
    public static Map<UUID,UUID> Tpaheremap = new HashMap<>();
    public static File playerfiledir;
    public static MiniMessage miniMessage;



    @Override
    public void onEnable() {


        //注册经济系统
        MlcEconomy mlcEconomy = new MlcEconomy(this);
        getServer().getServicesManager().register(Economy.class, mlcEconomy, this, ServicePriority.Normal);

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

        //初试化文件
        this.saveDefaultConfig();
        saveResource("words.txt", false);
        saveResource("items/fesitems.yml",false);
        saveResource("items/cratesitems.yml",false);


        //初始化静态变量
        instance = this;
        fileConfiguration = instance.getConfig();
        wordsnum = this.getConfig().getInt("words");
        damagetype = new NamespacedKey(this,"damagetype");
        armortype = new NamespacedKey(this,"armortype");
        raritytier = new NamespacedKey(this,"raritytier");



        //minimessage初始化
        miniMessage = MiniMessage.miniMessage();

        //加右键收获作物列表
        crops.add(Material.CARROTS);
        crops.add(Material.POTATOES);
        crops.add(Material.BEETROOTS);
        crops.add(Material.WHEAT);

        //初始化player文件夹，因为玩家文件分开保持，只初始化文件夹
        playerfiledir = playerfiledircreater();
        //初始化货币数据库文件
        try {
            new Moneyfilemanager();
            getLogger().info("\n\n货币数据库建立成功\n\n");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        //指令配方注入等
        try {
            Task.task();
        } catch (URISyntaxException | NoSuchAlgorithmException | IOException e) {
            throw new RuntimeException(e);
        }


        // Initialize domain (领地) system
        com.mlc.mlcdomain.Mlcdomain.init(this);

        // Initialize waystone (传送石碑) system
        com.mlc.mlcwaystone.Mlcwaystone.init(this);

        // Initialize mlc-styte system
        com.mlc.mlcstyte.MlcStyte.init(this);

        getLogger().info("\n\nmlc核心插件加载成功\n\n");
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

    public File playerfiledircreater(){
        File filerdir = new File(instance.getDataFolder(),"players");
        if(!filerdir.exists()){
            filerdir.mkdirs();
        }
        return filerdir;
    }
}

