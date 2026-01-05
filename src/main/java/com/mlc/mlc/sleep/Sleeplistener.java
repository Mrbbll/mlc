package com.mlc.mlc.sleep;

import com.mlc.mlc.Mlc;
import io.papermc.paper.event.player.PlayerDeepSleepEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;

import static com.mlc.mlc.Mlc.instance;
import static com.mlc.mlc.Mlc.wordsnum;

public class Sleeplistener implements Listener {

    @EventHandler
    public void onsleep(PlayerDeepSleepEvent onbed) throws IOException {
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
        //睡觉回血
        @Nullable AttributeInstance maxHealth = player.getAttribute(Attribute.MAX_HEALTH);
        assert maxHealth != null;
        double healthnum = maxHealth.getBaseValue();
        if(healthnum<=18){
            maxHealth.setBaseValue(healthnum + 2);
        }
        else {
            maxHealth.setBaseValue(20);
        }
    }
}
