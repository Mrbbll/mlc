package com.mlc.mlc.items.recipes;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.FoodComponent;

import static com.mlc.mlc.Mlc.instance;
import static com.mlc.mlc.items.itemmannager.mlcitems.healfood;

public class Healfood {
    public static void healfoodrecipe(){

        ShapelessRecipe shapelessRecipe = new ShapelessRecipe(new NamespacedKey(instance,"healfoodrecipe"),healfood);
        shapelessRecipe.addIngredient(1,Material.BOWL);
        shapelessRecipe.addIngredient(1,Material.CARROT);
        shapelessRecipe.addIngredient(1,Material.BEEF);
        shapelessRecipe.addIngredient(1,Material.CHICKEN);
        shapelessRecipe.addIngredient(1,Material.MUTTON);
        shapelessRecipe.addIngredient(1,Material.PORKCHOP);
        shapelessRecipe.addIngredient(1,Material.SWEET_BERRIES);
        Bukkit.addRecipe(shapelessRecipe);
    }
}
