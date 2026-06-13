package com.mlc.mlc.mlcmain.ess.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


import java.util.UUID;

import static com.mlc.mlc.Mlc.Tpaheremap;
import static com.mlc.mlc.Mlc.Tpamap;

public class tpaccept implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        Player player = (Player) commandSender;
        UUID playerUUID = player.getUniqueId();
        if (!Tpamap.containsKey(playerUUID)&&!Tpaheremap.containsKey(playerUUID)) {
            player.sendMessage(Component.text("你没有待处理的传送请求！", TextColor.fromHexString("#f73636")));
            return true;
        }

        if(Tpamap.containsKey(playerUUID)){
            UUID requesterUUID = Tpamap.get(playerUUID);
            Player requester = Bukkit.getPlayer(requesterUUID);

            if (requester == null || !requester.isOnline()) {
                player.sendMessage(Component.text("请求者已离线！", TextColor.fromHexString("#f73636")));
                Tpamap.remove(playerUUID);
                return true;
            }

            requester.teleport(player.getLocation());
            requester.sendMessage(Component.text("你的传送请求已被接受！", TextColor.fromHexString("#3cff2e")));
            player.sendMessage(Component.text("已接受 " + requester.getName() + " 的传送请求！", TextColor.fromHexString("#3cff2e")));

            Tpamap.remove(playerUUID);
            return true;
        };
        if(Tpaheremap.containsKey(playerUUID)){
            UUID requesterUUID = Tpaheremap.get(playerUUID);
            Player requester = Bukkit.getPlayer(requesterUUID);

            if (requester == null || !requester.isOnline()) {
                player.sendMessage(Component.text("请求者已离线！", TextColor.fromHexString("#f73636")));
                Tpaheremap.remove(playerUUID);
                return true;
            }

            player.teleport(requester.getLocation());
            requester.sendMessage(Component.text("你的传送请求已被接受！", TextColor.fromHexString("#3cff2e")));
            player.sendMessage(Component.text("已接受 " + requester.getName() + " 的传送请求！", TextColor.fromHexString("#3cff2e")));

            Tpaheremap.remove(playerUUID);
            return true;
        }

        return false;
    }
}
