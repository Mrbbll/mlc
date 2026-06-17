package com.mlc.mlc.mlcmain.menu.commands;

import com.mlc.mlc.mlcmain.menu.Mlcmenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class menu implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (sender instanceof Player player) {
            Mlcmenu.open(player);
            return true;
        }
        sender.sendMessage("该命令只能由玩家执行");
        return false;
    }
}
