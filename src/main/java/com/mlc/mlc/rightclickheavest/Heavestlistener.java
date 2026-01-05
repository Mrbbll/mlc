package com.mlc.mlc.rightclickheavest;

import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static com.mlc.mlc.Mlc.crops;

public class Heavestlistener implements Listener {


    @EventHandler
    public void rightclik(PlayerInteractEvent event){
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block block = event.getClickedBlock();
        Player player = event.getPlayer();
        if (block == null || !crops.contains(block.getType())) return;
        if (!isFullyGrown(block)) return;
        harvestCrop(player, block);
    }

    private void harvestCrop(Player player, Block block) {
        Ageable ageable = (Ageable) block.getBlockData();

        // 获取掉落物
        List<ItemStack> drops = new ArrayList<>(block.getDrops());


        // 重置作物状态（保留种子）
        ageable.setAge(0);
        block.setBlockData(ageable);

        if (!player.getInventory().addItem(drops.toArray(new ItemStack[0])).isEmpty()) {
            drops.forEach(drop -> block.getWorld().dropItemNaturally(block.getLocation().add(0.5,0,0.5), drop));
        }
        player.swingMainHand();
    }

    private boolean isFullyGrown(Block block) {
        if (block.getBlockData() instanceof Ageable ageable) {
            return ageable.getAge() == ageable.getMaximumAge();
        }
        return false;
    }

}
