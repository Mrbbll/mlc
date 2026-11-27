package com.mlc.mlc.totalassault.data;

import java.util.Arrays;
import java.util.Optional;

public enum difficulties {
    NORMAL("normal"),
    HARD("hard"),
    HARDCORE("hardcore"),
    EX("ex"),
    INS("ins"),
    TM("tm"),
    LU("lu");

    private final String id;

    difficulties(String id) {
        this.id = id;
    }

    /** 返回配置文件和数据库中使用的难度 ID。 */
    public String getId() {
        return id;
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
