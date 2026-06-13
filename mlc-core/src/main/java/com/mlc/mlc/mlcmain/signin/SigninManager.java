package com.mlc.mlc.mlcmain.signin;

import com.mlc.mlc.mlcmain.hook.economy.Moneyfilemanager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import static com.mlc.mlc.Mlc.instance;
import static com.mlc.mlc.Mlc.miniMessage;

public class SigninManager {
    public static void reward(Player player, int loginCount ){
        int rewardMoney = 0;
        if(loginCount<7) {
            rewardMoney = 20 + loginCount * 10;
        }else {
            rewardMoney = 100;
        }
        int finalRewardMoney = rewardMoney;
        //回主线程
        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.runTask(instance, ()->{
            Moneyfilemanager.setPlayerMoney(
                    player.getUniqueId(),
                    Moneyfilemanager.getPlayerMoney(player.getUniqueId()) + finalRewardMoney);
        });
        player.sendMessage(miniMessage.deserialize("<green>连续登录"+ loginCount + "天" + "\n+"+ finalRewardMoney));
    }
}
