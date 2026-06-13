package com.mlc.mlc.mlcmain.mail.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
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
import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.mlc.mlc.Mlc.instance;
import static com.mlc.mlc.mlcmain.mail.listener.Maillistener.mailnum;

public class sendmail implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {

        if(strings.length==0)
        {
            commandSender.sendMessage(Component.text("需要提供玩家名字", TextColor.color(0xFF4213)));
            return false;
        }
        //先监测手上是否为空
        Player player = (Player) commandSender;
        ItemStack item = player.getInventory().getItemInMainHand();
        if(item.getType()== Material.AIR)
        {
            commandSender.sendMessage(Component.text("手上物品为空", TextColor.color(0xFF4213)));
            return false;
        }

        try {
            savetomailgui(item,strings[0]);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //删除手上物品
        player.getInventory().getItemInMainHand().setAmount(0);
        player.sendMessage(Component.text(  "成功发送邮件到" + strings[0], TextColor.fromHexString("#7cff4d")));
        Player receiver = Bukkit.getPlayer(strings[0]);
        if (receiver != null) {
            receiver.sendMessage(Component.text(player.getName() + "向你发送了一封邮件")
                    .decoration(TextDecoration.BOLD,true)
                    .color(TextColor.fromHexString("#7cff4d"))
            );
        }
        return true;
    }


    //保存到文件
    public  void savetomailgui(ItemStack item,String player) throws IOException {
        File mailDir = new File(instance.getDataFolder(), "mail");
        OfflinePlayer player1 = Bukkit.getOfflinePlayer(player);
        // 初始化目录
        if (!mailDir.exists()) mailDir.mkdirs();

        //初始化文件
        String string = player1.getUniqueId() +".yml";
        File file = new File(mailDir,string);
        if(!file.exists())
        {
            file.createNewFile();
        }

        //初始化item
        FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(file);
        if (!fileConfiguration.contains("item")) {
            fileConfiguration.set("item", null);
            fileConfiguration.save(file);
        }

        //序列化itemmeta
        Map<String, Object> serialized = item.serialize();

        //获取物品存储id
        String itemid = getitemid();

        //存储物品

        saveitem(itemid,serialized,file);
        mailnum.put(player1.getUniqueId(),mailnum.getOrDefault(player1.getUniqueId(),0)+1);


    }

    public String getitemid()
    {
        long timeMillis = System.currentTimeMillis();
        return String.valueOf(timeMillis);
    }

    public void saveitem(String itemid, Map<String, Object> serialized , File file) throws IOException {
        FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(file);

        fileConfiguration.set("item."+itemid,serialized);
        fileConfiguration.save(file);
    }
}
