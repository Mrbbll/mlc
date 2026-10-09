package com.mlc.mlcbot.combat;

import com.mlc.mlcbot.BotStrength;
import com.mlc.mlcbot.BotType;
import com.mlc.mlcbot.practice.x.ae;
import com.mlc.mlcbot.practice.x.ah;
import com.mlc.mlcbot.practice.x.af;

/** Presets use the original switches and original EASY/MEDIUM/HARD/PRO timing tables. */
public final class PracticeProfiles {
    private PracticeProfiles() {}
    public static ae cpvp(BotType type, BotStrength strength) {
        return cpvp(type, strength, new af());
    }
    public static ae cpvp(BotType type, BotStrength strength, af defaults) {
        ae settings = new ae();
        settings.a(defaults);
        settings.a(switch (strength) {
            case EASY -> ah.EASY;
            case NORMAL -> ah.MEDIUM;
            case HARD -> ah.HARD;
            case EXPERT -> ah.PRO;
        });
        settings.a(true); // Pearl pursuit, retreat and predicted landing.
        settings.b(type == BotType.MACE || type == BotType.ADVANCED);
        settings.c(true); // Original golden apple strategy.
        settings.d(true); // Obsidian placement.
        settings.e(type == BotType.ADVANCED); // Original soft obstacle breaking.
        settings.f(true); // Strafing.
        settings.g(type == BotType.ANCHOR || type == BotType.ADVANCED);
        return settings;
    }
}
