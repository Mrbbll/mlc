package com.mlc.mlcbot;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.generator.structure.GeneratedStructure;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.BoundingBox;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class WildCampSpawnerTest {
    private JavaPlugin plugin;
    private BotManager manager;
    private WildCampSpawner spawner;
    private World world;
    private GeneratedStructure camp;
    private Location at;
    private BukkitScheduler scheduler;

    @BeforeEach void setup() {
        plugin = mock(JavaPlugin.class);
        when(plugin.getName()).thenReturn("mlc");
        when(plugin.namespace()).thenReturn("mlc");
        when(plugin.getLogger()).thenReturn(Logger.getAnonymousLogger());
        Server server = mock(Server.class);
        scheduler = mock(BukkitScheduler.class);
        when(plugin.getServer()).thenReturn(server);
        when(server.getScheduler()).thenReturn(scheduler);
        manager = mock(BotManager.class);
        spawner = spy(new WildCampSpawner(plugin, manager));
        world = mock(World.class);
        camp = mock(GeneratedStructure.class);
        PersistentDataContainer data = container();
        when(camp.getPersistentDataContainer()).thenReturn(data);
        when(camp.getBoundingBox()).thenReturn(new BoundingBox(0, 63, 0, 12, 70, 12));
        at = new Location(world, 6.5, 64, 6.5);
        doReturn(at).when(spawner).findSpawn(eq(world), any(BoundingBox.class));
    }

    @Test void matchesVanillaCampVariantsOnly() {
        for (String biome : new String[]{"forest", "dappled_forest", "swamp", "snowy_taiga"}) {
            assertTrue(WildCampSpawner.isAbandonedCamp(NamespacedKey.minecraft("abandoned_camp_" + biome)));
        }
        assertFalse(WildCampSpawner.isAbandonedCamp(NamespacedKey.minecraft("village_plains")));
        assertFalse(WildCampSpawner.isAbandonedCamp(new NamespacedKey("custom", "abandoned_camp_forest")));
    }

    @Test void persistsOneSpawnPerCampAcrossListenersAndIntersectingChunks() {
        spawner.spawnCamp(world, camp);
        WildCampSpawner reloaded = spy(new WildCampSpawner(plugin, manager));
        reloaded.spawnCamp(world, camp);
        spawner.spawnCamp(world, camp);
        verify(manager, times(1)).spawnWild(at);
        verify(reloaded, never()).findSpawn(any(), any());
    }

    @Test void unsafeLocationCanBeRetriedWithoutConsumingTheCamp() {
        doReturn(null, at).when(spawner).findSpawn(eq(world), any(BoundingBox.class));
        spawner.spawnCamp(world, camp);
        verifyNoInteractions(manager);
        spawner.spawnCamp(world, camp);
        verify(manager).spawnWild(at);
    }

    @Test void failedSpawnCanBeRetried() {
        doThrow(new IllegalStateException("spawn failed")).doNothing().when(manager).spawnWild(at);
        spawner.spawnCamp(world, camp);
        spawner.spawnCamp(world, camp);
        spawner.spawnCamp(world, camp);
        verify(manager, times(2)).spawnWild(at);
    }

    @Test void previouslyGeneratedChunksAreCheckedAfterLoadingAndRetried() {
        Chunk chunk = mock(Chunk.class);
        when(chunk.isLoaded()).thenReturn(true);
        doNothing().when(spawner).populate(chunk);
        spawner.onChunkLoad(new ChunkLoadEvent(chunk, false));
        ArgumentCaptor<Runnable> first = ArgumentCaptor.forClass(Runnable.class);
        ArgumentCaptor<Runnable> retry = ArgumentCaptor.forClass(Runnable.class);
        verify(scheduler).runTask(eq(plugin), first.capture());
        verify(scheduler).runTaskLater(eq(plugin), retry.capture(), eq(20L));
        verify(spawner, never()).populate(any());
        first.getValue().run();
        retry.getValue().run();
        verify(spawner, times(2)).populate(chunk);
        verifyNoInteractions(manager);
    }

    @Test void teleportChecksItsDestinationEvenWhenNoChunkLoadEventOccurs() {
        Player player = mock(Player.class);
        Location origin = new Location(world, 1000, 80, 1000);
        doNothing().when(spawner).scanNearby(any());
        spawner.onTeleport(new PlayerTeleportEvent(player, origin, at, PlayerTeleportEvent.TeleportCause.COMMAND));
        ArgumentCaptor<Runnable> first = ArgumentCaptor.forClass(Runnable.class);
        ArgumentCaptor<Runnable> retry = ArgumentCaptor.forClass(Runnable.class);
        verify(scheduler).runTask(eq(plugin), first.capture());
        verify(scheduler).runTaskLater(eq(plugin), retry.capture(), eq(20L));
        verify(spawner, never()).scanNearby(any());
        first.getValue().run();
        retry.getValue().run();
        verify(spawner, times(2)).scanNearby(at);
    }

    @Test void nearbyScanOnlyUsesLoadedChunksAndHandlesNegativeCoordinates() {
        Chunk chunk = mock(Chunk.class);
        when(world.isChunkLoaded(-2, -1)).thenReturn(true);
        when(world.getChunkAt(-2, -1)).thenReturn(chunk);
        doNothing().when(spawner).populate(chunk);
        spawner.scanNearby(new Location(world, -1, 64, -1));
        verify(world, times(1)).getChunkAt(anyInt(), anyInt());
        verify(spawner).populate(chunk);
    }

    @Test void unloadingBeforeTheScheduledScanDoesNotForceTheChunkBackIn() {
        Chunk chunk = mock(Chunk.class);
        spawner.onChunkLoad(new ChunkLoadEvent(chunk, true));
        ArgumentCaptor<Runnable> first = ArgumentCaptor.forClass(Runnable.class);
        ArgumentCaptor<Runnable> retry = ArgumentCaptor.forClass(Runnable.class);
        verify(scheduler).runTask(eq(plugin), first.capture());
        verify(scheduler).runTaskLater(eq(plugin), retry.capture(), eq(20L));
        first.getValue().run();
        retry.getValue().run();
        verify(spawner, never()).populate(any());
    }

    @Test void searchNeverLoadsChunksToFindTerrain() {
        WildCampSpawner real = new WildCampSpawner(plugin, manager);
        when(world.getMinHeight()).thenReturn(-64);
        when(world.getMaxHeight()).thenReturn(320);
        assertNull(real.findSpawn(world, camp.getBoundingBox()));
        verify(world, never()).getBlockAt(anyInt(), anyInt(), anyInt());
        verify(world, never()).getChunkAt(anyInt(), anyInt());
    }

    private static PersistentDataContainer container() {
        PersistentDataContainer container = mock(PersistentDataContainer.class);
        Set<NamespacedKey> keys = new HashSet<>();
        when(container.has(any(NamespacedKey.class), eq(PersistentDataType.BYTE)))
                .thenAnswer(invocation -> keys.contains(invocation.getArgument(0)));
        doAnswer(invocation -> { keys.add(invocation.getArgument(0)); return null; })
                .when(container).set(any(NamespacedKey.class), eq(PersistentDataType.BYTE), anyByte());
        return container;
    }
}
