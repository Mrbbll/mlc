package com.mlc.mlc.mlcmain.damageindicator;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

final class DamageIndicatorConfig {
    private static final String FILE_NAME = "damage-indicator.yml";
    private final JavaPlugin plugin;
    private FileConfiguration configuration;

    DamageIndicatorConfig(JavaPlugin plugin) {
        this.plugin = plugin;
        reload();
    }

    void reload() {
        File file = new File(plugin.getDataFolder(), FILE_NAME);
        if (!file.exists()) {
            plugin.saveResource(FILE_NAME, false);
        }
        configuration = YamlConfiguration.loadConfiguration(file);
    }

    public boolean enabled() {
        return configuration.getBoolean("enabled", true);
    }

    public double viewDistance() {
        return Math.max(1.0d, configuration.getDouble("view-distance", 32.0d));
    }

    public double heightOffset() {
        return configuration.getDouble("position.height-offset", 0.55d);
    }

    public double horizontalSpread() {
        return Math.max(0.0d, configuration.getDouble("position.horizontal-spread", 0.35d));
    }

    public String damageFormat() {
        return configuration.getString("formats.NORMAL", "<gray>NORMAL <white><damage></white>");
    }

    public String missFormat() {
        return configuration.getString("miss-format", "<white><bold>MISS</bold>");
    }

    public float spawnScale() {
        return (float) configuration.getDouble("animation.spawn-scale", 0.10d);
    }

    public float popScale() {
        return (float) configuration.getDouble("animation.pop-scale", 1.25d);
    }

    public float settleScale() {
        return (float) configuration.getDouble("animation.settle-scale", 1.00d);
    }

    public long popDelay() {
        return Math.max(0L, configuration.getLong("animation.pop-delay", 1L));
    }

    public long settleDelay() {
        return Math.max(0L, configuration.getLong("animation.settle-delay", 5L));
    }

    public long shrinkDelay() {
        return Math.max(0L, configuration.getLong("animation.shrink-delay", 16L));
    }

    public long removeDelay() {
        return Math.max(1L, configuration.getLong("animation.remove-delay", 19L));
    }

}
