package com.mlc.mlc.mlcmain.menu;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static com.mlc.mlc.Mlc.miniMessage;

public class Mlcmenu {
    public static Inventory menuinv;

    // 菜单物品槽位
    public static final int SLOT_RTP = 10;
    public static final int SLOT_HOME = 11;
    public static final int SLOT_TPA = 12;
    public static final int SLOT_BACK = 13;
    public static final int SLOT_SIT = 14;
    public static final int SLOT_MONEY = 15;
    public static final int SLOT_MAIL = 16;
    public static final int SLOT_MLCITEM = 20;
    public static final int SLOT_CRATES = 21;
    public static final int SLOT_ITEMSHOW = 22;
    public static final int SLOT_RELOAD = 24;

    public static void initmenuinv() {
        menuinv = Bukkit.createInventory(null, 9 * 6,
                Component.text("mlc 菜单", TextColor.fromHexString("#f73636")));

        // 清空所有槽位
        for (int i = 0; i < 9 * 6; i++) {
            menuinv.setItem(i, ItemStack.of(Material.AIR));
        }

        // 玻璃边框装饰
        ItemStack border = createMenuItem(Material.BLACK_STAINED_GLASS_PANE, " ", new String[]{});
        for (int i = 0; i < 9; i++) {
            menuinv.setItem(i, border);           // 第一行
            menuinv.setItem(45 + i, border);      // 最后一行
        }
        for (int i = 1; i < 5; i++) {
            menuinv.setItem(i * 9, border);       // 左列
            menuinv.setItem(i * 9 + 8, border);   // 右列
        }

        // 功能物品
        menuinv.setItem(SLOT_RTP, createMenuItem(
                Material.COMPASS,
                "&6随机传送 &7(RTP)",
                "&7随机传送到世界某处",
                "&e» 点击执行 &f/rtp"));

        menuinv.setItem(SLOT_HOME, createMenuItem(
                Material.RED_BED,
                "&6家 &7(Home)",
                "&7传送回你设置的家",
                "&e» 点击执行 &f/home"));

        menuinv.setItem(SLOT_TPA, createMenuItem(
                Material.ENDER_PEARL,
                "&6传送请求 &7(TPA)",
                "&7向其他玩家发送传送请求",
                "&e» 点击执行 &f/tpa"));

        menuinv.setItem(SLOT_BACK, createMenuItem(
                Material.ARROW,
                "&6返回 &7(Back)",
                "&7返回上一个传送点",
                "&e» 点击执行 &f/back"));

        menuinv.setItem(SLOT_SIT, createMenuItem(
                Material.OAK_STAIRS,
                "&6坐下 &7(Sit)",
                "&7坐在楼梯方块上",
                "&e» 点击执行 &f/sit"));

        menuinv.setItem(SLOT_MONEY, createMenuItem(
                Material.GOLD_INGOT,
                "&6经济 &7(Money)",
                "&7查看你的余额",
                "&e» 点击执行 &f/money"));

        menuinv.setItem(SLOT_MAIL, createMenuItem(
                Material.BOOK,
                "&6邮箱 &7(Mail)",
                "&7打开你的邮箱",
                "&e» 点击执行 &f/mymail"));

        menuinv.setItem(SLOT_MLCITEM, createMenuItem(
                Material.CHEST,
                "&6物品菜单 &7(MLC Items)",
                "&7浏览MLC自定义物品",
                "&e» 点击执行 &f/mlcitem"));

        menuinv.setItem(SLOT_CRATES, createMenuItem(
                Material.DIAMOND,
                "&6抽奖 &7(Crates)",
                "&7打开抽奖界面",
                "&e» 点击执行抽奖"));

        menuinv.setItem(SLOT_ITEMSHOW, createMenuItem(
                Material.NAME_TAG,
                "&6展示物品 &7(Item Show)",
                "&7在聊天中展示手中的物品",
                "&e» 点击执行 &f/item"));

        menuinv.setItem(SLOT_RELOAD, createMenuItem(
                Material.CLOCK,
                "&c重载配置 &7(Reload)",
                "&7重新加载插件配置",
                "&c需要管理员权限",
                "&e» 点击执行 &f/mlcreload"));
    }

    /**
     * 创建菜单物品
     * @param material 物品材质
     * @param name 显示名称 (支持 & 颜色代码)
     * @param lore 物品描述 (支持 & 颜色代码)
     * @return 创建好的 ItemStack
     */
    public static ItemStack createMenuItem(Material material, String name, String... lore) {
        ItemStack item = ItemStack.of(material);
        ItemMeta meta = item.getItemMeta();

        // 使用 & 颜色代码转换为 Adventure Component
        meta.displayName(formatLegacy(name));

        if (lore != null && lore.length > 0) {
            List<Component> loreList = new ArrayList<>();
            for (String line : lore) {
                if (!line.isEmpty()) {
                    loreList.add(formatLegacy(line));
                }
            }
            meta.lore(loreList);
        }

        item.setItemMeta(meta);
        return item;
    }

    /**
     * 将 & 颜色代码格式的字符串转换为 Adventure Component
     * 支持: &0-&9, &a-&f, &l, &m, &n, &o, &k, &r
     */
    private static Component formatLegacy(String text) {
        return miniMessage.deserialize(text);
    }

    public static void open(Player player) {
        player.openInventory(menuinv);
    }
}
