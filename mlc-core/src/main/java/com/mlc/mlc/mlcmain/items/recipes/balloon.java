package com.mlc.mlc.mlcmain.items.recipes;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ShapedRecipe;

import static com.mlc.mlc.Mlc.instance;
import static com.mlc.mlc.mlcmain.items.itemmannager.Mlcitems.balloon;

public class balloon {
    public static void backpackrecipe(){
        ShapedRecipe shapedRecipe = new ShapedRecipe(new NamespacedKey(instance,"balloonrecipe"),balloon);
        shapedRecipe = shapedRecipe.shape("xxx", "abc", "ded");
        shapedRecipe = shapedRecipe.setIngredient('x', Material.LEATHER)
                .setIngredient('a', Material.LIME_DYE).setIngredient('b', Material.LIGHT_BLUE_DYE).setIngredient('c',Material.YELLOW_DYE)
                .setIngredient('d',Material.STRING).setIngredient('e',Material.BLAST_FURNACE);
        Bukkit.addRecipe(shapedRecipe);
    }
}
