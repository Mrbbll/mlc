package com.mlc.mlcbot;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/** Counts and moves real kit items; never creates a virtual totem reserve. */
public final class TotemInventory {
    private TotemInventory() {}

    public static int count(PlayerInventory inventory) {
        int total = amount(inventory.getItemInOffHand());
        for (ItemStack item : inventory.getStorageContents()) total += amount(item);
        return total;
    }

    public static void refillOffhand(PlayerInventory inventory) {
        ItemStack offhand = inventory.getItemInOffHand();
        if (offhand != null && offhand.getType() != Material.AIR && offhand.getAmount() > 0) return;
        ItemStack[] storage = inventory.getStorageContents();
        for (int slot = 0; slot < storage.length; slot++) {
            ItemStack item = storage[slot];
            if (amount(item) == 0) continue;
            ItemStack moved = item.clone();
            moved.setAmount(1);
            if (item.getAmount() == 1) inventory.setItem(slot, null);
            else {
                ItemStack remaining = item.clone();
                remaining.setAmount(item.getAmount() - 1);
                inventory.setItem(slot, remaining);
            }
            inventory.setItemInOffHand(moved);
            return;
        }
    }

    private static int amount(ItemStack item) {
        return item != null && item.getType() == Material.TOTEM_OF_UNDYING ? Math.max(0, item.getAmount()) : 0;
    }
}
