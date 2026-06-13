package com.mlc.mlc.mlcmain.crates;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import java.util.concurrent.ThreadLocalRandom;


public enum PotterySherdRegistry {


    ARCHER(Material.ARCHER_POTTERY_SHERD),
    ARMS_UP(Material.ARMS_UP_POTTERY_SHERD),
    BLADE(Material.BLADE_POTTERY_SHERD),
    BREWER(Material.BREWER_POTTERY_SHERD),
    BURN(Material.BURN_POTTERY_SHERD),
    DANGER(Material.DANGER_POTTERY_SHERD),
    EXPLORER(Material.EXPLORER_POTTERY_SHERD),
    FRIEND(Material.FRIEND_POTTERY_SHERD),
    HEART(Material.HEART_POTTERY_SHERD),
    HEARTBREAK(Material.HEARTBREAK_POTTERY_SHERD),
    HOWL(Material.HOWL_POTTERY_SHERD),
    MINER(Material.MINER_POTTERY_SHERD),
    MOURNER(Material.MOURNER_POTTERY_SHERD),
    PLENTY(Material.PLENTY_POTTERY_SHERD),
    PRIZE(Material.PRIZE_POTTERY_SHERD),
    SCRAPE(Material.SCRAPE_POTTERY_SHERD),
    SHEAF(Material.SHEAF_POTTERY_SHERD),
    SHELTER(Material.SHELTER_POTTERY_SHERD),
    SKULL(Material.SKULL_POTTERY_SHERD),
    SNORT(Material.SNORT_POTTERY_SHERD);

    private final Material sherdMaterial;

    PotterySherdRegistry(Material sherdMaterial) {
        this.sherdMaterial = sherdMaterial;
    }

    // 核心：随机获取陶罐碎片 ItemStack
    public static ItemStack randomSherd() {
        PotterySherdRegistry[] values = values();
        PotterySherdRegistry random = values[ThreadLocalRandom.current().nextInt(values.length)];
        return new ItemStack(random.sherdMaterial);
    }

    public Material getSherdMaterial() {
        return sherdMaterial;
    }
}
