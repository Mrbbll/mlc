package com.mlc.mlc;

import com.mlc.mlc.backpack.listener.Backpacklistener;
import com.mlc.mlc.ess.Listener.Tplistener;
import com.mlc.mlc.ess.command.*;
import com.mlc.mlc.hook.economy.commands.money;
import com.mlc.mlc.listener.*;
import com.mlc.mlc.mail.command.mymail;
import com.mlc.mlc.mail.command.sendmail;
import com.mlc.mlc.mail.command.sendmailtoall;
import com.mlc.mlc.mlcitem.command.mlcgui;
import com.mlc.mlc.sit.Command.Sit;
import com.mlc.mlc.sit.Unsitlistener;
import org.bukkit.Bukkit;

import java.util.Objects;

import static com.mlc.mlc.Mlc.instance;

public class Task {
    public static void task(){
        Bukkit.getPluginManager().registerEvents(new Backpacklistener(),instance);
        Bukkit.getPluginManager().registerEvents(new Heavestlistener(),instance);
        Bukkit.getPluginManager().registerEvents(new Guilistener(), instance);
        Bukkit.getPluginManager().registerEvents(new Unsitlistener(), instance);
        Bukkit.getPluginManager().registerEvents(new Joinlistener(), instance);
        Bukkit.getPluginManager().registerEvents(new Tplistener(), instance);
        Bukkit.getPluginManager().registerEvents(new Deadlistener(), instance);
        Bukkit.getPluginManager().registerEvents(new Eatlistener(), instance);
        Bukkit.getPluginManager().registerEvents(new Enchantlistener(), instance);
        Objects.requireNonNull(Bukkit.getPluginCommand("back")).setExecutor((new back()));
        Objects.requireNonNull(Bukkit.getPluginCommand("sendmail")).setExecutor((new sendmail()));
        Objects.requireNonNull(Bukkit.getPluginCommand("mymail")).setExecutor((new mymail()));
        Objects.requireNonNull(Bukkit.getPluginCommand("mlcgui")).setExecutor(new mlcgui());
        Objects.requireNonNull(Bukkit.getPluginCommand("sendmailtoall")).setExecutor(new sendmailtoall());
        Objects.requireNonNull(Bukkit.getPluginCommand("home")).setExecutor(new home());
        Objects.requireNonNull(Bukkit.getPluginCommand("sethome")).setExecutor(new sethome());
        Objects.requireNonNull(Bukkit.getPluginCommand("delhome")).setExecutor(new delhome());
        Objects.requireNonNull(Bukkit.getPluginCommand("tpa")).setExecutor(new tpa());
        Objects.requireNonNull(Bukkit.getPluginCommand("tpaccept")).setExecutor(new tpaccept());
        Objects.requireNonNull(Bukkit.getPluginCommand("tpahere")).setExecutor(new tpahere());
        Objects.requireNonNull(Bukkit.getPluginCommand("sit")).setExecutor((new Sit()));
        Objects.requireNonNull(Bukkit.getPluginCommand("money")).setExecutor(new money());

    }


}
