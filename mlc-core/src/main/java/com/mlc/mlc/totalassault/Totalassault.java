package com.mlc.mlc.totalassault;

import com.mlc.mlc.totalassault.data.Datamanager;
import com.mlc.mlc.totalassault.data.Filereader;
import com.mlc.mlc.totalassault.data.TotalAssaultStorage;
import com.mlc.mlc.totalassault.menu.tamenu;
import com.mlc.mlc.totalassault.world.Worldmanager;
import com.mlc.mlc.totalassault.battle.BattleManager;
import com.mlc.mlc.totalassault.boss.BossRegistry;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.IOException;
import java.sql.SQLException;
import java.time.Instant;
import java.util.UUID;
import java.util.logging.Level;

public final class Totalassault {
    private static BukkitTask expirationTask;
    private static ChallengeCreator challengeCreator;
    private static BattleManager battleManager;

    private Totalassault() {}

    public static void init(JavaPlugin plugin) throws IOException, SQLException {
        if (expirationTask != null) throw new IllegalStateException("总力战已初始化");
        Filereader.ActivityConfig activity = Datamanager.readActivityConfig(plugin);
        Datamanager.init(Datamanager.databasePath(plugin));
        try {
            Datamanager.registerActivity(activity.id(), activity.name(), activity.bossId(), activity.start(), activity.difficulties());
            Datamanager.recoverAfterRestart(Instant.now());
            cleanupWorldsAfterRestart(plugin);
            BossRegistry bosses = new BossRegistry();
            if (bosses.find(activity.bossId()).isEmpty()) {
                plugin.getLogger().warning("活动 Boss 未注册或尚未实现，无法创建挑战：" + activity.bossId());
            }
            challengeCreator = new ChallengeCreator(plugin, activity, bosses);
            battleManager = new BattleManager(plugin, activity, bosses);
            tamenu.init(plugin, activity, challengeCreator, battleManager);
            // Paper 插件需要通过接口注册命令，不能从插件描述文件中获取命令。
            plugin.registerCommand("ta", "打开总力战菜单", (source, args) -> {
                if (args.length > 0) {
                    giveTickets(plugin, source.getSender(), activity, args);
                    return;
                }
                if (!(source.getSender() instanceof Player player)) {
                    source.getSender().sendMessage(TaText.line("<#b8ffde>只有玩家可以打开总力战菜单。"));
                    return;
                }
                tamenu.open(player);
            });
            expirationTask = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
                try {
                    battleManager.tick();
                    Datamanager.expireChallenges(Instant.now());
                    cleanupFinishedWorlds(plugin);
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

    /** 管理员向指定玩家增加当前活动、当前刷新日的挑战票。 */
    private static void giveTickets(JavaPlugin plugin, CommandSender sender,
                                    Filereader.ActivityConfig activity, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage(TaText.line("<#ffb4a8>只有管理员可以使用总力战赠票命令。"));
            return;
        }
        if (args.length != 3 || !args[0].equalsIgnoreCase("give")) {
            sender.sendMessage(TaText.line("<#ffd166>用法：/ta give <玩家名或UUID> <数量>"));
            return;
        }
        int amount;
        try {
            amount = Integer.parseInt(args[2]);
            if (amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException exception) {
            sender.sendMessage(TaText.line("<#ffb4a8>赠票数量必须是大于零的整数。"));
            return;
        }

        UUID playerId;
        try {
            playerId = UUID.fromString(args[1]);
        } catch (IllegalArgumentException exception) {
            // 只查找服务器缓存的玩家，不触发在线玩家资料查询。
            OfflinePlayer target = Bukkit.getOfflinePlayerIfCached(args[1]);
            if (target == null) {
                sender.sendMessage(TaText.line("<#ffb4a8>找不到该玩家，请使用已登录过的玩家名或 UUID。"));
                return;
            }
            playerId = target.getUniqueId();
        }
        Instant now = Instant.now();
        if (now.isBefore(activity.start()) || !now.isBefore(activity.start().plusSeconds(7L * 24 * 60 * 60))) {
            sender.sendMessage(TaText.line("<#ffb4a8>当前活动未开放，无法发放挑战票。"));
            return;
        }
        try {
            int total = Datamanager.giveTickets(playerId, activity.id(), amount, now);
            sender.sendMessage(TaText.line("<#b8ffde>已向 " + TaText.escape(args[1]) + " 发放 " + amount
                    + " 张挑战票，今日剩余 " + total + " 张。"));
        } catch (SQLException exception) {
            plugin.getLogger().log(Level.SEVERE, "发放总力战挑战票失败", exception);
            sender.sendMessage(TaText.line("<#ffb4a8>发放失败，请检查活动状态、票数上限及服务器日志。"));
        }
    }

    /** 重启时清理已结束的挑战和创建中断的临时世界。 */
    private static void cleanupWorldsAfterRestart(JavaPlugin plugin) throws SQLException {
        cleanupFinishedWorlds(plugin);
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

    /** 清理已经结束且仍等待删除的挑战世界。 */
    private static void cleanupFinishedWorlds(JavaPlugin plugin) throws SQLException {
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
    }

    public static boolean isInChallenge(Player player) throws SQLException {
        return Datamanager.findActiveChallenge(player.getUniqueId()).isPresent();
    }

    public static boolean isBattling(Player player) throws SQLException {
        return Datamanager.findActiveChallenge(player.getUniqueId())
                .map(challenge -> challenge.status().equals("BATTLING")).orElse(false);
    }

    public static void shutdown() throws SQLException {
        if (battleManager != null) {
            battleManager.shutdown();
            battleManager = null;
        }
        if (challengeCreator != null) {
            challengeCreator.shutdown();
            challengeCreator = null;
        }
        if (expirationTask != null) {
            expirationTask.cancel();
            expirationTask = null;
        }
        tamenu.shutdown();
        Datamanager.shutdown();
    }
}
