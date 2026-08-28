package com.mlc.mlcstyte;

import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;

import net.kyori.adventure.text.Component;
import net.milkbowl.vault.chat.Chat;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServiceRegisterEvent;
import org.bukkit.event.server.ServiceUnregisterEvent;

/**
 * Chat listener that applies Vault-based formatting via Paper's modern AsyncChatEvent.
 * Ported from VaultChatFormatter and modernized for Adventure Components.
 */
public class StyteChatListener implements Listener {

    /**
     * Set a custom renderer that builds the formatted chat Component using
     * Vault prefix/suffix and the configured format pattern.
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onAsyncChat(AsyncChatEvent event) {

        event.renderer(ChatRenderer.viewerUnaware((
                source, sourceDisplayName,message)->{
                    Component prefix = Component.empty();
                    Component suffix = Component.empty();

                    Chat chat = MlcStyte.vaultChat;
                    if(chat!=null){
                        prefix = StyteFormatter.toComponent(chat.getPlayerPrefix(source));
                        suffix = StyteFormatter.toComponent(chat.getPlayerSuffix(source));
                    }

                    return StyteFormatter.buildFormat(
                            MlcStyte.FORMAT,
                            prefix,
                            Component.text(source.getName()),
                            suffix,
                            message,
                            sourceDisplayName
                    );
                }
            )
        );
    }

    /**
     * Refresh Vault Chat when a new service is registered.
     */
    @EventHandler
    public void onServiceRegister(ServiceRegisterEvent event) {
        if (event.getProvider().getService() == Chat.class) {
            MlcStyte.refreshVault();
        }
    }

    /**
     * Refresh Vault Chat when a service is unregistered.
     */
    @EventHandler
    public void onServiceUnregister(ServiceUnregisterEvent event) {
        if (event.getProvider().getService() == Chat.class) {
            MlcStyte.refreshVault();
        }
    }
}
