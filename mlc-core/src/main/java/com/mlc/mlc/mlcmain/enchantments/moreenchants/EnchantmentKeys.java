package com.mlc.mlc.mlcmain.enchantments.moreenchants;

import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import net.kyori.adventure.key.Key;
import org.bukkit.enchantments.Enchantment;

public class EnchantmentKeys {
    public static final TypedKey<Enchantment> VEINMINE =
            TypedKey.create(
                    RegistryKey.ENCHANTMENT,
                    Key.key("mlc:veinmine")
            );
    public static final TypedKey<Enchantment> BRITTLE = TypedKey.create(RegistryKey.ENCHANTMENT, Key.key("mlc:brittle"));
    public static final TypedKey<Enchantment> FRAGILE = TypedKey.create(RegistryKey.ENCHANTMENT, Key.key("mlc:fragile"));

    private EnchantmentKeys() {
    }
}
