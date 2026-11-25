package com.mlc.mlc.mlcmain.fish.listener;

import com.mlc.mlc.mlcmain.fish.FishingEffects;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/** 监听原版钓鱼生命周期，效果服务不拦截收竿与掉落。 */
public final class FishingListener implements Listener {
    private final FishingEffects effects;

    public FishingListener(FishingEffects effects) {
        this.effects = effects;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFish(PlayerFishEvent event) {
        Player player = event.getPlayer();
        switch (event.getState()) {
            case FISHING -> effects.cast(player, event.getHook());
            case BITE -> effects.bite(player, event.getHook());
            // Paper 的 LURED 表示鱼正在靠近，FAILED_ATTEMPT 表示错过咬钩；鱼漂仍有效。
            case LURED, FAILED_ATTEMPT -> { }
            case CAUGHT_FISH, CAUGHT_ENTITY, REEL_IN, IN_GROUND -> effects.remove(player);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        effects.remove(event.getPlayer());
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        effects.remove(event.getEntity());
    }
}
