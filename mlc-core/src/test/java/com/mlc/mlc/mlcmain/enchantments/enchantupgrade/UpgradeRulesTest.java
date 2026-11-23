package com.mlc.mlc.mlcmain.enchantments.enchantupgrade;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UpgradeRulesTest {
    @Test
    void weightsTheTargetLevelTwiceAsMuchAsOtherLevels() {
        assertEquals(1.0, UpgradeRules.successProbability(1, 0));
        assertEquals(0.25, UpgradeRules.successProbability(3, 2));
        assertEquals(1.0 / 5.5, UpgradeRules.successProbability(5, 1));
        assertEquals(1.0 / 3.5, UpgradeRules.successProbability(1, 5));
    }

    @Test
    void floorsTheSuccessProbabilityAtOnePercent() {
        assertEquals(0.02, UpgradeRules.successProbability(50, 0));
        assertEquals(0.01, UpgradeRules.successProbability(100, 0));
        assertEquals(0.01, UpgradeRules.successProbability(90, 60));
    }

    @Test
    void calculatesRepairTimeFromLevelSum() {
        assertEquals(4L * 60 * 60 * 1000, UpgradeRules.repairDurationMillis(4));
    }

    @Test
    void rejectsItemsWithoutEnchantments() {
        assertThrows(IllegalArgumentException.class, () -> UpgradeRules.successProbability(0, 0));
        assertThrows(IllegalArgumentException.class, () -> UpgradeRules.successProbability(1, -1));
        assertThrows(IllegalArgumentException.class, () -> UpgradeRules.repairDurationMillis(0));
    }

    @Test
    void repairRuneAllowsEarlyRepairAndNaturalRepairRequiresDeadline() {
        assertFalse(UpgradeRules.canRepair(1_000, 999, false));
        assertTrue(UpgradeRules.canRepair(1_000, 1_000, false));
        assertTrue(UpgradeRules.canRepair(1_000, 999, true));
        assertFalse(UpgradeRules.canRepair(-1, 1_000, true));
    }
}
