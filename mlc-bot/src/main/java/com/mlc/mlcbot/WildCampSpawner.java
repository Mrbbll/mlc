package com.mlc.mlcbot;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import java.util.logging.Level;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.generator.structure.GeneratedStructure;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.BoundingBox;

/** One runtime bot per vanilla abandoned camp, including camps reached by teleport. */
public final class WildCampSpawner implements Listener {
    private final JavaPlugin plugin;
    private final BotManager manager;
    private final NamespacedKey spawned;

    public WildCampSpawner(JavaPlugin plugin, BotManager manager) {
        this.plugin = plugin;
        this.manager = manager;
        spawned = new NamespacedKey(plugin, "wild_camp_bot_spawned");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChunkLoad(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();
        // Wait until the chunk has finished loading before accessing terrain or spawning NMS players.
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            if (chunk.isLoaded()) populate(chunk);
        });
        // Neighbouring terrain/structure references may still be arriving during a teleport.
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (chunk.isLoaded()) populate(chunk);
        }, 20L);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        Location destination = event.getTo();
        if (destination != null) scheduleNearby(destination);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        scheduleNearby(event.getPlayer().getLocation());
    }

    private void scheduleNearby(Location destination) {
        Location at = destination.clone();
        plugin.getServer().getScheduler().runTask(plugin, () -> scanNearby(at));
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> scanNearby(at), 20L);
    }

    void scanNearby(Location at) {
        World world = at.getWorld();
        int centerX = at.getBlockX() >> 4, centerZ = at.getBlockZ() >> 4;
        for (int x = centerX - 2; x <= centerX + 2; x++) {
            for (int z = centerZ - 2; z <= centerZ + 2; z++) {
                if (world.isChunkLoaded(x, z)) populate(world.getChunkAt(x, z));
            }
        }
    }

    void populate(Chunk chunk) {
        for (GeneratedStructure structure : chunk.getStructures()) {
            NamespacedKey key = RegistryAccess.registryAccess().getRegistry(RegistryKey.STRUCTURE)
                    .getKey(structure.getStructure());
            if (key != null && isAbandonedCamp(key)) spawnCamp(chunk.getWorld(), structure);
        }
    }

    void spawnCamp(World world, GeneratedStructure structure) {
        if (structure.getPersistentDataContainer().has(spawned, PersistentDataType.BYTE)) return;
        Location at = findSpawn(world, structure.getBoundingBox());
        // Other intersecting chunks, or a subsequent load, can retry when there is safe terrain.
        if (at == null) {
            plugin.getLogger().warning("废弃营地暂未找到安全的机器人生成位置，将在后续加载时重试: "
                    + world.getName() + " " + structure.getBoundingBox());
            return;
        }
        try {
            manager.spawnWild(at);
            structure.getPersistentDataContainer().set(spawned, PersistentDataType.BYTE, (byte) 1);
        } catch (RuntimeException | LinkageError failure) {
            plugin.getLogger().log(Level.SEVERE, "废弃营地机器人生成失败: " + at, failure);
        }
    }

    static boolean isAbandonedCamp(NamespacedKey key) {
        return key.getNamespace().equals(NamespacedKey.MINECRAFT)
                && key.getKey().startsWith("abandoned_camp_");
    }

    Location findSpawn(World world, BoundingBox bounds) {
        int centerX = (int) Math.floor(bounds.getCenterX());
        int centerZ = (int) Math.floor(bounds.getCenterZ());
        int radius = (int) Math.ceil(Math.max(bounds.getWidthX(), bounds.getWidthZ()) / 2) + 1;
        int minY = Math.max(world.getMinHeight() + 1, (int) Math.floor(bounds.getMinY()) - 2);
        int maxY = Math.min(world.getMaxHeight() - 2, (int) Math.ceil(bounds.getMaxY()) + 2);
        for (int r = 0; r <= radius; r++) {
            for (int dx = -r; dx <= r; dx++) for (int dz = -r; dz <= r; dz++) {
                if (Math.max(Math.abs(dx), Math.abs(dz)) != r) continue;
                int x = centerX + dx, z = centerZ + dz;
                if (x < bounds.getMinX() || x >= bounds.getMaxX()
                        || z < bounds.getMinZ() || z >= bounds.getMaxZ()
                        || !world.isChunkLoaded(x >> 4, z >> 4)) continue;
                for (int y = minY; y <= maxY; y++) {
                    Location at = new Location(world, x + 0.5, y, z + 0.5);
                    if (BotManager.isSafeSpawn(at)) return at;
                }
            }
        }
        return null;
    }
}
