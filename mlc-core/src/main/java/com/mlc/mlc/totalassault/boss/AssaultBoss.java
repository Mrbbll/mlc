package com.mlc.mlc.totalassault.boss;

import org.bukkit.Location;
import org.bukkit.Material;

import java.util.Set;
import java.util.List;

/** 每种总力战 Boss 提供各自的难度属性、物品禁用规则和生成方法。 */
public interface AssaultBoss {
    /** 显示在难度说明中的技能名称、描述和当前难度参数。 */
    record SkillInfo(String name, String description, List<String> details) {}
    /** 创建挑战前检查依赖和模型，失败时不得扣票。 */
    default void validate() {}
    String id();
    /** 用于通关通报的纯文字名称，展示时由调用方转义并套用格式。 */
    default String displayName() { return id(); }
    default List<SkillInfo> skillInfo(String difficulty) { return List.of(); }
    BossAttributes attributes(String difficulty);
    Set<String> bannedModels();
    Set<Material> bannedMaterials();
    BossHandle spawn(Location location, String difficulty, double health);
}
