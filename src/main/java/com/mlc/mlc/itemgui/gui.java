package com.mlc.mlc.itemgui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.EquippableComponent;

import java.io.File;
import java.util.*;

import static com.mlc.mlc.Mlc.instance;
import static com.mlc.mlc.itemgui.itemflagloader.applyItemFlags;


public class gui {
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();
    public Inventory inv;
    public Player owner;
    public int num;
    public void loaditem(String itemid,ConfigurationSection configurationSection){
        ConfigurationSection configurationSection_item = configurationSection.getConfigurationSection(itemid);

        if (configurationSection_item != null) {
            if(!configurationSection_item.contains("type")){
                return;
            }

            ItemStack itemStack = ItemStack.of(Objects.requireNonNull(Material.getMaterial(configurationSection_item.getString("type", "STONE"))));
            ItemMeta meta =itemStack.getItemMeta();

            if(configurationSection_item.contains("name")){
                meta.itemName(miniMessage.deserialize(Objects.requireNonNull(configurationSection_item.getString("name"))));
            }

            if(configurationSection_item.contains("resource")){
                meta.setItemModel(NamespacedKey.fromString(Objects.requireNonNull(configurationSection_item.getString("resource"))));
            }

            if(configurationSection_item.contains("tooltips")){
                meta.setTooltipStyle(NamespacedKey.fromString(Objects.requireNonNull(configurationSection_item.getString("tooltips"))));
            }

            if(configurationSection_item.contains("lore")){
                List<Component> itemLore = Loreloader.getLoreComponents(configurationSection_item);

                meta.lore(itemLore);
            }

            if(configurationSection_item.contains("enchants")){
                Map<Enchantment, Integer> enchants = Enchantloader.loadEnchantments(configurationSection_item);
                for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
                    meta.addEnchant(entry.getKey(), entry.getValue(), true);
                }
            }

            if(configurationSection_item.contains("item_flags")){
                applyItemFlags(meta, configurationSection_item);
            }

            if(configurationSection_item.contains("durability")){
                durabilityloader.loaddurability(meta,configurationSection_item);
            }

            if(configurationSection_item.contains("attribute_modifiers")){
                attributeloader.applyAttributes(meta,configurationSection_item);
            }

            if(configurationSection_item.contains("head")){
                if(configurationSection_item.getBoolean("head")){
                    EquippableComponent equippableComponent = meta.getEquippable();
                    equippableComponent.setSlot(EquipmentSlot.HEAD);
                    meta.setEquippable(equippableComponent);
                }
            }

            if(configurationSection_item.contains("tagged")){
                PDCloader.loadpdc(configurationSection_item,meta);
            }

            itemStack.setItemMeta(meta);
            if(num<44){
                inv.setItem(num,itemStack);
            }
        }
    }


    public gui(Player player){
        inv = Bukkit.createInventory(player, 9*5, Component.text("mlc"));
        owner = player;
        File file = new File(instance.getDataFolder(),"items.yml");
        FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection configurationSection =fileConfiguration.getConfigurationSection("items");
        if (configurationSection != null) {
            Set<String> itemids = configurationSection.getKeys(false);
            num = 0;
            for(String itemid:itemids){

                loaditem(itemid,configurationSection);
                num++;
            }
        }


//        AttributeModifier am = new AttributeModifier(new NamespacedKey("mlc","plus"), 0.02, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.HEAD);
//        AttributeModifier am1 = new AttributeModifier(new NamespacedKey("mlc","plus1"), 3, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.HEAD);
//        AttributeModifier am2 = new AttributeModifier(new NamespacedKey("mlc","plus2"), 4, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.HEAD);
//        AttributeModifier am3 = new AttributeModifier(new NamespacedKey("mlc","plus3"), 2, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.HEAD);
//        //0item
////        ItemStack quit = new ItemStack(Material.BARRIER);
////        ItemMeta meta = quit.getItemMeta();
////        List<Component> lore1 = new ArrayList<>();
////        lore1.add(Component.text("退出"));
////        meta.itemName(Component.text("exit"));
////        meta.lore(lore1);
////        quit.setItemMeta(meta);
//
//        //1item
//        ItemStack fes1_hat_1_item = new ItemStack(Material.ECHO_SHARD);
//        ItemMeta meta1 = fes1_hat_1_item.getItemMeta();
//        NamespacedKey fes1_hat_1 = new NamespacedKey("mlc","fes1_hat_1");
//        meta1.setItemModel(fes1_hat_1);
//        meta1.itemName( Component.text("活动帽子1"));
//        EquippableComponent equippableComponent1 = meta1.getEquippable();
//        equippableComponent1.setSlot(EquipmentSlot.HEAD);
//        meta1.setEquippable(equippableComponent1);
//        meta1.setRarity(ItemRarity.UNCOMMON);
//        meta1.setTooltipStyle(NamespacedKey.fromString("mlc:mlc"));
//        Damageable damageable1 = (Damageable)meta1;
//        damageable1.setMaxDamage(407);
//        damageable1.setDamage(0);
//        meta1.addAttributeModifier(Attribute.MOVEMENT_SPEED,am);
//        meta1.addAttributeModifier(Attribute.ARMOR,am1);
//        meta1.addAttributeModifier(Attribute.ARMOR_TOUGHNESS,am2);
//        meta1.addAttributeModifier(Attribute.ATTACK_KNOCKBACK,am3);
//
//        fes1_hat_1_item.setItemMeta((ItemMeta) damageable1);
//        fes1_hat_1_item.setItemMeta(meta1);
//
//        //2item
//        ItemStack fes1_hat_2_item = new ItemStack(Material.ECHO_SHARD);
//        ItemMeta meta2 = fes1_hat_2_item.getItemMeta();
//        NamespacedKey fes1_hat_2 = new NamespacedKey("mlc","fes1_hat_2");
//        meta2.setItemModel(fes1_hat_2);
//        meta2.itemName( Component.text("活动帽子2"));
//        EquippableComponent equippableComponent2 = meta2.getEquippable();
//        equippableComponent2.setSlot(EquipmentSlot.HEAD);
//        meta2.setEquippable(equippableComponent2);
//        meta2.setRarity(ItemRarity.UNCOMMON);
//        meta2.setTooltipStyle(NamespacedKey.fromString("mlc:mlc"));
//
//        Damageable damageable2 = (Damageable)meta2;
//        damageable2.setMaxDamage(407);
//        damageable2.setDamage(0);
//
//        meta2.addAttributeModifier(Attribute.MOVEMENT_SPEED,am);
//        meta2.addAttributeModifier(Attribute.ARMOR,am1);
//        meta2.addAttributeModifier(Attribute.ARMOR_TOUGHNESS,am2);
//        meta2.addAttributeModifier(Attribute.ATTACK_KNOCKBACK,am3);
//
//        fes1_hat_2_item.setItemMeta((ItemMeta) damageable2);
//        fes1_hat_2_item.setItemMeta(meta2);
//
//        //3item
//        ItemStack fes1_hat_3_item = new ItemStack(Material.ECHO_SHARD);
//        ItemMeta meta3 = fes1_hat_3_item.getItemMeta();
//        NamespacedKey fes1_hat_3 = new NamespacedKey("mlc","fes1_hat_3");
//        meta3.setItemModel(fes1_hat_3);
//        meta3.itemName( Component.text("活动帽子3"));
//        EquippableComponent equippableComponent3 = meta3.getEquippable();
//        equippableComponent3.setSlot(EquipmentSlot.HEAD);
//        meta3.setEquippable(equippableComponent3);
//        meta3.setRarity(ItemRarity.UNCOMMON);
//        meta3.setTooltipStyle(NamespacedKey.fromString("mlc:mlc"));
//
//        Damageable damageable3 = (Damageable)meta3;
//        damageable3.setMaxDamage(407);
//        damageable3.setDamage(0);
//
//        meta3.addAttributeModifier(Attribute.MOVEMENT_SPEED,am);
//        meta3.addAttributeModifier(Attribute.ARMOR,am1);
//        meta3.addAttributeModifier(Attribute.ARMOR_TOUGHNESS,am2);
//        meta3.addAttributeModifier(Attribute.ATTACK_KNOCKBACK,am3);
//
//        fes1_hat_3_item.setItemMeta((ItemMeta) damageable3);
//        fes1_hat_3_item.setItemMeta(meta3);
//
//        //4item
//        ItemStack mlc_partyhat_1_item = new ItemStack(Material.ECHO_SHARD);
//        ItemMeta meta4 = mlc_partyhat_1_item.getItemMeta();
//        NamespacedKey mlc_partyhat_1 = new NamespacedKey("mlc","mlc_partyhat_1");
//        meta4.setItemModel(mlc_partyhat_1);
//        meta4.itemName( Component.text("生日帽子"));
//        List<Component> lore1 = new ArrayList<>();
//        lore1.add(Component.text("生日快乐喵")
//                .decoration(TextDecoration.ITALIC,false)
//                .color(TextColor.fromHexString("#eea468"))
//        );
//
//        meta4.lore(lore1);
//
//        EquippableComponent equippableComponent4 = meta4.getEquippable();
//        equippableComponent4.setSlot(EquipmentSlot.HEAD);
//        meta4.setEquippable(equippableComponent4);
//        meta4.setRarity(ItemRarity.EPIC);
//        meta4.setTooltipStyle(NamespacedKey.fromString("mlc:mlc"));
//
//        Damageable damageable4 = (Damageable)meta4;
//        damageable4.setMaxDamage(407);
//        damageable4.setDamage(0);
//
//        mlc_partyhat_1_item.setItemMeta((ItemMeta) damageable4);
//        mlc_partyhat_1_item.setItemMeta(meta4);
//
//
//
////        inv.setItem(0,quit);
//        inv.setItem(0,fes1_hat_1_item);
//        inv.setItem(1,fes1_hat_2_item);
//        inv.setItem(2,fes1_hat_3_item);
//        inv.setItem(3,mlc_partyhat_1_item);
    }
    public void open(){
        owner.openInventory(inv);
    }


}


