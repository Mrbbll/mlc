package com.mlc.mlcbot.combat;

import com.mlc.mlcbot.BotSession;
import com.mlc.mlcbot.BotType;
import com.mlc.mlcbot.practice.BotTrait;
import com.mlc.mlcbot.practice.PracticeBotPlugin;
import com.mlc.mlcbot.practice.bridge.NPC;
import com.mlc.mlcbot.practice.x.PracticeBotMode;
import com.mlc.mlcbot.practice.x.CpvpCombatController;
import com.mlc.mlcbot.practice.x.MeleeCombatController;
import com.mlc.mlcbot.practice.x.MeleeMovementController;
import com.mlc.mlcbot.practice.x.ShieldController;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/** Drives the extracted PracticeBot controllers against the embedded Citizens-derived player. */
public final class CombatBrain {
    private final BotTrait trait = new BotTrait();
    private CombatRuntime runtime;
    private NPC npc;
    private CpvpCombatController cpvp;
    private MeleeCombatController melee;
    private MeleeMovementController movement;
    private ShieldController shield;
    private BotSession session;

    public void initialize(PracticeBotPlugin plugin, BotSession session) {
        this.session = session;
        runtime = new CombatRuntime(session);
        npc = new NPC(session);
        trait.attach(npc);
        trait.setOwner(session.owner);
        trait.setBoundTarget(session.owner);
        trait.setGuiEnabled(false);
        trait.setTotemCount(session.totems);
        trait.setBotType(session.type.isCpvp() ? PracticeBotMode.CPVP : session.type == BotType.DUMMY ? PracticeBotMode.DUMMY : PracticeBotMode.NORMAL);
        trait.onSpawn();
        cpvp = new CpvpCombatController(plugin);
        shield = new ShieldController(plugin);
        melee = new MeleeCombatController(plugin, shield);
        movement = new MeleeMovementController(plugin);
        if (session.type.isCpvp()) {
            trait.setCpvpSettings(PracticeProfiles.cpvp(session.type, session.strength, plugin.getConfigManager().e()));
            trait.setCpvpEnabled(true);
            cpvp.c(session.handle.getBukkitEntity(), trait);
        } else if (session.type == BotType.NORMAL) {
            trait.setPvpEnabled(true);
            int level = session.strength.ordinal();
            trait.setPvpStrafe(level >= 1);
            trait.setPvpWTap(level >= 1);
            trait.setPvpSTap(level >= 2);
            trait.setPvpCrits(level >= 1);
            trait.setPvpCritChance(Math.min(3, level));
            trait.setPvpCritSpeed(level >= 2 ? 2 : 1);
            trait.setPvpShieldBreaker(level >= 2);
            trait.setPvpRetreat(level >= 1);
            trait.setPvpAggression(level == 0 ? 0 : level >= 2 ? 2 : 1);
            trait.setPvpReachMode(1);
        }
    }

    public void tick(BotSession session, Player target, long tick) {
        if (runtime.isClosed()) return;
        runtime.run(() -> {
            Player bot = session.handle.getBukkitEntity();
            session.handle.steer(new Vector(), false);
            trait.setTotemCount(session.totems);
            if (session.type == BotType.DUMMY) {
                Vector delta = target.getEyeLocation().toVector().subtract(bot.getEyeLocation().toVector());
                bot.setRotation((float)Math.toDegrees(Math.atan2(-delta.getX(), delta.getZ())),
                        (float)-Math.toDegrees(Math.atan2(delta.getY(), Math.hypot(delta.getX(), delta.getZ()))));
                return;
            }
            long now = System.currentTimeMillis();
            updateStates(bot, target, now);
            shield.a(npc, bot, trait, target, now);
            if (session.type.isCpvp()) cpvp.a(npc, bot, target, trait);
            else {
                if (session.handle.onGround() && npc.getNavigator().isNavigating()
                        && !melee.a(bot, target, trait, now)) movement.d(bot, target, trait, now);
                melee.b(npc, bot, target, trait, now);
            }
            npc.tick(tick);
        });
    }

    /** Extracted pre-controller state updates from PracticeBot x/u.java. */
    private void updateStates(Player bot, Player target, long now) {
        boolean grounded = MeleeMovementController.a(bot);
        double velocityY = bot.getVelocity().getY(), height = bot.getLocation().getY();
        trait.isInWater = false;
        if (bot.isInWater()) movement.a(bot, target, trait, npc);
        if (grounded) {
            trait.lastGroundTime = now;
            trait.airTicks = 0;
            trait.isLaunched = false;
        } else trait.airTicks++;
        trait.lastY = height;
        if (!grounded) {
            if (velocityY > 0 && trait.critPhase == 0) { trait.critPhase = 1; trait.peakY = height; }
            else if (velocityY > 0) trait.peakY = Math.max(trait.peakY, height);
            else if (velocityY < -.1 && trait.critPhase == 1) trait.critPhase = 2;
        }
        if (trait.wasInAir && grounded) {
            trait.lastLandingTime = now;
            trait.critPhase = 0;
            trait.attemptingCrit = false;
            trait.clearCritFallback();
        }
        trait.wasInAir = !grounded;
        if (trait.isPvpSTap() && trait.sTapActive && now < trait.sTapEndTime && grounded) {
            movement.a(bot, trait, npc.getNavigator());
        } else if (now >= trait.sTapEndTime) { trait.sTapActive = false; trait.sTapDirection = null; }
        if (trait.shouldRetreat && now >= trait.retreatUntil) trait.endRetreat();
        if (trait.wasKnockedBack && (trait.airTicks > 40 || now - trait.knockbackTime > 500 && velocityY < .05 && grounded)) {
            trait.wasKnockedBack = false;
            trait.isLaunched = false;
            bot.setGravity(true);
        }
    }

    public void hit(double damage, boolean byPlayer) {
        long now = System.currentTimeMillis();
        trait.triggerKnockback(now);
        trait.setKnockback(150);
        trait.lastHitTime = now;
        trait.registerHitTaken(now, damage);
        if (byPlayer) trait.lastHitByPlayerTime = now;
        faceAfterHit();
    }

    public void knockedBack() {
        trait.triggerKnockback(System.currentTimeMillis());
        trait.setKnockback(300);
        faceAfterHit();
    }

    private void faceAfterHit() {
        if (npc != null && session != null) {
            npc.getNavigator().cancelNavigation();
            Player bot = session.handle.getBukkitEntity(), target = Bukkit.getPlayer(session.owner);
            if (session.type.isCpvp()) cpvp.b(npc, bot, target, trait.getCpvpSettings());
            else if (session.type == BotType.NORMAL) melee.a(npc, bot, target);
        }
    }

    public void pause() {
        if (runtime != null) runtime.cancelActions();
        if (session != null && session.type == BotType.NORMAL && trait.isSwitchingToAxe && trait.previousMainHand != null) {
            session.handle.getBukkitEntity().getInventory().setItemInMainHand(trait.previousMainHand.clone());
        }
        trait.cancelDelayedAttack();
        trait.resetAllStates();
        if (session != null && cpvp != null) cpvp.d(session.handle.getUUID());
        if (npc != null) {
            npc.getOrAddTrait(com.mlc.mlcbot.practice.bridge.FollowTrait.class).follow(null);
            npc.getNavigator().cancelNavigation();
        }
        if (session != null) {
            session.handle.steer(new Vector(), false);
            session.handle.getBukkitEntity().setGravity(true);
        }
    }

    public boolean isStopped() { return runtime != null && runtime.isClosed(); }
    public boolean ownsCrystal(UUID id) { return runtime != null && runtime.ownsCrystal(id); }

    public void close() {
        trait.cancelDelayedAttack();
        trait.resetAllStates();
        if (runtime != null) runtime.close();
        if (session != null && cpvp != null) cpvp.d(session.handle.getUUID());
        if (npc != null) npc.close();
    }
}
