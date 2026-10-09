package com.mlc.mlcbot;

import java.util.Locale;

/** Maps the public strength names to PracticeBot's original difficulty presets. */
public enum BotStrength {
    EASY, NORMAL, HARD, EXPERT;

    public static BotStrength parse(String value) {
        return switch (value.toLowerCase(Locale.ROOT)) {
            case "easy", "1" -> EASY;
            case "normal", "medium", "2" -> NORMAL;
            case "hard", "3" -> HARD;
            case "expert", "pro", "4" -> EXPERT;
            default -> throw new IllegalArgumentException("强度: easy/1、normal/2、hard/3、expert/4");
        };
    }
}
