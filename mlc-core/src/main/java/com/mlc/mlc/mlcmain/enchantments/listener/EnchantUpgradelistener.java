package com.mlc.mlc.mlcmain.enchantments.listener;

import com.mlc.mlc.mlcmain.enchantments.EnchantmentMaxLevel;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.RespawnAnchor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.mlc.mlc.Mlc.instance;

public class EnchantUpgradelistener implements Listener {
    @EventHandler
    public void oncilck(PlayerInteractEvent event){
        Player player = event.getPlayer();
        Block center = event.getClickedBlock();
        if(event.getAction() == Action.PHYSICAL) {
            return;
        }

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

            ItemStack tool = player.getInventory().getItem(1);
            ItemStack book1 = player.getInventory().getItem(2);
            ItemStack book2 = player.getInventory().getItem(3);
            if(book1!=null && book2!=null && tool!=null){
                if(book1.equals(book2) && book1.getType() == Material.ENCHANTED_BOOK){
                    //判断魔咒是否冲突或者非法
                    EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta) book1.getItemMeta();
                    Map<Enchantment, Integer> bookenchants = enchantmentStorageMeta.getStoredEnchants();
                    if(!canApplyEnchants(tool,bookenchants,player)){
                        player.sendMessage(Component.text("魔咒冲突、非法或者附魔书为空、等级太低", TextColor.fromHexString("#f73636")));
                        return;
                    }
                    //合并魔咒并检查附魔上限
                    Map<Enchantment, Integer> mergedEnchants = getmergedEnchantmentMap(tool, bookenchants);
                    if(checkHasInvalidEnchants(mergedEnchants,player)){
                        return;
                    }

                    ItemStack toolfinal = mergeEnchants(tool.clone(),mergedEnchants);

                    //粒子效果
                    Location location = center.getLocation().clone().add(0.5,1,0.5);
                    World world = location.getWorld();
                    enchantparticle(location,world,player,tool.clone());

                    //清除物品
                    Objects.requireNonNull(player.getInventory().getItem(0)).setAmount(0);
                    Objects.requireNonNull(player.getInventory().getItem(1)).setAmount(0);
                    Objects.requireNonNull(player.getInventory().getItem(2)).setAmount(0);
                    Objects.requireNonNull(player.getInventory().getItem(3)).setAmount(0);
                    //掉落物品和重生锚复原
                    Bukkit.getScheduler().runTaskLater(instance,()->{
                        world.dropItem(location,toolfinal);

                        Block block1 = location.clone().add(0,-2,2).getBlock();
                        block1.setType(Material.RESPAWN_ANCHOR);
                        Block block2 = location.clone().add(0,-2,-2).getBlock();
                        block2.setType(Material.RESPAWN_ANCHOR);
                        Block block3 = location.clone().add(2,-2,0).getBlock();
                        block3.setType(Material.RESPAWN_ANCHOR);
                        Block block4 = location.clone().add(-2,-2,0).getBlock();
                        block4.setType(Material.RESPAWN_ANCHOR);
                    },60L);

                    return;
                }
                player.sendMessage(Component.text("两本附魔书未匹配", TextColor.fromHexString("#f73636")));
            }
        }

    }

    private boolean checkHasInvalidEnchants(Map<Enchantment, Integer> mergedEnchants,Player player) {

        for (Map.Entry<Enchantment, Integer> entry : mergedEnchants.entrySet()) {
            Enchantment enchantment = entry.getKey();
            int level = entry.getValue();
            if (enchantment.getMaxLevel() < level) {
                player.sendMessage(Component.text("附魔等级超过最大等级", TextColor.fromHexString("#f73636")));
                return true;
            }
        }
        return false;
    }

    private void enchantparticle(Location location, World world, Player player,ItemStack tool) {
        Item item1 = world.dropItem(location,ItemStack.of(Material.NETHER_STAR));
        item1.setVelocity(new Vector(0,0,0));
        item1.setCanPlayerPickup(false);
        item1.setTicksLived(6000 - 60);
        Item item2 = world.dropItem(location,ItemStack.of(Material.ENCHANTED_BOOK));
        item2.setVelocity(new Vector(0,0,0));
        item2.setCanPlayerPickup(false);
        item2.setTicksLived(6000 - 60);
        Item item3 = world.dropItem(location,ItemStack.of(Material.ENCHANTED_BOOK));
        item3.setVelocity(new Vector(0,0,0));
        item3.setCanPlayerPickup(false);
        item3.setTicksLived(6000 - 60);
        Item item4 = world.dropItem(location,tool);
        item4.setVelocity(new Vector(0,0,0));
        item4.setCanPlayerPickup(false);
        item4.setTicksLived(6000 - 60);

        final int[] count = {0,0};
        new BukkitRunnable() {
            @Override
            public void run() {
                count[0] += 1;
                world.spawnParticle(Particle.SCULK_CHARGE,location,50,
                        0,0,0,
                        0.5,
                        1.0F);

                if (count[0] >= 12) {
                    this.cancel();
                }
            }
        }.runTaskTimer(instance, 0L, 5L);

        new BukkitRunnable() {
            @Override
            public void run() {
                count[1] += 1;
                world.spawnParticle(Particle.SHRIEK,location.clone().add(0,-1,0),1,
                        0,0,0,
                        0.5,
                        4);
                world.spawnParticle(Particle.REVERSE_PORTAL,location.clone().add(0,-1,2),50,
                        0, 0, 0
                );
                world.spawnParticle(Particle.REVERSE_PORTAL,location.clone().add(0,-1,-2),50,
                        0, 0, 0
                );
                world.spawnParticle(Particle.REVERSE_PORTAL,location.clone().add(2,-1,0),50,
                        0, 0, 0
                );
                world.spawnParticle(Particle.REVERSE_PORTAL,location.clone().add(-2,-1,0),50,
                        0, 0, 0,
                        0.1
                );
                player.playSound(location,Sound.BLOCK_SCULK_SHRIEKER_SHRIEK,1,1);
                if (count[0] >= 3) {
                    this.cancel();
                }
            }
        }.runTaskTimer(instance, 0L, 20L);


    }

    public static ItemStack mergeEnchants(ItemStack item,Map<Enchantment, Integer> mergedEnchants) {

        // 应用合并后的附魔到物品
        ItemStack result = item.clone();
        ItemMeta meta = result.getItemMeta();
        // 清除原有附魔
        for (Enchantment enchant : item.getEnchantments().keySet()) {
            meta.removeEnchant(enchant);
        }
        // 添加合并后的附魔
        for (Map.Entry<Enchantment, Integer> entry : mergedEnchants.entrySet()) {
            meta.addEnchant(entry.getKey(), entry.getValue(), true);
        }
        result.setItemMeta(meta);
        return result;
    }

    private static @NotNull Map<Enchantment, Integer> getmergedEnchantmentMap(ItemStack item, Map<Enchantment, Integer> bookenchants) {
        // 创建合并后的附魔集合
        Map<Enchantment, Integer> mergedEnchants = new HashMap<>(item.getEnchantments());
        for (Map.Entry<Enchantment, Integer> entry : bookenchants.entrySet()) {
            Enchantment enchant = entry.getKey();
            int bookLevel = entry.getValue() + 1;
            // 如果物品已有该附魔，比较等级
            if (mergedEnchants.containsKey(enchant)) {
                int existingLevel = mergedEnchants.get(enchant);
                // 如果附魔书等级更高，覆盖原有附魔
                if (bookLevel > existingLevel) {
                    mergedEnchants.put(enchant, bookLevel);
                }
                // 如果等级相同则提升一级
                else if (bookLevel == existingLevel) {
                    mergedEnchants.put(enchant, bookLevel + 1);
                }
            }
            // 物品没有该附魔，直接添加
            else {
                mergedEnchants.put(enchant, bookLevel);
            }
        }
        return mergedEnchants;
    }

    public static boolean canApplyEnchants(ItemStack item, Map<Enchantment, Integer> enchants,Player player) {
        //检查附魔书是否为空
        if (enchants.isEmpty()) {
            player.sendMessage("附魔书附魔为空");
            return false;
        }
        for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
            Enchantment enchant = entry.getKey();
            //检查是否有非法魔咒
            if(!EnchantmentMaxLevel.maxLevelMap.containsKey(enchant)){
                player.sendMessage("该附魔未解锁该功能");
                return false;
            }
            //检查附魔是否适用于该物品类型
            if (!enchant.canEnchantItem(item)) {
                player.sendMessage("不能用于这个物品");
                return false;
            }
            //检查与已有附魔的冲突与等级
            for (Enchantment existingEnchant : item.getEnchantments().keySet()) {
                if (existingEnchant.conflictsWith(enchant) && existingEnchant!=enchant) {
                    player.sendMessage("conflict");
                    return false;
                } ;
                if(enchants.containsKey(existingEnchant)){
                    int existingLevel = item.getEnchantmentLevel(existingEnchant);
                    int booklevel = enchants.get(existingEnchant);
                    if(existingLevel > booklevel+1){
                        player.sendMessage("book level is lower than item");
                        return false;
                    }

                }
            }
        }
        return true;
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
