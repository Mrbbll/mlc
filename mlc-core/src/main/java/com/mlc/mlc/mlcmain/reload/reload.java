package com.mlc.mlc.mlcmain.reload;

import com.mlc.mlc.Mlc;
import com.mlc.mlc.mlcmain.items.itemmannager.Cratesitems;
import com.mlc.mlc.mlcmain.items.itemmannager.Fesitems;
import com.mlc.mlc.mlcmain.items.itemmannager.Mlcitems;
import com.mlc.mlc.mlcmain.mlcitem.itemgui.Gui;
import com.mlc.mlc.mlcmain.menu.buildershop;
import com.mlc.mlc.mlcmain.respacksender.packsender;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;


import static com.mlc.mlc.Mlc.instance;
import static com.mlc.mlc.mlcmain.dialog.Serverjoindialog.initserverjoindialog;

public class reload implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        instance.reloadConfig();
        Mlc.fileConfiguration = instance.getConfig();
        Mlc.wordsnum = Mlc.fileConfiguration.getInt("words");


        if (!buildershop.reload()) {
            commandSender.sendMessage(Component.text("商店配置重载失败，保留原有商品，请查看控制台", NamedTextColor.RED));
        }
        Cratesitems.init();
        Fesitems.init();
        Mlcitems.init();
        initserverjoindialog();
        // 清除缓存的GUI，使玩家重新打开/mlcitem时能获取到刷新后的物品
        Gui.openinvmap.clear();
        commandSender.sendMessage(Component.text("配置、物品和公告已刷新，正在更新资源包信息"));
        packsender.upload(commandSender);
        return true;
    }
}
