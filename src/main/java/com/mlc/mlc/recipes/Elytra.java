package com.mlc.mlc.recipes;

import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.item.DamageResistant;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.damage.DamageType;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import static com.mlc.mlc.Mlc.instance;
import static io.papermc.paper.registry.keys.AttributeKeys.ARMOR;

public class Elytra {
    public static void elytrarecipe(){
        ItemStack elytraitem = ItemStack.of(Material.ELYTRA);
        ItemMeta itemMeta = elytraitem.getItemMeta();
//        itemMeta.setItemModel(NamespacedKey.fromString("mlc:mlc_backpack"));
        itemMeta.itemName(Component.text("装甲鞘翅"));
        itemMeta.setTooltipStyle(NamespacedKey.fromString("mlc:mlc"));
        AttributeModifier attributeModifier = new AttributeModifier(new NamespacedKey(instance,"elytra"),6, AttributeModifier.Operation.ADD_NUMBER);
        Attribute attribute = RegistryAccess.registryAccess().getRegistry(RegistryKey.ATTRIBUTE).get(ARMOR);
        assert attribute != null;
        itemMeta.addAttributeModifier(attribute,attributeModifier);
        itemMeta.setItemModel(NamespacedKey.fromString("mlc:elytra"));
        elytraitem.setItemMeta(itemMeta);

        SmithingTransformRecipe smithingTransformRecipe = new SmithingTransformRecipe(
                new NamespacedKey(instance, "elytar"),
                new ItemStack(elytraitem),
                new RecipeChoice.MaterialChoice(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                new RecipeChoice.MaterialChoice(Material.ELYTRA),
                new RecipeChoice.MaterialChoice(Material.NETHERITE_INGOT)
        );
        Bukkit.addRecipe(smithingTransformRecipe);
    }
}
