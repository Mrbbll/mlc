package com.mlc.mlc.mlcmain.reload;

import com.mlc.mlc.mlcmain.items.itemmannager.Cratesitems;
import com.mlc.mlc.mlcmain.items.itemmannager.Fesitems;
import com.mlc.mlc.mlcmain.items.itemmannager.Mlcitems;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
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
