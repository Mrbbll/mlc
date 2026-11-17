package com.mlc.mlc.mlcmain.chat.listener;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class Chatlistener implements Listener {
    @EventHandler
    public void onchat(AsyncChatEvent event) {
        if (!PlainTextComponentSerializer.plainText().serialize(event.message()).contains("[item]")) return;
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand().clone();
        if (item.getType().isAir()) return;
        Component shownItem = item.effectiveName().hoverEvent(item.asHoverEvent());
        event.message(event.message().replaceText(builder ->
                builder.matchLiteral("[item]").replacement(shownItem)));
    }
}
