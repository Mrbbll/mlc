package com.mlc.mlc;

import io.papermc.paper.event.player.PlayerDeepSleepEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.util.HSVLike;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static com.mlc.mlc.Mlc.instance;
import static com.mlc.mlc.Mlc.wordsnum;

public class guilistener implements Listener {
    //和睡觉监听写一起

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        InventoryView inv = player.getOpenInventory();
        if (inv.title().equals(Component.text("mlc"))) {
            e.setCancelled(true);
            if(e.getRawSlot()<0||e.getRawSlot()>e.getInventory().getSize())
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
//            if(Objects.equals(itemStack.getItemMeta().itemName(), Component.text("活动帽子1"))){
//                player.give(itemStack);


        };

        return;

    }

    @EventHandler
//    public void onsleep(PlayerBedEnterEvent onbed) throws IOException {
public void onsleep(PlayerDeepSleepEvent onbed) throws  IOException{
        Player player = onbed.getPlayer();
        World world = onbed.getPlayer().getWorld();


////        if(world.getTime()%24000<12541)
////        {
////            return;
////        };
//        world.getTime();

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

        //时间
        world.setTime(0);

        //天气
        if(world.isThundering()&&!world.hasStorm()){
            world.setThundering(false);
            world.setStorm(false);
            world.setWeatherDuration(0);
        };
    }




}
