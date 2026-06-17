package com.mlc.mlc.mlcmain.ess.command;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class rtp implements CommandExecutor {
    private static final int RTP_MIN = -50000;
    private static final int RTP_MAX = 50000;

    private static final org.bukkit.Material[] DANGEROUS_GROUND = {
        org.bukkit.Material.LAVA,
        org.bukkit.Material.FIRE,
        org.bukkit.Material.CACTUS,
        org.bukkit.Material.SWEET_BERRY_BUSH
    };

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (commandSender instanceof Player player) {
            Random random = new Random();
            World world = player.getWorld();
            int x = random.nextInt(RTP_MIN, RTP_MAX);
            int z = random.nextInt(RTP_MIN, RTP_MAX);

            // 先异步加载区块，再获取地表高度
            world.getChunkAtAsync(x >> 4, z >> 4).thenAccept(chunk -> {
                // 在已加载的 chunk 上同步获取高度（此时 chunk 已加载，不会卡）
                int groundY = world.getHighestBlockYAt(x, z);
                Location safeLocation = findSafeLocation(world, x, groundY, z);

                player.teleportAsync(safeLocation, PlayerTeleportEvent.TeleportCause.PLUGIN)
                    .thenAccept(result -> {
                        if (result) {
                            player.sendMessage(Component.text("已随机传送到 (" + x + ", " + safeLocation.getBlockY() + ", " + z + ")"));
                        } else {
                            player.sendMessage(Component.text("传送失败"));
                        }
                    });
            });
            return true;
        }
        return false;
    }

    /**
     * 从地表高度向上搜索安全落脚点，避免卡方块/岩浆等
     */
    private Location findSafeLocation(World world, int x, int startY, int z) {
        int y = startY;
        // 地表可能在空气或水里，向上找到安全的站立位置
        for (int attempt = 0; attempt < 50; attempt++) {
            Block feetBlock = world.getBlockAt(x, y, z);
            Block groundBlock = world.getBlockAt(x, y - 1, z);
            Block headBlock = world.getBlockAt(x, y + 1, z);

            if (feetBlock.isPassable() && headBlock.isPassable() && groundBlock.getType().isSolid()) {
                // 检查脚下是否是危险方块
                boolean dangerous = false;
                for (org.bukkit.Material mat : DANGEROUS_GROUND) {
                    if (groundBlock.getType() == mat) {
                        dangerous = true;
                        break;
                    }
                }
                if (!dangerous) {
                    return new Location(world, x + 0.5, y, z + 0.5);
                }
            }
            y++;
        }
        //返回最高方块上方
        return new Location(world, x + 0.5, startY + 1, z + 0.5);
    }
}
