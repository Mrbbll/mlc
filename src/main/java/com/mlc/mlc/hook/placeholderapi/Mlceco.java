package com.mlc.mlc.hook.placeholderapi;

import com.mlc.mlc.hook.economy.Moneyfilemanager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Mlceco extends PlaceholderExpansion {
    @Override
    public String getIdentifier() {
        return "mlc";
    }

    @Override
    public String getAuthor() {
        return "Mr_bl";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) {
            return "";
        }
        if (params.equals("money")) {
            if(!Moneyfilemanager.playermoneyMap.containsKey(player.getUniqueId())){
                Moneyfilemanager.createPlayer(player.getUniqueId(), player.getName());
            }
            return String.valueOf(Moneyfilemanager.playermoneyMap.get(player.getUniqueId()));
        }

        return null;
    }
}
