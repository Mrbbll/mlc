package com.mlc.mlcbot.nms;

import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Entity;

/** Ground state used by Paper 26.3's CraftEntity, including embedded bot players. */
public final class EntityGroundState {
    private EntityGroundState() { }

    public static boolean isOnGround(Entity entity) {
        // Player.isOnGround() is deprecated and has no replacement player API.
        // Read the same native flag as CraftEntity without calling the deprecated override.
        return ((CraftEntity) entity).getHandle().onGround();
    }
}
