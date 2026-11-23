package com.mlc.mlc.mlcmain.enchantments.enchantupgrade;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UpgradeRulesTest {
    @Test
    void calculatesFailureProbabilityAndRepairTimeFromLevelSum() {
        assertEquals(0.0, UpgradeRules.failureProbability(1));
        assertEquals(0.75, UpgradeRules.failureProbability(4));
        assertEquals(4L * 60 * 60 * 1000, UpgradeRules.repairDurationMillis(4));
    }

    @Test
    void rejectsItemsWithoutEnchantments() {
        assertThrows(IllegalArgumentException.class, () -> UpgradeRules.failureProbability(0));
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
