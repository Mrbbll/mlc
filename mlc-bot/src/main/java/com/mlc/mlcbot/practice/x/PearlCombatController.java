// Extracted from the user-provided PracticeBot-Source; strategy logic retained.
package com.mlc.mlcbot.practice.x;

import static com.mlc.mlcbot.nms.EntityGroundState.isOnGround;

import com.mlc.mlcbot.practice.bridge.NPC;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/** 珍珠追击、撤退、拦截和投掷时机决策。 原源码：x/g0.java。 */
public final class PearlCombatController {
   private static final long ja = 110L;
   private static final double jb = 0.65;
   private final CpvpCombatController jc;

   public PearlCombatController(CpvpCombatController var1) {
      this.jc = var1;
   }

   private boolean a(Player var1, Player var2, CpvpCombatState var3, long var4, String var6, double var7) {
      boolean var9 = var3 != null && (var3.ow || var3.og || var3.ly || var3.lu || var3.le > 1800L || this.jc.b(var1, var3));
      if (!var9 && var2 != null) {
         var9 = var2.getHealth() <= 6.0 || var7 >= 24.0;
      }

      return this.jc.b(var1, var6, var9);
   }

   public boolean c(NPC var1, Player var2, Player var3, CpvpSettings var4, CpvpCombatState var5, long var6) {
      if (!this.a(var5, var4, var6)) {
         return false;
      } else if (this.c(var5, var4, var6)) {
         return false;
      } else if (this.e(var5, var6)) {
         return false;
      } else if (this.b(var2, var3, var5)) {
         return false;
      } else {
         Location var8 = var2.getLocation();
         Location var9 = var3.getLocation();
         double var10 = var8.distance(var9);
         if (var10 < 3.0) {
            return false;
         } else if (!this.a(var2, var3, var5, var6, "pearl-engage", var10)) {
            return false;
         } else if (this.t(var2, var3, var4, var5, var6)) {
            return false;
         } else if (this.d(var2, var3, var4, var5, var6)) {
            return false;
         } else {
            Location var12;
            Vector var13;
            if (!isOnGround(var3) && this.v(var3) > 2.0) {
               if (!this.f(var4)) {
                  return false;
               }

               PearlTrajectorySolution var17 = this.c(var2.getEyeLocation(), var3);
               if (var17 == null || var17.hT || !var17.hQ && !(var17.hP <= 0.65)) {
                  return false;
               }

               var13 = var17.hO;
               var12 = var3.getLocation().add(0.0, 1.0, 0.0);
            } else {
               Vector var14 = var9.toVector().subtract(var8.toVector());
               var14.setY(0);
               if (var14.lengthSquared() < 0.01) {
                  return false;
               }

               var14.normalize();
               double var15 = Math.max(var10 - 2.5, 3.0);
               var12 = var8.clone().add(var14.multiply(var15));
               var12.setY(var9.getY());
               var13 = this.i(var2.getEyeLocation(), var12);
               if (var13 == null) {
                  var13 = this.j(var2.getEyeLocation(), var12);
               }
            }

            if (var13 != null && this.a(var13)) {
               int var18 = this.a(var2, var4, var6, 4);
               if (var18 > 0) {
                  var5.kE = var6;
                  this.jc.d(var4, var5);
               }

               return this.a(var1, var2, var3, var4, var5, var12, var13);
            } else {
               return false;
            }
         }
      }
   }

   public boolean d(Player var1, Player var2, CpvpSettings var3, CpvpCombatState var4, long var5) {
      if (var1 != null && var2 != null && var1.isValid() && var2.isValid() && !var1.isDead() && !var2.isDead()) {
         if (!var1.getWorld().equals(var2.getWorld())) {
            return false;
         } else {
            double var7 = var1.getLocation().distance(var2.getLocation());
            boolean var9 = var7 <= 7.5 || this.j(var4, var5);
            if (!var9) {
               return false;
            } else {
               double var10 = this.a(var1, var2, var3, var4, var5);
               if (this.j(var3)) {
                  AnchorCombatPlan var12 = this.b(var1, var2, var3, var4, var5);
                  if (var12 != null && this.a(var3, var4, var2, var5, 120L) && this.a(var12, var10, var1, var2, var3, var4, var5)) {
                     return true;
                  }
               }

               boolean var13 = this.a(var1, var2, var3, var4, var5, 120L);
               if (this.j(var4, var5) && var13) {
                  return true;
               } else {
                  return var10 > 2.25 && var13 ? true : this.d(var4, var3, var5) && var7 <= 4.75 && this.a(var1, var2, var3) > 4.0;
               }
            }
         }
      } else {
         return false;
      }
   }

   public boolean a(CpvpSettings var1, CpvpCombatState var2, Player var3, long var4, long var6) {
      if (!var2.mG && !var2.mH && !var2.lt && !var2.mq) {
         long var8 = Math.max(110L, this.a(var1, this.ab(var3)));
         return var4 + var6 - var2.my >= var8;
      } else {
         return true;
      }
   }

   public boolean a(Player var1, Player var2, CpvpSettings var3, CpvpCombatState var4, long var5, long var7) {
      if ((var4.lt || var4.lB || var4.mq || var5 < var4.oE) && this.g(var1, var2, var3, var4, var5)) {
         return true;
      } else if (this.h(var1, var2, var3, var4, var5)) {
         long var11 = this.b(var3, this.ab(var2));
         return var5 + var7 - var4.kE >= var11;
      } else if (!this.i(var1, var2, var3, var4, var5)) {
         return false;
      } else {
         long var9 = this.a(var3, this.ab(var2));
         return var5 + var7 - var4.kD >= var9;
      }
   }

   public boolean d(NPC var1, Player var2, Player var3, CpvpSettings var4, CpvpCombatState var5, long var6) {
      if (!this.a(var5, var4, var6) && !this.b(var2, var5)) {
         return false;
      } else if (this.c(var5, var4, var6) && !this.b(var2, var5)) {
         return false;
      } else if (this.e(var5, var6)) {
         return false;
      } else if (this.t(var2, var3, var4, var5, var6)) {
         return false;
      } else {
         Location var8 = var2.getLocation();
         Location var9 = var3.getLocation();
         World var10 = var2.getWorld();
         Vector var11 = var8.toVector().subtract(var9.toVector());
         var11.setY(0);
         if (var11.lengthSquared() < 0.01) {
            var11 = new Vector(1, 0, 0);
         }

         var11.normalize();
         Location var12 = null;
         Vector var13 = null;

         for (double var14 = 8.0; var14 <= 18.0; var14 += 3.0) {
            for (double var16 = 0.0; var16 <= 60.0; var16 += 15.0) {
               for (int var21 : new int[]{0, 1, -1}) {
                  Vector var22 = var21 == 0 ? var11.clone() : this.b(var11.clone(), Math.toRadians(var16 * var21));
                  Location var23 = var8.clone().add(var22.multiply(var14));
                  Location var24 = this.a(var10, var23, var8.getY());
                  if (var24 != null && this.h(var2.getEyeLocation(), var24.clone().add(0.0, 1.0, 0.0))) {
                     Vector var25 = this.i(var2.getEyeLocation(), var24);
                     if (var25 != null) {
                        var12 = var24;
                        var13 = var25;
                        break;
                     }
                  }
               }

               if (var12 != null) {
                  break;
               }
            }

            if (var12 != null) {
               break;
            }
         }

         if (var12 != null && var13 != null) {
            EnderCrystal var26 = this.a(var2, var4);
            if (var26 != null) {
               if (!this.a(var2, Material.END_CRYSTAL)) {
                  return false;
               }

               var2.attack(var26);
               var2.swingMainHand();
               var5.kE = var6;
               this.jc.d(var4, var5);
            }

            return this.a(var1, var2, var3, var4, var5, var12, var13);
         } else {
            return false;
         }
      }
   }

   public boolean e(NPC var1, Player var2, Player var3, CpvpSettings var4, CpvpCombatState var5, long var6) {
      if (!this.f(var4)) {
         return false;
      } else if (!this.j(var4) || var5 == null || !var5.mG && !var5.mH && var5.mW <= var6 && (var5.my <= 0L || var6 - var5.my > 900L)) {
         if (!this.a(var5, var4, var6)) {
            return false;
         } else if (!this.ak(var3)) {
            return false;
         } else if (this.c(var5, var4, var6)) {
            return false;
         } else if (var5.mk) {
            return false;
         } else if (var5.lu) {
            return false;
         } else if (var6 - var5.kP < 1000L) {
            return false;
         } else if (!this.a(var2, Material.ENDER_PEARL)) {
            return false;
         } else if (!this.a(var2, Material.MACE)) {
            return false;
         } else if (isOnGround(var3)) {
            return false;
         } else {
            double var8 = this.v(var3);
            double var10 = var3.getLocation().getY();
            double var12 = var2.getLocation().getY();
            double var14 = var10 - var12;
            boolean var16 = var14 >= 2.0;
            boolean var17 = var8 >= 3.0;
            if (!var16 && !var17) {
               return false;
            } else {
               Location var18 = var3.getLocation();
               Location var19 = var2.getLocation();
               double var20 = var19.distance(var18);
               double var22 = this.d(var19, var18);
               if (var20 < 2.0 || var20 > 40.0) {
                  return false;
               } else if (!this.a(var2, var3, var5, var6, "air-mace", var20)) {
                  return false;
               } else if (this.q(var2, var3, var4, var5, var6)) {
                  return false;
               } else if (this.t(var2, var3, var4, var5, var6)) {
                  return false;
               } else {
                  double var24 = var3.getVelocity().getY();
                  boolean var26 = var24 > 0.1;
                  boolean var27 = var24 < -0.1;
                  boolean var28 = var14 >= 2.0;
                  if (!var26 && !var27 && !var28 && var8 < 4.0) {
                     return false;
                  } else {
                     PearlTrajectorySolution var29 = this.c(var2.getEyeLocation(), var3);
                     if (var29 == null) {
                        return false;
                     } else if (var29.hT) {
                        return false;
                     } else if (!var29.hQ && var29.hP > 1.5) {
                        return false;
                     } else {
                        Vector var30 = var29.hO;
                        if (var30 != null && this.a(var30)) {
                           EnderCrystal var31 = this.a(var2, var4);
                           if (var31 != null) {
                              if (!this.a(var2, Material.END_CRYSTAL)) {
                                 return false;
                              }

                              var2.attack(var31);
                              var2.swingMainHand();
                              var5.kE = var6;
                              this.jc.d(var4, var5);
                           }

                           this.a(var1, var2, var18.clone().add(0.0, 1.0, 0.0), var5);
                           if (!this.a(var2, Material.ENDER_PEARL)) {
                              return false;
                           } else {
                              EnderPearl var32 = (EnderPearl)var2.launchProjectile(EnderPearl.class, var30);
                              var2.swingMainHand();
                              this.a(var5, var4, var6, var32);
                              var5.lu = true;
                              var5.lv = var6;
                              var5.lw = var3.getUniqueId();
                              var5.lx = 5;
                              var5.ly = true;
                              var5.kR = 0L;
                              var5.ox = 0L;
                              var5.oy = true;
                              return true;
                           }
                        } else {
                           return false;
                        }
                     }
                  }
               }
            }
         }
      } else {
         return false;
      }
   }

   public boolean b(Player var1, CpvpCombatState var2) {
      return this.jc.b(var1, var2);
   }

   public boolean a(CpvpCombatState var1, CpvpSettings var2, long var3) {
      return this.jc.a(var1, var2, var3);
   }

   public boolean c(CpvpCombatState var1, CpvpSettings var2, long var3) {
      return this.jc.c(var1, var2, var3);
   }

   public boolean e(CpvpCombatState var1, long var2) {
      return this.jc.e(var1, var2);
   }

   public boolean b(Player var1, Player var2, CpvpCombatState var3) {
      return this.jc.b(var1, var2, var3);
   }

   public boolean t(Player var1, Player var2, CpvpSettings var3, CpvpCombatState var4, long var5) {
      return this.jc.t(var1, var2, var3, var4, var5);
   }

   public double v(Player var1) {
      return this.jc.v(var1);
   }

   public boolean f(CpvpSettings var1) {
      return this.jc.f(var1);
   }

   public PearlTrajectorySolution c(Location var1, Player var2) {
      return this.jc.c(var1, var2);
   }

   public int a(Player var1, CpvpSettings var2, long var3, int var5) {
      return this.jc.a(var1, var2, var3, var5);
   }

   public boolean a(NPC var1, Player var2, Player var3, CpvpSettings var4, CpvpCombatState var5, Location var6, Vector var7) {
      return this.jc.a(var1, var2, var3, var4, var5, var6, var7);
   }

   public boolean j(CpvpCombatState var1, long var2) {
      return this.jc.j(var1, var2);
   }

   public double a(Player var1, Player var2, CpvpSettings var3, CpvpCombatState var4, long var5) {
      return this.jc.a(var1, var2, var3, var4, var5);
   }

   public boolean j(CpvpSettings var1) {
      return this.jc.j(var1);
   }

   public AnchorCombatPlan b(Player var1, Player var2, CpvpSettings var3, CpvpCombatState var4, long var5) {
      return this.jc.b(var1, var2, var3, var4, var5);
   }

   public boolean a(AnchorCombatPlan var1, double var2, Player var4, Player var5, CpvpSettings var6, CpvpCombatState var7, long var8) {
      return this.jc.a(var1, var2, var4, var5, var6, var7, var8);
   }

   public boolean d(CpvpCombatState var1, CpvpSettings var2, long var3) {
      return this.jc.d(var1, var2, var3);
   }

   public double a(Player var1, Player var2, CpvpSettings var3) {
      return this.jc.a(var1, var2, var3);
   }

   public boolean ab(Player var1) {
      return this.jc.ab(var1);
   }

   public long a(CpvpSettings var1, boolean var2) {
      return this.jc.a(var1, var2);
   }

   public boolean g(Player var1, Player var2, CpvpSettings var3, CpvpCombatState var4, long var5) {
      return this.jc.g(var1, var2, var3, var4, var5);
   }

   public boolean h(Player var1, Player var2, CpvpSettings var3, CpvpCombatState var4, long var5) {
      return this.jc.h(var1, var2, var3, var4, var5);
   }

   public boolean i(Player var1, Player var2, CpvpSettings var3, CpvpCombatState var4, long var5) {
      return this.jc.i(var1, var2, var3, var4, var5);
   }

   public long b(CpvpSettings var1, boolean var2) {
      return this.jc.b(var1, var2);
   }

   public boolean a(Player var1, Material var2) {
      return this.jc.a(var1, var2);
   }

   public Vector b(Vector var1, double var2) {
      return this.jc.b(var1, var2);
   }

   public Location a(World var1, Location var2, double var3) {
      return this.jc.a(var1, var2, var3);
   }

   public boolean h(Location var1, Location var2) {
      return this.jc.h(var1, var2);
   }

   public Vector i(Location var1, Location var2) {
      return this.jc.i(var1, var2);
   }

   public EnderCrystal a(Player var1, CpvpSettings var2) {
      return this.jc.a(var1, var2);
   }

   public boolean a(Player var1, Material... var2) {
      return this.jc.a(var1, var2);
   }

   public void a(NPC var1, Player var2, Location var3, CpvpCombatState var4) {
      this.jc.a(var1, var2, var3, var4);
   }

   public void a(CpvpCombatState var1, CpvpSettings var2, long var3, EnderPearl var5) {
      this.jc.a(var1, var2, var3, var5);
   }

   public boolean a(Vector var1) {
      return this.jc.a(var1);
   }

   public Vector j(Location var1, Location var2) {
      return this.jc.j(var1, var2);
   }

   public boolean ak(Player var1) {
      return this.jc.ak(var1);
   }

   public boolean q(Player var1, Player var2, CpvpSettings var3, CpvpCombatState var4, long var5) {
      return this.jc.q(var1, var2, var3, var4, var5);
   }

   public double d(Location var1, Location var2) {
      return this.jc.d(var1, var2);
   }
}
