package com.mlc.mlc;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.mlc.mlc.Mlc.instance;
import static org.bukkit.inventory.ItemStack.serializeItemsAsBytes;

public class sendmail implements CommandExecutor {
//保存到文件
    public  void savetomailgui(ItemStack item,String player) throws IOException {
        File mailDir = new File(instance.getDataFolder(), "mail");

        if (!mailDir.exists()) mailDir.mkdirs(); // 创建目录
//
//        Player player1 = Bukkit.getPlayer(player);
        String string = player+".yml";
        File file = new File(mailDir,string);
        if(!file.exists())
        {
            file.createNewFile();
        };

        FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(file);
        if (!fileConfiguration.contains("item")) {
            fileConfiguration.set("item", new ArrayList<>());
            fileConfiguration.save(file);
        }


        ItemMeta itemMeta = item.getItemMeta();

        Map<String, Object> serialized = itemMeta.serialize();
        String type = item.getType().toString();
        int amount = item.getAmount();


        @SuppressWarnings("unchecked")
        List<Map<String, Object>> itemList = (List<Map<String, Object>>) fileConfiguration.get("item");

        Map<String, Object> newItem = new HashMap<>();
        newItem.put("type",type);
        newItem.put("amount",amount);
        newItem.put("itemmeta",serialized);

        itemList.add(newItem);
        fileConfiguration.set("item",itemList);
        fileConfiguration.save(file);
    }


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

//        Player receiver = Bukkit.getPlayer(s);
//        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(strings[0]);
        try {
            savetomailgui(item,strings[0]);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        return true;
    }


}
