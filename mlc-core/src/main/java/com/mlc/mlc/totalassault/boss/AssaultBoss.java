package com.mlc.mlc.totalassault.boss;

import org.bukkit.Location;
import org.bukkit.Material;

import java.util.Set;

/** 每种总力战 Boss 提供各自的难度属性、物品禁用规则和生成方法。 */
public interface AssaultBoss {
    /** 创建挑战前检查依赖和模型，失败时不得扣票。 */
    default void validate() {}
    String id();
    BossAttributes attributes(String difficulty);
    Set<String> bannedModels();
    Set<Material> bannedMaterials();
    BossHandle spawn(Location location, String difficulty, double health);
}
