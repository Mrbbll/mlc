package com.mlc.mlc.totalassault.boss.testboss;

/** 测试僵尸各难度的属性，创建挑战和生成僵尸共用。 */
public record TestBossDifficulty(double health, double damage, double speed) {
    public static TestBossDifficulty of(String id) {
        return switch (id) {
            case "normal" -> new TestBossDifficulty(40, 2, 0.23);
            case "hard" -> new TestBossDifficulty(80, 3, 0.24);
            case "hardcore" -> new TestBossDifficulty(140, 4, 0.25);
            case "ex" -> new TestBossDifficulty(240, 5, 0.26);
            case "ins" -> new TestBossDifficulty(400, 6, 0.27);
            case "tm" -> new TestBossDifficulty(640, 7, 0.28);
            case "lu" -> new TestBossDifficulty(1000, 8, 0.29);
            default -> throw new IllegalArgumentException("未知难度：" + id);
        };
    }
}
