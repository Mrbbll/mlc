package com.mlc.mlcbot.practice.x;

import com.mlc.mlcbot.BotStrength;
import com.mlc.mlcbot.BotType;
import com.mlc.mlcbot.combat.PracticeProfiles;
import com.mlc.mlcbot.nms.EntityGroundState;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CrystalBaseSelectorTest {
    @ParameterizedTest
    @EnumSource(value = BotType.class, names = {"CPVP", "ANCHOR", "MACE", "ADVANCED"})
    void airborneBaseSearchTraversesOrderedHeightSetWithoutListCast(BotType type) {
        World world = mock(World.class);
        when(world.getMinHeight()).thenReturn(-64);
        when(world.getMaxHeight()).thenReturn(320);
        Block stone = mock(Block.class);
        Material solidMaterial = mock(Material.class);
        when(stone.getType()).thenReturn(solidMaterial);
        when(world.getBlockAt(anyInt(), anyInt(), anyInt())).thenReturn(stone);

        Player bot = mock(Player.class);
        Player target = mock(Player.class);
        when(bot.getWorld()).thenReturn(world);
        when(target.getWorld()).thenReturn(world);
        when(bot.getLocation()).thenReturn(new Location(world, 0.5, 64, 0.5));
        when(target.getLocation()).thenReturn(new Location(world, 3.5, 67, 0.5));

        CpvpCombatController combat = mock(CpvpCombatController.class);
        when(combat.l(bot, target)).thenReturn(true);
        CrystalBaseSelector selector = new CrystalBaseSelector(combat);
        CpvpSettings settings = PracticeProfiles.cpvp(type, BotStrength.HARD);

        try (var ground = mockStatic(EntityGroundState.class)) {
            ground.when(() -> EntityGroundState.isOnGround(target)).thenReturn(false);
            Block result = assertDoesNotThrow(() -> selector.e(bot, target, settings, new CpvpCombatState(), 1000L));
            assertNull(result, "An arena of non-air blocks has no empty crystal placement candidate");
            // This corner at the target's height is visited by the placement scan, not its preliminary scans.
            verify(world, atLeastOnce()).getBlockAt(5, 67, 2);
        }
    }
}
