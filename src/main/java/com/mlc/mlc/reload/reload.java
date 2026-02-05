package com.mlc.mlc.reload;

import com.mlc.mlc.items.itemmannager.Cratesitems;
import com.mlc.mlc.items.itemmannager.Fesitems;
import com.mlc.mlc.items.itemmannager.Mlcitems;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class reload implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Cratesitems.init();
        Fesitems.init();
        Mlcitems.init();

        return true;
    }
}
