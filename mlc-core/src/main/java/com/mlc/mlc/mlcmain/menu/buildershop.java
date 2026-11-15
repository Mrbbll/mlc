package com.mlc.mlc.mlcmain.menu;

import com.mlc.mlc.mlcmain.hook.economy.Moneyfilemanager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.mlc.mlc.Mlc.instance;
import static com.mlc.mlc.Mlc.miniMessage;

/** 建筑商店：商品在 menu/shop.yml 的 builder-shop.products 中配置。 */
public final class buildershop implements Listener {
    private static final int SIZE = 54;
    private static final int PAGE_SIZE = 28;
    private static final int PREVIOUS = 46;
    private static final int PROFILE = 49;
    private static final int NEXT = 52;

    private record Product(Material material, int price, ItemStack display) { }

    // 只在启动和重载时替换，打开或翻页时均使用同一份内存快照。
    private static List<Product> cachedProducts = List.of();

    private static final class ShopHolder implements InventoryHolder {
        private final int page;
        private final List<Product> products;
        private Inventory inventory;

        private ShopHolder(int page, List<Product> products) {
            this.page = page;
            this.products = products;
        }

        @Override
        public @NotNull Inventory getInventory() {
            return inventory;
        }
    }

    public static void initConfig() {
        File file = new File(instance.getDataFolder(), "menu/shop.yml");
        if (!file.exists()) {
            instance.saveResource("menu/shop.yml", false);
        }
    }

    public static void open(Player player) {
        open(player, 0, cachedProducts);
    }

    public static boolean reload() {
        List<Product> products = new ArrayList<>();
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(new File(instance.getDataFolder(), "menu/shop.yml"));
        } catch (IOException | InvalidConfigurationException e) {
            instance.getLogger().severe("无法读取商店配置 menu/shop.yml: " + e.getMessage());
            return false;
        }
        ConfigurationSection section = config.getConfigurationSection("builder-shop.products");
        if (section == null) {
            instance.getLogger().severe("商店配置缺少 builder-shop.products，保留原有商品");
            return false;
        }
        for (String key : section.getKeys(false)) {
            Material material = Material.matchMaterial(key);
            int price = section.getInt(key, -1);
            if (material == null || !material.isItem() || material.isAir() || price <= 0
                    || (long) price * material.getMaxStackSize() > Integer.MAX_VALUE) {
                instance.getLogger().warning("建筑商店商品配置无效: " + key);
                continue;
            }
            products.add(new Product(material, price, createProductDisplay(material, price)));
        }
        cachedProducts = List.copyOf(products);
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getOpenInventory().getTopInventory().getHolder() instanceof ShopHolder holder) {
                int lastPage = Math.max(0, (cachedProducts.size() - 1) / PAGE_SIZE);
                open(player, Math.min(holder.page, lastPage), cachedProducts);
            }
        }
        return true;
    }

    private static ItemStack createProductDisplay(Material material, int price) {
        ItemStack display = new ItemStack(material);
        ItemMeta meta = display.getItemMeta();
        meta.lore(List.of(
                line(" "),
                line("<#aab8b0>单价  <#ffd166>" + price + " 🪙"),
                line("<#aab8b0>整组  <#ffd166>" + ((long) price * material.getMaxStackSize()) + " 🪙 <#77867e>(" + material.getMaxStackSize() + " 个)"),
                line(" "),
                line("<#b8ffde>左键 <#d8e3dc>购买 1 个"),
                line("<#b8ffde>Shift + 左键 <#d8e3dc>购买一组")
        ));
        display.setItemMeta(meta);
        return display;
    }

    private static void open(Player player, int page, List<Product> products) {
        int pages = Math.max(1, (products.size() + PAGE_SIZE - 1) / PAGE_SIZE);
        ShopHolder holder = new ShopHolder(page, products);
        Inventory inventory = Bukkit.createInventory(holder, SIZE,
                miniMessage.deserialize("<#b8ffde>建筑商店 <#8a9a91>· " + (page + 1) + "/" + pages));
        holder.inventory = inventory;
        ItemStack border = item(Material.GRAY_STAINED_GLASS_PANE, "<!i> ");
        for (int slot = 0; slot < SIZE; slot++) {
            if (slot < 9 || slot >= 45 || slot % 9 == 0 || slot % 9 == 8) inventory.setItem(slot, border);
        }
        for (int i = 0; i < PAGE_SIZE && page * PAGE_SIZE + i < products.size(); i++) {
            Product product = products.get(page * PAGE_SIZE + i);
            inventory.setItem(productSlot(i), product.display().clone());
        }
        if (page > 0) inventory.setItem(PREVIOUS, item(Material.ARROW, "<!i><#b8ffde>← 上一页"));
        if (page + 1 < pages) inventory.setItem(NEXT, item(Material.ARROW, "<!i><#b8ffde>下一页 →"));
        ItemStack head = item(Material.PLAYER_HEAD, "<!i><#ffd166>我的钱包");
        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        headMeta.setOwningPlayer(player);
        headMeta.lore(List.of(line("<#aab8b0>当前余额  <#ffd166>" + Moneyfilemanager.getPlayerMoney(player.getUniqueId()) + " 🪙")));
        head.setItemMeta(headMeta);
        inventory.setItem(PROFILE, head);
        player.openInventory(inventory);
    }

    private static int productSlot(int index) {
        return 10 + (index / 7) * 9 + index % 7;
    }

    private static Component line(String text) {
        return miniMessage.deserialize("<!i>" + text);
    }

    private static ItemStack item(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(miniMessage.deserialize(name));
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getView().getTopInventory().getHolder() instanceof ShopHolder holder)) return;
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) return;
        int slot = event.getRawSlot();
        if (slot == PREVIOUS && holder.page > 0) {
            refresh(player, holder, holder.page - 1);
            return;
        }
        if (slot == NEXT && (holder.page + 1) * PAGE_SIZE < holder.products.size()) {
            refresh(player, holder, holder.page + 1);
            return;
        }
        if (event.getClick() != ClickType.LEFT && event.getClick() != ClickType.SHIFT_LEFT) return;
        int index = -1;
        for (int i = 0; i < PAGE_SIZE; i++) if (productSlot(i) == slot) { index = holder.page * PAGE_SIZE + i; break; }
        if (index < 0 || index >= holder.products.size()) return;
        Product product = holder.products.get(index);
        int amount = event.getClick() == ClickType.SHIFT_LEFT ? product.material().getMaxStackSize() : 1;
        ItemStack purchase = new ItemStack(product.material(), amount);
        int capacity = 0;
        for (ItemStack existing : player.getInventory().getStorageContents()) {
            if (existing == null || existing.getType().isAir()) capacity += purchase.getMaxStackSize();
            else if (existing.isSimilar(purchase)) capacity += Math.max(0, existing.getMaxStackSize() - existing.getAmount());
        }
        if (capacity < amount) {
            player.sendMessage(line("<#ff8a80>背包空间不足，请先整理背包。"));
            return;
        }
        int cost = product.price() * amount;
        if (Moneyfilemanager.getPlayerMoney(player.getUniqueId()) < cost) {
            player.sendMessage(line("<#ff8a80>余额不足，需要 <#ffd166>" + cost + " 🪙<#ff8a80>。"));
            return;
        }
        if (!Moneyfilemanager.withdrawForShop(player.getUniqueId(), cost)) {
            player.sendMessage(line("<#ff8a80>购买失败，请稍后重试。"));
            return;
        }
        player.getInventory().addItem(purchase);
        player.sendMessage(line("<#b8ffde>购买成功 <#aab8b0>· <#ffd166>" + cost + " 🪙 <#aab8b0>· " + amount + " 个"));
        refresh(player, holder, holder.page);
    }

    private static void refresh(Player player, ShopHolder holder, int page) {
        Bukkit.getScheduler().runTask(instance, () -> {
            if (player.isOnline() && player.getOpenInventory().getTopInventory().equals(holder.inventory)) {
                open(player, page, holder.products);
            }
        });
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof ShopHolder) event.setCancelled(true);
    }
}
