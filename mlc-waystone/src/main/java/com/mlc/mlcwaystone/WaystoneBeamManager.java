package com.mlc.mlcwaystone;

import io.papermc.paper.event.packet.PlayerChunkUnloadEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/** Client-only beams. Never spawns world entities, changes blocks, or loads chunks. */
public final class WaystoneBeamManager implements Listener {
    private static final double VIEW_DISTANCE_SQUARED = 256 * 256;
    private final Supplier<Collection<WaystoneData>> waystones;
    private final Supplier<? extends Collection<? extends Player>> players;
    private final BiFunction<Location, Float, Visual> visuals;
    private final Map<UUID, Beam> beams = new HashMap<>();
    private BukkitTask task;
    private BukkitTask animationTask;
    private long animationTicks;

    public WaystoneBeamManager(Supplier<Collection<WaystoneData>> waystones) {
        this(waystones, Bukkit::getOnlinePlayers, PacketWaystoneBeam::new);
    }

    WaystoneBeamManager(Supplier<Collection<WaystoneData>> waystones,
                        Supplier<? extends Collection<? extends Player>> players,
                        BiFunction<Location, Float, Visual> visuals) {
        this.waystones = waystones;
        this.players = players;
        this.visuals = visuals;
    }

    public void start(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        task = plugin.getServer().getScheduler().runTaskTimer(plugin, this::refresh, 1L, 20L);
        animationTask = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            animate(animationTicks);
            animationTicks += 5;
        }, 6L, 5L);
    }

    void animate(long ticks) {
        for (Beam beam : beams.values()) {
            var viewers = beam.viewers.stream()
                    .filter(player -> player.isOnline() && beam.origin.getWorld().equals(player.getWorld())
                            && player.isChunkSent(chunkKey(beam.origin)))
                    .toList();
            if (!viewers.isEmpty()) {
                beam.visual.animate(viewers, ticks);
            }
        }
    }

    public void refresh() {
        Set<UUID> active = new HashSet<>();
        Collection<? extends Player> onlinePlayers = players.get();
        for (WaystoneData data : waystones.get()) {
            Location base = data.getLocation();
            if (!data.isPublic() || !hasLoadedStructure(base) || !hasClearSky(base)) {
                continue;
            }
            Location origin = new Location(base.getWorld(), base.getBlockX() + 0.5,
                    base.getBlockY() + 1.75, base.getBlockZ() + 0.5);
            active.add(data.getId());
            Beam beam = beams.get(data.getId());
            if (beam == null || !beam.origin.equals(origin)) {
                if (beam != null) {
                    beam.remove();
                    beams.remove(data.getId());
                }
                float height = (float) (base.getWorld().getMaxHeight() - origin.getY());
                beam = new Beam(origin, visuals.apply(origin, height));
                beams.put(data.getId(), beam);
            }
            beam.syncViewers(onlinePlayers);
        }
        Iterator<Map.Entry<UUID, Beam>> iterator = beams.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, Beam> entry = iterator.next();
            if (!active.contains(entry.getKey())) {
                entry.getValue().remove();
                iterator.remove();
            }
        }
    }

    static boolean hasLoadedStructure(Location base) {
        World world = base.getWorld();
        if (world == null || base.getBlockY() < world.getMinHeight()
                || base.getBlockY() + 1 >= world.getMaxHeight()
                || !world.isChunkLoaded(base.getBlockX() >> 4, base.getBlockZ() >> 4)) {
            return false;
        }
        return world.getBlockAt(base.getBlockX(), base.getBlockY(), base.getBlockZ()).getType() == Material.LODESTONE
                && world.getBlockAt(base.getBlockX(), base.getBlockY() + 1, base.getBlockZ()).getType() == Material.BEACON;
    }

    static boolean hasClearSky(Location base) {
        World world = base.getWorld();
        for (int y = base.getBlockY() + 2; y < world.getMaxHeight(); y++) {
            Block block = world.getBlockAt(base.getBlockX(), y, base.getBlockZ());
            // Bedrock is transparent to vanilla beacon beams (e.g. the Nether roof).
            if (block.getType() != Material.BEDROCK && block.getType().isOccluding()) {
                return false;
            }
        }
        return true;
    }

    @EventHandler(ignoreCancelled = true)
    public void onChunkUnload(ChunkUnloadEvent event) {
        Iterator<Beam> iterator = beams.values().iterator();
        while (iterator.hasNext()) {
            Beam beam = iterator.next();
            if (beam.origin.getWorld().equals(event.getWorld())
                    && (beam.origin.getBlockX() >> 4) == event.getChunk().getX()
                    && (beam.origin.getBlockZ() >> 4) == event.getChunk().getZ()) {
                beam.remove();
                iterator.remove();
            }
        }
    }

    // These transitions discard the client's entities. Forget visibility so the
    // next refresh sends fresh spawn packets even for a same-world respawn.
    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        forgetViewer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerChunkUnload(PlayerChunkUnloadEvent event) {
        for (Beam beam : beams.values()) {
            if (beam.origin.getWorld().equals(event.getWorld())
                    && (beam.origin.getBlockX() >> 4) == event.getChunk().getX()
                    && (beam.origin.getBlockZ() >> 4) == event.getChunk().getZ()
                    && beam.viewers.remove(event.getPlayer())) {
                beam.hide(event.getPlayer());
            }
        }
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        forgetViewer(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        forgetViewer(event.getPlayer());
    }

    private void forgetViewer(Player player) {
        for (Beam beam : beams.values()) {
            beam.viewers.remove(player);
        }
    }

    public void shutdown() {
        if (task != null) {
            task.cancel();
        }
        if (animationTask != null) {
            animationTask.cancel();
        }
        beams.values().forEach(Beam::remove);
        beams.clear();
    }

    interface Visual {
        void show(Player player);
        void hide(Player player);
        void animate(Collection<Player> viewers, long ticks);
    }

    private static long chunkKey(Location location) {
        return ((long) (location.getBlockX() >> 4) & 0xffffffffL)
                | ((long) (location.getBlockZ() >> 4) << 32);
    }

    private static final class Beam {
        private final Location origin;
        private final Visual visual;
        private final Set<Player> viewers = new HashSet<>();

        private Beam(Location origin, Visual visual) {
            this.origin = origin;
            this.visual = visual;
        }

        private void syncViewers(Collection<? extends Player> players) {
            Set<Player> visible = new HashSet<>();
            for (Player player : players) {
                if (!player.isOnline() || !origin.getWorld().equals(player.getWorld())
                        || !player.isChunkSent(chunkKey(origin))) {
                    continue;
                }
                Location location = player.getLocation();
                double dx = location.getX() - origin.getX();
                double dz = location.getZ() - origin.getZ();
                if (dx * dx + dz * dz > VIEW_DISTANCE_SQUARED) {
                    continue;
                }
                visible.add(player);
                if (viewers.add(player)) {
                    visual.show(player);
                }
            }
            Iterator<Player> iterator = viewers.iterator();
            while (iterator.hasNext()) {
                Player player = iterator.next();
                if (!visible.contains(player)) {
                    hide(player);
                    iterator.remove();
                }
            }
        }

        private void hide(Player player) {
            if (player.isOnline() && origin.getWorld().equals(player.getWorld())) {
                visual.hide(player);
            }
        }

        private void remove() {
            viewers.forEach(this::hide);
            viewers.clear();
        }
    }
}
