
package com.mlc.mlc.mlcmain.dropmoney;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static com.mlc.mlc.mlcmain.items.itemmannager.Mlcitems.money_nugget;

public class DropmoneyListener implements Listener {

    private record BlockPosition(UUID world, int x, int y, int z) {
        static BlockPosition of(Block block) {
            return new BlockPosition(block.getWorld().getUID(), block.getX(), block.getY(), block.getZ());
        }
    }

    private final Set<BlockPosition> placedBlocks = new HashSet<>();

    private static boolean hasSpecialDrops(Material material) {
        return switch (material) {
            case ANDESITE, DIORITE, GRANITE, TUFF, STONE, DEEPSLATE -> true;
            default -> false;
        };
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        Block block = event.getBlockPlaced();
        BlockPosition position = BlockPosition.of(block);
        if (hasSpecialDrops(block.getType())) placedBlocks.add(position);
        else placedBlocks.remove(position);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBreakComplete(BlockBreakEvent event) {
        placedBlocks.remove(BlockPosition.of(event.getBlock()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPistonExtend(BlockPistonExtendEvent event) {
        moveBlocks(event.getBlocks(), event.getDirection());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPistonRetract(BlockPistonRetractEvent event) {
        moveBlocks(event.getBlocks(), event.getDirection());
    }

    private void moveBlocks(List<Block> blocks, BlockFace direction) {
        Set<BlockPosition> moved = new HashSet<>();
        for (Block block : blocks) {
            if (placedBlocks.remove(BlockPosition.of(block))) {
                moved.add(BlockPosition.of(block.getRelative(direction)));
            }
        }
        placedBlocks.addAll(moved);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockExplosion(BlockExplodeEvent event) {
        event.blockList().forEach(block -> placedBlocks.remove(BlockPosition.of(block)));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityExplosion(EntityExplodeEvent event) {
        event.blockList().forEach(block -> placedBlocks.remove(BlockPosition.of(block)));
    }

    // 金币基础掉落概率
    private static final double MONEY_CHANCE = 0.05;

    // 矿物掉落概率表
    private static final List<OreChance> ORE_CHANCES = List.of(
            new OreChance(Material.RAW_COPPER, 0.010),
            new OreChance(Material.RAW_IRON, 0.005),
            new OreChance(Material.REDSTONE, 0.0010),
            new OreChance(Material.LAPIS_LAZULI, 0.0008),
            new OreChance(Material.AMETHYST_SHARD, 0.0006),
            new OreChance(Material.RAW_GOLD, 0.0004),
            new OreChance(Material.DIAMOND, 0.00001)
    );

    private record OreChance(Material material, double chance) {}

    @EventHandler(ignoreCancelled = true)
    public void onDropmoney(BlockBreakEvent event) {

        if (placedBlocks.contains(BlockPosition.of(event.getBlock()))) {
            return;
        }

        ItemStack tool = event.getPlayer()
                .getInventory()
                .getItemInMainHand();

        // 精准采集不触发特殊掉落
        if (tool.containsEnchantment(Enchantment.SILK_TOUCH)) {
            return;
        }

        int fortune = tool.getEnchantmentLevel(
                Enchantment.FORTUNE
        );

        switch (event.getBlock().getType()) {

            // 原有金币掉落
            case ANDESITE, DIORITE, GRANITE, TUFF -> {
                dropMoney(event, fortune);
            }

            // 新增矿物掉落
            case STONE, DEEPSLATE -> {
                dropOre(event, fortune);
            }

            default -> {
            }
        }
    }

    // 金币掉落
    private void dropMoney(BlockBreakEvent event, int fortune) {

        ThreadLocalRandom random = ThreadLocalRandom.current();

        if (random.nextDouble() >= MONEY_CHANCE) {
            return;
        }

        int amount = 1 + random.nextInt(fortune + 1);

        ItemStack drop = money_nugget.clone();
        drop.setAmount(amount);

        event.getBlock().getWorld().dropItemNaturally(
                event.getBlock().getLocation().add(0.5,0.5,0.5),
                drop
        );

        // 金币替代原版方块掉落
        event.setDropItems(false);
    }

    // 矿物掉落
    private void dropOre(BlockBreakEvent event, int fortune) {

        ThreadLocalRandom random = ThreadLocalRandom.current();

        double roll = random.nextDouble();

        for (OreChance ore : ORE_CHANCES) {

            if (roll < ore.chance()) {

                // 时运增加数量，不影响概率
                int amount = 1 + random.nextInt(fortune + 1);

                ItemStack drop = new ItemStack(
                        ore.material(),
                        amount
                );

                event.getBlock().getWorld().dropItemNaturally(
                        event.getBlock().getLocation().add(0.5,0.5,0.5),
                        drop
                );

                return;
            }

            roll -= ore.chance();
        }
    }
}
