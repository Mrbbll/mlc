package com.mlc.mlc;

import com.mlc.mlc.mlcmain.backpack.listener.Backpacklistener;
import com.mlc.mlc.mlcmain.chat.commands.Item;
import com.mlc.mlc.mlcmain.crates.listener.Opencreates;
import com.mlc.mlc.mlcmain.dialog.Listener.ServerJoinListener;
import com.mlc.mlc.mlcmain.dropmoney.DropmoneyListener;
import com.mlc.mlc.mlcmain.enchantments.Enchantlistener;
import com.mlc.mlc.mlcmain.ess.listener.Tplistener;
import com.mlc.mlc.mlcmain.ess.command.*;
import com.mlc.mlc.mlcmain.hook.economy.commands.money;
import com.mlc.mlc.mlcmain.hook.placeholderapi.Mlceco;
import com.mlc.mlc.mlcmain.items.itemmannager.Cratesitems;
import com.mlc.mlc.mlcmain.items.itemmannager.Fesitems;
import com.mlc.mlc.mlcmain.items.itemmannager.Mlcitems;
import com.mlc.mlc.mlcmain.items.recipes.*;
import com.mlc.mlc.mlcmain.Joinlistener.*;
import com.mlc.mlc.mlcmain.mail.command.mymail;
import com.mlc.mlc.mlcmain.mail.command.sendmail;
import com.mlc.mlc.mlcmain.mail.command.sendmailtoall;
import com.mlc.mlc.mlcmain.mail.listener.Maillistener;
import com.mlc.mlc.mlcmain.menu.Mlcmenu;
import com.mlc.mlc.mlcmain.menu.commands.menu;
import com.mlc.mlc.mlcmain.menu.listener.menuopenlistener;
import com.mlc.mlc.mlcmain.menu.listener.Tpalistener;
import com.mlc.mlc.mlcmain.maxhealth.Deadlistener;
import com.mlc.mlc.mlcmain.maxhealth.Eatlistener;
import com.mlc.mlc.mlcmain.mlcitem.command.mlcitemgui;
import com.mlc.mlc.mlcmain.mlcitem.listener.Guilistener;
import com.mlc.mlc.mlcmain.motd.Motd;
import com.mlc.mlc.mlcmain.reload.reload;
import com.mlc.mlc.mlcmain.respacksender.packsender;
import com.mlc.mlc.mlcmain.rightclickheavest.Heavestlistener;
import com.mlc.mlc.mlcmain.signin.Listener.JoinMoneyListener;
import com.mlc.mlc.mlcmain.sit.command.Sit;
import com.mlc.mlc.mlcmain.sit.listener.Unsitlistener;
import com.mlc.mlc.mlcmain.sleep.Sleeplistener;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import static com.mlc.mlc.Mlc.instance;
import static com.mlc.mlc.mlcmain.dialog.Serverjoindialog.initserverjoindialog;
import static com.mlc.mlc.mlcmain.dialog.Serverlinks.setserverLinks;

public class Task {
    public static void task() throws URISyntaxException, NoSuchAlgorithmException, IOException {
        Motd.setmotd();

        //设置服务器链接
        setserverLinks();
        //设置服务器加入显示的对话框
        initserverjoindialog();
        //设置材质包
        packsender.init();


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
        Bukkit.getPluginManager().registerEvents(new com.mlc.mlc.mlcmain.respacksender.Listener.Joinlistener(), instance);
        Bukkit.getPluginManager().registerEvents(new DropmoneyListener(), instance);
        Bukkit.getPluginManager().registerEvents(new JoinMoneyListener(), instance);
        Bukkit.getPluginManager().registerEvents(new Opencreates(), instance);
        Bukkit.getPluginManager().registerEvents(new menuopenlistener(), instance);
        Bukkit.getPluginManager().registerEvents(new Tpalistener(), instance);

        Objects.requireNonNull(Bukkit.getPluginCommand("mlcreload")).setExecutor(new reload());
        Objects.requireNonNull(Bukkit.getPluginCommand("back")).setExecutor((new back()));
        Objects.requireNonNull(Bukkit.getPluginCommand("sendmail")).setExecutor((new sendmail()));
        Objects.requireNonNull(Bukkit.getPluginCommand("mymail")).setExecutor((new mymail()));
        Objects.requireNonNull(Bukkit.getPluginCommand("mlcitem")).setExecutor(new mlcitemgui());
        Objects.requireNonNull(Bukkit.getPluginCommand("sendmailtoall")).setExecutor(new sendmailtoall());
//        Objects.requireNonNull(Bukkit.getPluginCommand("home")).setExecutor(new home());
//        Objects.requireNonNull(Bukkit.getPluginCommand("sethome")).setExecutor(new sethome());
//        Objects.requireNonNull(Bukkit.getPluginCommand("delhome")).setExecutor(new delhome());
        Objects.requireNonNull(Bukkit.getPluginCommand("tpa")).setExecutor(new tpa());
        Objects.requireNonNull(Bukkit.getPluginCommand("tpaccept")).setExecutor(new tpaccept());
        Objects.requireNonNull(Bukkit.getPluginCommand("tpahere")).setExecutor(new tpahere());
        Objects.requireNonNull(Bukkit.getPluginCommand("sit")).setExecutor((new Sit()));
        Objects.requireNonNull(Bukkit.getPluginCommand("money")).setExecutor(new money());
        Objects.requireNonNull(Bukkit.getPluginCommand("money")).setTabCompleter(new money());
        Objects.requireNonNull(Bukkit.getPluginCommand("item")).setExecutor(new Item());
        Objects.requireNonNull(Bukkit.getPluginCommand("rtp")).setExecutor(new rtp());
        Objects.requireNonNull(Bukkit.getPluginCommand("menu")).setExecutor(new menu());

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new Mlceco().register();
            System.out.println("\n\nmlc placeholder registered\n\n");
        }



        Cratesitems.init();
        Fesitems.init();
        Mlcitems.init();
        Mlcmenu.initmenuinv();

        //注册物品配方
        Backpack.backpackrecipe();
        Healfood.healfoodrecipe();
        Elytra.elytrarecipe();
        Moneyitemrecipe.money_nugget_to_ingotrecipe();
        Moneyitemrecipe.money_ingot_to_stackrecipe();
        Moneyitemrecipe.money_gemrecipe();
        Moneyitemrecipe.money_coin_to_nuggetrecipe();
        Moneyitemrecipe.money_ingot_to_nuggetrecipe();
        Moneyitemrecipe.money_stack_to_ingotrecipe();
        Moneyitemrecipe.money_nugget_to_coinrecipe();
        Moneyitemrecipe.money_stack_to_money_stack_x1recipe();
        Moneyitemrecipe.money_stack_x1_to_money_stack_x2recipe();
        Moneyitemrecipe.money_stack_x2_to_money_stack_x3recipe();
        Moneyitemrecipe.money_stack_x3_to_money_stack_x4recipe();
        Moneyitemrecipe.money_stack_x4_to_money_stack_x5recipe();
        Moneyitemrecipe.money_stack_x1_to_money_stackrecipe();
        Moneyitemrecipe.money_stack_x2_to_money_stack_x1recipe();
        Moneyitemrecipe.money_stack_x3_to_money_stack_x2recipe();
        Moneyitemrecipe.money_stack_x4_to_money_stack_x3recipe();
        Moneyitemrecipe.money_stack_x5_to_money_stack_x4recipe();
        Crate.crateRecipe();

    }


}
