package com.mlc.mlc.mlcmain.backpack.listener;

import com.mlc.mlc.mlcmain.backpack.backpackgui.Backpack;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataType;

import java.util.Map;
import java.util.UUID;

/**
 * Listener that drives backpack interaction.
 *
 * Patterns extracted from vane's StorageGroup:
 * - Contents are stored directly in the item's BlockStateMeta (no YAML).
 * - After every click/drag the transient inventory is written back to the item.
 * - A PDC boolean "is_open" flag reliably tracks which item is open,
 *   even if the player moves it between slots while the GUI is active.
 * - Drop-while-open is caught to prevent duplication.
 * - Nesting storage items is denied aggressively.
 */
public class Backpacklistener implements Listener {

    // ──────────────────────────────────────────────
    //  Right-click → open
    // ──────────────────────────────────────────────

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (event.getAction() == Action.LEFT_CLICK_AIR
            || event.getAction() == Action.LEFT_CLICK_BLOCK
            || event.getAction() == Action.PHYSICAL) {
            return;
        }

        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
        if (item.isEmpty()) return;
        if (!Backpack.isBackpackItem(item)) return;

        // Prevent the shulker-box base item from being placed as a block
        event.setUseInteractedBlock(Event.Result.DENY);
        event.setUseItemInHand(Event.Result.DENY);

        Backpack.openBackpack(event.getPlayer(), item);
        event.getPlayer().swingMainHand();
    }

    // ──────────────────────────────────────────────
    //  Anti-nesting on click
    // ──────────────────────────────────────────────

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        Map.Entry<UUID, ItemStack> entry = Backpack.openInventories.get(event.getInventory());
        if (entry == null || !entry.getKey().equals(player.getUniqueId())) return;

        boolean clickedIsStorage = Backpack.isStorageItem(event.getCurrentItem());
        boolean cursorIsStorage  = Backpack.isStorageItem(event.getCursor());
        boolean clickedIsPlayerInv = event.getClickedInventory() instanceof PlayerInventory;

        boolean cancel = false;
        switch (event.getAction()) {
            case DROP_ALL_CURSOR:
            case DROP_ALL_SLOT:
            case DROP_ONE_CURSOR:
            case DROP_ONE_SLOT:
                // Drops are handled separately (onDrop)
                cancel = false;
                break;
            case PLACE_ALL:
            case PLACE_ONE:
            case PLACE_SOME:
            case SWAP_WITH_CURSOR:
                // Deny placing a storage item INTO the backpack
                cancel = cursorIsStorage && !clickedIsPlayerInv;
                break;
            case MOVE_TO_OTHER_INVENTORY:
                // Deny shift-clicking a storage item from player inv into the backpack
                cancel = clickedIsStorage && clickedIsPlayerInv;
                break;
            case PICKUP_ALL:
            case PICKUP_HALF:
            case PICKUP_ONE:
            case PICKUP_SOME:
            case COLLECT_TO_CURSOR:
                // Always allow taking items out
                cancel = false;
                break;
            default:
                // Restrictive default: no storage items may be moved at all
                cancel = clickedIsStorage || cursorIsStorage;
                break;
        }

        // Number-key hotbar swap
        if (!cancel && event.getClick() == ClickType.NUMBER_KEY) {
            ItemStack hotbarItem = player.getInventory().getItem(event.getHotbarButton());
            boolean hotbarIsStorage = Backpack.isStorageItem(hotbarItem);
            cancel = (hotbarIsStorage || clickedIsStorage) && !clickedIsPlayerInv;
        }

        if (cancel) {
            event.setCancelled(true);
        }
    }

    // ──────────────────────────────────────────────
    //  Save after every click (real-time NBT update)
    // ──────────────────────────────────────────────

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void saveAfterClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        Map.Entry<UUID, ItemStack> entry = Backpack.openInventories.get(event.getInventory());
        if (entry == null || !entry.getKey().equals(player.getUniqueId())) return;

        Backpack.saveContents(entry.getValue(), event.getInventory());
    }

    // ──────────────────────────────────────────────
    //  Anti-nesting on drag
    // ──────────────────────────────────────────────

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        Map.Entry<UUID, ItemStack> entry = Backpack.openInventories.get(event.getInventory());
        if (entry == null || !entry.getKey().equals(player.getUniqueId())) return;

        for (ItemStack newItem : event.getNewItems().values()) {
            if (Backpack.isStorageItem(newItem)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    // ──────────────────────────────────────────────
    //  Save after every drag
    // ──────────────────────────────────────────────

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void saveAfterDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        Map.Entry<UUID, ItemStack> entry = Backpack.openInventories.get(event.getInventory());
        if (entry == null || !entry.getKey().equals(player.getUniqueId())) return;

        Backpack.saveContents(entry.getValue(), event.getInventory());
    }

    // ──────────────────────────────────────────────
    //  Close — final save + clear open flag
    // ──────────────────────────────────────────────

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClose(InventoryCloseEvent event) {
        Map.Entry<UUID, ItemStack> entry = Backpack.openInventories.remove(event.getInventory());
        if (entry == null || !entry.getKey().equals(event.getPlayer().getUniqueId())) return;

        ItemStack item = entry.getValue();
        // Clear the open flag
        item.editMeta(m ->
            m.getPersistentDataContainer().set(Backpack.IS_OPEN_KEY, PersistentDataType.BOOLEAN, false));
        // Final save
        Backpack.saveContents(item, event.getInventory());
    }

    // ──────────────────────────────────────────────
    //  Drop protection — close GUI if backpack is dropped
    // ──────────────────────────────────────────────

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDrop(PlayerDropItemEvent event) {
        if (!Backpack.isStorageItem(event.getItemDrop().getItemStack())) return;

        if (Backpack.isCurrentlyOpen(event.getItemDrop().getItemStack())) {
            boolean isKnownInventory = Backpack.openInventories.containsKey(
                event.getPlayer().getOpenInventory().getTopInventory());
            if (isKnownInventory) {
                // Force-close to prevent duping
                event.getPlayer().closeInventory(InventoryCloseEvent.Reason.CANT_USE);
            } else {
                // Stale flag — fix it
                event.getItemDrop().getItemStack().editMeta(m ->
                    m.getPersistentDataContainer().set(Backpack.IS_OPEN_KEY,
                        PersistentDataType.BOOLEAN, false));
            }
        }
    }

    // ──────────────────────────────────────────────
    //  Pickup — reset open flag as safety measure
    // ──────────────────────────────────────────────

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (!Backpack.isStorageItem(event.getItem().getItemStack())) return;

        // Guard against bugged / stale open flags
        event.getItem().getItemStack().editMeta(m ->
            m.getPersistentDataContainer().set(Backpack.IS_OPEN_KEY,
                PersistentDataType.BOOLEAN, false));
    }
}
