package com.mlc.mlcstyte;

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
        event.renderer((source, sourceDisplayName, message, viewer) -> {
            Component prefix;
            Component suffix;

            if (MlcStyte.vaultChat != null) {
                prefix = StyteFormatter.toComponent(MlcStyte.vaultChat.getPlayerPrefix(source));
                suffix = StyteFormatter.toComponent(MlcStyte.vaultChat.getPlayerSuffix(source));
            } else {
                prefix = Component.empty();
                suffix = Component.empty();
            }

            Component name = Component.text(source.getName());

            return StyteFormatter.buildFormat(
                    MlcStyte.FORMAT, prefix, name, suffix, message, sourceDisplayName
            );
        });
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
    public void onServiceUnregister(ServiceRegisterEvent event) {
        if (event.getProvider().getService() == Chat.class) {
            MlcStyte.refreshVault();
        }
    }
}
