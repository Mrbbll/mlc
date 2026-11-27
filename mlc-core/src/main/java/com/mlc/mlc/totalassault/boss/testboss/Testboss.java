package com.mlc.mlc.totalassault.boss.testboss;

import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Zombie;
import java.util.Objects;

public class Testboss {
    private Testboss() {}

    @SuppressWarnings("deprecation")
    public static Zombie spawn(Location location, double maxHealth, double attackDamage,
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
}
