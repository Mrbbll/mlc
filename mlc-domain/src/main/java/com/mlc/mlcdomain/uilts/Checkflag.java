package com.mlc.mlcdomain.uilts;

import com.mlc.mlcdomain.dataManager.Databasemanager;
import com.mlc.mlcdomain.dataManager.DomainData;
import com.mlc.mlcdomain.listener.Flags;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;

import javax.swing.text.html.parser.Entity;

import static com.mlc.mlcdomain.listener.Flags.PLAYER_INTERACT;

public class Checkflag {
    public Checkflag() {
    }

    public static void Checkplayerflag(@NotNull Player player, @NotNull DomainData domainData, Flags flag, Cancellable event){
        if(domainData.getLevel() == 0){
            return;
        }else if(Databasemanager.checkPermission(player.getUniqueId(),domainData.getPlayerUuid())){
            return;
        }

            switch (flag){
                case BLOCK_PLACE:
                case BLOCK_BREAK:
                    if(domainData.getLevel()>=1){
                    player.sendMessage("你没有权限");
                    event.setCancelled(true);
                    }

                    break;
                case PLAYER_INTERACT:
                    if(domainData.getLevel()>=2){
                    player.sendMessage("你没有权限");
                    event.setCancelled(true);
                    }
                    break;
            }


    }

}
