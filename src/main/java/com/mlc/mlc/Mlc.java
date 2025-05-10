package com.mlc.mlc;


import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.bukkit.event.block.BlockPlaceEvent;

import java.nio.file.Path;
import java.util.Objects;

public final class Mlc extends JavaPlugin {


    public static JavaPlugin instance;
    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("mlc核心插件加载成功");

        instance = this;
        Bukkit.getPluginManager().registerEvents(new guilistener(),this);
        Objects.requireNonNull(Bukkit.getPluginCommand("mlcgui")).setExecutor(new mlcgui());
    }



    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("mlc核心插件卸载成功");
    }
}
