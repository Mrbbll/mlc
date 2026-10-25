package com.mlc.mlc.mlcmain.menu;

import net.kyori.adventure.text.Component;
import com.destroystokyo.paper.profile.PlayerProfile;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.mlc.mlc.Mlc.instance;
import static com.mlc.mlc.Mlc.miniMessage;

public class Mlcmenu {
    public static Inventory menuinv;

    // 菜单物品槽位
    public static final int SLOT_RTP = 10;
    public static final int SLOT_TPA = 12;
    public static final int SLOT_MONEY = 14;
    public static final int SLOT_MAIL = 16;
    public static final int SLOT_MAP = 21;
    public static final int SLOT_ITEMSHOW = 22;
    public static final int SLOT_QUESTION = 23;

    // 超链接URL
    public static final String MAP_URL = "http://43.248.188.28:19423/";
    public static final String DOCS_URL = "https://docs.qq.com/aio/DRnp3YU9sRmxWYnVB";

    public static void initmenuinv() {
        menuinv = Bukkit.createInventory(null, 9 * 6,
                miniMessage.deserialize("<#b8ffde>快捷菜单</#b8ffde>"));

        // 清空所有槽位
        for (int i = 0; i < 9 * 6; i++) {
            menuinv.setItem(i, ItemStack.of(Material.AIR));
        }

        // 玻璃边框装饰
        ItemStack border = createMenuItem(Material.BLACK_STAINED_GLASS_PANE, " ",null);
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
                "<!i><#ffaa00>随机传送 <#aaaaaa>(RTP)",
                null,
                "<!i><#aaaaaa>随机传送到世界某处",
                "<!i><#ffff55>» 点击执行 <#ffffff>/rtp"));

        menuinv.setItem(SLOT_TPA, createMenuItem(
                Material.ENDER_PEARL,
                "<!i><#ffaa00>传送请求 <#aaaaaa>(TPA)",
                null,
                "<!i><#aaaaaa>向其他玩家发送传送请求",
                "<!i><#ffff55>» 点击执行 <#ffffff>/tpa"));


        menuinv.setItem(SLOT_MONEY, createMenuItem(
                Material.GOLD_INGOT,
                "<!i><#ffaa00>经济 <#aaaaaa>(Money)",
                null,
                "<!i><#aaaaaa>查看你的余额",
                "<!i><#ffff55>» 点击执行 <#ffffff>/money"));

        menuinv.setItem(SLOT_MAIL, createMenuItem(
                Material.BOOK,
                "<!i><#ffaa00>邮箱 <#aaaaaa>(Mail)",
                null,
                "<!i><#aaaaaa>打开你的邮箱",
                "<!i><#ffff55>» 点击执行 <#ffffff>/mail"));

        menuinv.setItem(SLOT_MAP, createMenuItem(
                Material.CHEST,
                "<!i><#ffaa00>网页地图 <#aaaaaa>(MLC Items)",
                null,
                "<!i><#ffff55>» 点击在聊天栏打开超链接"));

        menuinv.setItem(SLOT_QUESTION, createMenuItem(
                Material.CHEST,
                "<!i><#ffaa00>在线文档",
                null,
                "<!i><#aaaaaa>打开在线文档",
                "<!i><#ffff55>» 点击在聊天栏打开超链接"));

        menuinv.setItem(SLOT_ITEMSHOW, createMenuItem(
                Material.NAME_TAG,
                "<!i><#ffaa00>展示物品 <#aaaaaa>(Item Show)",
                null,
                "<!i><#aaaaaa>在聊天中展示手中的物品",
                "<!i><#ffff55>» 点击执行 <#ffffff>/item"));
    }

    /**
     * 创建菜单物品
     * @param material 物品材质
     * @param name 显示名称
     * @param lore 物品描述
     * @return 创建好的 ItemStack
     */
    public static ItemStack createMenuItem(Material material, String name, String model_space ,String... lore) {
        ItemStack item = ItemStack.of(material);
        ItemMeta meta = item.getItemMeta();
        if(model_space!=null){
            meta.setItemModel(NamespacedKey.fromString(model_space));
        }
        // 使用 MiniMessage RGB 颜色标签转换为 Adventure Component
        meta.displayName(formatMiniMessage(name));

        if (lore != null && lore.length > 0) {
            List<Component> loreList = new ArrayList<>();
            for (String line : lore) {
                if (!line.isEmpty()) {
                    loreList.add(formatMiniMessage(line));
                }
            }
            meta.lore(loreList);
        }

        item.setItemMeta(meta);
        return item;
    }

    /**
     * 将 MiniMessage 格式的字符串转换为 Adventure Component，支持 <#RRGGBB> 颜色标签
     */
    private static Component formatMiniMessage(String text) {
        return miniMessage.deserialize(text);
    }

    /**
     * 创建自定义玩家头颅物品
     * @param name 显示名称 (MiniMessage 格式，支持 <#RRGGBB> 颜色标签)
     * @param uuid 材质哈希 (http://textures.minecraft.net/texture/ 后面的部分)
     * @param modelSpace 自定义物品模型命名空间 (可为null)
     * @param lore 物品描述
     * @return 创建好的头颅 ItemStack
     */
    public static ItemStack createSkullItem(String name, String uuid, String modelSpace, String... lore) {
        ItemStack head = ItemStack.of(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        // 设置自定义皮肤材质
        if (uuid != null && !uuid.isEmpty()) {
            OfflinePlayer offlinePlayer = instance.getServer().getOfflinePlayer(UUID.fromString(uuid));
            meta.setOwningPlayer(offlinePlayer);
        }

        // 设置自定义物品模型
        if (modelSpace != null) {
            meta.setItemModel(NamespacedKey.fromString(modelSpace));
        }

        meta.displayName(formatMiniMessage(name));

        if (lore != null && lore.length > 0) {
            List<Component> loreList = new ArrayList<>();
            for (String line : lore) {
                if (!line.isEmpty()) {
                    loreList.add(formatMiniMessage(line));
                }
            }
            meta.lore(loreList);
        }

        head.setItemMeta(meta);
        return head;
    }

    public static void open(Player player) {
        player.openInventory(menuinv);
    }
}

