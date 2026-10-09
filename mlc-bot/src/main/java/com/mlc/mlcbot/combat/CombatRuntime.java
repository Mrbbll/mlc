package com.mlc.mlcbot.combat;

import com.mlc.mlcbot.BotSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.RespawnAnchor;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

/** Owns the world effects and delayed work of one original combat controller. Main thread only. */
public final class CombatRuntime implements AutoCloseable {
    private static final Map<UUID, CombatRuntime> RUNTIMES = new HashMap<>();
    private static CombatRuntime current;
    private final BotSession session;
    private final Set<BukkitTask> tasks = new HashSet<>();
    private final Map<Block, Change> blocks = new HashMap<>();
    private final Map<UUID, Entity> entities = new HashMap<>();
    private final Set<UUID> crystals = new HashSet<>();
    private boolean closed;

    public CombatRuntime(BotSession session) {
        this.session = session;
        RUNTIMES.put(session.handle.getUUID(), this);
    }

    public void run(Runnable action) {
        if (closed) return;
        CombatRuntime previous = current;
        current = this;
        try { action.run(); } finally { current = previous; }
    }

    /** Includes recursively scheduled actions; closing a bot cancels the entire action chain. */
    public static BukkitTask runTaskLater(Plugin plugin, Runnable action, long delay) {
        CombatRuntime runtime = current;
        if (runtime == null) throw new IllegalStateException("Combat action scheduled outside its bot session");
        BukkitTask[] task = new BukkitTask[1];
        task[0] = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            runtime.tasks.remove(task[0]);
            runtime.run(() -> {
                Player owner = Bukkit.getPlayer(runtime.session.owner);
                Player bot = runtime.session.handle.getBukkitEntity();
                if (!bot.isValid() || bot.isDead() || owner == null || owner.isDead()
                        || !bot.getWorld().equals(owner.getWorld())
                        || owner.getGameMode() == org.bukkit.GameMode.CREATIVE
                        || owner.getGameMode() == org.bukkit.GameMode.SPECTATOR) return;
                try { action.run(); }
                catch (RuntimeException failure) {
                    plugin.getLogger().log(java.util.logging.Level.SEVERE, "mlc-bot 延迟策略失败，已停止该机器人", failure);
                    runtime.close();
                }
            });
        }, delay);
        runtime.tasks.add(task[0]);
        return task[0];
    }

    public boolean isClosed() { return closed; }
    public boolean ownsCrystal(UUID id) { return crystals.contains(id); }

    public static boolean place(Player bot, Block block, Material material) {
        CombatRuntime runtime = RUNTIMES.get(bot.getUniqueId());
        if (runtime == null || runtime.closed || !loaded(block) || !block.getWorld().getWorldBorder().isInside(block.getLocation())
                || runtime.blocks.size() >= 256 && !runtime.blocks.containsKey(block)) return false;
        BlockState old = block.getState();
        block.setType(material, false);
        BlockPlaceEvent event = new BlockPlaceEvent(block, old, block.getRelative(BlockFace.DOWN),
                new ItemStack(material), bot, true, EquipmentSlot.HAND);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled() || !event.canBuild()) { old.update(true, false); return false; }
        runtime.record(block, old);
        return true;
    }

    public static EnderCrystal spawnCrystal(Player bot, Location at) {
        CombatRuntime runtime = RUNTIMES.get(bot.getUniqueId());
        if (runtime != null) runtime.pruneEntities();
        if (runtime == null || runtime.closed || runtime.entities.size() >= 128) return null;
        Block base = at.clone().add(0, -1, 0).getBlock();
        if (!loaded(base) || !interact(bot, base, Material.END_CRYSTAL)) return null;
        EnderCrystal crystal = at.getWorld().spawn(at, EnderCrystal.class, entity -> {
            entity.setShowingBottom(false);
            entity.setPersistent(false);
        });
        runtime.track(crystal);
        runtime.crystals.add(crystal.getUniqueId());
        return crystal;
    }

    public static void trackProjectile(Projectile entity) {
        if (entity.getShooter() instanceof Player player) {
            CombatRuntime runtime = RUNTIMES.get(player.getUniqueId());
            if (runtime != null) runtime.track(entity);
        }
    }

    private void track(Entity entity) {
        pruneEntities();
        entity.setPersistent(false);
        entities.put(entity.getUniqueId(), entity);
    }

    private void pruneEntities() {
        entities.values().removeIf(item -> !item.isValid());
        crystals.retainAll(entities.keySet());
    }

    public static boolean charge(Player bot, Block block) {
        CombatRuntime runtime = RUNTIMES.get(bot.getUniqueId());
        if (runtime == null || runtime.closed || !loaded(block) || !interact(bot, block, Material.GLOWSTONE)
                || runtime.blocks.size() >= 256 && !runtime.blocks.containsKey(block)
                || !(block.getBlockData() instanceof RespawnAnchor anchor)) return false;
        BlockState old = block.getState();
        anchor.setCharges(Math.max(1, anchor.getCharges()));
        block.setBlockData(anchor, false);
        runtime.record(block, old);
        return true;
    }

    public static boolean breakBlock(Player bot, Block block) {
        CombatRuntime runtime = RUNTIMES.get(bot.getUniqueId());
        if (runtime == null || runtime.closed || !loaded(block)
                || runtime.blocks.size() >= 256 && !runtime.blocks.containsKey(block)) return false;
        BlockState old = block.getState();
        if (!bot.breakBlock(block)) return false;
        runtime.record(block, old);
        return true;
    }

    public static boolean explodeAnchor(Player bot, Block anchor, Location center) {
        CombatRuntime runtime = RUNTIMES.get(bot.getUniqueId());
        if (runtime == null || runtime.closed || !loaded(anchor) || !interact(bot, anchor, Material.NETHERITE_SWORD)) return false;
        BlockState old = anchor.getState();
        anchor.setType(Material.AIR, false);
        // Keep native explosion damage/knockback and protection events, with an identifiable bot source.
        boolean exploded = center.getWorld().createExplosion(center, 5.0F, false, false, bot);
        if (!exploded && anchor.getType().isAir()) old.update(true, false);
        runtime.record(anchor, old);
        return exploded;
    }

    private static boolean interact(Player bot, Block block, Material item) {
        PlayerInteractEvent event = new PlayerInteractEvent(bot, Action.RIGHT_CLICK_BLOCK,
                new ItemStack(item), block, BlockFace.UP, EquipmentSlot.HAND);
        Bukkit.getPluginManager().callEvent(event);
        return event.useInteractedBlock() != Event.Result.DENY && event.useItemInHand() != Event.Result.DENY;
    }

    private void record(Block block, BlockState old) {
        Change previous = blocks.get(block);
        BlockState original = previous == null ? old : previous.original();
        if (original.getBlockData().equals(block.getBlockData())) blocks.remove(block);
        else blocks.put(block, new Change(original, block.getBlockData().clone()));
    }

    private static boolean loaded(Block block) {
        return block.getWorld().isChunkLoaded(block.getX() >> 4, block.getZ() >> 4);
    }

    @Override public void close() {
        if (closed) return;
        closed = true;
        RUNTIMES.remove(session.handle.getUUID());
        cancelActions();
        for (Entity entity : entities.values()) if (entity.isValid()) entity.remove();
        entities.clear();
        for (var entry : blocks.entrySet()) {
            Block block = entry.getKey();
            Change change = entry.getValue();
            if (loaded(block) && block.getBlockData().equals(change.expected())) change.original().update(true, false);
        }
        blocks.clear();
        crystals.clear();
    }

    public void cancelActions() {
        for (BukkitTask task : new ArrayList<>(tasks)) task.cancel();
        tasks.clear();
    }

    private record Change(BlockState original, BlockData expected) {}
}
