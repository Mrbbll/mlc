package com.mlc.mlc.mlcmain.items.loader;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

import static com.mlc.mlc.Mlc.*;
import static com.mlc.mlc.mlcmain.items.itemmannager.Cratesitems.*;


public class PDCloader {
    public static void loadpdc(ConfigurationSection configurationSection, ItemMeta meta, Integer itemid){
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
                //稀有度
                if(type.equals("cratesraritytier")){
                    persistentDataContainer.set(raritytier, PersistentDataType.INTEGER, section.getInt("raritytier",5));
                    List<Component> lore = meta.lore();
                    Component component = null;
                            switch (section.getInt("raritytier", 0)) {
                        case 3 -> {
                            component = Component.text("T3 RARITY").color(TextColor.color(0x2AFFF5));
                            t3list.add(itemid);
                        }
                        case 2 -> {
                            component = Component.text("T2 RARITY").color(TextColor.color(0xFFD71C));
                            t2list.add(itemid);
                        }
                        case 1 -> {
                            component = Component.text("T1 RARITY").color(TextColor.color(0xFF0000));
                            t1list.add(itemid);
                        }
                    };
                    if (lore != null) {
                        lore.addLast(component);
                    meta.lore(lore);
                }
            }
        }
    }
}
}
