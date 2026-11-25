package com.mlc.mlc.mlcmain.fish;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.particle.type.ParticleTypes;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerParticle;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityTeleport;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/** 原版钓鱼的视觉效果：随机假下沉、真实咬钩提示和鱼靠近水花过滤。 */
public final class FishingEffects {
    private static final int FAKE_MIN_TICKS = 80;
    private static final int FAKE_MAX_TICKS = 240;
    private static final int FAKE_ANIMATION_TICKS = 12;
    private static final double WAKE_RADIUS_SQUARED = 100.0;
    private static final Title.Times BITE_TIMES = Title.Times.times(
            Duration.ZERO, Duration.ofMillis(900), Duration.ofMillis(200));

    private final Map<UUID, FloatState> hooks = new HashMap<>();
    // 发包监听器可能运行在网络线程，因此只读取主线程生成的不可变快照。
    private volatile Map<UUID, List<HookPosition>> viewerHooks = Map.of();
    private final BukkitTask ticker;
    private final PacketListenerAbstract particleFilter = new PacketListenerAbstract() {
        @Override
        public void onPacketSend(PacketSendEvent event) {
            if (event.getPacketType() != PacketType.Play.Server.PARTICLE) return;
            Player viewer = event.getPlayer();
            if (viewer == null) return;
            List<HookPosition> positions = viewerHooks.get(viewer.getUniqueId());
            if (positions == null) return;
            WrapperPlayServerParticle packet = new WrapperPlayServerParticle(event);
            // 原版靠近阶段会发 FISHING 水波、单个 BUBBLE，以及少量 SPLASH。
            // 按类型、数量和速度识别；咬钩时的粒子数量和速度不同，继续保留。
            var type = packet.getParticle().getType();
            ApproachType kind = type == ParticleTypes.FISHING ? ApproachType.FISHING
                    : type == ParticleTypes.BUBBLE ? ApproachType.BUBBLE
                    : type == ParticleTypes.SPLASH ? ApproachType.SPLASH : ApproachType.OTHER;
            if (!isApproachParticle(kind, packet.getParticleCount(), packet.getMaxSpeed())) return;
            Vector3d point = packet.getPosition();
            for (HookPosition hook : positions) {
                double dx = point.getX() - hook.x();
                double dy = point.getY() - hook.y();
                double dz = point.getZ() - hook.z();
                if (dx * dx + dy * dy + dz * dz <= WAKE_RADIUS_SQUARED) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    };

    public FishingEffects(JavaPlugin plugin) {
        PacketEvents.getAPI().getEventManager().registerListener(particleFilter);
        ticker = Bukkit.getScheduler().runTaskTimer(plugin, this::tick, 1L, 1L);
    }

    /** 抛竿事件发生在实体加入世界之前，首次检查留到下一个 tick。 */
    public void cast(Player player, FishHook hook) {
        hooks.put(player.getUniqueId(), new FloatState(player, hook, nextDelay()));
    }

    /** 仅响应真正的原版咬钩事件；不会取消事件或修改其战利品。 */
    public void bite(Player player, FishHook hook) {
        FloatState state = hooks.get(player.getUniqueId());
        // 标题直接依据真实事件发送，不依赖视觉效果的追踪状态。
        if (state != null && state.hook.getUniqueId().equals(hook.getUniqueId())) {
            // 原版咬钩窗口为 20～40 tick，避免在这段时间叠加假的下沉。
            if (state.fakeTicks > 0) {
                // 真咬钩期间立即恢复客户端的位置，避免假动画覆盖真实鱼漂。
                sendVisualPosition(state, 0);
                state.fakeTicks = 0;
            }
            state.delay = 60 + nextDelay();
        }
        player.showTitle(Title.title(Component.text("鱼咬钩了！", NamedTextColor.GOLD),
                Component.text("右键收竿", NamedTextColor.YELLOW), BITE_TIMES));
    }

    public void remove(Player player) {
        hooks.remove(player.getUniqueId());
    }

    public void shutdown() {
        ticker.cancel();
        PacketEvents.getAPI().getEventManager().unregisterListener(particleFilter);
        hooks.clear();
        viewerHooks = Map.of();
    }

    private void tick() {
        Map<UUID, List<HookPosition>> worlds = new HashMap<>();
        hooks.values().removeIf(state -> {
            FishHook hook = state.hook;
            if (!hook.isValid() || hook.isDead()) return true;
            Location location = hook.getLocation();
            worlds.computeIfAbsent(location.getWorld().getUID(), ignored -> new ArrayList<>())
                    .add(new HookPosition(location.getX(), location.getY(), location.getZ()));
            if (state.fakeTicks > 0) {
                // 仅向钓鱼者发送位置包，服务端鱼漂、咬钩计时和战利品保持原样。
                int elapsed = FAKE_ANIMATION_TICKS - state.fakeTicks + 1;
                sendVisualPosition(state, fakeOffset(elapsed));
                state.fakeTicks--;
            } else if (hook.isInWater() && --state.delay <= 0) {
                state.fakeTicks = FAKE_ANIMATION_TICKS;
                state.delay = nextDelay();
            }
            return false;
        });
        Map<UUID, List<HookPosition>> snapshot = new HashMap<>();
        for (Player viewer : Bukkit.getOnlinePlayers()) {
            List<HookPosition> positions = worlds.get(viewer.getWorld().getUID());
            if (positions != null) snapshot.put(viewer.getUniqueId(), List.copyOf(positions));
        }
        viewerHooks = Map.copyOf(snapshot);
    }

    /** 给客户端发送临时位置；不修改服务端实体位置或速度。 */
    private static void sendVisualPosition(FloatState state, double yOffset) {
        Location location = state.hook.getLocation();
        PacketEvents.getAPI().getPlayerManager().sendPacket(state.player,
                new WrapperPlayServerEntityTeleport(state.hook.getEntityId(),
                        new Vector3d(location.getX(), location.getY() + yOffset, location.getZ()),
                        location.getYaw(), location.getPitch(), state.hook.isOnGround()));
    }

    /** 下沉后上浮，最后一帧回到服务端的真实位置。 */
    static double fakeOffset(int elapsed) {
        return -0.35 * Math.sin(Math.PI * elapsed / FAKE_ANIMATION_TICKS);
    }

    /** 识别原版鱼靠近阶段的三种粒子，避免误过滤咬钩时的水花。 */
    enum ApproachType { FISHING, BUBBLE, SPLASH, OTHER }

    static boolean isApproachParticle(ApproachType type, int count, float speed) {
        return (type == ApproachType.FISHING && count == 0)
                || (type == ApproachType.BUBBLE && count == 1 && speed == 0.0f)
                || (type == ApproachType.SPLASH && (count == 2 || count == 3) && speed == 0.0f);
    }

    private static int nextDelay() {
        return ThreadLocalRandom.current().nextInt(FAKE_MIN_TICKS, FAKE_MAX_TICKS + 1);
    }

    private static final class FloatState {
        private final Player player;
        private final FishHook hook;
        private int delay;
        private int fakeTicks;

        private FloatState(Player player, FishHook hook, int delay) {
            this.player = player;
            this.hook = hook;
            this.delay = delay;
        }
    }

    private record HookPosition(double x, double y, double z) {}
}
