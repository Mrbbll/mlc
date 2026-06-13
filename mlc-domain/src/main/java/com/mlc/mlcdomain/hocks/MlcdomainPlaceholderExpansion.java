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
        DomainData domainData = Databasemanager.getDomainAt(player.getWorld().getName(),player.getLocation().getChunk().getX(),player.getLocation().getChunk().getZ());
        PlayerData playerData = Databasemanager.getPlayer(player.getUniqueId());
        // %mlcdomain_domain_name%
        if(params.equals("domain_name")){
            if (domainData != null) {
                return domainData.getDomain();
            }
            else return "";
        }
        // %mlcdomain_domain_count%
        if(params.equals("domain_count")){
            if (playerData != null) {
                return String.valueOf(playerData.getChunkCount());
            }
            else return "";
        }
        // %mlcdomain_domain_owner%
        if(params.equals("domain_owner")){
            if (domainData != null) {
                return domainData.getPlayerName();
            }
            return "";
        }
        // %mlcdomain_domain_remain_days%
        if(params.equals("domain_remain_days")){
            if (playerData != null) {
                return String.valueOf(playerData.getRemainDays());
            }
            else return "";
        }
        return "";
    }


}
