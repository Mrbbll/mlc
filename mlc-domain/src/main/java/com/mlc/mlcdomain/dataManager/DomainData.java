package com.mlc.mlcdomain.dataManager;

import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.util.UUID;

public class DomainData {
    private int id;
    private String domain;
    private UUID playerUuid;
    private String playerName;
    private String world;
    private int x;
    private int z;
    private int level;
    private Timestamp createdAt;

    public DomainData(int id, String domain, UUID playerUuid, String playerName, String world, int x, int z, int level, Timestamp createdAt) {
        this.id = id;
        this.domain = domain;
        this.playerUuid = playerUuid;
        this.playerName = playerName;
        this.world = world;
        this.x = x;
        this.z = z;
        this.level = level;
        this.createdAt = createdAt;
    }

    public DomainData() {
    }

    /**
     * 获取
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * 设置
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * 获取
     * @return domain
     */
    public String getDomain() {
        return domain;
    }

    /**
     * 设置
     * @param domain
     */
    public void setDomain(String domain) {
        this.domain = domain;
    }

    /**
     * 获取
     * @return playerUuid
     */
    public UUID getPlayerUuid() {
        return playerUuid;
    }

    /**
     * 设置
     * @param playerUuid
     */
    public void setPlayerUuid(UUID playerUuid) {
        this.playerUuid = playerUuid;
    }

    /**
     * 获取
     * @return playerName
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * 设置
     * @param playerName
     */
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    /**
     * 获取
     * @return world
     */
    public String getWorld() {
        return world;
    }

    /**
     * 设置
     * @param world
     */
    public void setWorld(String world) {
        this.world = world;
    }

    /**
     * 获取
     * @return x
     */
    public int getX() {
        return x;
    }

    /**
     * 设置
     * @param x
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * 获取
     * @return z
     */
    public int getZ() {
        return z;
    }

    /**
     * 设置
     * @param z
     */
    public void setZ(int z) {
        this.z = z;
    }

    /**
     * 获取
     * @return level
     */
    public int getLevel() {
        return level;
    }

    /**
     * 设置
     * @param level
     */
    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * 获取
     * @return createdAt
     */
    public Timestamp getCreatedAt() {
        return createdAt;
    }

    /**
     * 设置
     * @param createdAt
     */
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String toString() {
        return "DomainData{id = " + id + ", domain = " + domain + ", playerUuid = " + playerUuid + ", playerName = " + playerName + ", world = " + world + ", x = " + x + ", z = " + z + ", level = " + level + ", createdAt = " + createdAt + "}";
    }
}
