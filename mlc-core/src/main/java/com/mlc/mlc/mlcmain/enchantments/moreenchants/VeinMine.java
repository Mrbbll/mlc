package com.mlc.mlc.mlcmain.enchantments.moreenchants;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import com.mlc.mlcdomain.api.DomainProtection;

import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

public class VeinMine implements Listener {
    public static Enchantment VEINMINE;

    public VeinMine() {
        VEINMINE = RegistryAccess.registryAccess()
                .getRegistry(RegistryKey.ENCHANTMENT)
                .getOrThrow(EnchantmentKeys.VEINMINE);
    }


    @EventHandler
    public void onBlockbreak(BlockBreakEvent event){
        if(event.isCancelled()) return;

        Player player = event.getPlayer();
        if(player.isSneaking()){
            return;
        }


        ItemStack tool = player.getInventory().getItemInMainHand();
        if (tool.getEnchantmentLevel(VEINMINE) == 0) return;   // 没这把附魔就不管

        Material block = event.getBlock().getType();

        if (!(tool.getItemMeta() instanceof Damageable damageable)) {
            return;
        }

        int limit = tool.getEnchantmentLevel(VEINMINE) * 8;    // 等级越高挖越多

        breakVein(event.getBlock(), block, limit, tool,player);
        event.setCancelled(true);
    }


    private void breakVein(Block start, Material ore, int limit,ItemStack tool,Player player) {
        // BFS 洪水填充:从起点扩散,只处理同类方块
        Deque<Block> queue = new ArrayDeque<>();
        Set<Block> visited = new HashSet<>();
        queue.add(start);
        while (!queue.isEmpty() && visited.size() < limit) {
            Block b = queue.poll();
            if (!visited.add(b) ||
                    b.getType() != ore ||
                    !DomainProtection.canBreak(player, b)){
                continue;
            }
            b.breakNaturally(tool);                 // 掉落物按玩家工具计算
            tool.damage(1,player);

            ItemStack currentTool = player.getInventory().getItemInMainHand();
            if (currentTool.getType().isAir()) {
                break;
            }

            for (Block n : neighbors(b)) {
                if (n.getType() == ore) {
                    queue.add(n);
                }
            }
        }
    }

    private Iterable<? extends Block> neighbors(Block b) {
        Set<Block> blocks = new HashSet<>();
        blocks.add(b.getRelative(BlockFace.DOWN));
        blocks.add(b.getRelative(BlockFace.UP));
        blocks.add(b.getRelative(BlockFace.EAST));
        blocks.add(b.getRelative(BlockFace.SOUTH));
        blocks.add(b.getRelative(BlockFace.WEST));
        blocks.add(b.getRelative(BlockFace.NORTH));
        return blocks;
    }


}
