package com.mlc.mlc.totalassault.data;

public class Scorecompute {
    private Scorecompute() {}

    // 难度越高越好；同难度用时越短越好，相同则替换为后一次成绩。
    public static boolean shouldReplace(int difficulty, long elapsedMillis,
                                        int previousDifficulty, long previousElapsedMillis) {
        return difficulty > previousDifficulty
                || (difficulty == previousDifficulty && elapsedMillis <= previousElapsedMillis);
    }
}
