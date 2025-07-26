package com.mlc.mlc.Listener;

import com.mlc.mlc.Mlc;
import com.mlc.mlc.mailgui.Mailgui;
import io.papermc.paper.event.player.PlayerDeepSleepEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static com.mlc.mlc.Mlc.instance;
import static com.mlc.mlc.Mlc.wordsnum;

public class Guilistener implements Listener {
    //和睡觉监听写一起

    @EventHandler
    //普通的界面事件监听
    public void onClick(InventoryClickEvent e) throws IOException {
        Player player = (Player) e.getWhoClicked();
        InventoryView inv = player.getOpenInventory();
        if (inv.title().equals(Component.text("mlc", TextColor.fromHexString("#f73636")))) {
            e.setCancelled(true);
            if(e.getRawSlot()<0||e.getRawSlot()>=e.getInventory().getSize())
            {
                return;
            };
            ItemStack itemStack = e.getCurrentItem();
            if(itemStack == null)
            {
                return;
            };
            if(Objects.equals(itemStack.getItemMeta().itemName(), Component.text("exit")))
            {
                player.kick(Component.text("拜拜"));
            }
            else{
                player.give(itemStack);
            };
        };

        //邮箱事件监听
        if(inv.title().equals(Component.text("邮箱", TextColor.fromHexString("#66ee1d"), TextDecoration.BOLD))){
            e.setCancelled(true);
            ItemStack itemStack =e.getCurrentItem();
            if(e.getRawSlot()<0||e.getRawSlot()>=e.getInventory().getSize())
            {
                return;
            };
            if(itemStack==null)
            {
                return;
            }
            else{
                player.give(itemStack);
                int slot = e.getRawSlot();
                String string = player.getUniqueId() + ".yml";
                File mailDir = new File(instance.getDataFolder(), "mail");
                File file = new File(mailDir, string);
                FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(file);
                ConfigurationSection configurationSection = fileConfiguration.getConfigurationSection("item");
                List<String> items = configurationSection.getKeys(false).stream().toList();
                configurationSection.set(items.get(slot),null);
                fileConfiguration.save(file);

                Mailgui.open(player,file);
            }
        }
    }
    @EventHandler
    public void onsleep(PlayerDeepSleepEvent onbed) throws  IOException{
        Player player = onbed.getPlayer();
        World world = onbed.getPlayer().getWorld();

        String path = instance.getDataPath().toString();
        List<String> words = Mlc.readwords(path);

        if(wordsnum == 9601){
            instance.getConfig().set("words", 0);
            instance.saveConfig();
        };

        wordsnum++;
        String line = words.get(wordsnum);
        String cleanedLine1 = line.replace("\t", "  ");

//提示
        Bukkit.broadcast(Component.text("\n" + player.getName() + "上床睡不着，开始背单词了......\n").color(TextColor.fromHexString("#38ff8e")));
        Bukkit.broadcast(Component.text(cleanedLine1+" \n ").color(TextColor.fromHexString("#c3fd26")));
        Bukkit.broadcast(Component.text(player.getName() + "背了一会发现天亮了\n").color(TextColor.fromHexString("#38ff8e")));

        instance.getConfig().set("words", wordsnum);
        instance.saveConfig();
//        Bukkit.broadcast(Component.text(world.getTime()));
        //天气,时间
        if(world.isThundering()&&world.getTime()<12530){
            world.setThundering(false);
            world.setStorm(false);
            world.setWeatherDuration(0);
        } else {
            world.setTime(0);
            if (world.hasStorm()){
                world.setStorm(false);
                world.setWeatherDuration(0);
            }
        }
    }

    @EventHandler
    public void onjion(PlayerJoinEvent join){
        File mailDir = new File(instance.getDataFolder(), "mail");
        String string = join.getPlayer().getUniqueId() + ".yml";
        File file = new File(mailDir,string);
        FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection configurationSection = fileConfiguration.getConfigurationSection("item");
        if (configurationSection != null && !configurationSection.getKeys(false).isEmpty()) {
            join.getPlayer().sendMessage(Component.text("邮箱中有新信件！")
                    .color(TextColor.fromHexString("#7cff4d"))
                    .decoration(TextDecoration.BOLD,true)
            );
        }
    }




}
