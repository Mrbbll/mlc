package com.mlc.mlc.mlcmain.web.source;

import com.mlc.mlc.totalassault.data.Datamanager;
import com.mlc.mlc.totalassault.data.TotalAssaultStorage;
import com.mlc.mlc.totalassault.data.difficulties;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/** 总力战网页数据转换；原始数据库查询与物品反序列化分别在合适的线程执行。 */
public final class TotalAssaultSource {
    private final JavaPlugin plugin;
    private static final PlainTextComponentSerializer PLAIN = PlainTextComponentSerializer.plainText();

    public record Activity(String id, String name, String bossId) {}
    public record Item(String slot, String material, String name, int amount, String itemModel,
                       Integer damage, List<String> lore, Map<String, Integer> enchantments) {}
    public record Attempt(int number, long battleMillis, double bossHealthAfter, List<Item> items) {}
    public record Entry(String activityId, String activityName, String bossId, String uuid, String name,
                        String difficulty, long score, long battleMillis, long achievedAtMillis,
                        List<Attempt> attempts) {}
    public record Leaderboard(String updatedAt, String currentActivityId, List<Activity> activities, List<Entry> players) {}

    public TotalAssaultSource(JavaPlugin plugin) { this.plugin = plugin; }

    /** 保留已有历史成绩接口的参数和响应结构。 */
    public TotalAssaultStorage.HistoryPage query(UUID player, String activity, int limit, int offset) throws SQLException {
        return Datamanager.scoreHistory(player, activity, limit, offset);
    }

    /** 查询所有活动的玩家最佳成绩，供网页按活动和难度进行筛选。 */
    public Leaderboard leaderboard() throws SQLException {
        List<TotalAssaultStorage.ActivitySummary> registeredActivities = Datamanager.activities();
        List<TotalAssaultStorage.HistoryScore> scores = new ArrayList<>();
        int offset = 0;
        while (true) {
            var page = Datamanager.scoreHistory(null, null, 100, offset);
            scores.addAll(page.scores());
            offset += page.scores().size();
            if (page.scores().isEmpty() || offset >= page.total()) break;
        }
        // Bukkit 物品、玩家资料及活动配置都在主线程读取，避免在 HTTP 工作线程调用服务端对象。
        try {
            return Bukkit.getScheduler().callSyncMethod(plugin, () -> convert(registeredActivities, scores)).get(30, TimeUnit.SECONDS);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new SQLException("查询总力战排行榜被中断", exception);
        } catch (ExecutionException | TimeoutException exception) {
            throw new SQLException("转换总力战排行榜失败", exception);
        }
    }

    private Leaderboard convert(List<TotalAssaultStorage.ActivitySummary> registeredActivities, List<TotalAssaultStorage.HistoryScore> scores) {
        var current = Datamanager.readActivityConfig(plugin);
        Map<String, Activity> activities = new LinkedHashMap<>();
        activities.put(current.id(), new Activity(current.id(), current.name(), current.bossId()));
        for (var activity : registeredActivities) {
            activities.putIfAbsent(activity.id(), new Activity(activity.id(), activity.name(), activity.bossId()));
        }
        List<Entry> entries = new ArrayList<>();
        for (var score : scores) {
            activities.putIfAbsent(score.activityId(), new Activity(score.activityId(), score.activityName(), score.bossId()));
            UUID uuid = UUID.fromString(score.playerUuid());
            String name = Bukkit.getOfflinePlayer(uuid).getName();
            if (name == null) name = uuid.toString();
            long value = difficulties.fromId(score.difficulty())
                    .orElseThrow(() -> new IllegalStateException("未知难度：" + score.difficulty())).score(score.battleMillis());
            List<Attempt> attempts = new ArrayList<>();
            for (var attempt : score.attempts()) {
                List<Item> items = new ArrayList<>();
                for (var item : attempt.items()) items.add(convertItem(item));
                attempts.add(new Attempt(attempt.number(), attempt.battleMillis(), attempt.bossHealthAfter(), List.copyOf(items)));
            }
            entries.add(new Entry(score.activityId(), score.activityName(), score.bossId(), score.playerUuid(), name,
                    score.difficulty(), value, score.battleMillis(), score.achievedAtMillis(), List.copyOf(attempts)));
        }
        return new Leaderboard(Instant.now().toString(), current.id(), List.copyOf(activities.values()), List.copyOf(entries));
    }

    /** 将服务端物品快照转换为网页可以展示的内容；无法读取的物品保留槽位。 */
    private Item convertItem(TotalAssaultStorage.HistoryItem item) {
        try {
            ItemStack stack = ItemStack.deserializeBytes(Base64.getDecoder().decode(item.snapshotBase64()));
            ItemMeta meta = stack.getItemMeta();
            String name = meta != null && meta.hasDisplayName() && meta.displayName() != null
                    ? PLAIN.serialize(meta.displayName()) : stack.getType().getKey().toString();
            String model = meta != null && meta.hasItemModel() && meta.getItemModel() != null ? meta.getItemModel().toString() : null;
            Integer damage = meta instanceof Damageable damaged && damaged.hasDamage() ? damaged.getDamage() : null;
            List<String> lore = meta == null || meta.lore() == null ? List.of() : meta.lore().stream().map(PLAIN::serialize).toList();
            Map<String, Integer> enchantments = new LinkedHashMap<>();
            stack.getEnchantments().forEach((enchantment, level) -> enchantments.put(enchantment.getKey().toString(), level));
            return new Item(item.slot(), stack.getType().getKey().toString(), name, stack.getAmount(), model,
                    damage, lore, Map.copyOf(enchantments));
        } catch (RuntimeException exception) {
            plugin.getLogger().warning("无法读取总力战物品快照：" + item.slot());
            return new Item(item.slot(), "", "物品快照无法读取", 0, null, null, List.of(), Map.of());
        }
    }
}
