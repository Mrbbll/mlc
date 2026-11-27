package com.mlc.mlc.totalassault.data;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
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
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/** 总力战配置和内置资源文件的读取实现。 */
public final class Filereader {
    // 活动配置、数据库和建表脚本均位于总力战专用目录。
    private static final String DATA_DIRECTORY = "totalassault";
    private static final String ACTIVITY_RESOURCE = "totalassault/test-activity.yml";
    private static final String SCHEMA_RESOURCE = "/totalassault/schema.sql";
    private static final String DATABASE_FILE = "totalassault.db";
    private static final String LOBBY_WORLD = "lobbyworld";

    // 活动开始时间按北京时间解析，避免受服务器所在时区影响。
    private static final ZoneId ACTIVITY_ZONE = ZoneId.of("Asia/Shanghai");
    private static final DateTimeFormatter ACTIVITY_TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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
        Instant startTime = LocalDateTime.parse(start, ACTIVITY_TIME_FORMAT)
                .atZone(ACTIVITY_ZONE).toInstant();
        List<String> order = Arrays.stream(difficulties.values())
                .map(difficulties::getId)
                .toList();
        return new ActivityConfig(id, name, boss, startTime, order);
    }

    public record ArenaConfig(
            String schematic,
            Point pasteOrigin,
            Point playerSpawn,
            Point bossSpawn
    ) {

        /**
         * 挑战世界内的坐标。
         */
        public record Point(
                double x,
                double y,
                double z,
                float yaw,
                float pitch
        ) {
            /**
             * 根据实际的挑战世界生成位置。
             */
            public Location location(World world) {
                if (world == null) {
                    throw new IllegalArgumentException("挑战世界不能为空");
                }

                return new Location(world, x, y, z, yaw, pitch);
            }
        }

        /**
         * 从现有总力战活动配置中读取场地信息。
         *
         * 请先调用 Datamanager.readActivityConfig(plugin)，
         * 确保默认活动配置已经释放到插件数据目录。
         */
        public static ArenaConfig load(JavaPlugin plugin) {
            File file = new File(
                    plugin.getDataFolder(),
                    ACTIVITY_RESOURCE
            );

            if (!file.isFile()) {
                throw new IllegalStateException(
                        "总力战配置文件不存在：" + file.getAbsolutePath()
                );
            }

            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

            return new ArenaConfig(
                    requiredString(config, "arena.schematic"),
                    readPoint(config, "arena.paste-origin"),
                    readPoint(config, "arena.player-spawn"),
                    readPoint(config, "arena.boss-spawn")
            );
        }

        /**
         * 读取准备大厅和公共出口共用的出生点。
         *
         * lobbyworld 必须已经由服务器加载。
         */
        public static Location lobbySpawn() {
            World lobby = Bukkit.getWorld(LOBBY_WORLD);

            if (lobby == null) {
                throw new IllegalStateException(
                        "未找到大厅世界：" + LOBBY_WORLD
                );
            }

            return lobby.getSpawnLocation();
        }

        private static Point readPoint(
                YamlConfiguration config,
                String path
        ) {
            return new Point(
                    requiredDouble(config, path + ".x"),
                    requiredDouble(config, path + ".y"),
                    requiredDouble(config, path + ".z"),
                    (float) config.getDouble(path + ".yaw", 0),
                    (float) config.getDouble(path + ".pitch", 0)
            );
        }

        private static String requiredString(
                YamlConfiguration config,
                String path
        ) {
            String value = config.getString(path);

            if (value == null || value.isBlank()) {
                throw new IllegalArgumentException(
                        "缺少总力战配置：" + path
                );
            }

            return value;
        }

        private static double requiredDouble(
                YamlConfiguration config,
                String path
        ) {
            if (!config.isDouble(path) && !config.isInt(path)) {
                throw new IllegalArgumentException(
                        "缺少数字配置：" + path
                );
            }

            double value = config.getDouble(path);

            if (!Double.isFinite(value)) {
                throw new IllegalArgumentException(
                        "配置必须是有效数字：" + path
                );
            }

            return value;
        }

    }

    /** 获取数据库路径，不在此处创建文件。 */
    static Path databasePath(JavaPlugin plugin) {
        return plugin.getDataFolder().toPath().resolve(DATA_DIRECTORY).resolve(DATABASE_FILE);
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
