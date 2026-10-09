package com.mlc.mlcbot.combat;

import com.mlc.mlcbot.BotStrength;
import com.mlc.mlcbot.BotType;
import com.mlc.mlcbot.practice.x.ae;
import com.mlc.mlcbot.practice.x.g1;
import com.mlc.mlcbot.practice.x.j0;
import com.mlc.mlcbot.practice.x.k1;
import java.util.Random;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OriginalCombatTest {
    @Test void difficultyKeepsOriginalTimingAndHumanizedMistakes() {
        ae easy = PracticeProfiles.cpvp(BotType.ADVANCED, BotStrength.EASY);
        ae pro = PracticeProfiles.cpvp(BotType.ADVANCED, BotStrength.EXPERT);
        assertEquals(220, easy.aW());
        assertEquals(320, easy.aX());
        assertEquals(35, easy.bB());
        assertEquals(18, easy.bE());
        assertEquals(3, pro.aW());
        assertEquals(14, pro.aX());
        assertEquals(0, pro.bB());
        assertEquals(0, pro.bE());
        Random random = new Random(42);
        for (int index = 0; index < 1000; index++) {
            long place = pro.a(random), destroy = pro.b(random);
            assertTrue(place >= 3 && place <= 14);
            assertTrue(destroy >= 2 && destroy <= 18);
        }
    }

    @Test void presetStateCannotLeakAcrossBots() {
        ae anchor = PracticeProfiles.cpvp(BotType.ANCHOR, BotStrength.HARD);
        ae mace = PracticeProfiles.cpvp(BotType.MACE, BotStrength.NORMAL);
        ae full = PracticeProfiles.cpvp(BotType.ADVANCED, BotStrength.EXPERT);
        assertTrue(anchor.aQ());
        assertFalse(anchor.aL());
        assertTrue(mace.aL());
        assertFalse(mace.aQ());
        assertTrue(full.aL() && full.aQ() && full.aO());
        anchor.g(false);
        assertTrue(full.aQ());
        assertEquals(BotStrength.EXPERT, BotStrength.parse("pro"));
        assertEquals(BotStrength.NORMAL, BotStrength.parse("medium"));
    }

    @Test void rejectsLethalAndBadExplosionTrades() {
        j0 damage = new j0(new k1());
        assertFalse(damage.a(20, 20, 100), "lethal self damage must never be traded for target damage");
        assertFalse(damage.a(3.99, 4, 30), "low HP must retain the original survival margin");
        assertFalse(damage.a(9, 20, 10), "expensive unfavorable explosion must be rejected");
        assertFalse(damage.a(4, 20, 1), "low value target damage cannot justify self damage");
        assertTrue(damage.a(3, 20, 8), "a survivable favorable explosion should remain usable");
    }

    @Test void pearlVerticalSolverMatchesItsNativeDragAndGravityModel() {
        g1 trajectory = new g1(null);
        for (double height : new double[]{-3, 0, 2, 8}) {
            for (int ticks : new int[]{12, 20, 30}) {
                double vertical = trajectory.a(height, ticks);
                assertEquals(height, trajectory.b(vertical, ticks), 0.0001);
            }
        }
    }

    @Test void pearlAimKeepsOriginalSpeedAndRejectsInvalidInputs() {
        g1 trajectory = new g1(null);
        Location start = new Location(null, 0, 65, 0);
        Location target = new Location(null, 16, 66, 8);
        Vector velocity = trajectory.a(start, target, 20);
        assertNotNull(velocity);
        assertEquals(1.5, velocity.length(), 0.00001);
        assertTrue(velocity.getX() > 0 && velocity.getZ() > 0);
        assertNull(trajectory.a(start, start, 20));
        assertNull(trajectory.a(start, target, 0));
        assertFalse(trajectory.a(new Vector(Double.NaN, 0, 0)));
        assertFalse(trajectory.a(new Vector(0, Double.POSITIVE_INFINITY, 0)));
    }
}
