package com.mlc.mlc.mlcmain.items.itemfunc.balloon;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.particle.Particle;
import com.github.retrooper.packetevents.protocol.particle.data.ParticleGeyserBaseData;
import com.github.retrooper.packetevents.protocol.particle.type.ParticleTypes;
import com.github.retrooper.packetevents.protocol.sound.SoundCategory;
import com.github.retrooper.packetevents.protocol.sound.Sounds;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.util.Vector3f;
import com.github.retrooper.packetevents.util.Quaternion4f;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetPassengers;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerParticle;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSoundEffect;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static com.mlc.mlc.Mlc.instance;

/** The Balloon display exists only on clients and is never spawned in the world. */
final class BalloonVisual {
    // Vanilla allocates positive entity IDs. Avoid -1, which is the protocol's detach sentinel.
    private static final AtomicInteger IDS = new AtomicInteger(-2);
    private static final Map<UUID, Visual> visuals = new HashMap<>();
    // Packet callbacks can run on Netty threads; never access Bukkit state from them.
    private static final Map<UUID, Map<Integer, Integer>> mounted = new ConcurrentHashMap<>();
    private static final Map<UUID, Map<Integer, int[]>> passengers = new ConcurrentHashMap<>();
    private static BukkitTask task;
    // 26.3 Display entity metadata: interpolation start, duration, and left rotation.
    private static final int TRANSFORM_START = 8;
    private static final int TRANSFORM_DURATION = 9;
    private static final int LEFT_ROTATION = 13;
    private static final int ROTATION_TICKS = 3;

    private static final PacketListenerAbstract passengerListener = new PacketListenerAbstract() {
        @Override public void onPacketSend(PacketSendEvent event) {
            if (event.getPacketType() != PacketType.Play.Server.SET_PASSENGERS) return;
            Player viewer = event.getPlayer();
            if (viewer == null) return;
            UUID viewerId = viewer.getUniqueId();
            WrapperPlayServerSetPassengers packet = new WrapperPlayServerSetPassengers(event);
            Integer display = mounted.getOrDefault(viewerId, Map.of()).get(packet.getEntityId());
            int[] original = packet.getPassengers();
            if (display != null) original = Arrays.stream(original).filter(id -> id != display).toArray();
            passengers.computeIfAbsent(viewerId, ignored -> new ConcurrentHashMap<>()).put(packet.getEntityId(), original);
            if (display != null) {
                int[] combined = Arrays.copyOf(original, original.length + 1);
                combined[original.length] = display;
                packet.setPassengers(combined);
                event.markForReEncode(true);
            }
        }
    };

    private static final class Visual {
        final int display = IDS.getAndDecrement();
        final UUID displayUuid = UUID.randomUUID();
        final UUID world;
        final int ownerId;
        final Set<UUID> viewers = new HashSet<>();
        float lastYaw = Float.NaN;

        Visual(Player owner) {
            world = owner.getWorld().getUID();
            ownerId = owner.getEntityId();
        }
    }

    private BalloonVisual() { }

    static void init() {
        PacketEvents.getAPI().getEventManager().registerListener(passengerListener);
    }

    static void refresh(Player owner, boolean holding) {
        if (!holding || !owner.isOnline() || owner.isDead()) {
            remove(owner);
            return;
        }
        Visual current = visuals.get(owner.getUniqueId());
        if (current != null && (!current.world.equals(owner.getWorld().getUID()) || current.ownerId != owner.getEntityId())) {
            remove(owner);
            current = null;
        }
        if (current == null) visuals.put(owner.getUniqueId(), new Visual(owner));
        if (task == null) task = Bukkit.getScheduler().runTaskTimer(instance, BalloonVisual::tick, 1L, 2L);
    }

    private static void tick() {
        for (UUID id : Set.copyOf(visuals.keySet())) {
            Player owner = Bukkit.getPlayer(id);
            if (owner == null || !owner.isOnline() || owner.isDead()) {
                remove(id);
                continue;
            }
            Visual visual = visuals.get(id);
            if (!visual.world.equals(owner.getWorld().getUID()) || visual.ownerId != owner.getEntityId()) {
                remove(id);
                refresh(owner, true);
                visual = visuals.get(id);
            }
            Location location = position(owner);
            boolean yawChanged = Float.compare(visual.lastYaw, location.getYaw()) != 0;
            Set<Player> visible = new HashSet<>(owner.getTrackedBy());
            visible.add(owner);
            Set<UUID> wanted = new HashSet<>();
            for (Player viewer : visible) {
                if (!viewer.isOnline() || !viewer.getWorld().equals(owner.getWorld()) || !viewer.canSee(owner)) continue;
                wanted.add(viewer.getUniqueId());
                if (visual.viewers.add(viewer.getUniqueId())) spawn(viewer, owner, visual, location);
                else if (yawChanged) rotate(viewer, visual, location);
                if (com.mlc.mlc.mlcmain.items.itemfunc.balloon.Balloon.isFlyingWithBalloon(owner)) {
                    sendEffects(viewer, location.clone().add(location.getDirection().multiply(-0.5))
                            .add(0, 1.5, 0));
                }
            }
            visual.lastYaw = location.getYaw();
            for (UUID viewerId : Set.copyOf(visual.viewers)) {
                if (wanted.contains(viewerId)) continue;
                Player viewer = Bukkit.getPlayer(viewerId);
                if (viewer != null && viewer.isOnline()) destroy(viewer, visual);
                else mounted.remove(viewerId);
                visual.viewers.remove(viewerId);
            }
        }
        if (visuals.isEmpty() && task != null) {
            task.cancel();
            task = null;
        }
    }

    private static Location position(Player owner) {
        Location location = owner.getLocation();
        // Follow the player's position and horizontal rotation, not their vertical look angle.
        location.setPitch(0);
        return location;
    }

    private static void spawn(Player viewer, Player owner, Visual visual, Location location) {
        Vector3d pos = vector(location);
        var packets = PacketEvents.getAPI().getPlayerManager();
        packets.sendPacket(viewer, new WrapperPlayServerSpawnEntity(visual.display, Optional.of(visual.displayUuid),
                EntityTypes.ITEM_DISPLAY, pos, 0, 0, 0, 0, Optional.empty()));
        ItemStack item = new ItemStack(Material.SHULKER_BOX);
        item.editMeta(meta -> meta.setItemModel(NamespacedKey.fromString("mlc:balloon")));
        packets.sendPacket(viewer, new WrapperPlayServerEntityMetadata(visual.display, List.of(
                new EntityData<>(23, EntityDataTypes.ITEMSTACK, SpigotConversionUtil.fromBukkitItemStack(item)),
                new EntityData<>(TRANSFORM_DURATION, EntityDataTypes.INT, 0),
                new EntityData<>(LEFT_ROTATION, EntityDataTypes.QUATERNION, rotation(location.getYaw())))));
        int[] existing = passengers.getOrDefault(viewer.getUniqueId(), Map.of()).get(visual.ownerId);
        if (existing == null) existing = owner.getPassengers().stream().mapToInt(entity -> entity.getEntityId()).toArray();
        mounted.computeIfAbsent(viewer.getUniqueId(), ignored -> new ConcurrentHashMap<>()).put(visual.ownerId, visual.display);
        int[] combined = Arrays.copyOf(existing, existing.length + 1);
        combined[existing.length] = visual.display;
        packets.sendPacket(viewer, new WrapperPlayServerSetPassengers(visual.ownerId, combined));
    }

    private static void sendEffects(Player viewer, Location location) {
        // 飞行期间向能看到气球的观察者发送效果，位置随持有者更新。
        var packets = PacketEvents.getAPI().getPlayerManager();
        Vector3d pos = vector(location);
        packets.sendPacket(viewer, new WrapperPlayServerParticle(
                new Particle<>(ParticleTypes.GEYSER_POOF, new ParticleGeyserBaseData(1, 0.02f)),
                false, pos, new Vector3f(0.3f, 0.5f, 0.3f), 0.02f, 1));
        packets.sendPacket(viewer, new WrapperPlayServerSoundEffect(Sounds.BLOCK_CAMPFIRE_CRACKLE,
                SoundCategory.PLAYER, pos, 3f, 1.5f));
    }

    private static void rotate(Player viewer, Visual visual, Location location) {
        PacketEvents.getAPI().getPlayerManager().sendPacket(viewer,
                new WrapperPlayServerEntityMetadata(visual.display, List.of(
                        new EntityData<>(TRANSFORM_START, EntityDataTypes.INT, 0),
                        new EntityData<>(TRANSFORM_DURATION, EntityDataTypes.INT, ROTATION_TICKS),
                        new EntityData<>(LEFT_ROTATION, EntityDataTypes.QUATERNION, rotation(location.getYaw())))));
    }

    private static Quaternion4f rotation(float yaw) {
        double halfAngle = Math.toRadians(-yaw) / 2.0;
        return new Quaternion4f(0, (float) Math.sin(halfAngle), 0, (float) Math.cos(halfAngle));
    }

    private static Vector3d vector(Location location) {
        return new Vector3d(location.getX(), location.getY(), location.getZ());
    }

    static void remove(Player owner) { remove(owner.getUniqueId()); }

    static void quit(Player player) {
        remove(player);
        mounted.remove(player.getUniqueId());
        passengers.remove(player.getUniqueId());
    }

    private static void remove(UUID owner) {
        Visual visual = visuals.remove(owner);
        if (visual == null) return;
        for (UUID viewerId : visual.viewers) {
            Player viewer = Bukkit.getPlayer(viewerId);
            if (viewer != null && viewer.isOnline()) destroy(viewer, visual);
        }
    }

    private static void destroy(Player viewer, Visual visual) {
        Map<Integer, Integer> viewerMounts = mounted.get(viewer.getUniqueId());
        if (viewerMounts != null) viewerMounts.remove(visual.ownerId, visual.display);
        PacketEvents.getAPI().getPlayerManager().sendPacket(viewer,
                new WrapperPlayServerDestroyEntities(visual.display));
    }

    static void shutdown() {
        for (UUID owner : Set.copyOf(visuals.keySet())) remove(owner);
        if (task != null) task.cancel();
        task = null;
        PacketEvents.getAPI().getEventManager().unregisterListener(passengerListener);
        mounted.clear();
        passengers.clear();
    }
}
