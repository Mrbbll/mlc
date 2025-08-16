package com.mlc.mlc.listener;

import org.bukkit.Material;
import org.bukkit.entity.EntitySnapshot;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SpawnEggMeta;
import org.jetbrains.annotations.NotNull;

public class villagerbirth implements Listener {
    @EventHandler
    public void onbirth(EntitySpawnEvent event){
        if (event.getEntityType() == EntityType.VILLAGER) {
            // 将实体转换为Villager对象
            Villager villager = (Villager) event.getEntity();

            //villager.remove();
//            ItemStack itemStack = new ItemStack(Material.VILLAGER_SPAWN_EGG);
//            ItemMeta itemMeta = itemStack.getItemMeta();
//            SpawnEggMeta spawnEggMeta = (SpawnEggMeta) itemStack.getItemMeta();



            int level = villager.getVillagerLevel();
        }
    }
}
