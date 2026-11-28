package com.mlc.mlc.totalassault.boss.saint;

import com.mlc.mlc.totalassault.boss.BossHandle;
import com.mlc.mlc.totalassault.TaText;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.SoundCategory;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Mannequin;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
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
 * 移动追踪弹：每次施放依次经过难度表指定的全部固定点，途中发射追踪弹；未配置点位时跳过。
 * 头顶弹幕：在圣徒上方随机偏移的位置持续发射弹幕，弹幕沿发射时的方向飞行。
 *
 * 普通技能按难度表和冷却轮流选择；大招可以打断普通技能，眩晕会中断当前普通技能。
 * 各技能的伤害、范围、时间及可用技能在 SaintDifficulty 中按难度调整。
 * 技能音效使用原版声音，仅发送给本次挑战玩家；音效名称、音量和音调在下方统一调整。
 * 已生成的弹幕由本控制器维护，在出战结束时统一清理。
 */
final class SaintSkills implements BossHandle {
    private static final double FULL = 100;
    /** 圣徒战斗台词；每 20 秒随机选择一句，仅挑战玩家可见。 */
    private static final List<String> DIALOGUES = List.of(
            "<#f8e5a0>「此刻，群星将为汝等送葬。」",
            "<#f8e5a0>「神明不再垂怜，唯有终焉永存。」",
            "<#f8e5a0>「见证吧，此即万象归墟之刻！」",
            "<#f8e5a0>「以吾之名，宣告此世终末。」",
            "<#f8e5a0>「时间、空间、因果……皆归于虚无！」",
            "<#f8e5a0>「天幕倾覆，星海沉沦。」",
            "<#f8e5a0>「星辰坠落之时，便是尔等葬身之日。」",
            "<#f8e5a0>「汝逃得过幻影，却逃不过宿命。」",
            "<#f8e5a0>「僭越神权者，当受天诛！」"
    );
    /** 音效标识、音量与音调；替换为资源包音效时可直接修改声音标识。 */
    private record SoundCue(String sound, float volume, float pitch) {}
    private record SkillSounds(SoundCue start, SoundCue hit) {}

    private static final SkillSounds NORMAL_SOUNDS = new SkillSounds(
            new SoundCue("minecraft:entity.player.attack.sweep", 1.0f, 1.0f),
            new SoundCue("minecraft:entity.player.attack.strong", 1.0f, 1.0f));
    private static final SkillSounds LIGHTNING_SOUNDS = new SkillSounds(
            new SoundCue("minecraft:block.beacon.ambient", 0.8f, 1.5f),
            new SoundCue("minecraft:entity.lightning_bolt.thunder", 0.8f, 1.3f));
    private static final SkillSounds SHOCKWAVE_SOUNDS = new SkillSounds(
            new SoundCue("minecraft:entity.warden.sonic_charge", 0.9f, 1.2f),
            new SoundCue("minecraft:entity.warden.sonic_boom", 1.0f, 1.2f));
    private static final SkillSounds ULTIMATE_SOUNDS = new SkillSounds(
            new SoundCue("minecraft:entity.ender_dragon.growl", 0.9f, 0.8f),
            new SoundCue("minecraft:entity.generic.explode", 1.0f, 0.8f));
    private static final SkillSounds MOVE_SOUNDS = new SkillSounds(
            new SoundCue("minecraft:entity.illusioner.prepare_mirror", 0.8f, 1.1f),
            new SoundCue("minecraft:block.glass.break", 0.8f, 1.4f));
    private static final SkillSounds BARRAGE_SOUNDS = new SkillSounds(
            new SoundCue("minecraft:block.amethyst_block.chime", 0.8f, 1.2f),
            new SoundCue("minecraft:entity.arrow.hit_player", 0.8f, 1.0f));
    private static final SoundCue TRACKING_SHOT = new SoundCue("minecraft:entity.snowball.throw", 0.6f, 1.3f);
    private static final SoundCue BARRAGE_SHOT = new SoundCue("minecraft:entity.arrow.shoot", 0.45f, 1.6f);
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
    private long nextDialogue;
    private long activeAt;
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
    private long nextSkillAt;
    private int strikes;
    private int previewTicks;
    private List<SaintDifficulty.Point> movePoints = List.of();
    private int movePoint;

    /** 弹幕由服务端计算轨迹；追踪弹的冰展示实体随弹幕移动并在结束时移除。 */
    private static final class Bullet {
        private final Location location;
        private Vector velocity;
        private final boolean tracking;
        private final double damage;
        private final double speed;
        private final int freezeTicks;
        private final BlockDisplay display;
        private int remaining = 100;

        private Bullet(Location location, Vector velocity, boolean tracking, double damage, double speed,
                       int freezeTicks, BlockDisplay display) {
            this.location = location;
            this.velocity = velocity;
            this.tracking = tracking;
            this.damage = damage;
            this.speed = speed;
            this.freezeTicks = freezeTicks;
            this.display = display;
        }

        private void remove() { if (display != null) display.remove(); }
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
        this.activeAt = this.lastTime + SaintDifficulty.ACTIVATION_DELAY_TICKS * 50_000_000L;
        this.nextDialogue = this.activeAt + 20_000_000_000L;
        task = plugin.getServer().getScheduler().runTaskTimer(plugin, this::tick, 1L, 1L);
    }

    @Override
    public void onCritical() {
        if (task == null || stunnedUntil > System.nanoTime() || pendingStun) return;
        stun = Math.min(FULL, stun + 20);
        if (stun < FULL) return;
        stun = 0;
        if (current == 3) pendingStun = true;
        else {
            long now = System.nanoTime();
            finishSkill(now);
            stunnedUntil = now + 5_000_000_000L;
        }
    }

    private void tick() {
        if (!entity.isValid() || entity.isDead() || !player.isOnline() || !player.getWorld().equals(entity.getWorld())) return;
        // 持续朝向玩家；已锁定的范围技能方向仍沿用施法开始时的方向。
        facePlayer();
        long now = System.nanoTime();
        if (now < activeAt) {
            // 等待激活期间允许玩家攻击，但技能和大招进度都不运行。
            lastTime = now;
            return;
        }
        if (now >= nextDialogue) {
            String dialogue = DIALOGUES.get(ThreadLocalRandom.current().nextInt(DIALOGUES.size()));
            player.sendMessage(TaText.line("<#ffd166>圣徒 <#aab8b0>» " + dialogue));
            nextDialogue = now + 20_000_000_000L;
        }
        ultimate = Math.min(FULL, ultimate + Math.max(0, now - lastTime) / 1_000_000_000.0 * 2);
        lastTime = now;
        tickBullets();
        // 弹幕伤害可能同步触发死亡和出战结束，此时控制器已经被清理。
        if (task == null) return;
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
            finishSkill(now);
            if (pendingStun) { pendingStun = false; stunnedUntil = now + 5_000_000_000L; }
            return;
        }
        if (now < nextSkillAt) return;
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

    /** 普通技能结束后等待该技能设置的间隔；大招仍由进度条独立触发。 */
    private void finishSkill(long now) {
        int recoveryTicks = switch (current) {
            case 0, 1, 2 -> areaSkill(current).recoveryTicks();
            case 4 -> difficulty.projectile(SaintDifficulty.Skill.MOVE).recoveryTicks();
            case 5 -> difficulty.projectile(SaintDifficulty.Skill.BARRAGE).recoveryTicks();
            default -> 0;
        };
        if (recoveryTicks > 0) nextSkillAt = Math.max(nextSkillAt, now + recoveryTicks * 50_000_000L);
        model.stop();
        current = -1;
    }

    private void begin(int skill, long now) {
        current = skill;
        strikes = 0;
        previewTicks = 0;
        skillStart = now;
        nextAction = now;
        facePlayer();
        target = player.getLocation().clone();
        direction = entity.getLocation().getDirection().setY(0).normalize();
        if (skill <= 3) hitAt = now + areaSkill(skill).warningTicks() * 50_000_000L;
        if (skill == 4) {
            moveStart = entity.getLocation().clone();
            movePoints = SaintDifficulty.MOVEMENT_POINTS;
            movePoint = 0;
            SaintDifficulty.Point point = movePoints.get(movePoint);
            moveEnd = new Location(entity.getWorld(), point.x(), point.y(), point.z());
        }
        // 动画从技能预警开始时播放，名称与难度表中的英文技能名保持一致。
        model.play(switch (skill) {
            case 0 -> "normal_attack";
            case 1 -> "lightning";
            case 2 -> "shockwave";
            case 3 -> "ultimate";
            case 4 -> "move";
            case 5 -> "barrage";
            default -> throw new IllegalArgumentException("未知技能：" + skill);
        });
        playSound(entity.getLocation(), sounds(skill).start());
    }

    /** 将技能编号映射到对应的预警和命中音效。 */
    private SkillSounds sounds(int skill) {
        return switch (skill) {
            case 0 -> NORMAL_SOUNDS;
            case 1 -> LIGHTNING_SOUNDS;
            case 2 -> SHOCKWAVE_SOUNDS;
            case 3 -> ULTIMATE_SOUNDS;
            case 4 -> MOVE_SOUNDS;
            case 5 -> BARRAGE_SOUNDS;
            default -> throw new IllegalArgumentException("未知技能：" + skill);
        };
    }

    /** 音效只发送给挑战玩家，不向世界中的其他玩家广播。 */
    private void playSound(Location location, SoundCue sound) {
        if (player.isOnline() && player.getWorld().equals(location.getWorld())) {
            // 从玩家位置播放并设为最大音量，避免远程技能因距离产生衰减。
            player.playSound(player.getLocation(), sound.sound(), SoundCategory.HOSTILE,
                    Math.max(1.0f, sound.volume()), sound.pitch());
        }
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
        if (current == 2) {
            // 每四刻绘制一次扇形，沿每隔五度的射线以半格间距填充粒子。
            if (previewTicks++ % 4 != 0) return;
            Particle.DustOptions yellow = new Particle.DustOptions(Color.YELLOW, 1.0f);
            double angleStep = Math.toRadians(5);
            for (double offset = -half; offset < half; offset += angleStep) {
                drawShockwaveLine(center, base + offset, radius, yellow);
            }
            drawShockwaveLine(center, base + half, radius, yellow);
            return;
        }
        for (int i = 0; i <= 24; i++) {
            double angle = base - half + 2 * half * i / 24;
            Location point = center.clone().add(Math.cos(angle) * radius, 0.2, Math.sin(angle) * radius);
            if (current == 1 || current == 2) {
                entity.getWorld().spawnParticle(Particle.DUST, point, 1, 0, 0, 0, 0,
                        new Particle.DustOptions(Color.YELLOW, 1.0f));
            } else {
                entity.getWorld().spawnParticle(Particle.END_ROD, point, 1, 0, 0, 0, 0);
            }
        }
    }

    /** 绘制冲击波预警区域内的一条黄色射线，包含最外侧边界。 */
    private void drawShockwaveLine(Location center, double angle, double radius, Particle.DustOptions color) {
        double x = Math.cos(angle);
        double z = Math.sin(angle);
        for (double distance = 0; distance < radius; distance += 0.5) {
            entity.getWorld().spawnParticle(Particle.DUST, center.clone().add(x * distance, 0.2, z * distance),
                    1, 0, 0, 0, 0, color);
        }
        entity.getWorld().spawnParticle(Particle.DUST, center.clone().add(x * radius, 0.2, z * radius),
                1, 0, 0, 0, 0, color);
    }

    private void hit() {
        Location center = current == 0 || current == 1 ? target : entity.getLocation();
        // 即使玩家成功躲开，也能听到技能落下的声音。
        playSound(center, sounds(current).hit());
        Vector delta = player.getLocation().toVector().subtract(center.toVector());
        SaintDifficulty.AreaSkill settings = areaSkill(current);
        double radius = settings.radius();
        if (Math.abs(delta.getY()) > 3) return;
        delta.setY(0);
        if (delta.lengthSquared() > radius * radius) return;
        if (settings.halfAngleDegrees() < 180 && delta.lengthSquared() > 0.0001) {
            if (direction.dot(delta.normalize()) < Math.cos(Math.toRadians(settings.halfAngleDegrees()))) return;
        }
        if (current == 0) {
            // 只抵消普通攻击造成的速度变化，保留玩家受击前已有的移动速度。
            Vector previousVelocity = player.getVelocity().clone();
            player.damage(settings.damage(), entity);
            if (player.isValid() && !player.isDead()) player.setVelocity(previousVelocity);
        } else {
            player.damage(settings.damage(), entity);
        }
        if (settings.slownessTicks() > 0 && !player.isDead()) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, settings.slownessTicks(), settings.slownessAmplifier()));
        }
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
            if (progress >= 1) {
                movePoint++;
                if (movePoint >= movePoints.size()) {
                    finishSkill(now);
                } else {
                    // 以刚到达的位置作为下一段起点，继续前往本次施放中的下一个点。
                    moveStart = entity.getLocation().clone();
                    SaintDifficulty.Point point = movePoints.get(movePoint);
                    moveEnd = new Location(entity.getWorld(), point.x(), point.y(), point.z());
                    skillStart = now;
                }
            }
            return;
        }
        facePlayer();
        SaintDifficulty.ProjectileSkill settings = difficulty.projectile(SaintDifficulty.Skill.BARRAGE);
        if (now - skillStart >= settings.durationTicks() * 50_000_000L) {
            finishSkill(now);
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
        playSound(start, tracking ? TRACKING_SHOT : BARRAGE_SHOT);
        BlockDisplay display = null;
        if (tracking) {
            display = entity.getWorld().spawn(start, BlockDisplay.class, block -> {
                block.setBlock(Material.ICE.createBlockData());
                block.setPersistent(false);
                var transformation = block.getTransformation();
                transformation.getScale().set(0.1f, 0.1f, 0.1f);
                transformation.getTranslation().set(-0.05f, -0.05f, -0.05f);
                block.setTransformation(transformation);
            });
        }
        bullets.add(new Bullet(start, aim.normalize().multiply(settings.speed()), tracking, settings.damage(), settings.speed(),
                settings.freezeTicks(), display));
    }

    /** 推进和碰撞均在主线程处理；碰到方块、命中玩家或超时后移除。 */
    private void tickBullets() {
        var iterator = bullets.iterator();
        while (iterator.hasNext()) {
            Bullet bullet = iterator.next();
            if (--bullet.remaining <= 0 || player.isDead()) { bullet.remove(); iterator.remove(); continue; }
            if (bullet.tracking) {
                Vector aim = player.getEyeLocation().toVector().subtract(bullet.location.toVector());
                if (aim.lengthSquared() > 0.0001) {
                    Vector adjusted = bullet.velocity.clone().normalize().multiply(0.85).add(aim.normalize().multiply(0.15));
                    if (adjusted.lengthSquared() > 0.0001) bullet.velocity = adjusted.normalize().multiply(bullet.speed);
                }
            }
            bullet.location.add(bullet.velocity);
            if (bullet.location.getBlock().getType().isSolid()) { bullet.remove(); iterator.remove(); continue; }
            if (bullet.tracking) {
                if (bullet.display != null) bullet.display.teleport(bullet.location);
                entity.getWorld().spawnParticle(Particle.BLOCK, bullet.location, 4, 0.05, 0.05, 0.05, 0,
                        Material.ICE.createBlockData());
            } else {
                entity.getWorld().spawnParticle(Particle.END_ROD, bullet.location, 1, 0, 0, 0, 0);
            }
            if (player.getBoundingBox().expand(0.3).contains(bullet.location.toVector())) {
                // 先从迭代器移除，伤害事件可能同步结束出战并清空剩余弹幕。
                iterator.remove();
                bullet.remove();
                playSound(bullet.location, bullet.tracking ? MOVE_SOUNDS.hit() : BARRAGE_SOUNDS.hit());
                player.damage(bullet.damage, entity);
                if (task == null) return;
                if (bullet.freezeTicks > 0 && !player.isDead()) {
                    player.setFreezeTicks(Math.max(player.getFreezeTicks(), bullet.freezeTicks));
                }
            }
        }
    }

    @Override
    public void remove() {
        if (task != null) { task.cancel(); task = null; }
        bullets.forEach(Bullet::remove);
        bullets.clear();
        try { model.close(); } finally { entity.remove(); }
    }
}
