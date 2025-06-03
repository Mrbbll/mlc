package com.mlc.mlc.itemgui;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;

import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;


public class attributeloader {

    public static void applyAttributes(ItemMeta meta, ConfigurationSection section) {
        ConfigurationSection configurationSection = section.getConfigurationSection("attribute_modifiers");
        if(configurationSection!=null){
            for (String attributeName : configurationSection.getKeys(false)) {
                long timeMillis = System.currentTimeMillis();
                double value = configurationSection.getDouble(attributeName, 0);
                AttributeModifier attributeModifier = new AttributeModifier(Objects.requireNonNull(NamespacedKey.fromString("mlc:"+timeMillis)),value,AttributeModifier.Operation.ADD_NUMBER);
                Attribute attribute = RegistryAccess.registryAccess().getRegistry(RegistryKey.ATTRIBUTE).get(NamespacedKey.minecraft(attributeName.toLowerCase()));
                if (attribute != null) {
                    meta.addAttributeModifier(attribute,attributeModifier);
                }
            }
        }

    }
}