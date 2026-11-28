package com.mlc.mlc.totalassault.menu;

import com.mlc.mlc.totalassault.ChallengeCreator;
import com.mlc.mlc.totalassault.battle.BattleManager;
import com.mlc.mlc.totalassault.boss.AssaultBoss;
import com.mlc.mlc.totalassault.boss.BossRegistry;
import com.mlc.mlc.totalassault.data.Datamanager;
import com.mlc.mlc.totalassault.data.Filereader;
import com.mlc.mlc.totalassault.data.TotalAssaultStorage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;
import java.util.logging.Level;

/** 总力战菜单；玩家、页码和查看对象均保存在各自的容器中。 */
public final class tamenu {
    private static final MiniMessage MINI = MiniMessage.miniMessage();
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneId.of("Asia/Shanghai"));
    private static final int[] CONTENT = {10,11,12,13,14,15,16,19,20,21,22,23,24,25,28,29,30,31,32,33,34,37,38,39,40,41,42,43};
    private static final int BACK = 45, PREVIOUS = 46, INFO = 4, RANKING = 50, NEXT = 52, CLOSE = 53;
    private static final int SMALL_BACK = 18, SMALL_STATUS = 20, SMALL_RECORD = 22, SMALL_CONTROL = 24, SMALL_CLOSE = 26;
    public static JavaPlugin plugin;
    private static Filereader.ActivityConfig activity;
    private static ChallengeCreator challengeCreator;
    private static BattleManager battleManager;
    private static BossRegistry bosses;

    private enum Page { MAIN, ATTEMPTS, ITEMS, OPERATIONS, RANKING }

    private static final class Holder implements InventoryHolder {
        private final UUID player;
        private final Page page;
        private final int pageNumber;
        private final UUID subject;
        private final int returnPage;
        private final List<String> actions = new ArrayList<>();
        private final Map<String, Integer> skillPages = new HashMap<>();
        private Inventory inventory;
        private Holder(UUID player, Page page, int pageNumber, UUID subject, int returnPage) {
            this.player = player; this.page = page; this.pageNumber = pageNumber;
            this.subject = subject; this.returnPage = returnPage;
        }
        @Override public @NotNull Inventory getInventory() { return inventory; }
    }

    private tamenu() {}

    public static void init(JavaPlugin owner, Filereader.ActivityConfig config, ChallengeCreator creator, BattleManager manager) {
        plugin = owner; activity = config; challengeCreator = creator; battleManager = manager;
        bosses = new BossRegistry();
        owner.getServer().getPluginManager().registerEvents(new Listener(), owner);
    }

    /** 正在挑战的玩家直接打开操作页面；其他玩家进入难度选择页面。 */
    public static void open(Player player) {
        try {
            if (Datamanager.findActiveChallenge(player.getUniqueId()).isPresent()) operations(player);
            else main(player);
        } catch (SQLException exception) { report(player, exception); }
    }

    private static void report(Player player, SQLException exception) {
        plugin.getLogger().log(Level.SEVERE, "读取总力战菜单数据失败", exception);
        player.sendMessage(line("<#ffb4a8>总力战数据暂时无法读取，请稍后再试。"));
    }

    private static void show(Player player, Page page, int index, UUID subject, int returnPage, String attemptId) {
        try {
            switch (page) {
                case MAIN -> main(player);
                case OPERATIONS -> operations(player);
                case ATTEMPTS -> attemptList(player, index, subject, returnPage);
                case ITEMS -> attemptItems(player, subject, index, returnPage, attemptId);
                case RANKING -> ranking(player, index);
            }
        } catch (SQLException exception) { report(player, exception); }
    }

    /** 三行用于主页和操作页，其余页面使用六行以容纳记录和装备。 */
    private static Holder inventory(Player player, Page page, int index, UUID subject, int returnPage, String title) {
        Holder holder = new Holder(player.getUniqueId(), page, index, subject, returnPage);
        int size = page == Page.MAIN || page == Page.OPERATIONS ? 27 : 54;
        holder.inventory = Bukkit.createInventory(holder, size, line("<#b8ffde>" + title));
        ItemStack border = icon(Material.BLACK_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 9; i++) {
            holder.inventory.setItem(i, border);
            holder.inventory.setItem(size - 9 + i, border);
        }
        for (int row = 1; row < size / 9 - 1; row++) {
            holder.inventory.setItem(row * 9, border);
            holder.inventory.setItem(row * 9 + 8, border);
        }
        holder.inventory.setItem(size - 1, icon(Material.BARRIER, "<#ffb4a8>关闭"));
        return holder;
    }

    private static void main(Player player) throws SQLException {
        Instant now = Instant.now();
        Instant end = activity.start().plusSeconds(7L * 24 * 60 * 60);
        boolean active = !now.isBefore(activity.start()) && now.isBefore(end);
        var current = Datamanager.findActiveChallenge(player.getUniqueId());
        var score = Datamanager.bestScore(player.getUniqueId(), activity.id());
        int unlocked = Datamanager.highestClearedDifficulty(player.getUniqueId(), activity.id()) + 1;
        Integer tickets = active ? Datamanager.remainingTickets(player.getUniqueId(), activity.id(), now) : null;
        Holder holder = inventory(player, Page.MAIN, 0, null, 0, "总力战");
        holder.inventory.setItem(INFO, icon(Material.NETHER_STAR, "<#b8ffde>" + escape(activity.name()),
                "<#aab8b0>Boss：<#ffffff>" + escape(activity.bossId()),
                "<#aab8b0>活动：" + (active ? "<#b8ffde>进行中" : now.isBefore(activity.start()) ? "<#ffd166>尚未开始" : "<#ffb4a8>已结束"),
                "<#aab8b0>开始：<#ffffff>" + DATE.format(activity.start()) + " 北京时间",
                "<#aab8b0>结束：<#ffffff>" + DATE.format(end) + " 北京时间"));
        holder.inventory.setItem(SMALL_STATUS, icon(Material.PAPER, "<#ffd166>挑战票",
                tickets == null ? "<#aab8b0>活动未开放" : "<#aab8b0>今日剩余：<#ffffff>" + tickets,
                "<#71877a>每日北京时间 04:00 刷新"));
        holder.inventory.setItem(SMALL_RECORD, score.isPresent()
                ? icon(Material.WRITABLE_BOOK, "<#ffd166>本期最佳成绩", "<#aab8b0>难度：<#ffffff>" + escape(difficultyName(score.get().difficultyOrder())),
                    "<#aab8b0>用时：<#ffffff>" + duration(score.get().battleMillis()), "<#b8ffde>» 点击查看出战记录与排行榜")
                : icon(Material.BOOK, "<#ffd166>出战记录与排行榜", "<#71877a>尚无通关成绩", "<#b8ffde>» 点击查看排行榜"));
        holder.inventory.setItem(SMALL_CONTROL, current.isPresent()
                ? icon(Material.CLOCK, "<#ffd166>当前挑战", "<#b8ffde>» 点击查看挑战操作")
                : icon(Material.CLOCK, "<#aab8b0>当前没有挑战", "<#71877a>选择已解锁的难度创建挑战"));
        // 七个内置难度恰好放在中间一行，不再显示页码。
        for (int i = 0; i < activity.difficulties().size() && i < 7; i++) {
            String difficulty = activity.difficulties().get(i);
            boolean available = i <= unlocked;
            holder.inventory.setItem(10 + i, difficultyIcon(difficulty, available, 0));
            holder.actions.add(difficulty);
        }
        player.openInventory(holder.inventory);
    }

    /** 每个难度图标单独保存技能页码，数值始终来自该 Boss 的实际难度数据。 */
    private static ItemStack difficultyIcon(String difficulty, boolean available, int requestedPage) {
        List<String> lore = new ArrayList<>();
        var boss = bosses.find(activity.bossId());
        if (boss.isPresent()) {
            AssaultBoss definition = boss.get();
            lore.add("<#aab8b0>Boss：<#ffffff>" + escape(definition.displayName()));
            lore.add("<#aab8b0>最大血量：<#ffffff>" + definition.attributes(difficulty).health());
            List<AssaultBoss.SkillInfo> skills = definition.skillInfo(difficulty);
            if (!skills.isEmpty()) {
                int page = Math.floorMod(requestedPage, skills.size());
                var skill = skills.get(page);
                lore.add("<#ffd166>技能 " + (page + 1) + "/" + skills.size() + "：" + escape(skill.name()));
                lore.add("<#aab8b0>" + escape(skill.description()));
                for (String detail : skill.details()) lore.add("<#aab8b0>" + escape(detail));
                lore.add("<#71877a>Shift 左键：下一技能　Shift 右键：上一技能");
            } else lore.add("<#71877a>该 Boss 暂无技能说明");
        } else lore.add("<#ffb4a8>Boss 尚未注册");
        lore.add(available ? "<#b8ffde>已解锁，左键准备挑战" : "<#ffb4a8>通关上一难度后解锁");
        return icon(available ? Material.IRON_SWORD : Material.GRAY_DYE,
                (available ? "<#ffd166>" : "<#71877a>") + escape(difficulty), lore.toArray(String[]::new));
    }

    private static void operations(Player player) throws SQLException {
        var current = Datamanager.findActiveChallenge(player.getUniqueId());
        if (current.isEmpty()) { main(player); return; }
        Holder holder = inventory(player, Page.OPERATIONS, 0, null, 0, "挑战操作");
        holder.inventory.setItem(INFO, icon(Material.CLOCK, "<#ffd166>当前挑战",
                "<#aab8b0>难度：<#ffffff>" + escape(current.get().difficulty()),
                "<#aab8b0>状态：<#ffffff>" + (current.get().status().equals("BATTLING") ? "出战中" : "准备中"),
                "<#aab8b0>剩余时间：<#ffffff>" + duration(Math.max(0, current.get().expiresAt() - System.currentTimeMillis()))));
        holder.inventory.setItem(10, icon(Material.ENDER_PEARL, "<#ffd166>继续挑战"));
        holder.inventory.setItem(12, icon(Material.OAK_DOOR, "<#ffd166>退出本次出战", "<#aab8b0>保留本次进度"));
        holder.inventory.setItem(14, icon(Material.CLOCK, "<#ffd166>重置本次出战", "<#aab8b0>回退至上次保存的进度"));
        holder.inventory.setItem(16, icon(Material.BARRIER, "<#ffb4a8>放弃挑战", "<#aab8b0>结束挑战并消耗本张票"));
        holder.inventory.setItem(SMALL_BACK, icon(Material.ARROW, "<#b8ffde>返回总力战"));
        player.openInventory(holder.inventory);
    }

    private static void ranking(Player player, int requestedPage) throws SQLException {
        List<TotalAssaultStorage.RankingEntry> entries = Datamanager.ranking(activity.id());
        int pages = pageCount(entries.size());
        int page = Math.max(0, Math.min(requestedPage, pages - 1));
        Holder holder = inventory(player, Page.RANKING, page, null, 0, "本期总力战排行榜");
        holder.inventory.setItem(INFO, icon(Material.GOLD_INGOT, "<#ffd166>本期排行榜", "<#aab8b0>按最佳得分从高到低排列", "<#aab8b0>点击玩家查看出战记录"));
        int start = page * CONTENT.length;
        for (int i = start; i < Math.min(start + CONTENT.length, entries.size()); i++) {
            var entry = entries.get(i);
            String name = Bukkit.getOfflinePlayer(entry.player()).getName();
            if (name == null) name = entry.player().toString();
            holder.actions.add(entry.player().toString());
            holder.inventory.setItem(CONTENT[i - start], icon(Material.PLAYER_HEAD,
                    "<#ffd166>第 " + (i + 1) + " 名 · " + escape(name),
                    "<#aab8b0>难度：<#ffffff>" + escape(entry.difficulty().toUpperCase(Locale.ROOT)),
                    "<#aab8b0>得分：<#ffffff>" + entry.score(), "<#aab8b0>用时：<#ffffff>" + duration(entry.battleMillis()),
                    "<#b8ffde>» 点击查看出战记录"));
        }
        holder.inventory.setItem(BACK, icon(Material.ARROW, "<#b8ffde>返回出战记录"));
        pagination(holder, page, pages);
        player.openInventory(holder.inventory);
    }

    private static void attemptList(Player player, int requestedPage, UUID subject, int rankingPage) throws SQLException {
        UUID target = subject == null ? player.getUniqueId() : subject;
        var score = Datamanager.bestScore(target, activity.id());
        if (score.isEmpty()) { ranking(player, Math.max(0, rankingPage)); return; }
        List<TotalAssaultStorage.Attempt> saved = Datamanager.attempts(score.get().challengeId()).stream()
                .filter(attempt -> attempt.status().equals("SAVED")).toList();
        int pages = pageCount(saved.size());
        int page = Math.max(0, Math.min(requestedPage, pages - 1));
        Holder holder = inventory(player, Page.ATTEMPTS, page, target, rankingPage, "最佳成绩 · 出战记录");
        String name = Bukkit.getOfflinePlayer(target).getName();
        if (name == null) name = target.toString();
        holder.inventory.setItem(INFO, icon(Material.WRITABLE_BOOK, "<#ffd166>" + escape(name) + " 的最佳成绩",
                "<#aab8b0>难度：<#ffffff>" + escape(difficultyName(score.get().difficultyOrder())),
                "<#aab8b0>累计用时：<#ffffff>" + duration(score.get().battleMillis()),
                "<#aab8b0>完成时间：<#ffffff>" + DATE.format(Instant.ofEpochMilli(score.get().achievedAt()))));
        int start = page * CONTENT.length;
        for (int i = start; i < Math.min(start + CONTENT.length, saved.size()); i++) {
            var attempt = saved.get(i);
            holder.actions.add(attempt.id());
            holder.inventory.setItem(CONTENT[i - start], icon(Material.PAPER, "<#ffd166>第 " + attempt.number() + " 次出战",
                    "<#aab8b0>用时：<#ffffff>" + duration(attempt.battleMillis()),
                    "<#aab8b0>Boss 剩余血量：<#ffffff>" + attempt.bossHealthAfter(), "<#b8ffde>» 点击查看进场物品"));
        }
        holder.inventory.setItem(BACK, icon(Material.ARROW, rankingPage < 0 ? "<#b8ffde>返回总力战" : "<#b8ffde>返回排行榜"));
        holder.inventory.setItem(RANKING, icon(Material.GOLD_INGOT, "<#ffd166>排行榜", "<#b8ffde>» 点击查看其他玩家成绩"));
        pagination(holder, page, pages);
        player.openInventory(holder.inventory);
    }

    private static void attemptItems(Player player, UUID subject, int attemptPage, int rankingPage, String attemptId) throws SQLException {
        if (subject == null || attemptId == null) { main(player); return; }
        // 重新检查目标玩家当前最佳成绩，避免使用过期的菜单打开其他挑战的记录。
        var score = Datamanager.bestScore(subject, activity.id());
        if (score.isEmpty() || Datamanager.attempts(score.get().challengeId()).stream()
                .noneMatch(a -> a.id().equals(attemptId) && a.status().equals("SAVED"))) {
            attemptList(player, 0, subject, rankingPage); return;
        }
        Holder holder = inventory(player, Page.ITEMS, attemptPage, subject, rankingPage, "出战 · 进场物品");
        holder.inventory.setItem(INFO, icon(Material.CHEST, "<#ffd166>进场物品快照", "<#aab8b0>显示进场时的物品和数量"));
        String[] names = {"HEAD", "CHEST", "LEGS", "FEET", "MAIN_HAND", "OFF_HAND", "HOTBAR_2", "HOTBAR_3", "HOTBAR_4", "HOTBAR_5"};
        String[] labels = {"头盔", "胸甲", "护腿", "靴子", "主手", "副手", "快捷栏第 2 格", "快捷栏第 3 格", "快捷栏第 4 格", "快捷栏第 5 格"};
        // 与示意图一致：护甲在左侧竖排，主副手在中间，四个辅助槽在右侧竖排。
        int[] slots = {10, 19, 28, 37, 12, 39, 15, 24, 33, 42};
        for (int i = 0; i < names.length; i++) holder.inventory.setItem(slots[i], icon(Material.GRAY_STAINED_GLASS_PANE, "<#71877a>" + labels[i] + "：空"));
        for (TotalAssaultStorage.AttemptItem item : Datamanager.attemptItems(attemptId)) {
            for (int i = 0; i < names.length; i++) {
                if (!names[i].equals(item.slot())) continue;
                try {
                    ItemStack display = ItemStack.deserializeBytes(item.snapshot());
                    ItemMeta meta = display.getItemMeta();
                    if (meta != null) {
                        List<Component> lore = meta.lore() == null ? new ArrayList<>() : new ArrayList<>(meta.lore());
                        lore.add(line("<#71877a>进场位置：" + labels[i]));
                        meta.lore(lore); display.setItemMeta(meta);
                    }
                    holder.inventory.setItem(slots[i], display);
                } catch (RuntimeException exception) {
                    plugin.getLogger().log(Level.WARNING, "无法读取总力战物品快照", exception);
                    holder.inventory.setItem(slots[i], icon(Material.BARRIER, "<#ffb4a8>" + labels[i] + "：无法读取"));
                }
                break;
            }
        }
        holder.inventory.setItem(BACK, icon(Material.ARROW, "<#b8ffde>返回出战记录"));
        player.openInventory(holder.inventory);
    }

    private static void pagination(Holder holder, int page, int pages) {
        if (page > 0) holder.inventory.setItem(PREVIOUS, icon(Material.ARROW, "<#b8ffde>上一页"));
        if (page + 1 < pages) holder.inventory.setItem(NEXT, icon(Material.ARROW, "<#b8ffde>下一页"));
        holder.inventory.setItem(47, icon(Material.MAP, "<#aab8b0>第 " + (page + 1) + " / " + pages + " 页"));
    }

    static void click(InventoryClickEvent event) {
        if (!(event.getView().getTopInventory().getHolder() instanceof Holder holder)) return;
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player) || !holder.player.equals(player.getUniqueId())) return;
        int slot = event.getRawSlot();
        if (slot < 0 || slot >= holder.inventory.getSize()) return;
        if (holder.page == Page.MAIN && slot >= 10 && slot <= 16
                && (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT)) {
            int index = slot - 10;
            if (index >= holder.actions.size()) return;
            String difficulty = holder.actions.get(index);
            var definition = bosses.find(activity.bossId());
            if (definition.isEmpty()) return;
            int count = definition.get().skillInfo(difficulty).size();
            if (count == 0) return;
            int delta = event.getClick() == ClickType.SHIFT_LEFT ? 1 : -1;
            int page = Math.floorMod(holder.skillPages.getOrDefault(difficulty, 0) + delta, count);
            holder.skillPages.put(difficulty, page);
            ItemStack previous = holder.inventory.getItem(slot);
            holder.inventory.setItem(slot, difficultyIcon(difficulty, previous != null && previous.getType() == Material.IRON_SWORD, page));
            return;
        }
        if (event.getClick() != ClickType.LEFT) return;
        boolean small = holder.page == Page.MAIN || holder.page == Page.OPERATIONS;
        if (slot == (small ? SMALL_CLOSE : CLOSE)) { later(player, player::closeInventory); return; }
        if (small && slot == SMALL_BACK && holder.page == Page.OPERATIONS) { later(player, () -> show(player, Page.MAIN, 0, null, 0, null)); return; }
        if (!small && slot == BACK) {
            if (holder.page == Page.ITEMS) later(player, () -> show(player, Page.ATTEMPTS, holder.pageNumber, holder.subject, holder.returnPage, null));
            else if (holder.page == Page.ATTEMPTS && holder.returnPage >= 0) later(player, () -> show(player, Page.RANKING, holder.returnPage, null, 0, null));
            else if (holder.page == Page.RANKING) later(player, () -> {
                try {
                    if (Datamanager.bestScore(player.getUniqueId(), activity.id()).isPresent()) attemptList(player, 0, player.getUniqueId(), -1);
                    else main(player);
                } catch (SQLException exception) { report(player, exception); }
            });
            else later(player, () -> show(player, Page.MAIN, 0, null, 0, null));
            return;
        }
        if (!small && (slot == PREVIOUS || slot == NEXT)) {
            ItemStack item = holder.inventory.getItem(slot);
            if (item != null && item.getType() == Material.ARROW) {
                int page = holder.pageNumber + (slot == PREVIOUS ? -1 : 1);
                later(player, () -> show(player, holder.page, page, holder.subject, holder.returnPage, null));
            }
            return;
        }
        if (holder.page == Page.MAIN && slot == SMALL_RECORD) { later(player, () -> show(player, Page.ATTEMPTS, 0, player.getUniqueId(), -1, null)); return; }
        if (holder.page == Page.MAIN && slot == SMALL_CONTROL) { later(player, () -> show(player, Page.OPERATIONS, 0, null, 0, null)); return; }
        if (holder.page == Page.ATTEMPTS && slot == RANKING) { later(player, () -> show(player, Page.RANKING, Math.max(0, holder.returnPage), null, 0, null)); return; }
        if (holder.page == Page.OPERATIONS && (slot == 10 || slot == 12 || slot == 14 || slot == 16)) {
            later(player, () -> {
                player.closeInventory();
                switch (slot) {
                    case 10 -> battleManager.enter(player);
                    case 12 -> battleManager.exit(player);
                    case 14 -> battleManager.reset(player);
                    case 16 -> battleManager.giveUp(player);
                    default -> { }
                }
            });
            return;
        }
        if (holder.page == Page.MAIN && slot >= 10 && slot <= 16) {
            int i = slot - 10;
            if (i < holder.actions.size()) later(player, () -> challengeCreator.start(player, holder.actions.get(i)));
            return;
        }
        for (int i = 0; i < CONTENT.length && i < holder.actions.size(); i++) {
            if (slot != CONTENT[i]) continue;
            String action = holder.actions.get(i);
            if (holder.page == Page.ATTEMPTS) later(player, () -> show(player, Page.ITEMS, holder.pageNumber, holder.subject, holder.returnPage, action));
            else if (holder.page == Page.RANKING) later(player, () -> show(player, Page.ATTEMPTS, 0, UUID.fromString(action), holder.pageNumber, null));
            return;
        }
    }

    static void drag(InventoryDragEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof Holder holder
                && event.getRawSlots().stream().anyMatch(slot -> slot < holder.inventory.getSize())) event.setCancelled(true);
    }
    private static void later(Player player, Runnable action) { Bukkit.getScheduler().runTask(plugin, () -> { if (player.isOnline()) action.run(); }); }
    private static int pageCount(int count) { return Math.max(1, (count + CONTENT.length - 1) / CONTENT.length); }
    private static String difficultyName(int order) { return order >= 0 && order < activity.difficulties().size() ? activity.difficulties().get(order) : "未知难度"; }
    private static String duration(long millis) {
        long value = Math.max(0, millis), seconds = value / 1000;
        return String.format(Locale.ROOT, "%02d:%02d:%02d.%03d", seconds / 3600, seconds / 60 % 60, seconds % 60, value % 1000);
    }
    private static String escape(String text) { return MINI.escapeTags(text); }
    private static Component line(String text) { return MINI.deserialize("<!i>" + text); }
    private static ItemStack icon(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material); ItemMeta meta = item.getItemMeta();
        meta.displayName(line(name)); meta.lore(java.util.Arrays.stream(lore).map(tamenu::line).toList()); item.setItemMeta(meta);
        return item;
    }
    public static void shutdown() {
        for (Player player : Bukkit.getOnlinePlayers()) if (player.getOpenInventory().getTopInventory().getHolder() instanceof Holder) player.closeInventory();
        activity = null; challengeCreator = null; battleManager = null; bosses = null; plugin = null;
    }
}
