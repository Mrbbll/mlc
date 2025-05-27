package com.mlc.mlc.guis;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.configuration.file.FileConfiguration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Enchantloader {
    public static Map<Enchantment, Integer> loadEnchantments(ConfigurationSection config) {
        Map<Enchantment, Integer> enchantments = new HashMap<>();
        List<String> enchantStrings = config.getStringList("enchants");

        for (String entry : enchantStrings) {
            String[] parts = entry.split(",");
            if (parts.length != 2) continue;

            int level;
            try {
                level = Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
                continue;
            }

            Enchantment enchantment = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(NamespacedKey.minecraft(parts[0].toLowerCase()));
            enchantments.put(enchantment, level);
        }

        return enchantments;
    }
}
