package com.mlc.mlc.mlcmain.crates;

import org.bukkit.enchantments.Enchantment;
import java.util.concurrent.ThreadLocalRandom;

public enum EnchantmentRegistry {
    // 保护类附魔
    PROTECTION(Enchantment.PROTECTION, 4),
    FIRE_PROTECTION(Enchantment.FIRE_PROTECTION, 4),
    FEATHER_FALLING(Enchantment.FEATHER_FALLING, 4),
    BLAST_PROTECTION(Enchantment.BLAST_PROTECTION, 4),
    PROJECTILE_PROTECTION(Enchantment.PROJECTILE_PROTECTION, 4),

    // 武器附魔
    SHARPNESS(Enchantment.SHARPNESS, 5),
    SMITE(Enchantment.SMITE, 5),
    BANE_OF_ARTHROPODS(Enchantment.BANE_OF_ARTHROPODS, 5),
    KNOCKBACK(Enchantment.KNOCKBACK, 2),
    FIRE_ASPECT(Enchantment.FIRE_ASPECT, 2),
    LOOTING(Enchantment.LOOTING, 3),
    SWEEPING_EDGE(Enchantment.SWEEPING_EDGE, 3),
    //三叉戟附魔
    IMPALING(Enchantment.IMPALING, 5),
    RIPTIDE(Enchantment.RIPTIDE, 3),
    CHANNELING(Enchantment.CHANNELING, 1),
    //重锤附魔
    DENSITY(Enchantment.DENSITY, 1),
    BREACH(Enchantment.BREACH, 3),
    WIND_BURST(Enchantment.WIND_BURST, 3),
    // 工具附魔
    EFFICIENCY(Enchantment.EFFICIENCY, 5),
    SILK_TOUCH(Enchantment.SILK_TOUCH, 1),
    FORTUNE(Enchantment.FORTUNE, 3),

    // 弓箭附魔
    POWER(Enchantment.POWER, 5),
    PUNCH(Enchantment.PUNCH, 2),
    FLAME(Enchantment.FLAME, 1),
    INFINITY(Enchantment.INFINITY, 1),

    //弩附魔
    MULTISHOT(Enchantment.MULTISHOT, 1),
    PIERCING(Enchantment.PIERCING, 4),
    QUICK_CHARGE(Enchantment.QUICK_CHARGE, 3),

    // 钓竿附魔
    LUCK_OF_THE_SEA(Enchantment.LUCK_OF_THE_SEA, 3),
    LURE(Enchantment.LURE, 3),

    // 其他附魔
    UNBREAKING(Enchantment.UNBREAKING, 3),
    RESPIRATION(Enchantment.RESPIRATION, 3),
    AQUA_AFFINITY(Enchantment.AQUA_AFFINITY, 1),
    THORNS(Enchantment.THORNS, 3),
    DEPTH_STRIDER(Enchantment.DEPTH_STRIDER, 3),
    FROST_WALKER(Enchantment.FROST_WALKER, 2),
    MENDING(Enchantment.MENDING, 1),
    BINDING_CURSE(Enchantment.BINDING_CURSE, 1),
    VANISHING_CURSE(Enchantment.VANISHING_CURSE, 1),
    LOYALTY(Enchantment.LOYALTY, 3),
    SOUL_SPEED(Enchantment.SOUL_SPEED, 3),
    SWIFT_SNEAK(Enchantment.SWIFT_SNEAK, 3);

    private final Enchantment enchantment;
    private final int maxLevel;

    EnchantmentRegistry(Enchantment enchantment, int maxLevel) {
        this.enchantment = enchantment;
        this.maxLevel = maxLevel;
    }

    public Enchantment getEnchantment() {
        return enchantment;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    // 随机获取附魔（包含等级）
    public static EnchantmentData randomEnchantment() {
        EnchantmentRegistry[] values = values();
        EnchantmentRegistry random = values[ThreadLocalRandom.current().nextInt(values.length)];
        int level = ThreadLocalRandom.current().nextInt(1, random.maxLevel + 1);
        return new EnchantmentData(random.enchantment, level);
    }

    // 数据记录类
        public record EnchantmentData(Enchantment enchantment, int level) {
    }
}