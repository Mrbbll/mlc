package com.mlc.mlc.mlcmain.mail.command;

import com.mlc.mlc.mlcmain.mail.mailgui.Mailgui;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class mymail implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        Mailgui.open((Player)commandSender);
        return false;
    }
}
