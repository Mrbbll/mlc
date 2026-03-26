package com.mlc.mlc.mlcmain.items.loader;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class Itemflagloader {
    public static void applyItemFlags(ItemMeta meta, ConfigurationSection section) {
        List<String> flags = section.getStringList("item_flags");
        for (String flagName : flags) {
            try {
                ItemFlag flag = ItemFlag.valueOf(flagName.toUpperCase());
                meta.addItemFlags(flag);
            } catch (IllegalArgumentException e) {
                System.out.println("无效的 ItemFlag: " + flagName);
            }
        }
    }
}
