package com.mlc.mlc.mlcmain.enchantments.enchantupgrade;

/** 附魔升级的概率与修复时间规则。 */
public final class UpgradeRules {
    // 每次升级消耗的经验等级；成功和失败均消耗。
    public static final int UPGRADE_EXPERIENCE_LEVELS = 50;
    public static final int UPGRADE_RUNES_PER_ATTEMPT = 1;
    public static final int PROTECTION_RUNES_ON_FAILURE = 1;
    public static final int REPAIR_RUNES_PER_ITEM = 1;
    public static final int STABILITY_RUNES_PER_PREVENTION = 1;
    public static final double NEGATIVE_ENCHANTMENT_CHANCE = 0.10;
    // 每一级已有魔咒对应的修复时间；按现实时间计算。
    public static final long REPAIR_HOURS_PER_ENCHANTMENT_LEVEL = 1L;
    public static final long MILLIS_PER_HOUR = 60L * 60L * 1000L;

    private UpgradeRules() {
    }

    /** 失败概率 = 1 - 1 / 已有魔咒等级之和。 */
    public static double failureProbability(int totalLevels) {
        if (totalLevels <= 0) throw new IllegalArgumentException("totalLevels must be positive");
        return 1.0 - 1.0 / totalLevels;
    }

    /** 修复时间 = 已有魔咒等级之和 × 每级小时数。 */
    public static long repairDurationMillis(int totalLevels) {
        if (totalLevels <= 0) throw new IllegalArgumentException("totalLevels must be positive");
        return Math.multiplyExact(Math.multiplyExact((long) totalLevels, REPAIR_HOURS_PER_ENCHANTMENT_LEVEL), MILLIS_PER_HOUR);
    }

    /** 修复符文允许跳过自然恢复时间，但不能修复缺少期限的物品。 */
    public static boolean canRepair(long readyAt, long nowMillis, boolean useRune) {
        return readyAt >= 0 && (useRune || nowMillis >= readyAt);
    }
}
