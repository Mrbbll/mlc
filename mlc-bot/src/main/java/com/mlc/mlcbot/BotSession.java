package com.mlc.mlcbot;

import com.mlc.mlcbot.combat.CombatBrain;
import com.mlc.mlcbot.navigation.BotNavigator;
import com.mlc.mlcbot.nms.BotPlayer;
import java.util.UUID;

/** Runtime-only state, deliberately never saved to a config or NPC store. */
public final class BotSession {
    public final UUID owner;
    public final BotType type;
    public final BotStrength strength;
    public final BotPlayer handle;
    public final BotNavigator navigator = new BotNavigator();
    public final CombatBrain brain = new CombatBrain();
    public UUID target;
    public TotemSupply totems;

    public BotSession(UUID owner, BotType type, BotStrength strength, BotPlayer handle) {
        this.owner = owner;
        this.type = type;
        this.strength = strength;
        this.handle = handle;
    }
}
