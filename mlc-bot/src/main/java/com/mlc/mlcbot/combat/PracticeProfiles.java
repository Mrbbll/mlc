package com.mlc.mlcbot.combat;

import com.mlc.mlcbot.BotStrength;
import com.mlc.mlcbot.BotType;
import com.mlc.mlcbot.practice.x.CpvpSettings;
import com.mlc.mlcbot.practice.x.CpvpDifficulty;
import com.mlc.mlcbot.practice.x.CpvpDefaults;

/** Presets use the original switches and original EASY/MEDIUM/HARD/PRO timing tables. */
public final class PracticeProfiles {
    private PracticeProfiles() {}
    public static CpvpSettings cpvp(BotType type, BotStrength strength) {
        return cpvp(type, strength, new CpvpDefaults());
    }
    public static CpvpSettings cpvp(BotType type, BotStrength strength, CpvpDefaults defaults) {
        CpvpSettings settings = new CpvpSettings();
        settings.a(defaults);
        settings.a(switch (strength) {
            case EASY -> CpvpDifficulty.EASY;
            case NORMAL -> CpvpDifficulty.MEDIUM;
            case HARD -> CpvpDifficulty.HARD;
            case EXPERT -> CpvpDifficulty.PRO;
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
