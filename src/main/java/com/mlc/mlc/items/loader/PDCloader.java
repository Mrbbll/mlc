package com.mlc.mlc.items.loader;

import net.kyori.adventure.text.Component;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

import static com.mlc.mlc.Mlc.armortype;
import static com.mlc.mlc.Mlc.damagetype;


public class PDCloader {
    public static void loadpdc(ConfigurationSection configurationSection, ItemMeta meta){
        ConfigurationSection section = configurationSection.getConfigurationSection("tagged");
        if (section != null) {
            for(String type : section.getKeys(false)){
                PersistentDataContainer persistentDataContainer = meta.getPersistentDataContainer();
                if(type.equals("armortype")){
                    persistentDataContainer.set(armortype, PersistentDataType.STRING, section.getString("armortype","entity"));
                    List<Component> lore = meta.lore();
                    if (lore != null) {
                        lore.addLast(Component.text("防御类型："+section.getString("armortype","entity")));
                    }
                    meta.lore(lore);
                }
                if(type.equals("damagetype")){
                    persistentDataContainer.set(damagetype, PersistentDataType.STRING, section.getString("damagetype","entity"));
                    List<Component> lore = meta.lore();
                    if (lore != null) {
                        lore.addLast(Component.text("攻击类型："+section.getString("damagetype","entity")));
                    }
                    meta.lore(lore);
                }
            }
        }
    }
}
