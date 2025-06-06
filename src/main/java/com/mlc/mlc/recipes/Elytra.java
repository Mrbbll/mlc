package com.mlc.mlc.recipes;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import static com.mlc.mlc.Mlc.instance;

public class Elytra {
    public void elytrarecipe(){
        ItemStack elytraitem = ItemStack.of(Material.ELYTRA);
        ItemMeta itemMeta = elytraitem.getItemMeta();
//        itemMeta.setItemModel(NamespacedKey.fromString("mlc:mlc_backpack"));
        itemMeta.itemName(Component.text("装甲鞘翅"));
        itemMeta.setTooltipStyle(NamespacedKey.fromString("mlc:mlc"));

        elytraitem.setItemMeta(itemMeta);

        SmithingTransformRecipe smithingTransformRecipe = new SmithingTransformRecipe(
                new NamespacedKey(instance, "elytar"),
                new ItemStack(elytraitem),
                new RecipeChoice.MaterialChoice(Material.ICE),
                new RecipeChoice.MaterialChoice(Material.ICE),
                new RecipeChoice.MaterialChoice(Material.ICE)
        );
        Bukkit.addRecipe(smithingTransformRecipe);
    }
}
