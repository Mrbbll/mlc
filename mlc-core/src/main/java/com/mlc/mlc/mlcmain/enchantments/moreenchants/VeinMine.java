package com.mlc.mlc.mlcmain.enchantments.moreenchants;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import com.mlc.mlcdomain.api.DomainProtection;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class VeinMine implements Listener {
    public static Enchantment VEINMINE;
    private final Set<UUID> mining = new HashSet<>();

    public VeinMine() {
        VEINMINE = RegistryAccess.registryAccess()
                .getRegistry(RegistryKey.ENCHANTMENT)
                .getOrThrow(EnchantmentKeys.VEINMINE);
    }


    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockbreak(BlockBreakEvent event){
        Player player = event.getPlayer();
        if (mining.contains(player.getUniqueId())) return;
        if(player.isSneaking()){
            return;
        }


        ItemStack tool = player.getInventory().getItemInMainHand();
        if (tool.getEnchantmentLevel(VEINMINE) == 0) return;   // 没这把附魔就不管

        Material block = event.getBlock().getType();

        if (!(tool.getItemMeta() instanceof Damageable)) {
            return;
        }

        int limit = tool.getEnchantmentLevel(VEINMINE) * 8;    // 等级越高挖越多

        mining.add(player.getUniqueId());
        try {
            breakVein(event.getBlock(), block, limit, player);
        } finally {
            mining.remove(player.getUniqueId());
        }
    }


    static void breakVein(Block start, Material ore, int limit, Player player) {
        // BFS 洪水填充:从起点扩散,只处理同类方块
        Deque<Block> queue = new ArrayDeque<>();
        Set<Block> visited = new HashSet<>();
        queue.add(start);
        visited.add(start);
        World world = start.getWorld();
        int minHeight = world.getMinHeight();
        int maxHeight = world.getMaxHeight();
        int broken = 0;
        while (!queue.isEmpty() && broken < limit) {
            Block b = queue.poll();
            if (!world.isChunkLoaded(b.getX() >> 4, b.getZ() >> 4) || b.getType() != ore ||
                    !DomainProtection.canBreak(player, b)){
                continue;
            }
            // 起点由原始事件处理；其他方块走完整玩家挖掘流程。
            if (!b.equals(start) && !player.breakBlock(b)) continue;
            broken++;

            ItemStack currentTool = player.getInventory().getItemInMainHand();
            if (broken >= limit || currentTool.getAmount() <= 0 || currentTool.getType() == Material.AIR) {
                break;
            }

            int x = b.getX(), y = b.getY(), z = b.getZ();
            // 26 邻接：入队前去重，不为每个方块分配邻居集合；只扩展成功破坏的方块。
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    int ny = y + dy;
                    if (ny < minHeight || ny >= maxHeight) continue;
                    for (int dz = -1; dz <= 1; dz++) {
                        if (dx == 0 && dy == 0 && dz == 0) continue;
                        int nx = x + dx, nz = z + dz;
                        if (!world.isChunkLoaded(nx >> 4, nz >> 4)) continue;
                        Block neighbor = world.getBlockAt(nx, ny, nz);
                        if (visited.add(neighbor) && neighbor.getType() == ore) queue.add(neighbor);
                    }
                }
            }
        }
    }


}
