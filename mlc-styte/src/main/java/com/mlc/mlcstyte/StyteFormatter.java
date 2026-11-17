package com.mlc.mlcstyte;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;

/**
 * Chat formatting utilities ported from VaultChatFormatter.
 * Uses Adventure Components with MiniMessage for color parsing.
 */
public final class StyteFormatter {

    // Format placeholders
    public static final String NAME_PLACEHOLDER = "{name}";
    public static final String DISPLAYNAME_PLACEHOLDER = "{displayname}";
    public static final String MESSAGE_PLACEHOLDER = "{message}";
    public static final String PREFIX_PLACEHOLDER = "{prefix}";
    public static final String SUFFIX_PLACEHOLDER = "{suffix}";

//    public static final String DEFAULT_FORMAT = "<" + PREFIX_PLACEHOLDER + NAME_PLACEHOLDER + SUFFIX_PLACEHOLDER + "> " + MESSAGE_PLACEHOLDER;

    /** Pattern matching any placeholder token */
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile(
            "\\{prefix}|\\{suffix}|\\{name}|\\{displayname}|\\{message}"
    );

    private StyteFormatter() {
        // Utility class — prevent instantiation
    }

    /**
     * Converts a MiniMessage-formatted string into an Adventure Component.
     *
     * @param miniMessageText the text with MiniMessage markup
     * @return the corresponding Component, or {@link Component#empty()} if input is null/empty
     */
    public static Component toComponent(String miniMessageText) {
        if (miniMessageText == null || miniMessageText.isEmpty()) {
            return Component.empty();
        }
        return miniMessage().deserialize(miniMessageText);
    }

    /**
     * Builds a formatted chat Component from the format pattern and placeholder values.
     * Literal text between placeholders is appended as plain text (no parsing).
     * Vault prefix/suffix should be pre-parsed via {@link #toComponent(String)}.
     *

     * @param prefix      Vault prefix Component
     * @param name        player name Component
     * @param suffix      Vault suffix Component
     * @param message     the chat message Component
     * @param displayName the player display name Component
     * @return the assembled chat Component
     */
    public static Component buildFormat(Player player,
                                        Component prefix,
                                        Component name,
                                        Component suffix,
                                        Component message,
                                        Component displayName) {
        Component connector = Component.text(" >> ", TextColor.color(255, 170, 50))
                .decoration(TextDecoration.ITALIC, false);
        String playerName = player.getName();
        Component head = miniMessage().deserialize("<head:" + player.getUniqueId() + ">");
        head = head.clickEvent(ClickEvent.suggestCommand("/msg " + playerName))
                .hoverEvent(HoverEvent.showText(toComponent("<#53ff24>点击私聊")));
        Component shownName = (displayName != null ? displayName : name)
                .clickEvent(ClickEvent.suggestCommand("/tpa " + playerName))
                .hoverEvent(HoverEvent.showText(toComponent("<#ffbd14>点击传送")));
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH.mm.ss"));
        Component timedMessage = Component.empty()
                .hoverEvent(HoverEvent.showText(toComponent("<#828282>" + time)))
                .append(message);

        TextComponent.Builder builder = Component.text()
                .append(head)
                .append(prefix)
                .append(shownName)
                .append(suffix)
                .append(connector)
                .append(timedMessage);

        return builder.build();
    }
}
