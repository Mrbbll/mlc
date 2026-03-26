package com.mlc.mlc.mlcmain.crates.commands;

import com.mlc.mlc.mlcmain.crates.Crates;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class choujiang implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if(sender instanceof Player player){
            Crates.getitems(player);
        }
        return false;
    }

}
