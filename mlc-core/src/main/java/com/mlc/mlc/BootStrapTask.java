package com.mlc.mlc;

import com.mlc.mlc.mlcmain.enchantments.moreenchants.chainbreak;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.event.RegistryEvent;
import io.papermc.paper.registry.event.RegistryEvents;
import io.papermc.paper.registry.keys.ItemTypeKeys;
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import io.papermc.paper.registry.set.RegistrySet;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Registry;
import io.papermc.paper.registry.tag.Tag;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemType;


public class BootStrapTask {
    public static void run(BootstrapContext context){
        context.getLogger().info("\n INJECTING ENCHANTMENTS \n");
        context.getLifecycleManager().registerEventHandler(RegistryEvents.ENCHANTMENT.compose().newHandler(
                event -> {
                    Tag<ItemType> pickaxesTag = event.getOrCreateTag(ItemTypeTagKeys.PICKAXES);
                    Tag<ItemType> axesTag = event.getOrCreateTag(ItemTypeTagKeys.AXES);
                    Tag<ItemType> hoestag = event.getOrCreateTag(ItemTypeTagKeys.HOES);
                    Tag<ItemType> shovelsTag = event.getOrCreateTag(ItemTypeTagKeys.SHOVELS);

                    RegistrySet excavatorSet = RegistrySet.keySet(RegistryKey.ITEM,
                            ItemTypeKeys.WOODEN_PICKAXE, ItemTypeKeys.STONE_PICKAXE, ItemTypeKeys.IRON_PICKAXE, ItemTypeKeys.GOLDEN_PICKAXE, ItemTypeKeys.DIAMOND_PICKAXE, ItemTypeKeys.NETHERITE_PICKAXE,
                            ItemTypeKeys.WOODEN_SHOVEL, ItemTypeKeys.STONE_SHOVEL, ItemTypeKeys.IRON_SHOVEL, ItemTypeKeys.GOLDEN_SHOVEL, ItemTypeKeys.DIAMOND_SHOVEL, ItemTypeKeys.NETHERITE_SHOVEL
                    );

                    event.registry().register(
                            chainbreak.VEINMINE_KEY,
                            b -> b.description(Component.text("Veinmine").color(NamedTextColor.GRAY))
                                    .supportedItems(pickaxesTag)
                                    .weight(2)
                                    .maxLevel(5)
                                    .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(15, 9))
                                    .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(65, 9))
                                    .anvilCost(4)
                                    .activeSlots(EquipmentSlotGroup.MAINHAND)
                    );


                }
        ));
    }

}
