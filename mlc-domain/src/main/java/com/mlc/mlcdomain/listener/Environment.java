package com.mlc.mlcdomain.listener;

import com.mlc.mlcdomain.dataManager.Databasemanager;
import com.mlc.mlcdomain.dataManager.DomainData;
import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;


public class Environment implements Listener {

    /**
     * 检查指定位置是否在高等级领地内(level>=3)，如果是则取消事件。
     * 先检查事件是否已取消，避免不必要的数据库查询。
     */
    private static boolean isProtectedLocation(Location loc, Cancellable event) {
        if (event.isCancelled()) return true; // 已被取消，无需再次查询
        DomainData domainData = Databasemanager.getDomainAt(
                loc.getWorld().getName(),
                loc.getChunk().getX(),
                loc.getChunk().getZ());
        return domainData != null && domainData.getLevel() >= 3;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handle(EntityDamageByEntityEvent event) {
        if (isProtectedLocation(event.getEntity().getLocation(), event)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handle1(EntityExplodeEvent event) {
        if (isProtectedLocation(event.getEntity().getLocation(), event)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handle2(BlockExplodeEvent event) {
        if (isProtectedLocation(event.getBlock().getLocation(), event)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handle3(HangingBreakByEntityEvent event) {
        if (isProtectedLocation(event.getEntity().getLocation(), event)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handle4(EntityTeleportEvent event) {
        if (isProtectedLocation(event.getEntity().getLocation(), event)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handle5(CreatureSpawnEvent event) {
        if (isProtectedLocation(event.getEntity().getLocation(), event)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handle6(BlockIgniteEvent event) {
        if (isProtectedLocation(event.getBlock().getLocation(), event)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handle7(BlockFormEvent event) {
        if (isProtectedLocation(event.getBlock().getLocation(), event)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handle8(BlockFadeEvent event) {
        if (isProtectedLocation(event.getBlock().getLocation(), event)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handle9(BlockFromToEvent event) {
        if (isProtectedLocation(event.getBlock().getLocation(), event)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handle10(BlockPistonExtendEvent event) {
        if (isProtectedLocation(event.getBlock().getLocation(), event)) {
            event.setCancelled(true);
        }
    }
}
