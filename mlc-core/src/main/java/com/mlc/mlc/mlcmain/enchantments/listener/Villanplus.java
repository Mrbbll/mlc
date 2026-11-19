
package com.mlc.mlc.mlcmain.enchantments.listener;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Villanplus implements Listener {

    // 抢夺每级增加25%经验
    private static final double LOOTING_EXP_BONUS = 0.25;

    // 抢夺增加经验
    @EventHandler
    public void onMobDeath(EntityDeathEvent event) {

        Player killer = event.getEntity().getKiller();

        if (killer == null) {
            return;
        }

        ItemStack weapon = killer.getInventory()
                .getItemInMainHand();

        int looting = weapon.getEnchantmentLevel(
                Enchantment.LOOTING
        );

        if (looting <= 0) {
            return;
        }

        int exp = event.getDroppedExp();

        int newExp = (int) Math.min(
                Integer.MAX_VALUE,
                Math.round(exp * (1 + looting * LOOTING_EXP_BONUS))
        );

        event.setDroppedExp(newExp);
    }

    // 精准采集瞬间破坏玻璃
    @EventHandler(ignoreCancelled = true)
    public void onGlassBreak(BlockDamageEvent event) {

        Material type = event.getBlock().getType();

        if (!isGlass(type)) {
            return;
        }
        if(event.getPlayer().isSneaking()){
            return;
        }
        ItemStack tool = event.getItemInHand();

        if (!tool.containsEnchantment(Enchantment.SILK_TOUCH)) {
            return;
        }

        event.setInstaBreak(true);
    }

    // 判断玻璃类型
    private boolean isGlass(Material type) {

        if (type == Material.GLASS
                || type == Material.GLASS_PANE
                || type == Material.TINTED_GLASS) {
            return true;
        }

        String name = type.name();

        return name.endsWith("_STAINED_GLASS")
                || name.endsWith("_STAINED_GLASS_PANE");
    }
}
