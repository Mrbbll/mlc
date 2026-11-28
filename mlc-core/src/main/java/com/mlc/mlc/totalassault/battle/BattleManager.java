package com.mlc.mlc.totalassault.battle;

import com.mlc.mlc.totalassault.boss.AssaultBoss;
import com.mlc.mlc.totalassault.boss.BossHandle;
import com.mlc.mlc.totalassault.boss.BossRegistry;
import com.mlc.mlc.totalassault.boss.saint.Saint;
import com.mlc.mlc.totalassault.TaText;
import com.mlc.mlc.totalassault.data.Datamanager;
import com.mlc.mlc.totalassault.data.Filereader;
import com.mlc.mlc.totalassault.data.TotalAssaultStorage;
import com.mlc.mlc.totalassault.menu.tamenu;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
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

    private record Battle(TotalAssaultStorage.Challenge challenge, String attemptId, BossHandle boss,
                          long startNanos, GameMode originalMode, Set<String> identities) {}

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
            if (!teleport(player, arena.playerSpawn().location(world))) throw new IllegalStateException("传送失败");
            moved = true;
            long start = System.nanoTime();
            boss = definition.spawn(arena.bossSpawn().location(world), challenge.difficulty(), challenge.bossHealth());
            Saint.Progress progress = definition.id().equals("saint")
                    ? Datamanager.saintProgress(challenge.id()) : new Saint.Progress(0, 0);
            boss.start(plugin, player, progress.ultimate(), progress.stun());
            player.getInventory().setItem(8, controlItem());
            battles.put(player.getUniqueId(), new Battle(challenge, attemptId, boss, start, original, Set.copyOf(result.identities())));
            player.sendMessage(TaText.line("<#b8ffde>出战开始，单次出战限时四分钟。"));
        } catch (SQLException | RuntimeException exception) {
            if (boss != null) boss.remove();
            player.getInventory().setItem(8, null);
            player.setGameMode(original);
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
        long millis = Math.min(240_000L, Math.max(0, (System.nanoTime() - battle.startNanos()) / 1_000_000L));
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
        battles.remove(player.getUniqueId());
        battle.boss().remove();
        cleanInventory(player, battle.identities());
        player.setGameMode(battle.originalMode());
        if (move && !player.isDead()) {
            returnToLobby(player);
        }
        player.sendMessage(TaText.line("<#b8ffde>本次出战：" + TaText.escape(action)));
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
            } else if (System.nanoTime() - battle.startNanos() >= 240_000_000_000L) {
                finish(player, "保存", true);
            }
        }
    }

    private void endExpired(Player player, Battle battle, Instant now) {
        try {
            String reason = !now.isBefore(activity.start().plusSeconds(7L * 24 * 60 * 60)) ? "ACTIVITY_ENDED" : "EXPIRED";
            Datamanager.failChallenge(battle.challenge().id(), reason, now);
            battles.remove(player.getUniqueId());
            battle.boss().remove();
            cleanInventory(player, battle.identities());
            player.setGameMode(battle.originalMode());
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
    @EventHandler(ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (battles.containsKey(event.getPlayer().getUniqueId()) && isControl(event.getItem())) {
            event.setCancelled(true);
            tamenu.open(event.getPlayer());
        }
    }
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        if (!internalTeleports.contains(event.getPlayer().getUniqueId()) && battles.containsKey(event.getPlayer().getUniqueId())) finish(event.getPlayer(), "回退", false);
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
