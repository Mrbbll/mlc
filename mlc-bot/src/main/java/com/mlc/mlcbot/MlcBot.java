package com.mlc.mlcbot;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;

public final class MlcBot {
    private static BotManager manager;
    private MlcBot() { }

    public static void init(JavaPlugin plugin) {
        shutdown();
        if (!plugin.getServer().getMinecraftVersion().equals("26.3")) {
            plugin.getLogger().warning("mlc-bot 的内部假人实现目前仅适配 Paper 26.3，模块未启用。");
            return;
        }
        manager = new BotManager(plugin);
        BotCommand command = new BotCommand(plugin, manager);
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS,
                event -> event.registrar().register("mlcbot", "生成 PvP 练习机器人", command));
        plugin.getLogger().info("mlc-bot loaded (embedded NPC, fixed kits)");
    }

    public static void shutdown() {
        if (manager != null) { manager.close(); manager = null; }
    }
}
