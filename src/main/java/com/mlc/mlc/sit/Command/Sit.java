package com.mlc.mlc.sit.Command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import static com.mlc.mlc.Mlc.instance;


public class Sit implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        Player player = (Player) commandSender;
        if(player.isInsideVehicle()){
            return false;
        }
        Location location = player.getLocation();
        Block block = location.clone().add(0,-1,0).getBlock();
        if(!block.getType().isSolid()){
            player.sendMessage(Component.text("这里不能坐下", TextColor.color(0xFF4213)));
            return false;
        }
        ItemDisplay itemDisplay = (ItemDisplay) location.getWorld().spawnEntity(location, EntityType.ITEM_DISPLAY);
        itemDisplay.addPassenger(player);
        PersistentDataContainer persistentDataContainer = itemDisplay.getPersistentDataContainer();
        persistentDataContainer.set(new NamespacedKey(instance,"sit"), PersistentDataType.STRING, "sit");

        return false;
    }
}
