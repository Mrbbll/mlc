package com.mlc.mlc.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executor;

import static com.mlc.mlc.Mlc.*;

public class tpahere implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        Player player = (Player) commandSender;
        if (strings.length < 1 || strings.length > 2) {
            player.sendMessage(Component.text("用法: /tpahere <玩家名>",TextColor.fromHexString("#f73636")));
            return true;
        }
        Player target = Bukkit.getPlayer(strings[0]);
        if (target == null || !target.isOnline()) {
            player.sendMessage(Component.text("目标玩家不在线！",TextColor.fromHexString("#f73636")));
            return true;
        }
        if (player.getUniqueId().equals(target.getUniqueId())) {
            player.sendMessage(Component.text("你不能向自己发送传送请求！",TextColor.fromHexString("#f73636")));
            return true;
        }

        Tpaheremap.put(target.getUniqueId(), player.getUniqueId());

        player.sendMessage(Component.text("已向 " + target.getName() + " 发送传送请求！",TextColor.fromHexString("#e5fe67")));
        target.sendMessage(Component.text(player.getName() + " 想要你传送到ta那里。",TextColor.fromHexString("#e5fe67")));
        target.sendMessage(Component.text("使用 /tpaccept 接受，3分钟后自动拒绝",TextColor.fromHexString("#e5fe67")));
        target.sendMessage(Component.text("或者点我同意", TextColor.fromHexString("#e5fe67"))
                        .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND,"/tpaccept"))
                        .append(Component.text("[✓]", TextColor.fromHexString("#3cff2e"), TextDecoration.BOLD)
                                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND,"/tpaccept")))
                );
//下面是移除
        Bukkit.getScheduler().runTaskLater(instance, () -> {
            if (Tpaheremap.containsKey(target.getUniqueId()) &&
                    Tpaheremap.get(target.getUniqueId()).equals(player.getUniqueId())) {
                Tpaheremap.remove(target.getUniqueId());
                player.sendMessage(Component.text("你的传送请求已过期！",TextColor.fromHexString("#f73636")));
                target.sendMessage(Component.text(player.getName() + " 的传送请求已过期！",TextColor.fromHexString("#f73636")));
            }
        },180 * 20L);
        return false;
    }
}
