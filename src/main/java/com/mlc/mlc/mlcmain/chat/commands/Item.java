package com.mlc.mlc.mlcmain.chat.commands;

import me.clip.placeholderapi.libs.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Item implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (sender instanceof Player player){
            ItemStack itemStack = player.getInventory().getItemInMainHand();

            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta==null){
                return false;
            }


            Component component = Component.text(player.getName() + "展示了手上的东西：", TextColor.color(42, 255, 195)).append(itemStack.effectiveName()).hoverEvent(itemStack.asHoverEvent());

            player.sendMessage(component);

        }else {
            return false;
        }
        return false;
    }
}
