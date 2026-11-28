package com.mlc.mlc.totalassault.boss.saint;

import com.mlc.mlc.totalassault.boss.BossHandle;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Mannequin;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 圣徒的一次出战技能控制器，所有世界和实体操作均在服务端主线程执行。
 *
 * 普通攻击：在玩家当前位置生成圆形预警，多次攻击时每次重新锁定位置。
 * 落雷：锁定玩家当前位置，预警后对圆形区域造成伤害。
 * 冲击波：向玩家方向释放扇形攻击。
 * 大招：战斗期间持续积攒进度，满值时释放范围攻击。
 * 移动追踪弹：在难度表指定的固定点之间移动，并向玩家发射持续追踪的弹幕；未配置点位时跳过。
 * 头顶弹幕：在圣徒上方随机偏移的位置持续发射弹幕，弹幕沿发射时的方向飞行。
 *
 * 普通技能按难度表和冷却轮流选择；大招可以打断普通技能，眩晕会中断当前普通技能。
 * 各技能的伤害、范围、时间及可用技能在 SaintDifficulty 中按难度调整。
 * 已生成的弹幕由本控制器维护，在出战结束时统一清理。
 */
final class SaintSkills implements BossHandle {
    private static final double FULL = 100;
    /** 普通技能的轮换顺序；大招由进度单独触发。 */
    private static final SaintDifficulty.Skill[] ROTATION = {
            SaintDifficulty.Skill.NORMAL_ATTACK, SaintDifficulty.Skill.LIGHTNING, SaintDifficulty.Skill.SHOCKWAVE,
            SaintDifficulty.Skill.MOVE, SaintDifficulty.Skill.BARRAGE
    };
    private final Mannequin entity;
    private final double maximumHealth;
    private final Saint.Model model;
    private final long[] ready = new long[ROTATION.length];
    private final SaintDifficulty difficulty;
    private final List<Bullet> bullets = new ArrayList<>();
    private Player player;
    private BukkitTask task;
    private double ultimate;
    private double stun;
    private long lastTime;
    private long stunnedUntil;
    private boolean pendingStun;
    private int current = -1;
    private int selection;
    private long hitAt;
    private Location target;
    private Vector direction;
    private Location moveStart;
    private Location moveEnd;
    private long skillStart;
    private long nextAction;
    private int strikes;
    private int movePoint;

    /** 弹幕使用服务端粒子和位置模拟，不生成会遗留在世界中的实体。 */
    private static final class Bullet {
        private final Location location;
        private Vector velocity;
        private final boolean tracking;
        private final double damage;
        private final double speed;
        private int remaining = 100;

        private Bullet(Location location, Vector velocity, boolean tracking, double damage, double speed) {
            this.location = location;
            this.velocity = velocity;
            this.tracking = tracking;
            this.damage = damage;
            this.speed = speed;
        }
    }

    SaintSkills(Mannequin entity, double maximumHealth, Saint.Model model, SaintDifficulty difficulty) {
        this.entity = entity;
        this.maximumHealth = maximumHealth;
        this.model = model;
        this.difficulty = difficulty;
    }
    @Override public UUID entityId() { return entity.getUniqueId(); }
    @Override public double health() { return entity.getHealth(); }
    @Override public double ultimateProgress() { return ultimate; }
    @Override public double stunProgress() { return stun; }

    @Override
    public void start(JavaPlugin plugin, Player player, double ultimate, double stun) {
        if (task != null) throw new IllegalStateException("圣徒控制器已经启动");
        this.player = player;
        this.ultimate = Math.max(0, Math.min(FULL, ultimate));
        this.stun = Math.max(0, Math.min(FULL, stun));
        this.lastTime = System.nanoTime();
        task = plugin.getServer().getScheduler().runTaskTimer(plugin, this::tick, 1L, 1L);
    }

    @Override
    public void onCritical() {
        if (task == null || stunnedUntil > System.nanoTime() || pendingStun) return;
        stun = Math.min(FULL, stun + 20);
        if (stun < FULL) return;
        stun = 0;
        if (current == 3) pendingStun = true;
        else { current = -1; stunnedUntil = System.nanoTime() + 5_000_000_000L; }
    }

    private void tick() {
        if (!entity.isValid() || entity.isDead() || !player.isOnline() || !player.getWorld().equals(entity.getWorld())) return;
        long now = System.nanoTime();
        ultimate = Math.min(FULL, ultimate + Math.max(0, now - lastTime) / 1_000_000_000.0 * 2);
        lastTime = now;
        tickBullets();
        if (now < stunnedUntil) return;
        // 大招充满后立即打断普通技能；眩晕期间则等待眩晕结束。
        if (ultimate >= FULL && current != 3 && difficulty.hasSkill(SaintDifficulty.Skill.ULTIMATE)) {
            ultimate = 0;
            begin(3, now);
            return;
        }
        if (current >= 0) {
            if (current == 4 || current == 5) {
                tickContinuous(now);
                return;
            }
            preview();
            if (now < hitAt) return;
            hit();
            SaintDifficulty.AreaSkill settings = areaSkill(current);
            if (++strikes < settings.strikes()) {
                // 普通攻击的每一轮重新锁定玩家，其他技能沿用最初的目标位置。
                if (current == 0) target = player.getLocation().clone();
                hitAt = now + settings.strikeIntervalTicks() * 50_000_000L;
                return;
            }
            current = -1;
            if (pendingStun) { pendingStun = false; stunnedUntil = now + 5_000_000_000L; }
            return;
        }
        facePlayer();
        double distance = entity.getLocation().distanceSquared(player.getLocation());
        for (int i = 0; i < ROTATION.length; i++) {
            int skill = (selection + i) % ROTATION.length;
            if (!difficulty.hasSkill(ROTATION[skill])) continue;
            double shockwaveRadius = skill == 2 ? difficulty.area(SaintDifficulty.Skill.SHOCKWAVE).radius() : 0;
            if (now < ready[skill] || (skill == 2 && distance > shockwaveRadius * shockwaveRadius)) continue;
            if (skill == 3 && SaintDifficulty.MOVEMENT_POINTS.isEmpty()) continue;
            selection = (skill + 1) % ROTATION.length;
            // 3 保留给大招，普通技能编号 3、4 对应移动和弹幕。
            int actualSkill = skill >= 3 ? skill + 1 : skill;
            begin(actualSkill, now);
            double multiplier = entity.getHealth() <= maximumHealth * 0.5 ? 0.8 : 1.0;
            int cooldown = switch (skill) {
                case 0 -> difficulty.area(SaintDifficulty.Skill.NORMAL_ATTACK).cooldownSeconds();
                case 1 -> difficulty.area(SaintDifficulty.Skill.LIGHTNING).cooldownSeconds();
                case 2 -> difficulty.area(SaintDifficulty.Skill.SHOCKWAVE).cooldownSeconds();
                case 3 -> difficulty.projectile(SaintDifficulty.Skill.MOVE).cooldownSeconds();
                default -> difficulty.projectile(SaintDifficulty.Skill.BARRAGE).cooldownSeconds();
            };
            ready[skill] = now + (long) (cooldown * multiplier * 1_000_000_000L);
            return;
        }
    }

    private void begin(int skill, long now) {
        current = skill;
        strikes = 0;
        skillStart = now;
        nextAction = now;
        facePlayer();
        target = player.getLocation().clone();
        direction = entity.getLocation().getDirection().setY(0).normalize();
        if (skill <= 3) hitAt = now + areaSkill(skill).warningTicks() * 50_000_000L;
        if (skill == 4) {
            moveStart = entity.getLocation().clone();
            List<SaintDifficulty.Point> points = SaintDifficulty.MOVEMENT_POINTS.stream()
                    .sorted(java.util.Comparator.comparingDouble(SaintDifficulty.Point::x)
                            .thenComparingDouble(SaintDifficulty.Point::y).thenComparingDouble(SaintDifficulty.Point::z))
                    .toList();
            SaintDifficulty.Point point = points.get(movePoint++ % points.size());
            moveEnd = new Location(entity.getWorld(), point.x(), point.y(), point.z());
        }
        model.play(switch (skill) { case 0 -> "横扫"; case 1 -> "落雷"; case 2 -> "冲击波"; case 4 -> "移动"; case 5 -> "弹幕"; default -> "大招"; });
    }

    /** 根据正在释放的范围技能读取当前难度的参数。 */
    private SaintDifficulty.AreaSkill areaSkill(int skill) {
        return switch (skill) {
            case 0 -> difficulty.area(SaintDifficulty.Skill.NORMAL_ATTACK);
            case 1 -> difficulty.area(SaintDifficulty.Skill.LIGHTNING);
            case 2 -> difficulty.area(SaintDifficulty.Skill.SHOCKWAVE);
            case 3 -> difficulty.area(SaintDifficulty.Skill.ULTIMATE);
            default -> throw new IllegalArgumentException("不是范围技能：" + skill);
        };
    }

    private void facePlayer() {
        Vector direction = player.getLocation().toVector().subtract(entity.getLocation().toVector()).setY(0);
        if (direction.lengthSquared() > 0.0001) entity.setRotation((float) Math.toDegrees(Math.atan2(-direction.getX(), direction.getZ())), 0);
    }

    /** 通过粒子标记技能边界，真实伤害仍在命中时间计算。 */
    private void preview() {
        Location center = current == 0 || current == 1 ? target : entity.getLocation();
        SaintDifficulty.AreaSkill settings = areaSkill(current);
        double radius = settings.radius();
        double half = Math.toRadians(settings.halfAngleDegrees());
        double base = settings.halfAngleDegrees() < 180 ? Math.atan2(direction.getZ(), direction.getX()) : 0;
        for (int i = 0; i <= 24; i++) {
            double angle = base - half + 2 * half * i / 24;
            entity.getWorld().spawnParticle(Particle.END_ROD, center.clone().add(Math.cos(angle) * radius, 0.2, Math.sin(angle) * radius), 1, 0, 0, 0, 0);
        }
    }

    private void hit() {
        Location center = current == 0 || current == 1 ? target : entity.getLocation();
        Vector delta = player.getLocation().toVector().subtract(center.toVector());
        SaintDifficulty.AreaSkill settings = areaSkill(current);
        double radius = settings.radius();
        if (Math.abs(delta.getY()) > 3) return;
        delta.setY(0);
        if (delta.lengthSquared() > radius * radius) return;
        if (settings.halfAngleDegrees() < 180 && delta.lengthSquared() > 0.0001) {
            if (direction.dot(delta.normalize()) < Math.cos(Math.toRadians(settings.halfAngleDegrees()))) return;
        }
        player.damage(settings.damage(), entity);
    }

    /** 按当前难度设定的持续时间和间隔，移动并发射追踪弹或固定方向的弹幕。 */
    private void tickContinuous(long now) {
        if (current == 4) {
            SaintDifficulty.ProjectileSkill settings = difficulty.projectile(SaintDifficulty.Skill.MOVE);
            double progress = Math.min(1, (now - skillStart) / (settings.durationTicks() * 50_000_000.0));
            Vector offset = moveEnd.toVector().subtract(moveStart.toVector()).multiply(progress);
            Location next = moveStart.clone().add(offset);
            next.setYaw(entity.getLocation().getYaw());
            next.setPitch(entity.getLocation().getPitch());
            entity.teleport(next);
            facePlayer();
            if (now >= nextAction && progress < 1) {
                fireBullet(true);
                nextAction = now + settings.intervalTicks() * 50_000_000L;
            }
            if (progress >= 1) current = -1;
            return;
        }
        facePlayer();
        SaintDifficulty.ProjectileSkill settings = difficulty.projectile(SaintDifficulty.Skill.BARRAGE);
        if (now - skillStart >= settings.durationTicks() * 50_000_000L) {
            current = -1;
        } else if (now >= nextAction) {
            fireBullet(false);
            nextAction = now + settings.intervalTicks() * 50_000_000L;
        }
    }

    /** 生成位置在圣徒头顶附近随机偏移，追踪弹每刻修正方向，弹幕按发射方向飞行。 */
    private void fireBullet(boolean tracking) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        SaintDifficulty.ProjectileSkill settings = difficulty.projectile(
                tracking ? SaintDifficulty.Skill.MOVE : SaintDifficulty.Skill.BARRAGE);
        double spread = settings.horizontalSpread();
        double heightSpread = settings.verticalSpread();
        Location start = entity.getLocation().clone().add(
                spread == 0 ? 0 : random.nextDouble(-spread, spread),
                settings.spawnHeight() + (heightSpread == 0 ? 0 : random.nextDouble(0, heightSpread)),
                spread == 0 ? 0 : random.nextDouble(-spread, spread));
        Vector aim = player.getEyeLocation().toVector().subtract(start.toVector());
        if (aim.lengthSquared() < 0.0001) return;
        bullets.add(new Bullet(start, aim.normalize().multiply(settings.speed()), tracking, settings.damage(), settings.speed()));
    }

    /** 推进和碰撞均在主线程处理；碰到方块、命中玩家或超时后移除。 */
    private void tickBullets() {
        var iterator = bullets.iterator();
        while (iterator.hasNext()) {
            Bullet bullet = iterator.next();
            if (--bullet.remaining <= 0 || player.isDead()) { iterator.remove(); continue; }
            if (bullet.tracking) {
                Vector aim = player.getEyeLocation().toVector().subtract(bullet.location.toVector());
                if (aim.lengthSquared() > 0.0001) {
                    Vector adjusted = bullet.velocity.clone().normalize().multiply(0.85).add(aim.normalize().multiply(0.15));
                    if (adjusted.lengthSquared() > 0.0001) bullet.velocity = adjusted.normalize().multiply(bullet.speed);
                }
            }
            bullet.location.add(bullet.velocity);
            if (bullet.location.getBlock().getType().isSolid()) { iterator.remove(); continue; }
            entity.getWorld().spawnParticle(Particle.END_ROD, bullet.location, 1, 0, 0, 0, 0);
            if (player.getBoundingBox().expand(0.3).contains(bullet.location.toVector())) {
                player.damage(bullet.damage, entity);
                iterator.remove();
            }
        }
    }

    @Override
    public void remove() {
        if (task != null) { task.cancel(); task = null; }
        bullets.clear();
        try { model.close(); } finally { entity.remove(); }
    }
}
