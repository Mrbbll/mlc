package com.mlc.mlc.commands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/** Adapts existing command handlers to Paper's command lifecycle. */
public final class ExecutorCommand extends Command implements BasicCommand, PluginIdentifiableCommand {
    private final JavaPlugin plugin;
    private final CommandExecutor executor;

    private ExecutorCommand(JavaPlugin plugin, String name, CommandExecutor executor, ConfigurationSection metadata) {
        super(name, metadata.getString("description", ""), metadata.getString("usage", ""), metadata.getStringList("aliases"));
        this.plugin = plugin;
        this.executor = executor;
        setPermission(metadata.getString("permission"));
    }

    public static void register(JavaPlugin plugin, Map<String, CommandExecutor> handlers) throws IOException {
        YamlConfiguration metadata;
        try (InputStreamReader reader = new InputStreamReader(
                Objects.requireNonNull(plugin.getResource("plugin.yml"), "Missing command metadata"), StandardCharsets.UTF_8)) {
            metadata = YamlConfiguration.loadConfiguration(reader);
        }
        List<ExecutorCommand> commands = handlers.entrySet().stream().map(entry -> new ExecutorCommand(
                plugin, entry.getKey(), entry.getValue(), Objects.requireNonNull(
                        metadata.getConfigurationSection("commands." + entry.getKey()), "Missing command " + entry.getKey()))).toList();
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            for (ExecutorCommand command : commands) {
                event.registrar().register(command.getName(), command.getDescription(), command.getAliases(), command);
            }
        });
    }

    @Override
    public JavaPlugin getPlugin() {
        return plugin;
    }

    @Override
    public String permission() {
        return getPermission();
    }

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        execute(source.getSender(), getName(), args);
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        boolean handled = executor.onCommand(sender, this, label, args);
        if (!handled && !getUsage().isBlank()) {
            sender.sendMessage(getUsage().replace("<command>", label));
        }
        return handled;
    }

    @Override
    public Collection<String> suggest(CommandSourceStack source, String[] args) {
        if (executor instanceof TabCompleter completer) {
            List<String> completions = completer.onTabComplete(source.getSender(), this, getName(), args);
            if (completions != null) return completions;
        }
        return super.tabComplete(source.getSender(), getName(), args);
    }
}
