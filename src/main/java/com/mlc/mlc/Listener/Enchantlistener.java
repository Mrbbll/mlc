package com.mlc.mlc.Listener;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class Enchantlistener implements Listener {
    @EventHandler
    public void oncilck(PlayerInteractEvent event){
        Player player = event.getPlayer();
        Block center = event.getClickedBlock();
        if(Objects.equals(event.getHand(), EquipmentSlot.OFF_HAND)){
            return;
        }
        if(center == null){
            return;
        }
        else if (!center.getType().equals(Material.SCULK_SHRIEKER)) {
            return;
        }
        else if(!player.getInventory().getItemInMainHand().equals(ItemStack.of(Material.NETHER_STAR))){
            return;
        }
        else if(!checkblock(center)){
            return;
        }
        else{
            event.setCancelled(true);
            player.sendMessage("ok");
            ItemStack tool = player.getInventory().getItem(1);
            if(tool.getType().equals(Material.NETHERITE_SWORD)){

            }
        }

    }

    public boolean checkblock(Block center){
        Location location_center = center.getLocation();
        if(location_center.clone().add(0,-1,0).getBlock().getType().equals(Material.CRYING_OBSIDIAN)
            &&location_center.clone().add(0,-2,0).getBlock().getType().equals(Material.NETHERITE_BLOCK)
                &&location_center.clone().add(-1,-2,1).getBlock().getType().equals(Material.NETHERITE_BLOCK)
                &&location_center.clone().add(0,-2,1).getBlock().getType().equals(Material.NETHERITE_BLOCK)
                &&location_center.clone().add(1,-2,1).getBlock().getType().equals(Material.NETHERITE_BLOCK)
                &&location_center.clone().add(-1,-2,0).getBlock().getType().equals(Material.NETHERITE_BLOCK)
                &&location_center.clone().add(1,-2,0).getBlock().getType().equals(Material.NETHERITE_BLOCK)
                &&location_center.clone().add(-1,-2,-1).getBlock().getType().equals(Material.NETHERITE_BLOCK)
                &&location_center.clone().add(0,-2,-1).getBlock().getType().equals(Material.NETHERITE_BLOCK)
                &&location_center.clone().add(1,-2,-1).getBlock().getType().equals(Material.NETHERITE_BLOCK)

                &&location_center.clone().add(-2,-2,2).getBlock().getType().equals(Material.SCULK_CATALYST)
                &&location_center.clone().add(2,-2,2).getBlock().getType().equals(Material.SCULK_CATALYST)
                &&location_center.clone().add(-2,-2,-2).getBlock().getType().equals(Material.SCULK_CATALYST)
                &&location_center.clone().add(2,-2,-2).getBlock().getType().equals(Material.SCULK_CATALYST)

                &&location_center.clone().add(0,-1,2).getBlock().getType().equals(Material.RESPAWN_ANCHOR)
                &&location_center.clone().add(-2,-1,0).getBlock().getType().equals(Material.RESPAWN_ANCHOR)
                &&location_center.clone().add(2,-1,0).getBlock().getType().equals(Material.RESPAWN_ANCHOR)
                &&location_center.clone().add(0,-1,-2).getBlock().getType().equals(Material.RESPAWN_ANCHOR)
        ){
            return true;
        };

        return false;
    };


}
