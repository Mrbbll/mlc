package com.mlc.mlc.totalassault;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

/** 统一解码总力战中展示给玩家的文字。 */
public final class TaText {
    private static final MiniMessage MINI = MiniMessage.miniMessage();

    private TaText() {}

    public static Component line(String text) {
        return MINI.deserialize("<!i>" + text);
    }

    /** 动态文字先转义，避免物品或其他数据被当成格式标签。 */
    public static String escape(String text) {
        return MINI.escapeTags(text);
    }
}
