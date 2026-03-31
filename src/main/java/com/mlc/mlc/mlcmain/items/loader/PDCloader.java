package com.mlc.mlc.mlcmain.items.loader;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
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
                    persistentDataContainer.set(raritytier, PersistentDataType.INTEGER, section.getInt("raritytier",3));
                    List<Component> lore = meta.lore();
                    Component component = null;
                            switch (section.getInt("cratesraritytier", 0)) {
                        case 3 -> {
                            component = miniMessage.deserialize("<#2AFFF5><!i>T3 RARITY");
                            t3list.add(itemid);
                        }
                        case 2 -> {
                            component = miniMessage.deserialize("<#FFD71C><!i>T2 RARITY");
                            t2list.add(itemid);
                        }
                        case 1 -> {
                            component = miniMessage.deserialize("<#FF0000><!i>T1 RARITY");
                            t1list.add(itemid);
                        }
                    };
                    if (lore != null) {
                        lore.addLast(component);
                        meta.lore(lore);
                    } else {
                        List<Component> itemLore = new ArrayList<>();
                        itemLore.add(component);
                        meta.lore(itemLore);

                    }
                }
            }
        }
    }
}
