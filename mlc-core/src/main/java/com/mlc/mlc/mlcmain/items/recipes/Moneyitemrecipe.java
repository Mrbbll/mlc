package com.mlc.mlc.mlcmain.items.recipes;

import com.mlc.mlc.mlcmain.items.itemmannager.Mlcitems;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;

import static com.mlc.mlc.Mlc.instance;
import static com.mlc.mlc.mlcmain.items.itemmannager.Mlcitems.*;

public class Moneyitemrecipe {
    public static void money_nugget_to_ingotrecipe(){
        ShapedRecipe shapedRecipe = new ShapedRecipe(new NamespacedKey(instance,"money_nugget_to_ingotrecipe"), money_ingot);
        shapedRecipe = shapedRecipe.shape("iii", "iii", "iii");
        shapedRecipe = shapedRecipe.setIngredient('i', money_nugget);
        Bukkit.addRecipe(shapedRecipe);
    }
    public static void money_ingot_to_nuggetrecipe(){
        ItemStack money_nuggt_9 = money_nugget.clone();
        money_nuggt_9.setAmount(9);
        ShapelessRecipe shapelessRecipe = new ShapelessRecipe(new NamespacedKey(instance,"money_ingot_to_nuggetrecipe"), money_nuggt_9);
        shapelessRecipe.addIngredient(money_ingot);
        Bukkit.addRecipe(shapelessRecipe);
    }

    public static void money_stack_to_ingotrecipe(){
        ItemStack money_ingot_9 = money_ingot.clone();
        money_ingot_9.setAmount(9);
        ShapelessRecipe shapelessRecipe = new ShapelessRecipe(new NamespacedKey(instance,"money_stack_to_ingotrecipe"), money_ingot_9);
        shapelessRecipe.addIngredient(money_stack);
        Bukkit.addRecipe(shapelessRecipe);
    }


    public static void money_ingot_to_stackrecipe(){
        ShapedRecipe shapedRecipe1 = new ShapedRecipe(new NamespacedKey(instance,"money_ingot_to_stackrecipe"), money_stack);
        shapedRecipe1 = shapedRecipe1.shape("iii", "iii", "iii");
        shapedRecipe1 = shapedRecipe1.setIngredient('i', money_ingot);
        Bukkit.addRecipe(shapedRecipe1);
    }
    public static void money_gemrecipe(){
        BlastingRecipe blastingRecipe = new BlastingRecipe(new NamespacedKey(instance,"money_gemrecipe"),money_gem, new RecipeChoice.ExactChoice(Mlcitems.money_stack),3.0f,102400);
        Bukkit.addRecipe(blastingRecipe);
    }

    public static void money_nugget_to_coinrecipe(){
       StonecuttingRecipe stonecuttingRecipe = new StonecuttingRecipe(new NamespacedKey(instance,"money_nugget_to_coinrecipe"),money_coin, new RecipeChoice.ExactChoice(money_nugget));
       Bukkit.addRecipe(stonecuttingRecipe);
    }

    public static void money_coin_to_nuggetrecipe(){
        FurnaceRecipe furnaceRecipe = new FurnaceRecipe(new NamespacedKey(instance,"money_coin_to_nuggetrecipe"),money_nugget, new RecipeChoice.ExactChoice(money_coin),1.0f,200);
        Bukkit.addRecipe(furnaceRecipe);
    }

    public static void money_stack_to_money_stack_x1recipe(){
        ShapedRecipe shapedRecipe = new ShapedRecipe(new NamespacedKey(instance,"money_stack_to_money_stack_x1recipe"), money_stack_x1);
        shapedRecipe = shapedRecipe.shape("sss","sss","sss");
        shapedRecipe = shapedRecipe.setIngredient('s', money_stack);
        Bukkit.addRecipe(shapedRecipe);
    }
    public static void money_stack_x1_to_money_stack_x2recipe(){
        ShapedRecipe shapedRecipe = new ShapedRecipe(new NamespacedKey(instance,"money_stack_x1_to_money_stack_x2recipe"), money_stack_x2);
        shapedRecipe = shapedRecipe.shape("sss","sss","sss");
        shapedRecipe = shapedRecipe.setIngredient('s', money_stack_x1);
        Bukkit.addRecipe(shapedRecipe);
    }
    public static void money_stack_x2_to_money_stack_x3recipe(){
        ShapedRecipe shapedRecipe = new ShapedRecipe(new NamespacedKey(instance,"money_stack_x2_to_money_stack_x3recipe"), money_stack_x3);
        shapedRecipe = shapedRecipe.shape("sss","sss","sss");
        shapedRecipe = shapedRecipe.setIngredient('s', money_stack_x2);
        Bukkit.addRecipe(shapedRecipe);
    }
    public static void money_stack_x3_to_money_stack_x4recipe(){
        ShapedRecipe shapedRecipe = new ShapedRecipe(new NamespacedKey(instance,"money_stack_x3_to_money_stack_x4recipe"), money_stack_x4);
        shapedRecipe = shapedRecipe.shape("sss","sss","sss");
        shapedRecipe = shapedRecipe.setIngredient('s', money_stack_x3);
        Bukkit.addRecipe(shapedRecipe);
    }
    public static void money_stack_x4_to_money_stack_x5recipe(){
        ShapedRecipe shapedRecipe = new ShapedRecipe(new NamespacedKey(instance,"money_stack_x4_to_money_stack_x5recipe"), money_stack_x5);
        shapedRecipe = shapedRecipe.shape("sss","sss","sss");
        shapedRecipe = shapedRecipe.setIngredient('s', money_stack_x4);
        Bukkit.addRecipe(shapedRecipe);
    }
    public static void money_stack_x1_to_money_stackrecipe(){
        ItemStack money_stack_9 = money_stack.clone();
        money_stack_9.setAmount(9);
        ShapelessRecipe shapelessRecipe = new ShapelessRecipe(new NamespacedKey(instance,"money_stack_x1_to_money_stackrecipe"), money_stack_9);
        shapelessRecipe.addIngredient(money_stack_x1);
        Bukkit.addRecipe(shapelessRecipe);
    }
    public static void money_stack_x2_to_money_stack_x1recipe(){
        ItemStack money_stack_x1_9 = money_stack_x1.clone();
        money_stack_x1_9.setAmount(9);
        ShapelessRecipe shapelessRecipe = new ShapelessRecipe(new NamespacedKey(instance,"money_stack_x2_to_money_stack_x1recipe"), money_stack_x1_9);
        shapelessRecipe.addIngredient(money_stack_x2);
        Bukkit.addRecipe(shapelessRecipe);
    }
    public static void money_stack_x3_to_money_stack_x2recipe(){
        ItemStack money_stack_x2_9 = money_stack_x2.clone();
        money_stack_x2_9.setAmount(9);
        ShapelessRecipe shapelessRecipe = new ShapelessRecipe(new NamespacedKey(instance,"money_stack_x3_to_money_stack_x2recipe"), money_stack_x2_9);
        shapelessRecipe.addIngredient(money_stack_x3);
        Bukkit.addRecipe(shapelessRecipe);
    }
    public static void money_stack_x4_to_money_stack_x3recipe(){
        ItemStack money_stack_x3_9 = money_stack_x3.clone();
        money_stack_x3_9.setAmount(9);
        ShapelessRecipe shapelessRecipe = new ShapelessRecipe(new NamespacedKey(instance,"money_stack_x4_to_money_stack_x3recipe"), money_stack_x3_9);
        shapelessRecipe.addIngredient(money_stack_x4);
        Bukkit.addRecipe(shapelessRecipe);
    }

    public static void money_stack_x5_to_money_stack_x4recipe(){
        ItemStack money_stack_x4_9 = money_stack_x4.clone();
        money_stack_x4_9.setAmount(9);
        ShapelessRecipe shapelessRecipe = new ShapelessRecipe(new NamespacedKey(instance,"money_stack_x5_to_money_stack_x4recipe"), money_stack_x4_9);
        shapelessRecipe.addIngredient(money_stack_x5);
        Bukkit.addRecipe(shapelessRecipe);
    }

}
