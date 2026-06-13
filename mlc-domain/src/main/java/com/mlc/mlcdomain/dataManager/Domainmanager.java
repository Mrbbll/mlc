package com.mlc.mlcdomain.dataManager;

import com.mlc.mlcdomain.hocks.Vaultapi;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

import static com.mlc.mlcdomain.Mlcdomain.instance;

public class Domainmanager {
    //小于4个不扣钱

    public static boolean creatdomaincostmoney(OfflinePlayer offlinePlayer){
        Player player = (Player) offlinePlayer;
        PlayerData playerData = Databasemanager.getPlayer(offlinePlayer.getUniqueId());
        double playermoney = Vaultapi.getBalance(offlinePlayer);
        if (playerData!=null) {
            if(playerData.getChunkCount()<4){
                return true;
            }else {
                double costmoney = playerData.getChunkCount()*10;
                if(playermoney<costmoney){
                    player.sendMessage(Component.text("你需要"+costmoney+"元，余额不足")
                            .color(TextColor.fromHexString("#FF0000")));
                    return false;
                }else {
                    player.sendMessage(Component.text("你支付了"+costmoney+"元")
                            .color(TextColor.fromHexString("#00FF00")));
                    Vaultapi.withdrawPlayer(offlinePlayer,costmoney);
                }
            }
            return true;
        }else {
            instance.getLogger().warning("玩家" + player.getName() + "创建领地失败，Databasemanager.getPlayer(offlinePlayer.getUniqueId())中没有玩家数据");
        }
        return false;
    };
    public static boolean deldomaingiveback(OfflinePlayer offlinePlayer){
        Player player = (Player) offlinePlayer;
        PlayerData playerData = Databasemanager.getPlayer(offlinePlayer.getUniqueId());
        if(playerData.getChunkCount()<4){
            player.sendMessage(Component.text("你的领地区块数量不足4个，无返还费用")
                    .color(TextColor.fromHexString("#FF0000")));
            return true;
        }else {
            double giveBackMoney = playerData.getChunkCount();
            player.sendMessage(Component.text("你返还了"+giveBackMoney+"元")
                    .color(TextColor.fromHexString("#00FF00")));
            Vaultapi.depositPlayer(offlinePlayer,giveBackMoney);
        }
        return true;
    }
}
