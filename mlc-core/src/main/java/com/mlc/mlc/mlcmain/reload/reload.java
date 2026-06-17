package com.mlc.mlc.mlcmain.reload;

import com.mlc.mlc.mlcmain.items.itemmannager.Cratesitems;
import com.mlc.mlc.mlcmain.items.itemmannager.Fesitems;
import com.mlc.mlc.mlcmain.items.itemmannager.Mlcitems;
import com.mlc.mlc.mlcmain.mlcitem.itemgui.Gui;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static com.mlc.mlc.mlcmain.dialog.Serverjoindialog.gonggao;
import static com.mlc.mlc.mlcmain.dialog.Serverjoindialog.initserverjoindialog;

public class reload implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Cratesitems.init();
        Fesitems.init();
        Mlcitems.init();
        initserverjoindialog();
        // 清除缓存的GUI，使玩家重新打开/mlcitem时能获取到刷新后的物品
        Gui.openinvmap.clear();
        commandSender.sendMessage(Component.text("已刷新"));
        return true;
    }
}
