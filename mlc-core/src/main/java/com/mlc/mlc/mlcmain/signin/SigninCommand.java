package com.mlc.mlc.mlcmain.signin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class SigninCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("只有玩家可以签到。");
            return true;
        }
        if (args.length != 0) {
            return false;
        }

        SigninManager.signIn(player, true);
        return true;
    }
}
