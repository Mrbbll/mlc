package com.mlc.mlc.mlcmain.enchantments.moreenchants;

import com.mlc.mlcdomain.api.DomainProtection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class VeinMineTest {
    @Test void joinsAnIsolatedDiagonalChainWithoutAnyFaceAdjacentBridge() {
        Arena arena = new Arena();
        Block start = arena.ore(8, 64, 8);
        Block edge = arena.ore(9, 65, 8);
        Block corner = arena.ore(10, 66, 9);
        Block horizontalDiagonal = arena.ore(11, 66, 10);
        arena.mine(8, 64, 8, 8);
        for (Block block : new Block[]{start, edge, corner, horizontalDiagonal}) {
            verify(block).breakNaturally(arena.tool);
        }
        verify(arena.tool, times(4)).damage(1, arena.player);
    }

    @Test void reachesAll26NeighborsIncludingEdgesAndCornersWithoutDuplicateBreaks() {
        Arena arena = new Arena();
        for (int dx = -1; dx <= 1; dx++) for (int dy = -1; dy <= 1; dy++) for (int dz = -1; dz <= 1; dz++) {
            arena.ore(8 + dx, 64 + dy, 8 + dz);
        }
        arena.mine(8, 64, 8, 27);
        for (Block block : arena.ores.values()) verify(block, times(1)).breakNaturally(arena.tool);
        verify(arena.tool, times(27)).damage(1, arena.player);
    }

    @Test void denseVeinStopsAtEightSuccessfulBreaks() {
        Arena arena = new Arena();
        for (int dx = -1; dx <= 1; dx++) for (int dy = -1; dy <= 1; dy++) for (int dz = -1; dz <= 1; dz++) {
            arena.ore(8 + dx, 64 + dy, 8 + dz);
        }
        arena.mine(8, 64, 8, 8);
        verify(arena.tool, times(8)).damage(1, arena.player);
        long remaining = arena.ores.values().stream().filter(block -> block.getType() == Material.DIAMOND_ORE).count();
        assertEquals(19, remaining);
    }

    @Test void respectsProtectedBlocksAndDoesNotCrossThemToReachAnotherDiagonal() {
        Arena arena = new Arena();
        Block first = arena.ore(8, 64, 8);
        Block protectedBlock = arena.ore(9, 65, 9);
        Block beyond = arena.ore(10, 66, 10);
        try (var protection = mockStatic(DomainProtection.class)) {
            protection.when(() -> DomainProtection.canBreak(eq(arena.player), any(Block.class))).thenReturn(true);
            protection.when(() -> DomainProtection.canBreak(arena.player, protectedBlock)).thenReturn(false);
            VeinMine.breakVein(first, Material.DIAMOND_ORE, 8, arena.tool, arena.player);
        }
        verify(first).breakNaturally(arena.tool);
        verify(protectedBlock, never()).breakNaturally(any(ItemStack.class));
        verify(beyond, never()).breakNaturally(any(ItemStack.class));
    }

    @Test void neverQueriesUnloadedNeighborChunksOrOutsideWorldHeight() {
        Arena arena = new Arena();
        when(arena.world.isChunkLoaded(anyInt(), anyInt())).thenAnswer(call -> (int) call.getArgument(0) == 0
                && (int) call.getArgument(1) == 0);
        arena.ore(15, 319, 15);
        arena.mine(15, 319, 15, 8);
        verify(arena.world, never()).getBlockAt(eq(16), anyInt(), anyInt());
        verify(arena.world, never()).getBlockAt(anyInt(), anyInt(), eq(16));
        verify(arena.world, never()).getBlockAt(anyInt(), eq(320), anyInt());
    }

    @Test void stopsExpandingWhenTheToolBreaks() {
        Arena arena = new Arena();
        Block first = arena.ore(8, 64, 8);
        Block diagonal = arena.ore(9, 65, 9);
        doAnswer(call -> { when(arena.inventory.getItemInMainHand()).thenReturn(arena.empty); return null; })
                .when(arena.tool).damage(1, arena.player);
        arena.mine(8, 64, 8, 8);
        verify(first).breakNaturally(arena.tool);
        verify(diagonal, never()).breakNaturally(any(ItemStack.class));
    }

    private record Position(int x, int y, int z) {}

    private static final class Arena {
        final World world = mock(World.class);
        final Player player = mock(Player.class);
        final PlayerInventory inventory = mock(PlayerInventory.class);
        final ItemStack tool = mock(ItemStack.class);
        final ItemStack empty = mock(ItemStack.class);
        final Map<Position, Block> blocks = new HashMap<>();
        final Map<Position, Block> ores = new HashMap<>();

        Arena() {
            when(world.getMinHeight()).thenReturn(-64);
            when(world.getMaxHeight()).thenReturn(320);
            when(world.isChunkLoaded(anyInt(), anyInt())).thenReturn(true);
            when(world.getBlockAt(anyInt(), anyInt(), anyInt())).thenAnswer(call -> block(
                    new Position(call.getArgument(0), call.getArgument(1), call.getArgument(2)), Material.AIR));
            when(player.getInventory()).thenReturn(inventory);
            when(inventory.getItemInMainHand()).thenReturn(tool);
            when(tool.getType()).thenReturn(Material.NETHERITE_PICKAXE);
            when(tool.getAmount()).thenReturn(1);
            when(empty.getType()).thenReturn(Material.AIR);
        }

        Block ore(int x, int y, int z) {
            Position position = new Position(x, y, z);
            Block block = block(position, Material.DIAMOND_ORE);
            ores.put(position, block);
            return block;
        }

        Block block(Position position, Material initial) {
            return blocks.computeIfAbsent(position, key -> {
                Block block = mock(Block.class);
                AtomicReference<Material> type = new AtomicReference<>(initial);
                when(block.getWorld()).thenReturn(world);
                when(block.getX()).thenReturn(key.x());
                when(block.getY()).thenReturn(key.y());
                when(block.getZ()).thenReturn(key.z());
                when(block.getType()).thenAnswer(call -> type.get());
                when(block.breakNaturally(tool)).thenAnswer(call -> { type.set(Material.AIR); return true; });
                return block;
            });
        }

        void mine(int x, int y, int z, int limit) {
            try (var protection = mockStatic(DomainProtection.class)) {
                protection.when(() -> DomainProtection.canBreak(eq(player), any(Block.class))).thenReturn(true);
                VeinMine.breakVein(ores.get(new Position(x, y, z)), Material.DIAMOND_ORE, limit, tool, player);
            }
        }
    }
}
