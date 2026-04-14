package com.mlc.mlc.mlcmain.crates;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.trim.TrimPattern;
import java.util.concurrent.ThreadLocalRandom;


public enum ArmorTrimPatternRegistry {

    SENTRY(TrimPattern.SENTRY, Material.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE),
    DUNE(TrimPattern.DUNE, Material.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE),
    COAST(TrimPattern.COAST, Material.COAST_ARMOR_TRIM_SMITHING_TEMPLATE),
    WILD(TrimPattern.WILD, Material.WILD_ARMOR_TRIM_SMITHING_TEMPLATE),
    WARD(TrimPattern.WARD, Material.WARD_ARMOR_TRIM_SMITHING_TEMPLATE),
    EYE(TrimPattern.EYE, Material.EYE_ARMOR_TRIM_SMITHING_TEMPLATE),
    VEX(TrimPattern.VEX, Material.VEX_ARMOR_TRIM_SMITHING_TEMPLATE),
    TIDE(TrimPattern.TIDE, Material.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE),
    SNOUT(TrimPattern.SNOUT, Material.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE),
    RIB(TrimPattern.RIB, Material.RIB_ARMOR_TRIM_SMITHING_TEMPLATE),
    SPIRE(TrimPattern.SPIRE, Material.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE),
    SILENCE(TrimPattern.SILENCE, Material.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE),
    WAYFINDER(TrimPattern.WAYFINDER, Material.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE),
    SHAPER(TrimPattern.SHAPER, Material.SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE),
    HOST(TrimPattern.HOST, Material.HOST_ARMOR_TRIM_SMITHING_TEMPLATE),
    RAISER(TrimPattern.RAISER, Material.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE);

    private final TrimPattern pattern;
    private final Material templateItem;

    ArmorTrimPatternRegistry(TrimPattern pattern, Material templateItem) {
        this.pattern = pattern;
        this.templateItem = templateItem;
    }

    // 核心：随机获取 纹饰模板(ItemStack) 同附魔随机逻辑
    public static ItemStack randomTemplate() {
        ArmorTrimPatternRegistry[] values = values();
        ArmorTrimPatternRegistry random = values[ThreadLocalRandom.current().nextInt(values.length)];
        return new ItemStack(random.templateItem);
    }

    // Getter 可选
    public TrimPattern getPattern() {
        return pattern;
    }

    public Material getTemplateItem() {
        return templateItem;
    }
}