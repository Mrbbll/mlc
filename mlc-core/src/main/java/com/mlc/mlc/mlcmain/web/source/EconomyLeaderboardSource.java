package com.mlc.mlc.mlcmain.web.source;

import com.mlc.mlc.mlcmain.hook.economy.Moneyfilemanager;
import com.mlc.mlc.mlcmain.hook.economy.Moneyfilemanager.RankedBalance;
import com.mlc.mlc.mlcmain.web.LeaderboardSnapshot;

import java.sql.SQLException;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/** 在独立线程定期查询经济账户，HTTP 线程只读取完整的排行榜快照。 */
public final class EconomyLeaderboardSource implements AutoCloseable {
    private final Logger logger;
    private final ScheduledExecutorService worker = Executors.newSingleThreadScheduledExecutor(
            Thread.ofPlatform().daemon().name("mlc-economy-leaderboard").factory());
    private volatile LeaderboardSnapshot<RankedBalance> latest;

    public EconomyLeaderboardSource(Logger logger) {
        this.logger = logger;
    }

    public void start() {
        worker.scheduleWithFixedDelay(this::refresh, 0, 60, TimeUnit.SECONDS);
    }

    public LeaderboardSnapshot<RankedBalance> latest() {
        return latest;
    }

    private void refresh() {
        try {
            var ranking = Moneyfilemanager.getBalanceRanking(LeaderboardSnapshot.MAX_LIMIT);
            latest = new LeaderboardSnapshot<>(Instant.now(), ranking.totalPlayers(), ranking.players());
        } catch (SQLException | RuntimeException exception) {
            latest = null;
            logger.log(Level.WARNING, "刷新经济排行榜失败。", exception);
        }
    }

    @Override
    public void close() {
        worker.shutdownNow();
    }
}
