package com.mlc.mlc.totalassault.boss.testboss;

import com.mlc.mlc.totalassault.boss.AssaultBoss;
import com.mlc.mlc.totalassault.boss.BossAttributes;
import com.mlc.mlc.totalassault.boss.BossHandle;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Zombie;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/** 原版僵尸实现，仅用于验证总力战流程。 */
public class Testboss implements AssaultBoss {
    @Override public String id() { return "test_zombie"; }

    @Override
    public BossAttributes attributes(String difficulty) {
        TestBossDifficulty values = TestBossDifficulty.of(difficulty);
        return new BossAttributes(values.health(), values.damage(), values.speed());
    }

    // 测试僵尸暂时不禁用任何模型或材质。
    @Override public Set<String> bannedModels() { return Set.of(); }
    @Override public Set<Material> bannedMaterials() { return Set.of(); }

    @Override
    public BossHandle spawn(Location location, String difficulty, double health) {
        BossAttributes values = attributes(difficulty);
        Zombie zombie = spawnZombie(location, values.health(), values.damage(), values.speed(), health);
        return new BossHandle() {
            @Override public UUID entityId() { return zombie.getUniqueId(); }
            @Override public double health() { return zombie.getHealth(); }
            @Override public void remove() { zombie.remove(); }
        };
    }

    @SuppressWarnings("deprecation")
    private static Zombie spawnZombie(Location location, double maxHealth, double attackDamage,
                               double movementSpeed, double health) {
        if (maxHealth <= 0 || health <= 0 || health > maxHealth || attackDamage < 0 || movementSpeed < 0) {
            throw new IllegalArgumentException("Invalid test boss attributes");
        }
        return Objects.requireNonNull(location.getWorld()).spawn(location, Zombie.class, zombie -> {
            Objects.requireNonNull(zombie.getAttribute(Attribute.MAX_HEALTH)).setBaseValue(maxHealth);
            Objects.requireNonNull(zombie.getAttribute(Attribute.ATTACK_DAMAGE)).setBaseValue(attackDamage);
            Objects.requireNonNull(zombie.getAttribute(Attribute.MOVEMENT_SPEED)).setBaseValue(movementSpeed);
            zombie.setHealth(health);
            // 使用兼容当前服务端版本的接口，防止僵尸远离玩家时消失。
            zombie.setRemoveWhenFarAway(false);
            zombie.setPersistent(true);
            zombie.setCanPickupItems(false);
            zombie.setConversionTime(-1);
        });
    }


    //
}
