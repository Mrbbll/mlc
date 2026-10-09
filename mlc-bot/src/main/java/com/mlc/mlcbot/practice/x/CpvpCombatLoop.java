// Extracted from the user-provided PracticeBot-Source; strategy logic retained.
package com.mlc.mlcbot.practice.x;

import static com.mlc.mlcbot.nms.EntityGroundState.isOnGround;

import com.mlc.mlcbot.practice.BotTrait;
import com.mlc.mlcbot.practice.PracticeBotPlugin;
import java.util.Random;
import com.mlc.mlcbot.practice.bridge.NPC;
import com.mlc.mlcbot.practice.bridge.FollowTrait;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/** 每次 tick 的 CPVP 主决策和策略调度。 原源码：x/au.java。 */
public final class CpvpCombatLoop {
   private static final int cF = 2;
   private static final int cG = 3;
   private static final double cH = 4.0;
   private static final double cI = 8.0;
   private static final long cJ = 1400L;
   private static final double cK = 5.0;
   private static final double cL = 3.0;
   private static final double cM = 4.0;
   private static final double cN = 0.65;
   private static final double cO = 0.9;
   private static final double cP = 2.25;
   private static final double cQ = 100.0;
   private static final long cR = 100L;
   private final CpvpCombatController cS;
   private final PracticeBotPlugin cT;
   private final Random cU;

   public CpvpCombatLoop(CpvpCombatController var1) {
      this.cS = var1;
      this.cT = var1.ca();
      this.cU = var1.cb();
   }

   public Location q(Player var1) {
      return this.cS.q(var1);
   }

   private void e(NPC var1, Player var2) {
      if (var1 != null) {
         var1.getNavigator().cancelNavigation();
         FollowTrait var3 = (FollowTrait)var1.getTraitNullable(FollowTrait.class);
         if (var3 != null && var3.getFollowing() != null) {
            var3.follow(null);
         }
      }

      if (var2 != null) {
         var2.setSprinting(false);
         Vector var4 = var2.getVelocity();
         var4.setX(var4.getX() * 0.35);
         var4.setZ(var4.getZ() * 0.35);
         var2.setVelocity(var4);
      }
   }

   private boolean a(NPC var1, Player var2, Player var3, CpvpCombatState var4, long var5, CpvpSettings var7) {
      boolean var8 = this.cU.nextInt(100) < 60;
      if (var8) {
         return this.cS.b(var1, var2, var3, var4, var5, var7) ? true : this.cS.c(var1, var2, var3, var4, var5, var7);
      } else {
         return this.cS.c(var1, var2, var3, var4, var5, var7) ? true : this.cS.b(var1, var2, var3, var4, var5, var7);
      }
   }

   private long c(CpvpCombatState var1) {
      if (var1 == null) {
         return 0L;
      } else {
         long var2 = Math.max(var1.kD, var1.kE);
         var2 = Math.max(var2, var1.kH);
         var2 = Math.max(var2, var1.my);
         var2 = Math.max(var2, var1.kK);
         var2 = Math.max(var2, var1.mK);
         var2 = Math.max(var2, var1.oj);
         return Math.max(var2, var1.lU);
      }
   }

   private boolean b(CpvpCombatState var1, long var2) {
      return var1 != null
         && (var1.lt || var1.lB || var1.mq || var1.mG || var1.mH || var1.mg || var1.lu || var1.ly || var1.lk || var1.os || this.cS.k(var1, var2));
   }

   private void a(Player var1, Player var2, CpvpCombatState var3, long var4, float var6, float var7) {
      if (var1 != null && var2 != null && var3 != null) {
         if (!this.b(var3, var4)) {
            if (var1.getWorld().equals(var2.getWorld())) {
               Location var8 = var1.getEyeLocation();
               Location var9 = var2.getLocation();
               double var10 = var9.getX() - var8.getX();
               double var12 = var9.getZ() - var8.getZ();
               if (!(var10 * var10 + var12 * var12 < 1.0E-4)) {
                  float var14 = var1.getLocation().getYaw();
                  float var15 = var1.getLocation().getPitch();
                  float var16 = (float)Math.toDegrees(Math.atan2(-var10, var12));
                  float var17 = var16 - var14;

                  while (var17 > 180.0F) {
                     var17 -= 360.0F;
                  }

                  while (var17 < -180.0F) {
                     var17 += 360.0F;
                  }

                  float var18 = var14 + var17 * var6;
                  float var19 = var15 * var7;
                  if (Math.abs(var19) < 0.35F) {
                     var19 = 0.0F;
                  }

                  var1.setRotation(var18, var19);
               }
            }
         }
      }
   }

   private boolean a(CpvpSettings var1, CpvpCombatState var2, long var3, String var5) {
      if (var1 != null && var2 != null && var5 != null) {
         int var6 = var1.bH();
         if (var6 <= 0) {
            var2.mc = 0L;
            var2.md = 0L;
            var2.me = 0L;
            var2.mf = "";
            return false;
         } else if (var5.equals(var2.mf) && var3 < var2.mc) {
            return true;
         } else if (var5.equals(var2.mf) && var3 < var2.md) {
            return false;
         } else if (var3 < var2.me) {
            return false;
         } else {
            var2.me = var3 + 180L;
            if (this.cU.nextInt(100) >= var6) {
               return false;
            } else {
               long var7 = Math.max(90L, var1.c(this.cU));
               if (var7 <= 0L) {
                  var7 = 120L + this.cU.nextInt(121);
               }

               var2.mf = var5;
               var2.mc = var3 + var7;
               var2.md = var2.mc + 420L;
               return true;
            }
         }
      } else {
         return false;
      }
   }

   private boolean a(NPC var1, Player var2, Player var3, CpvpSettings var4, CpvpCombatState var5, long var6, double var8) {
      if (var5 == null || var8 > 3.45) {
         return false;
      } else if (this.b(var5, var6)) {
         return false;
      } else if (var6 - var5.mV < 950L) {
         return false;
      } else {
         long var10 = this.c(var5);
         if (var10 > 0L && var6 - var10 < 1000L) {
            return false;
         } else {
            var5.mV = var6;
            this.cS.i(var5);
            this.cS.h(var5);
            var5.lt = false;
            return this.cS.j(var4) && this.cS.a(var1, var2, var3, var4, var5, var6) ? true : this.cS.i(var1, var2, var3, var4, var5, var6);
         }
      }
   }

   public void a(NPC var1, Player var2, Player var3, BotTrait var4) {
      if (var4.isCpvpEnabled()) {
         if (var2 != null && !var2.isDead()) {
            CpvpCombatState var5 = this.cS.ck().b(var2.getUniqueId(), var0 -> new CpvpCombatState());
            long var6 = System.currentTimeMillis();
            boolean editorOpen = false;
            if (editorOpen) {
               var1.getNavigator().cancelNavigation();
            } else {
               if (!BotTrait.isLiveCombatTarget(var3)) {
                  var3 = this.e(var2, var4);
               }

               if (var3 != null) {
                  if (this.cS.ak(var3)) {
                     if (var2.getWorld().equals(var3.getWorld())) {
                        CpvpSettings var9 = var4.getCpvpSettings();
                        var5.lB = false;
                        var5.lz = false;
                        var5.mx = false;
                        var5.mH = false;
                        if (var5.mG && var6 - var5.my > 1500L) {
                           this.cS.a(var2, var5);
                        }

                        this.cS.u(var2, var3, var9, var5, var6);
                        if (this.cS.j(var5, var6) && !var5.mq && !this.cS.g(var2, var3, var9, var5, var6) && var6 - var5.oj > 180L) {
                           this.cS.h(var5);
                           var5.lt = false;
                        }

                        if (var5.os && (var5.ot <= 0L || var6 - var5.ot > 100L)) {
                           this.cS.d(var5);
                        }

                        var5.lg = var3.getUniqueId();
                        if (var5.lk) {
                           double var10 = var2.getLocation().distance(var3.getLocation());
                           if (this.cS.b(var2, var3, var5, var6, var10)) {
                              this.cS.h(var2, var5);
                           } else {
                              this.e(var1, var2);
                              if (this.cS.e(var2, var5, var6)) {
                                 return;
                              }
                           }
                        }

                        this.cS.b(var2, var5, var6);
                        this.cS.a(var2, var5, var6);
                        this.cS.f(var2, var5, var6);
                        if (!this.cS.b(var1, var2, var3, var9, var5, var6)) {
                           if (!this.cS.j(var9) || !this.cS.cA().r(var2) && !this.cS.cA().s(var2) || !this.cS.a(var1, var2, var3, var9, var5, var6)) {
                              CrystalCombatPlan var41 = this.cS.k(var2, var3, var9, var5, var6);
                              boolean var11 = var41.dU() == CrystalPlanStatus.NONE && "HIGH_LOAD_THROTTLED".equals(var41.B());
                              if (var41.dO()) {
                                 var2.setGravity(true);
                                 this.cS.j(var5);
                                 if (var41.dP()) {
                                    boolean var42 = this.cS.t(var1, var2, var3, var9, var5, var6);
                                    if (!var42 && this.cS.o(var1, var2, var3, var9, var5, var6)) {
                                       return;
                                    }
                                 } else {
                                    boolean var43 = var41.dU() == CrystalPlanStatus.BREAK_SAFE
                                       ? this.cS.l(var1, var2, var3, var9, var5, var6)
                                       : this.cS.j(var1, var2, var3, var9, var5, var6);
                                    if (!var43) {
                                       if (this.cS.o(var1, var2, var3, var9, var5, var6)) {
                                          return;
                                       }

                                       Location var44 = var41.dS();
                                       if (var44 != null) {
                                          this.cS.c(var1, var2, var44, var5);
                                       }

                                       this.cS.u(var2);
                                    }
                                 }
                              } else {
                                 var2.setGravity(true);
                                 Vector var12 = var2.getVelocity();
                                 Location var13 = var2.getLocation();
                                 if (var5.lc == null) {
                                    var5.lc = var13.clone();
                                    var5.ld = var6;
                                 }

                                 boolean var14 = this.cS.ag(var2);
                                 if (var14) {
                                    var5.kT = var13.getY();
                                    var5.lastGroundTime = var6;
                                    var5.airTicks = 0;
                                    var5.kU++;
                                    var5.or = 0;
                                    if (var5.inKnockback && var5.kU >= 3) {
                                       double var15 = Math.sqrt(var12.getX() * var12.getX() + var12.getZ() * var12.getZ());
                                       if (var15 < 0.15 && Math.abs(var12.getY()) < 0.08) {
                                          var5.inKnockback = false;
                                          var5.kV = 0L;
                                          var5.kZ = true;
                                          var5.kY = var6;
                                       }
                                    }
                                 } else {
                                    var5.airTicks++;
                                    var5.kU = 0;
                                    double var45 = var12.length();
                                    if (var45 < 0.03) {
                                       var5.or++;
                                    } else {
                                       var5.or = 0;
                                    }
                                 }

                                 this.cS.f(var2, var5);
                                 this.cS.a(var2, var5, var12, var6, var14);
                                 if (!var14 && !this.cS.ac(var2)) {
                                    if (var5.inKnockback) {
                                       this.cS.a(var1, var2, var3, var9);
                                       var5.kW = var12.clone();
                                       var5.ol = var12.length();
                                    } else if (!var5.oI) {
                                       double var48 = var2.getVelocity().getY();
                                       double var49 = var2.getLocation().distance(var3.getLocation());
                                       double var19 = this.cS.d(var2.getLocation(), var3.getLocation());
                                       double var21 = this.cS.v(var2);
                                       if (var48 < -0.08
                                          && var21 >= 2.0
                                          && var19 < 5.5
                                          && var49 < 6.5
                                          && !this.cS.q(var2, var3, var9, var5, var6)
                                          && this.cS.f(var9)
                                          && this.cS.a(var2, Material.MACE)) {
                                          this.cS.g(var1, var2, var3, var9, var5, var6);
                                       }

                                       if (!this.cS.m(var2, var3, var9, var5, var6)) {
                                          this.cS.a(var1, var2, this.q(var3), var5);
                                       }

                                       if (var5.lu) {
                                          this.cS.h(var1, var2, var3, var9, var5, var6);
                                       }

                                       if (var5.airTicks > 20 && var5.or > 10 && var5.lc != null) {
                                          double var23 = var5.lc.distance(var13);
                                          if (var23 < 0.01) {
                                             Vector var25 = var2.getVelocity();
                                             var25.setY(-0.15);
                                             var2.setVelocity(var25);
                                             var5.or = 0;
                                          }
                                       }

                                       if (var5.airTicks > 10) {
                                          double var50 = var12.length();
                                          if (var50 < 0.03) {
                                             Location var51 = var13.clone().add(0.0, -0.5, 0.0);
                                             Block var52 = var51.getBlock();
                                             if (var52.getType().isSolid()) {
                                                var5.airTicks = 0;
                                                var5.or = 0;
                                                var5.kU = 1;
                                                if (var5.inKnockback) {
                                                   var5.inKnockback = false;
                                                   var5.kZ = false;
                                                   var5.om = false;
                                                }
                                             }
                                          }
                                       }

                                       var5.kW = var12.clone();
                                       var5.ol = var12.length();
                                    } else {
                                       var5.oM++;
                                       this.cS.e(var2, var5);
                                       if (var5.oM > 25 || var2.getLocation().getY() < var5.oN - 1.0) {
                                          this.cS.l(var5, var6);
                                       }

                                       if (!this.cS.m(var2, var3, var9, var5, var6)) {
                                          this.cS.a(var1, var2, this.q(var3), var5);
                                       }

                                       var5.kW = var12.clone();
                                       var5.ol = var12.length();
                                    }
                                 } else if (this.cS.ac(var2)) {
                                    if (!this.cS.g(var5, var6)) {
                                       EnderCrystal var47 = this.cS.aa(var2);
                                       if (var47 != null) {
                                          this.cS.a(var1, var2, var3, var47, var9, var5, var6);
                                       }

                                       this.cS.l(var1, var2, var3, var9, var5, var6);
                                    }

                                    this.cS.u(var1, var2, var3, var9, var5, var6);
                                 } else if (!this.cS.p(var1, var2, var3, var9, var5, var6)) {
                                    double var46 = var5.lc.distance(var13);
                                    if (var6 - var5.ld > 500L) {
                                       if (var46 < 0.5) {
                                          var5.le = var5.le + (var6 - var5.ld);
                                       } else {
                                          var5.le = 0L;
                                       }

                                       var5.lc = var13.clone();
                                       var5.ld = var6;
                                    }

                                    this.cS.ah(var2);
                                    Location var17 = var3.getLocation();
                                    double var18 = var13.distance(var17);
                                    double var20 = var2.getAttribute(Attribute.MAX_HEALTH).getValue();
                                    double var22 = var2.getHealth();
                                    double var24 = var22 / var20;
                                    boolean var26 = var24 < this.cS.l(var9);
                                    boolean var27 = this.cS.ab(var3);
                                    if (!var5.mg || !this.cS.f(var1, var2, var3, var9, var5, var6)) {
                                       if (var5.lu) {
                                          this.cS.h(var1, var2, var3, var9, var5, var6);
                                          if (var5.lu || var5.lw != null || var5.lx > 0 || var5.ly) {
                                             return;
                                          }
                                       }

                                       if (var5.lk) {
                                          if (this.cS.b(var2, var3, var5, var6, var18)) {
                                             this.cS.h(var2, var5);
                                          } else {
                                             this.e(var1, var2);
                                             if (this.cS.e(var2, var5, var6)) {
                                                return;
                                             }
                                          }
                                       }

                                       if (this.cS.k(var5, var6)) {
                                          boolean var28 = this.cS.o(var2, var3, var9, var5, var6) || this.cS.m(var2, var3, var9, var5, var6);
                                          boolean var29 = this.cS.j(var9) && (var5.mG || var5.mH || var5.mW > var6 || var5.my > 0L && var6 - var5.my <= 900L);
                                          if (!var28 && !var29) {
                                             if (!this.cS.a(var2, var3, var5)) {
                                                this.cS.a(var1, var2, this.q(var3), var5);
                                             }

                                             var5.kW = var12.clone();
                                             var5.ol = var12.length();
                                             return;
                                          }

                                          this.cS.j(var5);
                                          this.cS.u(var2);
                                       }

                                       if (!this.cS.f(var9) || !this.cS.e(var1, var2, var3, var9, var5, var6)) {
                                          if (!var26) {
                                             if (var5.lq) {
                                                var5.lq = false;
                                                var5.lr = false;
                                                var5.ls = false;
                                             }

                                             if (var5.ln > 0 || var5.lo > 0) {
                                                var5.ln = 0;
                                                var5.lo = 0;
                                             }
                                          }

                                          if (var5.mG) {
                                             if (var5.le <= 900L && var6 - var5.my <= 1500L) {
                                                Location var55 = var5.mB != null ? var5.mB : this.q(var3);
                                                this.cS.b(var1, var2, var55, var5);
                                                return;
                                             }

                                             this.cS.a(var2, var5);
                                          }

                                          if (!this.cS.g(var5, var6) && !this.cS.k(var5, var6)) {
                                             if (!var5.mq && this.cS.a(var1, var2, var9, var5, var6)) {
                                                return;
                                             }

                                             EnderCrystal var53 = this.cS.aa(var2);
                                             if (var53 != null) {
                                                this.cS.a(var1, var2, var3, var53, var9, var5, var6);
                                             }

                                             if (this.cS.j(var5, var6)) {
                                                Block var56 = var5.oi.getBlock();
                                                if (var56.getType() == Material.OBSIDIAN && this.cS.a(var56, var3, var5, var6)) {
                                                   if (this.cS.a(var1, var2, var3, var56, var9, var5, var6)) {
                                                      this.cS.h(var5);
                                                      return;
                                                   }
                                                } else {
                                                   this.cS.h(var5);
                                                }
                                             }

                                             if (var6 < var5.mv && var5.mw != null && !var5.lz) {
                                                Block var57 = var5.mw.getBlock();
                                                if (var57.getType() == Material.OBSIDIAN && this.cS.a(var57, var3, var5, var6)) {
                                                   if (this.cS.a(var1, var2, var3, var57, var9, var5, var6)) {
                                                      return;
                                                   }
                                                } else {
                                                   var5.mv = 0L;
                                                   var5.mw = null;
                                                }
                                             }

                                             if (this.cS.m(var1, var2, var3, var9, var5, var6)) {
                                                return;
                                             }

                                             if (this.cS.a(var1, var2, var3, var9, var5, var6)) {
                                                return;
                                             }

                                             if (this.cS.j(var2, var3, var9, var5, var6) && this.cS.l(var1, var2, var3, var9, var5, var6)) {
                                                return;
                                             }

                                             if (!var11 && !this.cS.a(var2, var3, var5, var9, var6) && this.cS.j(var1, var2, var3, var9, var5, var6)) {
                                                return;
                                             }

                                             if (this.cS.l(var1, var2, var3, var9, var5, var6)) {
                                                return;
                                             }
                                          }

                                          if (var26
                                             && this.cS.g(var9)
                                             && var18 > 4.0
                                             && !var5.lk
                                             && !var5.mk
                                             && !var5.lB
                                             && !var5.lt
                                             && !var5.mq
                                             && !var5.mG
                                             && !var5.mH
                                             && !this.cS.m(var2, var3, var9, var5, var6)
                                             && var5.ln < Math.max(1, var5.lo <= 0 ? 2 : var5.lo)
                                             && this.cS.d(var2, var5, var6)) {
                                             this.e(var1, var2);
                                          } else if (!this.cS.t(var1, var2, var3, var9, var5, var6)) {
                                             boolean var54 = this.cS.j(var9) && (this.cS.cA().r(var2) || this.cS.cA().s(var2));
                                             boolean var58 = !var54
                                                && this.cS.m(var2, var3)
                                                && !this.cS.j(var5, var6)
                                                && !this.cS.g(var5, var6)
                                                && this.cS.d(var5, var9, var6)
                                                && this.cS.a(var2, Material.OBSIDIAN)
                                                && this.cS.a(var2, Material.END_CRYSTAL);
                                             if (!var58 || !this.cS.k(var1, var2, var3, var9, var5, var6)) {
                                                if (this.cS.j(var5, var6) && !this.cS.g(var2, var3, var9, var5, var6)) {
                                                   this.cS.h(var5);
                                                   var5.lt = false;
                                                }

                                                if (this.cS.j(var9) && var5.mT >= 3 && !var5.mG && !var5.mH) {
                                                   var5.mW = var6 + 2200L;
                                                   this.cS.k(var5);
                                                   this.cS.i(var5);
                                                   this.cS.h(var5);
                                                   var5.lt = false;
                                                   var5.mq = false;
                                                   if (this.cS.a(var1, var2, var3, var9, var5, var6)) {
                                                      return;
                                                   }
                                                }

                                                if (!this.cS.j(var9)
                                                   || var5.mG
                                                   || var5.mH
                                                   || this.cS.g(var2, var3, var9, var5, var6)
                                                   || !this.cS.a(var1, var2, var3, var9, var5, var6)) {
                                                   if (!this.a(var1, var2, var3, var9, var5, var6, var18)) {
                                                      if (this.cS.g(var5, var6)
                                                         || this.cS.k(var5, var6)
                                                         || var11
                                                         || !this.cS.d(var5, var9, var6)
                                                         || !this.cS.a(var2, Material.OBSIDIAN)
                                                         || !this.cS.a(var2, Material.END_CRYSTAL)
                                                         || !this.cS.k(var1, var2, var3, var9, var5, var6)) {
                                                         if (!this.cS.o(var1, var2, var3, var9, var5, var6)) {
                                                            if (var14) {
                                                               var2.setSprinting(true);
                                                            }

                                                            if (!var14
                                                               || !(var18 > 5.0)
                                                               || !this.cS.a(var5, var9, var6)
                                                               || !this.cS.a(var2, Material.ENDER_PEARL)
                                                               || this.cS.c(var5, var9, var6)
                                                               || this.cS.e(var5, var6)
                                                               || this.a(var9, var5, var6, "AGGRESSIVE_CHASE")
                                                               || !this.cS.c(var1, var2, var3, var9, var5, var6)) {
                                                               if (var5.le > 1400L
                                                                  && var18 > 4.0
                                                                  && this.cS.a(var5, var9, var6)
                                                                  && this.cS.a(var2, Material.ENDER_PEARL)
                                                                  && var14
                                                                  && !this.cS.c(var5, var9, var6)
                                                                  && this.cS.c(var1, var2, var3, var9, var5, var6)) {
                                                                  var5.le = 0L;
                                                               } else if (!var26
                                                                  && var14
                                                                  && this.cS.a(var5, var9, var6)
                                                                  && this.cS.a(var2, Material.ENDER_PEARL)
                                                                  && isOnGround(var3)
                                                                  && this.cS.a(var2, var3, var5, var18)
                                                                  && !this.cS.c(var5, var9, var6)
                                                                  && this.cS.c(var1, var2, var3, var9, var5, var6)) {
                                                                  var5.le = 0L;
                                                               } else {
                                                                  if (!var26
                                                                     && var18 > 8.0
                                                                     && this.cS.a(var5, var9, var6)
                                                                     && this.cS.a(var2, Material.ENDER_PEARL)
                                                                     && var14
                                                                     && !this.cS.c(var5, var9, var6)) {
                                                                     if (!isOnGround(var3)
                                                                        && this.cS.v(var3) > 1.5
                                                                        && this.cS.b(var2, "airborne-pearl-intercept", var5.le > 1400L || var5.ow || var5.og)) {
                                                                        double var30 = var3.getLocation().getY();
                                                                        double var32 = var2.getLocation().getY();
                                                                        double var34 = var30 - var32;
                                                                        if (this.cS.f(var9) && var34 >= 2.0) {
                                                                           PearlTrajectorySolution var66 = this.cS.c(var2.getEyeLocation(), var3);
                                                                           if (var66 != null
                                                                              && (var66.hQ || var66.hP <= 1.15)
                                                                              && this.cS.c(var1, var2, var3, var9, var5, var6)) {
                                                                              return;
                                                                           }
                                                                        } else if (this.cS.f(var9)) {
                                                                           PearlTrajectorySolution var36 = this.cS.c(var2.getEyeLocation(), var3);
                                                                           if (var36 != null
                                                                              && (var36.hQ || var36.hP <= 0.65)
                                                                              && this.cS.c(var1, var2, var3, var9, var5, var6)) {
                                                                              return;
                                                                           }
                                                                        }
                                                                     } else if (!this.a(var9, var5, var6, "FAR_GROUND_CHASE")
                                                                        && this.cS.c(var1, var2, var3, var9, var5, var6)) {
                                                                        return;
                                                                     }
                                                                  }

                                                                  boolean var59 = this.cS.a(var2, var3, var5);
                                                                  if (!this.cS.m(var2, var3, var9, var5, var6) && var18 <= 3.0 && !var59) {
                                                                     this.cS.a(var1, var2, this.q(var3), var5);
                                                                  }

                                                                  if (!var14) {
                                                                     var5.kW = var12.clone();
                                                                     var5.ol = var12.length();
                                                                  } else if (var18 > 4.0) {
                                                                     double var60 = var3.getLocation().getY() - var2.getLocation().getY();
                                                                     double var62 = this.cS.d(var2.getLocation(), var3.getLocation());
                                                                     boolean var65 = var60 > 0.9 && var62 > 2.25;
                                                                     if (!this.cS.m(var2, var3, var9, var5, var6)) {
                                                                        this.a(var2, var3, var5, var6, var65 ? 0.52F : 0.42F, var65 ? 0.35F : 0.55F);
                                                                     }

                                                                     this.cS.s(var1, var2, var3, var9, var5, var6);
                                                                     if (!this.cS.m(var2, var3, var9, var5, var6)) {
                                                                        this.a(var2, var3, var5, var6, var65 ? 0.72F : 0.62F, var65 ? 0.25F : 0.45F);
                                                                     }

                                                                     boolean var68 = this.cS.ae(var3);
                                                                     boolean var37 = this.cS.q(var2, var3, var9, var5, var6);
                                                                     boolean var38 = var37 && !var5.lB && !var5.mq && !var5.mH && !var5.mG && var18 <= 3.5;
                                                                     if (var18 <= 3.5 && (!var37 || var38)) {
                                                                        boolean var39 = this.a(var1, var2, var3, var5, var6, var9);
                                                                        if (!var39) {
                                                                           var39 = this.cS.i(var1, var2, var3, var9, var5, var6);
                                                                        }

                                                                        if (var39) {
                                                                           var5.kW = var12.clone();
                                                                           var5.ol = var12.length();
                                                                           return;
                                                                        }
                                                                     }

                                                                     if (!this.cS.g(var5, var6) && !this.cS.j(var5, var6) && !this.cS.k(var5, var6)) {
                                                                        boolean var69 = this.cS.m(var2, var3);
                                                                        boolean var40 = var69 || !var11 && var6 - var5.kH >= 250 + this.cU.nextInt(40);
                                                                        if (var40
                                                                           && this.cS.d(var5, var9, var6)
                                                                           && !this.cS.q(var1, var2, var3, var9, var5, var6)
                                                                           && !this.cS.r(var1, var2, var3, var9, var5, var6)) {
                                                                           this.cS.k(var1, var2, var3, var9, var5, var6);
                                                                        }
                                                                     }
                                                                  } else {
                                                                     FollowTrait var31 = (FollowTrait)var1.getTraitNullable(FollowTrait.class);
                                                                     if (var31 != null && var31.getFollowing() != null) {
                                                                        var31.follow(null);
                                                                     }

                                                                     var1.getNavigator().cancelNavigation();
                                                                     if (this.cS.f(var9)
                                                                        && this.cS.d(var2, var5, var9, var6)
                                                                        && var18 <= 4.2
                                                                        && !this.cS.c(var5, var9, var6)
                                                                        && !var5.lt
                                                                        && !var5.mg
                                                                        && this.cS.a(var2, Material.MACE)
                                                                        && this.cS.h(var2.getEyeLocation(), var3.getEyeLocation())
                                                                        && var6 - var5.mj > 2000L
                                                                        && !var5.mk
                                                                        && !this.cS.q(var2, var3, var9, var5, var6)) {
                                                                        this.cS.j(var5);
                                                                        var5.mg = true;
                                                                        var5.mh = var6 + 450L;
                                                                        var5.mi = 0;
                                                                     }

                                                                     boolean var61 = this.cS.ae(var3);
                                                                     boolean var33 = this.cS.q(var2, var3, var9, var5, var6);
                                                                     boolean var63 = var33 && !var5.lB && !var5.mq && !var5.mH && !var5.mG && var18 <= 3.35;
                                                                     if (!var5.mg
                                                                        && (!var33 || var63)
                                                                        && var18 <= 3.0
                                                                        && this.cS.h(var2.getEyeLocation(), var3.getEyeLocation())) {
                                                                        boolean var35 = this.a(var1, var2, var3, var5, var6, var9);
                                                                        if (!var35) {
                                                                           var35 = this.cS.i(var1, var2, var3, var9, var5, var6);
                                                                        }

                                                                        if (var35) {
                                                                           var5.kW = var12.clone();
                                                                           var5.ol = var12.length();
                                                                           return;
                                                                        }
                                                                     }

                                                                     if (!this.cS.g(var5, var6) && !this.cS.j(var5, var6) && !this.cS.k(var5, var6)) {
                                                                        boolean var64 = this.cS.m(var2, var3);
                                                                        boolean var67 = var64 || !var11 && var6 - var5.kH >= 300 + this.cU.nextInt(60);
                                                                        if (var67
                                                                           && this.cS.d(var5, var9, var6)
                                                                           && !this.cS.q(var1, var2, var3, var9, var5, var6)
                                                                           && !this.cS.r(var1, var2, var3, var9, var5, var6)) {
                                                                           this.cS.k(var1, var2, var3, var9, var5, var6);
                                                                        }
                                                                     }

                                                                     if (!this.a(var1, var2, var3, var9, var5, var6, var18)) {
                                                                        this.cS.a(var1, var2, var3, var9, var5, var6, var26);
                                                                        var5.kW = var12.clone();
                                                                        var5.ol = var12.length();
                                                                     }
                                                                  }
                                                               }
                                                            }
                                                         }
                                                      }
                                                   }
                                                }
                                             }
                                          }
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public Player e(Player var1, BotTrait var2) {
      Player var3 = var2.getBehaviorTargetPlayer();
      if (!BotTrait.isLiveCombatTarget(var3)) {
         return null;
      } else if (!var1.getWorld().equals(var3.getWorld())) {
         return null;
      } else {
         double var4 = var1.getLocation().distance(var3.getLocation());
         if (var4 > 100.0) {
            return null;
         } else {
            return var3;
         }
      }
   }
}
