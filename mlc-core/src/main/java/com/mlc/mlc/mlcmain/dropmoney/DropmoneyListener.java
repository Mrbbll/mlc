
package com.mlc.mlc.mlcmain.dropmoney;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

import static com.mlc.mlc.mlcmain.items.itemmannager.Mlcitems.money_nugget;

public class DropmoneyListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onDropmoney(BlockBreakEvent event) {

        switch (event.getBlock().getType()) {
            case ANDESITE, DIORITE, GRANITE, TUFF -> {

                ItemStack tool = event.getPlayer()
                        .getInventory()
                        .getItemInMainHand();

                // 精准采集不掉落金币
                if (tool.containsEnchantment(Enchantment.SILK_TOUCH)) {
                    return;
                }

                // 固定12%概率
                if (ThreadLocalRandom.current().nextDouble() >= 0.12) {
                    return;
                }

                // 获取时运等级
                int fortune = tool.getEnchantmentLevel(Enchantment.FORTUNE);

                // 根据时运等级随机决定金币数量
                int amount = 1 + ThreadLocalRandom.current()
                        .nextInt(fortune + 1);

                ItemStack drop = money_nugget.clone();
                drop.setAmount(amount);

                event.getBlock().getWorld().dropItemNaturally(
                        event.getBlock().getLocation(),
                        drop
                );

                // 取消原版方块掉落
                event.setDropItems(false);
            }
            default -> {
            }
        }
    }
}
