package com.mlc.mlc.mlcmain.crates;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import java.util.concurrent.ThreadLocalRandom;


public enum BannerPatternRegistry {

    CREEPER(Material.CREEPER_BANNER_PATTERN),
    SKULL(Material.SKULL_BANNER_PATTERN),
    FLOWER(Material.FLOWER_BANNER_PATTERN),
    MOJANG(Material.MOJANG_BANNER_PATTERN),
    GLOBE(Material.GLOBE_BANNER_PATTERN),
    PIGLIN(Material.PIGLIN_BANNER_PATTERN),
    FIELD_MASONED(Material.FIELD_MASONED_BANNER_PATTERN),
    BORDURE_INDENTED(Material.BORDURE_INDENTED_BANNER_PATTERN),
    GUSTER(Material.GUSTER_BANNER_PATTERN),
    FLOW(Material.FLOW_BANNER_PATTERN);


    private final Material patternMaterial;

    BannerPatternRegistry(Material patternMaterial) {
        this.patternMaterial = patternMaterial;
    }

    // 核心方法：随机获取旗帜图案 ItemStack
    public static ItemStack randomPattern() {
        BannerPatternRegistry[] values = values();
        BannerPatternRegistry random = values[ThreadLocalRandom.current().nextInt(values.length)];
        return new ItemStack(random.patternMaterial);
    }

    public Material getPatternMaterial() {
        return patternMaterial;
    }
}