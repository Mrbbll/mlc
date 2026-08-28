package com.mlc.mlcstyte;

import net.milkbowl.vault.chat.Chat;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * mlc-styte — Chat formatting module ported from VaultChatFormatter.
 *
 * <p>Provides Vault-based chat formatting with placeholders:
 * {prefix}, {suffix}, {name}, {displayname}, {message}.
 * Supports &#rrggbb hex colors.</p>
 *
 * <p>This is a library module bundled into mlc-core — it is NOT a standalone plugin.
 * Initialized via {@link #init(JavaPlugin)} from {@code Mlc.onEnable()}.</p>
 */
public final class MlcStyte {
    public static JavaPlugin instance;
    public static Chat vaultChat = null;

    public static final String FORMAT = "<{prefix}{name}{suffix}> {message}";

    private MlcStyte() {
        // 防止实例化
    }

    /**
     * Initialize mlc-styte system. Called from Mlc.onEnable().
     *
     * @param plugin the MLC plugin instance
     */
    public static void init(JavaPlugin plugin) {
        instance = plugin;
        instance.getLogger().info("mlc-styte 模块加载中...");

        refreshVault();

        // Register chat listener
        Bukkit.getPluginManager().registerEvents(new StyteChatListener(), instance);

        instance.getLogger().info("mlc-styte 模块加载成功");
    }

    /**
     * Check for a new Vault Chat implementation and update the reference.
     */
    public static void refreshVault() {
        Chat newChat = Bukkit.getServer().getServicesManager().load(Chat.class);
        if (newChat != vaultChat) {
            instance.getLogger().info(
                    "检测到 Vault Chat 实现变更: "
                            + (vaultChat == null ? "null" : vaultChat.getName())
                            + " -> "
                            + (newChat == null ? "null" : newChat.getName())
            );
        }
        vaultChat = newChat;
    }
}
