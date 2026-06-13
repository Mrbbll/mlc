package com.mlc.mlc.mlcmain.items.recipes;

import com.mlc.mlc.mlcmain.items.itemmannager.Mlcitems;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

import static com.mlc.mlc.Mlc.instance;

public class Crate {
    public static void crateRecipe(){
        RecipeChoice recipeChoice = new RecipeChoice.MaterialChoice(
                Material.SPRUCE_PLANKS,
                Material.OAK_PLANKS,
                Material.JUNGLE_PLANKS,
                Material.BIRCH_PLANKS,
                Material.DARK_OAK_PLANKS,
                Material.ACACIA_PLANKS,
                Material.CHERRY_PLANKS,
                Material.PALE_OAK_PLANKS,
                Material.MANGROVE_PLANKS,
                Material.WARPED_PLANKS,
                Material.CRIMSON_PLANKS
        );
        ShapedRecipe shapedRecipe = new ShapedRecipe(new NamespacedKey(instance, "craterecipe"), Mlcitems.crate);
        shapedRecipe = shapedRecipe.shape("XOX", "OAO", "XOX");
        shapedRecipe.setIngredient('X', Material.IRON_INGOT);
        shapedRecipe.setIngredient('O', recipeChoice);
        shapedRecipe.setIngredient('A', Mlcitems.money_gem);
        Bukkit.addRecipe(shapedRecipe);
    }
}
