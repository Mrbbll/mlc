package com.mlc.mlc.hook.placeholderapi;

import com.mlc.mlc.hook.economy.Moneyfilemanager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class Mlceco extends PlaceholderExpansion {
    @Override
    public String getIdentifier() {
        return "mlc";
    }

    @Override
    public String getAuthor() {
        return "MLC";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (player == null) {
            return "";
        }
        if (identifier.equals("money")) {
            return String.valueOf(Moneyfilemanager.playermoneyMap.get(player.getUniqueId()));
        }
        return null;
    }
}
