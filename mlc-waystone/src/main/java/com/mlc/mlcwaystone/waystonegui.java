package com.mlc.mlcwaystone;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.mlc.mlcwaystone.Mlcwaystone.file;
import static com.mlc.mlcwaystone.Mlcwaystone.itemsflie;

public class waystonegui {
    public static Inventory inv;
    public static Inventory editinv;
    public static void openwaystonegui(Player player) {
        Component text = Component.text("传送点");
        if(inv == null){
            inv = Bukkit.createInventory(null,9*6,text);
            setitem(inv);
        }
        player.openInventory(inv);
    }
    public static void openeditgui(Player player){
        Component text = Component.text("传送点编辑");
        if(editinv == null){
            editinv = Bukkit.createInventory(null,9*6,text);
            setitem(editinv);
        }
        player.openInventory(editinv);
    }


    public static void setitem(Inventory inv) {
        inv.clear();
        FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(file);
        FileConfiguration fileConfiguration_item = YamlConfiguration.loadConfiguration(itemsflie);
        ConfigurationSection configurationSection = fileConfiguration.getConfigurationSection("waystones");
        ConfigurationSection configurationSection_item = fileConfiguration_item.getConfigurationSection("items");
        if (configurationSection != null) {
            Set<String> locids = configurationSection.getKeys(false);

            int num = 0;

            for (String locid : locids) {
                ConfigurationSection locs = configurationSection.getConfigurationSection(locid);
                if (locs != null) {
                    assert configurationSection_item != null;
                    ItemStack item = ItemStack.of(Objects.requireNonNull(Material.getMaterial(configurationSection_item.getString(locid + ".type", "CAMPFIRE"))));
                    ItemMeta meta = item.getItemMeta();
                    meta.itemName(Component.text(configurationSection_item.getString(locid + ".itemname","错误名字")));

                    Location loc = Location.deserialize(locs.getValues(false));
                    List<Component> lore = new ArrayList<>();
                    lore.add(Component.text("位置:")
                            .decoration(TextDecoration.ITALIC, false)
                            .color(TextColor.fromHexString("#B5EE59")));
                    lore.add((Component.text(loc.getWorld().getName())
                            .decoration(TextDecoration.ITALIC, false)
                            .color(TextColor.fromHexString("#B5EE59"))));
                    lore.add(Component.text(loc.getX())
                            .decoration(TextDecoration.ITALIC, false)
                            .color(TextColor.fromHexString("#B5EE59")));
                    lore.add(Component.text(loc.getY())
                            .decoration(TextDecoration.ITALIC, false)
                            .color(TextColor.fromHexString("#B5EE59")));
                    lore.add(Component.text(loc.getZ())
                            .decoration(TextDecoration.ITALIC, false)
                            .color(TextColor.fromHexString("#b5ee59")));
                    meta.lore(lore);
                    item.setItemMeta(meta);
                    inv.setItem(num, item);
                    num++;
                }
            }
        }
        fileConfiguration.getList("waystones");
    }
}
