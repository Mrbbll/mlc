package com.mlc.mlc.mlcmain.mlcitem.command;

import com.mlc.mlc.mlcmain.mlcitem.itemgui.Gui;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


public class mlcitemgui implements CommandExecutor{


    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        Player player = (Player) commandSender;
        Gui.open(player);

        return true;
    }


}
