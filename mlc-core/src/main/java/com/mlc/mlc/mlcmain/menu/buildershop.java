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
import java.util.EnumMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import static com.mlc.mlc.Mlc.instance;
import static com.mlc.mlc.Mlc.miniMessage;

/** 商店商品按分类配置在 menu/shop.yml 中。 */
public final class buildershop implements Listener {
    private static final int SIZE = 54;
    private static final int PAGE_SIZE = 28;
    private static final int PREVIOUS = 46;
    private static final int PROFILE = 49;
    private static final int NEXT = 52;

    private enum Category {
        BUILDING("建材", Material.BRICKS, 2, "building-products"),
        EGGS("刷怪蛋", Material.CREEPER_SPAWN_EGG, 4, "egg-products"),
        REDSTONE("红石元件", Material.REDSTONE, 6, "redstone-products");

        private final String name;
        private final Material icon;
        private final int slot;
        private final String configKey;

        Category(String name, Material icon, int slot, String configKey) {
            this.name = name;
            this.icon = icon;
            this.slot = slot;
            this.configKey = configKey;
        }
    }

    private record Product(Material material, double price, ItemStack display) { }

    // 只在启动和重载时替换，打开或翻页时均使用同一份内存快照。
    private static Map<Category, List<Product>> cachedProducts = Map.of();

    private static final class ShopHolder implements InventoryHolder {
        private final Category category;
        private final int page;
        private final List<Product> products;
        private Inventory inventory;

        private ShopHolder(Category category, int page, List<Product> products) {
            this.category = category;
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
        open(player, Category.BUILDING, 0, cachedProducts.getOrDefault(Category.BUILDING, List.of()));
    }

    public static boolean reload() {
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(new File(instance.getDataFolder(), "menu/shop.yml"));
        } catch (IOException | InvalidConfigurationException e) {
            instance.getLogger().severe("无法读取商店配置 menu/shop.yml: " + e.getMessage());
            return false;
        }
        Map<Category, List<Product>> products = new EnumMap<>(Category.class);
        for (Category category : Category.values()) products.put(category, new ArrayList<>());
        for (Category category : Category.values()) {
            ConfigurationSection section = config.getConfigurationSection("builder-shop." + category.configKey);
            if (section == null) {
                instance.getLogger().severe("商店配置缺少 builder-shop." + category.configKey + "，保留原有商品");
                return false;
            }
            loadProducts(section, products.get(category));
        }
        for (Category category : Category.values()) products.put(category, List.copyOf(products.get(category)));
        cachedProducts = Map.copyOf(products);
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getOpenInventory().getTopInventory().getHolder() instanceof ShopHolder holder) {
                List<Product> categoryProducts = cachedProducts.get(holder.category);
                int lastPage = Math.max(0, (categoryProducts.size() - 1) / PAGE_SIZE);
                open(player, holder.category, Math.min(holder.page, lastPage), categoryProducts);
            }
        }
        return true;
    }

    private static void loadProducts(ConfigurationSection section, List<Product> products) {
        for (String key : section.getKeys(false)) {
            Material material = Material.matchMaterial(key);
            double price = section.getDouble(key, -1);
            if (material == null || !material.isItem() || material.isAir() || price <= 0
                    || !Double.isFinite(price) || java.math.BigDecimal.valueOf(price).stripTrailingZeros().scale() > 2
                    || price * material.getMaxStackSize() > Integer.MAX_VALUE) {
                instance.getLogger().warning("商店商品配置无效: " + key);
                continue;
            }
            products.add(new Product(material, price, createProductDisplay(material, price)));
        }
    }

    private static ItemStack createProductDisplay(Material material, double price) {
        ItemStack display = new ItemStack(material);
        ItemMeta meta = display.getItemMeta();
        meta.lore(List.of(
                line(" "),
                line("<#aab8b0>单价  <#ffd166>" + price + " <!shadow><white><sprite:items:\"mlc:item/crystal_coin\">"),
                line("<#aab8b0>整组  <#ffd166>" + java.math.BigDecimal.valueOf(price).multiply(java.math.BigDecimal.valueOf(material.getMaxStackSize())).stripTrailingZeros().toPlainString() + " <!shadow><white><sprite:items:\"mlc:item/crystal_coin\"> <#77867e>(" + material.getMaxStackSize() + " 个)"),
                line(" "),
                line("<#b8ffde>左键 <#d8e3dc>购买 1 个"),
                line("<#b8ffde>Shift + 左键 <#d8e3dc>购买一组")
        ));
        display.setItemMeta(meta);
        return display;
    }

    private static void open(Player player, Category category, int page, List<Product> products) {
        int pages = Math.max(1, (products.size() + PAGE_SIZE - 1) / PAGE_SIZE);
        ShopHolder holder = new ShopHolder(category, page, products);
        Inventory inventory = Bukkit.createInventory(holder, SIZE,
                miniMessage.deserialize("<#b8ffde>商店 <#8a9a91>· <#d8e3dc>" + category.name + " <#8a9a91>· " + (page + 1) + "/" + pages));
        holder.inventory = inventory;
        ItemStack border = item(Material.GRAY_STAINED_GLASS_PANE, "<!i> ");
        for (int slot = 0; slot < SIZE; slot++) {
            if (slot < 9 || slot >= 45 || slot % 9 == 0 || slot % 9 == 8) inventory.setItem(slot, border);
        }
        for (Category tab : Category.values()) {
            ItemStack button = item(tab.icon, "<!i>" + (tab == category ? "<#ffd166>▸ " : "<#b8ffde>") + tab.name);
            ItemMeta meta = button.getItemMeta();
            meta.lore(List.of(line(tab == category ? "<#ffd166>当前分类" : "<#aab8b0>点击切换分类")));
            button.setItemMeta(meta);
            inventory.setItem(tab.slot, button);
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
        headMeta.lore(List.of(line("<#aab8b0>当前余额  <#ffd166>" + Moneyfilemanager.getPlayerMoney(player.getUniqueId()) + " <!shadow><white><sprite:items:\"mlc:item/crystal_coin\">")));
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
        for (Category tab : Category.values()) {
            if (slot == tab.slot) {
                if (tab != holder.category) refresh(player, holder, tab, 0);
                return;
            }
        }
        if (slot == PREVIOUS && holder.page > 0) {
            refresh(player, holder, holder.category, holder.page - 1);
            return;
        }
        if (slot == NEXT && (holder.page + 1) * PAGE_SIZE < holder.products.size()) {
            refresh(player, holder, holder.category, holder.page + 1);
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
        double cost = java.math.BigDecimal.valueOf(product.price()).multiply(java.math.BigDecimal.valueOf(amount)).doubleValue();
        if (Moneyfilemanager.getPlayerMoney(player.getUniqueId()) < cost) {
            player.sendMessage(line("<#ff8a80>余额不足，需要 <#ffd166>" + cost + " <!shadow><white><sprite:items:\"mlc:item/crystal_coin\"><#ff8a80>。"));
            return;
        }
        if (!Moneyfilemanager.withdrawBalance(player.getUniqueId(), cost)) {
            player.sendMessage(line("<#ff8a80>购买失败，请稍后重试。"));
            return;
        }
        player.getInventory().addItem(purchase);
        player.sendMessage(line("<#b8ffde>购买成功 <#aab8b0>· <#ffd166>" + cost + " <!shadow><white><sprite:items:\"mlc:item/crystal_coin\"> <#aab8b0>· " + amount + " 个"));
        refresh(player, holder, holder.category, holder.page);
    }

    private static void refresh(Player player, ShopHolder holder, Category category, int page) {
        Bukkit.getScheduler().runTask(instance, () -> {
            if (player.isOnline() && player.getOpenInventory().getTopInventory().equals(holder.inventory)) {
                open(player, category, page, category == holder.category ? holder.products : cachedProducts.getOrDefault(category, List.of()));
            }
        });
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof ShopHolder) event.setCancelled(true);
    }
}
