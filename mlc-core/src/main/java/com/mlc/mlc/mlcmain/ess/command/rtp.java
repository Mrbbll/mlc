package com.mlc.mlc.mlcmain.ess.command;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

import static com.mlc.mlc.Mlc.instance;
import static com.mlc.mlc.Mlc.miniMessage;

public class rtp implements CommandExecutor {
    private static final int RTP_MIN = -50000;
    private static final int RTP_MAX = 50000;
    private static final int MAX_ATTEMPTS = 10;
    private static final int COOL_DOWN = 30;
    private static Map<OfflinePlayer,Long> COOL_DOWN_MAP = new HashMap<>();
    private static final int CHUNK_MANHATTAN_DIST = 10; // 曼哈顿距离 ≤ 10 的菱形区域

    private static final org.bukkit.Material[] DANGEROUS_GROUND = {
        org.bukkit.Material.LAVA,
        org.bukkit.Material.FIRE,
        org.bukkit.Material.CACTUS,
        org.bukkit.Material.SWEET_BERRY_BUSH
    };

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (commandSender instanceof Player player) {
            long lastUse = COOL_DOWN_MAP.getOrDefault(player, 0L);
            long remaining = lastUse + COOL_DOWN * 1000L - System.currentTimeMillis();
            if(remaining > 0){
                player.sendMessage(miniMessage.deserialize("<red><b>随机传送处于冷却中，剩余 " + (remaining / 1000 + 1) + " 秒"));
                return false;
            }
            COOL_DOWN_MAP.put(player, System.currentTimeMillis());
            player.setSendViewDistance(2);
            player.sendMessage(miniMessage.deserialize("<green>正在查找和生成传送位置，请等待....."));
            tryRtp(player, new Random(), MAX_ATTEMPTS);
            return true;
        }
        return false;
    }

    /**
     * 尝试随机传送，遇到岩浆等危险地表则重新选点重试
     */
    private void tryRtp(Player player, Random random, int attemptsLeft) {
        if (attemptsLeft <= 0) {
            player.setSendViewDistance(instance.getServer().getViewDistance());
            player.sendMessage(Component.text("尝试 " + MAX_ATTEMPTS + " 次后仍无法找到安全位置，请稍后再试"));
            return;
        }

        World world = player.getWorld();
        int x = random.nextInt(RTP_MIN, RTP_MAX);
        int z = random.nextInt(RTP_MIN, RTP_MAX);

        // 1. 异步加载目标区块
        world.getChunkAtAsync(x >> 4, z >> 4).thenAccept(chunk -> {
            int groundY = world.getHighestBlockYAt(x, z);
            Block groundBlock = world.getBlockAt(x, groundY - 1, z);

            // 2. 地表脚下是危险方块（岩浆等），换地方重试
            if (isDangerous(groundBlock)) {
                tryRtp(player, random, attemptsLeft - 1);
                return;
            }

            // 3. 向上搜索精确安全落脚点
            Location safeLocation = findSafeLocation(world, x, groundY, z);

            // 4. 预加载曼哈顿距离 ≤ 10 的菱形区块，全部加载完再传送
            preloadChunks(world, x >> 4, z >> 4, CHUNK_MANHATTAN_DIST)
                .thenAccept(v -> {
                    player.teleportAsync(safeLocation, PlayerTeleportEvent.TeleportCause.PLUGIN)
                        .thenAccept(result -> {
                            if (result) {
                                //逐步开放视野防止他妈的卡
                                player.setSendViewDistance(instance.getServer().getViewDistance()/2);
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        if(player.isOnline()){
                                            player.setSendViewDistance(instance.getServer().getViewDistance());
                                        }
                                    }
                                }.runTaskLater(instance,100);
                                player.sendMessage(Component.text("已随机传送到 (" + x + ", " + safeLocation.getBlockY() + ", " + z + ")"));
                            } else {
                                player.setSendViewDistance(instance.getServer().getViewDistance());
                                player.sendMessage(Component.text("传送失败"));
                            }
                        });
                });
        });
    }

    /**
     * 异步预加载以 (cx, cz) 为中心、曼哈顿距离 ≤ dist 的菱形区块
     */
    private CompletableFuture<Void> preloadChunks(World world, int cx, int cz, int dist) {
        // 曼哈顿距离 D 的菱形区域区块数 = 1 + 2*D*(D+1)，D=10 时为 221 个
        int size = 1 + 2 * dist * (dist + 1);
        CompletableFuture<?>[] futures = new CompletableFuture<?>[size];
        int i = 0;
        for (int dx = -dist; dx <= dist; dx++) {
            int maxDz = dist - Math.abs(dx);
            for (int dz = -maxDz; dz <= maxDz; dz++) {
                futures[i++] = world.getChunkAtAsync(cx + dx, cz + dz);
            }
        }
        return CompletableFuture.allOf(futures);
    }

    /**
     * 判断方块是否属于危险地表
     */
    private boolean isDangerous(Block block) {
        for (org.bukkit.Material mat : DANGEROUS_GROUND) {
            if (block.getType() == mat) {
                return true;
            }
        }
        return false;
    }

    /**
     * 从地表高度向上搜索安全落脚点，避免卡方块/头部无空间等
     */
    private Location findSafeLocation(World world, int x, int startY, int z) {
        int y = startY;
        for (int attempt = 0; attempt < 50; attempt++) {
            Block feetBlock = world.getBlockAt(x, y, z);
            Block groundBlock = world.getBlockAt(x, y - 1, z);
            Block headBlock = world.getBlockAt(x, y + 1, z);

            if (feetBlock.isPassable() && headBlock.isPassable() && groundBlock.getType().isSolid() && !isDangerous(groundBlock)) {
                return new Location(world, x + 0.5, y, z + 0.5);
            }
            y++;
        }
        // 兜底：回到地表最高方块上方
        return new Location(world, x + 0.5, startY + 1, z + 0.5);
    }
}
