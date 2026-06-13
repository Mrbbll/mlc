package com.mlc.mlcdomain.hocks;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

import static com.mlc.mlcdomain.Mlcdomain.instance;

public class Vaultapi {
    private static Economy econ = null;
    // 初始化Vault插件提供的经济系统
    public static boolean setupEconomy() {
        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        instance.getLogger().info("Vault 插件找到了");
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        instance.getLogger().info("\n\nmlc插件提供的经济系统初始化成功\n\n");
        return true;
    }
    // 获取Vault插件提供的经济系统实例
    public static Economy getEconomy() {
        return econ;
    }
    // 返回货币的复数形式名称
    public static String currencyNamePlural() {
        return econ.currencyNamePlural();
    }
    // 返回货币的单数形式名称
    public static String currencyNameSingular() {
        return econ.currencyNameSingular();
    }
    // 从玩家账户中提取指定金额
    public static void withdrawPlayer(OfflinePlayer player, double amount) {
        econ.withdrawPlayer(player, amount);
    }
    // 向玩家账户存款指定金额
    public static void depositPlayer(OfflinePlayer player, double amount) {
        econ.depositPlayer(player, amount);
    }
    // 获取玩家账户余额
    public static double getBalance(OfflinePlayer player) {
        return econ.getBalance(player);
    }
}

