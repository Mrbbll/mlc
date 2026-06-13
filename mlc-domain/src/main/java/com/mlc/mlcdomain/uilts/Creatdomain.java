package com.mlc.mlcdomain.uilts;

import com.mlc.mlcdomain.dataManager.Databasemanager;
import com.mlc.mlcdomain.dataManager.Domainmanager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;

import static com.mlc.mlcdomain.Mlcdomain.instance;

public class Creatdomain {
    public static void createDomain(Player player, String domainName) {
        Databasemanager.createOrUpdatePlayer(player.getUniqueId(), player.getName());
        if(!Domainmanager.creatdomaincostmoney(player)){
            instance.getLogger().warning("玩家" + player.getName() + "创建领地失败，余额不足");

            return;
        }
        if(Databasemanager.createDomain(domainName, player.getUniqueId(), player.getName(), player.getWorld().getName(), player.getLocation().getChunk().getX(), player.getLocation().getChunk().getZ(), 1)){
            instance.getLogger().info("玩家" + player.getName() + "创建了领地：" + domainName);
            player.sendMessage(Component.text("认领了区块：").color(TextColor.fromHexString("#00ff00"))
                    .append(Component.text(domainName).color(TextColor.fromHexString("#ffb000"))));
        }else{
            player.sendMessage(Component.text("创建失败").color(TextColor.fromHexString("#ee1d1d")));
        }
    }


}
