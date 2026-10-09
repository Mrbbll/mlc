package com.mlc.mlcbot;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/** Fixed defaults adapted from PracticeBot x/j.java, x/bs.java and default_inv.yml. */
public final class DefaultKits {
    public static final int TOTEMS = 99;

    private DefaultKits() { }

    public static void equip(Player bot, BotType type) {
        PlayerInventory inv = bot.getInventory();
        inv.clear();
        inv.setHelmet(enchanted(Material.NETHERITE_HELMET, Enchantment.PROTECTION, 4));
        inv.setChestplate(enchanted(Material.NETHERITE_CHESTPLATE, Enchantment.PROTECTION, 4));
        inv.setLeggings(enchanted(Material.NETHERITE_LEGGINGS,
                type == BotType.CPVP ? Enchantment.BLAST_PROTECTION : Enchantment.PROTECTION, 4));
        inv.setBoots(enchanted(Material.NETHERITE_BOOTS, Enchantment.PROTECTION, 4));
        inv.setItem(0, enchanted(Material.NETHERITE_SWORD, Enchantment.SHARPNESS, 5));
        inv.setItem(1, enchanted(Material.NETHERITE_AXE, Enchantment.SHARPNESS, 5));
        inv.setItemInOffHand(new ItemStack(Material.TOTEM_OF_UNDYING));
        // Totems are stored as normal non-stackable items and replenished from a finite reserve.
        inv.setItem(35, new ItemStack(Material.TOTEM_OF_UNDYING));
        if (type == BotType.CPVP) {
            inv.getItem(0).addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
            inv.setItem(2, new ItemStack(Material.OBSIDIAN, 64));
            inv.setItem(3, new ItemStack(Material.END_CRYSTAL, 64));
            inv.setItem(4, new ItemStack(Material.END_CRYSTAL, 64));
            inv.setItem(5, new ItemStack(Material.GOLDEN_APPLE, 64));
            inv.setItem(6, enchanted(Material.NETHERITE_PICKAXE, Enchantment.EFFICIENCY, 5));
            inv.setItem(7, new ItemStack(Material.ENDER_PEARL, 16));
            inv.setItem(8, new ItemStack(Material.OBSIDIAN, 64));
            for (int slot = 9; slot < 35; slot++) {
                Material material = switch (slot % 4) {
                    case 0 -> Material.END_CRYSTAL;
                    case 1 -> Material.OBSIDIAN;
                    case 2 -> Material.GOLDEN_APPLE;
                    default -> Material.ENDER_PEARL;
                };
                inv.setItem(slot, new ItemStack(material, material == Material.ENDER_PEARL ? 16 : 64));
            }
        }
        inv.setHeldItemSlot(0);
    }

    private static ItemStack enchanted(Material material, Enchantment enchantment, int level) {
        ItemStack item = new ItemStack(material);
        item.addUnsafeEnchantment(enchantment, level);
        item.editMeta(meta -> meta.setUnbreakable(true));
        return item;
    }
}
