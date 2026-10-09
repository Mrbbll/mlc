// Extracted from the user-provided PracticeBot-Source; strategy logic retained.
package com.mlc.mlcbot.practice.x;

import com.mlc.mlcbot.practice.bridge.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/** CPVP 剑与重锤攻击、切换及攻击窗口控制。 原源码：x/bv.java。 */
public final class SwordMaceController {
   private static final double hb = 3.0;
   private static final double hc = 4.0;
   private static final long hd = 600L;
   private static final long he = 400L;
   private static final long hf = 260L;
   private static final double hg = -0.15;
   private static final long hh = 1200L;
   private static final long hi = 420L;
   private final CpvpCombatController hj;

   public SwordMaceController(CpvpCombatController var1) {
      this.hj = var1;
   }

   public void j(CpvpCombatState var1) {
      if (var1 != null) {
         this.k(var1);
         var1.mg = false;
         var1.mi = 0;
         var1.mh = 0L;
         var1.lu = false;
         var1.lw = null;
         var1.lx = 0;
         var1.ly = false;
      }
   }

   public boolean k(CpvpCombatState var1, long var2) {
      if (var1 == null || var1.kO == 0L) {
         return false;
      } else if (var2 >= var1.kM) {
         this.k(var1);
         return false;
      } else {
         return true;
      }
   }

   public long a(Player var1, CpvpCombatState var2, CpvpSettings var3, long var4) {
      if (var2 == null) {
         return 0L;
      } else {
         long var6 = ++var2.kN;
         var2.kO = var6;
         long var8 = Math.max(260L, this.c(var1, var2, var3, var4) + 180L);
         var2.kM = var4 + var8;
         return var6;
      }
   }

   public boolean b(CpvpCombatState var1, long var2, long var4) {
      return var2 != 0L && this.k(var1, var4) && var1.kO == var2;
   }

   public boolean b(Player var1, CpvpCombatState var2, CpvpSettings var3, long var4) {
      return this.c(var1, var2, var3, var4) <= 0L;
   }

   public long c(Player var1, CpvpCombatState var2, CpvpSettings var3, long var4) {
      if (var1 == null || var2 == null) {
         return Long.MAX_VALUE;
      } else if (this.e(var2, var3, var4)) {
         return 0L;
      } else if (var2.kP > 0L && var4 - var2.kP <= 900L && var2.kK < var2.kP) {
         return 0L;
      } else {
         long var6 = Math.max(0L, var4 - var2.kK);
         return Math.max(0L, this.c(var1, var3) - var6);
      }
   }

   private boolean e(CpvpCombatState var1, CpvpSettings var2, long var3) {
      if (var1 == null || var1.kH <= 0L) {
         return false;
      } else if (var1.kL >= var1.kH) {
         return false;
      } else {
         long var5 = var3 - var1.kH;
         return var5 >= 0L && var5 <= this.n(var2);
      }
   }

   private long n(CpvpSettings var1) {
      long var2 = var1 == null ? 500L : Math.max(100L, var1.bJ());
      long var4 = var1 == null ? 0L : Math.max(0L, var1.bD());
      long var6 = Math.max(220L, Math.min(520L, Math.round(var2 * 0.6)));
      return Math.max(var6, Math.min(520L, var4 + 120L));
   }

   private void o(CpvpCombatState var1, long var2) {
      if (var1 != null && var1.kH > 0L) {
         if (var2 - var1.kH >= 0L && var2 - var1.kH <= 650L) {
            var1.kL = var1.kH;
         }
      }
   }

   private long c(Player var1, CpvpSettings var2) {
      long var3 = var2 == null ? 0L : Math.max(0L, var2.bJ());
      return Math.max(var3, this.i(var1));
   }

   private long i(Player var1) {
      return Math.max(50L, Math.round(1000.0 / this.j(var1)));
   }

   private double j(Player var1) {
      AttributeInstance var2 = var1.getAttribute(Attribute.ATTACK_SPEED);
      double var3 = var2 != null ? var2.getValue() : 4.0;
      return var3 > 0.01 ? var3 : 4.0;
   }

   public void k(CpvpCombatState var1) {
      if (var1 != null) {
         var1.kO = 0L;
         var1.kM = 0L;
      }
   }

   private void a(NPC var1, Player var2, Player var3, CpvpCombatState var4, long var5, String var7, boolean var8, String var9) {
   }

   private void a(NPC var1, Player var2, Player var3, CpvpCombatState var4) {
      if (var4 != null) {
         var4.lW = null;
         var4.lX = 0L;
         var4.lt = false;
         var4.lB = false;
         var4.mq = false;
         this.hj.i(var4);
         this.hj.h(var4);
         this.hj.l(var4);
      }

      Location var5 = this.hj.q(var3);
      if (var5 != null) {
         this.hj.a(var1, var2, var5, var4);
      }
   }

   private boolean u(Player var1, Player var2) {
      if (var1 != null && var2 != null && var1.getWorld().equals(var2.getWorld())) {
         Location var3 = var1.getLocation();
         Location var4 = var2.getLocation();
         if (var3.getBlockX() == var4.getBlockX() && var3.getBlockY() == var4.getBlockY() && var3.getBlockZ() == var4.getBlockZ()) {
            Vector var5 = var4.toVector().subtract(var3.toVector());
            var5.setY(0.0);
            return var5.lengthSquared() <= 0.17639999999999997;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private boolean a(NPC var1, Player var2, Player var3, CpvpSettings var4, CpvpCombatState var5, long var6, String var8) {
      if (!this.hj.j(var4)) {
         return false;
      } else if (this.u(var2, var3)) {
         return false;
      } else {
         boolean var9 = var5 != null && (var5.mG || var5.mH || var5.mW > var6 || var5.my > 0L && var6 - var5.my <= 900L);
         boolean var10 = var5 != null && this.hj.p(var2, var3, var4, var5, var6);
         if (!var9 && !var10) {
            return false;
         } else {
            this.a(var1, var2, var3, var5, var6, var8, false, "ANCHORING_SUPPRESSED_MELEE");
            this.j(var5);
            return true;
         }
      }
   }

   private boolean b(NPC var1, Player var2, Player var3, CpvpSettings var4, CpvpCombatState var5, long var6, String var8) {
      CrystalCombatPlan var9 = this.hj.k(var2, var3, var4, var5, var6);
      if (!var9.dP()) {
         return false;
      } else {
         this.a(var1, var2, var3, var5, var6, var8, false, "SELF_DAMAGE_UNSAFE_RECOVERY");
         this.j(var5);
         return true;
      }
   }

   private boolean ar(Player var1, Player var2, CpvpSettings var3, CpvpCombatState var4, long var5) {
      if (var1 != null && var2 != null && var3 != null && var4 != null) {
         if (!this.hj.cr().ak(var2)) {
            return false;
         } else if (this.hj.l(var1, var2, var3, var4, var5)) {
            return false;
         } else {
            if (this.hj.j(var3) && !this.u(var1, var2)) {
               boolean var7 = var4.mG || var4.mH || var4.mW > var5 || var4.my > 0L && var5 - var4.my <= 900L;
               boolean var8 = this.hj.p(var1, var2, var3, var4, var5);
               if (var7 || var8) {
                  return false;
               }
            }

            return !this.hj.q(var1, var2, var3, var4, var5) || this.e(var1, var2, var4);
         }
      } else {
         return false;
      }
   }

   private boolean e(Player var1, Player var2, CpvpCombatState var3) {
      if (var1 == null || var2 == null || var3 == null) {
         return false;
      } else {
         return !var3.lB && !var3.mq && !var3.mH && !var3.mG ? var1.getLocation().distance(var2.getLocation()) <= 3.35 : false;
      }
   }

   private boolean as(Player var1, Player var2, CpvpSettings var3, CpvpCombatState var4, long var5) {
      if (!this.ar(var1, var2, var3, var4, var5)) {
         return false;
      } else if (!this.hj.cr().ap(var1)) {
         return false;
      } else if (!this.b(var1, var4, var3, var5)) {
         return false;
      } else {
         var1.attack(var2);
         var1.swingMainHand();
         this.o(var4, var5);
         return true;
      }
   }

   private boolean at(Player var1, Player var2, CpvpSettings var3, CpvpCombatState var4, long var5) {
      if (!this.ar(var1, var2, var3, var4, var5)) {
         return false;
      } else if (!this.hj.cr().a(var1, Material.MACE)) {
         return false;
      } else {
         var1.attack(var2);
         var1.swingMainHand();
         return true;
      }
   }

   private void q(Player var1, CpvpCombatState var2) {
      this.k(var2);
      if (var1 != null && var1.isValid() && !var1.isDead()) {
         var1.setSprinting(true);
         this.hj.u(var1);
      }
   }

   private void r(Player var1, CpvpCombatState var2) {
      com.mlc.mlcbot.combat.CombatRuntime.runTaskLater(this.hj.ca().plugin(), () -> {
         if (var1 != null && var1.isValid() && !var1.isDead()) {
            if (!this.k(var2, System.currentTimeMillis())) {
               var1.setSprinting(true);
               this.hj.u(var1);
            }
         }
      }, 2L);
   }

   private void a(NPC var1, Player var2, Player var3, CpvpSettings var4, CpvpCombatState var5, long var6, boolean var8, int var9) {
      com.mlc.mlcbot.combat.CombatRuntime.runTaskLater(this.hj.ca().plugin(), () -> this.b(var1, var2, var3, var4, var5, var6, var8), var9);
   }

   private void b(NPC var1, Player var2, Player var3, CpvpSettings var4, CpvpCombatState var5, long var6, boolean var8) {
      long var9 = System.currentTimeMillis();
      if (!this.b(var5, var6, var9)) {
         this.a(var1, var2, var3, var5, var9, "pendingSwordExecution", false, "PENDING_SWORD_EXPIRED");
      } else if (!var2.isValid() || var2.isDead() || !var3.isValid() || var3.isDead()) {
         this.a(var1, var2, var3, var5, var9, "pendingSwordExecution", false, "INVALID_ENTITY");
         this.q(var2, var5);
      } else if (this.b(var1, var2, var3, var4, var5, var9, "pendingSwordExecution")) {
         this.q(var2, var5);
      } else {
         this.a(var1, var2, var3, var5);
         if (this.hj.q(var2, var3, var4, var5, var9) && !this.e(var2, var3, var5)) {
            this.a(var1, var2, var3, var5, var9, "pendingSwordExecution", false, "MELEE_SUPPRESSED");
            this.q(var2, var5);
         } else if (!this.hj.co().h(var2.getEyeLocation(), var3.getEyeLocation())) {
            this.a(var1, var2, var3, var5, var9, "pendingSwordExecution", false, "NO_LOS");
            this.q(var2, var5);
         } else {
            double var11 = var2.getLocation().distance(var3.getLocation());
            if (var11 > 3.3) {
               this.a(var1, var2, var3, var5, var9, "pendingSwordExecution", false, "OUT_OF_RANGE");
               this.q(var2, var5);
            } else if (!this.hj.cr().ap(var2)) {
               this.a(var1, var2, var3, var5, var9, "pendingSwordExecution", false, "NO_SWORD");
               this.q(var2, var5);
            } else {
               long var13 = this.c(var2, var5, var4, var9);
               if (var13 > 0L) {
                  long var15 = var9 + var13;
                  if (var15 >= var5.kM) {
                     this.a(var1, var2, var3, var5, var9, "pendingSwordExecution", false, "ATTACK_DELAY_EXPIRED_PENDING_WINDOW");
                     this.q(var2, var5);
                  } else {
                     this.a(var1, var2, var3, var5, var9, "pendingSwordExecution", false, "WAITING_ATTACK_DELAY=" + var13 + "ms");
                     int var17 = Math.max(1, (int)Math.ceil(var13 / 50.0));
                     this.a(var1, var2, var3, var4, var5, var6, var8, var17);
                  }
               } else {
                  this.a(var1, var2, var3, var5);
                  var2.setSprinting(var8);
                  this.a(var1, var2, var3, var5, var9, "pendingSwordExecution", true, "EXECUTE_SWORD_ATTACK");
                  if (!this.as(var2, var3, var4, var5, var9)) {
                     this.a(var1, var2, var3, var5, var9, "pendingSwordExecution", false, "DIRECT_ATTACK_FAILED");
                     this.q(var2, var5);
                  } else {
                     var5.kK = var9;
                     this.a(var5, var2, var3, var9);
                     this.k(var5);
                     this.r(var2, var5);
                  }
               }
            }
         }
      }
   }

   public boolean d(Player var1, CpvpCombatState var2, CpvpSettings var3, long var4) {
      if (!this.hj.cm().f(var3)) {
         return false;
      } else if (this.hj.ag(var1)) {
         return false;
      } else if (var1.getVelocity().getY() >= -0.15) {
         return false;
      } else {
         double var6 = this.v(var1);
         double var8 = 0.0;
         if (var2.kT > 0.0) {
            var8 = var2.kT - var1.getLocation().getY();
         }

         return var6 < 2.0 && var8 < 2.0 ? false : var4 - var2.kP < 1200L || var2.ly || var2.lu || var2.airTicks > 1;
      }
   }

   public boolean f(NPC var1, Player var2, Player var3, CpvpSettings var4, CpvpCombatState var5, long var6) {
      if (!this.b(var1, var2, var3, var4, var5, var6, "handleManualMaceSwap") && !this.a(var1, var2, var3, var4, var5, var6, "handleManualMaceSwap")) {
         if (!this.hj.cm().f(var4)) {
            this.a(var1, var2, var3, var5, var6, "handleManualMaceSwap", false, "MACE_DISABLED");
            var5.mg = false;
            this.hj.cr().a(var2, Material.NETHERITE_SWORD, Material.DIAMOND_SWORD);
            return false;
         } else {
            this.a(var1, var2, var3, var5);
            if (this.hj.q(var2, var3, var4, var5, var6) && !this.e(var2, var3, var5)) {
               this.a(var1, var2, var3, var5, var6, "handleManualMaceSwap", false, "MELEE_SUPPRESSED");
               var5.mg = false;
               this.hj.cr().a(var2, Material.END_CRYSTAL);
               return false;
            } else {
               double var8 = var2.getLocation().distance(var3.getLocation());
               boolean var10 = false;
               if (var6 > var5.mh) {
                  var10 = true;
               }

               if (var5.mi >= 2) {
                  var10 = true;
               }

               if (var8 > 5.5) {
                  var10 = true;
               }

               if (this.hj.ag(var2) && var5.mi > 0) {
                  var10 = true;
               }

               if (var10) {
                  this.a(var1, var2, var3, var5, var6, "handleManualMaceSwap", false, "MANUAL_MACE_END");
                  var5.mg = false;
                  var5.mj = var6;
                  this.hj.cr().a(var2, Material.NETHERITE_SWORD, Material.DIAMOND_SWORD);
                  return false;
               } else {
                  this.hj.cr().a(var2, Material.MACE);
                  Location var11 = this.hj.q(var3);
                  if (var11 != null) {
                     this.hj.a(var1, var2, var11, var5);
                  }

                  if (var8 <= 4.0 && var6 - var5.kR >= 600L) {
                     if (this.hj.co().h(var2.getEyeLocation(), var3.getEyeLocation())) {
                        long var12 = System.currentTimeMillis();
                        this.a(var1, var2, var3, var5, var12, "handleManualMaceSwap.attack", true, "EXECUTE_MANUAL_MACE");
                        if (this.at(var2, var3, var4, var5, var12)) {
                           var5.kR = var12;
                           var5.mi++;
                        } else {
                           this.a(var1, var2, var3, var5, var12, "handleManualMaceSwap.attack", false, "DIRECT_ATTACK_FAILED");
                        }
                     } else {
                        this.a(var1, var2, var3, var5, var6, "handleManualMaceSwap.attack", false, "NO_LOS");
                     }
                  }

                  return true;
               }
            }
         }
      } else {
         var5.mg = false;
         this.hj.cr().a(var2, Material.END_CRYSTAL);
         return false;
      }
   }

   public boolean b(NPC var1, Player var2, Player var3, CpvpCombatState var4, long var5, CpvpSettings var7) {
      if (!this.hj.cr().ak(var3)) {
         this.a(var1, var2, var3, var4, var5, "tryDtapAttack", false, "INVALID_TARGET");
         return false;
      } else if (!this.b(var1, var2, var3, var7, var4, var5, "tryDtapAttack") && !this.a(var1, var2, var3, var7, var4, var5, "tryDtapAttack")) {
         this.a(var1, var2, var3, var4);
         if (this.hj.q(var2, var3, var7, var4, var5) && !this.e(var2, var3, var4)) {
            this.a(var1, var2, var3, var4, var5, "tryDtapAttack", false, "MELEE_SUPPRESSED");
            return false;
         } else if (this.k(var4, var5)) {
            this.a(var1, var2, var3, var4, var5, "tryDtapAttack", false, "PENDING_SWORD_ACTIVE");
            return false;
         } else {
            double var8 = var2.getLocation().distance(var3.getLocation());
            if (var8 > 3.3) {
               this.a(var1, var2, var3, var4, var5, "tryDtapAttack", false, "OUT_OF_RANGE");
               return false;
            } else if (var5 - var4.oF < 400L) {
               this.a(var1, var2, var3, var4, var5, "tryDtapAttack", false, "DTAP_COOLDOWN=" + (400L - (var5 - var4.oF)) + "ms");
               return false;
            } else if (!this.hj.co().h(var2.getEyeLocation(), var3.getEyeLocation())) {
               this.a(var1, var2, var3, var4, var5, "tryDtapAttack", false, "NO_LOS");
               return false;
            } else if (!this.hj.cr().ap(var2)) {
               this.a(var1, var2, var3, var4, var5, "tryDtapAttack", false, "NO_SWORD");
               return false;
            } else if (!this.b(var2, var4, var7, var5)) {
               this.a(var1, var2, var3, var4, var5, "tryDtapAttack", false, "ATTACK_DELAY=" + this.c(var2, var4, var7, var5) + "ms");
               return false;
            } else {
               var2.setSprinting(false);
               var4.oG = false;
               var4.oH = var5;
               var4.oF = var5;
               this.a(var1, var2, var3, var4);
               long var10 = this.a(var2, var4, var7, var5);
               this.a(var1, var2, var3, var4, var5, "pendingSwordStart.dtap", true, "DTAP_PENDING_SWORD_STARTED");
               com.mlc.mlcbot.combat.CombatRuntime.runTaskLater(
                     this.hj.ca().plugin(),
                     () -> {
                        long var8x = System.currentTimeMillis();
                        if (!this.b(var4, var10, var8x)) {
                           this.a(var1, var2, var3, var4, var8x, "tryDtapAttack.phase", false, "PENDING_SWORD_EXPIRED");
                        } else if (!var2.isValid() || var2.isDead() || !var3.isValid() || var3.isDead()) {
                           this.a(var1, var2, var3, var4, var8x, "tryDtapAttack.phase", false, "INVALID_ENTITY");
                           this.q(var2, var4);
                        } else if (!this.b(var1, var2, var3, var7, var4, var8x, "tryDtapAttack.phase")
                           && !this.a(var1, var2, var3, var7, var4, var8x, "tryDtapAttack.phase")) {
                           double var10x = var2.getLocation().distance(var3.getLocation());
                           if (var10x > 3.5) {
                              this.a(var1, var2, var3, var4, var8x, "tryDtapAttack.phase", false, "OUT_OF_RANGE");
                              this.q(var2, var4);
                           } else {
                              this.a(var1, var2, var3, var4);
                              if (this.hj.q(var2, var3, var7, var4, var8x) && !this.e(var2, var3, var4)) {
                                 this.a(var1, var2, var3, var4, var8x, "tryDtapAttack.phase", false, "MELEE_SUPPRESSED");
                                 this.q(var2, var4);
                              } else {
                                 var2.setSprinting(true);
                                 Vector var12 = var3.getLocation().toVector().subtract(var2.getLocation().toVector()).setY(0);
                                 if (var12.lengthSquared() > 0.01) {
                                    var12.normalize().multiply(0.15);
                                    Vector var13 = var2.getVelocity();
                                    var13.setX(var13.getX() + var12.getX());
                                    var13.setZ(var13.getZ() + var12.getZ());
                                    var2.setVelocity(var13);
                                 }

                                 this.a(var1, var2, var3, var7, var4, var10, true, 1);
                              }
                           }
                        } else {
                           this.q(var2, var4);
                        }
                     },
                     1L
                  );
               return true;
            }
         }
      } else {
         return false;
      }
   }

   public boolean g(NPC var1, Player var2, Player var3, CpvpSettings var4, CpvpCombatState var5, long var6) {
      if (!this.b(var1, var2, var3, var4, var5, var6, "tryFallingMaceAttack") && !this.a(var1, var2, var3, var4, var5, var6, "tryFallingMaceAttack")) {
         if (!this.hj.cm().f(var4)) {
            this.a(var1, var2, var3, var5, var6, "tryFallingMaceAttack", false, "MACE_DISABLED");
            return false;
         } else if (!this.hj.cr().a(var2, Material.MACE)) {
            this.a(var1, var2, var3, var5, var6, "tryFallingMaceAttack", false, "NO_MACE");
            return false;
         } else if (!this.hj.cr().ak(var3)) {
            this.a(var1, var2, var3, var5, var6, "tryFallingMaceAttack", false, "INVALID_TARGET");
            return false;
         } else {
            this.a(var1, var2, var3, var5);
            if (this.hj.q(var2, var3, var4, var5, var6) && !this.e(var2, var3, var5)) {
               this.a(var1, var2, var3, var5, var6, "tryFallingMaceAttack", false, "MELEE_SUPPRESSED");
               return false;
            } else if (this.hj.ag(var2)) {
               var5.lh = 0;
               var5.ox = 0L;
               this.a(var1, var2, var3, var5, var6, "tryFallingMaceAttack", false, "BOT_ON_GROUND");
               return false;
            } else {
               double var8 = var2.getVelocity().getY();
               if (var8 > -0.08) {
                  this.a(var1, var2, var3, var5, var6, "tryFallingMaceAttack", false, "NOT_FALLING");
                  return false;
               } else {
                  double var10 = 0.0;
                  if (var5.kT > 0.0) {
                     var10 = var5.kT - var2.getLocation().getY();
                  }

                  double var12 = this.v(var2);
                  if (var10 < 2.0 && var12 < 2.0) {
                     if (var2.getInventory().getItemInMainHand().getType() == Material.MACE) {
                        this.hj.cr().a(var2, Material.END_CRYSTAL);
                     }

                     this.a(var1, var2, var3, var5, var6, "tryFallingMaceAttack", false, "FALL_HEIGHT_TOO_LOW");
                     return false;
                  } else {
                     double var14 = var2.getLocation().distance(var3.getLocation());
                     double var16 = this.hj.d(var2.getLocation(), var3.getLocation());
                     if (var16 > 4.5) {
                        this.a(var1, var2, var3, var5, var6, "tryFallingMaceAttack", false, "HORIZONTAL_RANGE_FAIL");
                        return false;
                     } else if (var14 > 5.5) {
                        this.a(var1, var2, var3, var5, var6, "tryFallingMaceAttack", false, "RANGE_FAIL");
                        return false;
                     } else {
                        this.a(var1, var2, var3, var5);
                        boolean var18 = true;
                        if (var10 >= 2.0) {
                           long var19 = var6 - var5.ox;
                           if (var19 > 150L) {
                              var5.oy = !var5.oy;
                              var5.ox = var6;
                           }

                           if (!(var8 < -0.5) && !(var10 > 4.0)) {
                              var18 = var5.oy;
                           } else {
                              var18 = true;
                           }
                        }

                        if (var18) {
                           this.hj.cr().a(var2, Material.MACE);
                        } else {
                           this.hj.cr().a(var2, Material.NETHERITE_SWORD, Material.DIAMOND_SWORD);
                        }

                        if (var14 <= 4.0 && var6 - var5.kR >= 350L) {
                           if (this.hj.co().h(var2.getEyeLocation(), var3.getEyeLocation())) {
                              long var22 = System.currentTimeMillis();
                              this.a(
                                 var1,
                                 var2,
                                 var3,
                                 var5,
                                 var22,
                                 var18 ? "tryFallingMaceAttack.mace" : "tryFallingMaceAttack.sword",
                                 true,
                                 var18 ? "EXECUTE_FALLING_MACE" : "EXECUTE_FALLING_SWORD"
                              );
                              boolean var21 = var18 ? this.at(var2, var3, var4, var5, var22) : this.as(var2, var3, var4, var5, var22);
                              if (var21) {
                                 var5.kR = var22;
                                 var5.lh++;
                              } else {
                                 this.a(
                                    var1,
                                    var2,
                                    var3,
                                    var5,
                                    var22,
                                    var18 ? "tryFallingMaceAttack.mace" : "tryFallingMaceAttack.sword",
                                    false,
                                    "DIRECT_ATTACK_FAILED"
                                 );
                              }
                           } else {
                              this.a(var1, var2, var3, var5, var6, "tryFallingMaceAttack", false, "NO_LOS");
                           }
                        }

                        return true;
                     }
                  }
               }
            }
         }
      } else {
         return false;
      }
   }

   public void h(NPC var1, Player var2, Player var3, CpvpSettings var4, CpvpCombatState var5, long var6) {
      if (!this.b(var1, var2, var3, var4, var5, var6, "handleMaceAttack") && !this.a(var1, var2, var3, var4, var5, var6, "handleMaceAttack")) {
         if (!this.hj.cr().ak(var3)) {
            this.a(var1, var2, var3, var5, var6, "handleMaceAttack", false, "INVALID_TARGET");
            this.d(var2, var5);
         } else if (var3 != null && !var3.isDead()) {
            if (!var3.getUniqueId().equals(var5.lw)) {
               this.a(var1, var2, var3, var5, var6, "handleMaceAttack", false, "TARGET_MISMATCH");
               this.d(var2, var5);
            } else {
               this.a(var1, var2, var3, var5);
               if (this.hj.q(var2, var3, var4, var5, var6) && !this.e(var2, var3, var5)) {
                  this.a(var1, var2, var3, var5, var6, "handleMaceAttack", false, "MELEE_SUPPRESSED");
                  this.d(var2, var5);
               } else {
                  double var8 = var2.getLocation().distance(var3.getLocation());
                  double var10 = this.hj.d(var2.getLocation(), var3.getLocation());
                  boolean var12 = !this.hj.ag(var2);
                  double var13 = var2.getVelocity().getY();
                  boolean var15 = var12 && var13 < -0.1;
                  if (!var12 && this.hj.ag(var2) && var5.lx <= 0) {
                     this.a(var1, var2, var3, var5, var6, "handleMaceAttack", false, "NO_MACE_SWINGS_REMAINING");
                     this.d(var2, var5);
                  } else if (var6 - var5.lv > 5000L) {
                     this.a(var1, var2, var3, var5, var6, "handleMaceAttack", false, "MACE_SWAP_TIMEOUT");
                     this.d(var2, var5);
                  } else {
                     this.a(var1, var2, var3, var5);
                     if (var15 && var10 <= 4.5) {
                        double var24 = 0.0;
                        if (var5.kT > 0.0) {
                           var24 = var5.kT - var2.getLocation().getY();
                        }

                        boolean var18 = true;
                        long var19 = var6 - var5.ox;
                        if (var19 > 120L) {
                           if (!(var13 < -0.6) && !(var24 > 5.0)) {
                              var5.oy = !var5.oy;
                              var18 = var5.oy;
                           } else {
                              var18 = true;
                           }

                           var5.ox = var6;
                        }

                        if (var18) {
                           this.hj.cr().a(var2, Material.MACE);
                        } else {
                           this.hj.cr().a(var2, Material.NETHERITE_SWORD, Material.DIAMOND_SWORD);
                        }

                        if (var8 <= 4.0 && var6 - var5.kR >= 300L && var3.isValid() && !var3.isDead()) {
                           long var21 = System.currentTimeMillis();
                           this.a(
                              var1,
                              var2,
                              var3,
                              var5,
                              var21,
                              var18 ? "handleMaceAttack.airMace" : "handleMaceAttack.airSword",
                              true,
                              var18 ? "EXECUTE_AIR_MACE" : "EXECUTE_AIR_SWORD"
                           );
                           boolean var23 = var18 ? this.at(var2, var3, var4, var5, var21) : this.as(var2, var3, var4, var5, var21);
                           if (var23) {
                              var5.kR = var21;
                              var5.lx--;
                           } else {
                              this.a(
                                 var1, var2, var3, var5, var21, var18 ? "handleMaceAttack.airMace" : "handleMaceAttack.airSword", false, "DIRECT_ATTACK_FAILED"
                              );
                           }
                        }
                     } else if (var12 && !var15 && var6 < var5.lv + 500L) {
                        this.hj.cr().a(var2, Material.MACE);
                     } else if (!var12 && var5.lx > 0 && var8 <= 4.0) {
                        this.hj.cr().a(var2, Material.MACE);
                        if (var6 - var5.kR >= 600L && var3.isValid() && !var3.isDead()) {
                           long var16 = System.currentTimeMillis();
                           this.a(var1, var2, var3, var5, var16, "handleMaceAttack.groundMace", true, "EXECUTE_GROUND_MACE");
                           if (this.at(var2, var3, var4, var5, var16)) {
                              var5.kR = var16;
                              var5.lx--;
                              if (var5.lx <= 0) {
                                 com.mlc.mlcbot.combat.CombatRuntime.runTaskLater(this.hj.ca().plugin(), () -> this.d(var2, var5), 3L);
                              }
                           } else {
                              this.a(var1, var2, var3, var5, var16, "handleMaceAttack.groundMace", false, "DIRECT_ATTACK_FAILED");
                           }
                        }
                     } else {
                        if (var8 > 7.0 || var6 - var5.lv > 4000L) {
                           this.a(var1, var2, var3, var5, var6, "handleMaceAttack", false, "RESET_MACE_STATE");
                           this.d(var2, var5);
                        }
                     }
                  }
               }
            }
         } else {
            this.a(var1, var2, var3, var5, var6, "handleMaceAttack", false, "INVALID_ENTITY");
            this.d(var2, var5);
         }
      } else {
         this.d(var2, var5);
      }
   }

   public void d(Player var1, CpvpCombatState var2) {
      var2.lu = false;
      var2.lw = null;
      var2.lx = 0;
      var2.ly = false;
      var2.ox = 0L;
      var2.oy = true;
      this.hj.cr().a(var1, Material.END_CRYSTAL);
   }

   public double v(Player var1) {
      Location var2 = var1.getLocation();

      for (int var3 = 0; var3 <= 50; var3++) {
         Block var4 = var2.clone().add(0.0, -var3 - 0.1, 0.0).getBlock();
         if (var4.getType().isSolid()) {
            return var3 + (var2.getY() - Math.floor(var2.getY()));
         }
      }

      return 50.0;
   }

   public boolean c(NPC var1, Player var2, Player var3, CpvpCombatState var4, long var5, CpvpSettings var7) {
      if (!this.hj.cr().ak(var3)) {
         this.a(var1, var2, var3, var4, var5, "hitWithKBSword", false, "INVALID_TARGET");
         return false;
      } else if (!this.b(var1, var2, var3, var7, var4, var5, "hitWithKBSword") && !this.a(var1, var2, var3, var7, var4, var5, "hitWithKBSword")) {
         this.a(var1, var2, var3, var4);
         if (this.hj.q(var2, var3, var7, var4, var5) && !this.e(var2, var3, var4)) {
            this.a(var1, var2, var3, var4, var5, "hitWithKBSword", false, "MELEE_SUPPRESSED");
            return false;
         } else if (this.k(var4, var5)) {
            this.a(var1, var2, var3, var4, var5, "hitWithKBSword", false, "PENDING_SWORD_ACTIVE");
            return false;
         } else {
            double var8 = var2.getLocation().distance(var3.getLocation());
            if (var8 > 3.0) {
               this.a(var1, var2, var3, var4, var5, "hitWithKBSword", false, "OUT_OF_RANGE");
               return false;
            } else {
               var2.setSprinting(false);
               this.a(var1, var2, var3, var4);
               if (!this.hj.cr().ap(var2)) {
                  this.a(var1, var2, var3, var4, var5, "hitWithKBSword", false, "NO_SWORD");
                  return false;
               } else if (!this.b(var2, var4, var7, var5)) {
                  this.a(var1, var2, var3, var4, var5, "hitWithKBSword", false, "ATTACK_DELAY=" + this.c(var2, var4, var7, var5) + "ms");
                  return false;
               } else {
                  long var10 = this.a(var2, var4, var7, var5);
                  this.a(var1, var2, var3, var4, var5, "pendingSwordStart.kbSword", true, "KB_PENDING_SWORD_STARTED");
                  this.a(var1, var2, var3, var7, var4, var10, false, 1);
                  return true;
               }
            }
         }
      } else {
         return false;
      }
   }

   public boolean i(NPC var1, Player var2, Player var3, CpvpSettings var4, CpvpCombatState var5, long var6) {
      if (!this.hj.cr().ak(var3)) {
         this.a(var1, var2, var3, var5, var6, "forceIdleSwordHit", false, "INVALID_TARGET");
         return false;
      } else if (this.a(var1, var2, var3, var4, var5, var6, "forceIdleSwordHit")) {
         return false;
      } else if (var5 != null
         && !var5.lt
         && !var5.lB
         && !var5.mq
         && !var5.mG
         && !var5.mH
         && !var5.mg
         && !var5.lu
         && !var5.ly
         && !var5.lk
         && !var5.os
         && !this.hj.k(var5, var6)) {
         double var8 = var2.getLocation().distance(var3.getLocation());
         if (var8 > 3.35) {
            this.a(var1, var2, var3, var5, var6, "forceIdleSwordHit", false, "OUT_OF_RANGE");
            return false;
         } else if (!this.hj.co().h(var2.getEyeLocation(), var3.getEyeLocation())) {
            this.a(var1, var2, var3, var5, var6, "forceIdleSwordHit", false, "NO_LOS");
            return false;
         } else if (!this.hj.cr().ap(var2)) {
            this.a(var1, var2, var3, var5, var6, "forceIdleSwordHit", false, "NO_SWORD");
            return false;
         } else if (!this.b(var2, var5, var4, var6)) {
            this.a(var1, var2, var3, var5);
            this.a(var1, var2, var3, var5, var6, "forceIdleSwordHit", false, "ATTACK_DELAY=" + this.c(var2, var5, var4, var6) + "ms");
            return false;
         } else {
            this.a(var1, var2, var3, var5);
            var2.setSprinting(true);
            var2.attack(var3);
            var2.swingMainHand();
            this.o(var5, var6);
            var5.kK = var6;
            this.a(var5, var2, var3, var6);
            this.hj.z(var2);
            this.a(var1, var2, var3, var5, var6, "forceIdleSwordHit", true, "IDLE_DIRECT_SWORD_HIT");
            return true;
         }
      } else {
         this.a(var1, var2, var3, var5, var6, "forceIdleSwordHit", false, "NON_IDLE_COMBAT_ACTION");
         return false;
      }
   }

   public void a(CpvpCombatState var1, Player var2, Player var3, long var4) {
      if (var1 != null && var2 != null && var3 != null) {
         var1.mI = var3.getUniqueId();
         var1.mK = var4;
         var1.mL = var3.getLocation().getY();
         var1.mJ = var4 + 420L;
         var1.mM = var3.getLocation().clone();
         var1.mN = var2.getLocation().clone();
         Vector var6 = var3.getLocation().toVector().subtract(var2.getLocation().toVector()).setY(0.0);
         if (var6.lengthSquared() < 0.001) {
            var6 = var2.getEyeLocation().getDirection().setY(0.0);
         }

         var1.mO = var6.lengthSquared() > 0.001 ? var6.normalize() : null;
         if (var4 - var1.mU > 2200L) {
            var1.mT = 0;
         }

         var1.mT++;
         var1.mU = var4;
      }
   }
}
