// HumanController/EntityHumanNPC lifecycle adapted from Citizens2 v26_3_R1 (OSL-3.0).
package com.mlc.mlcbot.nms;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.util.Vector;
import org.bukkit.plugin.java.JavaPlugin;

/** A real, damageable ServerPlayer with no socket and no login/PlayerList entry. */
public final class BotPlayer extends ServerPlayer {
    private float walkForward;
    private float walkStrafe;
    private boolean jumpRequested;
    private final JavaPlugin plugin;
    private ItemStack lastWeapon = ItemStack.EMPTY;

    public BotPlayer(JavaPlugin plugin, Location at, String name) {
        super(MinecraftServer.getServer(), ((CraftWorld) at.getWorld()).getHandle(),
                new GameProfile(UUID.randomUUID(), name), ClientInformation.createDefault());
        this.plugin = plugin;
        EmptyConnection network = new EmptyConnection();
        connection = new EmptyPacketListener(MinecraftServer.getServer(), network, this,
                CommonListenerCookie.createInitial(getGameProfile(), false));
        network.setListenerForServerboundHandshake(connection);
        EmptyConnection.setField(ServerGamePacketListenerImpl.class, connection, "waitingForRespawn", false);
        EmptyConnection.setField(ServerGamePacketListenerImpl.class, connection, "clientLoadedTimeoutTimer", 0);
        snapTo(at.getX(), at.getY(), at.getZ(), at.getYaw(), at.getPitch());
        setYHeadRot(at.getYaw());
        getBukkitEntity().setSleepingIgnored(true);
        getBukkitEntity().setPersistent(false);
        moonrise$setRealPlayer(false);
    }

    /** Input is a world-space direction; yaw still faces the opponent. */
    public void steer(Vector direction, boolean jump) {
        double yaw = Math.toRadians(getYRot());
        walkForward = (float) (-Math.sin(yaw) * direction.getX() + Math.cos(yaw) * direction.getZ());
        walkStrafe = (float) (Math.cos(yaw) * direction.getX() + Math.sin(yaw) * direction.getZ());
        jumpRequested = jump;
    }

    @Override
    public void doTick() {
        // ServerPlayer's normal doTick expects client input. Citizens supplies autonomous travel instead.
        super.baseTick();
        if (!ItemStack.isSameItem(lastWeapon, getMainHandItem())) {
            resetOnlyAttackStrengthTicker();
            lastWeapon = getMainHandItem().copy();
        }
        if (isAlive()) {
            if (jumpRequested && onGround()) jumpFromGround();
            if (isInWater() && jumpRequested) setDeltaMovement(getDeltaMovement().add(0, 0.04, 0));
            setSpeed((float) getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED));
            double x = getX(), y = getY(), z = getZ();
            travel(new Vec3(walkStrafe, 0, walkForward));
            doCheckFallDamage(getX() - x, getY() - y, getZ() - z, onGround());
            applyEffectsFromBlocks();
            pushEntities();
        }
        jumpRequested = false;
        ++attackStrengthTicker;
        getCooldowns().tick();
        if (isUsingItem()) updateUsingItem(getItemInHand(getUsedItemHand()));
        updatePlayerPose();
    }

    @Override
    public void tick() {
        super.tick();
        // Fake connections don't tick doTick as connected players do.
        doTick();
        detectEquipmentUpdates();
    }

    @Override
    public boolean hurtServer(ServerLevel world, DamageSource source, float damage) {
        boolean hurt = super.hurtServer(world, source, damage);
        // Citizens prevents Player.attack from restoring the pre-hit velocity after sending it to a nonexistent client.
        if (hurt && syncVelocity) {
            syncVelocity = false;
            Bukkit.getScheduler().runTask(plugin, () -> { if (!isRemoved()) syncVelocity = true; });
        }
        return hurt;
    }

    public void destroy() {
        steer(new Vector(), false);
        ServerLevel world = level();
        world.removePlayerImmediately(this, RemovalReason.DISCARDED);
        world.getChunkSource().removeEntity(this);
    }
}
