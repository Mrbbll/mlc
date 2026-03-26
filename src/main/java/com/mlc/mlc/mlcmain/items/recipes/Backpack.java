package com.mlc.mlc.mlcmain.items.recipes;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ShapedRecipe;

import static com.mlc.mlc.Mlc.instance;
import static com.mlc.mlc.mlcmain.items.itemmannager.Mlcitems.backpack;

public class Backpack {
    public static void backpackrecipe(){


        ShapedRecipe shapedRecipe = new ShapedRecipe(new NamespacedKey(instance,"backpackrecipe"),backpack);
        shapedRecipe = shapedRecipe.shape("bcb", "cxc", "ccc");
        shapedRecipe = shapedRecipe.setIngredient('c', Material.LEATHER).setIngredient('x', Material.SHULKER_BOX).setIngredient('b', Material.LEAD);
        Bukkit.addRecipe(shapedRecipe);
    }
}
