package com.mlc.mlcbot;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class BotCommand implements BasicCommand {
    private final JavaPlugin plugin;
    private final BotManager manager;

    public BotCommand(JavaPlugin plugin, BotManager manager) { this.plugin = plugin; this.manager = manager; }

    @Override public void execute(CommandSourceStack source, String[] args) {
        CommandSender sender = source.getSender();
        if (!(sender instanceof Player player)) { sender.sendMessage("该指令只能由玩家执行。"); return; }
        if (!sender.hasPermission("mlc.bot")) { sender.sendMessage("你没有使用机器人的权限 (mlc.bot)。"); return; }
        if (args.length == 1 && args[0].equalsIgnoreCase("remove")) {
            sender.sendMessage(manager.remove(player.getUniqueId()) ? "已移除你的机器人。" : "你没有机器人。");
            return;
        }
        if (args.length != 3 || !args[0].equalsIgnoreCase("spawn")) {
            sender.sendMessage("用法: /mlcbot spawn <normal|cpvp|anchor|mace|advanced|dummy> <easy|normal|hard|expert>");
            sender.sendMessage("移除: /mlcbot remove；强度也支持 1–4。");
            return;
        }
        try {
            BotType type = BotType.parse(args[1]);
            BotStrength strength = BotStrength.parse(args[2]);
            manager.spawn(player, type, strength);
            sender.sendMessage("已生成 " + type.name().toLowerCase(Locale.ROOT) + " 机器人，强度 "
                    + strength.name().toLowerCase(Locale.ROOT) + "，使用固定默认 kits。");
        } catch (IllegalArgumentException e) {
            sender.sendMessage(e.getMessage());
        } catch (RuntimeException | LinkageError e) {
            plugin.getLogger().log(Level.SEVERE, "mlc-bot 生成失败", e);
            sender.sendMessage("机器人生成失败，请查看服务器日志；需要 Paper 26.3。");
        }
    }

    @Override public Collection<String> suggest(CommandSourceStack source, String[] args) {
        if (!source.getSender().hasPermission("mlc.bot")) return List.of();
        List<String> options;
        if (args.length <= 1) options = List.of("spawn", "remove");
        else if (!args[0].equalsIgnoreCase("spawn")) return List.of();
        else if (args.length == 2) options = List.of("normal", "cpvp", "anchor", "mace", "advanced", "dummy");
        else if (args.length == 3) options = List.of("easy", "normal", "medium", "hard", "expert", "pro", "1", "2", "3", "4");
        else return List.of();
        String prefix = args.length == 0 ? "" : args[args.length - 1].toLowerCase(Locale.ROOT);
        return options.stream().filter(value -> value.startsWith(prefix)).toList();
    }

    @Override public String permission() { return "mlc.bot"; }
}
