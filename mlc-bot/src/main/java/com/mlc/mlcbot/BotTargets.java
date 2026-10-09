package com.mlc.mlcbot;

import com.mlc.mlcbot.practice.BotTrait;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public final class BotTargets {
    public static final double RANGE = 48.0;

    private BotTargets() {}

    public static boolean isEligible(Player bot, Player candidate) {
        return candidate != null && candidate != bot && candidate.getGameMode() == GameMode.SURVIVAL
                && BotTrait.isLiveHumanTarget(candidate) && bot.getWorld().equals(candidate.getWorld())
                && bot.getLocation().distanceSquared(candidate.getLocation()) <= RANGE * RANGE;
    }

    public static Player nearest(Player bot, Iterable<? extends Player> players) {
        Player nearest = null;
        double best = Double.POSITIVE_INFINITY;
        for (Player player : players) {
            if (!isEligible(bot, player)) continue;
            double distance = bot.getLocation().distanceSquared(player.getLocation());
            if (distance < best) {
                nearest = player;
                best = distance;
            }
        }
        return nearest;
    }
}
