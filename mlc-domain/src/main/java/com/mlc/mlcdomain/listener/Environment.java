package com.mlc.mlcdomain.listener;

import com.mlc.mlcdomain.dataManager.Databasemanager;
import com.mlc.mlcdomain.dataManager.DomainData;
import com.mlc.mlcdomain.uilts.Checkflag;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.jetbrains.annotations.NotNull;


public class Environment implements Listener {
//    @EventHandler
//    public void onPlayerJoin(org.bukkit.event.player.PlayerJoinEvent event) {
//        event.getPlayer().sendMessage("Welcome to the server!");
//    }
    @EventHandler(priority = EventPriority.LOWEST)
    public void handle(EntityDamageByEntityEvent event) {
        DomainData domainData = Databasemanager.getDomainAt(event.getEntity().getWorld().getName(), event.getEntity().getLocation().getChunk().getX(), event.getEntity().getLocation().getChunk().getZ());
        if (domainData == null ||domainData.getLevel() < 3) {
            return;
        }
        if (event.isCancelled()) return;
        event.setCancelled(true);
    }
    @EventHandler(priority = EventPriority.LOWEST)
    public void handle1(EntityExplodeEvent event) {
        DomainData domainData = Databasemanager.getDomainAt(event.getEntity().getWorld().getName(), event.getEntity().getLocation().getChunk().getX(), event.getEntity().getLocation().getChunk().getZ());
        if (domainData == null ||domainData.getLevel() < 3) {
            return;
        }
        if (event.isCancelled()) return;
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handle2(BlockExplodeEvent event) {
        DomainData domainData = Databasemanager.getDomainAt(event.getBlock().getWorld().getName(), event.getBlock().getLocation().getChunk().getX(), event.getBlock().getLocation().getChunk().getZ());
        if (domainData == null ||domainData.getLevel() < 3) {
            return;
        }
        if (event.isCancelled()) return;
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handle3(HangingBreakByEntityEvent event) {
        DomainData domainData = Databasemanager.getDomainAt(event.getEntity().getWorld().getName(), event.getEntity().getLocation().getChunk().getX(), event.getEntity().getLocation().getChunk().getZ());
        if (domainData == null ||domainData.getLevel() < 3) {
            return;
        }
        if (event.isCancelled()) return;
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handle4(EntityTeleportEvent event) {
        DomainData domainData = Databasemanager.getDomainAt(event.getEntity().getWorld().getName(), event.getEntity().getLocation().getChunk().getX(), event.getEntity().getLocation().getChunk().getZ());
        if (domainData == null ||domainData.getLevel() < 3) {
            return;
        }
        if (event.isCancelled()) return;
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handle5(CreatureSpawnEvent event) {
        DomainData domainData = Databasemanager.getDomainAt(event.getEntity().getWorld().getName(), event.getEntity().getLocation().getChunk().getX(), event.getEntity().getLocation().getChunk().getZ());
        if (domainData == null ||domainData.getLevel() < 3) {
            return;
        }
        if (event.isCancelled()) return;
        event.setCancelled(true);
    }
    @EventHandler(priority = EventPriority.LOWEST)
    public void handle6(BlockIgniteEvent event) {
        DomainData domainData = Databasemanager.getDomainAt(event.getBlock().getWorld().getName(), event.getBlock().getLocation().getChunk().getX(), event.getBlock().getLocation().getChunk().getZ());
        if (domainData == null ||domainData.getLevel() < 3) {
            return;
        }
        if (event.isCancelled()) return;
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handle7(BlockFormEvent event) {
        DomainData domainData = Databasemanager.getDomainAt(event.getBlock().getWorld().getName(), event.getBlock().getLocation().getChunk().getX(), event.getBlock().getLocation().getChunk().getZ());
        if (domainData == null ||domainData.getLevel() < 3) {
            return;
        }
        if (event.isCancelled()) return;
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handle8(BlockFadeEvent event) {
        DomainData domainData = Databasemanager.getDomainAt(event.getBlock().getWorld().getName(), event.getBlock().getLocation().getChunk().getX(), event.getBlock().getLocation().getChunk().getZ());
        if (domainData == null ||domainData.getLevel() < 3) {
            return;
        }
        if (event.isCancelled()) return;
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handle9(BlockFromToEvent event) {
        DomainData domainData = Databasemanager.getDomainAt(event.getBlock().getWorld().getName(), event.getBlock().getLocation().getChunk().getX(), event.getBlock().getLocation().getChunk().getZ());
        if (domainData == null ||domainData.getLevel() < 3) {
            return;
        }
        if (event.isCancelled()) return;
        event.setCancelled(true);
    }
    @EventHandler(priority = EventPriority.LOWEST)
    public void handle10(BlockPistonExtendEvent event) {
        DomainData domainData = Databasemanager.getDomainAt(event.getBlock().getWorld().getName(), event.getBlock().getLocation().getChunk().getX(), event.getBlock().getLocation().getChunk().getZ());
        if (domainData == null ||domainData.getLevel() < 3) {
            return;
        }
        if (event.isCancelled()) return;
        event.setCancelled(true);
    }
}
