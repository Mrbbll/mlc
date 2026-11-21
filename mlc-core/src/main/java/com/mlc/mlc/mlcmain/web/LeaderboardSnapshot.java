package com.mlc.mlc.mlcmain.web;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

/** 一次采集的结果。记录全量人数，最多保留接口可返回的前 500 名。 */
record LeaderboardSnapshot(Instant updatedAt, int totalPlayers, List<PlayerTime> players) {
    static final int MAX_LIMIT = 500;

    record PlayerTime(String uuid, String name, long seconds) {}

    static LeaderboardSnapshot from(List<PlayerTime> entries) {
        return new LeaderboardSnapshot(Instant.now(), entries.size(), entries.stream()
                .sorted(Comparator.comparingLong(PlayerTime::seconds).reversed()
                        .thenComparing(PlayerTime::name).thenComparing(PlayerTime::uuid))
                .limit(MAX_LIMIT)
                .toList());
    }
}
