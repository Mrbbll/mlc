package com.mlc.mlcdomain.api;

import com.mlc.mlcdomain.dataManager.Databasemanager;
import com.mlc.mlcdomain.dataManager.DomainData;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public final class DomainProtection {

    private DomainProtection() {
    }

    public static boolean canBreak(Player player, Block block) {
        DomainData domain = Databasemanager.getDomainAt(
                block.getWorld().getName(),
                block.getChunk().getX(),
                block.getChunk().getZ()
        );

        if (domain == null) {
            return true;
        }

        if (domain.getLevel() == 0) {
            return true;
        }

        return Databasemanager.checkPermission(
                player.getUniqueId(),
                domain.getPlayerUuid()
        );
    }
}
