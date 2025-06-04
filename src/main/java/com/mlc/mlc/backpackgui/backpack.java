package com.mlc.mlc.backpackgui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import static com.mlc.mlc.Mlc.instance;

public class backpack {
    private static final String BACKPACK_KEY = "mlc:backpack";


    public Inventory inventorycreater(Player player,ItemStack itemStack) throws IOException {
        Inventory inv = Bukkit.createInventory(player, 3 * 9,
                Component.text("背包")
                .decoration(TextDecoration.ITALIC,false).decoration(TextDecoration.BOLD,true)
                .color(TextColor.fromHexString("#eea468")));
        ItemMeta meta = itemStack.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        NamespacedKey key = NamespacedKey.fromString(BACKPACK_KEY);
        if (key != null && !pdc.has(key, PersistentDataType.LONG)) {
            long timeMillis = System.currentTimeMillis();
            pdc.set(key,PersistentDataType.LONG,timeMillis);
        };
        itemStack.setItemMeta(meta);
        pdc = meta.getPersistentDataContainer();

        if (key != null && pdc.has(key,PersistentDataType.LONG)){
            long num = pdc.getOrDefault(key,PersistentDataType.LONG,1L);
            if(num==1){
                return inv;
            };
            File backpackDir = new File(instance.getDataFolder(), "backpacks");
            if(!backpackDir.exists()){
                backpackDir.mkdirs();
            }
            String string = player.getUniqueId() + ".yml";
            File file = new File(backpackDir,string);
            if(!file.exists())
            {
                file.createNewFile();
            }
            FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(file);
            List<Map<?, ?>> itemMaps = fileConfiguration.getMapList("" + num);

            ItemStack[] items = deserializeItems(itemMaps);
            for (ItemStack item : items) {
                if (item != null && item.getType() != Material.AIR) {
                    inv.addItem(item);
                }
            }
        }
        return inv;
    }

    private ItemStack[] deserializeItems(List<Map<?, ?>> itemMaps) {
        ItemStack[] items = new ItemStack[27]; // 3x9格子

        for (int i = 0; i < Math.min(itemMaps.size(), items.length); i++) {
            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> itemMap = (Map<String, Object>) itemMaps.get(i);
                if (itemMap != null && !itemMap.isEmpty()) {
                    items[i] = ItemStack.deserialize(itemMap);
                }
            } catch (Exception e) {
                instance.getLogger().log(Level.WARNING, "反序列化物品失败", e);
            }
        }

        return items;
    }


    public void openbackpack(Player player, ItemStack itemStack) throws IOException {
        player.openInventory(inventorycreater(player,itemStack));
    }

    public void saveBackpack(Player player, Inventory inventory) throws IOException {

        ItemStack itemStack = player.getInventory().getItemInMainHand();
        ItemMeta meta =itemStack.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        NamespacedKey key = NamespacedKey.fromString(BACKPACK_KEY);
        assert key != null;
        if (pdc.has(key,PersistentDataType.LONG)) {
            File mailDir = new File(instance.getDataFolder(), "backpacks");
            String string = player.getUniqueId() + ".yml";
            File file = new File(mailDir,string);
            if(!file.exists())
            {
                file.createNewFile();
            }
            FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(file);


            long num = pdc.getOrDefault(key,PersistentDataType.LONG,1L);
            if(num==1){
                return;
            }
            List<Map<String, Object>> itemMaps = new ArrayList<>();
            ItemStack[] contents = inventory.getContents();

            for (ItemStack item : contents) {
                if (item != null && item.getType() != Material.AIR) {
                    itemMaps.add(item.serialize());
                } else {
                    itemMaps.add(null); // 保存空槽位
                }
            }

            fileConfiguration.set(num + "", itemMaps);

            try {
                fileConfiguration.save(file);
            } catch (IOException e) {
                instance.getLogger().log(Level.SEVERE, "保存背包数据失败", e);
            }

        }


    }
}
