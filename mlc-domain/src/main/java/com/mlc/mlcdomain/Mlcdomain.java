package com.mlc.mlcdomain;

import com.mlc.mlcdomain.dataManager.Databasemanager;
import com.mlc.mlcdomain.hocks.MlcdomainPlaceholderExpansion;
import com.mlc.mlcdomain.hocks.Vaultapi;
import com.mlc.mlcdomain.hocks.bluemap.Bluemapapi;
import com.mlc.mlcdomain.register.Register;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public final class Mlcdomain {
    public static JavaPlugin instance;
    public static MiniMessage miniMessage;

    /**
     * Initialize domain system. Called from Mlc.onEnable().
     */
    public static void init(JavaPlugin plugin) {
        // Set instance to the Mlc plugin
        instance = plugin;

        // Initialize Vault economy (already registered by mlc-core)
        if (!Vaultapi.setupEconomy()) {
            System.out.println("\n\n没找到提供经济系统的插件\n\n");
        }

        System.out.println("\n\nmlcdomain registered\n\n");
        Register.register();
        try {
            new Databasemanager();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        miniMessage = MiniMessage.miniMessage();

        // Register PlaceholderAPI expansion
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new MlcdomainPlaceholderExpansion(instance).register();
            System.out.println("\n\nmlcdomain placeholder registered\n\n");
        }
        Bluemapapi.init();
    }
}
