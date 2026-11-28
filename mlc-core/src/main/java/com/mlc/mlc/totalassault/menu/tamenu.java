package com.mlc.mlc.totalassault.menu;

import com.mlc.mlc.totalassault.data.Datamanager;
import com.mlc.mlc.totalassault.data.Filereader;
import com.mlc.mlc.totalassault.data.TotalAssaultStorage;
import com.mlc.mlc.totalassault.ChallengeCreator;
import com.mlc.mlc.totalassault.battle.BattleManager;
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
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

/** 总力战菜单。每次打开都为玩家创建独立容器。 */
public final class tamenu {
    private static final MiniMessage MINI = MiniMessage.miniMessage();
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            .withZone(ZoneId.of("Asia/Shanghai"));
    private static final int SIZE = 54;
    private static final int[] CONTENT = {
            10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43
    };
    private static final int BACK = 45, PREVIOUS = 46, INFO = 4, STATUS = 48;
    private static final int RECORD = 49, CONTROL = 50, NEXT = 52, CLOSE = 53;
    public static JavaPlugin plugin;
    private static Filereader.ActivityConfig activity;
    private static ChallengeCreator challengeCreator;
    private static BattleManager battleManager;

    private enum Page { MAIN, ATTEMPTS, ITEMS, OPERATIONS }

    private static final class Holder implements InventoryHolder {
        private final UUID player;
        private final Page page;
        private final int pageNumber;
        private final List<String> actions = new ArrayList<>();
        private Inventory inventory;

        private Holder(UUID player, Page page, int pageNumber) {
            this.player = player;
            this.page = page;
            this.pageNumber = pageNumber;
        }
        @Override public @NotNull Inventory getInventory() { return inventory; }
    }

    private tamenu() {}

    public static void init(JavaPlugin owner, Filereader.ActivityConfig config, ChallengeCreator creator, BattleManager manager) {
        plugin = owner;
        activity = config;
        challengeCreator = creator;
        battleManager = manager;
        owner.getServer().getPluginManager().registerEvents(new Listener(), owner);
    }

    public static void open(Player player) {
        show(player, Page.MAIN, 0, null);
    }

    private static void show(Player player, Page page, int requestedPage, String attemptId) {
        try {
            switch (page) {
                case MAIN -> main(player, requestedPage);
                case ATTEMPTS -> attemptList(player, requestedPage);
                case ITEMS -> attemptItems(player, attemptId);
                case OPERATIONS -> operations(player);
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "读取总力战菜单数据失败", e);
            player.sendMessage(line("<#ffb4a8>总力战数据暂时无法读取，请稍后再试。"));
        }
    }

    private static Holder inventory(Player player, Page page, int pageNumber, String title) {
        Holder holder = new Holder(player.getUniqueId(), page, pageNumber);
        holder.inventory = Bukkit.createInventory(holder, SIZE, line("<#b8ffde>" + title));
        ItemStack border = icon(Material.BLACK_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 9; i++) {
            holder.inventory.setItem(i, border);
            holder.inventory.setItem(45 + i, border);
        }
        for (int row = 1; row < 5; row++) {
            holder.inventory.setItem(row * 9, border);
            holder.inventory.setItem(row * 9 + 8, border);
        }
        holder.inventory.setItem(CLOSE, icon(Material.BARRIER, "<#ffb4a8>关闭"));
        return holder;
    }

    private static void main(Player player, int requestedPage) throws SQLException {
        Instant now = Instant.now();
        Instant end = activity.start().plusSeconds(7L * 24 * 60 * 60);
        boolean open = !now.isBefore(activity.start()) && now.isBefore(end);
        int pages = pageCount(activity.difficulties().size());
        int page = Math.max(0, Math.min(requestedPage, pages - 1));
        var current = Datamanager.findActiveChallenge(player.getUniqueId());
        var score = Datamanager.bestScore(player.getUniqueId(), activity.id());
        int unlocked = Datamanager.highestClearedDifficulty(player.getUniqueId(), activity.id()) + 1;
        Integer tickets = open ? Datamanager.remainingTickets(player.getUniqueId(), activity.id(), now) : null;

        Holder holder = inventory(player, Page.MAIN, page, "总力战");
        holder.inventory.setItem(INFO, icon(Material.NETHER_STAR, "<#b8ffde>" + escape(activity.name()),
                "<#aab8b0>Boss：<#ffffff>" + escape(activity.bossId()),
                "<#aab8b0>活动：" + (open ? "<#b8ffde>进行中" : now.isBefore(activity.start()) ? "<#ffd166>尚未开始" : "<#ffb4a8>已结束"),
                "<#aab8b0>开始：<#ffffff>" + DATE.format(activity.start()) + " 北京时间",
                "<#aab8b0>结束：<#ffffff>" + DATE.format(end) + " 北京时间"));
        holder.inventory.setItem(STATUS, icon(Material.PAPER, "<#ffd166>挑战票",
                tickets == null ? "<#aab8b0>活动未开放" : "<#aab8b0>今日剩余：<#ffffff>" + tickets,
                "<#71877a>每日北京时间 04:00 刷新"));
        if (current.isPresent()) {
            TotalAssaultStorage.Challenge c = current.get();
            long remaining = Math.max(0, Duration.between(now, Instant.ofEpochMilli(c.expiresAt())).getSeconds());
            holder.inventory.setItem(CONTROL, icon(Material.CLOCK, "<#ffd166>当前挑战",
                    "<#aab8b0>难度：<#ffffff>" + escape(c.difficulty()),
                    "<#aab8b0>状态：<#ffffff>" + (c.status().equals("BATTLING") ? "出战中" : "准备中"),
                    "<#aab8b0>剩余时间：<#ffffff>" + duration(remaining * 1000),
                    "<#b8ffde>» 点击查看挑战操作"));
        } else {
            holder.inventory.setItem(CONTROL, icon(Material.CLOCK, "<#aab8b0>当前没有挑战",
                    "<#71877a>选择已解锁的难度创建挑战"));
        }
        holder.inventory.setItem(RECORD, score.isPresent()
                ? icon(Material.WRITABLE_BOOK, "<#ffd166>本期最佳成绩",
                    "<#aab8b0>难度：<#ffffff>" + escape(difficultyName(score.get().difficultyOrder())),
                    "<#aab8b0>用时：<#ffffff>" + duration(score.get().battleMillis()),
                    "<#b8ffde>» 点击查看出战记录")
                : icon(Material.BOOK, "<#aab8b0>本期最佳成绩", "<#71877a>尚无通关成绩"));
        int start = page * CONTENT.length;
        for (int index = start; index < Math.min(start + CONTENT.length, activity.difficulties().size()); index++) {
            String difficulty = activity.difficulties().get(index);
            boolean available = index <= unlocked;
            holder.inventory.setItem(CONTENT[index - start], icon(available ? Material.IRON_SWORD : Material.GRAY_DYE,
                    (available ? "<#ffd166>" : "<#71877a>") + escape(difficulty),
                    available ? "<#b8ffde>已解锁" : "<#ffb4a8>通关上一难度后解锁",
                    available ? "<#b8ffde>» 点击准备挑战" : "<#71877a>该难度尚未解锁"));
            holder.actions.add(difficulty);
        }
        pagination(holder, page, pages);
        player.openInventory(holder.inventory);
    }

    private static void operations(Player player) throws SQLException {
        var current = Datamanager.findActiveChallenge(player.getUniqueId());
        if (current.isEmpty()) { main(player, 0); return; }
        Holder holder = inventory(player, Page.OPERATIONS, 0, "挑战操作");
        holder.inventory.setItem(INFO, icon(Material.CLOCK, "<#ffd166>当前挑战",
                "<#aab8b0>难度：<#ffffff>" + escape(current.get().difficulty()),
                "<#aab8b0>状态：<#ffffff>" + (current.get().status().equals("BATTLING") ? "出战中" : "准备中")));
        holder.inventory.setItem(19, icon(Material.ENDER_PEARL, "<#ffd166>继续挑战"));
        holder.inventory.setItem(21, icon(Material.OAK_DOOR, "<#ffd166>退出本次出战", "<#aab8b0>保留本次进度"));
        holder.inventory.setItem(23, icon(Material.CLOCK, "<#ffd166>重置本次出战", "<#aab8b0>回退至上次保存的进度"));
        holder.inventory.setItem(25, icon(Material.BARRIER, "<#ffb4a8>放弃挑战", "<#aab8b0>结束挑战并消耗本张票"));
        holder.inventory.setItem(BACK, icon(Material.ARROW, "<#b8ffde>返回总力战"));
        player.openInventory(holder.inventory);
    }

    private static void attemptList(Player player, int requestedPage) throws SQLException {
        var score = Datamanager.bestScore(player.getUniqueId(), activity.id());
        if (score.isEmpty()) { main(player, 0); return; }
        List<TotalAssaultStorage.Attempt> saved = Datamanager.attempts(score.get().challengeId()).stream()
                .filter(attempt -> attempt.status().equals("SAVED")).toList();
        int pages = pageCount(saved.size());
        int page = Math.max(0, Math.min(requestedPage, pages - 1));
        Holder holder = inventory(player, Page.ATTEMPTS, page, "最佳成绩 · 出战记录");
        holder.inventory.setItem(INFO, icon(Material.WRITABLE_BOOK, "<#ffd166>本期最佳成绩",
                "<#aab8b0>难度：<#ffffff>" + escape(difficultyName(score.get().difficultyOrder())),
                "<#aab8b0>累计用时：<#ffffff>" + duration(score.get().battleMillis()),
                "<#aab8b0>完成时间：<#ffffff>" + DATE.format(Instant.ofEpochMilli(score.get().achievedAt()))));
        int start = page * CONTENT.length;
        for (int index = start; index < Math.min(start + CONTENT.length, saved.size()); index++) {
            TotalAssaultStorage.Attempt attempt = saved.get(index);
            holder.actions.add(attempt.id());
            holder.inventory.setItem(CONTENT[index - start], icon(Material.PAPER,
                    "<#ffd166>第 " + attempt.number() + " 次出战",
                    "<#aab8b0>用时：<#ffffff>" + duration(attempt.battleMillis()),
                    "<#aab8b0>Boss 剩余血量：<#ffffff>" + attempt.bossHealthAfter(),
                    "<#b8ffde>» 点击查看进场物品"));
        }
        holder.inventory.setItem(BACK, icon(Material.ARROW, "<#b8ffde>返回总力战"));
        pagination(holder, page, pages);
        player.openInventory(holder.inventory);
    }

    private static void attemptItems(Player player, String attemptId) throws SQLException {
        // 只允许查看当前玩家本期最佳成绩中已保存的出战。
        var score = Datamanager.bestScore(player.getUniqueId(), activity.id());
        if (score.isEmpty() || Datamanager.attempts(score.get().challengeId()).stream()
                .noneMatch(a -> a.id().equals(attemptId) && a.status().equals("SAVED"))) {
            main(player, 0);
            return;
        }
        Holder holder = inventory(player, Page.ITEMS, 0, "出战 · 进场物品");
        holder.inventory.setItem(INFO, icon(Material.CHEST, "<#ffd166>进场物品快照",
                "<#aab8b0>显示进入本次出战时的物品和数量"));
        String[] names = {"HEAD", "CHEST", "LEGS", "FEET", "MAIN_HAND", "OFF_HAND", "HOTBAR_2", "HOTBAR_3"};
        String[] labels = {"头盔", "胸甲", "护腿", "靴子", "主手", "副手", "快捷栏第 2 格", "快捷栏第 3 格"};
        int[] slots = {10, 11, 12, 13, 19, 20, 21, 22};
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
                        meta.lore(lore);
                        display.setItemMeta(meta);
                    }
                    holder.inventory.setItem(slots[i], display);
                } catch (RuntimeException e) {
                    plugin.getLogger().log(Level.WARNING, "无法读取总力战物品快照", e);
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
        if (!(event.getWhoClicked() instanceof Player player) || !holder.player.equals(player.getUniqueId())
                || event.getClick() != ClickType.LEFT) return;
        int slot = event.getRawSlot();
        if (slot < 0 || slot >= SIZE) return;
        if (slot == CLOSE) { later(player, player::closeInventory); return; }
        if (slot == BACK && holder.page != Page.MAIN) {
            later(player, () -> show(player, holder.page == Page.ITEMS ? Page.ATTEMPTS : Page.MAIN, 0, null));
            return;
        }
        if (slot == PREVIOUS && holder.pageNumber > 0) {
            later(player, () -> show(player, holder.page, holder.pageNumber - 1, null));
            return;
        }
        if (slot == NEXT && holder.inventory.getItem(NEXT) != null && holder.inventory.getItem(NEXT).getType() == Material.ARROW) {
            later(player, () -> show(player, holder.page, holder.pageNumber + 1, null));
            return;
        }
        if (slot == RECORD && holder.page == Page.MAIN) {
            later(player, () -> show(player, Page.ATTEMPTS, 0, null));
            return;
        }
        if (slot == CONTROL && holder.page == Page.MAIN) {
            later(player, () -> show(player, Page.OPERATIONS, 0, null));
            return;
        }
        if (holder.page == Page.OPERATIONS && (slot == 19 || slot == 21 || slot == 23 || slot == 25)) {
            later(player, () -> {
                player.closeInventory();
                switch (slot) {
                    case 19 -> battleManager.enter(player);
                    case 21 -> battleManager.exit(player);
                    case 23 -> battleManager.reset(player);
                    case 25 -> battleManager.giveUp(player);
                    default -> { }
                }
            });
            return;
        }
        for (int i = 0; i < CONTENT.length && i < holder.actions.size(); i++) {
            if (slot != CONTENT[i]) continue;
            if (holder.page == Page.ATTEMPTS) {
                String attemptId = holder.actions.get(i);
                later(player, () -> show(player, Page.ITEMS, 0, attemptId));
            } else if (holder.page == Page.MAIN) {
                String difficulty = holder.actions.get(i);
                later(player, () -> challengeCreator.start(player, difficulty));
            }
            return;
        }
    }

    static void drag(InventoryDragEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof Holder
                && event.getRawSlots().stream().anyMatch(slot -> slot < SIZE)) event.setCancelled(true);
    }

    private static void later(Player player, Runnable action) {
        Bukkit.getScheduler().runTask(plugin, () -> { if (player.isOnline()) action.run(); });
    }
    private static int pageCount(int count) { return Math.max(1, (count + CONTENT.length - 1) / CONTENT.length); }
    private static String difficultyName(int order) {
        return order >= 0 && order < activity.difficulties().size() ? activity.difficulties().get(order) : "未知难度";
    }
    private static String duration(long millis) {
        long seconds = Math.max(0, millis) / 1000;
        return String.format("%02d:%02d:%02d.%03d", seconds / 3600, seconds / 60 % 60, seconds % 60, Math.max(0, millis) % 1000);
    }
    private static String escape(String text) { return MINI.escapeTags(text); }
    private static Component line(String text) { return MINI.deserialize("<!i>" + text); }
    private static ItemStack icon(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(line(name));
        meta.lore(java.util.Arrays.stream(lore).map(tamenu::line).toList());
        item.setItemMeta(meta);
        return item;
    }

    public static void shutdown() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getOpenInventory().getTopInventory().getHolder() instanceof Holder) player.closeInventory();
        }
        activity = null;
        challengeCreator = null;
        battleManager = null;
        plugin = null;
    }
}
