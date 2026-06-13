package com.mlc.mlcdomain.listener;

import com.mlc.mlcdomain.dataManager.Databasemanager;
import com.mlc.mlcdomain.dataManager.DomainData;
import com.mlc.mlcdomain.uilts.Checkflag;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPlaceEvent;

public class Playerlistener implements Listener {
//    @EventHandler
//    public void PlayerMove(org.bukkit.event.player.PlayerMoveEvent event) {
//        Checkflag.Checkplayerflag(event.getPlayer().getLocation(), Flags.PLAYER_MOVE, event);
//    }
    @EventHandler(priority = EventPriority.LOWEST)
    public void PlayerInteract(org.bukkit.event.player.PlayerInteractEvent event) {
        DomainData domainData = null;
        if (event.getClickedBlock() != null) {
            domainData = Databasemanager.getDomainAt(event.getClickedBlock().getLocation().getWorld().getName(), event.getClickedBlock().getLocation().getChunk().getX(), event.getClickedBlock().getLocation().getChunk().getZ());
        }
        if(domainData == null){
            return;
        }else {
            Checkflag.Checkplayerflag(event.getPlayer(), domainData, Flags.PLAYER_INTERACT, event);
        }
    }
    @EventHandler(priority = EventPriority.LOWEST)
    public void PlayerBreakBlock(BlockBreakEvent event) {
        DomainData domainData = Databasemanager.getDomainAt(event.getBlock().getLocation().getWorld().getName(), event.getBlock().getLocation().getChunk().getX(), event.getBlock().getLocation().getChunk().getZ());
        if(domainData == null){
            return;
        }else {
            Checkflag.Checkplayerflag(event.getPlayer(), domainData, Flags.BLOCK_BREAK, event);
        }
    }
    @EventHandler(priority = EventPriority.LOWEST)
    public void PlayerPlaceBlock(BlockPlaceEvent event) {
        DomainData domainData = Databasemanager.getDomainAt(event.getBlock().getLocation().getWorld().getName(), event.getBlock().getLocation().getChunk().getX(), event.getBlock().getLocation().getChunk().getZ());
        if(domainData == null){
            return;
        }else {
            Checkflag.Checkplayerflag(event.getPlayer(), domainData, Flags.BLOCK_PLACE, event);
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void handle(EntityPlaceEvent event) {
        DomainData domainData = Databasemanager.getDomainAt(event.getBlock().getWorld().getName(), event.getBlock().getLocation().getChunk().getX(), event.getBlock().getLocation().getChunk().getZ());
        if (domainData == null ||domainData.getLevel() < 3) {
            return;
        }
        if (event.isCancelled()) return;
        event.setCancelled(true);
    }



}
