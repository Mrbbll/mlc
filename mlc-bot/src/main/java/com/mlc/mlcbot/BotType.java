package com.mlc.mlcbot;

import java.util.Locale;

/** Original modes plus fixed presets for PracticeBot's CPVP strategy switches. */
public enum BotType {
    NORMAL, CPVP, ANCHOR, MACE, ADVANCED, DUMMY;

    public boolean isCpvp() { return this != NORMAL && this != DUMMY; }

    public static BotType parse(String value) {
        return switch (value.toLowerCase(Locale.ROOT)) {
            case "normal", "sword" -> NORMAL;
            case "cpvp", "crystal" -> CPVP;
            case "anchor", "cpvp-anchor" -> ANCHOR;
            case "mace", "cpvp-mace" -> MACE;
            case "advanced", "full", "cpvp-full" -> ADVANCED;
            case "dummy" -> DUMMY;
            default -> throw new IllegalArgumentException("类型: normal、cpvp、anchor、mace、advanced、dummy");
        };
    }
}
