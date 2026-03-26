package com.mlc.mlc.mlcmain.chat.listener;

import com.mlc.mlc.mlcmain.chat.Chatmanager;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentIteratorFlag;
import net.kyori.adventure.text.ComponentIteratorType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Set;

public class Chatlistener implements Listener {
    @EventHandler
    public void onchat(AsyncChatEvent event){
        Player player = event.getPlayer();
        String messageStr = event.message().toString();
        //判断是不是有[item]在消息里
        if(messageStr.contains("[item]")){
            //替换[item]为物品图标，并复制物品lore等信息
            Component itemname = player.getInventory().getItemInMainHand().getItemMeta().displayName();
            if (itemname != null) {
                String replaced = messageStr.replace("[item]", itemname.toString());
                event.message(Component.text(replaced));

        }
    }

    }
}
