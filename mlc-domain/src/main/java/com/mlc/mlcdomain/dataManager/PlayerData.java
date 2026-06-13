package com.mlc.mlcdomain.dataManager;

import java.sql.Timestamp;
import java.util.UUID;

public class PlayerData {

    private UUID playerUuid;
    private String playerName;
    private int chunkCount;
    private int remainDays;

    public PlayerData(UUID playerUuid, String playerName, int chunkCount, int remainDays) {
        this.playerUuid = playerUuid;
        this.playerName = playerName;
        this.chunkCount = chunkCount;
        this.remainDays = remainDays;
    }

    public PlayerData() {
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
     * @return chunkCount
     */
    public int getChunkCount() {
        return chunkCount;
    }

    /**
     * 设置
     * @param chunkCount
     */
    public void setChunkCount(int chunkCount) {
        this.chunkCount = chunkCount;
    }

    /**
     * 获取
     * @return remainDays
     */
    public int getRemainDays() {
        return remainDays;
    }

    /**
     * 设置
     * @param remainDays
     */
    public void setRemainDays(int remainDays) {
        this.remainDays = remainDays;
    }

    public String toString() {
        return "PlayerData{playerUuid = " + playerUuid + ", playerName = " + playerName + ", chunkCount = " + chunkCount + ", remainDays = " + remainDays + "}";
    }
}
