package com.mlc.mlcwaystone;

import com.mlc.mlcwaystone.commands.Reload;
import com.mlc.mlcwaystone.listener.WaystoneListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class Mlcwaystone {
    public static JavaPlugin instance;
    public static File dataFile;
    public static ConcurrentHashMap<UUID, WaystoneData> waystoneDataMap = new ConcurrentHashMap<>();

    /**
     * Initialize waystone system. Called from Mlc.onEnable().
     */
    public static void init(JavaPlugin plugin) {
        instance = plugin;
        instance.getLogger().info("传送石碑系统加载中...");

        // Ensure data directory exists
        File waystoneDir = new File(instance.getDataFolder(), "waystone");
        if (!waystoneDir.exists()) {
            waystoneDir.mkdirs();
        }

        dataFile = new File(waystoneDir, "data.yml");

        // Save default resource only if file does not exist (first run)
        if (!dataFile.exists()) {
            instance.saveResource("waystone/data.yml", false);
        }

        // Load existing data
        loadData();

        // Register listener and command
        Bukkit.getPluginManager().registerEvents(new WaystoneListener(), instance);
        Objects.requireNonNull(Bukkit.getPluginCommand("reloadwaystone")).setExecutor(new Reload());

        instance.getLogger().info("传送石碑系统加载成功，当前传送点数量: " + waystoneDataMap.size());
    }

    /**
     * Load all waystones from data.yml into the in-memory map.
     */
    public static void loadData() {
        waystoneDataMap.clear();
        FileConfiguration config = YamlConfiguration.loadConfiguration(dataFile);
        ConfigurationSection section = config.getConfigurationSection("waystones");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                try {
                    UUID id = UUID.fromString(key);
                    ConfigurationSection wsSection = section.getConfigurationSection(key);
                    if (wsSection != null) {
                        WaystoneData data = WaystoneData.fromConfig(id, wsSection);
                        waystoneDataMap.put(id, data);
                    }
                } catch (IllegalArgumentException e) {
                    instance.getLogger().warning("跳过无效的传送点ID: " + key);
                }
            }
        }
    }

    /**
     * Save all waystones from the in-memory map to data.yml.
     */
    public static void saveData() {
        FileConfiguration config = new YamlConfiguration();
        for (Map.Entry<UUID, WaystoneData> entry : waystoneDataMap.entrySet()) {
            config.set("waystones." + entry.getKey().toString(),
                    WaystoneData.toConfig(entry.getValue()));
        }
        try {
            config.save(dataFile);
        } catch (IOException e) {
            instance.getLogger().severe("保存传送石碑数据失败: " + e.getMessage());
        }
    }

    /**
     * Add a new waystone to the map and persist to disk.
     */
    public static void addWaystone(WaystoneData data) {
        waystoneDataMap.put(data.getId(), data);
        saveData();
    }

    /**
     * Remove a waystone from the map and persist to disk.
     */
    public static void removeWaystone(UUID id) {
        waystoneDataMap.remove(id);
        saveData();
    }

    /**
     * Find a waystone by its LODESTONE block location.
     * Returns the waystone data, or null if not found.
     */
    public static WaystoneData getWaystoneAt(Location lodestoneLoc) {
        for (WaystoneData data : waystoneDataMap.values()) {
            if (data.getLocation().equals(lodestoneLoc)) {
                return data;
            }
        }
        return null;
    }

    /**
     * Check if a LODESTONE location already has a waystone registered.
     */
    public static boolean isWaystoneLocation(Location lodestoneLoc) {
        return getWaystoneAt(lodestoneLoc) != null;
    }

}
