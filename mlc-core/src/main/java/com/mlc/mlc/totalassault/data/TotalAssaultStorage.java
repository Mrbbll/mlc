package com.mlc.mlc.totalassault.data;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import com.mlc.mlc.totalassault.boss.saint.Saint;

/** 总力战数据库。所有公开操作共用一条连接并串行执行。 */
public final class TotalAssaultStorage {
    private static final ZoneId BEIJING = ZoneId.of("Asia/Shanghai");
    private final Connection connection;

    public record Challenge(String id, String activityId, UUID player, String difficulty, int difficultyOrder,
                            String status, long startedAt, long expiresAt, double bossHealth, long battleMillis,
                            String worldName, String worldState) {}
    public record AttemptItem(String slot, String identityType, String identityValue, byte[] snapshot) {}
    public record Score(String challengeId, int difficultyOrder, long battleMillis, long achievedAt) {}
    public record Attempt(String id, int number, String status, long startedAt, Long endedAt, Long battleMillis, Double bossHealthAfter) {}
    public record WorldPreparation(UUID player, String worldName, String state) {}
    public record Reward(String key, String activityId, UUID player, String challengeId, String kind, byte[] payload) {}
    /** 对外查询使用的成绩和物品快照，不包含未保存或已回退的出战。 */
    public record HistoryItem(String slot, String identityType, String identityValue, String snapshotBase64) {}
    public record HistoryAttempt(int number, long battleMillis, double bossHealthAfter, List<HistoryItem> items) {}
    public record HistoryScore(String activityId, String activityName, String bossId, String playerUuid,
                               String challengeId, String difficulty, int difficultyOrder, long battleMillis,
                               long achievedAtMillis, List<HistoryAttempt> attempts) {}
    public record HistoryPage(long total, int limit, int offset, List<HistoryScore> scores) {}

    TotalAssaultStorage(Path file) throws SQLException, IOException {
        Datamanager.prepareDatabaseDirectory(file);
        connection = DriverManager.getConnection("jdbc:sqlite:" + file.toAbsolutePath());
        try {
            try (Statement s = connection.createStatement()) {
                s.execute("PRAGMA foreign_keys=ON");
                s.execute("PRAGMA busy_timeout=5000");
            }
            int version;
            try (Statement s = connection.createStatement(); ResultSet rs = s.executeQuery("PRAGMA user_version")) {
                version = rs.next() ? rs.getInt(1) : 0;
            }
            if (version == 0) initializeSchema();
            else {
                if (version == 1) { migrateSaintProgress(); version = 2; }
                if (version == 2) { migrateTicketBonus(); version = 3; }
                if (version != 3) throw new SQLException("Unsupported total assault database version: " + version);
            }
        } catch (SQLException | IOException | RuntimeException e) {
            try { connection.close(); } catch (SQLException close) { e.addSuppressed(close); }
            throw e;
        }
    }

    /** 为旧数据库增加赠票记录，不修改已有挑战和每日票数。 */
    private void migrateTicketBonus() throws SQLException {
        transaction(() -> {
            try (Statement statement = connection.createStatement()) {
                statement.execute("CREATE TABLE ta_ticket_bonus (activity_id TEXT NOT NULL, player_uuid TEXT NOT NULL, ticket_day TEXT NOT NULL, remaining INTEGER NOT NULL CHECK (remaining >= 0), PRIMARY KEY (activity_id,player_uuid,ticket_day), FOREIGN KEY (activity_id,player_uuid,ticket_day) REFERENCES ta_ticket_day(activity_id,player_uuid,ticket_day))");
                statement.execute("PRAGMA user_version = 3");
            }
            return null;
        });
    }

    /** 给已有数据库增加圣徒进度表，不删除原有票券与成绩。 */
    private void migrateSaintProgress() throws SQLException {
        transaction(() -> {
            try (Statement statement = connection.createStatement()) {
                statement.execute("CREATE TABLE ta_saint_progress (challenge_id TEXT PRIMARY KEY REFERENCES ta_challenge(id), ultimate REAL NOT NULL CHECK (ultimate >= 0 AND ultimate <= 100), stun REAL NOT NULL CHECK (stun >= 0 AND stun <= 100))");
                statement.execute("PRAGMA user_version = 2");
            }
            return null;
        });
    }

    private void initializeSchema() throws IOException, SQLException {
        String sql = Datamanager.readDatabaseSchema();
        StringBuilder clean = new StringBuilder();
        for (String line : sql.split("\\R")) {
            if (!line.stripLeading().startsWith("--")) clean.append(line).append('\n');
        }
        transaction(() -> {
            try (Statement s = connection.createStatement()) {
                for (String statement : clean.toString().split(";")) {
                    if (!statement.isBlank()) s.execute(statement);
                }
            }
            return null;
        });
    }

    @FunctionalInterface private interface Work<T> { T run() throws SQLException; }
    private <T> T transaction(Work<T> work) throws SQLException {
        connection.setAutoCommit(false);
        try {
            T result = work.run();
            connection.commit();
            return result;
        } catch (SQLException | RuntimeException e) {
            try { connection.rollback(); } catch (SQLException rollback) { e.addSuppressed(rollback); }
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    private int update(String sql, Object... args) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (int i = 0; i < args.length; i++) ps.setObject(i + 1, args[i]);
            return ps.executeUpdate();
        }
    }
    private static void require(boolean condition, String message) throws SQLException {
        if (!condition) throw new SQLException(message);
    }
    static LocalDate ticketDay(Instant now) {
        return now.atZone(BEIJING).minusHours(4).toLocalDate();
    }

    /** 活动 ID 一经登记不可改变起止时间、Boss 和难度顺序。 */
    synchronized void registerActivity(String id, String name, String bossId, Instant start, List<String> difficulties) throws SQLException {
        Objects.requireNonNull(start);
        require(id != null && !id.isBlank() && name != null && !name.isBlank() && bossId != null && !bossId.isBlank(), "Invalid activity");
        require(difficulties != null && !difficulties.isEmpty() && difficulties.stream().allMatch(d -> d != null && !d.isBlank())
                && new HashSet<>(difficulties).size() == difficulties.size(), "Invalid difficulties");
        long startMs = start.toEpochMilli();
        long endMs = start.plusSeconds(7L * 24 * 60 * 60).toEpochMilli();
        transaction(() -> {
            try (PreparedStatement ps = connection.prepareStatement("SELECT name,boss_id,start_ms,end_ms FROM ta_activity WHERE id=?")) {
                ps.setString(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        require(name.equals(rs.getString(1)) && bossId.equals(rs.getString(2)) && startMs == rs.getLong(3) && endMs == rs.getLong(4), "Activity configuration changed for " + id);
                        List<String> existing = new ArrayList<>();
                        try (PreparedStatement d = connection.prepareStatement("SELECT difficulty_id FROM ta_difficulty WHERE activity_id=? ORDER BY difficulty_order")) {
                            d.setString(1, id);
                            try (ResultSet dr = d.executeQuery()) { while (dr.next()) existing.add(dr.getString(1)); }
                        }
                        require(existing.equals(difficulties), "Difficulty configuration changed for " + id);
                        return null;
                    }
                }
            }
            update("INSERT INTO ta_activity VALUES (?,?,?,?,?)", id, name, bossId, startMs, endMs);
            for (int i = 0; i < difficulties.size(); i++) update("INSERT INTO ta_difficulty VALUES (?,?,?)", id, difficulties.get(i), i);
            return null;
        });
    }

    private boolean active(String activity, long now) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("SELECT 1 FROM ta_activity WHERE id=? AND start_ms<=? AND end_ms>?")) {
            ps.setString(1, activity); ps.setLong(2, now); ps.setLong(3, now);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }
    private void ensureTicket(UUID player, String activity, String day) throws SQLException {
        update("INSERT INTO ta_ticket_day(activity_id,player_uuid,ticket_day,remaining) VALUES(?,?,?,3) ON CONFLICT DO NOTHING", activity, player.toString(), day);
    }
    synchronized int remainingTickets(UUID player, String activity, Instant now) throws SQLException {
        return transaction(() -> {
            require(active(activity, now.toEpochMilli()), "Activity is not active");
            String day = ticketDay(now).toString();
            ensureTicket(player, activity, day);
            try (PreparedStatement ps = connection.prepareStatement("SELECT d.remaining+COALESCE(b.remaining,0) FROM ta_ticket_day d LEFT JOIN ta_ticket_bonus b ON b.activity_id=d.activity_id AND b.player_uuid=d.player_uuid AND b.ticket_day=d.ticket_day WHERE d.activity_id=? AND d.player_uuid=? AND d.ticket_day=?")) {
                ps.setString(1, activity); ps.setString(2, player.toString()); ps.setString(3, day);
                try (ResultSet rs = ps.executeQuery()) { require(rs.next(), "Ticket row missing"); return rs.getInt(1); }
            }
        });
    }

    /** 增加当前刷新日的赠票；操作与余额上限检查在同一事务完成。 */
    synchronized int giveTickets(UUID player, String activity, int amount, Instant now) throws SQLException {
        require(player != null && amount > 0, "赠票数量必须大于零");
        return transaction(() -> {
            require(active(activity, now.toEpochMilli()), "活动未开放");
            String day = ticketDay(now).toString();
            ensureTicket(player, activity, day);
            long remaining;
            try (PreparedStatement ps = connection.prepareStatement("SELECT d.remaining+COALESCE(b.remaining,0) FROM ta_ticket_day d LEFT JOIN ta_ticket_bonus b ON b.activity_id=d.activity_id AND b.player_uuid=d.player_uuid AND b.ticket_day=d.ticket_day WHERE d.activity_id=? AND d.player_uuid=? AND d.ticket_day=?")) {
                ps.setString(1, activity); ps.setString(2, player.toString()); ps.setString(3, day);
                try (ResultSet rs = ps.executeQuery()) { require(rs.next(), "票券记录不存在"); remaining = rs.getLong(1); }
            }
            require(remaining + amount <= Integer.MAX_VALUE, "票券数量超过上限");
            update("INSERT INTO ta_ticket_bonus(activity_id,player_uuid,ticket_day,remaining) VALUES(?,?,?,?) ON CONFLICT(activity_id,player_uuid,ticket_day) DO UPDATE SET remaining=remaining+excluded.remaining", activity, player.toString(), day, amount);
            return (int) (remaining + amount);
        });
    }

    synchronized void prepareWorld(UUID player, String world, Instant now) throws SQLException {
        transaction(() -> {
            require(findActiveChallenge(player).isEmpty(), "Player already has a challenge");
            update("INSERT INTO ta_world_preparation VALUES (?,?,?,'CREATING')", player.toString(), world, now.toEpochMilli());
            return null;
        });
    }
    synchronized void setPreparationState(UUID player, String state) throws SQLException {
        require(Set.of("CLEANING", "ERROR").contains(state), "Invalid preparation state");
        require(update("UPDATE ta_world_preparation SET state=? WHERE player_uuid=?", state, player.toString()) == 1, "Preparation not found");
    }
    /** 只有确认世界已经删除后调用。 */
    synchronized void removePreparation(UUID player) throws SQLException {
        require(update("DELETE FROM ta_world_preparation WHERE player_uuid=? AND state IN ('CLEANING','ERROR')", player.toString()) == 1, "Preparation not ready for removal");
    }
    synchronized List<WorldPreparation> preparations() throws SQLException {
        List<WorldPreparation> result = new ArrayList<>();
        try (Statement s = connection.createStatement(); ResultSet rs = s.executeQuery("SELECT player_uuid,world_name,state FROM ta_world_preparation")) {
            while (rs.next()) result.add(new WorldPreparation(UUID.fromString(rs.getString(1)), rs.getString(2), rs.getString(3)));
        }
        return result;
    }

    /** 世界准备完成后调用；扣票、创建挑战和移除准备记录在同一事务中。 */
    synchronized void startChallenge(String challengeId, UUID player, String activity, String difficulty,
                                            String world, double bossHealth, Instant now) throws SQLException {
        require(Double.isFinite(bossHealth) && bossHealth > 0, "Invalid boss health");
        transaction(() -> {
            long time = now.toEpochMilli();
            require(active(activity, time), "Activity is not active");
            require(findActiveChallenge(player).isEmpty(), "Player already has a challenge");
            int order;
            try (PreparedStatement ps = connection.prepareStatement("SELECT difficulty_order FROM ta_difficulty WHERE activity_id=? AND difficulty_id=?")) {
                ps.setString(1, activity); ps.setString(2, difficulty);
                try (ResultSet rs = ps.executeQuery()) { require(rs.next(), "Difficulty not found"); order = rs.getInt(1); }
            }
            int highest = -1;
            try (PreparedStatement ps = connection.prepareStatement("SELECT highest_cleared_order FROM ta_progress WHERE activity_id=? AND player_uuid=?")) {
                ps.setString(1, activity); ps.setString(2, player.toString());
                try (ResultSet rs = ps.executeQuery()) { if (rs.next()) highest = rs.getInt(1); }
            }
            require(order <= highest + 1, "Difficulty is locked");
            try (PreparedStatement ps = connection.prepareStatement("SELECT 1 FROM ta_world_preparation WHERE player_uuid=? AND world_name=? AND state='CREATING'")) {
                ps.setString(1, player.toString()); ps.setString(2, world);
                try (ResultSet rs = ps.executeQuery()) { require(rs.next(), "World preparation not found"); }
            }
            String day = ticketDay(now).toString();
            ensureTicket(player, activity, day);
            // 优先消耗每日票，日票用完后再消耗同一刷新日的赠票。
            int consumed = update("UPDATE ta_ticket_day SET remaining=remaining-1 WHERE activity_id=? AND player_uuid=? AND ticket_day=? AND remaining>0", activity, player.toString(), day);
            if (consumed == 0) consumed = update("UPDATE ta_ticket_bonus SET remaining=remaining-1 WHERE activity_id=? AND player_uuid=? AND ticket_day=? AND remaining>0", activity, player.toString(), day);
            require(consumed == 1, "No tickets remaining");
            update("INSERT INTO ta_challenge(id,activity_id,player_uuid,ticket_day,difficulty_id,difficulty_order,status,started_at_ms,expires_at_ms,saved_boss_health,world_name,world_state) VALUES(?,?,?,?,?,?,'PREPARING',?,?,?,?, 'READY')",
                    challengeId, activity, player.toString(), day, difficulty, order, time, now.plusSeconds(3600).toEpochMilli(), bossHealth, world);
            require(update("DELETE FROM ta_world_preparation WHERE player_uuid=? AND world_name=?", player.toString(), world) == 1, "World preparation disappeared");
            return null;
        });
    }

    synchronized Optional<Challenge> findActiveChallenge(UUID player) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("SELECT id,activity_id,player_uuid,difficulty_id,difficulty_order,status,started_at_ms,expires_at_ms,saved_boss_health,saved_battle_ms,world_name,world_state FROM ta_challenge WHERE player_uuid=? AND status IN ('PREPARING','BATTLING')")) {
            ps.setString(1, player.toString());
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? Optional.of(challenge(rs)) : Optional.empty(); }
        }
    }
    private static Challenge challenge(ResultSet rs) throws SQLException {
        return new Challenge(rs.getString(1), rs.getString(2), UUID.fromString(rs.getString(3)), rs.getString(4), rs.getInt(5), rs.getString(6), rs.getLong(7), rs.getLong(8), rs.getDouble(9), rs.getLong(10), rs.getString(11), rs.getString(12));
    }
    private Challenge getChallenge(String id) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("SELECT id,activity_id,player_uuid,difficulty_id,difficulty_order,status,started_at_ms,expires_at_ms,saved_boss_health,saved_battle_ms,world_name,world_state FROM ta_challenge WHERE id=?")) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) { require(rs.next(), "Challenge not found"); return challenge(rs); }
        }
    }
    private void requireLive(Challenge c, Instant now) throws SQLException {
        require(now.toEpochMilli() < c.expiresAt() && active(c.activityId(), now.toEpochMilli()), "Challenge expired");
    }

    /** 记录进场物品，并在同一事务中进入战斗状态。黑名单和背包其他槽位由调用方检查。 */
    synchronized void startAttempt(String challengeId, String attemptId, List<AttemptItem> items, Instant now) throws SQLException {
        transaction(() -> {
            Challenge c = getChallenge(challengeId);
            require("PREPARING".equals(c.status()), "Challenge is not preparing");
            requireLive(c, now);
            Set<String> used = usedItemIdentities(challengeId);
            for (AttemptItem item : items) {
                require(item.snapshot() != null && item.snapshot().length > 0 && item.identityValue() != null && !item.identityValue().isBlank(), "Invalid item snapshot");
                require(!used.contains(item.identityType() + ":" + item.identityValue()), "Item used by an earlier attempt");
            }
            int number;
            try (PreparedStatement ps = connection.prepareStatement("SELECT COALESCE(MAX(attempt_no),0)+1 FROM ta_attempt WHERE challenge_id=?")) {
                ps.setString(1, challengeId);
                try (ResultSet rs = ps.executeQuery()) { rs.next(); number = rs.getInt(1); }
            }
            update("INSERT INTO ta_attempt(id,challenge_id,attempt_no,status,started_at_ms) VALUES(?,?,?,'ACTIVE',?)", attemptId, challengeId, number, now.toEpochMilli());
            for (AttemptItem item : items) update("INSERT INTO ta_attempt_item VALUES(?,?,?,?,?)", attemptId, item.slot(), item.identityType(), item.identityValue(), item.snapshot());
            require(update("UPDATE ta_challenge SET status='BATTLING' WHERE id=? AND status='PREPARING'", challengeId) == 1, "Challenge state changed");
            return null;
        });
    }

    synchronized Set<String> usedItemIdentities(String challengeId) throws SQLException {
        Set<String> result = new HashSet<>();
        try (PreparedStatement ps = connection.prepareStatement("SELECT DISTINCT i.identity_type,i.identity_value FROM ta_attempt_item i JOIN ta_attempt a ON a.id=i.attempt_id WHERE a.challenge_id=? AND a.status='SAVED'")) {
            ps.setString(1, challengeId);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) result.add(rs.getString(1) + ":" + rs.getString(2)); }
        }
        return result;
    }

    synchronized List<AttemptItem> attemptItems(String attemptId) throws SQLException {
        List<AttemptItem> result = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement("SELECT slot,identity_type,identity_value,item_snapshot FROM ta_attempt_item WHERE attempt_id=? ORDER BY slot")) {
            ps.setString(1, attemptId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(new AttemptItem(rs.getString(1), rs.getString(2), rs.getString(3), rs.getBytes(4)));
            }
        }
        return result;
    }

    /** 按出战顺序返回；成绩展示时只取 SAVED 的出战。 */
    synchronized List<Attempt> attempts(String challengeId) throws SQLException {
        List<Attempt> result = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement("SELECT id,attempt_no,status,started_at_ms,ended_at_ms,battle_ms,boss_health_after FROM ta_attempt WHERE challenge_id=? ORDER BY attempt_no")) {
            ps.setString(1, challengeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    long end = rs.getLong(5); Long endedAt = rs.wasNull() ? null : end;
                    long duration = rs.getLong(6); Long battleTime = rs.wasNull() ? null : duration;
                    double health = rs.getDouble(7); Double remainingHealth = rs.wasNull() ? null : health;
                    result.add(new Attempt(rs.getString(1), rs.getInt(2), rs.getString(3), rs.getLong(4), endedAt, battleTime, remainingHealth));
                }
            }
        }
        return result;
    }

    synchronized int highestClearedDifficulty(UUID player, String activity) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("SELECT highest_cleared_order FROM ta_progress WHERE activity_id=? AND player_uuid=?")) {
            ps.setString(1, activity); ps.setString(2, player.toString());
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? rs.getInt(1) : -1; }
        }
    }

    synchronized void saveAttempt(String challengeId, String attemptId, long battleMillis, double bossHealth, Instant now) throws SQLException {
        transaction(() -> {
            Challenge c = getChallenge(challengeId);
            requireLive(c, now);
            saveAttemptInTransaction(c, attemptId, battleMillis, bossHealth, now);
            return null;
        });
    }

    /** 出战进度与圣徒两条进度在同一事务中保存。 */
    synchronized void saveSaintAttempt(String challengeId, String attemptId, long battleMillis, double bossHealth,
                                       Saint.Progress progress, Instant now) throws SQLException {
        require(progress != null && Double.isFinite(progress.ultimate()) && Double.isFinite(progress.stun())
                && progress.ultimate() >= 0 && progress.ultimate() <= 100 && progress.stun() >= 0 && progress.stun() <= 100,
                "圣徒进度无效");
        transaction(() -> {
            Challenge challenge = getChallenge(challengeId);
            requireLive(challenge, now);
            saveAttemptInTransaction(challenge, attemptId, battleMillis, bossHealth, now);
            update("INSERT INTO ta_saint_progress(challenge_id,ultimate,stun) VALUES(?,?,?) ON CONFLICT(challenge_id) DO UPDATE SET ultimate=excluded.ultimate,stun=excluded.stun",
                    challengeId, progress.ultimate(), progress.stun());
            return null;
        });
    }

    synchronized Saint.Progress saintProgress(String challengeId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT ultimate,stun FROM ta_saint_progress WHERE challenge_id=?")) {
            statement.setString(1, challengeId);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? new Saint.Progress(result.getDouble(1), result.getDouble(2)) : new Saint.Progress(0, 0);
            }
        }
    }

    private void saveAttemptInTransaction(Challenge c, String attemptId, long battleMillis, double bossHealth, Instant now) throws SQLException {
        require("BATTLING".equals(c.status()), "Challenge is not battling");
        require(battleMillis >= 0 && battleMillis <= 240_000 && Double.isFinite(bossHealth) && bossHealth >= 0, "Invalid battle progress");
        require(update("UPDATE ta_attempt SET status='SAVED',ended_at_ms=?,battle_ms=?,boss_health_after=? WHERE id=? AND challenge_id=? AND status='ACTIVE' AND started_at_ms<=?", now.toEpochMilli(), battleMillis, bossHealth, attemptId, c.id(), now.toEpochMilli()) == 1, "Active attempt not found");
        require(update("UPDATE ta_challenge SET status='PREPARING',saved_boss_health=?,saved_battle_ms=saved_battle_ms+? WHERE id=? AND status='BATTLING'", bossHealth, battleMillis, c.id()) == 1, "Challenge state changed");
    }

    synchronized void rollbackAttempt(String challengeId, Instant now) throws SQLException {
        transaction(() -> {
            require(update("UPDATE ta_attempt SET status='ROLLED_BACK',ended_at_ms=? WHERE challenge_id=? AND status='ACTIVE'", now.toEpochMilli(), challengeId) == 1, "Active attempt not found");
            require(update("UPDATE ta_challenge SET status='PREPARING' WHERE id=? AND status='BATTLING'", challengeId) == 1, "Challenge state changed");
            return null;
        });
    }

    /** 完成最后一次出战、解锁和最佳成绩更新合并在同一事务。 */
    synchronized void completeChallenge(String challengeId, String attemptId, long battleMillis, Instant now) throws SQLException {
        transaction(() -> {
            Challenge c = getChallenge(challengeId);
            requireLive(c, now);
            saveAttemptInTransaction(c, attemptId, battleMillis, 0, now);
            require(update("UPDATE ta_challenge SET status='SUCCESS',end_reason='BOSS_DEFEATED',ended_at_ms=?,world_state='CLEANING' WHERE id=? AND status='PREPARING'", now.toEpochMilli(), challengeId) == 1, "Challenge state changed");
            update("INSERT INTO ta_progress VALUES(?,?,?) ON CONFLICT(activity_id,player_uuid) DO UPDATE SET highest_cleared_order=MAX(highest_cleared_order,excluded.highest_cleared_order)", c.activityId(), c.player().toString(), c.difficultyOrder());
            update("INSERT INTO ta_best_score VALUES(?,?,?,?,?,?) ON CONFLICT(activity_id,player_uuid) DO UPDATE SET challenge_id=excluded.challenge_id,difficulty_order=excluded.difficulty_order,battle_ms=excluded.battle_ms,achieved_at_ms=excluded.achieved_at_ms WHERE excluded.difficulty_order>difficulty_order OR (excluded.difficulty_order=difficulty_order AND excluded.battle_ms<=battle_ms)", c.activityId(), c.player().toString(), challengeId, c.difficultyOrder(), c.battleMillis() + battleMillis, now.toEpochMilli());
            return null;
        });
    }

    synchronized void failChallenge(String challengeId, String reason, Instant now) throws SQLException {
        require(Set.of("GIVE_UP", "EXPIRED", "ACTIVITY_ENDED").contains(reason), "Invalid failure reason");
        transaction(() -> {
            Challenge c = getChallenge(challengeId);
            require(c.status().equals("PREPARING") || c.status().equals("BATTLING"), "Challenge already ended");
            if (c.status().equals("BATTLING")) {
                require(update("UPDATE ta_attempt SET status='ROLLED_BACK',ended_at_ms=? WHERE challenge_id=? AND status='ACTIVE'", now.toEpochMilli(), challengeId) == 1, "Active attempt not found");
            }
            require(update("UPDATE ta_challenge SET status='FAILED',end_reason=?,ended_at_ms=?,world_state='CLEANING' WHERE id=? AND status IN ('PREPARING','BATTLING')", reason, now.toEpochMilli(), challengeId) == 1, "Challenge state changed");
            return null;
        });
    }

    synchronized Optional<Score> bestScore(UUID player, String activity) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("SELECT challenge_id,difficulty_order,battle_ms,achieved_at_ms FROM ta_best_score WHERE player_uuid=? AND activity_id=?")) {
            ps.setString(1, player.toString()); ps.setString(2, activity);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(new Score(rs.getString(1), rs.getInt(2), rs.getLong(3), rs.getLong(4))) : Optional.empty();
            }
        }
    }

    /** 只读取各期最佳成绩及其已保存出战，不创建票券或变更挑战。 */
    synchronized HistoryPage scoreHistory(String player, String activity, int limit, int offset) throws SQLException {
        String filter = " WHERE (? IS NULL OR s.player_uuid=?) AND (? IS NULL OR s.activity_id=?)";
        long total;
        try (PreparedStatement ps = connection.prepareStatement("SELECT COUNT(*) FROM ta_best_score s" + filter)) {
            ps.setString(1, player); ps.setString(2, player); ps.setString(3, activity); ps.setString(4, activity);
            try (ResultSet rs = ps.executeQuery()) { total = rs.next() ? rs.getLong(1) : 0; }
        }
        List<HistoryScore> scores = new ArrayList<>();
        String sql = "SELECT s.activity_id,a.name,a.boss_id,s.player_uuid,s.challenge_id,c.difficulty_id,"
                + "s.difficulty_order,s.battle_ms,s.achieved_at_ms FROM ta_best_score s "
                + "JOIN ta_activity a ON a.id=s.activity_id JOIN ta_challenge c ON c.id=s.challenge_id"
                + filter + " ORDER BY a.start_ms DESC,s.activity_id,s.player_uuid LIMIT ? OFFSET ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, player); ps.setString(2, player); ps.setString(3, activity); ps.setString(4, activity);
            ps.setInt(5, limit); ps.setInt(6, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String challengeId = rs.getString(5);
                    List<HistoryAttempt> saved = new ArrayList<>();
                    for (Attempt attempt : attempts(challengeId)) {
                        if (!attempt.status().equals("SAVED")) continue;
                        List<HistoryItem> items = new ArrayList<>();
                        for (AttemptItem item : attemptItems(attempt.id())) {
                            items.add(new HistoryItem(item.slot(), item.identityType(), item.identityValue(),
                                    Base64.getEncoder().encodeToString(item.snapshot())));
                        }
                        saved.add(new HistoryAttempt(attempt.number(), attempt.battleMillis(), attempt.bossHealthAfter(), List.copyOf(items)));
                    }
                    scores.add(new HistoryScore(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),
                            challengeId, rs.getString(6), rs.getInt(7), rs.getLong(8), rs.getLong(9), List.copyOf(saved)));
                }
            }
        }
        return new HistoryPage(total, limit, offset, List.copyOf(scores));
    }

    synchronized void setWorldState(String challengeId, String state) throws SQLException {
        require(Set.of("CLEANING", "DELETED", "ERROR").contains(state), "Invalid world state");
        require(update("UPDATE ta_challenge SET world_state=? WHERE id=? AND status IN ('SUCCESS','FAILED') AND world_state IN ('CLEANING','ERROR')", state, challengeId) == 1, "World not awaiting cleanup");
    }

    synchronized List<Challenge> challengesNeedingRecovery() throws SQLException {
        List<Challenge> result = new ArrayList<>();
        try (Statement s = connection.createStatement(); ResultSet rs = s.executeQuery("SELECT id,activity_id,player_uuid,difficulty_id,difficulty_order,status,started_at_ms,expires_at_ms,saved_boss_health,saved_battle_ms,world_name,world_state FROM ta_challenge WHERE status IN ('PREPARING','BATTLING') OR world_state IN ('CLEANING','ERROR')")) {
            while (rs.next()) result.add(challenge(rs));
        }
        return result;
    }

    /** 重启后结束尚未完成的挑战；已经消耗的票不返还。 */
    synchronized void recoverAfterRestart(Instant now) throws SQLException {
        expireChallenges(now);
        for (Challenge c : challengesNeedingRecovery()) {
            if (c.status().equals("PREPARING") || c.status().equals("BATTLING")) {
                // 目前数据库没有专门的重启原因，暂按放弃记录。
                failChallenge(c.id(), "GIVE_UP", now);
            }
        }
    }

    /** 按现实时间结束过期挑战；世界清理由玩法层执行。 */
    synchronized void expireChallenges(Instant now) throws SQLException {
        for (Challenge c : challengesNeedingRecovery()) {
            if (!c.status().equals("PREPARING") && !c.status().equals("BATTLING")) continue;
            if (!active(c.activityId(), now.toEpochMilli())) {
                failChallenge(c.id(), "ACTIVITY_ENDED", now);
            } else if (now.toEpochMilli() >= c.expiresAt()) {
                failChallenge(c.id(), "EXPIRED", now);
            }
        }
    }

    synchronized boolean enqueueReward(Reward reward, Instant now) throws SQLException {
        return update("INSERT INTO ta_reward_delivery(reward_key,activity_id,player_uuid,challenge_id,reward_kind,payload,status,created_at_ms) VALUES(?,?,?,?,?,?,'PENDING',?) ON CONFLICT(reward_key) DO NOTHING", reward.key(), reward.activityId(), reward.player().toString(), reward.challengeId(), reward.kind(), reward.payload(), now.toEpochMilli()) == 1;
    }
    synchronized List<Reward> pendingRewards() throws SQLException {
        List<Reward> result = new ArrayList<>();
        try (Statement s = connection.createStatement(); ResultSet rs = s.executeQuery("SELECT reward_key,activity_id,player_uuid,challenge_id,reward_kind,payload FROM ta_reward_delivery WHERE status='PENDING'")) {
            while (rs.next()) result.add(new Reward(rs.getString(1), rs.getString(2), UUID.fromString(rs.getString(3)), rs.getString(4), rs.getString(5), rs.getBytes(6)));
        }
        return result;
    }
    /** 仅在幂等邮件服务确认投递成功后调用。 */
    synchronized void markRewardDelivered(String key, Instant now) throws SQLException {
        require(update("UPDATE ta_reward_delivery SET status='DELIVERED',delivered_at_ms=? WHERE reward_key=? AND status='PENDING'", now.toEpochMilli(), key) == 1, "Pending reward not found");
    }

    synchronized void close() throws SQLException { connection.close(); }
}
