package com.mlc.mlc.totalassault.data;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Arrays;

/** 总力战配置和内置资源文件的读取实现。 */
public final class Filereader {
    private static final String ACTIVITY_RESOURCE = "totalassault/test-activity.yml";
    private static final String SCHEMA_RESOURCE = "/totalassault/schema.sql";

    private Filereader() {}

    /** 活动配置：活动 ID、显示名称、Boss ID、开始时间和按顺序排列的难度 ID。 */
    public record ActivityConfig(String id, String name, String bossId, Instant start, List<String> difficulties) {
        public ActivityConfig {
            difficulties = List.copyOf(difficulties);
        }
    }

    static ActivityConfig readActivity(JavaPlugin plugin) {
        File file = new File(plugin.getDataFolder(), ACTIVITY_RESOURCE);
        if (!file.exists()) plugin.saveResource(ACTIVITY_RESOURCE, false);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        String id = Objects.requireNonNull(config.getString("activity.id"), "缺少活动 ID");
        String name = Objects.requireNonNull(config.getString("activity.name"), "缺少活动名称");
        String boss = Objects.requireNonNull(config.getString("activity.boss-id"), "缺少 Boss ID");
        String start = Objects.requireNonNull(config.getString("activity.start"), "缺少活动开始时间");
        Instant startTime = LocalDateTime.parse(start, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                .atZone(ZoneId.of("Asia/Shanghai")).toInstant();
        List<String> order = Arrays.stream(difficulties.values())
                .map(difficulties::getId)
                .toList();
        return new ActivityConfig(id, name, boss, startTime, order);
    }

    static Path databasePath(JavaPlugin plugin) {
        return plugin.getDataFolder().toPath().resolve("totalassault").resolve("totalassault.db");
    }

    static void prepareDatabaseDirectory(Path file) throws IOException {
        if (file.getParent() != null) Files.createDirectories(file.getParent());
    }

    static String readDatabaseSchema() throws IOException {
        try (InputStream in = Filereader.class.getResourceAsStream(SCHEMA_RESOURCE)) {
            if (in == null) throw new IOException("缺少总力战数据库结构资源: " + SCHEMA_RESOURCE);
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
