package com.mlc.mlc.items.itemmannager;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.FoodComponent;

import java.util.ArrayList;
import java.util.List;

import static com.mlc.mlc.Mlc.instance;
import static com.mlc.mlc.Mlc.miniMessage;
import static io.papermc.paper.registry.keys.AttributeKeys.ARMOR;

public class mlcitems {
    public static ItemStack backpack;
    public static ItemStack healfood;
    public static ItemStack elytraitem;
    public static ItemStack money_nugget;
    public static ItemStack money_ingot;
    public static ItemStack money_stack;
    public static ItemStack money_gem;
    public static ItemStack money_coin;
    public static void init(){
        //初始化背包
        backpack = ItemStack.of(Material.ECHO_SHARD);
        ItemMeta itemMeta = backpack.getItemMeta();
        itemMeta.setItemModel(NamespacedKey.fromString("mlc:mlc_backpack"));
        itemMeta.itemName(Component.text("背包"));
        List<Component> lorelist = new ArrayList<>();
        lorelist.add(miniMessage.deserialize("<!i>请不要存储贵重物品，建议存储建筑材料").color(TextColor.color(0x7CFF4D)));
        lorelist.add(miniMessage.deserialize("<!i>可能有丢失风险").color(TextColor.color(0x7CFF4D)));
        itemMeta.lore(lorelist);
        itemMeta.setTooltipStyle(NamespacedKey.fromString("mlc:mlc"));
        backpack.setItemMeta(itemMeta);

        //初始化 heal food
        healfood = ItemStack.of(Material.MUSHROOM_STEW);
        ItemMeta itemMeta_1 = healfood.getItemMeta();
        itemMeta_1.setItemModel(NamespacedKey.fromString("mlc:mlc_healfood"));
        itemMeta_1.itemName(Component.text("回复汤"));
        FoodComponent foodComponent = itemMeta_1.getFood();

        foodComponent.setCanAlwaysEat(true);
        foodComponent.setSaturation(10.0f);
        foodComponent.setNutrition(10);

        itemMeta_1.setUseRemainder(ItemStack.of(Material.BOWL));
        itemMeta_1.setFood(foodComponent);
        itemMeta_1.setTooltipStyle(NamespacedKey.fromString("mlc:mlc"));
        healfood.setItemMeta(itemMeta_1);

        //初始化装甲鞘翅
        elytraitem = ItemStack.of(Material.ELYTRA);
        ItemMeta itemMeta_2 = elytraitem.getItemMeta();
        itemMeta_2.itemName(Component.text("装甲鞘翅"));
        itemMeta_2.setTooltipStyle(NamespacedKey.fromString("mlc:mlc"));
        AttributeModifier attributeModifier = new AttributeModifier(new NamespacedKey(instance,"elytra"),6, AttributeModifier.Operation.ADD_NUMBER);
        Attribute attribute = RegistryAccess.registryAccess().getRegistry(RegistryKey.ATTRIBUTE).get(ARMOR);
        assert attribute != null;
        itemMeta_2.addAttributeModifier(attribute,attributeModifier);
        itemMeta_2.setItemModel(NamespacedKey.fromString("mlc:elytra"));
        elytraitem.setItemMeta(itemMeta_2);

        //初始化货币
        money_nugget = ItemStack.of(Material.ECHO_SHARD);
        ItemMeta itemMeta_3 = money_nugget.getItemMeta();
        itemMeta_3.itemName(Component.text("水晶碎片"));
        itemMeta_3.setTooltipStyle(NamespacedKey.fromString("mlc:mlc"));
        itemMeta_3.setItemModel(NamespacedKey.fromString("mlc:crystal_nugget"));
        money_nugget.setItemMeta(itemMeta_3);

        money_ingot = ItemStack.of(Material.ECHO_SHARD);
        ItemMeta itemMeta_4 = money_ingot.getItemMeta();
        itemMeta_4.itemName(Component.text("水晶碎块"));
        itemMeta_4.setTooltipStyle(NamespacedKey.fromString("mlc:mlc"));
        itemMeta_4.setItemModel(NamespacedKey.fromString("mlc:crystal_ingot"));
        money_ingot.setItemMeta(itemMeta_4);

        money_stack = ItemStack.of(Material.ECHO_SHARD);
        ItemMeta itemMeta_5 = money_ingot.getItemMeta();
        itemMeta_5.itemName(Component.text("水晶块"));
        itemMeta_5.setTooltipStyle(NamespacedKey.fromString("mlc:mlc"));
        itemMeta_5.setItemModel(NamespacedKey.fromString("mlc:crystal_stack"));
        money_stack.setItemMeta(itemMeta_5);

        money_gem = ItemStack.of(Material.ECHO_SHARD);
        ItemMeta itemMeta_6 = money_gem.getItemMeta();
        itemMeta_6.itemName(Component.text("水晶石"));
        itemMeta_6.setTooltipStyle(NamespacedKey.fromString("mlc:mlc"));
        itemMeta_6.setItemModel(NamespacedKey.fromString("mlc:crystal_gem"));
        money_gem.setItemMeta(itemMeta_6);

        money_coin = ItemStack.of(Material.ECHO_SHARD);
        ItemMeta itemMeta_7 = money_coin.getItemMeta();
        itemMeta_7.itemName(Component.text("水晶币"));
        itemMeta_7.setTooltipStyle(NamespacedKey.fromString("mlc:mlc"));
        itemMeta_7.setItemModel(NamespacedKey.fromString("mlc:crystal_coin"));
        money_coin.setItemMeta(itemMeta_7);
    }
}
