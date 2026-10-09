package com.mlc.mlcbot;

import com.mlc.mlcbot.practice.BotTrait;
import com.mlc.mlcbot.practice.bridge.CitizensAPI;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BotIdentityTest {
    @Test
    void spawnedBotIsRecognizedBeforeNpcRegistryRegistration() {
        Player bot = player(false);
        assertNull(CitizensAPI.getNPCRegistry().getNPC(bot));
        BotIdentity.mark(bot);

        assertTrue(BotIdentity.isBot(bot));
        assertTrue(BotTrait.isLiveBotTarget(bot));
        assertFalse(BotTrait.isLiveHumanTarget(bot));
    }

    @Test
    void ordinaryPlayerRemainsAHumanTarget() {
        Player human = player(false);
        assertFalse(BotIdentity.isBot(human));
        assertFalse(BotTrait.isLiveBotTarget(human));
        assertTrue(BotTrait.isLiveHumanTarget(human));
        assertFalse(BotIdentity.isBot(null));
    }

    @Test
    void foreignNpcIsStillExcludedWithoutBeingMarkedAsMlcBot() {
        Player npc = player(true);
        assertFalse(BotIdentity.isBot(npc));
        assertTrue(BotTrait.isLiveBotTarget(npc));
        assertFalse(BotTrait.isLiveHumanTarget(npc));
    }

    private static Player player(boolean foreignNpc) {
        Map<NamespacedKey, Object> data = new HashMap<>();
        PersistentDataContainer container = (PersistentDataContainer) Proxy.newProxyInstance(
                PersistentDataContainer.class.getClassLoader(), new Class<?>[]{PersistentDataContainer.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "set" -> { data.put((NamespacedKey) args[0], args[2]); yield null; }
                    case "get" -> data.get(args[0]);
                    default -> throw new UnsupportedOperationException(method.getName());
                });
        UUID uuid = UUID.randomUUID();
        return (Player) Proxy.newProxyInstance(Player.class.getClassLoader(), new Class<?>[]{Player.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "getUniqueId" -> uuid;
                    case "getPersistentDataContainer" -> container;
                    case "hasMetadata" -> foreignNpc && "NPC".equals(args[0]);
                    case "isValid", "isOnline" -> true;
                    case "isDead" -> false;
                    case "getGameMode" -> GameMode.SURVIVAL;
                    default -> throw new UnsupportedOperationException(method.getName());
                });
    }
}
