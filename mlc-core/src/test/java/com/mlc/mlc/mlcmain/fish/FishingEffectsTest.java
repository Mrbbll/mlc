package com.mlc.mlc.mlcmain.fish;

import com.mlc.mlc.mlcmain.fish.listener.FishingListener;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FishingEffectsTest {
    @Test
    void luredAndFailedAttemptKeepTrackingUntilReelIn() {
        FishingEffects effects = mock(FishingEffects.class);
        FishingListener listener = new FishingListener(effects);
        Player player = mock(Player.class);
        FishHook hook = mock(FishHook.class);
        PlayerFishEvent event = mock(PlayerFishEvent.class);
        when(event.getPlayer()).thenReturn(player);
        when(event.getHook()).thenReturn(hook);

        when(event.getState()).thenReturn(PlayerFishEvent.State.FISHING, PlayerFishEvent.State.LURED,
                PlayerFishEvent.State.BITE, PlayerFishEvent.State.FAILED_ATTEMPT, PlayerFishEvent.State.REEL_IN);
        listener.onFish(event);
        listener.onFish(event);
        listener.onFish(event);
        listener.onFish(event);
        verify(effects).cast(player, hook);
        verify(effects).bite(player, hook);
        verify(effects, never()).remove(player);
        listener.onFish(event);
        verify(effects).remove(player);
    }

    @Test
    void fakeAnimationSinksAndReturnsToTheRealPosition() {
        assertTrue(FishingEffects.fakeOffset(1) < 0);
        assertEquals(-0.35, FishingEffects.fakeOffset(6), 0.00001);
        assertEquals(0, FishingEffects.fakeOffset(12), 0.00001);
    }

    @Test
    void distinguishesApproachParticlesFromBiteParticles() {
        assertTrue(FishingEffects.isApproachParticle(FishingEffects.ApproachType.FISHING, 0, 1));
        assertTrue(FishingEffects.isApproachParticle(FishingEffects.ApproachType.BUBBLE, 1, 0));
        assertTrue(FishingEffects.isApproachParticle(FishingEffects.ApproachType.SPLASH, 2, 0));
        assertTrue(FishingEffects.isApproachParticle(FishingEffects.ApproachType.SPLASH, 3, 0));
        assertFalse(FishingEffects.isApproachParticle(FishingEffects.ApproachType.FISHING, 6, 0.2f));
        assertFalse(FishingEffects.isApproachParticle(FishingEffects.ApproachType.BUBBLE, 6, 0.2f));
        assertFalse(FishingEffects.isApproachParticle(FishingEffects.ApproachType.SPLASH, 1, 0));
    }
}
