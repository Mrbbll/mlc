package com.mlc.mlc.totalassault.boss.saint;

import java.util.List;
import java.util.Map;

/** 圣徒各难度的血量和技能表；每个技能名称后紧跟该难度的参数。 */
public record SaintDifficulty(double health, Map<Skill, SkillSettings> skills) {
    /** 技能名称和说明与实际伤害参数分开，菜单按当前难度自动组合。 */
    public enum Skill {
        NORMAL_ATTACK("蚀界连环", "连续锁定玩家位置，释放圆形攻击。"),
        LIGHTNING("苍雷神罚", "锁定玩家位置，短暂预警后发动攻击，ins及以上难度会额外缓慢效果。"),
        SHOCKWAVE("裂空之息", "向目标方向释放扇形冲击波。"),
        MOVE("幻影追魂", "依次移动到固定点位，并发射追踪弹，ins及以上难度会额外霜冻效果。"),
        BARRAGE("星陨天幕", "在上方持续生成射向玩家的弹幕。"),
        ULTIMATE("终焉·万象归墟", "战斗中逐渐蓄力，充满后释放范围攻击。");

        private final String displayName;
        private final String description;
        Skill(String displayName, String description) { this.displayName = displayName; this.description = description; }
        public String displayName() { return displayName; }
        public String description() { return description; }
    }

    /** 入场镜头绝对坐标；每个点的 durationTicks 是从该点移动到下一点的时间。 */
    public record CameraPoint(double x, double y, double z, float yaw, float pitch, int durationTicks) {}
    /** 入场文字：主标题按字符显示，副标题在主标题完成后出现。 */
    public static final String INTRO_TITLE = "第七圣徒";
    public static final String INTRO_SUBTITLE = "<#c4abd9>—— 无垢的审判者 ——";
    public static final String INTRO_DIALOGUE = "<#e2d2f0>「愿群星见证，愿神明垂怜。」";
    public static final int INTRO_TITLE_CHARACTER_TICKS = 4;
    /** 镜头结束并开始计时后，圣徒延迟激活技能的时间。 */
    public static final int ACTIVATION_DELAY_TICKS = 200;
    public static final List<CameraPoint> INTRO_POINTS = List.of(
            // 示例：new CameraPoint(20, 78, 70, 0, 10, 60),
            //       new CameraPoint(25, 76, 80, 90, 5, 60)
            new CameraPoint(23,68,87,-12.9f,-25.6f,80),
            new CameraPoint(19,68,88,-45.3f,-26.3f,0)
    );

    /** 挑战世界内的绝对坐标；移动时严格按列表填写顺序依次经过所有点位。 */
    public record Point(double x, double y, double z) {}
    public static final List<Point> MOVEMENT_POINTS = List.of(
            // 示例：new Point(100, 70, 100), new Point(110, 70, 100)
            new Point(27, 74, 77),
            new Point(28, 74, 86),
            new Point(21, 74, 77),
            new Point(24, 71, 92)
    );

    public sealed interface SkillSettings permits AreaSkill, ProjectileSkill {}

    /** 范围技能：伤害、半径、半角（度）、预警刻数、冷却秒数、攻击次数、攻击间隔刻数、结束后的技能间隔刻数、迟缓持续刻数、迟缓等级（0 为一级）。 */
    public record AreaSkill(double damage, double radius, double halfAngleDegrees, int warningTicks,
                            int cooldownSeconds, int strikes, int strikeIntervalTicks, int recoveryTicks,
                            int slownessTicks, int slownessAmplifier) implements SkillSettings {
        /** 没有额外迟缓效果的技能可以使用这个简写。 */
        public AreaSkill(double damage, double radius, double halfAngleDegrees, int warningTicks,
                         int cooldownSeconds, int strikes, int strikeIntervalTicks, int recoveryTicks) {
            this(damage, radius, halfAngleDegrees, warningTicks, cooldownSeconds, strikes, strikeIntervalTicks, recoveryTicks, 0, 0);
        }
    }

    /** 弹幕技能：伤害、冷却秒数、每刻飞行距离、发射间隔刻数、持续刻数、生成高度、水平偏移、垂直偏移、结束后的技能间隔刻数、命中时的霜冻刻数。
     * 移动追踪弹的持续刻数表示移动至每一个点分别需要的时间；头顶弹幕则表示整个技能的持续时间。
     */
    public record ProjectileSkill(double damage, int cooldownSeconds, double speed, int intervalTicks,
                                  int durationTicks, double spawnHeight, double horizontalSpread,
                                  double verticalSpread, int recoveryTicks, int freezeTicks) implements SkillSettings {
        /** 没有额外霜冻效果的技能可以使用这个简写。 */
        public ProjectileSkill(double damage, int cooldownSeconds, double speed, int intervalTicks,
                               int durationTicks, double spawnHeight, double horizontalSpread, double verticalSpread, int recoveryTicks) {
            this(damage, cooldownSeconds, speed, intervalTicks, durationTicks, spawnHeight, horizontalSpread, verticalSpread, recoveryTicks, 0);
        }
    }

    public SaintDifficulty {
        skills = Map.copyOf(skills);
        skills.forEach((skill, settings) -> {
            boolean projectile = skill == Skill.MOVE || skill == Skill.BARRAGE;
            if (projectile != (settings instanceof ProjectileSkill)) {
                throw new IllegalArgumentException("技能参数类型不匹配：" + skill);
            }
        });
    }

    /** 未在该难度列出的技能不可使用。 */
    public boolean hasSkill(Skill skill) { return skills.containsKey(skill); }
    public AreaSkill area(Skill skill) { return (AreaSkill) skills.get(skill); }
    public ProjectileSkill projectile(Skill skill) { return (ProjectileSkill) skills.get(skill); }

    public static SaintDifficulty of(String id) {
        return switch (id) {
            case "normal" -> new SaintDifficulty(40, Map.ofEntries(
                    Map.entry(Skill.NORMAL_ATTACK, new AreaSkill(6, 3, 180, 20, 8, 5, 12, 60)), // 普通攻击
                    Map.entry(Skill.LIGHTNING, new AreaSkill(8, 3, 180, 30, 12, 1, 0, 60)), // 落雷
                    Map.entry(Skill.ULTIMATE, new AreaSkill(14, 20, 180, 60, 0, 1, 0, 0)))); // 大招，进度满时触发
            case "hard" -> new SaintDifficulty(80, Map.ofEntries(
                    Map.entry(Skill.NORMAL_ATTACK, new AreaSkill(6, 3, 180, 20, 8, 5, 12, 60)), // 普通攻击
                    Map.entry(Skill.LIGHTNING, new AreaSkill(8, 3, 180, 30, 12, 1, 0, 60)), // 落雷
                    Map.entry(Skill.SHOCKWAVE, new AreaSkill(10, 12, 45, 40, 16, 1, 0, 60)), // 冲击波
                    Map.entry(Skill.ULTIMATE, new AreaSkill(14, 20, 180, 60, 0, 1, 0, 0)))); // 大招，进度满时触发
            case "hardcore" -> new SaintDifficulty(140, Map.ofEntries(
                    Map.entry(Skill.NORMAL_ATTACK, new AreaSkill(6, 3, 180, 20, 8, 5, 12, 60)), // 普通攻击
                    Map.entry(Skill.LIGHTNING, new AreaSkill(8, 3, 180, 30, 12, 1, 0, 60)), // 落雷
                    Map.entry(Skill.SHOCKWAVE, new AreaSkill(10, 12, 45, 40, 16, 1, 0, 60)), // 冲击波
                    Map.entry(Skill.MOVE, new ProjectileSkill(5, 18, 0.30, 10, 40, 1, 0.75, 0.5, 60)), // 移动追踪弹
                    Map.entry(Skill.ULTIMATE, new AreaSkill(14, 20, 180, 60, 0, 1, 0, 0)))); // 大招，进度满时触发
            case "ex" -> new SaintDifficulty(240, Map.ofEntries(
                    Map.entry(Skill.NORMAL_ATTACK, new AreaSkill(8, 3, 180, 20, 8, 5, 12, 60)), // 普通攻击
                    Map.entry(Skill.LIGHTNING, new AreaSkill(8, 3, 180, 30, 12, 1, 0, 60)), // 落雷
                    Map.entry(Skill.SHOCKWAVE, new AreaSkill(10, 12, 45, 40, 16, 1, 0, 60)), // 冲击波
                    Map.entry(Skill.MOVE, new ProjectileSkill(6, 18, 0.30, 10, 40, 1, 0.75, 0.5, 60)), // 移动追踪弹
                    Map.entry(Skill.BARRAGE, new ProjectileSkill(5, 20, 0.45, 5, 80, 2.5, 0.75, 0.5, 60)), // 头顶弹幕
                    Map.entry(Skill.ULTIMATE, new AreaSkill(25, 20, 180, 60, 0, 1, 0, 0)))); // 大招，进度满时触发
            case "ins" -> new SaintDifficulty(600, Map.ofEntries(
                    Map.entry(Skill.NORMAL_ATTACK, new AreaSkill(10, 3, 180, 20, 8, 5, 12, 60)), // 普通攻击
                    Map.entry(Skill.LIGHTNING, new AreaSkill(30, 3, 180, 30, 12, 1, 0, 60, 60, 0)), // 落雷：迟缓 60 刻，一级
                    Map.entry(Skill.SHOCKWAVE, new AreaSkill(25, 12, 45, 40, 16, 1, 0, 60, 60, 0)), // 冲击波
                    Map.entry(Skill.MOVE, new ProjectileSkill(10, 18, 0.30, 10, 40, 1, 0.75, 0.5, 60, 140)), // 移动追踪弹
                    Map.entry(Skill.BARRAGE, new ProjectileSkill(10, 20, 0.45, 5, 80, 2.5, 0.75, 0.5, 60)), // 头顶弹幕
                    Map.entry(Skill.ULTIMATE, new AreaSkill(40, 20, 180, 60, 0, 1, 0, 0)))); // 大招，进度满时触发
            case "tm" -> new SaintDifficulty(840, Map.ofEntries(
                    Map.entry(Skill.NORMAL_ATTACK, new AreaSkill(20, 3, 180, 20, 8, 5, 12, 60)), // 普通攻击
                    Map.entry(Skill.LIGHTNING, new AreaSkill(40, 3, 180, 30, 12, 1, 0, 60, 120, 1)), // 落雷：迟缓 120 刻，2级
                    Map.entry(Skill.SHOCKWAVE, new AreaSkill(30, 12, 45, 40, 16, 1, 0, 60, 60, 0)), // 冲击波
                    Map.entry(Skill.MOVE, new ProjectileSkill(20, 18, 0.30, 10, 40, 1, 0.75, 0.5, 60, 140)), // 移动追踪弹
                    Map.entry(Skill.BARRAGE, new ProjectileSkill(20, 20, 0.45, 5, 80, 2.5, 0.75, 0.5, 60)), // 头顶弹幕
                    Map.entry(Skill.ULTIMATE, new AreaSkill(50, 20, 180, 60, 0, 1, 0, 0)))); // 大招，进度满时触发
            case "lu" -> new SaintDifficulty(1000, Map.ofEntries(
                    Map.entry(Skill.NORMAL_ATTACK, new AreaSkill(30, 3, 180, 20, 8, 5, 12, 60)), // 普通攻击
                    Map.entry(Skill.LIGHTNING, new AreaSkill(60, 3, 180, 30, 12, 1, 0, 60, 180, 1)), // 落雷：迟缓 180 刻，2级
                    Map.entry(Skill.SHOCKWAVE, new AreaSkill(70, 12, 45, 40, 16, 1, 0, 60, 60, 0)), // 冲击波
                    Map.entry(Skill.MOVE, new ProjectileSkill(50, 18, 0.30, 10, 40, 1, 0.75, 0.5, 60, 140)), // 移动追踪弹
                    Map.entry(Skill.BARRAGE, new ProjectileSkill(60, 20, 0.45, 5, 80, 2.5, 0.75, 0.5, 60)), // 头顶弹幕
                    Map.entry(Skill.ULTIMATE, new AreaSkill(60, 20, 180, 60, 0, 1, 0, 0)))); // 大招，进度满时触发
            default -> throw new IllegalArgumentException("未知圣徒难度：" + id);
        };
    }
}
