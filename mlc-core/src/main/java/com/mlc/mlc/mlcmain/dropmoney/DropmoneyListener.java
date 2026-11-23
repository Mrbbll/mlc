
package com.mlc.mlc.mlcmain.dropmoney;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static com.mlc.mlc.mlcmain.items.itemmannager.Mlcitems.money_nugget;

public class DropmoneyListener implements Listener {

    // 金币基础掉落概率
    private static final double MONEY_CHANCE = 0.1;

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
