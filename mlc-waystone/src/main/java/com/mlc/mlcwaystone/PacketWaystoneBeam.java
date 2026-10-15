package com.mlc.mlcwaystone;

import com.mojang.math.Transformation;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.util.Brightness;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.entity.CraftEntityType;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** Packet snapshots only; there are no entities registered in a server world. */
final class PacketWaystoneBeam implements WaystoneBeamManager.Visual {
    private final List<Packet<? super ClientGamePacketListener>> spawnPackets;
    private final ClientboundAddEntityPacket coreSpawn;
    private final ClientboundRemoveEntitiesPacket remove;
    private float yaw;

    PacketWaystoneBeam(Location origin, float height) {
        List<Packet<? super ClientGamePacketListener>> packets = new ArrayList<>();
        int core = column(origin, height, 0.12f,
                ((CraftBlockData) Material.WHITE_CONCRETE.createBlockData()).getState(), packets);
        int halo = column(origin, height, 0.32f,
                ((CraftBlockData) Material.WHITE_STAINED_GLASS.createBlockData()).getState(), packets);
        spawnPackets = List.copyOf(packets);
        coreSpawn = (ClientboundAddEntityPacket) packets.getFirst();
        remove = new ClientboundRemoveEntitiesPacket(core, halo);
    }

    private static int column(Location origin, float height, float width, BlockState block,
                              List<Packet<? super ClientGamePacketListener>> packets) {
        // Detached metadata template, discarded after encoding. Never calls
        // World.spawn/addFreshEntity, starts tracking, or enters a server tick list.
        // Its constructor allocates a server-wide entity ID to prevent collisions.
        EntityType<?> type = CraftEntityType.bukkitToMinecraft(org.bukkit.entity.EntityType.BLOCK_DISPLAY);
        Display.BlockDisplay template = new Display.BlockDisplay(type,
                ((CraftWorld) origin.getWorld()).getHandle());
        template.setPos(origin.getX(), origin.getY(), origin.getZ());
        template.setBlockState(block);
        template.setTransformation(new Transformation(new Matrix4f()
                .translation(-width / 2, 0, -width / 2).scale(width, height, width)));
        template.setBrightnessOverride(new Brightness(15, 15));
        template.setViewRange(4);
        template.setWidth(0);
        template.setHeight(0);
        template.setShadowRadius(0);
        template.setShadowStrength(0);
        // Clients interpolate rotation targets between the five-tick packet updates.
        template.getEntityData().set(Display.DATA_POS_ROT_INTERPOLATION_DURATION_ID, 5);
        packets.add(new ClientboundAddEntityPacket(template.getId(), template.getUUID(),
                origin.getX(), origin.getY(), origin.getZ(), 0, 0,
                type, 0, Vec3.ZERO, 0));
        packets.add(new ClientboundSetEntityDataPacket(template.getId(), template.getEntityData().packAll()));
        return template.getId();
    }

    @Override
    public void show(Player player) {
        List<Packet<? super ClientGamePacketListener>> packets = new ArrayList<>(spawnPackets);
        packets.set(0, new ClientboundAddEntityPacket(coreSpawn.getId(), coreSpawn.getUUID(),
                coreSpawn.getX(), coreSpawn.getY(), coreSpawn.getZ(), 0, yaw,
                coreSpawn.getType(), 0, Vec3.ZERO, 0));
        ((CraftPlayer) player).getHandle().connection.send(new ClientboundBundlePacket(packets));
    }

    @Override
    public void hide(Player player) {
        ((CraftPlayer) player).getHandle().connection.send(remove);
    }

    @Override
    public void animate(Collection<Player> viewers, long ticks) {
        // Approximate vanilla's rotating inner beam; the outer halo stays still.
        // Target the next update so the client continuously interpolates to it.
        yaw = (float) (Math.toDegrees((ticks + 5) * 0.0375) % 360);
        byte packedYaw = (byte) Math.floor(yaw * 256.0 / 360.0);
        var rotation = new ClientboundMoveEntityPacket.Rot(coreSpawn.getId(), packedYaw, (byte) 0, false);
        for (Player viewer : viewers) {
            ((CraftPlayer) viewer).getHandle().connection.send(rotation);
        }
    }
}
