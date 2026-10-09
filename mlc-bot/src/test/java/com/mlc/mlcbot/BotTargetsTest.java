package com.mlc.mlcbot;

import java.util.List;
import java.util.UUID;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BotTargetsTest {
    @Test void choosesNearestSurvivalHumanInsteadOfACloserNpcOrOtherGameMode() {
        World world = mock(World.class);
        Player bot = player(world, 0, GameMode.SURVIVAL);
        Player far = player(world, 8, GameMode.SURVIVAL);
        Player near = player(world, 3, GameMode.SURVIVAL);
        Player creative = player(world, 1, GameMode.CREATIVE);
        Player adventure = player(world, 1, GameMode.ADVENTURE);
        Player spectator = player(world, 1, GameMode.SPECTATOR);
        Player npc = player(world, 1, GameMode.SURVIVAL);
        when(npc.hasMetadata("NPC")).thenReturn(true);
        assertSame(near, BotTargets.nearest(bot, List.of(bot, far, creative, adventure, spectator, npc, near)));
    }

    @Test void returnsNoTargetForDeadOfflineOtherWorldAndOutOfRangePlayers() {
        World world = mock(World.class);
        Player bot = player(world, 0, GameMode.SURVIVAL);
        Player dead = player(world, 1, GameMode.SURVIVAL);
        when(dead.isDead()).thenReturn(true);
        Player offline = player(world, 1, GameMode.SURVIVAL);
        when(offline.isOnline()).thenReturn(false);
        Player elsewhere = player(mock(World.class), 1, GameMode.SURVIVAL);
        Player far = player(world, 49, GameMode.SURVIVAL);
        assertNull(BotTargets.nearest(bot, List.of(dead, offline, elsewhere, far)));
    }

    @Test void reevaluatesNearestTargetAndIncludesTheRangeBoundary() {
        World world = mock(World.class);
        Player bot = player(world, 0, GameMode.SURVIVAL);
        Player first = player(world, 2, GameMode.SURVIVAL);
        Player second = player(world, 48, GameMode.SURVIVAL);
        assertSame(first, BotTargets.nearest(bot, List.of(first, second)));
        when(first.getGameMode()).thenReturn(GameMode.CREATIVE);
        assertSame(second, BotTargets.nearest(bot, List.of(first, second)));
        when(first.getGameMode()).thenReturn(GameMode.SURVIVAL);
        when(second.getLocation()).thenReturn(new Location(world, 1, 64, 0));
        assertSame(second, BotTargets.nearest(bot, List.of(first, second)));
    }

    private static Player player(World world, double x, GameMode mode) {
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(UUID.randomUUID());
        when(player.getWorld()).thenReturn(world);
        when(player.getLocation()).thenReturn(new Location(world, x, 64, 0));
        when(player.getGameMode()).thenReturn(mode);
        when(player.isValid()).thenReturn(true);
        when(player.isOnline()).thenReturn(true);
        when(player.getPersistentDataContainer()).thenReturn(mock(PersistentDataContainer.class));
        return player;
    }
}
