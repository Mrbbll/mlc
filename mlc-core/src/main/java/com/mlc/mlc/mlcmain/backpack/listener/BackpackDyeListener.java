package com.mlc.mlc.mlcmain.backpack.listener;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.DyedItemColor;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import net.kyori.adventure.text.Component;

import static com.mlc.mlc.Mlc.instance;

public class BackpackDyeListener implements Listener {

    private final NamespacedKey backpackId = new NamespacedKey(instance, "backpack_id");;

    @EventHandler
    public void onDye(PlayerInteractEvent event) {

        Action action = event.getAction();

        if (action != Action.RIGHT_CLICK_AIR &&
                action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        PlayerInventory inv = player.getInventory();

        ItemStack mainHand = inv.getItemInMainHand();
        ItemStack offHand = inv.getItemInOffHand();

        // 检查副手是否为自定义背包
        if (!isBackpack(offHand)) return;

        // 检查主手是否为原版染料
        DyeColor dye = getDyeColor(mainHand.getType());
        if (dye == null) return;

        // 阻止原版右键行为，包括副手放置潜影盒
        event.setUseInteractedBlock(Event.Result.DENY);
        event.setUseItemInHand(Event.Result.DENY);

        // 只在主手事件中执行染色，避免重复触发
        if (event.getHand() != EquipmentSlot.HAND) return;

        // 已经是相同颜色时，不消耗染料
        DyedItemColor current =
                offHand.getData(DataComponentTypes.DYED_COLOR);

        if (current != null &&
                current.color().equals(dye.getColor())) {
            return;
        }

        // 复制背包，保留所有原有数据
        ItemStack result = offHand.clone();

        // 修改染色组件
        result.setData(
                DataComponentTypes.DYED_COLOR,
                DyedItemColor.dyedItemColor(dye.getColor())
        );

        // 更新副手背包
        inv.setItemInOffHand(result);

        // 消耗主手一个染料
        if (mainHand.getAmount() <= 1) {
            inv.setItemInMainHand(new ItemStack(Material.AIR));
        } else {
            ItemStack remaining = mainHand.clone();
            remaining.setAmount(remaining.getAmount() - 1);
            inv.setItemInMainHand(remaining);
        }
    }

    private boolean isBackpack(ItemStack item) {

        if (item == null ||
                item.getType() != Material.SHULKER_BOX) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();

        return meta != null &&
                meta.getPersistentDataContainer().has(
                        backpackId,
                        PersistentDataType.BYTE
                );
    }

    private DyeColor getDyeColor(Material material) {

        String name = material.name();

        if (!name.endsWith("_DYE")) return null;

        String colorName = name.substring(
                0,
                name.length() - 4
        );

        try {
            return DyeColor.valueOf(colorName);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}