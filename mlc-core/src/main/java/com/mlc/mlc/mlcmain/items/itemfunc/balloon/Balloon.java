package com.mlc.mlc.mlcmain.items.itemfunc.balloon;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.mlc.mlc.Mlc.instance;
import static com.mlc.mlc.Mlc.miniMessage;

/** 热气球燃料保存在潜影盒物品中，已点燃燃料的剩余时间保存在物品的 PDC 中。 */
public final class Balloon implements Listener {
    private static final NamespacedKey MODEL = NamespacedKey.fromString("mlc:balloon");
    // 剩余时间使用原版熔炉燃烧时间的 tick 数。
    private static final NamespacedKey BURN = NamespacedKey.fromString("mlc:balloon_burn");
    private static final Map<UUID, Holder> open = new HashMap<>();
    // 仅记录由热气球开启飞行的玩家，以便结束时撤销。
    private static final Set<UUID> grantedFlight = new HashSet<>();
    private static final Map<UUID, BukkitTask> flightTasks = new HashMap<>();
    // 每 tick 检查气球飞行位移，并为服务端施加的速度预留逐渐衰减的额度。
    private static final double MAX_HORIZONTAL_PER_TICK = 1.25;
    private static final double MAX_VERTICAL_PER_TICK = 0.5;
    private static final double VELOCITY_DECAY = 0.98;
    private static final Map<UUID, FlightMovement> flightMovement = new HashMap<>();

    private static final class FlightMovement {
        Location position;
        double horizontalVelocity;
        double verticalVelocity;
        double horizontalCredit = MAX_HORIZONTAL_PER_TICK;
        double verticalCredit = MAX_VERTICAL_PER_TICK;
        boolean correcting;
        FlightMovement(Location position) { this.position = position; }
    }

    private static final class Holder implements InventoryHolder {
        final Player player;
        final ItemStack item;
        final int slot;
        Inventory inventory;
        Holder(Player player, ItemStack item) {
            this.player = player;
            this.item = item;
            this.slot = player.getInventory().getHeldItemSlot();
        }
        @Override public @NotNull Inventory getInventory() { return inventory; }
    }

    private static boolean isBalloon(ItemStack item) {
        return item != null && item.getAmount() == 1 && item.hasItemMeta()
                && MODEL.equals(item.getItemMeta().getItemModel());
    }

    // 使用服务端的原版熔炉燃料判定。
    private static boolean isFuel(ItemStack item) {
        return item != null && !item.getType().isAir() && item.getType().isFuel();
    }

    @EventHandler
    public void onUse(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR
                && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        ItemStack item = event.getHand() == EquipmentSlot.HAND
                ? event.getPlayer().getInventory().getItemInMainHand() : event.getItem();
        if (!isBalloon(item)) return;
        // 拦截原版交互，避免把作为基础材质的潜影盒放到地上。
        event.setUseInteractedBlock(Event.Result.DENY);
        event.setUseItemInHand(Event.Result.DENY);
        // 副手仅阻止放置；燃料界面仍由主手打开，避免保存到错误的物品。
        if (event.getHand() != EquipmentSlot.HAND || !(item.getItemMeta() instanceof BlockStateMeta meta)
                || !(meta.getBlockState() instanceof ShulkerBox box)) return;
        Holder holder = new Holder(event.getPlayer(), item);
        holder.inventory = Bukkit.createInventory(holder, 27, miniMessage.deserialize("<#b8ffde>热气球 <#71877a>· 燃料"));
        // 打开临时界面时读取物品自身的潜影盒内容。
        holder.inventory.setContents(box.getInventory().getContents());
        event.getPlayer().openInventory(holder.inventory);
        open.put(event.getPlayer().getUniqueId(), holder);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onClick(InventoryClickEvent event) {
        if (!(event.getView().getTopInventory().getHolder() instanceof Holder holder)) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;
        int raw = event.getRawSlot();
        boolean top = raw >= 0 && raw < holder.inventory.getSize();
        // 界面打开期间固定源物品，避免移动它导致内容保存到错误位置。
        if (event.getClickedInventory() == player.getInventory() && event.getSlot() == player.getInventory().getHeldItemSlot()
                || event.getClick() == ClickType.NUMBER_KEY && event.getHotbarButton() == player.getInventory().getHeldItemSlot()
                || event.getClick() == ClickType.SWAP_OFFHAND || event.getClick() == ClickType.DOUBLE_CLICK) {
            event.setCancelled(true);
            return;
        }
        // 限制普通放入、快捷栏交换和 Shift 转移，取出物品仍然允许。
        if (top) {
            switch (event.getAction()) {
                case PLACE_ALL, PLACE_ONE, PLACE_SOME, SWAP_WITH_CURSOR -> {
                    if (!isFuel(event.getCursor())) event.setCancelled(true);
                }
                case HOTBAR_SWAP -> {
                    if (!isFuel(player.getInventory().getItem(event.getHotbarButton()))) event.setCancelled(true);
                }
                case COLLECT_TO_CURSOR -> event.setCancelled(true);
                default -> { }
            }
        } else if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY && !isFuel(event.getCurrentItem())) {
            event.setCancelled(true);
        }
    }

    // 拖拽进入燃料界面的物品也必须是燃料。
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDrag(InventoryDragEvent event) {
        if (!(event.getView().getTopInventory().getHolder() instanceof Holder holder)) return;
        if (event.getRawSlots().stream().anyMatch(slot -> slot < holder.inventory.getSize()) && !isFuel(event.getOldCursor())) {
            event.setCancelled(true);
        }
    }

    // 切换手持物品前关闭界面并保存，防止源物品被替换。
    @EventHandler public void onHeld(PlayerItemHeldEvent event) { close(event.getPlayer()); }
    @EventHandler public void onSwap(PlayerSwapHandItemsEvent event) { close(event.getPlayer()); }
    @EventHandler public void onDrop(PlayerDropItemEvent event) {
        if (open.containsKey(event.getPlayer().getUniqueId()) && isBalloon(event.getItemDrop().getItemStack())) {
            event.setCancelled(true);
        }
    }
    @EventHandler public void onQuit(PlayerQuitEvent event) { close(event.getPlayer()); stopTask(event.getPlayer()); revoke(event.getPlayer()); BalloonVisual.quit(event.getPlayer()); }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof Holder holder)) return;
        save(holder);
        open.remove(holder.player.getUniqueId(), holder);
        refreshLater(holder.player);
    }

    private static void close(Player player) {
        Holder holder = open.get(player.getUniqueId());
        if (holder != null && player.getOpenInventory().getTopInventory().equals(holder.inventory)) player.closeInventory();
    }

    // 将临时界面写回热气球的 BlockStateMeta。
    private static void save(Holder holder) {
        holder.item.editMeta(BlockStateMeta.class, meta -> {
            if (meta.getBlockState() instanceof ShulkerBox box) {
                box.getInventory().setContents(holder.inventory.getContents());
                meta.setBlockState(box);
            }
        });
        holder.player.getInventory().setItem(holder.slot, holder.item);
    }

    // 玩家切换物品、修改背包或上下线时更新权限，不再持续扫描所有玩家。
    private static void refreshLater(Player player) {
        Bukkit.getScheduler().runTask(instance, () -> {
            if (player.isOnline()) refreshFlight(player);
        });
    }

    public static void refreshOnlinePlayers() {
        BalloonVisual.init();
        for (Player player : Bukkit.getOnlinePlayers()) refreshLater(player);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onHeldUpdate(PlayerItemHeldEvent event) { refreshLater(event.getPlayer()); }
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSwapUpdate(PlayerSwapHandItemsEvent event) { refreshLater(event.getPlayer()); }
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDropUpdate(PlayerDropItemEvent event) { refreshLater(event.getPlayer()); }
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPickupUpdate(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player player) refreshLater(player);
    }
    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryUpdate(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player player) refreshLater(player);
    }
    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryDragUpdate(InventoryDragEvent event) {
        if (event.getWhoClicked() instanceof Player player) refreshLater(player);
    }
    @EventHandler public void onJoin(PlayerJoinEvent event) { refreshLater(event.getPlayer()); }
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        FlightMovement movement = flightMovement.get(event.getPlayer().getUniqueId());
        if (movement != null) {
            if (movement.correcting) return;
            movement.position = event.getTo().clone();
            movement.horizontalVelocity = 0;
            movement.verticalVelocity = 0;
            movement.horizontalCredit = MAX_HORIZONTAL_PER_TICK;
            movement.verticalCredit = MAX_VERTICAL_PER_TICK;
        }
        refreshViewLater(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRespawn(PlayerRespawnEvent event) { refreshViewLater(event.getPlayer()); }

    private static void refreshViewLater(Player player) {
        Bukkit.getScheduler().runTask(instance, () -> {
            if (!player.isOnline()) return;
            BalloonVisual.resetView(player);
            refreshFlight(player);
        });
    }
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onGameMode(PlayerGameModeChangeEvent event) { refreshLater(event.getPlayer()); }

    // 双击跳跃开始飞行后才启动燃料任务；停止飞行则取消。
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onToggleFlight(PlayerToggleFlightEvent event) {
        if (event.isFlying()) refreshLater(event.getPlayer());
        else stopTask(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFlightVelocity(PlayerVelocityEvent event) {
        FlightMovement movement = flightMovement.get(event.getPlayer().getUniqueId());
        if (movement != null) {
            var velocity = event.getVelocity();
            movement.horizontalVelocity = Math.max(movement.horizontalVelocity, Math.hypot(velocity.getX(), velocity.getZ()));
            movement.verticalVelocity = Math.max(movement.verticalVelocity, Math.abs(velocity.getY()));
        }
    }

    private static ItemStack mainBalloon(Player player) {
        Holder holder = open.get(player.getUniqueId());
        return holder != null ? holder.item : player.getInventory().getItemInMainHand();
    }

    private static boolean available(Player player, ItemStack item) {
        return isBalloon(item) && hasFuel(player, item);
    }

    private static void refreshFlight(Player player) {
        BalloonVisual.refresh(player, isBalloon(mainBalloon(player)) || isBalloon(player.getInventory().getItemInOffHand()));
        boolean powered = available(player, mainBalloon(player))
                || available(player, player.getInventory().getItemInOffHand());
        if (!powered) {
            stopTask(player);
            revoke(player);
            return;
        }
        if (!player.getAllowFlight()) {
            player.setAllowFlight(true);
            grantedFlight.add(player.getUniqueId());
        }
        if (player.isFlying()) startTask(player);
        else stopTask(player);
    }

    private static void startTask(Player player) {
        if (flightTasks.containsKey(player.getUniqueId())) return;
        flightMovement.put(player.getUniqueId(), new FlightMovement(player.getLocation()));
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(instance, () -> burnTick(player), 1L, 1L);
        flightTasks.put(player.getUniqueId(), task);
    }

    private static void stopTask(Player player) {
        flightMovement.remove(player.getUniqueId());
        BukkitTask task = flightTasks.remove(player.getUniqueId());
        if (task != null) task.cancel();
    }

    static boolean isFlyingWithBalloon(Player player) {
        return player.isFlying() && flightTasks.containsKey(player.getUniqueId());
    }

    private static void burnTick(Player player) {
        if (!player.isOnline() || !player.isFlying()) {
            stopTask(player);
            return;
        }
        checkFlightMovement(player);
        ItemStack main = mainBalloon(player);
        ItemStack offhand = player.getInventory().getItemInOffHand();
        if (isBalloon(main) && burn(player, main)) {
            Holder holder = open.get(player.getUniqueId());
            player.getInventory().setItem(holder != null ? holder.slot : player.getInventory().getHeldItemSlot(), main);
        } else if (isBalloon(offhand) && burn(player, offhand)) {
            player.getInventory().setItemInOffHand(offhand);
        } else {
            stopTask(player);
            revoke(player);
        }
    }

    private static void checkFlightMovement(Player player) {
        FlightMovement movement = flightMovement.get(player.getUniqueId());
        if (movement == null) return;
        Location current = player.getLocation();
        if (!grantedFlight.contains(player.getUniqueId()) || player.getGameMode() == GameMode.CREATIVE
                || player.getGameMode() == GameMode.SPECTATOR || player.isInsideVehicle()
                || !current.getWorld().equals(movement.position.getWorld())) {
            movement.position = current;
            movement.horizontalVelocity = 0;
            movement.verticalVelocity = 0;
            movement.horizontalCredit = MAX_HORIZONTAL_PER_TICK;
            movement.verticalCredit = MAX_VERTICAL_PER_TICK;
            return;
        }
        double dx = current.getX() - movement.position.getX();
        double dy = current.getY() - movement.position.getY();
        double dz = current.getZ() - movement.position.getZ();
        // 最多储存一个 tick 的余量，容忍短时集中到达的移动数据。
        double horizontalAllowance = MAX_HORIZONTAL_PER_TICK + movement.horizontalCredit;
        double verticalAllowance = MAX_VERTICAL_PER_TICK + movement.verticalCredit;
        double horizontal = Math.hypot(dx, dz);
        double vertical = Math.abs(dy);
        double allowedHorizontal = horizontalAllowance + movement.horizontalVelocity;
        double allowedVertical = verticalAllowance + movement.verticalVelocity;
        movement.horizontalCredit = Math.min(MAX_HORIZONTAL_PER_TICK,
                Math.max(0, horizontalAllowance - Math.max(0, horizontal - movement.horizontalVelocity)));
        movement.verticalCredit = Math.min(MAX_VERTICAL_PER_TICK,
                Math.max(0, verticalAllowance - Math.max(0, vertical - movement.verticalVelocity)));
        movement.horizontalVelocity *= VELOCITY_DECAY;
        movement.verticalVelocity *= VELOCITY_DECAY;
        if (movement.horizontalVelocity < 0.01) movement.horizontalVelocity = 0;
        if (movement.verticalVelocity < 0.01) movement.verticalVelocity = 0;
        if (horizontal > allowedHorizontal || vertical > allowedVertical) {
            double scale = horizontal > allowedHorizontal ? allowedHorizontal / horizontal : 1;
            Location limited = current.clone();
            limited.setX(movement.position.getX() + dx * scale);
            limited.setY(movement.position.getY() + Math.copySign(Math.min(vertical, allowedVertical), dy));
            limited.setZ(movement.position.getZ() + dz * scale);
            // 修正会产生传送事件，避免将它误当作普通传送并重置基准。
            movement.correcting = true;
            try {
                player.teleport(limited);
            } finally {
                movement.correcting = false;
            }
            current = player.getLocation();
        }
        movement.position = current;
    }

    // 起飞前只检查是否有燃料，不取出燃料或减少剩余燃烧时间。
    private static boolean hasFuel(Player player, ItemStack item) {
        if (item.getPersistentDataContainer().getOrDefault(BURN, PersistentDataType.INTEGER, 0) > 0) return true;
        Holder holder = open.get(player.getUniqueId());
        if (!(item.getItemMeta() instanceof BlockStateMeta meta) || !(meta.getBlockState() instanceof ShulkerBox box)) return false;
        Inventory contents = holder != null && holder.item == item ? holder.inventory : box.getInventory();
        for (ItemStack fuel : contents.getContents()) {
            if (!isFuel(fuel)) continue;
            var type = fuel.getType().asItemType();
            if (type != null && type.getBurnDuration() > 0) return true;
        }
        return false;
    }

    private static boolean burn(Player player, ItemStack item) {
        // 正在燃烧的燃料先继续使用，耗尽后才从热气球内部取下一份。
        int remaining = item.getPersistentDataContainer().getOrDefault(BURN, PersistentDataType.INTEGER, 0);
        if (remaining <= 0) {
            Holder holder = open.get(player.getUniqueId());
            boolean editing = holder != null && holder.item == item;
            if (!(item.getItemMeta() instanceof BlockStateMeta meta) || !(meta.getBlockState() instanceof ShulkerBox box)) return false;
            // 打开界面时以临时界面为准，其他时候直接读取物品中的潜影盒内容。
            Inventory contents = editing ? holder.inventory : box.getInventory();
            for (int slot = 0; slot < contents.getSize(); slot++) {
                ItemStack fuel = contents.getItem(slot);
                if (!isFuel(fuel)) continue;
                var type = fuel.getType().asItemType();
                if (type == null || type.getBurnDuration() <= 0) continue;
                // 使用服务端提供的熔炉燃烧时长，而不是维护一份燃料时长表。
                remaining = type.getBurnDuration();
                boolean lavaBucket = fuel.getType() == Material.LAVA_BUCKET;
                fuel.setAmount(fuel.getAmount() - 1);
                contents.setItem(slot, fuel.getAmount() == 0 ? null : fuel);
                // 岩浆桶燃尽后返还空桶；背包已满时将其掉落在附近。
                if (lavaBucket) {
                    player.getInventory().addItem(new ItemStack(Material.BUCKET)).values()
                            .forEach(left -> player.getWorld().dropItemNaturally(player.getLocation(), left));
                }
                if (editing) save(holder);
                else {
                    meta.setBlockState(box);
                    item.setItemMeta(meta);
                }
                break;
            }
        }
        if (remaining <= 0) return false;
        // 每个 tick 扣除 20 个原版燃烧 tick，燃料消耗速度为熔炉的 20 倍。
        int left = Math.max(0, remaining - 20);
        item.editMeta(meta -> meta.getPersistentDataContainer().set(BURN, PersistentDataType.INTEGER, left));
        return true;
    }

    private static void revoke(Player player) {
        // 只撤销此功能记录过的飞行权限；创造和旁观模式保留自身飞行能力。
        if (!grantedFlight.remove(player.getUniqueId())) return;
        if (player.getGameMode() != GameMode.CREATIVE && player.getGameMode() != GameMode.SPECTATOR) {
            player.setFlying(false);
            player.setAllowFlight(false);
        }
    }

    public static void shutdown() {
        BalloonVisual.shutdown();
        for (Player player : Bukkit.getOnlinePlayers()) {
            close(player);
            stopTask(player);
            revoke(player);
        }
    }
}
