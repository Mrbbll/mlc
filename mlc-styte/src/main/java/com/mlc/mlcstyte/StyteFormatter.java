package com.mlc.mlcstyte;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Chat formatting utilities ported from VaultChatFormatter.
 * Uses Adventure Components and LegacyComponentSerializer for modern Paper API.
 */
public final class StyteFormatter {

    // Format placeholders
    public static final String NAME_PLACEHOLDER = "{name}";
    public static final String DISPLAYNAME_PLACEHOLDER = "{displayname}";
    public static final String MESSAGE_PLACEHOLDER = "{message}";
    public static final String PREFIX_PLACEHOLDER = "{prefix}";
    public static final String SUFFIX_PLACEHOLDER = "{suffix}";

    /** The default format */
    public static final String DEFAULT_FORMAT = "<" + PREFIX_PLACEHOLDER + NAME_PLACEHOLDER + SUFFIX_PLACEHOLDER + "> " + MESSAGE_PLACEHOLDER;

    /** Pattern matching any placeholder token */
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile(
            "\\{prefix}|\\{suffix}|\\{name}|\\{displayname}|\\{message}"
    );

    /**
     * Serializer that converts legacy '&' color codes (including &#rrggbb hex) to Adventure Components.
     */
    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.builder()
            .character('&')
            .hexColors()
            .build();

    private StyteFormatter() {
        // Utility class — prevent instantiation
    }

    /**
     * Converts a legacy color-coded string (using & codes) into an Adventure Component.
     *
     * @param legacyText the text with & color codes
     * @return the corresponding Component, or {@link Component#empty()} if input is null
     */
    public static Component toComponent(String legacyText) {
        if (legacyText == null || legacyText.isEmpty()) {
            return Component.empty();
        }
        return LEGACY_SERIALIZER.deserialize(legacyText);
    }

    /**
     * Builds a formatted chat Component from the format pattern and placeholder values.
     * Text between placeholders is parsed for & color codes via LegacyComponentSerializer.
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
            // Append literal text before this placeholder (with legacy color parsing)
            String before = format.substring(lastEnd, matcher.start());
            if (!before.isEmpty()) {
                builder.append(LEGACY_SERIALIZER.deserialize(before));
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

        // Append remaining literal text after the last placeholder
        String after = format.substring(lastEnd);
        if (!after.isEmpty()) {
            builder.append(LEGACY_SERIALIZER.deserialize(after));
        }

        return builder.build();
    }
}
