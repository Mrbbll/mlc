package com.mlc.mlc.totalassault.data;

import java.util.Arrays;
import java.util.Optional;

public enum difficulties {
    NORMAL("normal", 527_000, 120),
    HARD("hard", 1_054_000, 240),
    HARDCORE("hardcore", 4_216_000, 960),
    EX("ex", 10_160_000, 1_440),
    INS("ins", 21_016_600, 1_920),
    TM("tm", 31_708_000, 2_400),
    LU("lu", 44_025_000, 2_880);

    private final String id;
    private final long baseScore;
    private final long timeCoefficient;

    difficulties(String id, long baseScore, long timeCoefficient) {
        this.id = id;
        this.baseScore = baseScore;
        this.timeCoefficient = timeCoefficient;
    }

    /** 返回配置文件和数据库中使用的难度 ID。 */
    public String getId() {
        return id;
    }

    /** 按四分钟 Boss 的基础分和累计出战毫秒数计算得分，结果向下取整。 */
    public long score(long battleMillis) {
        return Math.floorDiv(baseScore * 1000L + (3_600_000L - battleMillis) * timeCoefficient, 1000L);
    }

    /**
     * 根据配置或数据库中的 ID 查询已定义的难度。
     *
     * @param id 难度 ID，大小写须与配置一致
     * @return 对应的枚举；自定义或未知 ID 返回空 Optional
     */
    public static Optional<difficulties> fromId(String id) {
        return Arrays.stream(values()).filter(value -> value.id.equals(id)).findFirst();
    }
}
