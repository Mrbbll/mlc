package com.mlc.mlc.totalassault;

import com.mlc.mlc.totalassault.data.Datamanager;
import com.mlc.mlc.totalassault.data.Filereader;
import com.mlc.mlc.totalassault.commands.command;
import com.mlc.mlc.totalassault.menu.tamenu;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.IOException;
import java.sql.SQLException;
import java.time.Instant;

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
            tamenu.init(plugin, activity);
            java.util.Objects.requireNonNull(plugin.getCommand("ta"), "plugin.yml 中缺少 ta 命令")
                    .setExecutor(new command());
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
