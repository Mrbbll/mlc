package com.mlc.mlc.mlcmain.items.itemmannager;

import com.mlc.mlc.mlcmain.items.loader.*;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.EquippableComponent;

import java.io.File;
import java.util.*;

import static com.mlc.mlc.Mlc.instance;
import static com.mlc.mlc.Mlc.miniMessage;
import static com.mlc.mlc.mlcmain.items.loader.Itemflagloader.applyItemFlags;

public class Fesitems {
    public static Map<Integer,ItemStack> itemsmap;
    public static void init(){
        if(itemsmap!=null && !itemsmap.isEmpty()){
            itemsmap.clear();
        }
        itemsmap = new HashMap<>();
        File file = new File(instance.getDataFolder(), "items/fesitems.yml");
        if (!file.exists()) {
            instance.saveResource("items/fesitems.yml", false);
        }
        FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection configurationSection =fileConfiguration.getConfigurationSection("items");
        if (configurationSection != null) {
            Set<String> itemids = configurationSection.getKeys(false);
            for(String itemid:itemids){

                loaditem(Integer.parseInt(itemid),configurationSection);
            }
        }
    }

    public static void loaditem(Integer itemid,ConfigurationSection configurationSection){
        ConfigurationSection configurationSection_item = configurationSection.getConfigurationSection(itemid.toString());

        if (configurationSection_item != null) {
            if(!configurationSection_item.contains("type")){
                return;
            }

            ItemStack itemStack = ItemStack.of(Objects.requireNonNull(Material.getMaterial(configurationSection_item.getString("type", "STONE"))));
            ItemMeta meta =itemStack.getItemMeta();

            if(configurationSection_item.contains("name")){
                meta.itemName(miniMessage.deserialize(Objects.requireNonNull(configurationSection_item.getString("name","null"))));
            }

            if(configurationSection_item.contains("resource")){
                meta.setItemModel(NamespacedKey.fromString(Objects.requireNonNull(configurationSection_item.getString("resource","null"))));
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
                Durabilityloader.loaddurability(meta,configurationSection_item);
            }

            if(configurationSection_item.contains("attribute_modifiers")){
                Attributeloader.applyAttributes(meta,configurationSection_item);
            }

            if(configurationSection_item.contains("head")){
                if(configurationSection_item.getBoolean("head")){
                    EquippableComponent equippableComponent = meta.getEquippable();
                    equippableComponent.setSlot(EquipmentSlot.HEAD);
                    meta.setEquippable(equippableComponent);
                }
            }

            if(configurationSection_item.contains("tagged")){
                PDCloader.loadpdc(configurationSection_item,meta,itemid);
            }

            itemStack.setItemMeta(meta);
            itemsmap.put(itemid, itemStack);

        }
    }


}
