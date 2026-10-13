package com.mlc.mlc.mlcmain.mail.command;

import com.mlc.mlc.mlcmain.mail.mailgui.Mailgui;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class MailCommand implements CommandExecutor, TabCompleter {
    private final sendmail send = new sendmail();
    private final sendmailtoall sendToAll = new sendmailtoall();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            error(sender, "该命令只能由玩家执行");
            return true;
        }
        if (args.length == 0) {
            Mailgui.open(player);
            return true;
        }
        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "send" -> {
                if (args.length != 2) {
                    error(sender, "用法: /mail send <玩家名>");
                    return true;
                }
                send.onCommand(sender, command, label, Arrays.copyOfRange(args, 1, args.length));
            }
            case "sendtoall" -> {
                if (!sender.hasPermission("mlc.op")) {
                    error(sender, "你没有权限给所有人发送邮件");
                    return true;
                }
                if (args.length != 1) {
                    error(sender, "用法: /mail sendtoall");
                    return true;
                }
                sendToAll.onCommand(sender, command, label, new String[0]);
            }
            default -> error(sender, "用法: /mail、/mail send <玩家名>、/mail sendtoall");
        }
        return true;
    }

    @Override
    public @NotNull List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                              @NotNull String alias, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            return List.of();
        }
        if (args.length == 1) {
            List<String> subcommands = new ArrayList<>(List.of("send"));
            if (sender.hasPermission("mlc.op")) {
                subcommands.add("sendtoall");
            }
            String prefix = args[0].toLowerCase(Locale.ROOT);
            return subcommands.stream().filter(name -> name.startsWith(prefix)).toList();
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("send")) {
            Player player = (Player) sender;
            String prefix = args[1].toLowerCase(Locale.ROOT);
            return Bukkit.getOnlinePlayers().stream()
                    .filter(player::canSee)
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase(Locale.ROOT).startsWith(prefix))
                    .toList();
        }
        return List.of();
    }

    private static void error(CommandSender sender, String message) {
        sender.sendMessage(Component.text(message, TextColor.color(0xFF4213)));
    }
}
