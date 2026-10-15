package com.mlc.mlcwaystone;

import org.bukkit.Location;
import org.bukkit.Chunk;
import io.papermc.paper.event.packet.PlayerChunkUnloadEvent;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class WaystoneBeamManagerTest {
    @Test
    void unloadedChunksAreNeverReadOrLoaded() {
        World world = mock(World.class);
        when(world.getMinHeight()).thenReturn(-64);
        when(world.getMaxHeight()).thenReturn(320);

        assertFalse(WaystoneBeamManager.hasLoadedStructure(new Location(world, -1, 64, -17)));

        verify(world).isChunkLoaded(-1, -2);
        verify(world, never()).getBlockAt(anyInt(), anyInt(), anyInt());
        verify(world, never()).getChunkAt(anyInt(), anyInt());
    }

    @Test
    void opaqueRoofBlocksBeamWhileGlassAndBedrockAllowIt() {
        World world = mock(World.class);
        Block roof = mock(Block.class);
        when(world.getMaxHeight()).thenReturn(67);
        when(world.getBlockAt(0, 66, 0)).thenReturn(roof);
        Location base = new Location(world, 0, 64, 0);

        Material opaque = mock(Material.class);
        when(opaque.isOccluding()).thenReturn(true);
        Material transparent = mock(Material.class);
        when(roof.getType()).thenReturn(opaque);
        assertFalse(WaystoneBeamManager.hasClearSky(base));
        when(roof.getType()).thenReturn(transparent);
        assertTrue(WaystoneBeamManager.hasClearSky(base));
        when(roof.getType()).thenReturn(Material.BEDROCK);
        assertTrue(WaystoneBeamManager.hasClearSky(base));
    }

    @Test
    void existingPublicPointsGetBeamsWithoutDuplicatesAndRemovalCleansThemUp() {
        World world = mock(World.class);
        Block baseBlock = mock(Block.class);
        Block beacon = mock(Block.class);
        when(world.getMinHeight()).thenReturn(-64);
        when(world.getMaxHeight()).thenReturn(66);
        when(world.isChunkLoaded(0, 0)).thenReturn(true);
        when(world.getBlockAt(0, 64, 0)).thenReturn(baseBlock);
        when(world.getBlockAt(0, 65, 0)).thenReturn(beacon);
        when(baseBlock.getType()).thenReturn(Material.LODESTONE);
        when(beacon.getType()).thenReturn(Material.BEACON);
        Player viewer = viewer(world);
        WaystoneBeamManager.Visual visual = mock(WaystoneBeamManager.Visual.class);
        WaystoneData point = new WaystoneData(UUID.randomUUID(), UUID.randomUUID(), "owner",
                "PUBLIC", new Location(world, 0, 64, 0), "BEACON", "point", 0);
        List<WaystoneData> points = new ArrayList<>(List.of(point));
        WaystoneBeamManager manager = new WaystoneBeamManager(() -> points, () -> List.of(viewer),
                (origin, height) -> visual);

        manager.refresh();
        manager.refresh();
        verify(visual).show(viewer);

        points.clear();
        manager.refresh();
        verify(visual).hide(viewer);
        // All world access is read-only; no spawn/addEntity call is allowed.
        Set<String> readOnly = Set.of("getMinHeight", "getMaxHeight", "isChunkLoaded", "getBlockAt");
        assertTrue(mockingDetails(world).getInvocations().stream()
                .allMatch(call -> readOnly.contains(call.getMethod().getName())));
    }

    @Test
    void privatePointsNeverCreateBeams() {
        World world = mock(World.class);
        WaystoneData point = new WaystoneData(UUID.randomUUID(), UUID.randomUUID(), "owner",
                "PRIVATE", new Location(world, 0, 64, 0), "SOUL_LANTERN", "point", 0);

        new WaystoneBeamManager(() -> List.of(point), List::of,
                (origin, height) -> { throw new AssertionError("Private beam created"); }).refresh();

        verifyNoInteractions(world);
    }

    @Test
    void waitsForClientChunkThenRemovesAndResendsWhenViewerReturns() {
        Fixture fixture = new Fixture();
        when(fixture.viewer.isChunkSent(0L)).thenReturn(false);
        fixture.manager.refresh();
        verifyNoInteractions(fixture.visual);

        when(fixture.viewer.isChunkSent(0L)).thenReturn(true);
        fixture.manager.refresh();
        verify(fixture.visual).show(fixture.viewer);

        when(fixture.viewer.getLocation()).thenReturn(new Location(fixture.world, 300, 64, 0));
        fixture.manager.refresh();
        verify(fixture.visual).hide(fixture.viewer);

        when(fixture.viewer.getLocation()).thenReturn(new Location(fixture.world, 0, 64, 0));
        fixture.manager.refresh();
        verify(fixture.visual, times(2)).show(fixture.viewer);
    }

    @Test
    void respawnResendsAndShutdownRemovesClientEntities() {
        Fixture fixture = new Fixture();
        fixture.manager.refresh();
        PlayerRespawnEvent respawn = mock(PlayerRespawnEvent.class);
        when(respawn.getPlayer()).thenReturn(fixture.viewer);
        fixture.manager.onRespawn(respawn);
        fixture.manager.refresh();
        verify(fixture.visual, times(2)).show(fixture.viewer);

        fixture.manager.shutdown();
        verify(fixture.visual).hide(fixture.viewer);
    }

    @Test
    void anotherWorldNeverReceivesBeamPackets() {
        Fixture fixture = new Fixture();
        when(fixture.viewer.getWorld()).thenReturn(mock(World.class));
        fixture.manager.refresh();
        verifyNoInteractions(fixture.visual);
    }

    @Test
    void clientChunkReloadResendsPacketsEvenBetweenRefreshes() {
        Fixture fixture = new Fixture();
        fixture.manager.refresh();
        Chunk chunk = mock(Chunk.class);
        PlayerChunkUnloadEvent event = mock(PlayerChunkUnloadEvent.class);
        when(event.getWorld()).thenReturn(fixture.world);
        when(event.getChunk()).thenReturn(chunk);
        when(event.getPlayer()).thenReturn(fixture.viewer);

        fixture.manager.onPlayerChunkUnload(event);
        verify(fixture.visual).hide(fixture.viewer);
        fixture.manager.refresh();
        verify(fixture.visual, times(2)).show(fixture.viewer);
    }

    @Test
    void rotationOnlyUpdatesCurrentViewersAndStopsAfterShutdown() {
        Fixture fixture = new Fixture();
        fixture.manager.refresh();
        fixture.manager.animate(5);
        verify(fixture.visual).animate(List.of(fixture.viewer), 5);

        when(fixture.viewer.isChunkSent(0L)).thenReturn(false);
        fixture.manager.animate(10);
        verify(fixture.visual, never()).animate(anyCollection(), eq(10L));

        fixture.manager.shutdown();
        fixture.manager.animate(15);
        verify(fixture.visual, never()).animate(anyCollection(), eq(15L));
    }

    private static Player viewer(World world) {
        Player viewer = mock(Player.class);
        when(viewer.isOnline()).thenReturn(true);
        when(viewer.getWorld()).thenReturn(world);
        when(viewer.getLocation()).thenReturn(new Location(world, 0, 64, 0));
        when(viewer.isChunkSent(0L)).thenReturn(true);
        return viewer;
    }

    private static final class Fixture {
        final World world = mock(World.class);
        final Player viewer = viewer(world);
        final WaystoneBeamManager.Visual visual = mock(WaystoneBeamManager.Visual.class);
        final WaystoneBeamManager manager;

        Fixture() {
            Block lodestone = mock(Block.class);
            Block beacon = mock(Block.class);
            when(world.getMinHeight()).thenReturn(-64);
            when(world.getMaxHeight()).thenReturn(66);
            when(world.isChunkLoaded(0, 0)).thenReturn(true);
            when(world.getBlockAt(0, 64, 0)).thenReturn(lodestone);
            when(world.getBlockAt(0, 65, 0)).thenReturn(beacon);
            when(lodestone.getType()).thenReturn(Material.LODESTONE);
            when(beacon.getType()).thenReturn(Material.BEACON);
            WaystoneData point = new WaystoneData(UUID.randomUUID(), UUID.randomUUID(), "owner",
                    "PUBLIC", new Location(world, 0, 64, 0), "BEACON", "point", 0);
            manager = new WaystoneBeamManager(() -> List.of(point), () -> List.of(viewer),
                    (origin, height) -> visual);
        }
    }
}
