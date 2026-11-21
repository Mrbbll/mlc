package com.mlc.mlc.mlcmain.signin.Listener;

import com.mlc.mlc.mlcmain.lobby.LobbyWorld;
import com.mlc.mlc.mlcmain.signin.SigninManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import static com.mlc.mlc.Mlc.instance;
import static com.mlc.mlc.Mlc.miniMessage;

public class JoinMoneyListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (SigninManager.hasSignedInBefore(player)) {
            player.sendMessage(miniMessage.deserialize("<green><b>欢迎回来"));
        } else {
            player.sendMessage(miniMessage.deserialize("<green><b>欢迎来到MLC服务器"));
            Bukkit.getScheduler().runTaskLater(instance, () -> LobbyWorld.teleporttolobbyworld(player), 1L);
        }

        SigninManager.signIn(player, false);
    }
}
