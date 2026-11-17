package com.mlc.mlc.mlcmain.items.itemfunc.create.crates.commands;

import com.mlc.mlc.mlcmain.items.itemfunc.create.crates.Crates;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static com.mlc.mlc.Mlc.miniMessage;

public class choujiang implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if(sender instanceof Player player){
            if(player.isOp()){
                Crates.getitems(player);
            }
            else {
                player.sendMessage(miniMessage.deserialize("no premission"));
            }
        }
        return false;
    }

}
