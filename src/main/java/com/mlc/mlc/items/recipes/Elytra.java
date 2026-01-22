package com.mlc.mlc.items.recipes;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import static com.mlc.mlc.Mlc.instance;
import static com.mlc.mlc.items.itemmannager.mlcitems.elytraitem;
import static io.papermc.paper.registry.keys.AttributeKeys.ARMOR;

public class Elytra {
    public static void elytrarecipe(){
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
