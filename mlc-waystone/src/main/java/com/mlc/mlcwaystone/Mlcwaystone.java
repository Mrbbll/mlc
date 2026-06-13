package com.mlc.mlcwaystone;

import com.mlc.mlcwaystone.commands.Reload;
import com.mlc.mlcwaystone.listener.Events;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;

import static com.mlc.mlcwaystone.waystonegui.editinv;
import static com.mlc.mlcwaystone.waystonegui.inv;

public final class Mlcwaystone {
    public static JavaPlugin instance;
    public static File file;
    public static File itemsflie;
    public static ItemStack waystoneitem = new ItemStack(Material.ECHO_SHARD);

    /**
     * Initialize waystone system. Called from Mlc.onEnable().
     */
    public static void init(JavaPlugin plugin) {
        instance = plugin;
        instance.saveResource("waystone/location.yml", false);
        instance.saveResource("waystone/displayitems.yml", false);

        file = new File(instance.getDataFolder(), "waystone/location.yml");
        itemsflie = new File(instance.getDataFolder(), "waystone/displayitems.yml");

        instance.getLogger().info("\n\n\n传送石插件加载成功\n\n\n");
        Bukkit.getPluginManager().registerEvents(new Events(), instance);

        ItemMeta meta = waystoneitem.getItemMeta();
        meta.setItemModel(NamespacedKey.fromString("mlc:waystone"));
        meta.setTooltipStyle(NamespacedKey.fromString("mlc:mlc"));
        meta.itemName(Component.text("传送石碑"));
        waystoneitem.setItemMeta(meta);

        inv = Bukkit.createInventory(null, 9 * 6, Component.text("传送点"));
        editinv = Bukkit.createInventory(null, 9 * 6, Component.text("传送点编辑"));
        waystonegui.setitem(inv);
        waystonegui.setitem(editinv);
        Objects.requireNonNull(Bukkit.getPluginCommand("reloadwaystone")).setExecutor(new Reload());
    }
}
