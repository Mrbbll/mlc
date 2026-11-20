/**
 * 文件说明：通过 PacketEvents 向客户端发送临时 TextDisplay 浮字。
 * 浮字从不加入 Bukkit 世界；生成、缩放和销毁都只发送给本次命中的可见玩家。
 */
package com.mlc.mlc.mlcmain.damageindicator;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.util.Vector3f;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Creates a client-only text display and animates it entirely through packets. */
final class DamageHologram {
    private static final byte BILLBOARD_CENTER = 3;
    private static final byte TEXT_FLAGS = 0x03; // shadow + see-through
    private static final float SHRINK_SCALE = 0.01f;

    /* Minecraft 1.20.2+ TextDisplay tracked-data indices; this plugin targets Paper 26.3+. */
    private static final int TRANSFORMATION_INTERPOLATION_DELAY = 8;
    private static final int TRANSFORMATION_INTERPOLATION_DURATION = 9;
    private static final int POSITION_ROTATION_INTERPOLATION_DURATION = 10;
    private static final int SCALE = 12;
    private static final int BILLBOARD_CONSTRAINTS = 15;
    private static final int TEXT = 23;
    private static final int BACKGROUND_COLOR = 25;
    private static final int TEXT_FLAGS_INDEX = 27;

    private final JavaPlugin plugin;
    private final DamageIndicatorConfig config;
    private final Location location;
    private final Component text;
    private final Collection<? extends Player> viewers;
    private final int entityId;
    private final UUID entityUuid = UUID.randomUUID();
    private boolean destroyed;

    DamageHologram(JavaPlugin plugin, DamageIndicatorConfig config, Location location, Component text,
                  Collection<? extends Player> viewers) {
        this.plugin = plugin;
        this.config = config;
        this.location = location;
        this.text = text;
        this.viewers = viewers;
        this.entityId = Bukkit.getUnsafe().nextEntityId(location.getWorld());
    }

    void start(Runnable expire) {
        sendToViewers(new WrapperPlayServerSpawnEntity(
                entityId,
                Optional.of(entityUuid),
                EntityTypes.TEXT_DISPLAY,
                new Vector3d(location.getX(), location.getY(), location.getZ()),
                0.0f, 0.0f, 0.0f, 0,
                Optional.of(Vector3d.zero())
        ));
        sendToViewers(new WrapperPlayServerEntityMetadata(entityId, initialMetadata()));

        BukkitScheduler scheduler = plugin.getServer().getScheduler();
        scheduler.runTaskLater(plugin, () -> sendScale(config.popScale(), 4), config.popDelay());
        scheduler.runTaskLater(plugin, () -> sendScale(config.settleScale(), 1), config.settleDelay());
        scheduler.runTaskLater(plugin, () -> sendScale(SHRINK_SCALE, 3), config.shrinkDelay());
        scheduler.runTaskLater(plugin, expire, config.removeDelay());
    }

    private List<EntityData<?>> initialMetadata() {
        List<EntityData<?>> metadata = new ArrayList<>(8);
        metadata.add(new EntityData<>(TRANSFORMATION_INTERPOLATION_DELAY, EntityDataTypes.INT, 0));
        metadata.add(new EntityData<>(TRANSFORMATION_INTERPOLATION_DURATION, EntityDataTypes.INT, 0));
        metadata.add(new EntityData<>(POSITION_ROTATION_INTERPOLATION_DURATION, EntityDataTypes.INT, 0));
        metadata.add(new EntityData<>(SCALE, EntityDataTypes.VECTOR3F, vector(config.spawnScale())));
        metadata.add(new EntityData<>(BILLBOARD_CONSTRAINTS, EntityDataTypes.BYTE, BILLBOARD_CENTER));
        metadata.add(new EntityData<>(TEXT, EntityDataTypes.ADV_COMPONENT, text));
        metadata.add(new EntityData<>(BACKGROUND_COLOR, EntityDataTypes.INT, 0));
        metadata.add(new EntityData<>(TEXT_FLAGS_INDEX, EntityDataTypes.BYTE, TEXT_FLAGS));
        return metadata;
    }

    private void sendScale(float scale, int interpolationTicks) {
        if (destroyed) return;
        List<EntityData<?>> metadata = List.of(
                new EntityData<>(TRANSFORMATION_INTERPOLATION_DELAY, EntityDataTypes.INT, 0),
                new EntityData<>(TRANSFORMATION_INTERPOLATION_DURATION, EntityDataTypes.INT, interpolationTicks),
                new EntityData<>(POSITION_ROTATION_INTERPOLATION_DURATION, EntityDataTypes.INT, interpolationTicks),
                new EntityData<>(SCALE, EntityDataTypes.VECTOR3F, vector(scale))
        );
        sendToViewers(new WrapperPlayServerEntityMetadata(entityId, metadata));
    }

    void destroy() {
        destroyed = true;
        sendToViewers(new WrapperPlayServerDestroyEntities(entityId));
    }

    private void sendToViewers(PacketWrapper<?> packet) {
        for (Player viewer : viewers) {
            if (viewer.isOnline()) PacketEvents.getAPI().getPlayerManager().sendPacket(viewer, packet);
        }
    }

    private static Vector3f vector(float scale) {
        return new Vector3f(scale, scale, scale);
    }
}
