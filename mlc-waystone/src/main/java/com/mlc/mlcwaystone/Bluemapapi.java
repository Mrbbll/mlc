package com.mlc.mlcwaystone;

import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.BlueMapWorld;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import de.bluecolored.bluemap.api.markers.POIMarker;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Optional;

import static com.mlc.mlcwaystone.Mlcwaystone.instance;

public class Bluemapapi {

    private static final String MARKER_SET_ID = "公共传送点";
    private static final String MARKER_ID_PREFIX = "waystone_";

    /**
     * Initialize BlueMap integration. Called from Mlcwaystone.init().
     * Registers listeners that fire when BlueMap is ready.
     */
    public static void init() {
        BlueMapAPI.onEnable(api -> {
            instance.getLogger().info("Waystone BlueMap 集成已启用");

            // Create marker sets for all worlds
            createMarkerSets(api);

            // Sync all existing public waystones to BlueMap
            Mlcwaystone.waystoneDataMap.values().stream()
                    .filter(WaystoneData::isPublic)
                    .forEach(data -> createMarker(api, data));
        });

        BlueMapAPI.onDisable(api -> {
            instance.getLogger().info("Waystone BlueMap 集成已禁用");
        });
    }

    /**
     * Create marker sets in each world/map.
     */
    private static void createMarkerSets(BlueMapAPI api) {
        api.getWorlds().forEach(world -> {
            MarkerSet markerSet = MarkerSet.builder()
                    .label("公共传送点")
                    .defaultHidden(false)
                    .toggleable(true)
                    .build();
            world.getMaps().forEach(map -> {
                map.getMarkerSets().put(MARKER_SET_ID, markerSet);
            });
        });
    }

    /**
     * Create a POI marker on BlueMap for a public waystone.
     */
    public static void createWaystoneMarker(WaystoneData data) {
        if (!data.isPublic()) return;
        BlueMapAPI.getInstance().ifPresent(api -> createMarker(api, data));
    }

    /**
     * Remove a POI marker from BlueMap.
     */
    public static void removeWaystoneMarker(WaystoneData data) {
        if (!data.isPublic()) return;
        BlueMapAPI.getInstance().ifPresent(api -> removeMarker(api, data));
    }

    /**
     * Update a POI marker on BlueMap (remove + recreate).
     */
    public static void updateWaystoneMarker(WaystoneData data) {
        if (!data.isPublic()) return;
        BlueMapAPI.getInstance().ifPresent(api -> {
            removeMarker(api, data);
            createMarker(api, data);
        });
    }

    // --- Internal helpers ---

    private static void createMarker(BlueMapAPI api, WaystoneData data) {
        try {
            Location loc = data.getLocation();
            World bukkitWorld = loc.getWorld();
            if (bukkitWorld == null) return;

            Optional<BlueMapWorld> blueMapWorld = api.getWorld(bukkitWorld.getName());
            if (blueMapWorld.isEmpty()) return;

            String markerId = MARKER_ID_PREFIX + data.getId().toString();

            // Build HTML detail string with min-width + nowrap to prevent narrow wrapping
            String detail = "<div style='text-align:center; min-width:160px; white-space:nowrap;'>"
                    + "<b>" + data.getIconName() + "</b><br/>"
                    + "所有者: " + data.getOwnerName() + "<br/>"
                    + "坐标: " + (int) loc.getX() + ", " + (int) loc.getY() + ", " + (int) loc.getZ()
                    + "</div>";

            // Position the marker at the top of the lodestone (Y + 1.5, center of block above)
            POIMarker marker = POIMarker.builder()
                    .label(data.getIconName())
                    .detail(detail)
                    .position(loc.getX() + 0.5, loc.getY() + 1.5, loc.getZ() + 0.5)
                    .build();

            blueMapWorld.get().getMaps().forEach(map -> {
                MarkerSet markerSet = map.getMarkerSets().get(MARKER_SET_ID);
                if (markerSet != null) {
                    markerSet.getMarkers().put(markerId, marker);
                }
            });
        } catch (Exception e) {
            instance.getLogger().warning("创建传送点 BlueMap 标记失败: " + e.getMessage());
        }
    }

    private static void removeMarker(BlueMapAPI api, WaystoneData data) {
        try {
            Location loc = data.getLocation();
            World bukkitWorld = loc.getWorld();
            if (bukkitWorld == null) return;

            Optional<BlueMapWorld> blueMapWorld = api.getWorld(bukkitWorld.getName());
            if (blueMapWorld.isEmpty()) return;

            String markerId = MARKER_ID_PREFIX + data.getId().toString();

            blueMapWorld.get().getMaps().forEach(map -> {
                MarkerSet markerSet = map.getMarkerSets().get(MARKER_SET_ID);
                if (markerSet != null) {
                    markerSet.getMarkers().remove(markerId);
                }
            });
        } catch (Exception e) {
            instance.getLogger().warning("移除传送点 BlueMap 标记失败: " + e.getMessage());
        }
    }
}
