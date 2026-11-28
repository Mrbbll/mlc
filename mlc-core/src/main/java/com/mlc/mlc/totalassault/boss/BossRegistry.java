package com.mlc.mlc.totalassault.boss;

import com.mlc.mlc.totalassault.boss.saint.Saint;
import com.mlc.mlc.totalassault.boss.testboss.Testboss;

import java.util.Map;
import java.util.Optional;

/** 根据活动配置查找已经实现的 Boss。 */
public final class BossRegistry {
    private final Map<String, AssaultBoss> bosses;

    public BossRegistry() {
        AssaultBoss testBoss = new Testboss();
        AssaultBoss saint = new Saint();
        bosses = Map.of(testBoss.id(), testBoss, saint.id(), saint);
    }

    public Optional<AssaultBoss> find(String id) {
        return Optional.ofNullable(bosses.get(id));
    }
}
