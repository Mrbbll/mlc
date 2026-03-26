package com.mlc.mlc.mlcmain.backpack.backpackgui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import static com.mlc.mlc.Mlc.backpackfile;
import static com.mlc.mlc.Mlc.instance;

public class Backpack {
    private static final String BACKPACK_KEY = "mlc:backpack";

    public static Inventory inventorycreater(Player player,ItemStack itemStack) {
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
        }
        itemStack.setItemMeta(meta);
        pdc = meta.getPersistentDataContainer();

        if (key != null && pdc.has(key,PersistentDataType.LONG)){
            long num = pdc.getOrDefault(key,PersistentDataType.LONG,1L);
            if(num==1){
                return inv;
            }
            List<Map<?, ?>> itemMaps = backpackfile.getMapList("" + num);

            ItemStack[] items = deserializeItems(itemMaps);
            for (ItemStack item : items) {
                if (item != null && item.getType() != Material.AIR) {
                    inv.addItem(item);
                }
            }
        }
        return inv;
    }

    private static ItemStack[] deserializeItems(List<Map<?, ?>> itemMaps) {
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

    public static void openbackpack(Player player, ItemStack itemStack) {
        player.openInventory(inventorycreater(player,itemStack));
    }

    public static void saveBackpack(Player player, Inventory inventory ,ItemStack itemStack) throws IOException {

        ItemMeta meta =itemStack.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        NamespacedKey key = NamespacedKey.fromString(BACKPACK_KEY);
        assert key != null;
        if (pdc.has(key,PersistentDataType.LONG)) {
            File mailDir = new File(instance.getDataFolder(), "backpacks");
            String string = "backpack" + ".yml";
            File file = new File(mailDir,string);

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
            backpackfile.set(num + "", itemMaps);
            backpackfile.save(file);
        }
    }
}
