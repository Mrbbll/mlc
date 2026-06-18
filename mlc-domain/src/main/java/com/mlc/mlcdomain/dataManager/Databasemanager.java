package com.mlc.mlcdomain.dataManager;

import com.mlc.mlcdomain.hocks.bluemap.Bluemapapi;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


import static com.mlc.mlcdomain.Mlcdomain.instance;

//三个表：domains和domain_owners，domain_members
//存储区块信息
//id domain player_uuid player_name world x z level created_at


//存储区块拥有者信息
//player_uuid player_name chunk_count remain_days created_at

//存储区块成员信息
//id member_uuid owner_uuid permission_level created_at
//  -- 1=成员，2=管理员

public class Databasemanager {
    private static HikariDataSource dataSource;
    private final Plugin plugin = instance;
    private final String dbName = "domain_data";
    private final HikariConfig config = new HikariConfig();


    public Databasemanager() throws SQLException {
        initializeDataSource();
        preloadDomainCache();
    }

    /**
     * 启动时一次性加载全部领地到缓存。
     */
    private static void preloadDomainCache() {
        List<DomainData> allDomains = getDomainDataList();
        if (allDomains != null) {
            for (DomainData domain : allDomains) {
                String key = domain.getWorld() + "_" + domain.getX() + "_" + domain.getZ();
                Domaincache.addCache(key, domain);
            }
            instance.getLogger().info("✓ 领地缓存预热完成: " + Domaincache.size() + " 个领地区块");
        }
    }



    private void initializeDataSource() throws SQLException {
        try {
            // 确保数据文件夹存在
            if (!plugin.getDataFolder().exists()) {
                plugin.getDataFolder().mkdirs();
            }

            // 数据库文件
            File databaseFile = new File(plugin.getDataFolder(), dbName + ".db");

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
            try(Connection connection = dataSource.getConnection()){
                plugin.getLogger().info("\n\n✓ 数据库表连接完成\n\n");
                createTables(connection);

            }catch (SQLException e){
                instance.getServer().shutdown();
                plugin.getLogger().severe("\n\n数据库连接失败，服务器已关闭\n\n");
            }
        }




    }

    private void createTables(Connection connection) throws SQLException {
        // 创建 domains 表
        String createDomainTable = """
            CREATE TABLE IF NOT EXISTS domains (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            domain VARCHAR(64) NOT NULL,
            player_uuid VARCHAR(36) NOT NULL,
            player_name VARCHAR(16) NOT NULL,
            world VARCHAR(64) NOT NULL DEFAULT 'world',
            x INTEGER NOT NULL,
            z INTEGER NOT NULL,
            level INTEGER DEFAULT 1,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            UNIQUE(world, x, z),
            FOREIGN KEY (player_uuid) REFERENCES domain_owners(player_uuid)
            ON DELETE CASCADE
            )
            """;

        // 创建 domain_owners 表
        String createOwnersTable = """
            CREATE TABLE IF NOT EXISTS domain_owners (
            player_uuid VARCHAR(36) PRIMARY KEY,
            player_name VARCHAR(16) NOT NULL,
            chunk_count INTEGER DEFAULT 0,
            remain_days INTEGER DEFAULT 30,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            UNIQUE(player_uuid)
            )
            """;

        // 创建 domain_members 表
        String createMembersTable = """
            CREATE TABLE IF NOT EXISTS domain_members (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            member_uuid VARCHAR(36) NOT NULL,
            owner_uuid VARCHAR(36) NOT NULL,
            permission_level INTEGER DEFAULT 1,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            UNIQUE(member_uuid, owner_uuid),
            FOREIGN KEY (owner_uuid) REFERENCES domain_owners(player_uuid) ON DELETE CASCADE
            )
            """;
        // 创建索引
        String index1 = "CREATE INDEX IF NOT EXISTS idx_domain_location ON domains(world, x, z)";
        String index2 = "CREATE INDEX IF NOT EXISTS idx_domain_player ON domains(player_uuid)";
        String index3 = "CREATE INDEX IF NOT EXISTS idx_member_owner ON domain_members(member_uuid)";
        // 创建 Statement
        try(Statement statement = connection.createStatement()) {

            // 创建表
            statement.execute(createDomainTable);
            statement.execute(createOwnersTable);
            statement.execute(createMembersTable);

            // 创建索引
            statement.execute(index1);
            statement.execute(index2);
            statement.execute(index3);

            plugin.getLogger().info("\n\n✓ 数据库表初始化完成\n\n");
        }
    }

    // 关闭连接池
    public void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            plugin.getLogger().info("\n\n✓ HikariCP 连接池已关闭\n\n");
        }
    }



    /**
     * 创建或更新玩家信息
     */
    public static void createOrUpdatePlayer(@NotNull UUID playerUuid, String playerName) {
        String sql = """
            INSERT INTO domain_owners (player_uuid, player_name, chunk_count)
            VALUES (?, ?, 0)
            ON CONFLICT(player_uuid) DO UPDATE SET player_name = excluded.player_name
            """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, playerUuid.toString());
            pstmt.setString(2, playerName);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            instance.getLogger().warning("更新玩家信息失败: " + e.getMessage());
        }
    }

    /**
     * 获取玩家信息
     */
    public static PlayerData getPlayer(UUID playerUuid) {

        // 从缓存中获取玩家信息
        PlayerData playerData = Playercache.getCache(playerUuid);
        if (playerData != null) {
            return playerData;
        }

        String sql = "SELECT * FROM domain_owners WHERE player_uuid = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ) {
            pstmt.setString(1, playerUuid.toString());
            try(ResultSet rs = pstmt.executeQuery();){
                if (rs.next()) {
                    playerData = new PlayerData(
                            UUID.fromString(rs.getString("player_uuid")),
                            rs.getString("player_name"),
                            rs.getInt("chunk_count"),
                            rs.getInt("remain_days")
                    );
                    // 缓存玩家信息
                    Playercache.updateCache(playerUuid, playerData);
                    return playerData;
                }
            }
        } catch (SQLException e) {
            instance.getLogger().warning("获取玩家信息失败: " + e.getMessage());
        }
        return null;
    }

    /**
     * 更新玩家剩余天数
     */
    public static void updatePlayerRemainDays(UUID playerUuid, int remainDays) {

        String sql = "UPDATE domain_owners SET remain_days = ? WHERE player_uuid = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, remainDays);
            pstmt.setString(2, playerUuid.toString());
            pstmt.executeUpdate();

            // 更新缓存
            PlayerData playerData = Playercache.getCache(playerUuid);
            if (playerData != null) {
                playerData.setRemainDays(remainDays);
                Playercache.updateCache(playerUuid, playerData);
            }

        } catch (SQLException e) {
            instance.getLogger().warning("更新玩家剩余天数失败: " + e.getMessage());
        }
    }
    /**
     * 获取玩家剩余天数
     */
     public static int getPlayerRemainDays(UUID playerUuid) {
         // 从缓存中获取玩家信息
        PlayerData playerData = Playercache.getCache(playerUuid);
        if (playerData != null) {
            return playerData.getRemainDays();
        }

        String sql = "SELECT remain_days FROM domain_owners WHERE player_uuid = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ) {

            pstmt.setString(1, playerUuid.toString());
             try(ResultSet rs = pstmt.executeQuery();){
                 if (rs.next()) {
                    return rs.getInt("remain_days");
                }
             }
        } catch (SQLException e) {
            instance.getLogger().warning("获取玩家剩余天数失败: " + e.getMessage());
        }
        return 0;
    }


    /**
     * 更新玩家领地数量
     */
    public static void updatePlayerChunkCount(UUID playerUuid, int delta) {
        String sql = "UPDATE domain_owners SET chunk_count = chunk_count + ? WHERE player_uuid = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, delta);
            pstmt.setString(2, playerUuid.toString());
            pstmt.executeUpdate();
            // 更新缓存
            PlayerData playerData = Playercache.getCache(playerUuid);
            if (playerData != null) {
                playerData.setChunkCount(playerData.getChunkCount() + delta);
                Playercache.updateCache(playerUuid, playerData);
            }

        } catch (SQLException e) {
            instance.getLogger().warning("更新玩家领地数量失败: " + e.getMessage());
        }
    }


    /**
     * 添加领地
     */
    public static boolean createDomain(String domainName, UUID playerUuid, String playerName,
                                       String world, int x, int z, int level) {
        // 先确保玩家存在
        createOrUpdatePlayer(playerUuid, playerName);

        String sql = """
            INSERT INTO domains
            (domain, player_uuid, player_name, world, x, z, level)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, domainName);
            pstmt.setString(2, playerUuid.toString());
            pstmt.setString(3, playerName);
            pstmt.setString(4, world);
            pstmt.setInt(5, x);
            pstmt.setInt(6, z);
            pstmt.setInt(7, level);

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                // 获取自增id并直接构造缓存对象
                int generatedId = 0;
                try (ResultSet keys = pstmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        generatedId = keys.getInt(1);
                    }
                }

                // 直接构造 DomainData 加入缓存
                DomainData domainData = new DomainData(
                        generatedId, domainName, playerUuid, playerName,
                        world, x, z, level, new Timestamp(System.currentTimeMillis()));
                Domaincache.addCache(world + "_" + x + "_" + z, domainData);

                // 更新地图上的领地标记
                Bluemapapi.createDomainMarker(domainName, playerName, Bukkit.getWorld(world), x, z, level);
                // 更新玩家领地数量
                updatePlayerChunkCount(playerUuid, 1);

                return true;
            }

        } catch (SQLException e) {
            instance.getLogger().warning("添加领地失败: " + e.getMessage());
        }
        return false;
    }

    /**
     * 获取指定位置的领地（仅走缓存，启动时已全量加载）。
     */
    public static DomainData getDomainAt(String world, int x, int z) {
        return Domaincache.getCache(world + "_" + x + "_" + z);
    }

    /**
     * 获取玩家的所有领地
     */
    public static List<DomainData> getPlayerDomains(UUID playerUuid) {

        List<DomainData> domains = new ArrayList<>();
        String sql = "SELECT * FROM domains WHERE player_uuid = ? ORDER BY created_at DESC";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ) {

            pstmt.setString(1, playerUuid.toString());
             try(ResultSet rs = pstmt.executeQuery();) {
                 while (rs.next()) {
                     domains.add(new DomainData(
                             rs.getInt("id"),
                             rs.getString("domain"),
                             UUID.fromString(rs.getString("player_uuid")),
                             rs.getString("player_name"),
                             rs.getString("world"),
                             rs.getInt("x"),
                             rs.getInt("z"),
                             rs.getInt("level"),
                             rs.getTimestamp("created_at")
                     ));
             }
            }

        } catch (SQLException e) {
            instance.getLogger().warning("获取玩家领地失败: " + e.getMessage());
        }
        return domains;
    }

    /**
     * 删除领地
     */
    public static boolean deleteDomain(String world, int x, int z, UUID playerUuid) {
        // 先获取领地信息
        DomainData domain = getDomainAt(world, x, z);
        if (domain == null) {
            return false;
        }

        String sql = "DELETE FROM domains WHERE world = ? AND x = ? AND z = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, world);
            pstmt.setInt(2, x);
            pstmt.setInt(3, z);

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                // 删除地图上的领地标记
                Bluemapapi.removeDomainMarker(domain.getDomain(), Bukkit.getWorld(world), x, z);
                // 更新玩家领地数量
                updatePlayerChunkCount(playerUuid, -1);
                // 清除缓存
                Domaincache.removeCache(world + "_" + x + "_" + z);
                return true;
            }

        } catch (SQLException e) {
            instance.getLogger().warning("删除领地失败: " + e.getMessage());
        }
        return false;
    }

    /**
     * 更新领地信息，三种构造
     */
    public static boolean updateDomain(DomainData domain, String newName) {
        String sql = "UPDATE domains SET domain = ? WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newName);
            pstmt.setInt(2, domain.getId());

            // 更新缓存
            domain.setDomain(newName);
            Domaincache.updateCache(domain.getWorld() + "_" + domain.getX() + "_" + domain.getZ(), domain);

            // 更新地图上的领地标记
            Bluemapapi.updateDomainMarker(newName, domain.getPlayerName(), Bukkit.getWorld(domain.getWorld()), domain.getX(), domain.getZ(), domain.getLevel());
            return pstmt.executeUpdate() > 0;


        } catch (SQLException e) {
            instance.getLogger().warning("更新领地信息失败: " + e.getMessage());
        }
        return false;
    }

    public static boolean updateDomain(DomainData domain, int newLevel) {
        String sql = "UPDATE domains SET level = ? WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, newLevel);
            pstmt.setInt(2, domain.getId());

            // 更新缓存
            domain.setLevel(newLevel);
            Domaincache.updateCache(domain.getWorld() + "_" + domain.getX() + "_" + domain.getZ(), domain);
            // 更新地图上的领地标记
            Bluemapapi.updateDomainMarker(domain.getDomain(), domain.getPlayerName(), Bukkit.getWorld(domain.getWorld()), domain.getX(), domain.getZ(), newLevel);



            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            instance.getLogger().warning("更新领地信息失败: " + e.getMessage());
        }
        return false;
    }

    public static boolean updateDomain(DomainData domain, String newName, int newLevel) {
        String sql = "UPDATE domains SET domain = ?, level = ? WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newName);
            pstmt.setInt(2, newLevel);
            pstmt.setInt(3, domain.getId());
            // 更新缓存
            domain.setDomain(newName);
            domain.setLevel(newLevel);
            Domaincache.updateCache(domain.getWorld() + "_" + domain.getX() + "_" + domain.getZ(), domain);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            instance.getLogger().warning("更新领地信息失败: " + e.getMessage());
        }
        return false;
    }
    //领地权限管理
     /**
     * 添加成员
     */
    public static boolean addMember(UUID memberUuid, UUID ownerUuid) {
        String sql = """
        INSERT OR IGNORE INTO domain_members (member_uuid, owner_uuid, permission_level)
        VALUES (?, ? ,?)
        """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, memberUuid.toString());
            pstmt.setString(2, ownerUuid.toString());
            if(memberUuid.equals(ownerUuid)){
                pstmt.setInt(3, 2);
            }
            else{
                pstmt.setInt(3, 1);
            }
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            instance.getLogger().warning("添加成员失败: " + e.getMessage());
        }
        return false;
    }
    //删除成员
    public static boolean deleteMember(UUID memberUuid, UUID ownerUuid) {
        if(memberUuid.equals(ownerUuid)){
            return false;
        }
        String sql = "DELETE FROM domain_members WHERE member_uuid = ? AND owner_uuid = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, memberUuid.toString());
            pstmt.setString(2, ownerUuid.toString());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            instance.getLogger().warning("删除成员失败: " + e.getMessage());
        }
        return false;
    }
    //判断是不是有权限
     public static boolean checkPermission(UUID playerUuid, UUID ownerUuid) {
        if (playerUuid.equals(ownerUuid)){
            return true;
        }

        String sql = "SELECT permission_level FROM domain_members WHERE member_uuid = ? AND owner_uuid = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ) {

            pstmt.setString(1, playerUuid.toString());
            pstmt.setString(2, ownerUuid.toString());
             try(ResultSet rs = pstmt.executeQuery();){
                 return rs.next();
             }
        } catch (SQLException e) {
            instance.getLogger().warning("检查权限失败: " + e.getMessage());
        }
        return false;
    }
    //获取所有领地信息
    public static List<DomainData> getDomainDataList() {
        String sql = "SELECT * FROM domains";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            List<DomainData> domainDataList = new ArrayList<>();
            while (rs.next()) {
                DomainData domainData = new DomainData(
                        rs.getInt("id"),
                        rs.getString("domain"),
                        UUID.fromString(rs.getString("player_uuid")),
                        rs.getString("player_name"),
                        rs.getString("world"),
                        rs.getInt("x"),
                        rs.getInt("z"),
                        rs.getInt("level"),
                        rs.getTimestamp("created_at")
                );
                domainDataList.add(domainData);
            }
            return domainDataList;
        } catch (SQLException e) {
            instance.getLogger().warning("获取领地信息失败: " + e.getMessage());
        }
        return null;
    }
}
