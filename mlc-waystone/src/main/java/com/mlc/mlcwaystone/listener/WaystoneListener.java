package com.mlc.mlcwaystone.listener;

import com.mlc.mlcwaystone.Mlcwaystone;
import com.mlc.mlcwaystone.WaystoneData;
import com.mlc.mlcwaystone.WaystoneGUI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

import static com.mlc.mlcwaystone.Mlcwaystone.*;

public class WaystoneListener implements Listener {

    // ==================== Waystone Creation ====================

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Block placedBlock = event.getBlock();
        Material type = placedBlock.getType();
        Player player = event.getPlayer();

        // Case A: Placed SOUL_LANTERN or BEACON on top of LODESTONE
        if (type == Material.SOUL_LANTERN || type == Material.BEACON) {
            Block blockBelow = placedBlock.getRelative(BlockFace.DOWN);
            if (blockBelow.getType() == Material.LODESTONE) {
                registerWaystone(player, blockBelow, placedBlock);
                return;
            }
        }

        // Case B: Placed LODESTONE under an existing SOUL_LANTERN or BEACON
        if (type == Material.LODESTONE) {
            Block blockAbove = placedBlock.getRelative(BlockFace.UP);
            if (blockAbove.getType() == Material.SOUL_LANTERN || blockAbove.getType() == Material.BEACON) {
                registerWaystone(player, placedBlock, blockAbove);
            }
        }
    }

    /**
     * Register or update a waystone. Called when the structure (lodestone + top block) is complete.
     */
    private void registerWaystone(Player player, Block lodestoneBlock, Block topBlock) {
        Location lodestoneLoc = lodestoneBlock.getLocation();
        String waystoneType = (topBlock.getType() == Material.SOUL_LANTERN) ? "PRIVATE" : "PUBLIC";

        WaystoneData existing = getWaystoneAt(lodestoneLoc);

        if (existing != null) {
            // Update existing waystone type/owner
            existing.setType(waystoneType);
            existing.setOwner(player.getUniqueId());
            existing.setOwnerName(player.getName());
            saveData();
            String typeName = waystoneType.equals("PRIVATE") ? "私人" : "公共";
            player.sendMessage(Component.text("传送石碑类型已更新为" + typeName)
                    .color(TextColor.fromHexString("#00ff33")));
            return;
        }

        // Create new waystone
        UUID id = UUID.randomUUID();
        String defaultIconType = topBlock.getType().toString();
        String defaultIconName = "未命名传送点";

        WaystoneData data = new WaystoneData(
                id,
                player.getUniqueId(),
                player.getName(),
                waystoneType,
                lodestoneLoc,
                defaultIconType,
                defaultIconName,
                System.currentTimeMillis()
        );

        addWaystone(data);

        String typeName = waystoneType.equals("PRIVATE") ? "私人" : "公共";
        player.sendMessage(Component.text("成功创建" + typeName + "传送石碑")
                .color(TextColor.fromHexString("#00ff33")));
        instance.getLogger().info("玩家 " + player.getName() + " 创建了" + typeName + "传送石碑"
                + " 位于 " + lodestoneLoc.getWorld().getName()
                + " (" + lodestoneLoc.getBlockX() + ", " + lodestoneLoc.getBlockY() + ", " + lodestoneLoc.getBlockZ() + ")");
    }

    // ==================== Waystone Destruction ====================

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Block brokenBlock = event.getBlock();
        Player player = event.getPlayer();

        WaystoneData waystone = null;
        Block lodestoneBlock = null;

        // Case 1: Breaking the LODESTONE itself
        if (brokenBlock.getType() == Material.LODESTONE) {
            waystone = getWaystoneAt(brokenBlock.getLocation());
            lodestoneBlock = brokenBlock;
        }
        // Case 2: Breaking the SOUL_LANTERN or BEACON on top of a LODESTONE
        else if (brokenBlock.getType() == Material.SOUL_LANTERN || brokenBlock.getType() == Material.BEACON) {
            Block blockBelow = brokenBlock.getRelative(BlockFace.DOWN);
            if (blockBelow.getType() == Material.LODESTONE) {
                waystone = getWaystoneAt(blockBelow.getLocation());
                lodestoneBlock = blockBelow;
            }
        }

        if (waystone == null || lodestoneBlock == null) {
            return;
        }

        // Authorization check: owner or OP
        if (!canManage(player, waystone)) {
            event.setCancelled(true);
            player.sendMessage(Component.text("你没有权限拆除这个传送石碑")
                    .color(TextColor.fromHexString("#FF5555")));
            return;
        }

        // Remove the waystone data
        removeWaystone(waystone.getId());

        String typeName = waystone.isPrivate() ? "私人" : "公共";
        player.sendMessage(Component.text("已拆除" + typeName + "传送石碑")
                .color(TextColor.fromHexString("#00ff33")));

        instance.getLogger().info("玩家 " + player.getName() + " 拆除了" + typeName + "传送石碑"
                + " 位于 " + lodestoneBlock.getWorld().getName()
                + " (" + lodestoneBlock.getX() + ", " + lodestoneBlock.getY() + ", " + lodestoneBlock.getZ() + ")");
    }

    // ==================== Explosion Protection ====================

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityExplode(EntityExplodeEvent event) {
        // Remove waystone blocks from the explosion's block list
        event.blockList().removeIf(this::isWaystoneBlock);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockExplode(BlockExplodeEvent event) {
        // Remove waystone blocks from the explosion's block list
        event.blockList().removeIf(this::isWaystoneBlock);
    }

    // ==================== Piston Protection ====================

    @EventHandler(ignoreCancelled = true)
    public void onPistonExtend(BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks()) {
            if (isWaystoneBlock(block)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPistonRetract(BlockPistonRetractEvent event) {
        for (Block block : event.getBlocks()) {
            if (isWaystoneBlock(block)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    // ==================== GUI Open / Icon Set ====================

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onRightClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        // Only handle main hand to avoid double-firing
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        Block clickedBlock = Objects.requireNonNull(event.getClickedBlock());

        // Only care about SOUL_LANTERN or BEACON
        if (clickedBlock.getType() != Material.SOUL_LANTERN && clickedBlock.getType() != Material.BEACON) {
            return;
        }

        // Check if block below is LODESTONE and registered as a waystone
        Block blockBelow = clickedBlock.getRelative(BlockFace.DOWN);
        if (blockBelow.getType() != Material.LODESTONE) {
            return;
        }

        WaystoneData waystone = getWaystoneAt(blockBelow.getLocation());
        if (waystone == null) {
            return; // Not a registered waystone
        }

        Player player = event.getPlayer();
        event.setCancelled(true); // Prevent normal block interaction

        // Shift + right-click with item in hand -> set icon
        if (player.isSneaking() && !player.getInventory().getItemInMainHand().isEmpty()) {
            // Authorization check
            if (!canManage(player, waystone)) {
                player.sendMessage(Component.text("你没有权限设置此传送石碑的图标")
                        .color(TextColor.fromHexString("#FF5555")));
                return;
            }

            ItemStack heldItem = player.getInventory().getItemInMainHand();
            ItemMeta heldMeta = heldItem.getItemMeta();

            String iconType = heldItem.getType().toString();
            String iconName = PlainTextComponentSerializer.plainText().serialize(
                    heldMeta.hasItemName() ? heldMeta.itemName() : Component.text(heldItem.getType().toString())
            );

            // If the item has a custom display name, use it
            if (heldMeta.hasDisplayName()) {
                iconName = PlainTextComponentSerializer.plainText().serialize(
                        Objects.requireNonNull(heldMeta.displayName())
                );
            }

            waystone.setIconType(iconType);
            waystone.setIconName(iconName);
            saveData();

            player.sendMessage(Component.text("传送石碑图标已更新为: " + iconName)
                    .color(TextColor.fromHexString("#00ff33")));
            return;
        }

        // Normal right-click -> open GUI
        WaystoneGUI.open(player);
    }

    // ==================== GUI Click Handling ====================

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().equals(event.getView().getTopInventory()))) {
            return;
        }

        if (!WaystoneGUI.openInvs.contains(event.getInventory())) {
            return;
        }

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        int slot = event.getRawSlot();

        if (slot < 0 || slot >= event.getInventory().getSize()) {
            return;
        }

        // Pagination: previous page
        if (slot == WaystoneGUI.SLOT_PREV) {
            Integer currentPage = WaystoneGUI.playerPageMap.get(player);
            if (currentPage != null && currentPage > 1) {
                WaystoneGUI.open(player, currentPage - 1);
            }
            return;
        }

        // Pagination: next page
        if (slot == WaystoneGUI.SLOT_NEXT) {
            Integer currentPage = WaystoneGUI.playerPageMap.get(player);
            if (currentPage != null) {
                WaystoneGUI.open(player, currentPage + 1);
            }
            return;
        }

        // Clicked on a waystone entry? (slots 0-44)
        if (slot >= 0 && slot < 45) {
            UUID waystoneId = WaystoneGUI.getWaystoneIdAtSlot(player, slot);
            if (waystoneId == null) {
                return;
            }

            WaystoneData data = waystoneDataMap.get(waystoneId);
            if (data == null) {
                return;
            }

            // Teleport the player
            Location loc = data.getLocation();
            Location teleportDest = loc.clone().add(0.5, 1, 0.5);
            teleportDest.setYaw(0);
            teleportDest.setPitch(0);

            player.closeInventory();
            player.teleport(teleportDest);
            player.sendMessage(Component.text("已传送到: " + data.getIconName())
                    .color(TextColor.fromHexString("#00ff33")));
        }
    }

    // ==================== GUI Close Cleanup ====================

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (WaystoneGUI.openInvs.contains(event.getInventory())) {
            WaystoneGUI.openInvs.remove(event.getInventory());
            if (event.getPlayer() instanceof Player player) {
                WaystoneGUI.cleanup(player);
            }
        }
    }

    // ==================== Helpers ====================

    /**
     * Check if a block is part of a registered waystone structure.
     * Covers both the LODESTONE base and the SOUL_LANTERN/BEACON top block.
     */
    private boolean isWaystoneBlock(Block block) {
        // Check if this block is a registered LODESTONE
        if (block.getType() == Material.LODESTONE) {
            if (getWaystoneAt(block.getLocation()) != null) {
                return true;
            }
        }
        // Check if this block is the top block of a registered waystone
        if (block.getType() == Material.SOUL_LANTERN || block.getType() == Material.BEACON) {
            Block below = block.getRelative(BlockFace.DOWN);
            if (below.getType() == Material.LODESTONE && getWaystoneAt(below.getLocation()) != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if a player can manage (remove, edit icon) a waystone.
     * Private: only the owner can manage.
     * Public: only OP can manage.
     */
    private boolean canManage(Player player, WaystoneData waystone) {
        if (player.isOp()) {
            return true;
        }
        if (waystone.isPrivate() && waystone.getOwner().equals(player.getUniqueId())) {
            return true;
        }
        return false;
    }
}
