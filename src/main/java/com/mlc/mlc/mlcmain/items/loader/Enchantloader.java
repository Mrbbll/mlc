package com.mlc.mlc.mlcmain.items.loader;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;

import java.util.HashMap;
import java.util.Map;

public class Enchantloader {
    public static Map<Enchantment, Integer> loadEnchantments(ConfigurationSection config) {
        Map<Enchantment, Integer> enchantments = new HashMap<>();
        ConfigurationSection configurationSection = config.getConfigurationSection("enchants");

        if (configurationSection != null) {
            for (String enchantname : configurationSection.getKeys(false)) {
    //            String[] parts = entry.split(",");
    //            if (parts.length != 2) continue;
    //
    //            int level;
    //            try {
    //                level = Integer.parseInt(parts[1]);
    //            } catch (NumberFormatException e) {
    //                continue;
    //            }

                int level;
                level = configurationSection.getInt(enchantname,0);
                Enchantment enchantment = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(NamespacedKey.minecraft(enchantname.toLowerCase()));
                enchantments.put(enchantment, level);
            }
        }

        return enchantments;
    }
}
