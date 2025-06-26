package com.mlc.mlc.commands;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import static com.mlc.mlc.Mlc.instance;


public class sit implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        Player player = (Player) commandSender;
        Location location = player.getLocation();
        ItemDisplay itemDisplay = (ItemDisplay) location.getWorld().spawnEntity(location, EntityType.ITEM_DISPLAY);
        itemDisplay.addPassenger(player);
        PersistentDataContainer persistentDataContainer = itemDisplay.getPersistentDataContainer();
        persistentDataContainer.set(new NamespacedKey(instance,"sit"), PersistentDataType.STRING, "sit");

        return false;
    }
}
