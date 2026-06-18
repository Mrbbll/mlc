package com.mlc.mlcdomain.hocks;

import com.mlc.mlcdomain.dataManager.Databasemanager;
import com.mlc.mlcdomain.dataManager.DomainData;
import com.mlc.mlcdomain.dataManager.PlayerData;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class MlcdomainPlaceholderExpansion extends PlaceholderExpansion {
    private final JavaPlugin instance;
    public MlcdomainPlaceholderExpansion(JavaPlugin instance) {
        this.instance = instance;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "mlcdomain";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Mr_bl";
    }

    @Override
    public boolean persist() {
        return true; //
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }
    @Override
    @NotNull
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        // 按需查询：只有请求domain相关占位符时才查domains表
        // %mlcdomain_domain_name%
        switch (params) {
            case "domain_name" -> {
                DomainData domainData = Databasemanager.getDomainAt(
                        player.getWorld().getName(),
                        player.getLocation().getChunk().getX(),
                        player.getLocation().getChunk().getZ());
                return domainData != null ? domainData.getDomain() : "";
            }

            // %mlcdomain_domain_owner%
            case "domain_owner" -> {
                DomainData domainData = Databasemanager.getDomainAt(
                        player.getWorld().getName(),
                        player.getLocation().getChunk().getX(),
                        player.getLocation().getChunk().getZ());
                return domainData != null ? domainData.getPlayerName() : "";
            }

            // %mlcdomain_domain_count%
            case "domain_count" -> {
                PlayerData playerData = Databasemanager.getPlayer(player.getUniqueId());
                return playerData != null ? String.valueOf(playerData.getChunkCount()) : "";
            }

            // %mlcdomain_domain_remain_days%
            case "domain_remain_days" -> {
                PlayerData playerData = Databasemanager.getPlayer(player.getUniqueId());
                return playerData != null ? String.valueOf(playerData.getRemainDays()) : "";
            }
        }
        return "";
    }


}
