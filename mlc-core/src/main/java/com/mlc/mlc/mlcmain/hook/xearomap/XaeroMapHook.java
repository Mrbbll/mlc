package com.mlc.mlc.mlcmain.hook.xearomap;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPluginMessage;
import org.bukkit.GameMode;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public final class XaeroMapHook implements Listener {
    private static final String MINIMAP_CHANNEL = "xaerominimap:main";
    private static final String WORLD_MAP_CHANNEL = "xaeroworldmap:main";
    private static final List<String> CHANNELS = List.of(MINIMAP_CHANNEL, WORLD_MAP_CHANNEL);
    private static final String CONFIG = "xaero-map.";

    private final JavaPlugin plugin;
    private final Server server;
    private final boolean onlySameWorld;
    private final byte @Nullable [] levelIdPacket;
    private final Map<Player, Viewer> viewers = new HashMap<>();
    private final BukkitTask syncTask;

    private XaeroMapHook(JavaPlugin plugin) {
        this.plugin = plugin;
        server = plugin.getServer();
        var config = plugin.getConfig();
        onlySameWorld = config.getBoolean(CONFIG + "only-sync-same-world", true);

        if (config.getBoolean(CONFIG + "send-level-id", true)) {
            String key = CONFIG + "level-id";
            if (!config.isInt(key)) {
                config.set(key, ThreadLocalRandom.current().nextInt());
                plugin.saveConfig();
            }
            levelIdPacket = XaeroPackets.levelId(config.getInt(key));
        } else {
            levelIdPacket = null;
        }

        var messenger = server.getMessenger();
        for (String channel : CHANNELS) {
            messenger.registerIncomingPluginChannel(plugin, channel, this::onPluginMessage);
        }
        server.getPluginManager().registerEvents(this, plugin);
        int interval = Math.max(1, config.getInt(CONFIG + "sync-interval-ticks", 5));
        syncTask = server.getScheduler().runTaskTimer(plugin, this::syncMarkers, 1L, interval);
        server.getOnlinePlayers().forEach(this::sendHandshake);
    }

    public static @Nullable XaeroMapHook init(JavaPlugin plugin) {
        if (!plugin.getConfig().getBoolean(CONFIG + "enabled", true)) {
            return null;
        }
        if (plugin.getServer().getPluginManager().isPluginEnabled("XaeroTracker")) {
            plugin.getLogger().info("检测到独立 XaeroTracker，跳过 MLC 的 Xaero 地图追踪。");
            return null;
        }
        return new XaeroMapHook(plugin);
    }

    private void sendHandshake(Player player) {
        for (String channel : CHANNELS) {
            send(player, channel, XaeroPackets.HANDSHAKE);
            if (levelIdPacket != null) {
                send(player, channel, levelIdPacket);
            }
        }
    }

    private void onPluginMessage(String channel, Player player, byte[] payload) {
        if (!XaeroPackets.isHandshake(payload)) {
            return;
        }
        if (server.isPrimaryThread()) {
            acceptHandshake(player, channel);
        } else {
            server.getScheduler().runTask(plugin, () -> acceptHandshake(player, channel));
        }
    }

    private void acceptHandshake(Player player, String channel) {
        if (!player.isOnline()) {
            return;
        }
        Viewer viewer = viewers.computeIfAbsent(player, ignored -> new Viewer());
        String previousChannel = viewer.channel;
        if (MINIMAP_CHANNEL.equals(channel)) {
            viewer.hasMinimap = true;
        } else {
            viewer.hasWorldMap = true;
        }
        // Both Xaero mods share tracker data. Prefer the minimap when both answer.
        viewer.channel = viewer.hasMinimap ? MINIMAP_CHANNEL : WORLD_MAP_CHANNEL;
        if (!viewer.channel.equals(previousChannel)) {
            if (previousChannel != null) {
                send(player, previousChannel, XaeroPackets.RESET);
            }
            resetMarkers(player, viewer);
        }
    }

    private void syncMarkers() {
        if (viewers.isEmpty()) {
            return;
        }
        var targets = new HashMap<Player, XaeroPackets.Position>();
        for (Player player : server.getOnlinePlayers()) {
            if (isTrackable(player)) {
                targets.put(player, new XaeroPackets.Position(
                        player.getX(), player.getY(), player.getZ(), player.getWorld().getKey().toString()));
            }
        }
        // A position payload can be reused by all viewers of that target in this tick.
        var updates = new HashMap<UUID, byte[]>();
        for (var entry : viewers.entrySet()) {
            Player player = entry.getKey();
            Viewer viewer = entry.getValue();
            var visible = new HashMap<UUID, XaeroPackets.Position>();
            for (var targetEntry : targets.entrySet()) {
                Player target = targetEntry.getKey();
                if (target.equals(player) || (onlySameWorld && !target.getWorld().equals(player.getWorld()))
                        || !player.canSee(target)) {
                    continue;
                }
                UUID id = target.getUniqueId();
                XaeroPackets.Position position = targetEntry.getValue();
                visible.put(id, position);
                if (!position.equals(viewer.markers.get(id))) {
                    send(player, viewer.channel, updates.computeIfAbsent(id, key -> XaeroPackets.marker(key, position)));
                }
            }
            for (UUID id : viewer.markers.keySet()) {
                if (!visible.containsKey(id)) {
                    send(player, viewer.channel, XaeroPackets.marker(id, null));
                }
            }
            viewer.markers = visible;
        }
    }

    @SuppressWarnings("deprecation") // Vanish plugins commonly use this metadata key.
    private static boolean isTrackable(Player player) {
        return !player.isInvisible()
                && player.getGameMode() != GameMode.SPECTATOR
                && player.getMetadata("vanished").stream().noneMatch(MetadataValue::asBoolean);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        sendHandshake(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        Viewer viewer = viewers.get(player);
        if (viewer == null) {
            return;
        }
        if (levelIdPacket != null) {
            if (viewer.hasMinimap) {
                send(player, MINIMAP_CHANNEL, levelIdPacket);
            }
            if (viewer.hasWorldMap) {
                send(player, WORLD_MAP_CHANNEL, levelIdPacket);
            }
        }
        resetMarkers(player, viewer);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        viewers.remove(player);
        UUID id = player.getUniqueId();
        byte[] removal = XaeroPackets.marker(id, null);
        viewers.forEach((recipient, viewer) -> {
            if (viewer.markers.remove(id) != null) {
                send(recipient, viewer.channel, removal);
            }
        });
    }

    private void resetMarkers(Player player, Viewer viewer) {
        send(player, viewer.channel, XaeroPackets.RESET);
        viewer.markers.clear();
    }

    private static void send(Player player, String channel, byte[] payload) {
        // Xaero's handshake does not depend on Bukkit client channel registration.
        PacketEvents.getAPI().getPlayerManager().sendPacket(
                player, new WrapperPlayServerPluginMessage(channel, payload));
    }

    public void shutdown() {
        syncTask.cancel();
        HandlerList.unregisterAll(this);
        for (String channel : CHANNELS) {
            server.getMessenger().unregisterIncomingPluginChannel(plugin, channel);
        }
        viewers.forEach(this::resetMarkers);
        viewers.clear();
    }

    private static final class Viewer {
        private boolean hasMinimap;
        private boolean hasWorldMap;
        private String channel;
        private Map<UUID, XaeroPackets.Position> markers = new HashMap<>();
    }
}
