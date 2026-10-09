package com.mlc.mlcbot;

import java.util.Objects;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;

/** Entity-local bot marker, available before spawn and throughout death/removal events. */
public final class BotIdentity {
    private static final NamespacedKey BOT_KEY = Objects.requireNonNull(NamespacedKey.fromString("mlc:bot"));

    private BotIdentity() { }

    public static void mark(Entity entity) {
        entity.getPersistentDataContainer().set(BOT_KEY, PersistentDataType.BYTE, (byte) 1);
    }

    public static boolean isBot(Entity entity) {
        return entity != null && Byte.valueOf((byte) 1).equals(
                entity.getPersistentDataContainer().get(BOT_KEY, PersistentDataType.BYTE));
    }
}
