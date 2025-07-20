package com.mlc.mlc.Listener;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockType;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.RespawnAnchor;
import org.bukkit.enchantments.Enchantment;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import static com.mlc.mlc.Mlc.instance;

public class Enchantlistener implements Listener {
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
            player.sendMessage("ok");

            ItemStack tool = player.getInventory().getItem(1);
            ItemStack book1 = player.getInventory().getItem(2);
            ItemStack book2 = player.getInventory().getItem(3);
            if(book1!=null && book2!=null && tool!=null){
                if(book1.equals(book2) && book1.getType() == Material.ENCHANTED_BOOK){
                    EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta) book1.getItemMeta();
                    Map<Enchantment, Integer> bookenchants = enchantmentStorageMeta.getStoredEnchants();
                    ItemStack toolfinal = mergeEnchants(tool,bookenchants,player);
//                    Objects.requireNonNull(player.getInventory().getItem(0)).setAmount(0);
//                    Objects.requireNonNull(player.getInventory().getItem(1)).setAmount(0);
//                    Objects.requireNonNull(player.getInventory().getItem(2)).setAmount(0);
//                    Objects.requireNonNull(player.getInventory().getItem(3)).setAmount(0);
                    Location location = center.getLocation().clone().add(0.5,1,0.5);
                    World world = location.getWorld();

                    //粒子效果
                    final int[] count = {0};
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
                            count[0] += 1;
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


                    //掉落物品
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

    public static ItemStack mergeEnchants(ItemStack item, Map<Enchantment, Integer> bookenchants,Player player) {
        if (bookenchants.isEmpty()) {
            player.sendMessage("b");
            return item;
        }
        // 创建合并后的附魔集合
        Map<Enchantment, Integer> mergedEnchants = new HashMap<>(item.getEnchantments());

        for (Map.Entry<Enchantment, Integer> entry : bookenchants.entrySet()) {
            Enchantment enchant = entry.getKey();
            int bookLevel = entry.getValue() + 1;

            // 检查物品是否能接受该附魔
            if (!enchant.canEnchantItem(item)) {
                player.sendMessage(Component.text("魔咒冲突", TextColor.fromHexString("#f73636")));
                return item;
            }

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

        // 应用合并后的附魔到物品
        ItemStack result = item.clone();
        ItemMeta meta = result.getItemMeta();
        player.sendMessage("a");
        // 清除原有附魔
        for (Enchantment enchant : item.getEnchantments().keySet()) {
            meta.removeEnchant(enchant);
            player.sendMessage(enchant.toString());
        }

        // 添加合并后的附魔
        for (Map.Entry<Enchantment, Integer> entry : mergedEnchants.entrySet()) {
            meta.addEnchant(entry.getKey(), entry.getValue(), true);
            player.sendMessage(entry.toString());
        }

        result.setItemMeta(meta);
        return result;
    }

    public static boolean canApplyEnchants(ItemStack item, Map<Enchantment, Integer> enchants) {
        for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
            Enchantment enchant = entry.getKey();
            //检查附魔是否适用于该物品类型
            if (!enchant.canEnchantItem(item)) {
                return false;
            }

            //检查与已有附魔的冲突
            for (Enchantment existingEnchant : item.getEnchantments().keySet()) {
                if (existingEnchant.conflictsWith(enchant)) {
                    return false;
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
