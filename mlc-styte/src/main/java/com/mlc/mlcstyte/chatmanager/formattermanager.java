package com.mlc.mlcstyte.chatmanager;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class formattermanager implements Listener {
    public enum Target {
        NAME, MESSAGE
    }

    public record Selection(ChatEffect name, ChatEffect message) {
        public ChatEffect effect(Target target) {
            return target == Target.NAME ? name : message;
        }
    }

    private static final Selection DEFAULT = new Selection(ChatEffect.DEFAULT, ChatEffect.DEFAULT);

    private final NamespacedKey nameKey;
    private final NamespacedKey messageKey;
    // 异步聊天只读取不可变快照。玩家持久化数据在服务器线程上读写。
    private final Map<UUID, Selection> selections = new ConcurrentHashMap<>();

    public formattermanager(JavaPlugin plugin) {
        nameKey = new NamespacedKey(plugin, "chat_effect_name");
        messageKey = new NamespacedKey(plugin, "chat_effect_message");
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        plugin.getServer().getOnlinePlayers().forEach(this::load);
    }

    public Selection selection(UUID playerId) {
        return selections.getOrDefault(playerId, DEFAULT);
    }

    public void select(Player player, Target target, ChatEffect effect) {
        NamespacedKey key = target == Target.NAME ? nameKey : messageKey;
        PersistentDataContainer data = player.getPersistentDataContainer();
        if (effect == ChatEffect.DEFAULT) {
            data.remove(key);
        } else {
            data.set(key, PersistentDataType.STRING, effect.name());
        }
        load(player);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        load(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        selections.remove(event.getPlayer().getUniqueId());
    }

    private void load(Player player) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        selections.put(player.getUniqueId(), new Selection(
                ChatEffect.fromKey(data.get(nameKey, PersistentDataType.STRING)),
                ChatEffect.fromKey(data.get(messageKey, PersistentDataType.STRING))));
    }
}
