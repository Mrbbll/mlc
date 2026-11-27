package com.mlc.mlc.totalassault.data;

import org.bukkit.plugin.java.JavaPlugin;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/** 总力战数据的统一入口；数据库连接由此类管理。 */
public final class Datamanager {
    public static final int ta_rund_time = 60 * 60;
    public static final int ta_try_time = 60 * 4;
    private static TotalAssaultStorage storage;

    private Datamanager() {}

    /**
     * 读取活动配置；配置文件不存在时从插件资源复制默认文件。
     * @param plugin 总力战所属插件，用于定位数据目录和默认资源。
     * @return 活动 ID、名称、Boss ID、开始时间和有序难度 ID。
     * @throws IllegalArgumentException 默认资源不存在或复制失败。
     * @throws NullPointerException 配置中缺少必需字段。
     * @throws java.time.format.DateTimeParseException 活动开始时间格式不正确。
     */
    public static Filereader.ActivityConfig readActivityConfig(JavaPlugin plugin) {
        return Filereader.readActivity(plugin);
    }

    /**
     * 获取总力战数据库文件路径，不创建或打开文件。
     * @param plugin 总力战所属插件。
     * @return 插件数据目录下 totalassault/totalassault.db 的路径。
     */
    public static Path databasePath(JavaPlugin plugin) {
        return Filereader.databasePath(plugin);
    }

    /**
     * 读取插件内置的 SQLite 建表脚本。
     * @return UTF-8 编码的建表脚本文本。
     * @throws IOException 资源不存在或读取失败。
     */
    public static String readDatabaseSchema() throws IOException {
        return Filereader.readDatabaseSchema();
    }

    static void prepareDatabaseDirectory(Path file) throws IOException {
        Filereader.prepareDatabaseDirectory(file);
    }

    /**
     * 初始化数据库连接和表结构；插件启动时调用，无返回值。
     * @param file SQLite 数据库文件路径，父目录不存在时会创建。
     * @throws IOException 创建目录或读取建表资源失败。
     * @throws SQLException 连接或初始化数据库失败。
     * @throws IllegalStateException 数据库已经初始化。
     */
    public static synchronized void init(Path file) throws IOException, SQLException {
        if (storage != null) throw new IllegalStateException("总力战数据库已初始化");
        storage = new TotalAssaultStorage(file);
    }

    private static synchronized TotalAssaultStorage storage() {
        if (storage == null) throw new IllegalStateException("总力战数据库尚未初始化");
        return storage;
    }

    /**
     * 关闭数据库连接；插件关闭时调用，未初始化时不执行操作，无返回值。
     * @throws SQLException 关闭连接失败。
     */
    public static synchronized void shutdown() throws SQLException {
        if (storage != null) {
            try { storage.close(); } finally { storage = null; }
        }
    }

    /**
     * 获取一张票的挑战机会时长，无参数。
     * @return 挑战时长，单位为秒（3600）。
     */
    public static int getTa_rund_time() { return ta_rund_time; }
    /**
     * 获取单次出战的最长时长，无参数。
     * @return 单次出战时长，单位为秒（240）。
     */
    public static int getTa_try_time() { return ta_try_time; }
    /**
     * 计算指定时刻所属的每日票券日期，刷新点为北京时间 04:00。
     * @param now 要计算的实际时间。
     * @return 刷新点对应的日期；04:00 前属于前一天。
     */
    public static LocalDate ticketDay(Instant now) { return TotalAssaultStorage.ticketDay(now); }

    /**
     * 登记固定七天的活动及其难度顺序，无返回值。已有活动的配置必须一致。
     * @param id 活动唯一 ID。
     * @param name 活动显示名称。
     * @param bossId Boss 唯一 ID。
     * @param start 活动开始的实际时间。
     * @param difficulties 按解锁顺序排列的非空、无重复难度 ID 列表。
     * @throws SQLException 配置无效、已有配置不一致或数据库操作失败。
     */
    public static void registerActivity(String id, String name, String bossId, Instant start, List<String> difficulties) throws SQLException {
        storage().registerActivity(id, name, bossId, start, difficulties);
    }

    /**
     * 读取玩家当日票数；活动开放且当天尚无记录时创建三张票。
     * @param player 玩家 UUID。
     * @param activity 活动 ID。
     * @param now 查询时刻，用于检查活动状态和计算刷新日期。
     * @return 当日剩余票数，范围为 0 至 3。
     * @throws SQLException 活动未开放或数据库操作失败。
     */
    public static int remainingTickets(UUID player, String activity, Instant now) throws SQLException {
        return storage().remainingTickets(player, activity, now);
    }
    /**
     * 读取玩家当日票数，是 remainingTickets 的别名。
     * @param player 玩家 UUID。
     * @param activity 活动 ID。
     * @param now 查询时刻。
     * @return 当日剩余票数，范围为 0 至 3。
     * @throws SQLException 活动未开放或数据库操作失败。
     */
    public static int getremainticket(UUID player, String activity, Instant now) throws SQLException {
        return remainingTickets(player, activity, now);
    }

    /**
     * 在创建独立世界前登记准备记录，用于失败或重启后清理；不会实际创建世界或扣票，无返回值。
     * @param player 玩家 UUID。
     * @param world 准备创建的唯一世界名。
     * @param now 登记时刻。
     * @throws SQLException 玩家已有挑战、准备记录冲突或数据库操作失败。
     */
    public static void prepareWorld(UUID player, String world, Instant now) throws SQLException {
        storage().prepareWorld(player, world, now);
    }
    /**
     * 更新准备记录的清理状态，无返回值。
     * @param player 玩家 UUID。
     * @param state 状态：CLEANING（清理中）或 ERROR（清理失败）。
     * @throws SQLException 状态无效、记录不存在或数据库操作失败。
     */
    public static void setPreparationState(UUID player, String state) throws SQLException {
        storage().setPreparationState(player, state);
    }
    /**
     * 删除准备记录；只应在确认世界已删除后调用，无返回值。
     * @param player 玩家 UUID；记录须处于 CLEANING 或 ERROR。
     * @throws SQLException 记录不存在、状态不允许删除或数据库操作失败。
     */
    public static void removePreparation(UUID player) throws SQLException {
        storage().removePreparation(player);
    }
    /**
     * 读取所有尚未移交为挑战的世界准备记录，无参数。
     * @return 准备记录列表；无记录时为空列表。
     * @throws SQLException 数据库操作失败。
     */
    public static List<TotalAssaultStorage.WorldPreparation> preparations() throws SQLException {
        return storage().preparations();
    }

    /**
     * 在一个事务中扣票、创建挑战并移除准备记录；世界必须已准备好，无返回值。
     * @param challengeId 本次挑战唯一 ID。
     * @param player 玩家 UUID。
     * @param activity 活动 ID。
     * @param difficulty 选定的难度 ID。
     * @param world 已经登记并准备完成的世界名。
     * @param bossHealth Boss 初始血量，必须为正数。
     * @param now 开始时刻；一小时期限从此刻起算。
     * @throws SQLException 活动未开放、难度未解锁、票数不足、准备记录无效或数据库操作失败；事务会回滚。
     */
    public static void startChallenge(String challengeId, UUID player, String activity, String difficulty,
                                      String world, double bossHealth, Instant now) throws SQLException {
        storage().startChallenge(challengeId, player, activity, difficulty, world, bossHealth, now);
    }

    /**
     * 使用预定义难度创建挑战；扣票、创建挑战和移除准备记录在同一事务中完成。
     * @param challengeId 本次挑战唯一 ID
     * @param player 玩家 UUID
     * @param activity 活动 ID
     * @param difficulty 已定义的难度枚举
     * @param world 已经登记并准备完成的世界名
     * @param bossHealth Boss 初始血量，必须为正数
     * @param now 开始时刻；一小时期限从此刻起算
     * @throws SQLException 活动、难度、票数或世界状态不满足条件，或数据库操作失败
     */
    public static void startChallenge(String challengeId, UUID player, String activity, difficulties difficulty,
                                      String world, double bossHealth, Instant now) throws SQLException {
        startChallenge(challengeId, player, activity, java.util.Objects.requireNonNull(difficulty, "difficulty").getId(),
                world, bossHealth, now);
    }
    /**
     * 查询玩家尚未结束的挑战，包含准备中和出战中。
     * @param player 玩家 UUID。
     * @return 有挑战时返回挑战记录，否则返回空 Optional。
     * @throws SQLException 数据库操作失败。
     */
    public static Optional<TotalAssaultStorage.Challenge> findActiveChallenge(UUID player) throws SQLException {
        return storage().findActiveChallenge(player);
    }
    /**
     * 保存最后一次出战并在同一事务中完成挑战、更新解锁和最佳成绩，无返回值。
     * @param challengeId 挑战 ID。
     * @param attemptId 当前活动出战的 ID。
     * @param battleMillis 本次出战时长，单位毫秒，范围为 0 至 240000。
     * @param now Boss 被击败的时刻；挑战和活动必须仍有效。
     * @throws SQLException 挑战已过期、出战状态或参数无效，或数据库操作失败；事务会回滚。
     */
    public static void completeChallenge(String challengeId, String attemptId, long battleMillis, Instant now) throws SQLException {
        storage().completeChallenge(challengeId, attemptId, battleMillis, now);
    }
    /**
     * 将未结束挑战标为失败并准备清理世界；正在进行的出战会回退，无返回值。
     * @param challengeId 挑战 ID。
     * @param reason 失败原因：GIVE_UP、EXPIRED 或 ACTIVITY_ENDED。
     * @param now 结束时刻。
     * @throws SQLException 原因或挑战状态无效，或数据库操作失败。
     */
    public static void failChallenge(String challengeId, String reason, Instant now) throws SQLException {
        storage().failChallenge(challengeId, reason, now);
    }
    /**
     * 更新已结束挑战的世界清理状态，无返回值。
     * @param challengeId 已结束的挑战 ID。
     * @param state 状态：CLEANING、DELETED 或 ERROR；DELETED 仅在确认删除后使用。
     * @throws SQLException 状态无效、世界不等待清理或数据库操作失败。
     */
    public static void setWorldState(String challengeId, String state) throws SQLException {
        storage().setWorldState(challengeId, state);
    }

    /**
     * 记录进场物品快照并将挑战设为出战中；背包槽位和 Boss 黑名单由调用方检查，无返回值。
     * @param challengeId 准备中的挑战 ID。
     * @param attemptId 本次出战唯一 ID。
     * @param items 进场物品快照；一次出战可包含多个同类物品。
     * @param now 开始出战的时刻。
     * @throws SQLException 挑战已过期、状态无效、物品曾在已保存出战中使用或数据库操作失败。
     */
    public static void startAttempt(String challengeId, String attemptId, List<TotalAssaultStorage.AttemptItem> items, Instant now) throws SQLException {
        storage().startAttempt(challengeId, attemptId, items, now);
    }
    /**
     * 读取本张票中已保存出战的物品分类，不包含回退或正在进行的出战。
     * @param challengeId 挑战 ID。
     * @return 去重后的分类集合，格式为 MODEL:命名空间模型 ID 或 MATERIAL:材质名。
     * @throws SQLException 数据库操作失败。
     */
    public static Set<String> usedItemIdentities(String challengeId) throws SQLException {
        return storage().usedItemIdentities(challengeId);
    }
    /**
     * 读取本张票已保存出战的物品分类，是 usedItemIdentities 的别名。
     * @param challengeId 挑战 ID。
     * @return 去重后的物品分类集合。
     * @throws SQLException 数据库操作失败。
     */
    public static Set<String> getusedkit(String challengeId) throws SQLException {
        return usedItemIdentities(challengeId);
    }
    /**
     * 读取一次出战进场时的完整物品快照。
     * @param attemptId 出战 ID。
     * @return 物品快照列表；无记录时为空列表。
     * @throws SQLException 数据库操作失败。
     */
    public static List<TotalAssaultStorage.AttemptItem> attemptItems(String attemptId) throws SQLException {
        return storage().attemptItems(attemptId);
    }
    /**
     * 读取一张票的所有出战记录，包含进行中、已保存和已回退的记录。
     * @param challengeId 挑战 ID。
     * @return 按出战序号升序排列的列表；无记录时为空列表。
     * @throws SQLException 数据库操作失败。
     */
    public static List<TotalAssaultStorage.Attempt> attempts(String challengeId) throws SQLException {
        return storage().attempts(challengeId);
    }
    /**
     * 保存一次出战的血量和战斗时间，并将挑战切回准备状态，无返回值。
     * @param challengeId 挑战 ID。
     * @param attemptId 当前活动出战的 ID。
     * @param battleMillis 本次出战时长，单位毫秒，范围为 0 至 240000。
     * @param bossHealth 出战结束时 Boss 剩余血量，不得为负数。
     * @param now 出战结束时刻；挑战和活动必须仍有效。
     * @throws SQLException 出战或挑战状态、参数无效，或数据库操作失败。
     */
    public static void saveAttempt(String challengeId, String attemptId, long battleMillis, double bossHealth, Instant now) throws SQLException {
        storage().saveAttempt(challengeId, attemptId, battleMillis, bossHealth, now);
    }
    /**
     * 回退挑战中正在进行的出战，保留上一次已保存的进度，无返回值。
     * @param challengeId 挑战 ID。
     * @param now 回退时刻。
     * @throws SQLException 不存在正在进行的出战、状态不一致或数据库操作失败。
     */
    public static void rollbackAttempt(String challengeId, Instant now) throws SQLException {
        storage().rollbackAttempt(challengeId, now);
    }

    /**
     * 读取玩家本期已通关的最高难度顺序。
     * @param player 玩家 UUID。
     * @param activity 活动 ID。
     * @return 最高通关难度的从 0 开始顺序；未通关时返回 -1。
     * @throws SQLException 数据库操作失败。
     */
    public static int highestClearedDifficulty(UUID player, String activity) throws SQLException {
        return storage().highestClearedDifficulty(player, activity);
    }
    /**
     * 读取玩家本期的最高成绩。
     * @param player 玩家 UUID。
     * @param activity 活动 ID。
     * @return 有成绩时返回成绩记录，否则返回空 Optional。
     * @throws SQLException 数据库操作失败。
     */
    public static Optional<TotalAssaultStorage.Score> bestScore(UUID player, String activity) throws SQLException {
        return storage().bestScore(player, activity);
    }
    /**
     * 读取玩家本期的最高成绩，是 bestScore 的别名；此方法不计算排行榜名次。
     * @param player 玩家 UUID。
     * @param activity 活动 ID。
     * @return 有成绩时返回成绩记录，否则返回空 Optional。
     * @throws SQLException 数据库操作失败。
     */
    public static Optional<TotalAssaultStorage.Score> get_rank(UUID player, String activity) throws SQLException {
        return bestScore(player, activity);
    }

    /**
     * 读取未结束挑战以及世界仍待清理或清理失败的挑战，无参数。
     * @return 需要恢复或清理的挑战列表；无记录时为空列表。
     * @throws SQLException 数据库操作失败。
     */
    public static List<TotalAssaultStorage.Challenge> challengesNeedingRecovery() throws SQLException {
        return storage().challengesNeedingRecovery();
    }
    /**
     * 重启后结束尚未完成的挑战；不实际处理世界和玩家物品，无返回值。
     * @param now 恢复操作的实际时间。
     * @throws SQLException 状态更新或数据库操作失败。
     */
    public static void recoverAfterRestart(Instant now) throws SQLException {
        storage().recoverAfterRestart(now);
    }
    /**
     * 标记机会或活动已到期的挑战；不实际清理世界，无返回值。
     * @param now 检查截止时间时使用的实际时间。
     * @throws SQLException 状态更新或数据库操作失败。
     */
    public static void expireChallenges(Instant now) throws SQLException {
        storage().expireChallenges(now);
    }

    /**
     * 按奖励业务键创建待投递记录；此操作不会实际发送邮件。
     * @param reward 奖励待办，包含唯一业务键、活动、玩家、种类及序列化内容。
     * @param now 创建记录的时刻。
     * @return 成功创建返回 true；业务键已存在返回 false。
     * @throws SQLException 数据无效或数据库操作失败。
     */
    public static boolean enqueueReward(TotalAssaultStorage.Reward reward, Instant now) throws SQLException {
        return storage().enqueueReward(reward, now);
    }
    /**
     * 读取尚未标记投递成功的奖励待办，无参数。
     * @return 待投递奖励列表；无记录时为空列表。
     * @throws SQLException 数据库操作失败。
     */
    public static List<TotalAssaultStorage.Reward> pendingRewards() throws SQLException {
        return storage().pendingRewards();
    }
    /**
     * 将待办标记为已投递；只在具备幂等能力的邮件服务确认投递成功后调用，无返回值。
     * @param key 投递时使用的奖励业务唯一键。
     * @param now 确认投递的时刻。
     * @throws SQLException 待办不存在、已标记投递或数据库操作失败。
     */
    public static void markRewardDelivered(String key, Instant now) throws SQLException {
        storage().markRewardDelivered(key, now);
    }
}
