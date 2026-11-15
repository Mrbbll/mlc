package com.mlc.mlc.mlcmain.menu;

import com.mlc.mlc.mlcmain.hook.economy.Moneyfilemanager;
import com.mlc.mlc.mlcmain.items.itemmannager.Mlcitems;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.mlc.mlc.Mlc.miniMessage;

/** 每个玩家拥有独立的钱包容器，关闭时自动存入剩余水晶币。 */
public final class WalletMenu implements Listener {
    private static final int SIZE = 54, DEPOSIT = 48, BALANCE = 49, WITHDRAW = 50;

    private static final class Holder implements InventoryHolder {
        private Inventory inventory;
        @Override public @NotNull Inventory getInventory() { return inventory; }
    }

    public static void open(Player player) {
        // 先创建账户，之后存取才能确认数据库操作是否成功。
        Moneyfilemanager.getPlayerMoney(player.getUniqueId());
        Holder holder = new Holder();
        Inventory inventory = Bukkit.createInventory(holder, SIZE, line("<#b8ffde>水晶钱包"));
        holder.inventory = inventory;
        ItemStack border = icon(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < SIZE; i++) if (!isStorage(i)) inventory.setItem(i, border);
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        headMeta.setOwningPlayer(player);
        headMeta.displayName(line("<#b8ffde>" + miniMessage.escapeTags(player.getName()) + " 的钱包"));
        headMeta.lore(List.of(line("<#aab8b0>关闭时，容器中剩余的水晶币会自动存入")));
        head.setItemMeta(headMeta);
        inventory.setItem(4, head);
        inventory.setItem(DEPOSIT, icon(Material.HOPPER, "<#b8ffde>存入水晶币",
                "<#aab8b0>将水晶币放入中间的容器", "<#ffd166>点击存入容器中的全部水晶币"));
        inventory.setItem(WITHDRAW, icon(Material.DROPPER, "<#ffd166>取出水晶币",
                "<#aab8b0>水晶币会暂存在中间的容器", "<#b8ffde>左键 <#d8e3dc>取出 1 枚",
                "<#b8ffde>右键 <#d8e3dc>取出一组", "<#aab8b0>关闭时剩余水晶币将自动存入"));
        updateBalance(player, inventory);
        player.openInventory(inventory);
    }

    private static boolean isStorage(int slot) {
        return slot >= 9 && slot < 45 && slot % 9 != 0 && slot % 9 != 8;
    }

    private static boolean isCoin(ItemStack item) {
        return item != null && !item.getType().isAir() && item.isSimilar(Mlcitems.money_coin);
    }

    private static Component line(String text) { return miniMessage.deserialize("<!i>" + text); }

    private static ItemStack icon(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(line(name));
        meta.lore(java.util.Arrays.stream(lore).map(WalletMenu::line).toList());
        item.setItemMeta(meta);
        return item;
    }

    private static void updateBalance(Player player, Inventory inventory) {
        inventory.setItem(BALANCE, icon(Material.GOLD_INGOT, "<#ffd166>钱包余额",
                "<#aab8b0>当前余额 <#ffd166>" + Moneyfilemanager.getPlayerMoney(player.getUniqueId()) + " 水晶币",
                "<#71877a>1 枚水晶币 = 1 余额"));
    }

    private static void deposit(Player player, Inventory inventory) {
        int amount = 0;
        for (int slot = 0; slot < SIZE; slot++) {
            ItemStack item = inventory.getItem(slot);
            if (isStorage(slot) && isCoin(item)) amount += item.getAmount();
        }
        if (amount == 0) {
            player.sendMessage(line("<#ffb4a8>容器中没有可存入的水晶币。"));
            return;
        }
        if (!Moneyfilemanager.depositFromWallet(player.getUniqueId(), amount)) {
            player.sendMessage(line("<#ffb4a8>存入失败：余额可能已达上限或数据库不可用。"));
            return;
        }
        for (int slot = 0; slot < SIZE; slot++) if (isStorage(slot) && isCoin(inventory.getItem(slot))) inventory.setItem(slot, null);
        updateBalance(player, inventory);
        player.sendMessage(line("<#b8ffde>已存入 <#ffd166>" + amount + " <#b8ffde>枚水晶币。"));
    }

    private static int capacity(Inventory inventory) {
        int capacity = 0;
        for (int slot = 0; slot < SIZE; slot++) {
            if (!isStorage(slot)) continue;
            ItemStack item = inventory.getItem(slot);
            if (item == null || item.getType().isAir()) capacity += Mlcitems.money_coin.getMaxStackSize();
            else if (isCoin(item)) capacity += Math.max(0, item.getMaxStackSize() - item.getAmount());
        }
        return capacity;
    }

    // 调用前确保空间充足；仅写入容器槽位，避免覆盖按钮。
    private static void addCoins(Inventory inventory, int amount) {
        for (int slot = 0; slot < SIZE && amount > 0; slot++) {
            if (!isStorage(slot)) continue;
            ItemStack item = inventory.getItem(slot);
            if (!isCoin(item)) continue;
            int added = Math.min(amount, item.getMaxStackSize() - item.getAmount());
            if (added > 0) { item.setAmount(item.getAmount() + added); inventory.setItem(slot, item); amount -= added; }
        }
        for (int slot = 0; slot < SIZE && amount > 0; slot++) {
            if (!isStorage(slot)) continue;
            ItemStack item = inventory.getItem(slot);
            if (item != null && !item.getType().isAir()) continue;
            ItemStack coin = Mlcitems.money_coin.clone();
            int added = Math.min(amount, coin.getMaxStackSize());
            coin.setAmount(added);
            inventory.setItem(slot, coin);
            amount -= added;
        }
    }

    private static void withdraw(Player player, Inventory inventory, int amount) {
        if (capacity(inventory) < amount) {
            player.sendMessage(line("<#ffb4a8>钱包容器空间不足，请先取走物品。"));
            return;
        }
        if (Moneyfilemanager.getPlayerMoney(player.getUniqueId()) < amount) {
            player.sendMessage(line("<#ffb4a8>余额不足，需要 <#ffd166>" + amount + " <#ffb4a8>水晶币。"));
            return;
        }
        if (!Moneyfilemanager.withdrawBalance(player.getUniqueId(), amount)) {
            player.sendMessage(line("<#ffb4a8>取出失败，请稍后重试。"));
            return;
        }
        addCoins(inventory, amount);
        updateBalance(player, inventory);
        player.sendMessage(line("<#b8ffde>已取出 <#ffd166>" + amount + " <#b8ffde>枚水晶币，请从容器中拿取。"));
    }

    @EventHandler(ignoreCancelled = true)
    public void onClick(InventoryClickEvent event) {
        Inventory inventory = event.getView().getTopInventory();
        if (!(inventory.getHolder() instanceof Holder) || !(event.getWhoClicked() instanceof Player player)) return;
        int slot = event.getRawSlot();
        ClickType click = event.getClick();
        if (slot == DEPOSIT) {
            event.setCancelled(true);
            if (click == ClickType.LEFT || click == ClickType.RIGHT) deposit(player, inventory);
        } else if (slot == WITHDRAW) {
            event.setCancelled(true);
            if (click == ClickType.LEFT) withdraw(player, inventory, 1);
            else if (click == ClickType.RIGHT) withdraw(player, inventory, Mlcitems.money_coin.getMaxStackSize());
        } else if (slot >= 0 && slot < SIZE) {
            if (!isStorage(slot) || (click != ClickType.LEFT && click != ClickType.RIGHT)
                    || (event.getCursor() != null && !event.getCursor().getType().isAir() && !isCoin(event.getCursor()))) {
                event.setCancelled(true);
            }
        } else if (event.getClickedInventory() == player.getInventory()) {
            if (click == ClickType.SHIFT_LEFT || click == ClickType.SHIFT_RIGHT) {
                event.setCancelled(true);
                ItemStack item = event.getCurrentItem();
                if (!isCoin(item)) return;
                int amount = Math.min(item.getAmount(), capacity(inventory));
                if (amount == 0) return;
                addCoins(inventory, amount);
                int remaining = item.getAmount() - amount;
                if (remaining == 0) event.setCurrentItem(null);
                else {
                    item.setAmount(remaining);
                    event.setCurrentItem(item);
                }
            } else if (click != ClickType.LEFT && click != ClickType.RIGHT) {
                event.setCancelled(true);
            }
        } else event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onDrag(InventoryDragEvent event) {
        if (!(event.getView().getTopInventory().getHolder() instanceof Holder)) return;
        for (int slot : event.getRawSlots()) {
            if (slot < SIZE && (!isStorage(slot) || !isCoin(event.getOldCursor()))) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        if (!(inventory.getHolder() instanceof Holder) || !(event.getPlayer() instanceof Player player)) return;
        boolean hasCoins = false;
        for (int slot = 0; slot < SIZE; slot++) {
            if (isStorage(slot) && isCoin(inventory.getItem(slot))) {
                hasCoins = true;
                break;
            }
        }
        if (hasCoins) deposit(player, inventory);

        // 存入失败或存在其他物品时，先退还背包，放不下的才掉落。
        boolean returned = false;
        for (int slot = 0; slot < SIZE; slot++) {
            if (!isStorage(slot)) continue;
            ItemStack item = inventory.getItem(slot);
            if (item == null || item.getType().isAir()) continue;
            inventory.setItem(slot, null);
            returned = true;
            for (ItemStack leftover : player.getInventory().addItem(item).values()) {
                player.getWorld().dropItemNaturally(player.getLocation(), leftover);
            }
        }
        if (returned) player.sendMessage(line("<#ffb4a8>未存入的物品已退还，背包放不下的物品会掉落。"));
    }

    public static void closeAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getOpenInventory().getTopInventory().getHolder() instanceof Holder) player.closeInventory();
        }
    }
}
