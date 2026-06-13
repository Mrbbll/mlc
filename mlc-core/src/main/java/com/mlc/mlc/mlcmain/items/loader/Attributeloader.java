package com.mlc.mlc.mlcmain.items.loader;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;


public class Attributeloader {

    public static void applyAttributes(ItemMeta meta, ConfigurationSection section) {
        ConfigurationSection configurationSection = section.getConfigurationSection("attribute_modifiers");
//        attribute_modifiers:
//          - ADD_NUMBER:
//              - ANY:
//                  ATTACK_DAMAGE: 19
        if (configurationSection != null) {
            //操作类型
            for (String operationName : configurationSection.getKeys(false)) {
                ConfigurationSection operationSection = configurationSection.getConfigurationSection(operationName);
                if (operationSection != null) {
                    //生效栏位
                    AttributeModifier.Operation operation = AttributeModifier.Operation.valueOf(operationName.toUpperCase());

                    for (String slotName : operationSection.getKeys(false)) {
                        ConfigurationSection slotSection = operationSection.getConfigurationSection(slotName);
                        if (slotSection != null) {
                            //属性
                            for (String attributeName : slotSection.getKeys(false)) {
                                long timeMillis = System.currentTimeMillis();
                                double value = slotSection.getDouble(attributeName, 0);

                                AttributeModifier attributeModifier = new AttributeModifier(
                                        Objects.requireNonNull(NamespacedKey.fromString("mlc:" + timeMillis)),
                                        value,
                                        AttributeModifier.Operation.valueOf(operationName.toUpperCase()),
                                Objects.requireNonNull(EquipmentSlotGroup.getByName(slotName.toUpperCase())));

                                Attribute attribute = RegistryAccess.registryAccess()
                                                .getRegistry(RegistryKey.ATTRIBUTE)
                                                        .get(NamespacedKey.minecraft(attributeName
                                                                .toLowerCase()));
                                if (attribute != null) {
                                    meta.addAttributeModifier(attribute, attributeModifier);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}