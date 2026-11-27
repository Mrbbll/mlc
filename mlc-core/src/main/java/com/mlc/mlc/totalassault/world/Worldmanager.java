package com.mlc.mlc.totalassault.world;

import com.mlc.mlc.totalassault.data.Filereader;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * 创建总力战挑战世界，并加载场地原理图。
 *
 * 每张票使用一个独立世界，世界名称由挑战 ID 生成。
 * 此类负责世界的创建、原理图加载和卸载删除，不负责扣票。
 */
public final class Worldmanager {

    private static final String WORLD_PREFIX = "ta_";

    private Worldmanager() {
    }

    /**
     * 根据挑战 ID 生成世界名。
     *
     * 使用 UUID 而不是玩家名，避免名字重复或包含路径字符。
     */
    public static String worldName(UUID challengeId) {
        Objects.requireNonNull(challengeId, "挑战 ID 不能为空");

        return WORLD_PREFIX + challengeId.toString().replace("-", "");
    }

    /**
     * 创建本张票专属的挑战世界。
     *
     * 必须在服务器主线程调用。
     * 创建前会检查是否存在同名已加载世界或同名世界目录，
     * 避免误用原来的世界。
     */
    public static World createWorld(UUID challengeId) {
        if (!Bukkit.isPrimaryThread()) {
            throw new IllegalStateException("创建挑战世界必须在主线程执行");
        }

        String name = worldName(challengeId);

        if (Bukkit.getWorld(name) != null) {
            throw new IllegalStateException("挑战世界已经加载：" + name);
        }

        Path worldDirectory = Bukkit.getWorldContainer()
                .toPath()
                .resolve(name);

        if (Files.exists(worldDirectory)) {
            throw new IllegalStateException(
                    "挑战世界目录已经存在：" + worldDirectory
            );
        }

        WorldCreator creator = new WorldCreator(name);

        // 不生成地形、装饰和生物，只保留之后粘贴的场地。
        creator.generator(new ChunkGenerator() {
            @Override
            public Location getFixedSpawnLocation(World world, Random random) {
                // 虚空世界没有地面，固定出生点避免寻找自然出生位置。
                return new Location(world, 0.5, 64, 0.5);
            }
        });
        creator.generateStructures(false);

        World world = creator.createWorld();

        if (world == null) {
            throw new IllegalStateException("创建挑战世界失败：" + name);
        }

        return world;
    }

    /**
     * 将在线玩家送回大厅，卸载并删除已结束的挑战世界。
     * 必须在主线程调用，并确保原理图加载已经结束。
     */
    public static void deleteWorld(String worldName) throws IOException {
        if (!Bukkit.isPrimaryThread()) {
            throw new IllegalStateException("删除世界必须在主线程执行");
        }
        if (worldName == null || !worldName.startsWith("ta")) {
            throw new IOException("不是总力战世界：" + worldName);
        }

        Path container = Bukkit.getWorldContainer().toPath().toAbsolutePath().normalize();
        Path directory = container.resolve(worldName).normalize();
        if (!container.equals(directory.getParent())) {
            throw new IOException("世界路径无效：" + directory);
        }

        World world = Bukkit.getWorld(worldName);
        if (world != null) {
            List<Player> players = List.copyOf(world.getPlayers());
            if (!players.isEmpty()) {
                Location lobby = Filereader.ArenaConfig.lobbySpawn();
                for (Player player : players) {
                    if (!player.teleport(lobby)) {
                        throw new IOException("玩家无法离开挑战世界：" + player.getName());
                    }
                }
            }
            if (!world.getPlayers().isEmpty() || !Bukkit.unloadWorld(world, false)) {
                throw new IOException("挑战世界卸载失败：" + worldName);
            }
        }

        if (Files.notExists(directory, LinkOption.NOFOLLOW_LINKS)) {
            return;
        }
        // 不跟随目录符号链接，避免将删除范围扩展到其他位置。
        if (Files.isSymbolicLink(directory) || !Files.isDirectory(directory, LinkOption.NOFOLLOW_LINKS)) {
            throw new IOException("世界目录无效：" + directory);
        }
        try (Stream<Path> paths = Files.walk(directory)) {
            for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
                Files.delete(path);
            }
        }
    }

    /**
     * 异步读取并粘贴场地原理图。
     *
     * 调用方法本身必须在主线程执行。
     * 完成或失败后的回调也会切回主线程。
     *
     * 世界在原理图加载期间不能被卸载或删除。
     *
     * @param plugin 插件实例
     * @param world 已创建的挑战世界
     * @param arena 场地配置
     * @param onSuccess 加载完成后的回调
     * @param onFailure 加载失败后的回调
     */
    public static void pasteArenaAsync(
            JavaPlugin plugin,
            World world,
            Filereader.ArenaConfig arena,
            Runnable onSuccess,
            Consumer<Exception> onFailure
    ) {
        if (!Bukkit.isPrimaryThread()) {
            throw new IllegalStateException(
                    "启动场地加载任务必须在主线程执行"
            );
        }

        Objects.requireNonNull(plugin, "插件不能为空");
        Objects.requireNonNull(world, "挑战世界不能为空");
        Objects.requireNonNull(arena, "场地配置不能为空");
        Objects.requireNonNull(onSuccess, "成功回调不能为空");
        Objects.requireNonNull(onFailure, "失败回调不能为空");

        // 在主线程中准备好目标世界和坐标。
        com.sk89q.worldedit.world.World targetWorld =
                BukkitAdapter.adapt(world);

        File schematicFile = new File(
                plugin.getDataFolder(),
                arena.schematic()
        );

        Filereader.ArenaConfig.Point point = arena.pasteOrigin();

        BlockVector3 origin = BlockVector3.at(
                (int) Math.floor(point.x()),
                (int) Math.floor(point.y()),
                (int) Math.floor(point.z())
        );

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                pasteSchematic(targetWorld, schematicFile, origin);

                // Bukkit 世界、玩家和挑战状态的后续操作回到主线程。
                Bukkit.getScheduler().runTask(plugin, onSuccess);
            } catch (Exception exception) {
                Bukkit.getScheduler().runTask(
                        plugin,
                        () -> onFailure.accept(exception)
                );
            }
        });
    }

    /**
     * 读取原理图并执行粘贴。
     *
     * 本方法在异步任务中调用。
     * 编辑会话关闭后，本次编辑才算完成。
     */
    private static void pasteSchematic(
            com.sk89q.worldedit.world.World targetWorld,
            File schematicFile,
            BlockVector3 origin
    ) throws IOException, WorldEditException {
        if (!schematicFile.isFile()) {
            throw new IOException(
                    "找不到场地原理图：" + schematicFile.getAbsolutePath()
            );
        }

        ClipboardFormat format = ClipboardFormats.findByFile(schematicFile);

        if (format == null) {
            throw new IOException(
                    "无法识别原理图格式：" + schematicFile.getName()
            );
        }

        Clipboard clipboard;

        try (InputStream input = new FileInputStream(schematicFile);
             ClipboardReader reader = format.getReader(input)) {

            clipboard = reader.read();
        }

        try (ClipboardHolder holder = new ClipboardHolder(clipboard);
             EditSession editSession = WorldEdit.getInstance()
                     .newEditSessionBuilder()
                     .world(targetWorld)
                     .build()) {

            Operation operation = holder.createPaste(editSession)
                    .to(origin)
                    // 粘贴空气，清除原理图范围内已有的方块。
                    .ignoreAirBlocks(false)
                    .build();

            Operations.complete(operation);
        }
    }
}
