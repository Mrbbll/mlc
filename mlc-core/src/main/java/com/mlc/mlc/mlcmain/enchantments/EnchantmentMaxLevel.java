package com.mlc.mlc.mlcmain.enchantments;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.KeyPattern;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class EnchantmentMaxLevel {
    public static Map<Enchantment,Integer> maxLevelMap = new HashMap<>();

    static {
        maxLevelMap.put(Enchantment.AQUA_AFFINITY,1);
        maxLevelMap.put(Enchantment.BANE_OF_ARTHROPODS,1);
        maxLevelMap.put(Enchantment.BREACH,1);
        maxLevelMap.put(Enchantment.BINDING_CURSE,1);
        maxLevelMap.put(Enchantment.BLAST_PROTECTION,1);
        maxLevelMap.put(Enchantment.CHANNELING,1);
        maxLevelMap.put(Enchantment.DENSITY,1);
        maxLevelMap.put(Enchantment.DEPTH_STRIDER,1);
        maxLevelMap.put(Enchantment.EFFICIENCY,1);
        maxLevelMap.put(Enchantment.FORTUNE,1);
        maxLevelMap.put(Enchantment.FEATHER_FALLING,1);
        maxLevelMap.put(Enchantment.FLAME,1);
        maxLevelMap.put(Enchantment.FIRE_ASPECT,1);
        maxLevelMap.put(Enchantment.FROST_WALKER,1);
        maxLevelMap.put(Enchantment.FIRE_PROTECTION,1);
        maxLevelMap.put(Enchantment.IMPALING,1);
        maxLevelMap.put(Enchantment.INFINITY,1);
        maxLevelMap.put(Enchantment.KNOCKBACK,1);
        maxLevelMap.put(Enchantment.LOOTING,1);
        maxLevelMap.put(Enchantment.LOYALTY,1);
        maxLevelMap.put(Enchantment.LURE,1);
        maxLevelMap.put(Enchantment.LUNGE,1);
        maxLevelMap.put(Enchantment.LUCK_OF_THE_SEA,1);
        maxLevelMap.put(Enchantment.MENDING,1);
        maxLevelMap.put(Enchantment.MULTISHOT,1);
        maxLevelMap.put(Enchantment.PROTECTION,1);
        maxLevelMap.put(Enchantment.PIERCING,1);
        maxLevelMap.put(Enchantment.POWER,1);
        maxLevelMap.put(Enchantment.PUNCH,1);
        maxLevelMap.put(Enchantment.PROJECTILE_PROTECTION,1);
        maxLevelMap.put(Enchantment.QUICK_CHARGE,1);
        maxLevelMap.put(Enchantment.RESPIRATION,1);
        maxLevelMap.put(Enchantment.RIPTIDE,1);
        maxLevelMap.put(Enchantment.SHARPNESS,1);
        maxLevelMap.put(Enchantment.SMITE,1);
        maxLevelMap.put(Enchantment.SILK_TOUCH,1);
        maxLevelMap.put(Enchantment.SOUL_SPEED,1);
        maxLevelMap.put(Enchantment.SWEEPING_EDGE,1);
        maxLevelMap.put(Enchantment.SWIFT_SNEAK,1);
        maxLevelMap.put(Enchantment.THORNS,1);
        maxLevelMap.put(Enchantment.UNBREAKING,1);
        maxLevelMap.put(Enchantment.VANISHING_CURSE,1);
        maxLevelMap.put(Enchantment.WIND_BURST,1);
    }

    public static int getMaxLevel(Enchantment enchantment) {
        return maxLevelMap.getOrDefault(enchantment,1);
    }
    private static Enchantment getVanillaEnchantment(@NotNull @KeyPattern.Value String key) {
        return RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).getOrThrow(Key.key(Key.MINECRAFT_NAMESPACE, key));
    }
}
