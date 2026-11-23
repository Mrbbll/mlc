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
import org.bukkit.event.inventory.ClickType;
import net.milkbowl.vault.economy.EconomyResponse;
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
            // Handle BlueMap sync for type change
            boolean wasPublic = existing.isPublic();
            String oldType = existing.getType();

            // Update existing waystone type/owner
            existing.setType(waystoneType);
            existing.setOwner(player.getUniqueId());
            existing.setOwnerName(player.getName());
            saveData();

            // Sync BlueMap if visibility changed
            if (!wasPublic && existing.isPublic()) {
                Mlcwaystone.createBlueMapMarker(existing);
            } else if (wasPublic && existing.isPrivate()) {
                Mlcwaystone.removeBlueMapMarker(existing);
            } else if (wasPublic && existing.isPublic()) {
                Mlcwaystone.updateBlueMapMarker(existing);
            }

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

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Block brokenBlock = event.getBlock();
        Player player = event.getPlayer();

        // Match by position even if another plugin has already changed the block type.
        WaystoneData waystone = getWaystoneAt(brokenBlock.getLocation());
        boolean base = waystone != null;
        if (!base) {
            waystone = getWaystoneAt(brokenBlock.getRelative(BlockFace.DOWN).getLocation());
        }
        if (waystone == null) return;

        if (!canManage(player, waystone)) {
            if (!event.isCancelled()) {
                player.sendMessage(Component.text("你没有权限拆除这个传送石碑")
                        .color(TextColor.fromHexString("#FF5555")));
            }
            event.setCancelled(true);
        }

        WaystoneData captured = waystone;
        Material expected = base ? Material.LODESTONE
                : (waystone.isPrivate() ? Material.SOUL_LANTERN : Material.BEACON);
        // Some plugins cancel the Bukkit event and remove the block themselves. Check the
        // actual result after all event handlers and the normal block break have completed.
        instance.getServer().getScheduler().runTask(instance, () -> {
            if (waystoneDataMap.get(captured.getId()) != captured || brokenBlock.getType() == expected) return;
            removeWaystone(captured.getId());
            String typeName = captured.isPrivate() ? "私人" : "公共";
            player.sendMessage(Component.text("已拆除" + typeName + "传送石碑")
                    .color(TextColor.fromHexString("#00ff33")));
            Location loc = captured.getLocation();
            instance.getLogger().info("玩家 " + player.getName() + " 拆除了" + typeName + "传送石碑"
                    + " 位于 " + loc.getWorld().getName()
                    + " (" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ")");
        });
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
        protectFromPiston(event, event.getBlocks());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPistonRetract(BlockPistonRetractEvent event) {
        protectFromPiston(event, event.getBlocks());
    }

    private void protectFromPiston(BlockPistonEvent event, List<Block> blocks) {
        if (waystoneDataMap.isEmpty()) {
            return;
        }
        for (Block block : blocks) {
            if (isPossibleWaystoneTop(block.getX(), block.getY(), block.getZ())
                    && block.getType() == Material.SOUL_LANTERN
                    && isAboveRegisteredLodestone(block)) {
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
            waystone.setIconModel(heldMeta.getItemModel());
            saveData();
            if (waystone.isPublic()) {
                Mlcwaystone.updateBlueMapMarker(waystone);
            }

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

        if (slot == WaystoneGUI.SLOT_SWITCH) {
            WaystoneGUI.playerPublicView.put(player, !WaystoneGUI.playerPublicView.getOrDefault(player, false));
            WaystoneGUI.open(player, 1);
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

            if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) {
                if (!data.isPublic() || !data.getOwner().equals(player.getUniqueId())) {
                    player.sendMessage(Component.text("只有公共传送点的拥有者可以提升排名")
                            .color(TextColor.fromHexString("#FF5555")));
                    return;
                }
                promote(player, data, event.getClick() == ClickType.SHIFT_LEFT ? 10 : 100);
                return;
            }
            if (event.getClick() != ClickType.LEFT && event.getClick() != ClickType.RIGHT) return;
            if (data.isPrivate() && !player.isOp() && !data.getOwner().equals(player.getUniqueId())) return;

            // Teleport the player
            Location loc = data.getLocation();
            if (loc.getWorld() == null) {
                player.sendMessage(Component.text("传送点所在世界不可用，无法传送")
                        .color(TextColor.fromHexString("#FF5555")));
                return;
            }

            int feetY = loc.getBlockY() + 2;
            if (feetY + 1 >= loc.getWorld().getMaxHeight()
                    || !loc.getWorld().getBlockAt(loc.getBlockX(), feetY, loc.getBlockZ()).isPassable()
                    || !loc.getWorld().getBlockAt(loc.getBlockX(), feetY + 1, loc.getBlockZ()).isPassable()) {
                player.sendMessage(Component.text("传送点上方空间不足，无法安全传送")
                        .color(TextColor.fromHexString("#FF5555")));
                return;
            }

            Location teleportDest = new Location(loc.getWorld(), loc.getBlockX() + 0.5, feetY,
                    loc.getBlockZ() + 0.5, 0, 0);

            player.closeInventory();
            if (player.teleport(teleportDest)) {
                player.sendMessage(Component.text("已传送到: " + data.getIconName())
                        .color(TextColor.fromHexString("#00ff33")));
            } else {
                player.sendMessage(Component.text("传送失败，请检查目标位置或权限设置")
                        .color(TextColor.fromHexString("#FF5555")));
            }
        }
    }

    private void promote(Player player, WaystoneData data, int amount) {
        long previous = data.getPromotionAmount();
        if (previous > Long.MAX_VALUE - amount) {
            player.sendMessage(Component.text("排名投入已达到上限").color(TextColor.fromHexString("#FF5555")));
            return;
        }
        EconomyResponse payment = economy.withdrawPlayer(player, amount);
        if (!payment.transactionSuccess()) {
            player.sendMessage(Component.text("投入失败: " + payment.errorMessage)
                    .color(TextColor.fromHexString("#FF5555")));
            return;
        }
        data.setPromotionAmount(previous + amount);
        if (!saveData()) {
            data.setPromotionAmount(previous);
            EconomyResponse refund = economy.depositPlayer(player, amount);
            if (!refund.transactionSuccess()) {
                instance.getLogger().severe("传送点排名保存失败，退款也失败: 玩家 " + player.getUniqueId() + "，金额 " + amount);
                player.sendMessage(Component.text("保存失败且退款失败，请联系管理员").color(TextColor.fromHexString("#FF5555")));
            } else {
                player.sendMessage(Component.text("保存失败，水晶币已退回").color(TextColor.fromHexString("#FF5555")));
            }
            return;
        }
        player.sendMessage(Component.text("已投入 " + amount + " 水晶币，累计投入 " + data.getPromotionAmount() + " 水晶币")
                .color(TextColor.fromHexString("#80CBC4")));
        WaystoneGUI.open(player, WaystoneGUI.playerPageMap.getOrDefault(player, 1));
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
        Material type = block.getType();
        if (type == Material.LODESTONE) {
            return getWaystoneAt(block.getLocation()) != null;
        }
        return (type == Material.SOUL_LANTERN || type == Material.BEACON) && isAboveRegisteredLodestone(block);
    }

    private boolean isAboveRegisteredLodestone(Block block) {
        Block below = block.getRelative(BlockFace.DOWN);
        return below.getType() == Material.LODESTONE && getWaystoneAt(below.getLocation()) != null;
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
        if (waystone.getOwner().equals(player.getUniqueId())) {
            return true;
        }
        return false;
    }
}
