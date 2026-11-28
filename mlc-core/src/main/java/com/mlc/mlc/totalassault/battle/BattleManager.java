package com.mlc.mlc.totalassault.battle;

import com.mlc.mlc.totalassault.boss.AssaultBoss;
import com.mlc.mlc.totalassault.boss.BossHandle;
import com.mlc.mlc.totalassault.boss.BossRegistry;
import com.mlc.mlc.totalassault.boss.saint.Saint;
import com.mlc.mlc.totalassault.boss.saint.SaintDifficulty;
import com.destroystokyo.paper.event.player.PlayerStopSpectatingEntityEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import com.mlc.mlc.totalassault.TaText;
import com.mlc.mlc.totalassault.data.Datamanager;
import com.mlc.mlc.totalassault.data.Filereader;
import com.mlc.mlc.totalassault.data.TotalAssaultStorage;
import com.mlc.mlc.totalassault.data.difficulties;
import com.mlc.mlc.totalassault.menu.tamenu;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.sql.SQLException;
import java.time.Instant;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.List;
import java.util.logging.Level;

/** 管理单次出战、计时以及退出时的保存和回退。 */
public final class BattleManager implements Listener {
    private final JavaPlugin plugin;
    private final Filereader.ActivityConfig activity;
    private final BossRegistry bosses;
    private final NamespacedKey controlKey;
    private final Map<UUID, Battle> battles = new HashMap<>();
    private final Set<UUID> internalTeleports = new java.util.HashSet<>();
    private final Set<UUID> returnToLobby = new java.util.HashSet<>();
    private final Map<UUID, Intro> intros = new HashMap<>();
    private final Set<UUID> internalCameraChanges = new java.util.HashSet<>();

    private record Battle(TotalAssaultStorage.Challenge challenge, String attemptId, BossHandle boss,
                          long startNanos, GameMode originalMode, int originalMaximumNoDamageTicks,
                          Set<String> identities) {}

    /** 入场镜头只存在于本次出战内，不参与持久化。 */
    private static final class Intro {
        final ItemDisplay camera;
        final Location playerSpawn;
        final Saint.Progress progress;
        final List<SaintDifficulty.CameraPoint> points;
        BukkitTask task;
        int segment;
        int tick;
        int titleTicks;
        boolean attached;
        Intro(ItemDisplay camera, Location playerSpawn, Saint.Progress progress, List<SaintDifficulty.CameraPoint> points) {
            this.camera = camera; this.playerSpawn = playerSpawn; this.progress = progress; this.points = points;
        }
    }

    public BattleManager(JavaPlugin plugin, Filereader.ActivityConfig activity, BossRegistry bosses) {
        this.plugin = plugin;
        this.activity = activity;
        this.bosses = bosses;
        this.controlKey = new NamespacedKey(plugin, "totalassault_control");
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /** 从准备状态进入一次出战；失败时回退数据库及玩家状态。 */
    public void enter(Player player) {
        if (battles.containsKey(player.getUniqueId())) { player.sendMessage(TaText.line("<#b8ffde>你正在出战。")); return; }
        String attemptId = UUID.randomUUID().toString();
        TotalAssaultStorage.Challenge challenge = null;
        BossHandle boss = null;
        GameMode original = player.getGameMode();
        int originalMaximumNoDamageTicks = player.getMaximumNoDamageTicks();
        boolean started = false;
        boolean moved = false;
        try {
            challenge = Datamanager.findActiveChallenge(player.getUniqueId()).orElse(null);
            if (challenge == null || !challenge.status().equals("PREPARING")) { player.sendMessage(TaText.line("<#b8ffde>当前没有可以进入的挑战。")); return; }
            // 不能用当前活动的 Boss 接管其他活动遗留的挑战。
            if (!challenge.activityId().equals(activity.id())) { player.sendMessage(TaText.line("<#ffb4a8>当前挑战不属于本期活动。")); return; }
            AssaultBoss definition = bosses.find(activity.bossId()).orElse(null);
            if (definition == null) { player.sendMessage(TaText.line("<#ffb4a8>本期 Boss 尚未实现或配置有误，无法进入挑战。")); return; }
            World world = Bukkit.getWorld(challenge.worldName());
            if (world == null) { player.sendMessage(TaText.line("<#b8ffde>挑战世界未加载，请联系管理员。")); return; }
            Set<String> savedAttempts = new java.util.HashSet<>();
            for (TotalAssaultStorage.Attempt attempt : Datamanager.attempts(challenge.id())) {
                if ("SAVED".equals(attempt.status())) savedAttempts.add(attempt.id());
            }
            EquipmentCheck.Result result = EquipmentCheck.check(player, Datamanager.usedItemIdentities(challenge.id()), savedAttempts, definition, plugin);
            if (!result.errors().isEmpty()) {
                player.sendMessage(TaText.line("<#b8ffde>无法进入挑战，请处理以下问题："));
                result.errors().forEach(error -> player.sendMessage(TaText.line("<#ffb4a8>" + TaText.escape(error))));
                return;
            }
            Filereader.ArenaConfig arena = Filereader.ArenaConfig.load(plugin);
            Datamanager.startAttempt(challenge.id(), attemptId, result.items(), Instant.now());
            started = true;
            // 标记指向本次出战；如果之后回退，数据库状态不会变为已保存，标记也不会造成禁用。
            EquipmentCheck.markEquipment(player, plugin, attemptId);
            player.setGameMode(GameMode.ADVENTURE);
            // 出战期间将受伤后的无敌时间设为零，避免连续命中被原版冷却抵消。
            player.setMaximumNoDamageTicks(0);
            player.setNoDamageTicks(0);
            if (!teleport(player, arena.playerSpawn().location(world))) throw new IllegalStateException("传送失败");
            moved = true;
            boss = definition.spawn(arena.bossSpawn().location(world), challenge.difficulty(), challenge.bossHealth());
            Saint.Progress progress = definition.id().equals("saint")
                    ? Datamanager.saintProgress(challenge.id()) : new Saint.Progress(0, 0);
            player.getInventory().setItem(8, controlItem());
            boolean hasIntro = definition.id().equals("saint") && SaintDifficulty.INTRO_POINTS.size() >= 2;
            long start = hasIntro ? 0 : System.nanoTime();
            battles.put(player.getUniqueId(), new Battle(challenge, attemptId, boss, start, original,
                    originalMaximumNoDamageTicks, Set.copyOf(result.identities())));
            if (hasIntro) {
                beginIntro(player, world, arena.playerSpawn().location(world), progress);
            } else {
                boss.start(plugin, player, progress.ultimate(), progress.stun());
                player.sendMessage(TaText.line("<#b8ffde>出战开始，单次出战限时四分钟。"));
            }
        } catch (SQLException | RuntimeException exception) {
            clearIntro(player);
            battles.remove(player.getUniqueId());
            if (boss != null) boss.remove();
            player.getInventory().setItem(8, null);
            player.setGameMode(original);
            player.setMaximumNoDamageTicks(originalMaximumNoDamageTicks);
            if (moved) { try { teleport(player, Filereader.ArenaConfig.lobbySpawn()); } catch (RuntimeException ignored) { } }
            if (started && challenge != null) {
                try { Datamanager.rollbackAttempt(challenge.id(), Instant.now()); }
                catch (SQLException rollback) { exception.addSuppressed(rollback); }
            }
            plugin.getLogger().log(Level.SEVERE, "进入总力战出战失败", exception);
            player.sendMessage(TaText.line("<#b8ffde>无法进入出战，请稍后重试。"));
        }
    }

    /** 保存、回退、通关或放弃，数据库成功更新后再清理现场。 */
    private void finish(Player player, String action, boolean move) {
        Battle battle = battles.get(player.getUniqueId());
        if (battle == null) return;
        Instant now = Instant.now();
        if (!now.isBefore(activity.start().plusSeconds(7L * 24 * 60 * 60)) || now.toEpochMilli() >= battle.challenge().expiresAt()) {
            endExpired(player, battle, now);
            return;
        }
        // 超过四分钟才击败 Boss 时，按超时保存剩余进度处理。
        if (action.equals("通关") && System.nanoTime() - battle.startNanos() >= 240_000_000_000L) action = "保存";
        if (battle.startNanos() == 0 && action.equals("保存")) action = "回退";
        long millis = battle.startNanos() == 0 ? 0 : Math.min(240_000L, Math.max(0, (System.nanoTime() - battle.startNanos()) / 1_000_000L));
        try {
            switch (action) {
                case "保存" -> {
                    double health = Math.max(0.01, battle.boss().health());
                    if (activity.bossId().equals("saint")) {
                        Saint.Progress progress = new Saint.Progress(battle.boss().ultimateProgress(), battle.boss().stunProgress());
                        Datamanager.saveSaintAttempt(battle.challenge().id(), battle.attemptId(), millis, health, progress, Instant.now());
                    } else {
                        Datamanager.saveAttempt(battle.challenge().id(), battle.attemptId(), millis, health, Instant.now());
                    }
                }
                case "回退" -> Datamanager.rollbackAttempt(battle.challenge().id(), Instant.now());
                case "通关" -> Datamanager.completeChallenge(battle.challenge().id(), battle.attemptId(), millis, Instant.now());
                case "放弃" -> Datamanager.failChallenge(battle.challenge().id(), "GIVE_UP", Instant.now());
                default -> throw new IllegalArgumentException("未知出战操作");
            }
        } catch (SQLException exception) {
            plugin.getLogger().log(Level.SEVERE, "保存总力战出战状态失败", exception);
            player.sendMessage(TaText.line("<#b8ffde>挑战状态保存失败，请联系管理员。"));
            return;
        }
        // 必须等通关记录提交成功后再通报，避免失败或回退的出战被误报。
        if (action.equals("通关")) broadcastCompletion(player, battle, millis);
        battles.remove(player.getUniqueId());
        clearIntro(player);
        battle.boss().remove();
        cleanInventory(player, battle.identities());
        player.setGameMode(battle.originalMode());
        player.setMaximumNoDamageTicks(battle.originalMaximumNoDamageTicks());
        if (move && !player.isDead()) {
            returnToLobby(player);
        }
        player.sendMessage(TaText.line("<#b8ffde>本次出战：" + TaText.escape(action)));
    }

    /** 通报本次挑战的累计战斗时间与四分钟 Boss 计分结果。 */
    private void broadcastCompletion(Player player, Battle battle, long finalBattleMillis) {
        TotalAssaultStorage.Challenge challenge = battle.challenge();
        long totalMillis = challenge.battleMillis() + finalBattleMillis;
        difficulties difficulty = difficulties.fromId(challenge.difficulty()).orElse(null);
        if (difficulty == null) {
            plugin.getLogger().warning("无法通报总力战成绩，未知难度：" + challenge.difficulty());
            return;
        }
        String bossName = bosses.find(activity.bossId()).map(AssaultBoss::displayName).orElse(activity.bossId());
        String time = String.format(java.util.Locale.ROOT, "%d:%02d.%03d",
                totalMillis / 60_000, totalMillis / 1000 % 60, totalMillis % 1000);
        Bukkit.broadcast(TaText.line("<b><#ffd166>[总力战] <#b8ffde>"
                + TaText.escape(player.getName()) + " 击败了 " + TaText.escape(bossName)
                + " <#aab8b0>（" + TaText.escape(challenge.difficulty().toUpperCase(java.util.Locale.ROOT)) + "）"
                + " <#b8ffde>用时 <#ffd166>" + time + " <#b8ffde>得分 <#ffd166>" + difficulty.score(totalMillis)));
    }

    /** 创建仅用于镜头的展示实体，玩家以旁观视角跟随它。 */
    private void beginIntro(Player player, World world, Location playerSpawn, Saint.Progress progress) {
        List<SaintDifficulty.CameraPoint> points = List.copyOf(SaintDifficulty.INTRO_POINTS);
        for (int i = 0; i < points.size() - 1; i++) {
            if (points.get(i).durationTicks() <= 0) throw new IllegalArgumentException("入场镜头每段移动时间必须大于零");
        }
        SaintDifficulty.CameraPoint first = points.getFirst();
        Location initial = new Location(world, first.x(), first.y(), first.z(), first.yaw(), first.pitch());
        ItemDisplay camera = world.spawn(initial, ItemDisplay.class, display -> {
            display.setPersistent(false);
            display.setTeleportDuration(1);
        });
        Intro intro = new Intro(camera, playerSpawn, progress, points);
        intros.put(player.getUniqueId(), intro);
        Battle battle = battles.get(player.getUniqueId());
        Entity bossEntity = Bukkit.getEntity(battle.boss().entityId());
        if (bossEntity instanceof LivingEntity living) living.setInvulnerable(true);
        internalCameraChanges.add(player.getUniqueId());
        internalTeleports.add(player.getUniqueId());
        try {
            player.setGameMode(GameMode.SPECTATOR);
            // 先把玩家送到镜头起点，确保客户端能接收新生成的展示实体。
            if (!player.teleport(initial)) throw new IllegalStateException("无法传送到入场镜头起点");
        } finally {
            internalTeleports.remove(player.getUniqueId());
            internalCameraChanges.remove(player.getUniqueId());
        }
        player.sendMessage(TaText.line("<#b8ffde>入场演出中，按 Shift 跳过。"));
        updateIntroTitle(player, intro);
        // 等待实体跟踪数据发送给客户端，再绑定旁观目标；过早绑定可能导致客户端仍停在原视角。
        intro.task = Bukkit.getScheduler().runTaskTimer(plugin, () -> advanceIntro(player, intro), 3L, 1L);
    }

    /** 使用平滑插值沿配置点位移动，并对朝向使用最短旋转路径。 */
    private void advanceIntro(Player player, Intro intro) {
        if (intros.get(player.getUniqueId()) != intro) return;
        if (!player.isOnline() || !intro.camera.isValid()) {
            finish(player, "回退", false);
            return;
        }
        try {
            if (!intro.attached) {
                internalCameraChanges.add(player.getUniqueId());
                internalTeleports.add(player.getUniqueId());
                try { player.setSpectatorTarget(intro.camera); }
                finally {
                    internalTeleports.remove(player.getUniqueId());
                    internalCameraChanges.remove(player.getUniqueId());
                }
                if (!intro.camera.equals(player.getSpectatorTarget())) throw new IllegalStateException("无法绑定入场镜头目标");
                intro.attached = true;
                return;
            }
            intro.titleTicks++;
            if (intro.titleTicks % SaintDifficulty.INTRO_TITLE_CHARACTER_TICKS == 0) updateIntroTitle(player, intro);
            SaintDifficulty.CameraPoint from = intro.points.get(intro.segment);
            SaintDifficulty.CameraPoint to = intro.points.get(intro.segment + 1);
            double t = Math.min(1.0, (double) ++intro.tick / from.durationTicks());
            double smooth = t * t * (3 - 2 * t);
            float yawDelta = (float) ((((to.yaw() - from.yaw()) % 360) + 540) % 360 - 180);
            Location next = new Location(intro.camera.getWorld(),
                    from.x() + (to.x() - from.x()) * smooth,
                    from.y() + (to.y() - from.y()) * smooth,
                    from.z() + (to.z() - from.z()) * smooth,
                    (float) (from.yaw() + yawDelta * smooth),
                    (float) (from.pitch() + (to.pitch() - from.pitch()) * smooth));
            if (!intro.camera.teleport(next)) throw new IllegalStateException("镜头实体移动失败");
            if (t >= 1) {
                intro.segment++;
                intro.tick = 0;
                if (intro.segment >= intro.points.size() - 1) completeIntro(player, intro);
            }
        } catch (RuntimeException exception) {
            plugin.getLogger().log(Level.WARNING, "总力战入场演出失败，回退出战", exception);
            finish(player, "回退", true);
        }
    }

    /** 按 Unicode 字符逐字显示主标题，完成后显示副标题。 */
    private void updateIntroTitle(Player player, Intro intro) {
        String text = SaintDifficulty.INTRO_TITLE;
        int total = text.codePointCount(0, text.length());
        int count = Math.min(total, 1 + intro.titleTicks / SaintDifficulty.INTRO_TITLE_CHARACTER_TICKS);
        String visible = text.substring(0, text.offsetByCodePoints(0, count));
        Component subtitle = count >= total ? TaText.line(SaintDifficulty.INTRO_SUBTITLE) : Component.empty();
        player.showTitle(Title.title(TaText.line("<bold><#d5b5ff>" + TaText.escape(visible)), subtitle,
                Title.Times.times(Duration.ZERO, Duration.ofSeconds(1), Duration.ZERO)));
    }

    /** 演出结束或跳过后恢复玩家视角，再开启 Boss 技能和出战计时。 */
    private void completeIntro(Player player, Intro intro) {
        UUID id = player.getUniqueId();
        if (intros.get(id) != intro) return;
        Battle battle = battles.get(id);
        if (battle == null) { clearIntro(player); return; }
        Instant now = Instant.now();
        if (!now.isBefore(activity.start().plusSeconds(7L * 24 * 60 * 60)) || now.toEpochMilli() >= battle.challenge().expiresAt()) {
            endExpired(player, battle, now);
            return;
        }
        clearIntro(player);
        try {
            player.setGameMode(GameMode.ADVENTURE);
            if (!teleport(player, intro.playerSpawn)) throw new IllegalStateException("入场演出结束后传送失败");
            Entity bossEntity = Bukkit.getEntity(battle.boss().entityId());
            if (!(bossEntity instanceof LivingEntity living) || !living.isValid()) throw new IllegalStateException("入场演出结束时 Boss 不存在");
            battle.boss().start(plugin, player, intro.progress.ultimate(), intro.progress.stun());
            living.setInvulnerable(false);
            battles.put(id, new Battle(battle.challenge(), battle.attemptId(), battle.boss(), System.nanoTime(),
                    battle.originalMode(), battle.originalMaximumNoDamageTicks(), battle.identities()));
            // 无论镜头自然结束还是玩家跳过，都在战斗真正开始时显示同一句台词。
            player.sendActionBar(TaText.line(SaintDifficulty.INTRO_DIALOGUE));
            player.sendMessage(TaText.line("<#b8ffde>出战开始，单次出战限时四分钟。"));
        } catch (RuntimeException exception) {
            plugin.getLogger().log(Level.WARNING, "入场演出结束后无法开始战斗", exception);
            finish(player, "回退", true);
        }
    }

    /** 清除镜头任务、旁观目标和展示实体；调用方负责恢复游戏模式和传送。 */
    private void clearIntro(Player player) {
        Intro intro = intros.remove(player.getUniqueId());
        if (intro == null) return;
        if (intro.task != null) intro.task.cancel();
        if (player.isOnline()) player.clearTitle();
        internalCameraChanges.add(player.getUniqueId());
        internalTeleports.add(player.getUniqueId());
        try {
            if (player.isOnline() && player.getGameMode() == GameMode.SPECTATOR && intro.camera.equals(player.getSpectatorTarget())) {
                player.setSpectatorTarget(null);
            }
        } finally {
            internalTeleports.remove(player.getUniqueId());
            internalCameraChanges.remove(player.getUniqueId());
            intro.camera.remove();
        }
    }

    private void skipIntro(Player player) {
        Intro intro = intros.get(player.getUniqueId());
        if (intro != null) Bukkit.getScheduler().runTask(plugin, () -> {
            if (player.isOnline() && intros.get(player.getUniqueId()) == intro) completeIntro(player, intro);
        });
    }

    public void exit(Player player) { finish(player, "保存", true); }
    public void reset(Player player) { finish(player, "回退", true); }

    public void giveUp(Player player) {
        if (battles.containsKey(player.getUniqueId())) { finish(player, "放弃", true); return; }
        try {
            var challenge = Datamanager.findActiveChallenge(player.getUniqueId());
            if (challenge.isEmpty()) { player.sendMessage(TaText.line("<#b8ffde>当前没有挑战。")); return; }
            Datamanager.failChallenge(challenge.get().id(), "GIVE_UP", Instant.now());
            returnToLobby(player);
            player.sendMessage(TaText.line("<#b8ffde>已放弃挑战。"));
        } catch (SQLException exception) { plugin.getLogger().log(Level.SEVERE, "放弃挑战失败", exception); player.sendMessage(TaText.line("<#b8ffde>放弃挑战失败。")); }
    }

    /** 每秒检查战斗限时和活动、挑战结束。 */
    public void tick() {
        Instant now = Instant.now();
        for (UUID id : Set.copyOf(battles.keySet())) {
            Battle battle = battles.get(id);
            Player player = Bukkit.getPlayer(id);
            if (battle == null || player == null) continue;
            if (!now.isBefore(activity.start().plusSeconds(7L * 24 * 60 * 60)) || now.toEpochMilli() >= battle.challenge().expiresAt()) {
                endExpired(player, battle, now);
            } else if (battle.startNanos() != 0 && System.nanoTime() - battle.startNanos() >= 240_000_000_000L) {
                finish(player, "保存", true);
            }
        }
    }

    private void endExpired(Player player, Battle battle, Instant now) {
        try {
            String reason = !now.isBefore(activity.start().plusSeconds(7L * 24 * 60 * 60)) ? "ACTIVITY_ENDED" : "EXPIRED";
            Datamanager.failChallenge(battle.challenge().id(), reason, now);
            battles.remove(player.getUniqueId());
            clearIntro(player);
            battle.boss().remove();
            cleanInventory(player, battle.identities());
            player.setGameMode(battle.originalMode());
            player.setMaximumNoDamageTicks(battle.originalMaximumNoDamageTicks());
            if (!player.isDead()) returnToLobby(player);
            player.sendMessage(TaText.line("<#b8ffde>挑战已结束。"));
        } catch (SQLException | RuntimeException exception) { plugin.getLogger().log(Level.SEVERE, "结束过期挑战失败", exception); }
    }

    /** 逐格移除非入场物品，保留入场物品当前的数量和状态。 */
    private void cleanInventory(Player player, Set<String> original) {
        var inventory = player.getInventory();
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            ItemStack item = inventory.getItem(slot);
            if (item == null || item.getType().isAir()) continue;
            if (isControl(item) || !original.contains(EquipmentCheck.identity(item))) inventory.setItem(slot, null);
        }
        ItemStack cursor = player.getItemOnCursor();
        if (cursor != null && !cursor.getType().isAir() && (isControl(cursor) || !original.contains(EquipmentCheck.identity(cursor)))) player.setItemOnCursor(null);
    }

    private ItemStack controlItem() {
        ItemStack item = new ItemStack(Material.CLOCK);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(TaText.line("<#ffd166>总力战操作"));
        meta.getPersistentDataContainer().set(controlKey, PersistentDataType.BYTE, (byte) 1);
        item.setItemMeta(meta);
        return item;
    }
    private boolean isControl(ItemStack item) { return item != null && item.hasItemMeta() && item.getItemMeta().getPersistentDataContainer().has(controlKey, PersistentDataType.BYTE); }
    private boolean teleport(Player player, org.bukkit.Location location) {
        internalTeleports.add(player.getUniqueId());
        try { return player.teleport(location); } finally { internalTeleports.remove(player.getUniqueId()); }
    }

    /** 返回大厅时检查传送结果，让玩家和管理员都能知道失败原因。 */
    private void returnToLobby(Player player) {
        try {
            if (!teleport(player, Filereader.ArenaConfig.lobbySpawn())) {
                plugin.getLogger().warning("玩家返回总力战大厅的传送被取消：" + player.getName());
                player.sendMessage(TaText.line("<#ffb4a8>返回大厅失败，传送被取消，请联系管理员。"));
            }
        } catch (RuntimeException exception) {
            plugin.getLogger().log(Level.SEVERE, "玩家返回总力战大厅失败：" + player.getName(), exception);
            player.sendMessage(TaText.line("<#ffb4a8>返回大厅失败，请确认 lobbyworld 已加载并联系管理员。"));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDrop(PlayerDropItemEvent event) { if (battles.containsKey(event.getPlayer().getUniqueId())) event.setCancelled(true); }

    /** 旁观视角按 Shift 时可能先触发退出旁观事件，也兼容普通潜行事件。 */
    @EventHandler(ignoreCancelled = true)
    public void onStopSpectating(PlayerStopSpectatingEntityEvent event) {
        Player player = event.getPlayer();
        Intro intro = intros.get(player.getUniqueId());
        if (intro != null && !internalCameraChanges.contains(player.getUniqueId()) && event.getSpectatorTarget().equals(intro.camera)) {
            skipIntro(player);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onSneak(PlayerToggleSneakEvent event) {
        if (event.isSneaking() && intros.containsKey(event.getPlayer().getUniqueId())) skipIntro(event.getPlayer());
    }

    /** 受伤事件结束后的下一刻再次清零，覆盖其他逻辑在本次受伤中写入的无敌时间。 */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        Battle battle = battles.get(player.getUniqueId());
        if (battle == null) return;
        Bukkit.getScheduler().runTask(plugin, () -> {
            // 玩家可能已经结束出战或开始了新一轮，不要修改其他状态下的无敌时间。
            if (battles.get(player.getUniqueId()) == battle && player.isOnline()) player.setNoDamageTicks(0);
        });
    }
    @EventHandler(ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (battles.containsKey(event.getPlayer().getUniqueId()) && isControl(event.getItem())) {
            event.setCancelled(true);
            tamenu.open(event.getPlayer());
        }
    }
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        UUID id = event.getPlayer().getUniqueId();
        if (intros.containsKey(id) && event.getCause() == PlayerTeleportEvent.TeleportCause.SPECTATE) return;
        if (!internalTeleports.contains(id) && battles.containsKey(id)) finish(event.getPlayer(), "回退", false);
    }
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID id = event.getPlayer().getUniqueId();
        if (!battles.containsKey(id)) return;
        finish(event.getPlayer(), "回退", false);
        if (!battles.containsKey(id)) returnToLobby.add(id);
    }
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!returnToLobby.remove(player.getUniqueId())) return;
        Bukkit.getScheduler().runTask(plugin, () -> {
            if (!player.isOnline()) { returnToLobby.add(player.getUniqueId()); return; }
            try {
                if (!teleport(player, Filereader.ArenaConfig.lobbySpawn())) returnToLobby.add(player.getUniqueId());
            } catch (RuntimeException exception) {
                returnToLobby.add(player.getUniqueId());
                plugin.getLogger().log(Level.SEVERE, "断线后返回大厅失败", exception);
            }
        });
    }
    @EventHandler(priority = EventPriority.HIGH)
    public void onDeath(PlayerDeathEvent event) {
        if (!battles.containsKey(event.getEntity().getUniqueId())) return;
        event.setKeepInventory(true);
        event.getDrops().clear();
        finish(event.getEntity(), "保存", false);
    }
    /** 只记录本场玩家造成的有效原版近战暴击，不统计弓箭等投射物。 */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCritical(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player) || !event.isCritical() || event.getFinalDamage() <= 0) return;
        Battle battle = battles.get(player.getUniqueId());
        if (battle != null && battle.boss().entityId().equals(event.getEntity().getUniqueId())) battle.boss().onCritical();
    }
    @EventHandler
    public void onBossDeath(EntityDeathEvent event) {
        for (Map.Entry<UUID, Battle> entry : Map.copyOf(battles).entrySet()) {
            if (!entry.getValue().boss().entityId().equals(event.getEntity().getUniqueId())) continue;
            event.getDrops().clear();
            event.setDroppedExp(0);
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player != null) finish(player, "通关", true);
            return;
        }
    }
    public void shutdown() { for (UUID id : Set.copyOf(battles.keySet())) { Player player = Bukkit.getPlayer(id); if (player != null) finish(player, "回退", false); } }
}
