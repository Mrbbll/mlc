// Runtime combat state extracted from PracticeBot; persistence/editor lifecycle omitted.
package com.mlc.mlcbot.practice;

import com.mlc.mlcbot.practice.x.PracticeBotMode;
import com.mlc.mlcbot.practice.x.CpvpSettings;
import com.mlc.mlcbot.practice.x.CpvpDefaults;
import java.util.Arrays;
import java.util.Locale;
import java.util.UUID;
import com.mlc.mlcbot.practice.bridge.CitizensAPI;
import com.mlc.mlcbot.practice.bridge.Trait;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class BotTrait extends Trait {
   public long canBlockAfterAttackTime = 0L;
   private UUID owner;
   private UUID boundTarget;
   private transient int cachedBoundTargetTick = Integer.MIN_VALUE;
   private transient UUID cachedBoundTargetUuid = null;
   private transient Player cachedBoundTargetPlayer = null;
   private boolean autoTargetingEnabled = false;
   private boolean autoTargetBotsOnly = false;
   private boolean editorPreview = false;
   private UUID editorSessionOwner;
   private boolean lookAtOwner = true;
   private boolean pvpEnabled = false;
   private boolean followOwner = false;
   private boolean randomWalk = false;
   private boolean holdShield = false;
   private boolean useShield = false;
   private boolean resistance = false;
   private boolean frozen = false;
   private boolean shieldInMainHand = false;
   private boolean guiEnabled = true;
   private PracticeBotMode botType = PracticeBotMode.NORMAL;
   private String frozenAnchorWorld = null;
   private double frozenAnchorX = 0.0;
   private double frozenAnchorY = 0.0;
   private double frozenAnchorZ = 0.0;
   private float frozenAnchorYaw = 0.0F;
   private float frozenAnchorPitch = 0.0F;
   private ItemStack[] storedInventory = null;
   private ItemStack[] storedArmor = null;
   private ItemStack storedOffhand = null;
   private String armorType = "netherite";
   private String helmetMaterial = "netherite";
   private String chestplateMaterial = "netherite";
   private String bootsMaterial = "netherite";
   private String helmetEnchant = "protection";
   private String chestplateEnchant = "protection";
   private String leggingsEnchant = "protection";
   private String bootsEnchant = "protection";
   private String leggingsMaterial = "netherite";
   private String swordType = "sharp_kb";
   private String offhandType = "totem";
   private int totemCount = 64;
   private ItemStack customMainHand = null;
   private long attackWarmupTicks = 0L;
   private long attackWarmupEndMillis = 0L;
   private String helmetTrimPattern = "";
   private String helmetTrimMaterial = "";
   private String chestTrimPattern = "";
   private String chestTrimMaterial = "";
   private String legsTrimPattern = "";
   private String legsTrimMaterial = "";
   private String bootsTrimPattern = "";
   private String bootsTrimMaterial = "";
   private boolean pvpStrafe = false;
   private boolean pvpWTap = false;
   private boolean pvpSTap = false;
   private boolean pvpCrits = false;
   private boolean pvpShieldBreaker = false;
   private boolean pvpRetreat = false;
   private int pvpReachMode = 1;
   private int pvpAggression = 1;
   private int pvpCritChance = 2;
   private int pvpCritSpeed = 0;
   private boolean cpvpEnabled = false;
   private CpvpSettings cpvpSettings = null;
   public long shieldDisabledUntil = 0L;
   public long lastAttackTime = 0L;
   public boolean pendingDelayedAttack = false;
   public long pendingDelayedAttackId = 0L;
   public long lastHitTime = 0L;
   public long lastHitByPlayerTime = 0L;
   public long lastBlockDecision = 0L;
   public boolean isBlocking = false;
   public boolean wasBlocking = false;
   public long blockStartTime = 0L;
   public long reactiveBlockUntil = 0L;
   public boolean reactiveBlockTriggered = false;
   public boolean strafeRight = true;
   public long lastStrafeSwitch = 0L;
   public double currentStrafeSpeed = 0.0;
   public double targetStrafeSpeed = 0.0;
   public boolean strafeMovingForward = false;
   public long lastDirectionChange = 0L;
   public boolean sTapActive = false;
   public long sTapEndTime = 0L;
   public long sTapCooldown = 0L;
   public Vector sTapDirection = null;
   public boolean isLaunched = false;
   public long launchTime = 0L;
   public int airTicks = 0;
   public double lastY = 0.0;
   public long lastGroundTime = 0L;
   public boolean wasKnockedBack = false;
   public long knockbackTime = 0L;
   public boolean attemptingCrit = false;
   public long critJumpTime = 0L;
   public long lastLandingTime = 0L;
   public boolean wasInAir = false;
   public int critPhase = 0;
   public double critStartY = 0.0;
   public double peakY = 0.0;
   public int critAirborneTicks = 0;
   public int critDescendingTicks = 0;
   public boolean critSawAscent = false;
   public boolean critSawDescent = false;
   public boolean forceNormalAttackAfterCritFail = false;
   public long knockbackUntil = 0L;
   public boolean inKnockback = false;
   public long lastSprintJumpTime = 0L;
   public boolean shouldRetreat = false;
   public long retreatUntil = 0L;
   public long lastRetreatTime = 0L;
   public int consecutiveHitsTaken = 0;
   public long lastHitTakenTime = 0L;
   public long lastShieldPressureTime = 0L;
   private final long[] recentHitTimes = new long[6];
   private final double[] recentHitDamages = new double[6];
   private int recentHitCursor = 0;
   public boolean isInWater = false;
   public boolean isSwitchingToAxe = false;
   public ItemStack previousMainHand = null;
   public long switchBackTime = 0L;
   public boolean justBrokeShield = false;
   public long shieldBreakTime = 0L;
   public double lastMoveX = 0.0;
   public double lastMoveZ = 0.0;
   public double lastPosX = 0.0;
   public double lastPosZ = 0.0;
   public int stuckTicks = 0;
   public long lastJumpTime = 0L;
   public long lastStuckCheck = 0L;
   public long lastPathUpdate = 0L;
   public boolean overheadOrbitClockwise = true;
   public long overheadAvoidLockUntil = 0L;
   public boolean followDisabledByKnockback = false;
   public long lastRandomWalkTime = 0L;

   public PracticeBotMode getBotType() {
      return this.botType;
   }

   public void setBotType(PracticeBotMode var1) {
      this.botType = var1 == null ? PracticeBotMode.NORMAL : var1;
      this.enforceCpvpLookLock();
   }

   private boolean isCpvpBotMode() {
      return this.botType == PracticeBotMode.CPVP || this.cpvpEnabled;
   }

   private void enforceCpvpLookLock() {
      if (this.isCpvpBotMode()) {
         this.lookAtOwner = false;
      }
   }

   public BotTrait() {
      super("practicebot");
   }

   public void onAttach() {
   }

   public void onSpawn() {
      this.resetAllStates();
      this.armAttackWarmup();
      if (this.frozen && this.npc != null && this.npc.getEntity() != null) {
         this.captureFrozenAnchor(this.npc.getEntity().getLocation());
      }
   }

   public void resetAllStates() {
      long var1 = System.currentTimeMillis();
      this.lastAttackTime = 0L;
      this.pendingDelayedAttack = false;
      this.pendingDelayedAttackId = 0L;
      this.canBlockAfterAttackTime = 0L;
      this.lastHitTime = 0L;
      this.lastHitByPlayerTime = 0L;
      this.shieldDisabledUntil = 0L;
      this.followDisabledByKnockback = false;
      this.knockbackUntil = 0L;
      this.inKnockback = false;
      this.lastPathUpdate = 0L;
      this.overheadOrbitClockwise = true;
      this.overheadAvoidLockUntil = 0L;
      this.sTapActive = false;
      this.sTapEndTime = 0L;
      this.sTapCooldown = 0L;
      this.sTapDirection = null;
      this.attemptingCrit = false;
      this.critJumpTime = 0L;
      this.critPhase = 0;
      this.critStartY = 0.0;
      this.peakY = 0.0;
      this.critAirborneTicks = 0;
      this.critDescendingTicks = 0;
      this.critSawAscent = false;
      this.critSawDescent = false;
      this.forceNormalAttackAfterCritFail = false;
      this.lastLandingTime = var1;
      this.wasInAir = false;
      this.strafeRight = true;
      this.lastStrafeSwitch = 0L;
      this.currentStrafeSpeed = 0.0;
      this.targetStrafeSpeed = 0.0;
      this.strafeMovingForward = false;
      this.lastDirectionChange = 0L;
      this.lastBlockDecision = 0L;
      this.isBlocking = false;
      this.wasBlocking = false;
      this.blockStartTime = 0L;
      this.reactiveBlockUntil = 0L;
      this.reactiveBlockTriggered = false;
      this.isSwitchingToAxe = false;
      this.previousMainHand = null;
      this.switchBackTime = 0L;
      this.justBrokeShield = false;
      this.shieldBreakTime = 0L;
      this.lastSprintJumpTime = 0L;
      this.shouldRetreat = false;
      this.retreatUntil = 0L;
      this.lastRetreatTime = 0L;
      this.consecutiveHitsTaken = 0;
      this.lastHitTakenTime = 0L;
      this.lastShieldPressureTime = 0L;
      Arrays.fill(this.recentHitTimes, 0L);
      Arrays.fill(this.recentHitDamages, 0.0);
      this.recentHitCursor = 0;
      this.lastMoveX = 0.0;
      this.lastMoveZ = 0.0;
      this.isLaunched = false;
      this.launchTime = 0L;
      this.airTicks = 0;
      this.lastY = 0.0;
      this.lastGroundTime = var1;
      this.wasKnockedBack = false;
      this.knockbackTime = 0L;
      this.lastPosX = 0.0;
      this.lastPosZ = 0.0;
      this.stuckTicks = 0;
      this.lastJumpTime = 0L;
      this.lastStuckCheck = 0L;
      this.isInWater = false;
      this.lastRandomWalkTime = 0L;
      this.attackWarmupEndMillis = 0L;
   }

   public long getAttackWarmupTicks() {
      return this.attackWarmupTicks;
   }

   public void setAttackWarmupTicks(long var1) {
      this.attackWarmupTicks = Math.max(0L, var1);
   }

   public void armAttackWarmup() {
      this.attackWarmupEndMillis = this.attackWarmupTicks <= 0L ? 0L : System.currentTimeMillis() + this.attackWarmupTicks * 50L;
   }

   public boolean isAttackWarmupActive(long var1) {
      return this.attackWarmupEndMillis > var1;
   }

   public long getAttackWarmupRemainingTicks(long var1) {
      return !this.isAttackWarmupActive(var1) ? 0L : Math.max(0L, (this.attackWarmupEndMillis - var1 + 49L) / 50L);
   }

   public void clearAttackWarmup() {
      this.attackWarmupEndMillis = 0L;
   }

   public void setOwner(UUID var1) {
      this.owner = var1;
   }

   public UUID getOwnerUUID() {
      return this.owner;
   }

   public Player getOwnerPlayer() {
      if (this.owner == null) {
         return null;
      } else {
         Player var1 = Bukkit.getPlayer(this.owner);
         return var1 != null && var1.isOnline() ? var1 : null;
      }
   }

   public boolean isOwner(Player var1) {
      return var1 != null && this.owner != null && var1.getUniqueId().equals(this.owner);
   }

   public void setBoundTarget(UUID var1) {
      this.boundTarget = var1;
      this.cachedBoundTargetTick = Integer.MIN_VALUE;
      this.cachedBoundTargetUuid = null;
      this.cachedBoundTargetPlayer = null;
   }

   public UUID getBoundTargetUUID() {
      return this.boundTarget;
   }

   public boolean isAutoTargetingEnabled() {
      return this.autoTargetingEnabled;
   }

   public void setAutoTargetingEnabled(boolean var1) {
      this.autoTargetingEnabled = var1;
   }

   public boolean isAutoTargetBotsOnly() {
      return this.autoTargetBotsOnly;
   }

   public void setAutoTargetBotsOnly(boolean var1) {
      this.autoTargetBotsOnly = var1;
   }

   public Player getBoundTargetPlayer() {
      if (this.boundTarget == null) {
         return null;
      } else {
         int var1 = Bukkit.getCurrentTick();
         if (var1 != this.cachedBoundTargetTick || !this.boundTarget.equals(this.cachedBoundTargetUuid)) {
            Player var2 = Bukkit.getPlayer(this.boundTarget);
            if (var2 == null && Bukkit.getEntity(this.boundTarget) instanceof Player var4) {
               var2 = var4;
            }

            this.cachedBoundTargetTick = var1;
            this.cachedBoundTargetUuid = this.boundTarget;
            this.cachedBoundTargetPlayer = var2;
            return var2;
         } else {
            return this.cachedBoundTargetPlayer != null && this.cachedBoundTargetPlayer.isValid() && !this.cachedBoundTargetPlayer.isDead()
               ? this.cachedBoundTargetPlayer
               : null;
         }
      }
   }

   public boolean isBoundTarget(Player var1) {
      return var1 != null && this.boundTarget != null && var1.getUniqueId().equals(this.boundTarget);
   }

   public Player getBehaviorTargetPlayer() {
      Player var1 = this.getBoundTargetPlayer();
      return var1 != null ? var1 : this.getOwnerPlayer();
   }

   public static boolean isNpcPlayer(Player var0) {
      return var0 != null && CitizensAPI.getNPCRegistry().isNPC(var0);
   }

   public static boolean isLiveCombatTarget(Player var0) {
      return var0 != null && var0.isValid() && !var0.isDead() && var0.getGameMode() != GameMode.SPECTATOR;
   }

   public static boolean isLiveHumanTarget(Player var0) {
      return isLiveCombatTarget(var0) && var0.isOnline() && !isNpcPlayer(var0);
   }

   public static boolean isLiveBotTarget(Player var0) {
      return isLiveCombatTarget(var0) && isNpcPlayer(var0);
   }

   public boolean isEditorPreview() {
      return this.editorPreview;
   }

   public void setEditorPreview(boolean var1) {
      this.editorPreview = var1;
   }

   public UUID getEditorSessionOwnerUUID() {
      return this.editorSessionOwner;
   }

   public void setEditorSessionOwner(UUID var1) {
      this.editorSessionOwner = var1;
   }

   public boolean isEditorSessionOwner(Player var1) {
      return var1 != null && this.editorSessionOwner != null && this.editorSessionOwner.equals(var1.getUniqueId());
   }

   public boolean isLookAtOwner() {
      return this.isCpvpBotMode() ? false : this.lookAtOwner;
   }

   public void setLookAtOwner(boolean var1) {
      this.lookAtOwner = this.isCpvpBotMode() ? false : var1;
   }

   public boolean isPvpEnabled() {
      return this.pvpEnabled;
   }

   public void setPvpEnabled(boolean var1) {
      this.pvpEnabled = var1;
      if (var1) {
         this.cpvpEnabled = false;
      }
   }

   public ItemStack[] getStoredInventory() {
      return this.storedInventory;
   }

   public void setStoredInventory(ItemStack[] var1) {
      this.storedInventory = var1;
   }

   public ItemStack[] getStoredArmor() {
      return this.storedArmor;
   }

   public void setStoredArmor(ItemStack[] var1) {
      this.storedArmor = var1;
   }

   public ItemStack getStoredOffhand() {
      return this.storedOffhand;
   }

   public void setStoredOffhand(ItemStack var1) {
      this.storedOffhand = var1;
   }

   public boolean hasStoredInventory() {
      return this.storedInventory != null;
   }

   public void clearStoredInventory() {
      this.storedInventory = null;
      this.storedArmor = null;
      this.storedOffhand = null;
   }

   public boolean isFollowOwner() {
      return this.followOwner;
   }

   public void setFollowOwner(boolean var1) {
      this.followOwner = var1;
   }

   public boolean isRandomWalk() {
      return this.randomWalk;
   }

   public void setRandomWalk(boolean var1) {
      this.randomWalk = var1;
   }

   public boolean isHoldShield() {
      return this.holdShield;
   }

   public void setHoldShield(boolean var1) {
      this.holdShield = var1;
   }

   public boolean isUseShield() {
      return this.useShield;
   }

   public void setUseShield(boolean var1) {
      this.useShield = var1;
   }

   public boolean isResistance() {
      return this.resistance;
   }

   public void setResistance(boolean var1) {
      this.resistance = var1;
   }

   public boolean isFrozen() {
      return this.frozen;
   }

   public void setFrozen(boolean var1) {
      this.frozen = var1;
      if (!var1) {
         this.clearFrozenAnchor();
      }
   }

   public boolean isShieldInMainHand() {
      return this.shieldInMainHand;
   }

   public void setShieldInMainHand(boolean var1) {
      this.shieldInMainHand = var1;
   }

   public boolean isGuiEnabled() {
      return this.guiEnabled;
   }

   public void setGuiEnabled(boolean var1) {
      this.guiEnabled = var1;
   }

   public String getArmorType() {
      return this.armorType;
   }

   public void setArmorType(String var1) {
      String var2 = this.normalizeArmorMaterial(var1, "netherite");
      this.armorType = var2;
      this.helmetMaterial = var2;
      this.chestplateMaterial = var2;
      this.bootsMaterial = var2;
   }

   public String getHelmetMaterial() {
      return this.helmetMaterial;
   }

   public void setHelmetMaterial(String var1) {
      this.helmetMaterial = this.normalizeArmorMaterial(var1, this.helmetMaterial);
      this.refreshArmorTypeSnapshot();
   }

   public String getChestplateMaterial() {
      return this.chestplateMaterial;
   }

   public void setChestplateMaterial(String var1) {
      this.chestplateMaterial = this.normalizeArmorMaterial(var1, this.chestplateMaterial);
      this.refreshArmorTypeSnapshot();
   }

   public String getHelmetEnchant() {
      return this.helmetEnchant;
   }

   public void setHelmetEnchant(String var1) {
      this.helmetEnchant = var1;
   }

   public String getChestplateEnchant() {
      return this.chestplateEnchant;
   }

   public void setChestplateEnchant(String var1) {
      this.chestplateEnchant = var1;
   }

   public String getLeggingsEnchant() {
      return this.leggingsEnchant;
   }

   public void setLeggingsEnchant(String var1) {
      this.leggingsEnchant = var1;
   }

   public String getBootsEnchant() {
      return this.bootsEnchant;
   }

   public void setBootsEnchant(String var1) {
      this.bootsEnchant = var1;
   }

   public String getLeggingsMaterial() {
      return this.leggingsMaterial;
   }

   public void setLeggingsMaterial(String var1) {
      this.leggingsMaterial = this.normalizeArmorMaterial(var1, this.leggingsMaterial);
   }

   public String getBootsMaterial() {
      return this.bootsMaterial;
   }

   public void setBootsMaterial(String var1) {
      this.bootsMaterial = this.normalizeArmorMaterial(var1, this.bootsMaterial);
      this.refreshArmorTypeSnapshot();
   }

   public String getSwordType() {
      return this.swordType;
   }

   public void setSwordType(String var1) {
      this.swordType = var1;
   }

   public String getOffhandType() {
      return this.offhandType;
   }

   public void setOffhandType(String var1) {
      this.offhandType = var1;
   }

   public int getTotemCount() {
      return this.totemCount;
   }

   public void setTotemCount(int var1) {
      this.totemCount = Math.max(0, var1);
   }

   public void decrementTotemCount() {
      if (this.totemCount > 0) {
         this.totemCount--;
      }
   }

   public ItemStack getCustomMainHand() {
      return this.customMainHand;
   }

   public void setCustomMainHand(ItemStack var1) {
      this.customMainHand = var1 != null ? var1.clone() : null;
   }

   public String getHelmetTrimPattern() {
      return this.helmetTrimPattern;
   }

   public void setHelmetTrimPattern(String var1) {
      this.helmetTrimPattern = this.normalizeTrimValue(var1);
   }

   public String getHelmetTrimMaterial() {
      return this.helmetTrimMaterial;
   }

   public void setHelmetTrimMaterial(String var1) {
      this.helmetTrimMaterial = this.normalizeTrimValue(var1);
   }

   public String getChestTrimPattern() {
      return this.chestTrimPattern;
   }

   public void setChestTrimPattern(String var1) {
      this.chestTrimPattern = this.normalizeTrimValue(var1);
   }

   public String getChestTrimMaterial() {
      return this.chestTrimMaterial;
   }

   public void setChestTrimMaterial(String var1) {
      this.chestTrimMaterial = this.normalizeTrimValue(var1);
   }

   public String getLegsTrimPattern() {
      return this.legsTrimPattern;
   }

   public void setLegsTrimPattern(String var1) {
      this.legsTrimPattern = this.normalizeTrimValue(var1);
   }

   public String getLegsTrimMaterial() {
      return this.legsTrimMaterial;
   }

   public void setLegsTrimMaterial(String var1) {
      this.legsTrimMaterial = this.normalizeTrimValue(var1);
   }

   public String getBootsTrimPattern() {
      return this.bootsTrimPattern;
   }

   public void setBootsTrimPattern(String var1) {
      this.bootsTrimPattern = this.normalizeTrimValue(var1);
   }

   public String getBootsTrimMaterial() {
      return this.bootsTrimMaterial;
   }

   public void setBootsTrimMaterial(String var1) {
      this.bootsTrimMaterial = this.normalizeTrimValue(var1);
   }

   public boolean isPvpStrafe() {
      return this.pvpStrafe;
   }

   public void setPvpStrafe(boolean var1) {
      this.pvpStrafe = var1;
   }

   public boolean isPvpWTap() {
      return this.pvpWTap;
   }

   public void setPvpWTap(boolean var1) {
      this.pvpWTap = var1;
   }

   public boolean isPvpSTap() {
      return this.pvpSTap;
   }

   public void setPvpSTap(boolean var1) {
      this.pvpSTap = var1;
   }

   public boolean isPvpCrits() {
      return this.pvpCrits;
   }

   public void setPvpCrits(boolean var1) {
      this.pvpCrits = var1;
   }

   public boolean isPvpShieldBreaker() {
      return this.pvpShieldBreaker;
   }

   public void setPvpShieldBreaker(boolean var1) {
      this.pvpShieldBreaker = var1;
   }

   public boolean isPvpRetreat() {
      return this.pvpRetreat;
   }

   public void setPvpRetreat(boolean var1) {
      this.pvpRetreat = var1;
   }

   public int getPvpReachMode() {
      return this.pvpReachMode;
   }

   public void setPvpReachMode(int var1) {
      this.pvpReachMode = var1;
   }

   public int getPvpAggression() {
      return this.pvpAggression;
   }

   public void setPvpAggression(int var1) {
      this.pvpAggression = var1;
   }

   public int getPvpCritChance() {
      return this.pvpCritChance;
   }

   public void setPvpCritChance(int var1) {
      this.pvpCritChance = var1;
   }

   public int getPvpCritSpeed() {
      return this.pvpCritSpeed;
   }

   public void setPvpCritSpeed(int var1) {
      this.pvpCritSpeed = var1;
   }

   public boolean isCpvpEnabled() {
      return this.cpvpEnabled;
   }

   public void setCpvpEnabled(boolean var1) {
      this.cpvpEnabled = var1;
      this.enforceCpvpLookLock();
      if (var1) {
         this.pvpEnabled = false;
         this.lookAtOwner = false;
         this.followOwner = false;
         this.randomWalk = false;
         if (this.cpvpSettings == null) {
            this.initializeCpvpSettingsInternal();
         }
      }
   }

   public CpvpSettings getCpvpSettings() {
      if (this.cpvpSettings == null) {
         this.initializeCpvpSettingsInternal();
      }

      return this.cpvpSettings;
   }

   private void initializeCpvpSettingsInternal() {
      this.cpvpSettings = new CpvpSettings();
      PracticeBotPlugin var1 = PracticeBotPlugin.getInstance();
      if (var1 != null && var1.getConfigManager() != null) {
         CpvpDefaults var2 = var1.getConfigManager().e();
         if (var2 != null) {
            this.cpvpSettings.a(var2);
         }
      }
   }

   public void initializeCpvpSettings(PracticeBotPlugin var1) {
      this.initializeCpvpSettingsInternal();
   }

   public void setCpvpSettings(CpvpSettings var1) {
      this.cpvpSettings = var1;
   }

   public boolean isShieldDisabled(long var1) {
      return var1 < this.shieldDisabledUntil;
   }

   public boolean isInKnockback(long var1) {
      if (var1 >= this.knockbackUntil) {
         this.inKnockback = false;
      }

      return this.inKnockback;
   }

   public void setKnockback(long var1) {
      this.knockbackUntil = System.currentTimeMillis() + var1;
      this.inKnockback = true;
   }

   public void triggerKnockback(long var1) {
      this.wasKnockedBack = true;
      this.knockbackTime = var1;
      this.inKnockback = true;
      this.knockbackUntil = var1 + 500L;
   }

   public boolean isRecentlyKnockedBack(long var1) {
      return this.wasKnockedBack && var1 - this.knockbackTime < 600L;
   }

   public long beginDelayedAttack() {
      this.pendingDelayedAttackId++;
      this.pendingDelayedAttack = true;
      return this.pendingDelayedAttackId;
   }

   public boolean isPendingDelayedAttack(long var1) {
      return this.pendingDelayedAttack && this.pendingDelayedAttackId == var1;
   }

   public void finishDelayedAttack(long var1) {
      if (this.pendingDelayedAttackId == var1) {
         this.pendingDelayedAttack = false;
      }
   }

   public void cancelDelayedAttack() {
      this.pendingDelayedAttack = false;
      this.pendingDelayedAttackId++;
   }

   public void beginCritAttempt(long var1, double var3) {
      this.attemptingCrit = true;
      this.critJumpTime = var1;
      this.critPhase = 0;
      this.critStartY = var3;
      this.peakY = var3;
      this.critAirborneTicks = 0;
      this.critDescendingTicks = 0;
      this.critSawAscent = false;
      this.critSawDescent = false;
      this.forceNormalAttackAfterCritFail = false;
   }

   public void finishCritAttempt(boolean var1) {
      this.attemptingCrit = false;
      this.critJumpTime = 0L;
      this.critPhase = 0;
      this.critStartY = 0.0;
      this.peakY = 0.0;
      this.critAirborneTicks = 0;
      this.critDescendingTicks = 0;
      this.critSawAscent = false;
      this.critSawDescent = false;
      this.forceNormalAttackAfterCritFail = var1;
   }

   public void clearCritFallback() {
      this.forceNormalAttackAfterCritFail = false;
   }

   public void captureFrozenAnchor(Location var1) {
      if (var1 != null && var1.getWorld() != null) {
         this.frozenAnchorWorld = var1.getWorld().getName();
         this.frozenAnchorX = var1.getX();
         this.frozenAnchorY = var1.getY();
         this.frozenAnchorZ = var1.getZ();
         this.frozenAnchorYaw = var1.getYaw();
         this.frozenAnchorPitch = var1.getPitch();
      }
   }

   public void ensureFrozenAnchor(Location var1) {
      if (this.frozen) {
         if (this.getFrozenAnchorLocation() == null) {
            this.captureFrozenAnchor(var1);
         }
      }
   }

   public Location getFrozenAnchorLocation() {
      if (this.frozenAnchorWorld != null && !this.frozenAnchorWorld.isBlank()) {
         World var1 = Bukkit.getWorld(this.frozenAnchorWorld);
         return var1 == null
            ? null
            : new Location(var1, this.frozenAnchorX, this.frozenAnchorY, this.frozenAnchorZ, this.frozenAnchorYaw, this.frozenAnchorPitch);
      } else {
         return null;
      }
   }

   public void clearFrozenAnchor() {
      this.frozenAnchorWorld = null;
      this.frozenAnchorX = 0.0;
      this.frozenAnchorY = 0.0;
      this.frozenAnchorZ = 0.0;
      this.frozenAnchorYaw = 0.0F;
      this.frozenAnchorPitch = 0.0F;
   }

   public void clearKnockbackState() {
      this.wasKnockedBack = false;
      this.isLaunched = false;
   }

   public boolean shouldRetreatNow(long var1) {
      return this.shouldRetreat && var1 < this.retreatUntil;
   }

   public void triggerRetreat(long var1) {
      long var3 = System.currentTimeMillis();
      if (var3 - this.lastRetreatTime >= 1500L) {
         this.shouldRetreat = true;
         this.retreatUntil = var3 + var1;
         this.lastRetreatTime = var3;
      }
   }

   public void endRetreat() {
      this.shouldRetreat = false;
   }

   public void registerHitTaken(long var1) {
      this.registerHitTaken(var1, 0.0);
   }

   public void registerHitTaken(long var1, double var3) {
      if (var1 - this.lastHitTakenTime < 800L) {
         this.consecutiveHitsTaken++;
      } else {
         this.consecutiveHitsTaken = 1;
      }

      this.lastHitTakenTime = var1;
      this.recentHitTimes[this.recentHitCursor] = var1;
      this.recentHitDamages[this.recentHitCursor] = Math.max(0.0, var3);
      this.recentHitCursor = (this.recentHitCursor + 1) % this.recentHitTimes.length;
   }

   public void registerShieldPressure(long var1) {
      this.lastShieldPressureTime = var1;
   }

   public int countRecentHits(long var1, long var3) {
      int var5 = 0;

      for (long var9 : this.recentHitTimes) {
         if (var9 > 0L && var1 - var9 <= var3) {
            var5++;
         }
      }

      return var5;
   }

   public double getRecentDamageTaken(long var1, long var3) {
      double var5 = 0.0;

      for (int var7 = 0; var7 < this.recentHitTimes.length; var7++) {
         long var8 = this.recentHitTimes[var7];
         if (var8 > 0L && var1 - var8 <= var3) {
            var5 += this.recentHitDamages[var7];
         }
      }

      return var5;
   }

   public double getCritChancePercent() {
      return switch (this.pvpCritChance) {
         case 0 -> 0.25;
         case 1 -> 0.5;
         case 2 -> 0.75;
         default -> 1.0;
      };
   }

   public long getCritJumpDelay() {
      return switch (this.pvpCritSpeed) {
         case 0 -> 350L;
         case 1 -> 120L;
         default -> 0L;
      };
   }

   public String formatEnchantName(String var1) {
      if (var1 == null) {
         return "Protection IV";
      } else {
         return switch (var1) {
            case "blast_protection" -> "Blast Protection IV";
            case "fire_protection" -> "Fire Protection IV";
            case "projectile_protection" -> "Projectile Protection IV";
            case "none" -> "No Enchant";
            default -> "Protection IV";
         };
      }
   }

   private String normalizeTrimValue(String var1) {
      return var1 == null ? "" : var1.trim().toLowerCase();
   }

   private String normalizeArmorMaterial(String var1, String var2) {
      if (var1 != null && !var1.isBlank()) {
         return var1.trim().toLowerCase(Locale.ROOT);
      } else {
         return var2 != null && !var2.isBlank() ? var2.trim().toLowerCase(Locale.ROOT) : "netherite";
      }
   }

   private void refreshArmorTypeSnapshot() {
      this.armorType = this.normalizeArmorMaterial(this.chestplateMaterial, this.armorType);
   }

}
