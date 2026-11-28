package com.mlc.mlc.totalassault.boss;

import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/** 战斗中已生成的 Boss，由具体实现负责读取血量和清理。 */
public interface BossHandle {
    UUID entityId();
    double health();
    /** 启动本次出战的行为，并恢复已保存的进度。 */
    default void start(JavaPlugin plugin, Player player, double ultimate, double stun) {}
    default void onCritical() {}
    default double ultimateProgress() { return 0; }
    default double stunProgress() { return 0; }
    void remove();
}
