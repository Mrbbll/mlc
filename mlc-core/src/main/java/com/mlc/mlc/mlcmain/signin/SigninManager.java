package com.mlc.mlc.mlcmain.signin;

import com.mlc.mlc.mlcmain.hook.economy.Moneyfilemanager;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.time.Duration;

import static com.mlc.mlc.Mlc.instance;
import static com.mlc.mlc.Mlc.miniMessage;

public final class SigninManager {
    private static final long SIGNIN_INTERVAL = Duration.ofDays(1).toMillis();
    private static final NamespacedKey LAST_SIGNIN_TIME = new NamespacedKey(instance, "lastlogintime");
    private static final NamespacedKey SIGNIN_COUNT = new NamespacedKey(instance, "logincount");
    private static final NamespacedKey LAST_REWARD = new NamespacedKey(instance, "lastsigninreward");

    private SigninManager() {
    }

    public static boolean hasSignedInBefore(Player player) {
        return player.getPersistentDataContainer().getOrDefault(LAST_SIGNIN_TIME, PersistentDataType.LONG, 0L) != 0L;
    }

    public static void signIn(Player player, boolean notifyAlreadySignedIn) {
        var data = player.getPersistentDataContainer();
        long lastSigninTime = data.getOrDefault(LAST_SIGNIN_TIME, PersistentDataType.LONG, 0L);
        int signinCount = data.getOrDefault(SIGNIN_COUNT, PersistentDataType.INTEGER, 0);
        long now = System.currentTimeMillis();
        long elapsed = now - lastSigninTime;

        if (lastSigninTime != 0L && elapsed < SIGNIN_INTERVAL) {
            if (notifyAlreadySignedIn) {
                int reward = data.getOrDefault(LAST_REWARD, PersistentDataType.INTEGER, rewardFor(signinCount));
                player.sendActionBar(miniMessage.deserialize("<yellow>已签到，已获得 <gold>" + reward + " <!shadow><white><sprite:items:\"mlc:item/crystal_coin\">"));
            }
            return;
        }

        int nextCount = lastSigninTime != 0L && elapsed < SIGNIN_INTERVAL * 2 ? signinCount + 1 : 1;
        int reward = rewardFor(nextCount);
        var playerId = player.getUniqueId();
        double balance = Moneyfilemanager.getPlayerMoney(playerId);
        if (!Moneyfilemanager.trySetPlayerMoney(playerId, balance + reward)) {
            player.sendMessage(miniMessage.deserialize("<red><b>签到奖励发放失败，请使用 /signin 重试。"));
            return;
        }

        data.set(LAST_SIGNIN_TIME, PersistentDataType.LONG, now);
        data.set(SIGNIN_COUNT, PersistentDataType.INTEGER, nextCount);
        data.set(LAST_REWARD, PersistentDataType.INTEGER, reward);
        player.sendMessage(miniMessage.deserialize("<green>签到成功，连续签到 " + nextCount
                + " 天，获得 <gold>" + reward + " <!shadow><white><sprite:items:\"mlc:item/crystal_coin\">"));
    }

    private static int rewardFor(int signinCount) {
        return signinCount < 7 ? 20 + signinCount * 10 : 100;
    }
}
