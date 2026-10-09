package com.mlc.mlcbot.navigation;

import java.util.HashSet;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BotNavigatorTest {
    private static final class Grid implements BotNavigator.Terrain {
        final Set<String> blocked = new HashSet<>();
        int examined;
        public boolean canStand(int x, int y, int z) {
            examined++;
            return y == 64 && !blocked.contains(x + ":" + z);
        }
        public boolean passable(int x, int y, int z) {
            return !blocked.contains(x + ":" + z);
        }
    }

    @Test void routesAroundWallInsteadOfCrossingIt() {
        Grid grid = new Grid();
        for (int z = -2; z <= 2; z++) grid.blocked.add("3:" + z);
        BotNavigator nav = new BotNavigator(grid);
        Location at = new Location(null, 0.5, 64, 0.5);
        Location target = new Location(null, 7.5, 64, 0.5);
        boolean detoured = false;
        for (int tick = 0; tick < 200; tick++) {
            Vector move = nav.direction(at, target, tick).multiply(0.2);
            at.add(move);
            assertFalse(grid.blocked.contains(at.getBlockX() + ":" + at.getBlockZ()), "walked through wall");
            detoured |= Math.abs(at.getZ() - 0.5) > 2.5;
            if (at.toVector().distance(target.toVector()) < 1.5) break;
        }
        assertTrue(detoured);
        assertTrue(at.toVector().distance(target.toVector()) < 1.5, "failed to reach opponent around wall");
    }

    @Test void abandonsPathWhenTerrainChanges() {
        Grid grid = new Grid();
        BotNavigator nav = new BotNavigator(grid);
        Location at = new Location(null, 0.5, 64, 0.5), target = new Location(null, 5.5, 64, 0.5);
        assertTrue(nav.direction(at, target, 0).lengthSquared() > 0);
        grid.blocked.add("1:0");
        assertEquals(0, nav.direction(at, target, 1).lengthSquared());
        for (int tick = 2; tick < 12; tick++) nav.direction(at, target, tick);
        assertTrue(nav.direction(at, target, 12).lengthSquared() > 0, "did not plan a replacement route");
    }

    @Test void boundsWorkForUnreachableTargetAndNeverLeavesSearchRange() {
        Grid grid = new Grid();
        BotNavigator nav = new BotNavigator(grid);
        Location at = new Location(null, 0.5, 64, 0.5), target = new Location(null, 1000.5, 64, 0.5);
        for (int tick = 0; tick < 25; tick++) {
            int before = grid.examined;
            Vector move = nav.direction(at, target, tick);
            assertTrue(grid.examined - before <= 96 * 20 + 1, "search exceeded tick budget");
            assertTrue(Double.isFinite(move.lengthSquared()));
        }
        assertTrue(grid.examined <= 2048 * 20 + 25, "search exceeded total node limit");
    }
}
