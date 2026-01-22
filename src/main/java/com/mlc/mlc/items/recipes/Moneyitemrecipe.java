package com.mlc.mlc.items.recipes;

import com.mlc.mlc.items.itemmannager.mlcitems;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;

import static com.mlc.mlc.Mlc.instance;
import static com.mlc.mlc.items.itemmannager.mlcitems.*;

public class Moneyitemrecipe {
    public static void money_nugget_to_ingotrecipe(){
        ShapedRecipe shapedRecipe = new ShapedRecipe(new NamespacedKey(instance,"money_ingotrecipe"), money_ingot);
        shapedRecipe = shapedRecipe.shape("iii", "iii", "iii");
        shapedRecipe = shapedRecipe.setIngredient('i', money_nugget);
        Bukkit.addRecipe(shapedRecipe);
    }
    public static void money_ingot_to_nuggetrecipe(){
        ItemStack money_nuggt_9 = money_nugget;
        money_nuggt_9.setAmount(9);
        ShapelessRecipe shapelessRecipe = new ShapelessRecipe(new NamespacedKey(instance,"money_nuggt_9recipe"), money_nuggt_9);
        shapelessRecipe.addIngredient(money_ingot);
        Bukkit.addRecipe(shapelessRecipe);
    }

    public static void money_stack_to_ingotrecipe(){
        ItemStack money_ingot_9 = money_ingot;
        money_ingot_9.setAmount(9);
        ShapelessRecipe shapelessRecipe = new ShapelessRecipe(new NamespacedKey(instance,"money_ingot_9recipe"), money_ingot_9);
        shapelessRecipe.addIngredient(money_stack);
        Bukkit.addRecipe(shapelessRecipe);
    }


    public static void money_ingot_to_stackrecipe(){
        ShapedRecipe shapedRecipe1 = new ShapedRecipe(new NamespacedKey(instance,"money_stackrecipe"), money_stack);
        shapedRecipe1 = shapedRecipe1.shape("iii", "iii", "iii");
        shapedRecipe1 = shapedRecipe1.setIngredient('i', money_ingot);
        Bukkit.addRecipe(shapedRecipe1);
    }
    public static void money_gemrecipe(){
        BlastingRecipe blastingRecipe = new BlastingRecipe(new NamespacedKey(instance,"money_gemrecipe"),money_gem, new RecipeChoice.ExactChoice(mlcitems.money_stack),3.0f,102400);
        Bukkit.addRecipe(blastingRecipe);
    }

    public static void money_nugget_to_coinrecipe(){
       StonecuttingRecipe stonecuttingRecipe = new StonecuttingRecipe(new NamespacedKey(instance,"money_coinrecipe"),money_coin, new RecipeChoice.ExactChoice(money_nugget));
       Bukkit.addRecipe(stonecuttingRecipe);
    }

    public static void money_coin_to_nuggetrecipe(){
        FurnaceRecipe furnaceRecipe = new FurnaceRecipe(new NamespacedKey(instance,"money_nuggetrecipe"),money_nugget, new RecipeChoice.ExactChoice(money_coin),1.0f,200);
        Bukkit.addRecipe(furnaceRecipe);
    }
}
