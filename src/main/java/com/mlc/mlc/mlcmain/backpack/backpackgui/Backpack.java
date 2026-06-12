package com.mlc.mlc.mlcmain.backpack.backpackgui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Nameable;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Container;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Backpack core logic — directly modifies the shulker box in hand via BlockStateMeta.
 * No external YAML files; contents live in the item's NBT, just like a vanilla shulker box.
 */
public class Backpack {

    /** PDC key marking the backpack item that is currently open in a GUI. */
    public static final NamespacedKey IS_OPEN_KEY = NamespacedKey.fromString("mlc:is_open");

    /** Item model that identifies the backpack. */
    public static final String ITEM_MODEL = "mlc:mlc_backpack";

    /** Tracks transient inventories → (player UUID, backpack ItemStack). */
    public static final Map<Inventory, Map.Entry<UUID, ItemStack>> openInventories =
        Collections.synchronizedMap(new HashMap<>());

    /** Backpack inventory title fallback (styled). */
    public static final Component TITLE = Component.text("背包")
        .decoration(TextDecoration.ITALIC, false)
        .decoration(TextDecoration.BOLD, true)
        .color(TextColor.fromHexString("#eea468"));

    // ──────────────────────────────────────────────
    //  Backpack identification
    // ──────────────────────────────────────────────

    /** Returns true when the item is our backpack (by item model). */
    public static boolean isBackpackItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        var meta = item.getItemMeta();
        if (!meta.hasItemModel()) return false;
        var model = meta.getItemModel();
        return model != null && ITEM_MODEL.equals(model.toString());
    }

    // ──────────────────────────────────────────────
    //  Storage-item detection
    // ──────────────────────────────────────────────

    /**
     * Returns true when the item is any kind of container we should prevent
     * nesting inside another backpack (our backpack, vanilla shulker boxes, etc.).
     */
    public static boolean isStorageItem(ItemStack item) {
        if (item == null || item.getType().isAir()) return false;

        // Our own backpack
        if (isBackpackItem(item)) return true;

        // Any item whose BlockStateMeta wraps a ShulkerBox (includes vanilla shulker boxes)
        if (item.getItemMeta() instanceof BlockStateMeta meta
            && meta.getBlockState() instanceof ShulkerBox) {
            return true;
        }

        // Material-name fallback for shulker boxes without meta yet
        return item.getType().name().endsWith("_SHULKER_BOX");
    }

    /**
     * Returns true when the item's PDC {@code is_open} flag is set to true.
     */
    public static boolean isCurrentlyOpen(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        return Boolean.TRUE.equals(
            item.getPersistentDataContainer().get(IS_OPEN_KEY, PersistentDataType.BOOLEAN));
    }

    // ──────────────────────────────────────────────
    //  Open / close
    // ──────────────────────────────────────────────

    /**
     * Opens the backpack GUI for the player.
     * The item must have {@link BlockStateMeta} wrapping a {@link Container}.
     *
     * @return true if the inventory was opened successfully.
     */
    public static boolean openBackpack(Player player, ItemStack item) {
        // Validate the item carries a container block state
        if (!(item.getItemMeta() instanceof BlockStateMeta meta)
            || !(meta.getBlockState() instanceof Container container)) {
            return false;
        }

        // Only single items can be opened (stacked would share NBT → corruption)
        if (item.getAmount() != 1) {
            player.sendActionBar(Component.text("堆叠的背包无法打开！"));
            return false;
        }

        // Resolve inventory title: custom display name → default item name → fallback
        Component title = TITLE;
        if (meta.hasDisplayName()) title = meta.displayName();
        else if (meta.hasItemName()) title = meta.itemName();

        // Sync the name onto the block state so it persists in the item
        if (meta.getBlockState() instanceof Nameable nameable) {
            nameable.customName(title);
        }

        // Create a transient inventory with the resolved title
        Inventory transientInv = Bukkit.createInventory(
            player, container.getInventory().getType(), title);
        transientInv.setContents(container.getInventory().getContents());

        // Mark this item as "the one that's open" (clear any stale flags first)
        clearAllOpenFlags(player);
        item.editMeta(m ->
            m.getPersistentDataContainer().set(IS_OPEN_KEY, PersistentDataType.BOOLEAN, true));

        // Track for later save / close
        openInventories.put(transientInv,
            new AbstractMap.SimpleEntry<>(player.getUniqueId(), item));

        player.openInventory(transientInv);
        return true;
    }

    // ──────────────────────────────────────────────
    //  Save contents back into the item
    // ──────────────────────────────────────────────

    /**
     * Writes the transient inventory contents back into the backpack item's
     * {@link BlockStateMeta}.  Called after every click / drag so the item is
     * always up-to-date.
     *
     * If the item was moved to a different slot while the GUI was open, this
     * method locates it via the {@code is_open} PDC flag.
     */
    public static void saveContents(ItemStack item, Inventory inventory) {
        // ── Relocate: the item may have been moved while the GUI was open ──
        if (item.getType().isAir() && inventory.getHolder() instanceof Player player) {
            // 1) Check the cursor (player might be holding the backpack)
            ItemStack cursor = player.getOpenInventory().getCursor();
            if (cursor != null && isCurrentlyOpen(cursor)) {
                item = cursor;
                openInventories.put(inventory,
                    new AbstractMap.SimpleEntry<>(player.getUniqueId(), item));
            } else {
                // 2) Scan the player's inventory for the open item
                for (ItemStack invItem : player.getInventory().getContents()) {
                    if (invItem != null && isCurrentlyOpen(invItem)) {
                        item = invItem;
                        openInventories.put(inventory,
                            new AbstractMap.SimpleEntry<>(player.getUniqueId(), item));
                        break;
                    }
                }
            }
        }

        // ── Write transient inventory → BlockStateMeta ──
        final ItemStack finalItem = item;
        finalItem.editMeta(BlockStateMeta.class, meta -> {
            if (meta.getBlockState() instanceof Container container) {
                container.getInventory().setContents(inventory.getContents());
                meta.setBlockState(container);
            }
        });
    }

    /**
     * Clears the {@code is_open} flag on every item in the player's inventory.
     */
    public static void clearAllOpenFlags(Player player) {
        for (ItemStack invItem : player.getInventory().getContents()) {
            if (invItem != null && isCurrentlyOpen(invItem)) {
                invItem.editMeta(m ->
                    m.getPersistentDataContainer().set(IS_OPEN_KEY, PersistentDataType.BOOLEAN, false));
            }
        }
    }
}
