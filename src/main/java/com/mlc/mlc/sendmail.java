package com.mlc.mlc;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

import static com.mlc.mlc.Mlc.instance;
import static org.bukkit.inventory.ItemStack.serializeItemsAsBytes;

public class sendmail implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {

        if(strings.length==0)
        {
            commandSender.sendMessage("需要提供玩家名字");
            return false;
        }
        //先监测手上是否为空
        Player player = (Player) commandSender;
        ItemStack item = player.getInventory().getItemInMainHand();
        if(item.getType()== Material.AIR)
        {
            commandSender.sendMessage("手上物品为空");
            return false;
        };

        try {
            savetomailgui(item,strings[0]);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        return true;
    }


    //保存到文件
    public  void savetomailgui(ItemStack item,String player) throws IOException {
        File mailDir = new File(instance.getDataFolder(), "mail");

        // 初始化目录
        if (!mailDir.exists()) mailDir.mkdirs();

        //初始化文件
        String string = player+".yml";
        File file = new File(mailDir,string);
        if(!file.exists())
        {
            file.createNewFile();
        };

        //初始化item
        FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(file);
        if (!fileConfiguration.contains("item")) {
            fileConfiguration.set("item", null);
            fileConfiguration.save(file);
        }

        //获取物品信息
        ItemMeta itemMeta = item.getItemMeta();
        //序列化itemmeta以及其他信息

        Map<String, Object> serialized = item.serialize();
        String type = item.getType().toString();
        int amount = item.getAmount();

        //获取物品存储id
        String itemid = getitemid();

        //存储物品
        saveitem(itemid,serialized,type,amount,file);

    }


    public String getitemid()
    {
        long timeMillis = System.currentTimeMillis();
        Bukkit.broadcast(Component.text(timeMillis));
        return String.valueOf(timeMillis);
    };

    public void saveitem(String itemid, Map<String, Object> serialized , String type, Integer amount, File file) throws IOException {
        FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(file);

        fileConfiguration.set("item."+itemid+".type",type);
        fileConfiguration.set("item."+itemid+".amount",amount);
        fileConfiguration.set("item."+itemid+".itemmeta",serialized);
        fileConfiguration.save(file);
    }
}
