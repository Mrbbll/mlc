package com.mlc.mlcwaystone.commands;

import com.mlc.mlcwaystone.Mlcwaystone;
import com.mlc.mlcwaystone.WaystoneGUI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Reload implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, String[] args) {
        if (sender instanceof Player player && !player.isOp()) {
            player.sendMessage(Component.text("你没有权限执行此命令")
                    .color(TextColor.fromHexString("#FF5555")));
            return true;
        }

        // Reload data from file
        Mlcwaystone.loadData();

        // Clear all open GUI tracking so players get fresh data on next open
        WaystoneGUI.openInvs.clear();
        WaystoneGUI.playerPageMap.clear();
        WaystoneGUI.playerWaystoneOrder.clear();

        sender.sendMessage(Component.text("传送石碑数据已重新加载，当前传送点数量: "
                        + Mlcwaystone.waystoneDataMap.size())
                .color(TextColor.fromHexString("#00ff33")));
        return true;
    }
}
