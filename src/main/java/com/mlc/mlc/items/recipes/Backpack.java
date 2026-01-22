package com.mlc.mlc.items.recipes;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static com.mlc.mlc.Mlc.instance;
import static com.mlc.mlc.Mlc.miniMessage;
import static com.mlc.mlc.items.itemmannager.mlcitems.backpack;

public class Backpack {
    public static void backpackrecipe(){


        ShapedRecipe shapedRecipe = new ShapedRecipe(new NamespacedKey(instance,"backpackrecipe"),backpack);
        shapedRecipe = shapedRecipe.shape("bcb", "cxc", "ccc");
        shapedRecipe = shapedRecipe.setIngredient('c', Material.LEATHER).setIngredient('x', Material.SHULKER_BOX).setIngredient('b', Material.LEAD);
        Bukkit.addRecipe(shapedRecipe);
    }
}
