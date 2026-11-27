package com.mlc.mlc.totalassault;

import com.mlc.mlc.totalassault.data.Datamanager;
import com.mlc.mlc.totalassault.data.Filereader;
import com.mlc.mlc.totalassault.data.difficulties;
import com.mlc.mlc.totalassault.boss.testboss.TestBossDifficulty;
import com.mlc.mlc.totalassault.menu.tamenu;
import com.mlc.mlc.totalassault.world.Worldmanager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.sql.SQLException;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

/** 负责准备独立世界，并在准备完成后创建挑战。 */
public final class ChallengeCreator implements Listener {
    private final JavaPlugin plugin;
    private final Filereader.ActivityConfig activity;
    // 所有访问都在主线程进行，防止玩家重复点击创建多个世界。
    private final Set<UUID> preparing = new HashSet<>();
    private final Set<UUID> disconnected = new HashSet<>();
    private boolean closed;

    public ChallengeCreator(JavaPlugin plugin, Filereader.ActivityConfig activity) {
        this.plugin = plugin;
        this.activity = activity;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /** 准备期间断线后，即使重新连接也取消这次准备。 */
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        if (preparing.contains(playerId)) disconnected.add(playerId);
    }

    /** 检查资格并开始准备世界；扣票只在世界准备成功后进行。 */
    public void start(Player player, String difficultyId) {
        UUID playerId = player.getUniqueId();
        if (closed || !plugin.isEnabled()) return;
        if (!preparing.add(playerId)) {
            player.sendMessage(TaText.line("<#b8ffde>挑战世界正在准备，请稍候。"));
            return;
        }

        UUID challengeId = UUID.randomUUID();
        String worldName = Worldmanager.worldName(challengeId);
        boolean registered = false;
        try {
            Instant now = Instant.now();
            int order = activity.difficulties().indexOf(difficultyId);
            if (now.isBefore(activity.start()) || !now.isBefore(activity.start().plusSeconds(7L * 24 * 60 * 60))) {
                player.sendMessage(TaText.line("<#b8ffde>当前没有开放的总力战活动。"));
            } else if (order < 0 || difficulties.fromId(difficultyId).isEmpty()) {
                player.sendMessage(TaText.line("<#b8ffde>该难度不存在。"));
            } else if (Datamanager.findActiveChallenge(playerId).isPresent()) {
                player.sendMessage(TaText.line("<#b8ffde>请先结束当前挑战。"));
            } else if (Datamanager.preparations().stream().anyMatch(p -> p.player().equals(playerId))) {
                player.sendMessage(TaText.line("<#b8ffde>上一次挑战世界尚未清理，请联系管理员。"));
            } else if (order > Datamanager.highestClearedDifficulty(playerId, activity.id()) + 1) {
                player.sendMessage(TaText.line("<#b8ffde>请先通关上一难度。"));
            } else if (Datamanager.remainingTickets(playerId, activity.id(), now) <= 0) {
                player.sendMessage(TaText.line("<#b8ffde>今天的挑战票已经用完。"));
            } else {
                Filereader.ArenaConfig arena = Filereader.ArenaConfig.load(plugin);
                Datamanager.prepareWorld(playerId, worldName, now);
                registered = true;
                World world = Worldmanager.createWorld(challengeId);
                player.sendMessage(TaText.line("<#b8ffde>正在准备挑战世界，请稍候；准备完成后才会消耗挑战票。"));
                Worldmanager.pasteArenaAsync(plugin, world, arena,
                        () -> finish(playerId, challengeId, difficultyId, worldName),
                        error -> fail(playerId, worldName, "场地加载失败，没有消耗挑战票。", error));
                return;
            }
        } catch (SQLException | RuntimeException exception) {
            if (registered) {
                fail(playerId, worldName, "挑战世界准备失败，没有消耗挑战票。", exception);
                return;
            }
            plugin.getLogger().log(Level.SEVERE, "准备总力战挑战失败", exception);
            player.sendMessage(TaText.line("<#b8ffde>无法准备挑战，请稍后再试。"));
        }
        preparing.remove(playerId);
    }

    /** 粘贴完成后由数据库事务再次检查活动、难度、票数和挑战状态。 */
    private void finish(UUID playerId, UUID challengeId, String difficultyId, String worldName) {
        Player player = Bukkit.getPlayer(playerId);
        if (closed || !plugin.isEnabled()) return;
        if (disconnected.contains(playerId) || player == null || !player.isOnline()) {
            fail(playerId, worldName, "玩家已经离线，取消挑战准备。", null);
            return;
        }
        try {
            Datamanager.startChallenge(challengeId.toString(), playerId, activity.id(), difficultyId,
                    worldName, TestBossDifficulty.of(difficultyId).health(), Instant.now());
        } catch (SQLException | RuntimeException exception) {
            fail(playerId, worldName, "挑战创建失败，没有消耗挑战票。", exception);
            return;
        }
        preparing.remove(playerId);
        player.sendMessage(TaText.line("<#b8ffde>挑战创建成功，已消耗一张票；一小时倒计时已开始。"));
        tamenu.open(player);
    }

    /** 临时世界删除失败时保留数据库记录，等待服务器重启后继续清理。 */
    private void fail(UUID playerId, String worldName, String message, Exception error) {
        if (error != null) plugin.getLogger().log(Level.SEVERE, message, error);
        try {
            Datamanager.setPreparationState(playerId, "CLEANING");
            Worldmanager.deleteWorld(worldName);
            Datamanager.removePreparation(playerId);
        } catch (SQLException | IOException | RuntimeException cleanupError) {
            plugin.getLogger().log(Level.SEVERE, "清理挑战准备世界失败：" + worldName, cleanupError);
        } finally {
            preparing.remove(playerId);
            disconnected.remove(playerId);
        }
        Player player = Bukkit.getPlayer(playerId);
        if (player != null && player.isOnline()) player.sendMessage(TaText.line("<#ffb4a8>" + TaText.escape(message)));
    }

    /** 插件关闭后不再提交挑战，未完成的准备记录由下次启动处理。 */
    public void shutdown() {
        closed = true;
        preparing.clear();
        disconnected.clear();
    }
}
