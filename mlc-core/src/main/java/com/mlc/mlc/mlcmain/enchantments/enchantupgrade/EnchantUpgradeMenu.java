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
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import static com.mlc.mlc.Mlc.miniMessage;

/** 多方块附魔升级界面。所有库存和玩家操作均在服务端主线程执行。 */
public final class EnchantUpgradeMenu implements Listener {
    private static final int INSTRUCTIONS = 4;
    private static final int EQUIPMENT = 11;
    private static final int COST_OR_REPAIR = 22;
    private static final List<Integer> RUNE_SLOTS = List.of(28, 29, 30);
    private static final int PREVIOUS = 6;
    private static final int NEXT = 51;
    private static final List<Integer> ENCHANT_SLOTS = List.of(15, 24, 33, 42, 16, 25, 34, 43);
    private static final List<Integer> CORNERS = List.of(0, 8, 45, 53);
    private static final String UPGRADE_RUNE = "upgrade_rune";
    private static final String PROTECTION_RUNE = "protection_rune";
    private static final String REPAIR_RUNE = "repair_rune";
    private static final String STABILITY_RUNE = "stability_rune";
    private static final List<String> RUNE_TYPES = List.of(UPGRADE_RUNE, PROTECTION_RUNE, REPAIR_RUNE, STABILITY_RUNE);

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
    }

    @EventHandler(ignoreCancelled = true)
    public void onClick(InventoryClickEvent event) {
        if (!(event.getView().getTopInventory().getHolder() instanceof Menu menu)
                || !(event.getWhoClicked() instanceof Player player)) return;
        int raw = event.getRawSlot();
        if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) {
            event.setCancelled(true);
            shiftTransfer(event, menu, player);
            return;
        }
        if (raw >= menu.inventory.getSize()) {
            // 快捷键和双击收集可能绕过输入槽校验。
            if (event.getClick() != ClickType.LEFT && event.getClick() != ClickType.RIGHT) event.setCancelled(true);
            return;
        }
        event.setCancelled(true);
        if (raw < 0 || (event.getClick() != ClickType.LEFT && event.getClick() != ClickType.RIGHT)) return;
        if (isInputSlot(raw)) {
            ItemStack previousEquipment = menu.inventory.getItem(EQUIPMENT);
            moveInput(event, menu, raw);
            if (raw == EQUIPMENT && !Objects.equals(previousEquipment, menu.inventory.getItem(EQUIPMENT))) menu.page = 0;
            render(menu);
            return;
        }
        if (raw == COST_OR_REPAIR && UpgradeItems.isType(menu.inventory.getItem(EQUIPMENT), "broken_item")) {
            repair(player, menu);
            return;
        }

        int position = ENCHANT_SLOTS.indexOf(raw);
        if (position >= 0) {
            int index = menu.page * ENCHANT_SLOTS.size() + position;
            if (index < menu.enchantments.size()) upgrade(player, menu, menu.enchantments.get(index));
        } else if (raw == PREVIOUS && menu.page > 0) {
            menu.page--;
            render(menu);
        } else if (raw == NEXT && (menu.page + 1) * ENCHANT_SLOTS.size() < menu.enchantments.size()) {
            menu.page++;
            render(menu);
        }
    }

    private static void shiftTransfer(InventoryClickEvent event, Menu menu, Player player) {
        ItemStack source = event.getCurrentItem();
        if (source == null || source.isEmpty()) return;

        Inventory inv = menu.inventory;
        int raw = event.getRawSlot();
        int amount = source.getAmount();
        int remaining;
        if (isInputSlot(raw)) {
            ItemStack leftover = player.getInventory().addItem(source.clone()).get(0);
            remaining = leftover == null ? 0 : leftover.getAmount();
            if (raw == EQUIPMENT && remaining < amount) menu.page = 0;
        } else if (raw >= inv.getSize() && event.getClickedInventory() == event.getView().getBottomInventory()) {
            if (isRune(source)) {
                remaining = insertRunes(inv, source);
            } else {
                ItemStack equipment = inv.getItem(EQUIPMENT);
                if ((equipment != null && !equipment.isEmpty()) || !accepts(EQUIPMENT, source)) return;
                inv.setItem(EQUIPMENT, source.asOne());
                remaining = amount - 1;
                menu.page = 0;
            }
        } else {
            return;
        }
        if (remaining == amount) return;
        event.setCurrentItem(remaining == 0 ? null : source.asQuantity(remaining));
        render(menu);
    }

    private static int insertRunes(Inventory inventory, ItemStack source) {
        int remaining = source.getAmount();
        int limit = Math.min(source.getMaxStackSize(), inventory.getMaxStackSize());
        for (int slot : RUNE_SLOTS) {
            ItemStack current = inventory.getItem(slot);
            if (current == null || !current.isSimilar(source) || current.getAmount() >= limit) continue;
            int moved = Math.min(remaining, limit - current.getAmount());
            inventory.setItem(slot, current.asQuantity(current.getAmount() + moved));
            remaining -= moved;
            if (remaining == 0) return 0;
        }
        for (int slot : RUNE_SLOTS) {
            ItemStack current = inventory.getItem(slot);
            if (current != null && !current.isEmpty()) continue;
            int moved = Math.min(remaining, limit);
            inventory.setItem(slot, source.asQuantity(moved));
            remaining -= moved;
            if (remaining == 0) return 0;
        }
        return remaining;
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
        for (int slot = 0; slot < menu.inventory.getSize(); slot++) {
            if (!isInputSlot(slot)) continue;
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
        if (slot == EQUIPMENT) return UpgradeItems.isType(item, "broken_item") || !item.getEnchantments().isEmpty();
        return RUNE_SLOTS.contains(slot) && isRune(item);
    }

    private static boolean isRune(ItemStack item) {
        return RUNE_TYPES.stream().anyMatch(type -> UpgradeItems.isType(item, type));
    }

    private static boolean isInputSlot(int slot) {
        return slot == EQUIPMENT || RUNE_SLOTS.contains(slot);
    }

    private static void render(Menu menu) {
        Inventory inv = menu.inventory;
        ItemStack frame = icon(Material.GRAY_STAINED_GLASS_PANE, " ");
        ItemStack accent = icon(Material.CYAN_STAINED_GLASS_PANE, " ");
        ItemStack emptyEnchant = icon(Material.LIGHT_GRAY_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < inv.getSize(); i++) {
            if (isInputSlot(i)) continue;
            if (CORNERS.contains(i)) inv.setItem(i, accent);
            else if (ENCHANT_SLOTS.contains(i)) inv.setItem(i, emptyEnchant);
            else inv.setItem(i, frame);
        }
        ItemStack equipment = inv.getItem(EQUIPMENT);
        if (UpgradeItems.isType(equipment, "broken_item")) {
            inv.setItem(INSTRUCTIONS, icon(Material.KNOWLEDGE_BOOK, "<#b8ffde>装备修复说明",
                    "<#aab8b0>修复符文可放入左下任意一个空格",
                    "<#aab8b0>按住 Shift 点击可快速转移装备或符文",
                    "<#aab8b0>将鼠标移到铁砧上查看修复条件"));
            long now = System.currentTimeMillis();
            if (UpgradeItems.refreshLore(equipment, now)) inv.setItem(EQUIPMENT, equipment);
            long readyAt = UpgradeItems.readyAt(equipment);
            String repairStatus = readyAt < 0 ? "<#ffb4a8>破损装备数据无效"
                    : readyAt <= now ? "<#b8ffde>✦ 修复时间已到，点击修复"
                    : "<#aab8b0>距离自然恢复约 <#ffd166>" + ((readyAt - now + 59999) / 60000) + " <#aab8b0>分钟";
            if (readyAt > now) {
                inv.setItem(COST_OR_REPAIR, icon(Material.ANVIL, "<#b8ffde>修复破损装备", repairStatus,
                        "<#aab8b0>提前修复消耗 <#ffd166>" + UpgradeRules.REPAIR_RUNES_PER_ITEM + " <#aab8b0>个修复符文",
                        "<#aab8b0>已放入 <#d8e3dc>" + countRunes(inv, REPAIR_RUNE) + " <#aab8b0>个，点击铁砧修复"));
            } else {
                inv.setItem(COST_OR_REPAIR, icon(Material.ANVIL, "<#b8ffde>修复破损装备", repairStatus));
            }
            menu.enchantments = List.of();
            menu.page = 0;
            return;
        }
        inv.setItem(INSTRUCTIONS, icon(Material.KNOWLEDGE_BOOK, "<#b8ffde>附魔升级说明",
                "<#aab8b0>把已附魔的装备放入左侧空格",
                "<#aab8b0>符文可放入左下任意一个空格",
                "<#aab8b0>按住 Shift 点击可快速转移装备或符文",
                "<#aab8b0>点击右侧魔咒尝试升级"));
        Map<Enchantment, Integer> appliedEnchantments = equipment == null ? Map.of() : equipment.getEnchantments();
        menu.enchantments = appliedEnchantments.keySet().stream()
                .filter(enchantment -> !NegativeEnchantments.isNegative(enchantment))
                .sorted(Comparator.comparing(e -> e.getKey().toString())).toList();
        int pageSize = ENCHANT_SLOTS.size();
        int pages = Math.max(1, (menu.enchantments.size() + pageSize - 1) / pageSize);
        menu.page = Math.min(menu.page, pages - 1);
        int positiveLevels = positiveEnchantmentLevels(appliedEnchantments);
        inv.setItem(COST_OR_REPAIR, icon(Material.ENCHANTING_TABLE, "<#b8ffde>本次升级消耗",
                "<#aab8b0>经验等级 <#ffd166>" + UpgradeRules.UPGRADE_EXPERIENCE_LEVELS + " <#aab8b0>级",
                "<#aab8b0>升级符文 <#ffd166>" + UpgradeRules.UPGRADE_RUNES_PER_ATTEMPT
                        + " <#aab8b0>个（已放入 " + countRunes(inv, UPGRADE_RUNE) + " 个）",
                "<#aab8b0>升级失败时可消耗 <#ffd166>" + UpgradeRules.PROTECTION_RUNES_ON_FAILURE + " <#aab8b0>个保护符文",
                "<#aab8b0>触发负面魔咒时可消耗 <#ffd166>" + UpgradeRules.STABILITY_RUNES_PER_PREVENTION + " <#aab8b0>个稳定符文"));
        for (int i = 0; i < pageSize; i++) {
            int index = menu.page * pageSize + i;
            if (index >= menu.enchantments.size()) break;
            Enchantment enchant = menu.enchantments.get(index);
            int level = appliedEnchantments.get(enchant);
            int maxLevel = EnchantmentMaxLevel.getMaxLevel(enchant);
            if (level >= maxLevel) {
                inv.setItem(ENCHANT_SLOTS.get(i), icon(Material.BARRIER, enchantName(enchant, "<#ffb4a8>"),
                        "<#aab8b0>当前等级 <#d8e3dc>" + level + " <#71877a>/ <#d8e3dc>" + maxLevel,
                        "", "<#ffb4a8>◆ 已达到等级上限"));
                continue;
            }
            double success = UpgradeRules.successProbability(level, positiveLevels - level);
            ItemStack option = icon(Material.ENCHANTED_BOOK, enchantName(enchant, "<#b8ffde>"),
                    "<#aab8b0>当前等级 <#d8e3dc>" + level + " <#71877a>/ <#d8e3dc>" + maxLevel,
                    "<#aab8b0>升级后 <#b8ffde>" + (level + 1) + " 级",
                    "<#aab8b0>成功率 <#b8ffde>" + String.format(Locale.ROOT, "%.2f%%", success * 100),
                    "", "<#b8ffde>✦ 点击尝试升级");
            inv.setItem(ENCHANT_SLOTS.get(i), option);
        }
        if (pages > 1) {
            String pageStatus = "<#aab8b0>第 <#ffd166>" + (menu.page + 1) + " <#71877a>/ <#ffd166>" + pages + " <#aab8b0>页";
            if (menu.page > 0) inv.setItem(PREVIOUS, icon(Material.ARROW, "<#b8ffde>↑ 上一页", pageStatus));
            if (menu.page + 1 < pages) inv.setItem(NEXT, icon(Material.ARROW, "<#b8ffde>↓ 下一页", pageStatus));
        }
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
        if (!EnchantUpgradelistener.checkblock(menu.center)) {
            player.sendMessage(Component.text("多方块结构不完整。")); return;
        }
        if (equipment == null || equipment.getAmount() != 1) return;
        Map<Enchantment, Integer> appliedEnchantments = equipment.getEnchantments();
        int level = appliedEnchantments.getOrDefault(enchant, 0);
        if (level <= 0 || NegativeEnchantments.isNegative(enchant)) return;
        if (level >= EnchantmentMaxLevel.getMaxLevel(enchant)) {
            player.sendMessage(Component.text("这个魔咒已经达到等级上限。")); return;
        }
        if (countRunes(inv, UPGRADE_RUNE) < UpgradeRules.UPGRADE_RUNES_PER_ATTEMPT) {
            player.sendMessage(Component.text("请放入足够的升级符文。")); return;
        }
        if (player.getLevel() < UpgradeRules.UPGRADE_EXPERIENCE_LEVELS) {
            player.sendMessage(Component.text("经验等级不足。")); return;
        }
        int otherPositiveLevels = positiveEnchantmentLevels(appliedEnchantments) - level;
        boolean failure = ThreadLocalRandom.current().nextDouble() >= UpgradeRules.successProbability(level, otherPositiveLevels);
        boolean protectedFailure = failure
                && countRunes(inv, PROTECTION_RUNE) >= UpgradeRules.PROTECTION_RUNES_ON_FAILURE;
        // 先准备结果，成功后才扣除输入，避免结果创建失败导致损失。
        ItemStack result = equipment.clone();
        if (!failure) result.addUnsafeEnchantment(enchant, level + 1);
        boolean prevented = false;
        Enchantment negative = null;
        if (ThreadLocalRandom.current().nextDouble() < UpgradeRules.NEGATIVE_ENCHANTMENT_CHANCE) {
            negative = NegativeEnchantments.randomApplicable(result, ThreadLocalRandom.current());
            if (negative != null) {
                prevented = countRunes(inv, STABILITY_RUNE) >= UpgradeRules.STABILITY_RUNES_PER_PREVENTION;
                if (!prevented) result.addUnsafeEnchantment(negative, 1);
            }
        }
        if (failure && !protectedFailure) {
            int total = appliedEnchantments.values().stream().mapToInt(Integer::intValue).sum();
            result = UpgradeItems.breakItem(result, total, System.currentTimeMillis());
        }
        inv.setItem(EQUIPMENT, result);
        consumeRunes(inv, UPGRADE_RUNE, UpgradeRules.UPGRADE_RUNES_PER_ATTEMPT);
        if (protectedFailure) consumeRunes(inv, PROTECTION_RUNE, UpgradeRules.PROTECTION_RUNES_ON_FAILURE);
        if (prevented) consumeRunes(inv, STABILITY_RUNE, UpgradeRules.STABILITY_RUNES_PER_PREVENTION);
        player.setLevel(player.getLevel() - UpgradeRules.UPGRADE_EXPERIENCE_LEVELS);
        player.playSound(player.getLocation(), failure ? Sound.BLOCK_ANVIL_DESTROY : Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1, 1);
        player.sendMessage(Component.text(!failure ? "附魔升级成功。" : protectedFailure ? "升级失败，保护符文保护了装备。" : "升级失败，装备已破损。"));
        if (prevented) player.sendMessage(Component.text("稳定符文阻止了负面魔咒。"));
        else if (negative != null) player.sendMessage(Component.text("装备获得了负面魔咒：").append(negative.description()));
        render(menu);
    }

    private static int positiveEnchantmentLevels(Map<Enchantment, Integer> enchantments) {
        return enchantments.entrySet().stream()
                .filter(entry -> !NegativeEnchantments.isNegative(entry.getKey()))
                .mapToInt(Map.Entry::getValue)
                .sum();
    }

    private static void repair(Player player, Menu menu) {
        Inventory inv = menu.inventory;
        ItemStack broken = inv.getItem(EQUIPMENT);
        long ready = UpgradeItems.readyAt(broken);
        if (ready < 0) { player.sendMessage(Component.text("请放入有效的破损装备。")); return; }
        long now = System.currentTimeMillis();
        boolean early = now < ready;
        if (early && countRunes(inv, REPAIR_RUNE) < UpgradeRules.REPAIR_RUNES_PER_ITEM) {
            long remaining = ready - now;
            player.sendMessage(Component.text("距离修复完成还需 " + ((remaining + 59999) / 60000) + " 分钟，或放入修复符文提前修复。"));
            render(menu);
            return;
        }
        try {
            ItemStack restored = UpgradeItems.repair(broken, now, early);
            if (restored == null) { player.sendMessage(Component.text("破损装备数据无效。")); return; }
            inv.setItem(EQUIPMENT, restored);
            if (early) consumeRunes(inv, REPAIR_RUNE, UpgradeRules.REPAIR_RUNES_PER_ITEM);
            player.sendMessage(Component.text("装备修复完成。"));
            render(menu);
        } catch (IllegalArgumentException exception) {
            player.sendMessage(Component.text("破损装备数据无法读取。"));
        }
    }

    private static int countRunes(Inventory inventory, String type) {
        int count = 0;
        for (int slot : RUNE_SLOTS) {
            ItemStack item = inventory.getItem(slot);
            if (UpgradeItems.isType(item, type)) count += item.getAmount();
        }
        return count;
    }

    private static void consumeRunes(Inventory inventory, String type, int amount) {
        for (int slot : RUNE_SLOTS) {
            ItemStack item = inventory.getItem(slot);
            if (!UpgradeItems.isType(item, type)) continue;
            int consumed = Math.min(amount, item.getAmount());
            int remaining = item.getAmount() - consumed;
            if (remaining == 0) inventory.setItem(slot, null);
            else {
                item.setAmount(remaining);
                inventory.setItem(slot, item);
            }
            amount -= consumed;
            if (amount == 0) return;
        }
    }

    /** 停服或插件关闭时先归还界面中的输入物品。 */
    public static void closeAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getOpenInventory().getTopInventory().getHolder() instanceof Menu) player.closeInventory();
        }
    }
}
