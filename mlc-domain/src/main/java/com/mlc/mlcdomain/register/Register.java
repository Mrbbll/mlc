package com.mlc.mlcdomain.register;

import com.mlc.mlcdomain.listener.Environment;
import com.mlc.mlcdomain.listener.Others;
import com.mlc.mlcdomain.listener.Playerlistener;
import org.bukkit.Bukkit;

import static com.mlc.mlcdomain.Mlcdomain.instance;

public class Register {
    public static void register() {
        Bukkit.getPluginManager().registerEvents(new Environment(), instance);
        Bukkit.getPluginManager().registerEvents(new Playerlistener(), instance);
        Bukkit.getPluginManager().registerEvents(new Others(), instance);
    }
}
