package com.mlc.mlc.hook.economy;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.mlc.mlc.Mlc.instance;

public class Moneyfilemanager {
    private static HikariDataSource dataSource;
    private final Plugin plugin = instance;
    private final String dbName = "money_data";
    private HikariConfig config = new HikariConfig();
    public static Map<UUID, Integer> playermoneyMap = new HashMap<>();

    public Moneyfilemanager() throws SQLException {
        moneyfilecreater();
    }
    public void moneyfilecreater() throws SQLException {
        try {
            // 确保数据文件夹存在
            if (!plugin.getDataFolder().exists()) {
                plugin.getDataFolder().mkdirs();
            }

            // 数据库文件
            File databaseFile = new File(plugin.getDataFolder(),  dbName+ ".db");

            // HikariCP 配置
            config.setJdbcUrl("jdbc:sqlite:" + databaseFile.getAbsolutePath());
            config.setDriverClassName("org.sqlite.JDBC");
            config.setLeakDetectionThreshold(60000);
            dataSource = new HikariDataSource(config);
        } catch (Exception e) {
            plugin.getLogger().severe("\n\nHikariCP 初始化失败: " + e.getMessage() + "\n\n");
        }

        //连接数据库
        if (dataSource != null) {
            try (Connection connection = dataSource.getConnection();
                 ) {
                createTables(connection);
                plugin.getLogger().info("\n\n✓ 数据库表连接完成\n\n");
            }
        }

        //初始化

        moneyfileload();
    }

    private void createTables(Connection connection) {
        try {
            // 创建玩家货币表
            //player_uuid player_name money
            Statement statement = connection.createStatement();
            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS money_data (player_uuid VARCHAR(36) PRIMARY KEY, player_name VARCHAR(16), money INTEGER)
                    """);

            String index = "CREATE INDEX IF NOT EXISTS idx_player_uuid ON money_data (player_uuid)";
            statement.executeUpdate(index);

            plugin.getLogger().info("\n\n 数据库表创建完成 \n\n");
        } catch (SQLException e) {
            plugin.getLogger().severe("\n\n数据库表创建失败: " + e.getMessage() + "\n\n");
        }
    }

    public static void moneyfileload(){
        try {
            Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();

            // 从数据库加载玩家货币数据
            String query = "SELECT player_uuid, money FROM money_data";
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                UUID playerUUID = UUID.fromString(resultSet.getString("player_uuid"));
                int money = resultSet.getInt("money");
                playermoneyMap.put(playerUUID, money);
            }
        } catch (SQLException e) {
            instance.getLogger().severe("\n\n数据库加载玩家货币数据失败: " + e.getMessage() + "\n\n");
        }

    }
    public static int getPlayerMoney(UUID playerUUID) {
        if(!hasPlayer(playerUUID)){
            createPlayer(playerUUID, playerUUID.toString());
        }
        return playermoneyMap.getOrDefault(playerUUID, 0);
    }

    public static void setPlayerMoney(UUID playerUUID, int money) {
        // 保存到数据库
        try {
            Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            OfflinePlayer player = instance.getServer().getOfflinePlayer(playerUUID);
            String update = "UPDATE money_data SET player_name = '" + player.getName() + "', money = " + money + " WHERE player_uuid = '" + playerUUID + "'";
            statement.executeUpdate(update);
            playermoneyMap.put(playerUUID, money);
        } catch (SQLException e) {
            instance.getLogger().severe("\n\n数据库保存玩家货币数据失败: " + e.getMessage() + "\n\n");
        }
    }

    public static boolean hasPlayer(UUID playerUUID) {
        return playermoneyMap.containsKey(playerUUID);
    }

    public static boolean createPlayer(UUID playerUUID, String playerName) {
        if (hasPlayer(playerUUID)) {
            return false;
        }
        try {
            Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            String insert = "INSERT INTO money_data (player_uuid, player_name, money) VALUES ('" + playerUUID + "', '" + playerName + "', 0)";
            statement.executeUpdate(insert);
            playermoneyMap.put(playerUUID, 0);
            return true;
        } catch (SQLException e) {
            instance.getLogger().severe("\n\n数据库创建玩家货币数据失败: " + e.getMessage() + "\n\n");
            return false;
        }
    }
}

