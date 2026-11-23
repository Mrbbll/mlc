package com.mlc.mlc.mlcmain.enchantments.listener;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.RespawnAnchor;

/** 附魔升级多方块结构检查。 */
public final class EnchantUpgradelistener {
    private EnchantUpgradelistener() {
    }

    public static boolean checkblock(Block center){
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
                &&((RespawnAnchor)location_center.clone().add(0,-1,2).getBlock().getBlockData()).getCharges()==4
                &&location_center.clone().add(-2,-1,0).getBlock().getType().equals(Material.RESPAWN_ANCHOR)
                &&((RespawnAnchor)location_center.clone().add(-2,-1,0).getBlock().getBlockData()).getCharges()==4
                &&location_center.clone().add(2,-1,0).getBlock().getType().equals(Material.RESPAWN_ANCHOR)
                &&((RespawnAnchor)location_center.clone().add(2,-1,0).getBlock().getBlockData()).getCharges()==4
                &&location_center.clone().add(0,-1,-2).getBlock().getType().equals(Material.RESPAWN_ANCHOR)
                &&((RespawnAnchor)location_center.clone().add(0,-1,-2).getBlock().getBlockData()).getCharges()==4
        ){
            return true;
        };
        return false;
    };


}
