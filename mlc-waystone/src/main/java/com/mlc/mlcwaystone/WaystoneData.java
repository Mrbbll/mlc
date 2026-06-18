package com.mlc.mlcwaystone;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Data class holding a single waystone's information.
 */
public class WaystoneData {
    private UUID id;
    private UUID owner;
    private String ownerName;
    private String type;        // "PRIVATE" or "PUBLIC"
    private Location location;  // LODESTONE block center
    private String iconType;    // Material enum name, e.g. "CAMPFIRE"
    private String iconName;    // Display name in GUI
    private long createdAt;     // System.currentTimeMillis() at creation

    public WaystoneData(UUID id, UUID owner, String ownerName, String type,
                        Location location, String iconType, String iconName, long createdAt) {
        this.id = id;
        this.owner = owner;
        this.ownerName = ownerName;
        this.type = type;
        this.location = location;
        this.iconType = iconType;
        this.iconName = iconName;
        this.createdAt = createdAt;
    }

    // --- Getters ---

    public UUID getId() { return id; }
    public UUID getOwner() { return owner; }
    public String getOwnerName() { return ownerName; }
    public String getType() { return type; }
    public Location getLocation() { return location; }
    public String getIconType() { return iconType; }
    public String getIconName() { return iconName; }
    public long getCreatedAt() { return createdAt; }

    // --- Setters ---

    public void setId(UUID id) { this.id = id; }
    public void setOwner(UUID owner) { this.owner = owner; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    public void setType(String type) { this.type = type; }
    public void setLocation(Location location) { this.location = location; }
    public void setIconType(String iconType) { this.iconType = iconType; }
    public void setIconName(String iconName) { this.iconName = iconName; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    /**
     * Deserialize a WaystoneData from a YAML ConfigurationSection.
     */
    public static WaystoneData fromConfig(UUID id, ConfigurationSection section) {
        UUID owner = UUID.fromString(section.getString("owner", "00000000-0000-0000-0000-000000000000"));
        String ownerName = section.getString("ownerName", "未知");
        String type = section.getString("type", "PUBLIC");
        Location location = Location.deserialize(
                section.getConfigurationSection("location").getValues(false));
        String iconType = section.getString("iconType", "CAMPFIRE");
        String iconName = section.getString("iconName", "未命名传送点");
        long createdAt = section.getLong("createdAt", System.currentTimeMillis());
        return new WaystoneData(id, owner, ownerName, type, location, iconType, iconName, createdAt);
    }

    /**
     * Serialize this WaystoneData to a Map suitable for YAML storage.
     */
    public static Map<String, Object> toConfig(WaystoneData data) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("owner", data.getOwner().toString());
        map.put("ownerName", data.getOwnerName());
        map.put("type", data.getType());
        map.put("location", data.getLocation().serialize());
        map.put("iconType", data.getIconType());
        map.put("iconName", data.getIconName());
        map.put("createdAt", data.getCreatedAt());
        return map;
    }

    /**
     * Convenience check for whether this is a private waystone.
     */
    public boolean isPrivate() {
        return "PRIVATE".equals(type);
    }

    /**
     * Convenience check for whether this is a public waystone.
     */
    public boolean isPublic() {
        return "PUBLIC".equals(type);
    }
}
