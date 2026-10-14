package com.mlc.mlcwaystone.commands;

import com.mlc.mlcwaystone.Mlcwaystone;
import com.mlc.mlcwaystone.WaystoneGUI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;

public class Reload implements BasicCommand {
    @Override
    public void execute(CommandSourceStack source, String[] args) {
        CommandSender sender = source.getSender();
        if (sender instanceof Player player && !player.isOp()) {
            player.sendMessage(Component.text("你没有权限执行此命令")
                    .color(TextColor.fromHexString("#FF5555")));
            return;
        }

        // Close tracked GUIs while their click handlers are still active.
        Map<Player, Inventory> openInventories = new HashMap<>(WaystoneGUI.playerCurrentInv);
        for (Map.Entry<Player, Inventory> entry : openInventories.entrySet()) {
            Player player = entry.getKey();
            if (player.isOnline()
                    && entry.getValue().equals(player.getOpenInventory().getTopInventory())) {
                player.closeInventory();
            }
        }

        // Reload data from file
        Mlcwaystone.loadData();
        Mlcwaystone.syncBlueMapMarkers();

        // Clear all open GUI tracking so players get fresh data on next open
        WaystoneGUI.openInvs.clear();
        WaystoneGUI.playerPageMap.clear();
        WaystoneGUI.playerWaystoneOrder.clear();
        WaystoneGUI.playerCurrentInv.clear();

        sender.sendMessage(Component.text("传送石碑数据已重新加载，当前传送点数量: "
                        + Mlcwaystone.waystoneDataMap.size())
                .color(TextColor.fromHexString("#00ff33")));
    }

    @Override
    public String permission() {
        return "mlcwaystone.reload";
    }
}
