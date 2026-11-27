package com.mlc.mlc.totalassault;

import com.mlc.mlc.totalassault.data.Datamanager;
import com.mlc.mlc.totalassault.data.Filereader;
import com.mlc.mlc.totalassault.data.TotalAssaultStorage;
import com.mlc.mlc.totalassault.menu.tamenu;
import com.mlc.mlc.totalassault.world.Worldmanager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.IOException;
import java.sql.SQLException;
import java.time.Instant;
import java.util.logging.Level;

public final class Totalassault {
    private static BukkitTask expirationTask;

    private Totalassault() {}

    public static void init(JavaPlugin plugin) throws IOException, SQLException {
        if (expirationTask != null) throw new IllegalStateException("总力战已初始化");
        Filereader.ActivityConfig activity = Datamanager.readActivityConfig(plugin);
        Datamanager.init(Datamanager.databasePath(plugin));
        try {
            Datamanager.registerActivity(activity.id(), activity.name(), activity.bossId(), activity.start(), activity.difficulties());
            Datamanager.recoverAfterRestart(Instant.now());
            cleanupWorldsAfterRestart(plugin);
            tamenu.init(plugin, activity);
            // Paper 插件需要通过接口注册命令，不能从插件描述文件中获取命令。
            plugin.registerCommand("ta", "打开总力战菜单", (source, args) -> {
                if (!(source.getSender() instanceof Player player)) {
                    source.getSender().sendMessage("只有玩家可以打开总力战菜单。");
                    return;
                }
                tamenu.open(player);
            });
            expirationTask = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
                try {
                    Datamanager.expireChallenges(Instant.now());
                } catch (SQLException e) {
                    plugin.getLogger().log(java.util.logging.Level.SEVERE, "检查总力战过期状态失败", e);
                }
            }, 20L, 20L);
        } catch (SQLException | RuntimeException e) {
            tamenu.shutdown();
            try { Datamanager.shutdown(); } catch (SQLException close) { e.addSuppressed(close); }
            throw e;
        }
    }

    /** 重启时清理已结束的挑战和创建中断的临时世界。 */
    private static void cleanupWorldsAfterRestart(JavaPlugin plugin) throws SQLException {
        for (TotalAssaultStorage.Challenge challenge : Datamanager.challengesNeedingRecovery()) {
            if (!challenge.status().equals("SUCCESS") && !challenge.status().equals("FAILED")) {
                continue;
            }
            try {
                Worldmanager.deleteWorld(challenge.worldName());
                Datamanager.setWorldState(challenge.id(), "DELETED");
            } catch (IOException | RuntimeException exception) {
                // 保留待清理状态，下一次启动时可以继续处理。
                plugin.getLogger().log(Level.SEVERE, "删除挑战世界失败：" + challenge.worldName(), exception);
            }
        }
        for (TotalAssaultStorage.WorldPreparation preparation : Datamanager.preparations()) {
            try {
                Datamanager.setPreparationState(preparation.player(), "CLEANING");
                Worldmanager.deleteWorld(preparation.worldName());
                Datamanager.removePreparation(preparation.player());
            } catch (IOException | RuntimeException exception) {
                plugin.getLogger().log(Level.SEVERE, "删除准备世界失败：" + preparation.worldName(), exception);
            }
        }
    }

    public static boolean isInChallenge(Player player) throws SQLException {
        return Datamanager.findActiveChallenge(player.getUniqueId()).isPresent();
    }

    public static boolean isBattling(Player player) throws SQLException {
        return Datamanager.findActiveChallenge(player.getUniqueId())
                .map(challenge -> challenge.status().equals("BATTLING")).orElse(false);
    }

    public static void shutdown() throws SQLException {
        if (expirationTask != null) {
            expirationTask.cancel();
            expirationTask = null;
        }
        tamenu.shutdown();
        Datamanager.shutdown();
    }
}
