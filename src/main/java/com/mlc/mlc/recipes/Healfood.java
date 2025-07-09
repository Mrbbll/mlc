package com.mlc.mlc.recipes;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import static com.mlc.mlc.Mlc.instance;

public class Healfood {
    public void healfoodrecipe(){
        ItemStack healfooditem = ItemStack.of(Material.ECHO_SHARD);
        ItemMeta itemMeta = healfooditem.getItemMeta();
        itemMeta.setItemModel(NamespacedKey.fromString("mlc:mlc_healfood"));
        itemMeta.itemName(Component.text("回复汤"));
        itemMeta.setTooltipStyle(NamespacedKey.fromString("mlc:mlc"));
        healfooditem.setItemMeta(itemMeta);

        ShapelessRecipe shapelessRecipe = new ShapelessRecipe(new NamespacedKey(instance,"healfoodrecipe"),healfooditem);
        shapelessRecipe.addIngredient(1,Material.BOWL);
        shapelessRecipe.addIngredient(1,Material.CARROT);
        shapelessRecipe.addIngredient(1,Material.BEEF);
        shapelessRecipe.addIngredient(1,Material.CHICKEN);
        shapelessRecipe.addIngredient(1,Material.MUTTON);
        shapelessRecipe.addIngredient(1,Material.SWEET_BERRIES);
        Bukkit.addRecipe(shapelessRecipe);
    }
}
