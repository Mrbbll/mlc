package com.mlc.mlc.mlcmain.signin.Listener;

import com.mlc.mlc.mlcmain.signin.SigninManager;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitScheduler;

import static com.mlc.mlc.Mlc.instance;
import static com.mlc.mlc.Mlc.miniMessage;

public class JoinMoneyListener implements Listener{
    public static final int hour_24  = 24 * 60 * 60 * 1000;
    public static final int hour_48 = 48 * 60 * 60 * 1000;
    public static final NamespacedKey LASTLOGINTIME = new NamespacedKey(instance, "lastlogintime");
    public static final NamespacedKey LOGINCOUNT = new NamespacedKey(instance, "logincount");

    @EventHandler
    public void Onplayerjoin(PlayerJoinEvent event){
        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.runTaskAsynchronously(instance,()->{
            Player player = event.getPlayer();
            PersistentDataContainer pdc = player.getPersistentDataContainer();
            long lastlogintime = pdc.getOrDefault(LASTLOGINTIME, PersistentDataType.LONG,0L);
            int logincount = pdc.getOrDefault(LOGINCOUNT, PersistentDataType.INTEGER,0);

            //如果玩家上次登录时间为0L,则说明是第一次登录
            if(lastlogintime==0L){
                player.sendMessage(miniMessage.deserialize("<green>欢迎来到MLC服务器"));
                pdc.set(LASTLOGINTIME,PersistentDataType.LONG,System.currentTimeMillis());
                pdc.set(LOGINCOUNT,PersistentDataType.INTEGER,1);
                SigninManager.reward(player,1);
            }else {
                //如果玩家上次登录时间距离当前时间大于24小时且小于48小时

                if(System.currentTimeMillis()-lastlogintime>= hour_24
                        &&System.currentTimeMillis()-lastlogintime< hour_48){
                //根据连续登录数奖励
                    player.sendMessage(miniMessage.deserialize("<green>欢迎回来"));
                    SigninManager.reward(player,logincount + 1);
                    pdc.set(LASTLOGINTIME,PersistentDataType.LONG,System.currentTimeMillis());
                    pdc.set(LOGINCOUNT,PersistentDataType.INTEGER,logincount + 1);
                }else if(System.currentTimeMillis()-lastlogintime>= hour_48) {
                    //如果玩家上次登录时间距离当前时间大于48小时
                    player.sendMessage(miniMessage.deserialize("<green>欢迎回来"));
                    pdc.set(LASTLOGINTIME,PersistentDataType.LONG,System.currentTimeMillis());
                    pdc.set(LOGINCOUNT,PersistentDataType.INTEGER,1);
                    SigninManager.reward(player,1);
                }
                else {
                    //如果玩家上次登录时间距离当前时间小于24小时
                    player.sendMessage(miniMessage.deserialize("<green>欢迎回来"));
                }
            }

        });
    }
}