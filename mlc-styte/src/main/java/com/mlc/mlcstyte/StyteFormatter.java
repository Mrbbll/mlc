package com.mlc.mlcstyte;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

import java.util.regex.Matcher;
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
     * @param format      the format string with placeholders
     * @param prefix      Vault prefix Component
     * @param name        player name Component
     * @param suffix      Vault suffix Component
     * @param message     the chat message Component
     * @param displayName the player display name Component
     * @return the assembled chat Component
     */
    public static Component buildFormat(String format,
                                        Component prefix,
                                        Component name,
                                        Component suffix,
                                        Component message,
                                        Component displayName) {
        TextComponent.Builder builder = Component.text();
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(format);
        int lastEnd = 0;

        while (matcher.find()) {
            // Append literal text before this placeholder — plain text, no parsing
            String before = format.substring(lastEnd, matcher.start());
            if (!before.isEmpty()) {
                builder.append(Component.text(before));
            }

            // Append the replacement Component for the placeholder
            String placeholder = matcher.group();
            switch (placeholder) {
                case PREFIX_PLACEHOLDER  -> builder.append(prefix);
                case SUFFIX_PLACEHOLDER  -> builder.append(suffix);
                case NAME_PLACEHOLDER    -> builder.append(name);
                case DISPLAYNAME_PLACEHOLDER -> builder.append(displayName);
                case MESSAGE_PLACEHOLDER -> builder.append(message);
                default                  -> builder.append(Component.text(placeholder));
            }
            lastEnd = matcher.end();
        }


        // Append remaining literal text after the last placeholder — plain text
        String after = format.substring(lastEnd);
        if (!after.isEmpty()) {
            builder.append(Component.text(after));
        }

        return builder.build();
    }
}
