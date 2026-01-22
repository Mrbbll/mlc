package com.mlc.mlc.dialog.Listener;

import com.destroystokyo.paper.event.player.PlayerConnectionCloseEvent;
import com.mlc.mlc.respacksender.packsender;
import io.papermc.paper.connection.PlayerConfigurationConnection;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.event.connection.configuration.AsyncPlayerConnectionConfigureEvent;
import io.papermc.paper.event.player.PlayerCustomClickEvent;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jspecify.annotations.NullMarked;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static com.mlc.mlc.dialog.Serverjoindialog.serverjoindialog;

@NullMarked
public class ServerJoinListener implements Listener {

    //存储当前等待玩家
    private final Map<UUID, CompletableFuture<Boolean>> awaitingResponse = new ConcurrentHashMap<>();

    @EventHandler
    void onPlayerConfigure(AsyncPlayerConnectionConfigureEvent event) {
        Dialog dialog = serverjoindialog;
        if (dialog == null) {
            return;
        }

        PlayerConfigurationConnection connection = event.getConnection();
        //获取版本


        UUID uniqueId = connection.getProfile().getId();
        if (uniqueId == null) {
            return;
        }

        //设置超时
        CompletableFuture<Boolean> response = new CompletableFuture<>();
        response.completeOnTimeout(false, 1, TimeUnit.MINUTES);

        awaitingResponse.put(uniqueId, response);
        // 显示对话框
        Audience audience = connection.getAudience();
        audience.showDialog(dialog);

        // 等待玩家响应，处理结果
        if (!response.join()) {
            audience.closeDialog();
            connection.disconnect(Component.text("再见 :(", NamedTextColor.RED));
        }

        awaitingResponse.remove(uniqueId);
    }

    //点击事件处理
    @EventHandler
    void onHandleDialog(PlayerCustomClickEvent event) {
        if (!(event.getCommonConnection() instanceof PlayerConfigurationConnection configurationConnection)) {
            return;
        }

        UUID uniqueId = configurationConnection.getProfile().getId();
        if (uniqueId == null) {
            return;
        }

        Key key = event.getIdentifier();
        if (key.equals(Key.key("mlc:gonggao/disagree"))) {
            setConnectionJoinResult(uniqueId, false);
        } else if (key.equals(Key.key("mlc:gonggao/agree"))) {
            setConnectionJoinResult(uniqueId, true);
        }
    }

    //当链接被迫关了，移除他
    @EventHandler
    void onConnectionClose(PlayerConnectionCloseEvent event) {
        awaitingResponse.remove(event.getPlayerUniqueId());
    }

    //处理点击结果
    private void setConnectionJoinResult(UUID uniqueId, boolean value) {
        CompletableFuture<Boolean> future = awaitingResponse.get(uniqueId);
        if (future != null) {
            future.complete(value);
        }
    }
}