package com.mlc.mlc.mlcmain.items.recipes;

import org.bukkit.*;
import org.bukkit.inventory.*;

import static com.mlc.mlc.Mlc.instance;
import static com.mlc.mlc.mlcmain.items.itemmannager.Mlcitems.elytraitem;

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
