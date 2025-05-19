package com.mlc.mlc;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.mlc.mlc.Mlc.instance;

public class mailgui {

    public Inventory inv;
    public Player owner;



    public mailgui(Player player){
        inv = Bukkit.createInventory(player,6*9, Component.text("邮箱"));
        owner = player;





        String string = player.getName() + ".yml";
        File mailDir = new File(instance.getDataFolder(), "mail");
        File file = new File(mailDir,string);
        FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(file);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> items = (List<Map<String, Object>>) fileConfiguration.getList("item");
        if (items == null) {
            items = Collections.emptyList(); // 使用空列表替代null
        }
        else {
            Bukkit.broadcast(Component.text("a"));
        }
        for(int i =0;i<54&&i<items.size();i++){
            Map<String, Object> itemData = items.get(i);
            ItemStack item = createItemFromData(itemData);
            inv.setItem(i, item);
        }
    }



    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> getAllItems(FileConfiguration fileConfiguration) {
        return (List<Map<String, Object>>) fileConfiguration.getList("item");
    }

    @SuppressWarnings("unchecked")
    private static ItemStack createItemFromData(Map<String, Object> itemData) {
        try {
            // 获取物品类型和数量
            Bukkit.broadcast(Component.text((String)itemData.get("type")));
            Material material = Material.getMaterial((String) itemData.get("type"));
            int amount = (int) itemData.get("amount");
            ItemStack item = new ItemStack(material, amount);
            Map<String,Object> itemmeta = (Map<String, Object>) itemData.get("itemmeta");
            item = ItemStack.deserialize(itemmeta);

            return item;
        } catch (Exception e) {
            e.printStackTrace();
            // 如果出错，返回一个错误占位符
            return new ItemStack(Material.BARRIER);
        }
    }

    public void open(){
        owner.openInventory(inv);
    }

};


