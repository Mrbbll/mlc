// Extracted from the user-provided PracticeBot-Source; strategy logic retained.
package com.mlc.mlcbot.practice.x;

import static com.mlc.mlcbot.nms.EntityGroundState.isOnGround;

import com.mlc.mlcbot.practice.BotTrait;
import com.mlc.mlcbot.practice.PracticeBotPlugin;
import java.util.concurrent.ThreadLocalRandom;
import com.mlc.mlcbot.practice.bridge.Navigator;
import com.mlc.mlcbot.practice.bridge.NPC;
import com.mlc.mlcbot.practice.bridge.FollowTrait;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.Sound;
import org.bukkit.Statistic;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

/** 普通近战攻击、暴击、破盾和攻击节奏控制。 原源码：x/v.java。 */
public class MeleeCombatController {
   private static final double l1 = 3.0;
   private static final double m0 = 0.11;
   private static final int m1 = 2;
   private static final double n0 = -0.03;
   private static final double n1 = 1.9;
   private static final double o0 = 2.25;
   private static final double o1 = 1.25;
   private static final double p0 = 0.9;
   private static final long p1 = 650L;
   private static final long q0 = 2500L;
   private final PracticeBotPlugin q1;
   private final PracticeCombatConfig r0;
   private final ShieldController r1;
   private final MeleeMovementController s0;

   public MeleeCombatController(PracticeBotPlugin var1, ShieldController var2) {
      this.q1 = var1;
      this.r0 = var1.getConfigManager();
      this.r1 = var2;
      this.s0 = new MeleeMovementController(var1);
   }

   public void b(NPC var1, Player var2, Player var3, BotTrait var4, long var5) {
      if (var3 != null && var3.isOnline() && !var3.isDead() && var3.isValid()) {
         if (var3.getGameMode() == GameMode.SPECTATOR) {
            var1.getNavigator().cancelNavigation();
         } else if (!var2.getWorld().equals(var3.getWorld())) {
            var1.getNavigator().cancelNavigation();
         } else {
            Location var29 = var2.getLocation();
            Location var30 = var3.getLocation();
            double var10 = var29.distance(var30);
            double var12 = this.b(var4);
            Navigator var14 = var1.getNavigator();
            boolean var15 = MeleeMovementController.a(var2);
            boolean var16 = var4.wasKnockedBack;
            double var17 = var12 - 0.3;
            double var19 = var12 - 0.8;
            double var21 = Math.max(var17, 3.0);
            double var23 = var4.isPvpRetreat() ? this.a(var2, var3, var4, var5, var10, var17) : 0.0;

            float var25 = switch (var4.getPvpAggression()) {
               case 0 -> this.r0.am();
               case 2 -> this.r0.ao();
               default -> this.r0.an();
            };
            if (var4.isBlocking) {
               var25 *= 0.4F;
            }

            var2.setSprinting(false);
            boolean var26 = var4.isPvpRetreat() && var4.shouldRetreatNow(var5);
            if (var4.sTapActive && var5 < var4.sTapEndTime) {
               var14.cancelNavigation();
               this.v(var1);
               if (!var4.isBlocking && var2.hasLineOfSight(var3) && var10 <= var12) {
                  this.a(var2, var3, var4, var5, var10, var12, var15);
               }
            } else if (var16) {
               this.a(var1, var2, var3);
               boolean var31 = MeleeMovementController.a(var2);
               if (!var31) {
                  var2.setGravity(false);
                  Vector var32 = var2.getVelocity();
                  var32.setY((var32.getY() - 0.08) * 0.98);
                  var32.setX(var32.getX() * 0.91);
                  var32.setZ(var32.getZ() * 0.91);
                  var2.setVelocity(var32);
               } else {
                  var2.setGravity(true);
               }

               if (var26) {
                  this.a(var1, var2, var3);
                  this.s0.a(var2, var3, var4, var14, var5, var10, this.a(var17));
               }

               if (!var4.isBlocking && var2.hasLineOfSight(var3) && var10 <= var12) {
                  this.a(var2, var3, var4, var5, var10, var12, false);
               }
            } else {
               if (var26) {
                  if (!this.a(var2, var3, var4, var5, var10, var17, var23)) {
                     this.a(var1, var2, var3);
                     this.s0.a(var2, var3, var4, var14, var5, var10, this.a(var17));
                     if (!var4.isBlocking && var2.hasLineOfSight(var3) && var10 <= var12) {
                        this.a(var2, var3, var4, var5, var10, var12, var15);
                     }

                     return;
                  }

                  var4.endRetreat();
               }

               if (var4.isPvpRetreat() && var15 && this.a(var4, var5, var23)) {
                  var4.triggerRetreat(this.a(var4, var23));
                  if (var4.shouldRetreatNow(var5)) {
                     this.a(var1, var2, var3);
                     this.s0.a(var2, var3, var4, var14, var5, var10, this.a(var17));
                     if (!var4.isBlocking && var2.hasLineOfSight(var3) && var10 <= var12) {
                        this.a(var2, var3, var4, var5, var10, var12, var15);
                     }

                     return;
                  }
               }

               if (this.b(var2, var3, var4, var5)) {
                  this.a(var1, var2, var3);
                  this.s0.a(var2, var3, var4, 1.25, var10 > var17 ? 0.19 : 0.15);
                  if (!var4.isBlocking && var2.hasLineOfSight(var3) && var10 <= var12) {
                     this.a(var2, var3, var4, var5, var10, var12, var15);
                  }
               } else {
                  if (var10 > var21) {
                     this.a(var1, var3, var25);
                     if (var10 <= 2.5) {
                        this.a(var2, var3);
                     } else {
                        float var27 = var2.getLocation().getPitch();
                        if (Math.abs(var27) > 2.0F) {
                           float var28 = var27 * 0.7F;
                           var2.setRotation(var2.getLocation().getYaw(), var28);
                        }
                     }
                  } else {
                     this.a(var1, var2, var3);
                     var14.cancelNavigation();
                     this.a(var2, var3);
                     if (var15 && !var4.isBlocking && !var4.isInKnockback(var5)) {
                        if (var10 > var17) {
                           if (var4.isPvpStrafe()) {
                              this.s0.f(var2, var3, var4, var5);
                           } else {
                              this.s0.a(var2, var3, var10, var19);
                           }
                        } else if (var4.isPvpStrafe()) {
                           this.s0.b(var2, var3, var4, var5, var10, var19);
                        } else {
                           this.s0.a(var2, var3, var10, var19);
                        }
                     }
                  }

                  if (!var4.isBlocking && var2.hasLineOfSight(var3)) {
                     if (var4.isPvpCrits() && var15 && var10 <= var12 && !var4.attemptingCrit) {
                        var14.cancelNavigation();
                     }

                     this.a(var2, var3, var4, var5, var10, var12, var15);
                  }
               }
            }
         }
      } else {
         var1.getNavigator().cancelNavigation();
      }
   }

   private void a(Player var1, Player var2, BotTrait var3, long var4, double var6, double var8, boolean var10) {
      if (!(var6 > var8)) {
         if (var2.getGameMode() != GameMode.SPECTATOR) {
            boolean var11 = this.h(var2);
            if (var3.isPvpShieldBreaker()) {
               this.a(var1, var2, var3, var4, var11);
            }

            if (!var3.pendingDelayedAttack) {
               boolean var12 = this.a(var1, var3, var4);
               if (!var3.isPvpCrits()) {
                  var3.clearCritFallback();
                  var3.attemptingCrit = false;
                  if (var12) {
                     this.b(var1, var2, var3, var4, false);
                  }
               } else {
                  double var13 = var1.getVelocity().getY();
                  boolean var15 = var13 < -0.08;
                  if (var10) {
                     long var16 = var4 - var3.lastLandingTime;
                     boolean var18 = var3.getPvpCritSpeed() == 2;
                     long var19 = var18 ? 0L : var3.getCritJumpDelay();
                     double var21 = var3.getCritChancePercent();
                     if (var21 > 1.0) {
                        var21 /= 100.0;
                     }

                     var21 = Math.max(0.0, Math.min(1.0, var21));
                     boolean var23 = Math.random() < var21;
                     if (var23 && var16 >= var19) {
                        Vector var24 = var1.getVelocity();
                        var24.setY(0.42);
                        var1.setVelocity(var24);
                        var3.attemptingCrit = true;
                        var3.critJumpTime = var4;
                        var3.critPhase = 0;
                     } else {
                        if (!var23) {
                           if (!var12) {
                              return;
                           }

                           this.b(var1, var2, var3, var4, false);
                        }
                     }
                  } else if (!(var13 > 0.0)) {
                     if (var15) {
                        if (!var12) {
                           return;
                        }

                        this.b(var1, var2, var3, var4, true);
                        var3.attemptingCrit = false;
                        var3.critPhase = 0;
                     }
                  }
               }
            }
         }
      }
   }

   private void a(Player var1, Player var2, BotTrait var3, long var4, boolean var6) {
      if (var3.justBrokeShield && var2.getCooldown(Material.SHIELD) == 0) {
         var3.justBrokeShield = false;
      }

      if (var3.justBrokeShield && var2.getCooldown(Material.SHIELD) > 0 && var3.isSwitchingToAxe) {
         var1.getInventory()
            .setItemInMainHand(
               var3.previousMainHand != null
                  ? var3.previousMainHand
                  : (var3.getCustomMainHand() != null ? var3.getCustomMainHand().clone() : this.q1.getBotManager().H().E())
            );
         var3.isSwitchingToAxe = false;
      }

      if (var6 && !var3.isSwitchingToAxe && !var3.justBrokeShield) {
         ItemStack var7 = var1.getInventory().getItemInMainHand();
         if (var7 == null || var7.getType() == Material.AIR || !var7.getType().name().endsWith("_AXE")) {
            var3.previousMainHand = var7 != null && var7.getType() != Material.AIR
               ? var7.clone()
               : (var3.getCustomMainHand() != null ? var3.getCustomMainHand().clone() : this.q1.getBotManager().H().E());
            var3.isSwitchingToAxe = true;
            var3.switchBackTime = var4 + 5000L;
            var1.getInventory().setItemInMainHand(new ItemStack(Material.NETHERITE_AXE));
         }
      }

      if (var3.isSwitchingToAxe && var4 >= var3.switchBackTime) {
         var1.getInventory()
            .setItemInMainHand(
               var3.previousMainHand != null
                  ? var3.previousMainHand
                  : (var3.getCustomMainHand() != null ? var3.getCustomMainHand().clone() : this.q1.getBotManager().H().E())
            );
         var3.isSwitchingToAxe = false;
         var3.previousMainHand = null;
         var3.justBrokeShield = false;
      }
   }

   private void b(Player var1, Player var2, BotTrait var3, long var4, boolean var6) {
      if (var2.getGameMode() != GameMode.SPECTATOR) {
         if (var3.isPvpWTap() && !var6) {
            long var7 = var3.beginDelayedAttack();
            Runnable var9 = () -> {
               if (var1.isValid() && var2.isOnline() && var3.isPendingDelayedAttack(var7)) {
                  if (var2.getGameMode() == GameMode.SPECTATOR) {
                     var3.finishDelayedAttack(var7);
                  } else {
                     long var7x = System.currentTimeMillis();
                     if (!this.a(var1, var3, var7x)) {
                        var3.finishDelayedAttack(var7);
                     } else {
                        this.a(var1, var2, var3, var6, var7x, true);
                        var3.finishDelayedAttack(var7);
                        if (var3.isPvpSTap() && !var3.sTapActive && isOnGround(var1) && var2.isOnline() && var2.getGameMode() != GameMode.SPECTATOR) {
                           this.s0.e(var1, var2, var3, var7x);
                        }
                     }
                  }
               } else {
                  var3.finishDelayedAttack(var7);
               }
            };
            long var10 = this.a(var3, var6);
            if (var10 <= 0L) {
               var9.run();
            } else {
               com.mlc.mlcbot.combat.CombatRuntime.runTaskLater(this.q1.plugin(), var9, var10);
            }
         } else {
            this.a(var1, var2, var3, var6, var4, false);
            if (var3.isPvpSTap() && !var3.sTapActive && isOnGround(var1)) {
               this.s0.e(var1, var2, var3, var4);
            }
         }
      }
   }

   private void a(Player var1, Player var2, BotTrait var3, boolean var4, long var5, boolean var7) {
      if (var2.getGameMode() != GameMode.SPECTATOR) {
         this.a(var1, var2);
         var3.lastAttackTime = var5;
         if (var4) {
            var3.finishCritAttempt(false);
         }

         var3.clearCritFallback();
         var3.isBlocking = false;
         var3.wasBlocking = false;
         var3.canBlockAfterAttackTime = var5 + 350L;
         this.r1.m(var1);
         var1.swingMainHand();
         double var8 = this.b(var3);
         if (!(var1.getLocation().distanceSquared(var2.getLocation()) > var8 * var8)) {
            if (var1.hasLineOfSight(var2)) {
               ItemStack var10 = var1.getInventory().getItemInMainHand();
               boolean var11 = var10 != null && var10.getType().name().endsWith("_AXE");
               boolean var12 = this.h(var2);
               if (var12) {
                  if (var11) {
                     if (var2.getCooldown(Material.SHIELD) == 0) {
                        var2.setCooldown(Material.SHIELD, 100);

                        try {
                           var2.clearActiveItem();
                        } catch (Exception var23) {
                        }

                        var2.getWorld().playSound(var2.getLocation(), Sound.ITEM_SHIELD_BREAK, 1.0F, 1.0F);
                        var3.justBrokeShield = true;
                        var3.shieldBreakTime = var5;
                     }
                  } else {
                     var2.getWorld().playSound(var2.getLocation(), Sound.ITEM_SHIELD_BLOCK, 1.0F, 1.0F);
                  }
               } else {
                  double var13 = this.a(var1, var10);
                  int var15 = 0;
                  int var16 = 0;
                  int var17 = 0;
                  if (var10 != null && var10.hasItemMeta()) {
                     ItemMeta var18 = var10.getItemMeta();
                     if (var18 != null) {
                        for (Enchantment var20 : var18.getEnchants().keySet()) {
                           NamespacedKey var21 = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).getKey(var20);
                           if (var21 != null) {
                              String var22 = var21.getKey().toLowerCase();
                              if (var22.equals("knockback")) {
                                 var15 = var18.getEnchantLevel(var20);
                              } else if (var22.equals("fire_aspect")) {
                                 var16 = var18.getEnchantLevel(var20);
                              } else if (var22.equals("sharpness")) {
                                 var17 = var18.getEnchantLevel(var20);
                              }
                           }
                        }
                     }
                  }

                  if (var17 > 0) {
                     var13 += 0.5 + var17 * 0.5;
                  }

                  FileConfiguration var24 = this.r0.g();
                  switch (var3.getPvpAggression()) {
                     case 0:
                        var13 *= var24.getDouble("pvp.aggression-damage-modifiers.low", 1.0);
                        break;
                     case 1:
                        var13 *= var24.getDouble("pvp.aggression-damage-modifiers.medium", 1.05);
                        break;
                     case 2:
                        var13 *= var24.getDouble("pvp.aggression-damage-modifiers.high", 1.1);
                  }

                  if (var4) {
                     var13 *= this.r0.ah();
                  }

                  if (var16 > 0) {
                     var2.setFireTicks(var16 * 80);
                  }

                  int var25 = var2.getStatistic(Statistic.DEATHS);
                  boolean var26 = this.a(var1, var10, var4, var7);
                  var2.damage(var13, var1);
                  com.mlc.mlcbot.combat.CombatRuntime.runTaskLater(this.q1.plugin(), () -> {
                     if (var2.isValid()) {
                        int var2x = var2.getStatistic(Statistic.DEATHS);
                        if (var2x > var25) {
                           var2.setStatistic(Statistic.DEATHS, var25);
                        }
                     }
                  }, 1L);
                  if (var2.getGameMode() != GameMode.CREATIVE && (var15 > 0 || var7)) {
                     final int finalVar15 = var15;
                     final boolean finalVar7 = var7;
                     com.mlc.mlcbot.combat.CombatRuntime.runTaskLater(this.q1.plugin(), () -> {
                        if (var2.isValid() && !var2.isDead() && var2.getGameMode() != GameMode.CREATIVE) {
                           Vector var5x = var2.getLocation().toVector().subtract(var1.getLocation().toVector());
                           var5x.setY(0);
                           if (var5x.lengthSquared() < 0.001) {
                              var5x = var1.getLocation().getDirection();
                              var5x.setY(0);
                           }

                           var5x.normalize();
                           double var6 = (finalVar7 ? 1.0 : 0.0) + Math.max(0, finalVar15);
                           if (!(var6 <= 0.0)) {
                              double var8x = 1.0 - Math.min(1.0, this.l(var2));
                              if (!(var8x <= 0.0)) {
                                 Vector var10x = var2.getVelocity();
                                 double var11x = 0.4 * var6 * var8x;
                                 double var13x = Math.min(0.4, Math.max(0.0, var10x.getY()) * 0.5 + 0.1 * var8x);
                                 var2.setVelocity(new Vector(var10x.getX() * 0.5 + var5x.getX() * var11x, var13x, var10x.getZ() * 0.5 + var5x.getZ() * var11x));
                              }
                           }
                        }
                     }, 1L);
                  }

                  this.q1.addCombatTag(var2);
                  if (var4) {
                     var2.getWorld().playSound(var2.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 1.0F, 1.0F);
                  } else if (var26) {
                     var2.getWorld().playSound(var2.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 0.9F, 1.0F);
                     Vector var27 = var1.getLocation().getDirection().clone();
                     if (var27.lengthSquared() < 1.0E-6) {
                        var27 = new Vector(0, 0, 1);
                     }

                     var27.setY(0.0).normalize();
                     var2.getWorld()
                        .spawnParticle(Particle.SWEEP_ATTACK, var1.getLocation().clone().add(var27.multiply(0.9)).add(0.0, 1.0, 0.0), 1, 0.0, 0.0, 0.0, 0.0);
                  } else {
                     var2.getWorld().playSound(var2.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0F, 1.0F);
                  }
               }
            }
         }
      }
   }

   private boolean a(Player var1, ItemStack var2, boolean var3, boolean var4) {
      if (var2 == null || var2.getType().isAir()) {
         return false;
      } else if (!var3 && !var4 && !var1.isSprinting()) {
         if (!var2.getType().name().endsWith("_SWORD")) {
            return false;
         } else {
            return !MeleeMovementController.a(var1) ? false : Math.abs(var1.getVelocity().getY()) < 0.08;
         }
      } else {
         return false;
      }
   }

   private void a(Player var1, Player var2) {
      Location var3 = var1.getEyeLocation();
      Location var4 = var2.getEyeLocation();
      double var5 = var4.getX() - var3.getX();
      double var7 = var4.getY() - var3.getY();
      double var9 = var4.getZ() - var3.getZ();
      double var11 = Math.sqrt(var5 * var5 + var9 * var9);
      if (var11 < 0.001) {
         var11 = 0.001;
      }

      float var13 = (float)Math.toDegrees(Math.atan2(-var5, var9));
      float var14 = (float)(-Math.toDegrees(Math.atan2(var7, var11)));
      var14 = Math.max(-90.0F, Math.min(90.0F, var14));
      var1.setRotation(var13, var14);
   }

   public void a(NPC var1, Player var2, Player var3) {
      if (var1 != null && var2 != null) {
         var1.getNavigator().cancelNavigation();
         this.v(var1);
         if (this.b(var2, var3)) {
            this.a(var2, var3);
         }
      }
   }

   public boolean a(Player var1, Player var2, BotTrait var3, long var4) {
      return this.b(var1, var2, var3, var4);
   }

   public boolean h(Player var1) {
      if (var1.getCooldown(Material.SHIELD) > 0) {
         return false;
      } else if (!var1.isHandRaised()) {
         return false;
      } else {
         ItemStack var2 = var1.getActiveItem();
         return var2 != null && var2.getType() != Material.AIR ? var2.getType() == Material.SHIELD : false;
      }
   }

   private boolean a(Player var1, BotTrait var2, long var3) {
      return this.b(var1, var2, var3) >= 0.999;
   }

   private boolean b(Player var1, Player var2) {
      return BotTrait.isLiveCombatTarget(var2) && var1.getWorld().equals(var2.getWorld());
   }

   private boolean b(Player var1, Player var2, BotTrait var3, long var4) {
      if (!this.b(var1, var2)) {
         return false;
      } else {
         Location var6 = var1.getLocation();
         Location var7 = var2.getLocation();
         double var8 = var7.getY() - var6.getY();
         if (var8 <= 0.9) {
            if (var3 != null) {
               var3.overheadAvoidLockUntil = 0L;
            }

            return false;
         } else {
            double var10 = this.d(var6, var7);
            if (var10 <= 1.9) {
               if (var3 != null) {
                  var3.overheadAvoidLockUntil = Math.max(var3.overheadAvoidLockUntil, var4 + 650L);
               }

               return true;
            } else {
               return var3 != null && var4 < var3.overheadAvoidLockUntil && var10 <= 2.25;
            }
         }
      }
   }

   private double b(Player var1, BotTrait var2, long var3) {
      long var5 = this.i(var1);
      return var5 <= 0L ? 1.0 : Math.max(0.0, Math.min(1.0, (double)(var3 - var2.lastAttackTime) / var5));
   }

   private long i(Player var1) {
      return Math.max(50L, Math.round(1000.0 / this.j(var1)));
   }

   private double j(Player var1) {
      AttributeInstance var2 = var1.getAttribute(Attribute.ATTACK_SPEED);
      double var3 = var2 != null ? var2.getValue() : 4.0;
      return var3 > 0.01 ? var3 : 4.0;
   }

   private double a(Player var1, ItemStack var2) {
      AttributeInstance var3 = var1.getAttribute(Attribute.ATTACK_DAMAGE);
      double var4 = this.d(var2 == null ? Material.AIR : var2.getType());
      return var3 == null ? var4 : Math.max(1.0, var3.getValue() > 0.0 ? var3.getValue() : var4);
   }

   public double b(BotTrait var1) {
      return switch (var1.getPvpReachMode()) {
         case 0 -> this.r0.ai();
         case 1 -> this.r0.aj();
         case 2 -> this.r0.ak();
         default -> this.r0.al();
      };
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public double d(Material var1) {
      if (var1 == null) {
         return 1.0;
      } else {
         return switch (var1) {
            case WOODEN_SWORD, GOLDEN_SWORD -> 4.0;
            case STONE_SWORD -> 5.0;
            case IRON_SWORD -> 6.0;
            case DIAMOND_SWORD -> 7.0;
            case NETHERITE_SWORD -> 8.0;
            case WOODEN_AXE, GOLDEN_AXE -> 7.0;
            case STONE_AXE, IRON_AXE, DIAMOND_AXE -> 9.0;
            case NETHERITE_AXE -> 10.0;
            case WOODEN_PICKAXE, GOLDEN_PICKAXE -> 2.0;
            case STONE_PICKAXE -> 3.0;
            case IRON_PICKAXE -> 4.0;
            case DIAMOND_PICKAXE, NETHERITE_PICKAXE -> 5.0;
            case WOODEN_SHOVEL, GOLDEN_SHOVEL -> 2.5;
            case STONE_SHOVEL -> 3.5;
            case IRON_SHOVEL -> 4.5;
            case DIAMOND_SHOVEL, NETHERITE_SHOVEL -> 5.5;
            case WOODEN_HOE, STONE_HOE, GOLDEN_HOE -> 1.0;
            case IRON_HOE -> 2.0;
            case DIAMOND_HOE -> 3.0;
            case NETHERITE_HOE, TRIDENT -> 4.0;
            default -> 1.0;
         };
      }
   }

   private boolean c(Player var1, Player var2, BotTrait var3, long var4) {
      if (!MeleeMovementController.a(var1)) {
         return false;
      } else if (Math.abs(var1.getVelocity().getY()) > 0.03) {
         return false;
      } else if (!var1.hasLineOfSight(var2)) {
         return false;
      } else if (!this.a(var1, var2, var3)) {
         return false;
      } else if (!this.k(var1)) {
         return false;
      } else {
         return var4 - var3.lastLandingTime < var3.getCritJumpDelay() ? false : this.e(var3);
      }
   }

   private boolean a(Player var1, Player var2, BotTrait var3, double var4, long var6) {
      if (!var3.attemptingCrit) {
         return false;
      } else if (MeleeMovementController.a(var1)) {
         return false;
      } else if (var6 - var3.critJumpTime > 1000L) {
         return false;
      } else if (!var3.critSawAscent || !var3.critSawDescent) {
         return false;
      } else if (var3.critAirborneTicks >= 2 && var3.critDescendingTicks >= 1) {
         if (var3.peakY - var3.critStartY < 0.11) {
            return false;
         } else if (var1.getLocation().getY() - var3.critStartY < 0.05) {
            return false;
         } else {
            return var1.getVelocity().getY() > -0.03 ? false : var4 <= this.b(var3) && var1.hasLineOfSight(var2) && this.b(var1, var2, var3);
         }
      } else {
         return false;
      }
   }

   private boolean a(Player var1, Player var2, BotTrait var3) {
      Location var4 = var1.getLocation();
      Location var5 = var2.getLocation();
      double var6 = this.d(var4, var5);
      double var8 = Math.abs(var5.getY() - var4.getY());
      return var6 <= this.c(var3) && var8 <= 1.45;
   }

   private boolean b(Player var1, Player var2, BotTrait var3) {
      Location var4 = var1.getLocation();
      Location var5 = var2.getLocation();
      double var6 = this.d(var4, var5);
      double var8 = Math.abs(var5.getY() - var4.getY());
      return var6 <= this.d(var3) && var8 <= 1.45;
   }

   private double c(BotTrait var1) {
      double var2 = Math.min(this.b(var1) - 0.12, 2.95);
      return Math.max(2.3, var2);
   }

   private double d(BotTrait var1) {
      double var2 = Math.min(this.b(var1) - 0.2, 2.95);
      return Math.max(2.3, var2);
   }

   private boolean k(Player var1) {
      Location var2 = var1.getLocation();
      Block var3 = var2.clone().add(0.0, 1.0, 0.0).getBlock();
      Block var4 = var2.clone().add(0.0, 2.0, 0.0).getBlock();
      return !var3.getType().isSolid() && !var4.getType().isSolid();
   }

   private boolean e(BotTrait var1) {
      double var2 = Math.max(0.0, Math.min(1.0, var1.getCritChancePercent()));
      return var2 >= 0.999 || ThreadLocalRandom.current().nextDouble() < var2;
   }

   private long a(BotTrait var1, boolean var2) {
      if (!var2) {
         return 1L;
      } else {
         return switch (var1.getPvpCritSpeed()) {
            case 0 -> 2L;
            case 1 -> 1L;
            default -> 0L;
         };
      }
   }

   private double l(Player var1) {
      AttributeInstance var2 = var1.getAttribute(Attribute.KNOCKBACK_RESISTANCE);
      return var2 == null ? 0.0 : Math.max(0.0, Math.min(1.0, var2.getValue()));
   }

   private double d(Location var1, Location var2) {
      double var3 = var1.getX() - var2.getX();
      double var5 = var1.getZ() - var2.getZ();
      return Math.sqrt(var3 * var3 + var5 * var5);
   }

   private double a(Player var1, Player var2, BotTrait var3, long var4, double var6, double var8) {
      double var10 = 0.0;
      int var12 = var3.countRecentHits(var4, 2500L);
      var10 += Math.min(3.6, var12 * 0.85);
      if (var4 - var3.lastHitTakenTime <= 500L) {
         var10 += 0.9;
      }

      if (var3.consecutiveHitsTaken >= 2) {
         var10 += Math.min(2.4, (var3.consecutiveHitsTaken - 1) * 0.7);
      }

      double var13 = var1.getHealth() + var1.getAbsorptionAmount();
      double var15 = var2.getHealth() + var2.getAbsorptionAmount();
      if (var15 > var13) {
         var10 += Math.min(3.5, (var15 - var13) * 0.3);
      }

      if (var6 < var8 * 0.75) {
         var10++;
      } else if (var6 < var8) {
         var10 += 0.9;
      } else if (var6 < var8 + 0.6) {
         var10 += 0.35;
      }

      double var17 = 1.0 - this.b(var1, var3, var4);
      if (var17 > 0.05 && var6 < var8 + 0.35) {
         var10 += Math.min(1.5, var17 * 1.6);
      }

      if (var3.isShieldDisabled(var4)) {
         var10++;
      }

      if (var3.isUseShield() && var6 < var8 + 0.35 && var2.isSprinting()) {
         var10 += 0.7;
      }

      if (var3.isBlocking && var6 < var8 + 0.15) {
         var10 += 0.25;
      }
      var10 += switch (var3.getPvpAggression()) {
         case 0 -> 0.75;
         case 2 -> -0.45;
         default -> 0.2;
      };
      return Math.max(0.0, var10);
   }

   private boolean a(BotTrait var1, long var2, double var4) {
      if (var1.shouldRetreatNow(var2)) {
         return false;
      } else {
         double var6 = switch (var1.getPvpAggression()) {
            case 0 -> 3.6;
            case 2 -> 5.0;
            default -> 4.2;
         };
         return var4 >= var6;
      }
   }

   private boolean a(Player var1, Player var2, BotTrait var3, long var4, double var6, double var8, double var10) {
      if (!var3.shouldRetreatNow(var4)) {
         return true;
      } else {
         double var12 = this.a(var8);
         if (var6 >= var12 && this.b(var1, var3, var4) >= 0.9) {
            return true;
         } else {
            double var14 = switch (var3.getPvpAggression()) {
               case 0 -> 2.2;
               case 2 -> 3.0;
               default -> 2.6;
            };
            return var10 < var14 && var6 >= var8 + 0.35;
         }
      }
   }

   private double a(double var1) {
      return Math.max(var1 + 1.25, 3.35);
   }

   private long a(BotTrait var1, double var2) {
      long var4 = switch (var1.getPvpAggression()) {
         case 0 -> 750L;
         case 2 -> 500L;
         default -> 620L;
      };
      long var6 = (long)Math.min(350.0, Math.max(0.0, (var2 - 3.0) * 85.0));
      return var4 + var6;
   }

   private void a(NPC var1, Player var2, float var3) {
      FollowTrait var4 = (FollowTrait)var1.getOrAddTrait(FollowTrait.class);
      if (var4.getFollowing() != var2) {
         var4.follow(var2);

         try {
            var4.getClass().getMethod("setFollowingMargin", double.class).invoke(var4, 0.5);
         } catch (Exception var6) {
         }
      }

      var1.getNavigator().getDefaultParameters().speedModifier(var3);
   }

   private void v(NPC var1) {
      FollowTrait var2 = (FollowTrait)var1.getTraitNullable(FollowTrait.class);
      if (var2 != null && var2.getFollowing() != null) {
         var2.follow(null);
      }
   }
}
