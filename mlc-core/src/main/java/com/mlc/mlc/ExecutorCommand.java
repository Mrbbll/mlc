package com.mlc.mlc;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.List;

/** Adapts existing Bukkit executors to Paper's command lifecycle. */
final class ExecutorCommand extends Command implements BasicCommand {
    private final CommandExecutor executor;

    ExecutorCommand(String name, String description, CommandExecutor executor) {
        this(name, description, "", null, executor);
    }

    ExecutorCommand(String name, String description, String usage, String permission, CommandExecutor executor) {
        super(name, description, usage, List.of());
        setPermission(permission);
        this.executor = executor;
    }

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        execute(source.getSender(), getName(), args);
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) {
            return true;
        }
        boolean handled = executor.onCommand(sender, this, label, args);
        if (!handled && !getUsage().isEmpty()) {
            for (String line : getUsage().replace("<command>", label).split("\n")) {
                sender.sendMessage(line);
            }
        }
        return handled;
    }

    @Override
    public List<String> suggest(CommandSourceStack source, String[] args) {
        return tabComplete(source.getSender(), getName(), args);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        if (executor instanceof TabCompleter completer) {
            List<String> suggestions = completer.onTabComplete(sender, this, alias, args);
            if (suggestions != null) {
                return suggestions;
            }
        }
        return super.tabComplete(sender, alias, args);
    }

    @Override
    public String permission() {
        return getPermission();
    }
}
