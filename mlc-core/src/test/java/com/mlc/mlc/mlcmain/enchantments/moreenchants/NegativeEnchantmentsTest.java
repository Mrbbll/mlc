package com.mlc.mlc.mlcmain.enchantments.moreenchants;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NegativeEnchantmentsTest {
    @Test
    void brittleAlwaysAddsOneDamageAndBreaksAtMaximum() {
        assertEquals(11, NegativeEnchantments.damageAfterUse(10, 100, true, false, 0));
        assertEquals(-1, NegativeEnchantments.damageAfterUse(99, 100, true, false, 0));
    }

    @Test
    void fragileBreaksBelowOnePercentAndPreservesOtherOutcomes() {
        assertEquals(-1, NegativeEnchantments.damageAfterUse(10, 100, false, true, 0.009));
        assertEquals(10, NegativeEnchantments.damageAfterUse(10, 100, false, true, 0.01));
        assertEquals(11, NegativeEnchantments.damageAfterUse(10, 100, true, true, 0.5));
    }
}
