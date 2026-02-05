package com.mlc.mlc.mlcitem.command;

import com.mlc.mlc.mlcitem.itemgui.Gui;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;


import static com.mlc.mlc.mlcitem.itemgui.Gui.openinvmap;


public class mlcitemgui implements CommandExecutor{


    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        Player player = (Player) commandSender;
        Gui.open(player);

        return true;
    }


}
