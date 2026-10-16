package com.mlc.mlc;

import com.mlc.mlc.mlcmain.backpack.listener.Backpacklistener;
import com.mlc.mlc.mlcmain.chat.commands.Item;
import com.mlc.mlc.mlcmain.chat.listener.Chatlistener;
import com.mlc.mlc.mlcmain.crates.listener.Opencreates;
import com.mlc.mlc.mlcmain.dialog.Listener.ServerJoinListener;
import com.mlc.mlc.mlcmain.dropmoney.DropmoneyListener;
import com.mlc.mlc.mlcmain.enchantments.listener.EnchantRestricter;
import com.mlc.mlc.mlcmain.enchantments.listener.EnchantUpgradelistener;
import com.mlc.mlc.mlcmain.enchantments.moreenchants.VeinMine;
import com.mlc.mlc.mlcmain.ess.listener.Tplistener;
import com.mlc.mlc.mlcmain.ess.command.*;
import com.mlc.mlc.mlcmain.hook.economy.commands.money;
import com.mlc.mlc.mlcmain.hook.placeholderapi.Mlceco;
import com.mlc.mlc.mlcmain.items.itemmannager.Cratesitems;
import com.mlc.mlc.mlcmain.items.itemmannager.Fesitems;
import com.mlc.mlc.mlcmain.items.itemmannager.Mlcitems;
import com.mlc.mlc.mlcmain.items.recipes.*;
import com.mlc.mlc.mlcmain.Joinlistener.*;
import com.mlc.mlc.mlcmain.mail.command.MailCommand;
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
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;

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
        Bukkit.getPluginManager().registerEvents(new EnchantUpgradelistener(), instance);
        Bukkit.getPluginManager().registerEvents(new Sleeplistener(), instance);
        Bukkit.getPluginManager().registerEvents(new Maillistener(), instance);
        Bukkit.getPluginManager().registerEvents(new ServerJoinListener(), instance);
        Bukkit.getPluginManager().registerEvents(new com.mlc.mlc.mlcmain.respacksender.Listener.Joinlistener(), instance);
        Bukkit.getPluginManager().registerEvents(new DropmoneyListener(), instance);
        Bukkit.getPluginManager().registerEvents(new JoinMoneyListener(), instance);
        Bukkit.getPluginManager().registerEvents(new Opencreates(), instance);
        Bukkit.getPluginManager().registerEvents(new menuopenlistener(), instance);
        Bukkit.getPluginManager().registerEvents(new Tpalistener(), instance);
        Bukkit.getPluginManager().registerEvents(new VeinMine(), instance);
        Bukkit.getPluginManager().registerEvents(new Chatlistener(), instance);
        Bukkit.getPluginManager().registerEvents(new EnchantRestricter(), instance);


        // Paper plugins do not register commands declared in YAML.
        instance.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            var commands = event.registrar();
            commands.register("mlcreload", "重载配置",
                    new ExecutorCommand("mlcreload", "重载配置", "", "mlc.op", new reload()));
            commands.register("back", "回到上个路径点", new ExecutorCommand("back", "回到上个路径点", new back()));
            commands.register("mail", "打开邮箱、发送邮件或给所有人发送邮件",
                    new ExecutorCommand("mail", "打开邮箱、发送邮件或给所有人发送邮件",
                            "/mail 或 /mail send <玩家名> 或 /mail sendtoall", null, new MailCommand()));
            commands.register("mlcitem", "打开mlc物品菜单",
                    new ExecutorCommand("mlcitem", "打开mlc物品菜单", "", "mlc.op", new mlcitemgui()));
            commands.register("tpa", "请求传送", new ExecutorCommand("tpa", "请求传送", new tpa()));
            commands.register("tpaccept", "同意请求", new ExecutorCommand("tpaccept", "同意请求", new tpaccept()));
            commands.register("tpahere", "请求传送到自己位置", new ExecutorCommand("tpahere", "请求传送到自己位置", new tpahere()));
            commands.register("sit", "坐下", new ExecutorCommand("sit", "坐下", new Sit()));
            commands.register("money", "操作货币", new ExecutorCommand("money", "操作货币", new money()));
            commands.register("item", "展示物品", new ExecutorCommand("item", "展示物品", new Item()));
            commands.register("rtp", "随机传送", new ExecutorCommand("rtp", "随机传送", new rtp()));
            commands.register("menu", "打开MLC功能菜单", new ExecutorCommand("menu", "打开MLC功能菜单", new menu()));
            commands.register("home","回到家",new ExecutorCommand("home","回到家",new home()));
            commands.register("sethome","设置家",new ExecutorCommand("sethome","设置家",new sethome()));
        });

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
