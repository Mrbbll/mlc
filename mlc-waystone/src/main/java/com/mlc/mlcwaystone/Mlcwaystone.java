package com.mlc.mlcwaystone;

import com.mlc.mlcwaystone.commands.Reload;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import com.mlc.mlcwaystone.listener.WaystoneListener;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class Mlcwaystone {
    public static JavaPlugin instance;
    public static Economy economy;
    public static File dataFile;
    private static WaystoneBeamManager beamManager;
    public static ConcurrentHashMap<UUID, WaystoneData> waystoneDataMap = new ConcurrentHashMap<>();
    private static volatile Set<BlockPosition> waystoneTopPositions = Set.of();

    private record BlockPosition(int x, int y, int z) {}

    /**
     * Initialize waystone system. Called from Mlc.onEnable().
     */
    public static void init(JavaPlugin plugin, Economy currency) {
        instance = plugin;
        economy = currency;
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

        beamManager = new WaystoneBeamManager(() -> waystoneDataMap.values());
        beamManager.start(plugin);

        // Initialize BlueMap integration when the optional plugin is installed.
        initializeBlueMapIntegration();

        // Register listener and command
        Bukkit.getPluginManager().registerEvents(new WaystoneListener(), instance);
        instance.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS,
                event -> event.registrar().register("reloadwaystone", "重新加载传送石碑", new Reload()));

        instance.getLogger().info("传送石碑系统加载成功，当前传送点数量: " + waystoneDataMap.size());
    }

    /**
     * Remove transient visuals when the plugin shuts down.
     */
    public static void shutdown() {
        if (beamManager != null) {
            beamManager.shutdown();
            beamManager = null;
        }
    }

    /** Load all waystones from data.yml into the in-memory map. */
    public static void loadData() {
        waystoneDataMap.clear();
        FileConfiguration config = YamlConfiguration.loadConfiguration(dataFile);
        ConfigurationSection section = config.getConfigurationSection("waystones");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                UUID id;
                try {
                    id = UUID.fromString(key);
                } catch (IllegalArgumentException e) {
                    instance.getLogger().warning("跳过无效的传送点ID: " + key);
                    continue;
                }

                ConfigurationSection wsSection = section.getConfigurationSection(key);
                if (wsSection == null) {
                    instance.getLogger().warning("跳过无效的传送点数据（缺少配置节）: " + key);
                    continue;
                }

                try {
                    WaystoneData data = WaystoneData.fromConfig(id, wsSection);
                    waystoneDataMap.put(id, data);
                } catch (IllegalArgumentException | NullPointerException e) {
                    instance.getLogger().warning("跳过无效的传送点数据: " + key + " (" + e.getMessage() + ")");
                }
            }
        }
        rebuildWaystoneTopPositions();
    }

    /**
     * Initialize BlueMap integration only when its optional plugin is enabled.
     */
    public static void initializeBlueMapIntegration() {
        if (isBlueMapEnabled()) {
            Bluemapapi.init();
        }
    }

    /**
     * Rebuild the waystone marker set from the current data.
     */
    public static void syncBlueMapMarkers() {
        if (isBlueMapEnabled()) {
            Bluemapapi.syncWaystoneMarkers();
        }
    }

    public static void createBlueMapMarker(WaystoneData data) {
        if (isBlueMapEnabled()) {
            Bluemapapi.createWaystoneMarker(data);
        }
    }

    public static void removeBlueMapMarker(WaystoneData data) {
        if (isBlueMapEnabled()) {
            Bluemapapi.removeWaystoneMarker(data);
        }
    }

    public static void updateBlueMapMarker(WaystoneData data) {
        if (isBlueMapEnabled()) {
            Bluemapapi.updateWaystoneMarker(data);
        }
    }

    private static boolean isBlueMapEnabled() {
        return Bukkit.getPluginManager().isPluginEnabled("BlueMap");
    }

    /**
     * Save all waystones from the in-memory map to data.yml.
     */
    public static boolean saveData() {
        FileConfiguration config = new YamlConfiguration();
        for (Map.Entry<UUID, WaystoneData> entry : waystoneDataMap.entrySet()) {
            config.set("waystones." + entry.getKey().toString(),
                    WaystoneData.toConfig(entry.getValue()));
        }
        try {
            config.save(dataFile);
            return true;
        } catch (IOException e) {
            instance.getLogger().severe("保存传送石碑数据失败: " + e.getMessage());
            return false;
        }
    }

    /**
     * Add a new waystone to the map and persist to disk.
     * Syncs public waystones to BlueMap.
     */
    public static void addWaystone(WaystoneData data) {
        waystoneDataMap.put(data.getId(), data);
        rebuildWaystoneTopPositions();
        saveData();
        if (data.isPublic()) {
            createBlueMapMarker(data);
        }
    }

    /**
     * Remove a waystone from the map and persist to disk.
     * Removes BlueMap marker if it was public.
     */
    public static void removeWaystone(UUID id) {
        WaystoneData data = waystoneDataMap.remove(id);
        if (data != null) {
            rebuildWaystoneTopPositions();
            if (data.isPublic()) {
                removeBlueMapMarker(data);
            }
        }
        saveData();
    }

    private static void rebuildWaystoneTopPositions() {
        Set<BlockPosition> positions = new HashSet<>();
        for (WaystoneData data : waystoneDataMap.values()) {
            Location base = data.getLocation();
            positions.add(new BlockPosition(base.getBlockX(), base.getBlockY() + 1, base.getBlockZ()));
        }
        waystoneTopPositions = positions;
    }

    /** Fast coordinate filter. The caller verifies the world, block material and registration. */
    public static boolean isPossibleWaystoneTop(int x, int y, int z) {
        return waystoneTopPositions.contains(new BlockPosition(x, y, z));
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
