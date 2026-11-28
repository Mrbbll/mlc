package com.mlc.mlc.totalassault.boss.saint;

import java.util.Map;
import java.util.Set;

/** 圣徒各难度的血量和技能表；每个技能名称后紧跟该难度的参数。 */
public record SaintDifficulty(double health, Map<Skill, SkillSettings> skills) {
    public enum Skill { NORMAL_ATTACK, LIGHTNING, SHOCKWAVE, MOVE, BARRAGE, ULTIMATE }

    /** 挑战世界内的绝对坐标；填写点位后移动技能才会启用。 */
    public record Point(double x, double y, double z) {}
    public static final Set<Point> MOVEMENT_POINTS = Set.of(
            // 示例：new Point(100, 70, 100), new Point(110, 70, 100)
    );

    public sealed interface SkillSettings permits AreaSkill, ProjectileSkill {}

    /** 范围技能：伤害、半径、半角（度）、预警刻数、冷却秒数、攻击次数、攻击间隔刻数。 */
    public record AreaSkill(double damage, double radius, double halfAngleDegrees, int warningTicks,
                            int cooldownSeconds, int strikes, int strikeIntervalTicks) implements SkillSettings {}

    /** 弹幕技能：伤害、冷却秒数、每刻飞行距离、发射间隔刻数、持续刻数、生成高度、水平偏移、垂直偏移。 */
    public record ProjectileSkill(double damage, int cooldownSeconds, double speed, int intervalTicks,
                                  int durationTicks, double spawnHeight, double horizontalSpread,
                                  double verticalSpread) implements SkillSettings {}

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
                    Map.entry(Skill.NORMAL_ATTACK, new AreaSkill(6, 3, 180, 20, 8, 5, 12)), // 普通攻击
                    Map.entry(Skill.LIGHTNING, new AreaSkill(8, 3, 180, 30, 12, 1, 0)), // 落雷
                    Map.entry(Skill.ULTIMATE, new AreaSkill(14, 20, 180, 60, 0, 1, 0)))); // 大招，进度满时触发
            case "hard" -> new SaintDifficulty(80, Map.ofEntries(
                    Map.entry(Skill.NORMAL_ATTACK, new AreaSkill(6, 3, 180, 20, 8, 5, 12)), // 普通攻击
                    Map.entry(Skill.LIGHTNING, new AreaSkill(8, 3, 180, 30, 12, 1, 0)), // 落雷
                    Map.entry(Skill.SHOCKWAVE, new AreaSkill(10, 12, 45, 40, 16, 1, 0)), // 冲击波
                    Map.entry(Skill.ULTIMATE, new AreaSkill(14, 20, 180, 60, 0, 1, 0)))); // 大招，进度满时触发
            case "hardcore" -> new SaintDifficulty(140, Map.ofEntries(
                    Map.entry(Skill.NORMAL_ATTACK, new AreaSkill(6, 3, 180, 20, 8, 5, 12)), // 普通攻击
                    Map.entry(Skill.LIGHTNING, new AreaSkill(8, 3, 180, 30, 12, 1, 0)), // 落雷
                    Map.entry(Skill.SHOCKWAVE, new AreaSkill(10, 12, 45, 40, 16, 1, 0)), // 冲击波
                    Map.entry(Skill.MOVE, new ProjectileSkill(5, 18, 0.30, 10, 40, 1, 0.75, 0.5)), // 移动追踪弹
                    Map.entry(Skill.ULTIMATE, new AreaSkill(14, 20, 180, 60, 0, 1, 0)))); // 大招，进度满时触发
            case "ex" -> new SaintDifficulty(240, Map.ofEntries(
                    Map.entry(Skill.NORMAL_ATTACK, new AreaSkill(6, 3, 180, 20, 8, 5, 12)), // 普通攻击
                    Map.entry(Skill.LIGHTNING, new AreaSkill(8, 3, 180, 30, 12, 1, 0)), // 落雷
                    Map.entry(Skill.SHOCKWAVE, new AreaSkill(10, 12, 45, 40, 16, 1, 0)), // 冲击波
                    Map.entry(Skill.MOVE, new ProjectileSkill(6, 18, 0.30, 10, 40, 1, 0.75, 0.5)), // 移动追踪弹
                    Map.entry(Skill.BARRAGE, new ProjectileSkill(5, 20, 0.45, 5, 80, 2.5, 0.75, 0.5)), // 头顶弹幕
                    Map.entry(Skill.ULTIMATE, new AreaSkill(14, 20, 180, 60, 0, 1, 0)))); // 大招，进度满时触发
            case "ins" -> new SaintDifficulty(400, Map.ofEntries(
                    Map.entry(Skill.NORMAL_ATTACK, new AreaSkill(6, 3, 180, 20, 8, 5, 12)), // 普通攻击
                    Map.entry(Skill.LIGHTNING, new AreaSkill(8, 3, 180, 30, 12, 1, 0)), // 落雷
                    Map.entry(Skill.SHOCKWAVE, new AreaSkill(10, 12, 45, 40, 16, 1, 0)), // 冲击波
                    Map.entry(Skill.MOVE, new ProjectileSkill(7, 18, 0.30, 10, 40, 1, 0.75, 0.5)), // 移动追踪弹
                    Map.entry(Skill.BARRAGE, new ProjectileSkill(6, 20, 0.45, 5, 80, 2.5, 0.75, 0.5)), // 头顶弹幕
                    Map.entry(Skill.ULTIMATE, new AreaSkill(14, 20, 180, 60, 0, 1, 0)))); // 大招，进度满时触发
            case "tm" -> new SaintDifficulty(640, Map.ofEntries(
                    Map.entry(Skill.NORMAL_ATTACK, new AreaSkill(6, 3, 180, 20, 8, 5, 12)), // 普通攻击
                    Map.entry(Skill.LIGHTNING, new AreaSkill(8, 3, 180, 30, 12, 1, 0)), // 落雷
                    Map.entry(Skill.SHOCKWAVE, new AreaSkill(10, 12, 45, 40, 16, 1, 0)), // 冲击波
                    Map.entry(Skill.MOVE, new ProjectileSkill(8, 18, 0.30, 10, 40, 1, 0.75, 0.5)), // 移动追踪弹
                    Map.entry(Skill.BARRAGE, new ProjectileSkill(7, 20, 0.45, 5, 80, 2.5, 0.75, 0.5)), // 头顶弹幕
                    Map.entry(Skill.ULTIMATE, new AreaSkill(14, 20, 180, 60, 0, 1, 0)))); // 大招，进度满时触发
            case "lu" -> new SaintDifficulty(1000, Map.ofEntries(
                    Map.entry(Skill.NORMAL_ATTACK, new AreaSkill(6, 3, 180, 20, 8, 5, 12)), // 普通攻击
                    Map.entry(Skill.LIGHTNING, new AreaSkill(8, 3, 180, 30, 12, 1, 0)), // 落雷
                    Map.entry(Skill.SHOCKWAVE, new AreaSkill(10, 12, 45, 40, 16, 1, 0)), // 冲击波
                    Map.entry(Skill.MOVE, new ProjectileSkill(9, 18, 0.30, 10, 40, 1, 0.75, 0.5)), // 移动追踪弹
                    Map.entry(Skill.BARRAGE, new ProjectileSkill(8, 20, 0.45, 5, 80, 2.5, 0.75, 0.5)), // 头顶弹幕
                    Map.entry(Skill.ULTIMATE, new AreaSkill(14, 20, 180, 60, 0, 1, 0)))); // 大招，进度满时触发
            default -> throw new IllegalArgumentException("未知圣徒难度：" + id);
        };
    }
}
