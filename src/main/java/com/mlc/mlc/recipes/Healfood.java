package com.mlc.mlc.recipes;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.FoodComponent;
import io.papermc.paper.datacomponent.item.Consumable;

import static com.mlc.mlc.Mlc.instance;

public class Healfood {
    public static void healfoodrecipe(){
        ItemStack healfooditem = ItemStack.of(Material.MUSHROOM_STEW);
        ItemMeta itemMeta = healfooditem.getItemMeta();
        itemMeta.setItemModel(NamespacedKey.fromString("mlc:mlc_healfood"));
        itemMeta.itemName(Component.text("回复汤"));
        FoodComponent foodComponent = itemMeta.getFood();

        foodComponent.setCanAlwaysEat(true);
        foodComponent.setSaturation(10.0f);
        foodComponent.setNutrition(10);

        itemMeta.setUseRemainder(ItemStack.of(Material.BOWL));
        itemMeta.setFood(foodComponent);
        itemMeta.setTooltipStyle(NamespacedKey.fromString("mlc:mlc"));
        healfooditem.setItemMeta(itemMeta);

        ShapelessRecipe shapelessRecipe = new ShapelessRecipe(new NamespacedKey(instance,"healfoodrecipe"),healfooditem);
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
