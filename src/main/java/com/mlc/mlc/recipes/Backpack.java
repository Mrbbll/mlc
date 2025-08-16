package com.mlc.mlc.recipes;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
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

public class Backpack {
    public void backpackrecipe(){
        ItemStack backpackitem = ItemStack.of(Material.ECHO_SHARD);
        ItemMeta itemMeta = backpackitem.getItemMeta();
        itemMeta.setItemModel(NamespacedKey.fromString("mlc:mlc_backpack"));
        itemMeta.itemName(Component.text("背包"));
        List<Component> lorelist = new ArrayList<>();
        lorelist.add(miniMessage.deserialize("<!i>请不要存储贵重物品，建议存储建筑材料").color(TextColor.color(0x7CFF4D)));
        lorelist.add(miniMessage.deserialize("<!i>可能有丢失风险").color(TextColor.color(0x7CFF4D)));
        itemMeta.lore(lorelist);
        itemMeta.setTooltipStyle(NamespacedKey.fromString("mlc:mlc"));
        backpackitem.setItemMeta(itemMeta);

        ShapedRecipe shapedRecipe = new ShapedRecipe(new NamespacedKey(instance,"backpackrecipe"),backpackitem);
        shapedRecipe = shapedRecipe.shape("bcb", "cxc", "ccc");
        shapedRecipe = shapedRecipe.setIngredient('c', Material.LEATHER).setIngredient('x', Material.SHULKER_BOX).setIngredient('b', Material.LEAD);
        Bukkit.addRecipe(shapedRecipe);
    }
}
