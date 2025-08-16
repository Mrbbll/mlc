package com.mlc.mlc.mlcitem.loader;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class Durabilityloader {
    public static void loaddurability(ItemMeta meta, ConfigurationSection section){
        Damageable damageable = (Damageable)meta;

        if(section.contains("durability.unbreakable")){
            boolean isunbreakable;
            isunbreakable = section.getBoolean("durability.unbreakable",false);
            damageable.setUnbreakable(isunbreakable);
        }
        if (section.contains("durability.max_custom_durability"))
        {
            damageable.setMaxDamage(section.getInt("durability.max_custom_durability",0));
        }
        if(section.contains("durability.usages")){
            damageable.setDamage(section.getInt("durability.usages",0));
        }
    }
}
