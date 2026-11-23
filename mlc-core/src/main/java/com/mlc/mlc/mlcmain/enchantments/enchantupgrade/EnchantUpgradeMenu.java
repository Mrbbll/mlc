package com.mlc.mlc.mlcmain.enchantments.enchantupgrade;

import com.mlc.mlc.mlcmain.enchantments.EnchantmentMaxLevel;
import com.mlc.mlc.mlcmain.enchantments.listener.EnchantUpgradelistener;
import com.mlc.mlc.mlcmain.enchantments.moreenchants.NegativeEnchantments;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static com.mlc.mlc.Mlc.miniMessage;

/** 多方块附魔升级界面。所有库存和玩家操作均在服务端主线程执行。 */
public final class EnchantUpgradeMenu implements Listener {
    private static final int EQUIPMENT = 10;
    private static final int UPGRADE_RUNE = 12;
    private static final int PROTECTION_RUNE = 14;
    private static final int REPAIR = 16;
    private static final int REPAIR_RUNE = 20;
    private static final int STABILITY_RUNE = 23;
    private static final int FIRST_ENCHANT = 27;
    private static final int ENCHANTS_PER_PAGE = 18;
    private static final int PREVIOUS = 48;
    private static final int NEXT = 50;

    private static final class Menu implements InventoryHolder {
        private final Block center;
        private Inventory inventory;
        private int page;
        private List<Enchantment> enchantments = List.of();

        private Menu(Block center) { this.center = center; }

        @Override public @NotNull Inventory getInventory() { return inventory; }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getHand() != EquipmentSlot.HAND) return;
        Block center = event.getClickedBlock();
        if (center == null || center.getType() != Material.SCULK_SHRIEKER || !EnchantUpgradelistener.checkblock(center)) return;
        event.setCancelled(true);
        Player player = event.getPlayer();
        Menu menu = new Menu(center);
        menu.inventory = Bukkit.createInventory(menu, 54, line("<#b8ffde>附魔升级 <#71877a>· Enchant Upgrade"));
        render(menu);
        player.openInventory(menu.inventory);
        player.sendMessage(Component.text("放入装备和升级符文，选择下方魔咒；保护符文可选。"));
    }

    @EventHandler(ignoreCancelled = true)
    public void onClick(InventoryClickEvent event) {
        if (!(event.getView().getTopInventory().getHolder() instanceof Menu menu)
                || !(event.getWhoClicked() instanceof Player player)) return;
        int raw = event.getRawSlot();
        if (raw >= menu.inventory.getSize()) {
            // 禁止跨库存移动、快捷键和双击收集，以免绕过输入槽校验。
            if (event.getClick() != ClickType.LEFT && event.getClick() != ClickType.RIGHT) event.setCancelled(true);
            return;
        }
        event.setCancelled(true);
        if (raw < 0 || (event.getClick() != ClickType.LEFT && event.getClick() != ClickType.RIGHT)) return;
        if (raw == EQUIPMENT || raw == UPGRADE_RUNE || raw == PROTECTION_RUNE || raw == REPAIR_RUNE || raw == STABILITY_RUNE) {
            moveInput(event, menu, raw);
            render(menu);
        } else if (raw == PREVIOUS && menu.page > 0) {
            menu.page--;
            render(menu);
        } else if (raw == NEXT && (menu.page + 1) * ENCHANTS_PER_PAGE < menu.enchantments.size()) {
            menu.page++;
            render(menu);
        } else if (raw == REPAIR) {
            repair(player, menu);
        } else if (raw >= FIRST_ENCHANT && raw < FIRST_ENCHANT + ENCHANTS_PER_PAGE) {
            int index = menu.page * ENCHANTS_PER_PAGE + raw - FIRST_ENCHANT;
            if (index < menu.enchantments.size()) upgrade(player, menu, menu.enchantments.get(index));
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof Menu) event.setCancelled(true);
    }

    @EventHandler
    public void onOpen(InventoryOpenEvent event) {
        long now = System.currentTimeMillis();
        refreshInventoryLore(event.getInventory(), now);
        refreshInventoryLore(event.getPlayer().getInventory(), now);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        refreshInventoryLore(event.getPlayer().getInventory(), System.currentTimeMillis());
    }

    private static void refreshInventoryLore(Inventory inventory, long now) {
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            ItemStack item = inventory.getItem(slot);
            if (UpgradeItems.refreshLore(item, now)) inventory.setItem(slot, item);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof Menu menu) || !(event.getPlayer() instanceof Player player)) return;
        for (int slot : new int[]{EQUIPMENT, UPGRADE_RUNE, PROTECTION_RUNE, REPAIR_RUNE, STABILITY_RUNE}) {
            ItemStack item = menu.inventory.getItem(slot);
            menu.inventory.setItem(slot, null);
            if (item == null || item.getType().isAir()) continue;
            for (ItemStack remainder : player.getInventory().addItem(item).values()) {
                player.getWorld().dropItemNaturally(player.getLocation(), remainder);
            }
        }
    }

    private static void moveInput(InventoryClickEvent event, Menu menu, int slot) {
        ItemStack cursor = event.getWhoClicked().getItemOnCursor();
        ItemStack current = menu.inventory.getItem(slot);
        boolean emptyCursor = cursor.getType().isAir();
        if (!emptyCursor && !accepts(slot, cursor)) return;
        if (emptyCursor) {
            if (current == null || current.getType().isAir()) return;
            if (event.getClick() == ClickType.RIGHT && current.getAmount() > 1) {
                int amount = (current.getAmount() + 1) / 2;
                ItemStack taken = current.clone();
                taken.setAmount(amount);
                current.setAmount(current.getAmount() - amount);
                menu.inventory.setItem(slot, current);
                event.getWhoClicked().setItemOnCursor(taken);
            } else {
                menu.inventory.setItem(slot, null);
                event.getWhoClicked().setItemOnCursor(current);
            }
            return;
        }
        if (current == null || current.getType().isAir()) {
            int amount = slot == EQUIPMENT || event.getClick() == ClickType.RIGHT ? 1 : cursor.getAmount();
            ItemStack placed = cursor.clone();
            placed.setAmount(amount);
            menu.inventory.setItem(slot, placed);
            cursor.setAmount(cursor.getAmount() - amount);
            event.getWhoClicked().setItemOnCursor(cursor);
        } else if (slot != EQUIPMENT && current.isSimilar(cursor)) {
            int amount = Math.min(cursor.getAmount(), current.getMaxStackSize() - current.getAmount());
            if (event.getClick() == ClickType.RIGHT) amount = Math.min(amount, 1);
            current.setAmount(current.getAmount() + amount);
            cursor.setAmount(cursor.getAmount() - amount);
            menu.inventory.setItem(slot, current);
            event.getWhoClicked().setItemOnCursor(cursor);
        } else if (event.getClick() == ClickType.LEFT && (slot != EQUIPMENT || cursor.getAmount() == 1)) {
            menu.inventory.setItem(slot, cursor.clone());
            event.getWhoClicked().setItemOnCursor(current);
        }
    }

    private static boolean accepts(int slot, ItemStack item) {
        return switch (slot) {
            case UPGRADE_RUNE -> UpgradeItems.isType(item, "upgrade_rune");
            case PROTECTION_RUNE -> UpgradeItems.isType(item, "protection_rune");
            case REPAIR_RUNE -> UpgradeItems.isType(item, "repair_rune");
            case STABILITY_RUNE -> UpgradeItems.isType(item, "stability_rune");
            case EQUIPMENT -> UpgradeItems.isType(item, "broken_item") || !item.getEnchantments().isEmpty();
            default -> false;
        };
    }

    private static void render(Menu menu) {
        Inventory inv = menu.inventory;
        ItemStack frame = icon(Material.GRAY_STAINED_GLASS_PANE, " ");
        ItemStack accent = icon(Material.CYAN_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < inv.getSize(); i++) {
            if (i != EQUIPMENT && i != UPGRADE_RUNE && i != PROTECTION_RUNE && i != REPAIR_RUNE && i != STABILITY_RUNE) {
                inv.setItem(i, i == 0 || i == 8 || i == 45 || i == 53 ? accent : frame);
            }
        }
        inv.setItem(1, icon(Material.DIAMOND_CHESTPLATE, "<#b8ffde>装备槽",
                "<#aab8b0>在下方放入已附魔装备", "<#71877a>破损装备也可放在此处修复"));
        inv.setItem(3, icon(Material.LAPIS_LAZULI, "<#b8ffde>升级符文槽",
                "<#aab8b0>每次升级消耗 <#ffd166>" + UpgradeRules.UPGRADE_RUNES_PER_ATTEMPT + " <#aab8b0>个符文"));
        inv.setItem(5, icon(Material.SHIELD, "<#b8ffde>保护符文槽 <#71877a>· 可选",
                "<#aab8b0>升级失败时消耗符文", "<#aab8b0>保护装备免于破损"));
        inv.setItem(7, icon(Material.ANVIL, "<#b8ffde>修复装备",
                "<#aab8b0>自然恢复完成后点击下方按钮", "<#aab8b0>也可使用修复符文提前修复"));
        inv.setItem(19, icon(Material.PRISMARINE_CRYSTALS, "<#b8ffde>修复符文槽 <#71877a>· 可选",
                "<#aab8b0>放入右侧槽位，可提前修复装备",
                "<#aab8b0>提前修复消耗 <#ffd166>" + UpgradeRules.REPAIR_RUNES_PER_ITEM + " <#aab8b0>个符文"));
        inv.setItem(22, icon(Material.AMETHYST_SHARD, "<#b8ffde>稳定符文槽 <#71877a>· 可选",
                "<#aab8b0>升级时有 <#ffb4a8>" + String.format(java.util.Locale.ROOT, "%.0f%%", UpgradeRules.NEGATIVE_ENCHANTMENT_CHANCE * 100) + " <#aab8b0>概率获得负面魔咒",
                "<#aab8b0>触发时消耗符文，避免获得负面魔咒"));
        ItemStack equipment = inv.getItem(EQUIPMENT);
        if (UpgradeItems.refreshLore(equipment, System.currentTimeMillis())) inv.setItem(EQUIPMENT, equipment);
        long readyAt = UpgradeItems.readyAt(equipment);
        String repairStatus = readyAt < 0 ? "<#71877a>放入破损装备以查看修复状态"
                : readyAt <= System.currentTimeMillis() ? "<#b8ffde>✦ 修复时间已到，点击修复"
                : "<#aab8b0>剩余约 <#ffd166>" + ((readyAt - System.currentTimeMillis() + 59999) / 60000) + " <#aab8b0>分钟；可用修复符文提前修复";
        inv.setItem(REPAIR, icon(Material.ANVIL, "<#b8ffde>修复破损装备", repairStatus));
        menu.enchantments = equipment == null ? List.of() : equipment.getEnchantments().keySet().stream()
                .filter(enchantment -> !NegativeEnchantments.isNegative(enchantment))
                .sorted(Comparator.comparing(e -> e.getKey().toString())).toList();
        int pages = Math.max(1, (menu.enchantments.size() + ENCHANTS_PER_PAGE - 1) / ENCHANTS_PER_PAGE);
        menu.page = Math.min(menu.page, pages - 1);
        int total = equipment == null ? 0 : equipment.getEnchantments().values().stream().mapToInt(Integer::intValue).sum();
        for (int i = 0; i < ENCHANTS_PER_PAGE; i++) {
            int index = menu.page * ENCHANTS_PER_PAGE + i;
            if (index >= menu.enchantments.size()) break;
            Enchantment enchant = menu.enchantments.get(index);
            int level = equipment.getEnchantmentLevel(enchant);
            int maxLevel = EnchantmentMaxLevel.getMaxLevel(enchant);
            if (level >= maxLevel) {
                inv.setItem(FIRST_ENCHANT + i, icon(Material.BARRIER, enchantName(enchant, "<#ffb4a8>"),
                        "<#aab8b0>当前等级 <#d8e3dc>" + level + " <#71877a>/ <#d8e3dc>" + maxLevel,
                        "", "<#ffb4a8>◆ 已达到等级上限"));
                continue;
            }
            ItemStack option = icon(Material.ENCHANTED_BOOK, enchantName(enchant, "<#b8ffde>"),
                    "<#aab8b0>当前等级 <#d8e3dc>" + level + " <#71877a>/ <#d8e3dc>" + maxLevel,
                    "<#aab8b0>升级后 <#b8ffde>" + (level + 1) + " 级",
                    "", "<#aab8b0>失败概率 <#ffb4a8>" + String.format(java.util.Locale.ROOT, "%.2f%%", UpgradeRules.failureProbability(total) * 100),
                    "<#aab8b0>破损修复 <#ffd166>" + total * UpgradeRules.REPAIR_HOURS_PER_ENCHANTMENT_LEVEL + " 小时",
                    "<#aab8b0>消耗经验 <#ffd166>" + UpgradeRules.UPGRADE_EXPERIENCE_LEVELS + " 级",
                    "", "<#b8ffde>✦ 点击尝试升级");
            inv.setItem(FIRST_ENCHANT + i, option);
        }
        inv.setItem(PREVIOUS, icon(Material.ARROW, menu.page > 0 ? "<#b8ffde>← 上一页" : "<#71877a>← 已是第一页"));
        inv.setItem(NEXT, icon(Material.ARROW, menu.page + 1 < pages ? "<#b8ffde>下一页 →" : "<#71877a>已是最后一页 →"));
        inv.setItem(49, icon(Material.BOOK, "<#b8ffde>魔咒列表",
                "<#aab8b0>第 <#ffd166>" + (menu.page + 1) + " <#71877a>/ <#ffd166>" + pages + " <#aab8b0>页",
                "<#71877a>选择装备上已有的魔咒进行升级"));
    }

    private static Component line(String text) {
        return miniMessage.deserialize("<!i>" + text);
    }

    private static Component enchantName(Enchantment enchantment, String color) {
        return miniMessage.deserialize("<!i>" + color + "<enchant>",
                Placeholder.component("enchant", enchantment.description()));
    }

    private static ItemStack icon(Material material, String title, String... lines) {
        return icon(material, line(title), lines);
    }

    private static ItemStack icon(Material material, Component title, String... lines) {
        ItemStack item = ItemStack.of(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(title);
        List<Component> lore = new ArrayList<>();
        for (String line : lines) lore.add(line(line));
        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private static void upgrade(Player player, Menu menu, Enchantment enchant) {
        Inventory inv = menu.inventory;
        ItemStack equipment = inv.getItem(EQUIPMENT);
        ItemStack rune = inv.getItem(UPGRADE_RUNE);
        if (!EnchantUpgradelistener.checkblock(menu.center)) {
            player.sendMessage(Component.text("多方块结构不完整。")); return;
        }
        if (equipment == null || equipment.getAmount() != 1 || !equipment.getEnchantments().containsKey(enchant)) return;
        int level = equipment.getEnchantmentLevel(enchant);
        if (level >= EnchantmentMaxLevel.getMaxLevel(enchant)) {
            player.sendMessage(Component.text("这个魔咒已经达到等级上限。")); return;
        }
        if (!UpgradeItems.isType(rune, "upgrade_rune") || rune.getAmount() < UpgradeRules.UPGRADE_RUNES_PER_ATTEMPT) {
            player.sendMessage(Component.text("请放入足够的升级符文。")); return;
        }
        if (player.getLevel() < UpgradeRules.UPGRADE_EXPERIENCE_LEVELS) {
            player.sendMessage(Component.text("经验等级不足。")); return;
        }
        int total = equipment.getEnchantments().values().stream().mapToInt(Integer::intValue).sum();
        if (total <= 0) return;
        boolean failure = ThreadLocalRandom.current().nextDouble() < UpgradeRules.failureProbability(total);
        ItemStack protection = inv.getItem(PROTECTION_RUNE);
        boolean protectedFailure = failure && UpgradeItems.isType(protection, "protection_rune")
                && protection.getAmount() >= UpgradeRules.PROTECTION_RUNES_ON_FAILURE;
        // 先准备结果，成功后才扣除输入，避免结果创建失败导致损失。
        ItemStack result = equipment.clone();
        if (!failure) result.addUnsafeEnchantment(enchant, level + 1);
        ItemStack stability = inv.getItem(STABILITY_RUNE);
        boolean prevented = false;
        Enchantment negative = null;
        if (ThreadLocalRandom.current().nextDouble() < UpgradeRules.NEGATIVE_ENCHANTMENT_CHANCE) {
            negative = NegativeEnchantments.randomApplicable(result, ThreadLocalRandom.current());
            if (negative != null) {
                prevented = UpgradeItems.isType(stability, "stability_rune")
                        && stability.getAmount() >= UpgradeRules.STABILITY_RUNES_PER_PREVENTION;
                if (!prevented) result.addUnsafeEnchantment(negative, 1);
            }
        }
        if (failure && !protectedFailure) result = UpgradeItems.breakItem(result, total, System.currentTimeMillis());
        inv.setItem(EQUIPMENT, result);
        consume(inv, UPGRADE_RUNE, UpgradeRules.UPGRADE_RUNES_PER_ATTEMPT);
        if (protectedFailure) consume(inv, PROTECTION_RUNE, UpgradeRules.PROTECTION_RUNES_ON_FAILURE);
        if (prevented) consume(inv, STABILITY_RUNE, UpgradeRules.STABILITY_RUNES_PER_PREVENTION);
        player.setLevel(player.getLevel() - UpgradeRules.UPGRADE_EXPERIENCE_LEVELS);
        player.playSound(player.getLocation(), failure ? Sound.BLOCK_ANVIL_DESTROY : Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1, 1);
        player.sendMessage(Component.text(!failure ? "附魔升级成功。" : protectedFailure ? "升级失败，保护符文保护了装备。" : "升级失败，装备已破损。"));
        if (prevented) player.sendMessage(Component.text("稳定符文阻止了负面魔咒。"));
        else if (negative != null) player.sendMessage(Component.text("装备获得了负面魔咒：").append(negative.description()));
        render(menu);
    }

    private static void repair(Player player, Menu menu) {
        ItemStack broken = menu.inventory.getItem(EQUIPMENT);
        long ready = UpgradeItems.readyAt(broken);
        if (ready < 0) { player.sendMessage(Component.text("请放入有效的破损装备。")); return; }
        long now = System.currentTimeMillis();
        boolean early = now < ready;
        ItemStack rune = menu.inventory.getItem(REPAIR_RUNE);
        if (early && (!UpgradeItems.isType(rune, "repair_rune") || rune.getAmount() < UpgradeRules.REPAIR_RUNES_PER_ITEM)) {
            long remaining = ready - now;
            player.sendMessage(Component.text("距离修复完成还需 " + ((remaining + 59999) / 60000) + " 分钟，或放入修复符文提前修复。"));
            render(menu);
            return;
        }
        try {
            ItemStack restored = UpgradeItems.repair(broken, now, early);
            if (restored == null) { player.sendMessage(Component.text("破损装备数据无效。")); return; }
            menu.inventory.setItem(EQUIPMENT, restored);
            if (early) consume(menu.inventory, REPAIR_RUNE, UpgradeRules.REPAIR_RUNES_PER_ITEM);
            player.sendMessage(Component.text("装备修复完成。"));
            render(menu);
        } catch (IllegalArgumentException exception) {
            player.sendMessage(Component.text("破损装备数据无法读取。"));
        }
    }

    private static void consume(Inventory inventory, int slot, int amount) {
        ItemStack item = inventory.getItem(slot);
        if (item.getAmount() == amount) inventory.setItem(slot, null);
        else {
            item.setAmount(item.getAmount() - amount);
            inventory.setItem(slot, item);
        }
    }

    /** 停服或插件关闭时先归还界面中的输入物品。 */
    public static void closeAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getOpenInventory().getTopInventory().getHolder() instanceof Menu) player.closeInventory();
        }
    }
}
