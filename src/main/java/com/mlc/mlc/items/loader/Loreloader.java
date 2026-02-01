package com.mlc.mlc.items.loader;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.stream.Collectors;

public class Loreloader {
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    public static List<Component> getLoreComponents(ConfigurationSection section) {
        List<String> loreStrings = section.getStringList("lore");
        return loreStrings.stream()
                .map(miniMessage::deserialize) // 解析 MiniMessage 格式
                .collect(Collectors.toList());
    }
}
