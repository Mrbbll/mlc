package com.mlc.mlc.items.recipes;

import com.mlc.mlc.items.itemmannager.mlcitems;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.BlastingRecipe;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

import static com.mlc.mlc.Mlc.instance;
import static com.mlc.mlc.items.itemmannager.mlcitems.*;

public class Moneyitemrecipe {
    public static void money_ingotrecipe(){
        ShapedRecipe shapedRecipe = new ShapedRecipe(new NamespacedKey(instance,"money_ingotrecipe"), money_ingot);
        shapedRecipe = shapedRecipe.shape("iii", "iii", "iii");
        shapedRecipe = shapedRecipe.setIngredient('i', money_nugget);
        Bukkit.addRecipe(shapedRecipe);
    }
    public static void money_stackrecipe(){
        ShapedRecipe shapedRecipe1 = new ShapedRecipe(new NamespacedKey(instance,"money_stackrecipe"), money_stack);
        shapedRecipe1 = shapedRecipe1.shape("iii", "iii", "iii");
        shapedRecipe1 = shapedRecipe1.setIngredient('i', money_ingot);
        Bukkit.addRecipe(shapedRecipe1);
    }
    public static void money_gemrecipe(){
        BlastingRecipe blastingRecipe = new BlastingRecipe(new NamespacedKey(instance,"money_gemrecipe"),money_gem, new RecipeChoice.ExactChoice(mlcitems.money_stack),3.0f,102400);
        Bukkit.addRecipe(blastingRecipe);
    }
}
