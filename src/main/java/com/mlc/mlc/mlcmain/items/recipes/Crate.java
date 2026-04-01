package com.mlc.mlc.mlcmain.items.recipes;

import com.mlc.mlc.mlcmain.items.itemmannager.Mlcitems;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ShapedRecipe;

import static com.mlc.mlc.Mlc.instance;

public class Crate {
    public static void crateRecipe(){
        ShapedRecipe shapedRecipe = new ShapedRecipe(new NamespacedKey(instance, "craterecipe"), Mlcitems.crate);
        shapedRecipe = shapedRecipe.shape("XOX", "OAO", "XOX");
        shapedRecipe.setIngredient('X', Material.IRON_INGOT);
        shapedRecipe.setIngredient('O', Mlcitems.money_stack_x3);
        shapedRecipe.setIngredient('A', Mlcitems.w);
    }
}
