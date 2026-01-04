package com.mlc.mlc;

import com.mlc.mlc.backpack.listener.Backpacklistener;
import com.mlc.mlc.hook.economy.MlcEconomy;
import com.mlc.mlc.listener.*;
import com.mlc.mlc.mlcitem.command.*;
import com.mlc.mlc.ess.Listener.Tplistener;
import com.mlc.mlc.ess.command.*;
import com.mlc.mlc.mail.command.mymail;
import com.mlc.mlc.mail.command.sendmail;
import com.mlc.mlc.mail.command.sendmailtoall;
import com.mlc.mlc.recipes.Elytra;
import com.mlc.mlc.recipes.Healfood;
import com.mlc.mlc.recipes.Backpack;
import com.mlc.mlc.sit.Unsitlistener;
import com.mlc.mlc.sit.Command.Sit;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;


public final class Mlc extends JavaPlugin {


    public static JavaPlugin instance;
    public static int wordsnum;
    public static List<Material> crops = new ArrayList<>();
    public static NamespacedKey damagetype;
    public static NamespacedKey armortype;
    public static Map<UUID,UUID> Tpamap = new HashMap<>();
    public static Map<UUID,UUID> Tpaheremap = new HashMap<>();
    public static FileConfiguration backpackfile;
    public static File playerfiledir;
    public static MiniMessage miniMessage;
    private MlcEconomy MlcEconomy;

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("\n\nmlc核心插件加载成功\n\n");
        MlcEconomy = new MlcEconomy(this);
        getServer().getServicesManager().register(Economy.class, MlcEconomy, this, ServicePriority.Normal);
        //目前功能：邮箱，简易物品管理器，跳过睡觉
        this.saveDefaultConfig();
        saveResource("words.txt", false);
        saveResource("items.yml",false);
        instance = this;
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

        new Backpack().backpackrecipe();
        new Healfood().healfoodrecipe();
        new Task();
        new Elytra().elytrarecipe();
        Bukkit.getPluginManager().registerEvents(new Backpacklistener(),this);
        Bukkit.getPluginManager().registerEvents(new Heavestlistener(),this);
        Bukkit.getPluginManager().registerEvents(new Guilistener(), this);
        Bukkit.getPluginManager().registerEvents(new Unsitlistener(), this);
        Bukkit.getPluginManager().registerEvents(new Joinlistener(), this);
        Bukkit.getPluginManager().registerEvents(new Tplistener(), this);
        Bukkit.getPluginManager().registerEvents(new Deadlistener(), this);
        Bukkit.getPluginManager().registerEvents(new Eatlistener(), this);
        Bukkit.getPluginManager().registerEvents(new Enchantlistener(), this);
        Objects.requireNonNull(Bukkit.getPluginCommand("back")).setExecutor((new back()));
        Objects.requireNonNull(Bukkit.getPluginCommand("sendmail")).setExecutor((new sendmail()));
        Objects.requireNonNull(Bukkit.getPluginCommand("mymail")).setExecutor((new mymail()));
        Objects.requireNonNull(Bukkit.getPluginCommand("mlcgui")).setExecutor(new mlcgui());
        Objects.requireNonNull(Bukkit.getPluginCommand("sendmailtoall")).setExecutor(new sendmailtoall());
        Objects.requireNonNull(Bukkit.getPluginCommand("home")).setExecutor(new home());
        Objects.requireNonNull(Bukkit.getPluginCommand("sethome")).setExecutor(new sethome());
        Objects.requireNonNull(Bukkit.getPluginCommand("delhome")).setExecutor(new delhome());
        Objects.requireNonNull(Bukkit.getPluginCommand("tpa")).setExecutor(new tpa());
        Objects.requireNonNull(Bukkit.getPluginCommand("tpaccept")).setExecutor(new tpaccept());
        Objects.requireNonNull(Bukkit.getPluginCommand("tpahere")).setExecutor(new tpahere());
        Objects.requireNonNull(Bukkit.getPluginCommand("sit")).setExecutor((new Sit()));
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

