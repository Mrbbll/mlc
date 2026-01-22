package com.mlc.mlc;

import com.mlc.mlc.backpack.listener.Backpacklistener;
import com.mlc.mlc.dialog.Listener.ServerJoinListener;
import com.mlc.mlc.dropmoney.DropmoneyListener;
import com.mlc.mlc.enchantments.Enchantlistener;
import com.mlc.mlc.ess.listener.Tplistener;
import com.mlc.mlc.ess.command.*;
import com.mlc.mlc.hook.economy.commands.money;
import com.mlc.mlc.hook.placeholderapi.Mlceco;
import com.mlc.mlc.items.itemmannager.mlcitems;
import com.mlc.mlc.items.recipes.Backpack;
import com.mlc.mlc.items.recipes.Elytra;
import com.mlc.mlc.items.recipes.Healfood;
import com.mlc.mlc.items.recipes.Moneyitemrecipe;
import com.mlc.mlc.listener.*;
import com.mlc.mlc.mail.command.mymail;
import com.mlc.mlc.mail.command.sendmail;
import com.mlc.mlc.mail.command.sendmailtoall;
import com.mlc.mlc.mail.listener.Maillistener;
import com.mlc.mlc.maxhealth.Deadlistener;
import com.mlc.mlc.maxhealth.Eatlistener;
import com.mlc.mlc.mlcitem.command.mlcgui;
import com.mlc.mlc.mlcitem.listener.Guilistener;
import com.mlc.mlc.motd.Motd;
import com.mlc.mlc.respacksender.packsender;
import com.mlc.mlc.rightclickheavest.Heavestlistener;
import com.mlc.mlc.sit.command.Sit;
import com.mlc.mlc.sit.listener.Unsitlistener;
import com.mlc.mlc.sleep.Sleeplistener;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import static com.mlc.mlc.Mlc.instance;
import static com.mlc.mlc.dialog.Serverjoindialog.initserverjoindialog;
import static com.mlc.mlc.dialog.Serverlinks.setserverLinks;

public class Task {
    public static void task() throws URISyntaxException, NoSuchAlgorithmException, IOException {
        Motd.setmotd();

        //设置服务器链接
        setserverLinks();
        //设置服务器加入显示的对话框
        initserverjoindialog();
        //设置材质包
        packsender.init();

        //初始化物品
        mlcitems.init();

        Bukkit.getPluginManager().registerEvents(new Backpacklistener(),instance);
        Bukkit.getPluginManager().registerEvents(new Heavestlistener(),instance);
        Bukkit.getPluginManager().registerEvents(new Guilistener(), instance);
        Bukkit.getPluginManager().registerEvents(new Unsitlistener(), instance);
        Bukkit.getPluginManager().registerEvents(new Joinlistener(), instance);
        Bukkit.getPluginManager().registerEvents(new Tplistener(), instance);
        Bukkit.getPluginManager().registerEvents(new Deadlistener(), instance);
        Bukkit.getPluginManager().registerEvents(new Eatlistener(), instance);
        Bukkit.getPluginManager().registerEvents(new Enchantlistener(), instance);
        Bukkit.getPluginManager().registerEvents(new Sleeplistener(), instance);
        Bukkit.getPluginManager().registerEvents(new Maillistener(), instance);
        Bukkit.getPluginManager().registerEvents(new ServerJoinListener(), instance);
        Bukkit.getPluginManager().registerEvents(new com.mlc.mlc.respacksender.Listener.Joinlistener(), instance);
        Bukkit.getPluginManager().registerEvents(new DropmoneyListener(), instance);

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
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new Mlceco().register();
            System.out.println("\n\nmlcdomain placeholder registered\n\n");
        }

        //注册物品配方
        Backpack.backpackrecipe();
        Healfood.healfoodrecipe();
        Elytra.elytrarecipe();
        Moneyitemrecipe.money_ingotrecipe();
        Moneyitemrecipe.money_stackrecipe();
        Moneyitemrecipe.money_gemrecipe();
    }


}
