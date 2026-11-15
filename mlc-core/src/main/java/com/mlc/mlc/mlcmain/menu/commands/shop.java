package com.mlc.mlc.mlcmain.menu.commands;

import com.mlc.mlc.mlcmain.menu.buildershop;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class shop implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            buildershop.open(player);
        } else {
            sender.sendMessage("该命令只能由玩家执行");
        }
        return true;
    }
}
