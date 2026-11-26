package com.mlc.mlc.totalassault.data;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.Set;

public class Banneditem {
    private Banneditem() {}

    public static String identity(ItemStack item) {
        if (item == null || item.getType().isAir()) return null;
        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.hasItemModel() && meta.getItemModel() != null) {
            return "model:" + meta.getItemModel();
        }
        return "material:" + item.getType().name();
    }

    public static boolean isitembanned(ItemStack item, Set<String> models, Set<Material> materials) {
        String key = identity(item);
        if (key == null) return false;
        return key.startsWith("model:") ? models.contains(key.substring(6)) : materials.contains(item.getType());
    }
}
