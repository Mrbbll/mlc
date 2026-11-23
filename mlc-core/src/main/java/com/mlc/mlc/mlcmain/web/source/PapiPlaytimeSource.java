package com.mlc.mlc.mlcmain.web.source;

import com.mlc.mlc.mlcmain.web.LeaderboardSnapshot;
import com.mlc.mlcbot.BotIdentity;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

/**
 * 定期读取 PAPI 的累计时长。PAPI 和 Bukkit 调用都留在游戏线程，离线玩家分批读取。
 * 排行榜排序在异步线程执行，HTTP 线程通过 latest() 读取最近一次完整采集的不可变快照。
 */
public final class PapiPlaytimeSource implements AutoCloseable {
    private static final String EXPANSION = "statistic";
    private static final String PLACEHOLDER = "%statistic_seconds_played%";
    private static final int PLAYERS_PER_TICK = 8;
    private static final long REFRESH_TICKS = 20 * 60;

    private final JavaPlugin plugin;
    private volatile LeaderboardSnapshot<LeaderboardSnapshot.PlayerTime> latest;
    private BukkitTask refreshTask;
    private BukkitTask batchTask;
    private String previousWarning;
    private volatile boolean closed;
    private long generation;

    public PapiPlaytimeSource(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void start() {
        refreshTask = Bukkit.getScheduler().runTaskTimer(plugin, this::startRefresh, 1, REFRESH_TICKS);
    }

    public LeaderboardSnapshot<LeaderboardSnapshot.PlayerTime> latest() {
        return latest;
    }

    private void startRefresh() {
        if (batchTask != null) {
            return;
        }
        long refreshGeneration = ++generation;
        try {
            if (!PlaceholderAPI.isRegistered(EXPANSION)) {
                unavailable("在线时长排行榜需要启用 PlaceholderAPI 的 Statistic 扩展。", null);
                return;
            }
            Map<UUID, OfflinePlayer> players = new LinkedHashMap<>();
            for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
                players.put(player.getUniqueId(), player);
            }
            for (Player player : Bukkit.getOnlinePlayers()) {
                players.put(player.getUniqueId(), player);
            }
            batchTask = new Refresh(new ArrayList<>(players.values()), refreshGeneration).runTaskTimer(plugin, 1, 1);
        } catch (RuntimeException exception) {
            unavailable("准备在线时长排行榜失败。", exception);
        }
    }

    private final class Refresh extends BukkitRunnable {
        private final List<OfflinePlayer> players;
        private final List<LeaderboardSnapshot.PlayerTime> entries = new ArrayList<>();
        private int cursor;
        private int invalidValues;
        private final long refreshGeneration;

        private Refresh(List<OfflinePlayer> players, long refreshGeneration) {
            this.players = players;
            this.refreshGeneration = refreshGeneration;
        }

        @Override
        public void run() {
            try {
                int end = Math.min(cursor + PLAYERS_PER_TICK, players.size());
                while (cursor < end) {
                    collect(players.get(cursor++));
                }
                if (cursor == players.size()) {
                    if (!PlaceholderAPI.isRegistered(EXPANSION) || (entries.isEmpty() && invalidValues > 0)) {
                        unavailable("PlaceholderAPI 在线时长变量未返回可用数据。", null);
                    } else {
                        // 此后不再修改 entries，由工作线程独占并生成不可变快照。
                        publishAsync(entries, refreshGeneration);
                        if (invalidValues == 0) {
                            previousWarning = null;
                        } else {
                            warn("部分玩家的 " + PLACEHOLDER + " 返回值无效，已跳过。", null);
                        }
                    }
                    finish();
                }
            } catch (RuntimeException exception) {
                unavailable("采集在线时长排行榜失败。", exception);
                finish();
            }
        }

        private void collect(OfflinePlayer player) {
            UUID uuid = player.getUniqueId();
            Player online = Bukkit.getPlayer(uuid);
            if (online != null) {
                if (BotIdentity.isBot(online)) {
                    return;
                }
                player = online;
            } else if (player instanceof Player) {
                // 玩家可能在分批采集期间退出，此时通过离线对象读取已保存的数据。
                player = Bukkit.getOfflinePlayer(uuid);
            }
            String name = player.getName();
            if (name == null || name.isBlank()) {
                name = uuid.toString();
            }
            String value = PlaceholderAPI.setPlaceholders(player, PLACEHOLDER);
            try {
                long seconds = Long.parseLong(value);
                if (seconds >= 0) {
                    entries.add(new LeaderboardSnapshot.PlayerTime(uuid.toString(), name, seconds));
                    return;
                }
            } catch (NumberFormatException ignored) {
                // PAPI 未解析变量时可能返回占位符原文，不能把它记作 0 秒。
            }
            invalidValues++;
        }

        private void finish() {
            cancel();
            batchTask = null;
        }
    }

    private void publishAsync(List<LeaderboardSnapshot.PlayerTime> entries, long refreshGeneration) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                LeaderboardSnapshot<LeaderboardSnapshot.PlayerTime> snapshot = LeaderboardSnapshot.from(entries);
                if (closed) return;
                Bukkit.getScheduler().runTask(plugin, () -> {
                    if (!closed && generation == refreshGeneration) latest = snapshot;
                });
            } catch (RuntimeException exception) {
                if (!closed) {
                    plugin.getLogger().log(Level.WARNING, "生成在线时长排行榜失败。", exception);
                }
            }
        });
    }

    private void unavailable(String message, RuntimeException exception) {
        latest = null;
        warn(message, exception);
    }

    private void warn(String message, RuntimeException exception) {
        if (!message.equals(previousWarning)) {
            plugin.getLogger().log(Level.WARNING, message, exception);
            previousWarning = message;
        }
    }

    @Override
    public void close() {
        closed = true;
        generation++;
        if (refreshTask != null) {
            refreshTask.cancel();
        }
        if (batchTask != null) {
            batchTask.cancel();
        }
        latest = null;
    }
}
