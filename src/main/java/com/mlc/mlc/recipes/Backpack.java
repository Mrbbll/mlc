package com.mlc.mlc.recipes;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import static com.mlc.mlc.Mlc.instance;

public class Backpack {
    public void backpackrecipe(){
        ItemStack backpackitem = ItemStack.of(Material.ECHO_SHARD);
        ItemMeta itemMeta = backpackitem.getItemMeta();
        itemMeta.setItemModel(NamespacedKey.fromString("mlc:mlc_backpack"));
        itemMeta.itemName(Component.text("背包"));
        itemMeta.setTooltipStyle(NamespacedKey.fromString("mlc:mlc"));
        backpackitem.setItemMeta(itemMeta);

        ShapedRecipe shapedRecipe = new ShapedRecipe(new NamespacedKey(instance,"backpackrecipe"),backpackitem);
        shapedRecipe = shapedRecipe.shape("bcb", "cxc", "ccc");
        shapedRecipe = shapedRecipe.setIngredient('c', Material.LEATHER).setIngredient('x', Material.SHULKER_BOX).setIngredient('b', Material.LEAD);
        Bukkit.addRecipe(shapedRecipe);
    }
}
