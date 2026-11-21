package com.mlc.mlcstyte.chatmanager;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

public enum ChatEffect {
    DEFAULT("默认样式", Material.PAPER, null),
    SEASICK("海浪摇摆", Material.KELP, 0xFC05F8),
    LEAPFROG("跃动", Material.FROGSPAWN, 0xFC09F8),
    TREMOR("震颤", Material.PISTON, 0xFC0DF8),
    JELLY("果冻弹跳", Material.HONEYCOMB, 0xFC11F8),
    CARTWHEEL("翻转", Material.FIREWORK_ROCKET, 0xFC15F8),
    COINFLIP("硬币翻面", Material.GOLD_NUGGET, 0xFC19F8),
    HEARTBEAT("心跳", Material.POPPY, 0xFC1DF8),
    TYPEWRITER("打字机", Material.WRITABLE_BOOK, 0xFC21F8),
    HAUNTED("幽灵低语", Material.SOUL_LANTERN, 0xFC25F8),
    BANNER("旗帜飘扬", Material.CYAN_BANNER, 0xFC29F8),
    SPECTRUM("流动光谱", Material.PRISMARINE_CRYSTALS, 0xFC2DF8),
    SUNSET("落日余晖", Material.ORANGE_DYE, 0xFC31F8),
    ENCHANTED("附魔微光", Material.ENCHANTED_BOOK, 0xFC35F8),
    HOLOGRAM("全息投影", Material.AMETHYST_SHARD, 0xFC39F8),
    CONFETTI("彩纸飞舞", Material.FIREWORK_STAR, 0xFC3DF8),
    LEGENDARY("传奇辉光", Material.NETHER_STAR, 0xFC41F8),
    BLAZE("烈焰", Material.BLAZE_POWDER, 0xFC45F8),
    MAGMA("岩浆", Material.MAGMA_CREAM, 0xFC49F8),
    FROSTBITE("寒霜", Material.PACKED_ICE, 0xFC4DF8),
    FISHTANK("水族箱", Material.TROPICAL_FISH, 0xFC51F8),
    MOLTENTANK("熔岩池", Material.LAVA_BUCKET, 0xFC55F8),
    SLIME("史莱姆", Material.SLIME_BALL, 0xFC59F8),
    RADIOACTIVE("辐射辉光", Material.GLOW_LICHEN, 0xFC5DF8),
    STARLIGHT("流转星光", Material.END_CRYSTAL, 0xFC61F8),
    DATARAIN("数据雨", Material.ECHO_SHARD, 0xFC65F8),
    TUBELIGHT("霓虹灯管", Material.END_ROD, 0xFC69F8),
    OVERCHARGED("电荷过载", Material.LIGHTNING_ROD, 0xFC6DF8),
    CHUNKY("像素方块", Material.BRICKS, 0xFC71F8),
    STICKER("彩色贴纸", Material.PAINTING, 0xFC75F8),
    CORRUPTED("故障失真", Material.CRYING_OBSIDIAN, 0xFC79F8),
    EMBERS("飞舞余烬", Material.CAMPFIRE, 0xFC7DF8),
    DECRYPTING("解密中", Material.TRIAL_KEY, 0xFC81F8),
    RAINBOW("经典彩虹", Material.MAGENTA_DYE, 0xFC80F8),
    GOLD_GLINT("金色闪光", Material.GOLD_INGOT, 0xFA50F9),
    RED_PULSE("红色脉冲", Material.REDSTONE, 0xFA51F9);

    private static final List<ChatEffect> PRESETS = Arrays.stream(values())
            .filter(effect -> effect != DEFAULT)
            .toList();

    private final String label;
    private final Material icon;
    private final TextColor color;

    ChatEffect(String label, Material icon, Integer marker) {
        this.label = label;
        this.icon = icon;
        this.color = marker == null ? null : TextColor.color(marker);
    }

    public String label() {
        return label;
    }

    public Material icon() {
        return icon;
    }

    public static List<ChatEffect> presets() {
        return PRESETS;
    }

    static ChatEffect fromKey(String key) {
        for (ChatEffect effect : PRESETS) {
            if (effect.name().equals(key)) return effect;
        }
        return DEFAULT;
    }

    public Component apply(Component component) {
        return color == null ? component : recolor(component, NamedTextColor.WHITE);
    }

    private Component recolor(Component component, TextColor inheritedColor) {
        HoverEvent<?> hover = component.hoverEvent();
        if (hover != null && hover.action() == HoverEvent.Action.SHOW_ITEM) {
            // 保留 [item] 物品名的配色，包括原本继承的颜色。
            return component.colorIfAbsent(inheritedColor);
        }
        TextColor originalColor = component.color() == null ? inheritedColor : component.color();
        if (!component.children().isEmpty()) {
            component = component.children(component.children().stream()
                    .map(child -> recolor(child, originalColor))
                    .toList());
        }
        return component.color(color);
    }
}
