// Extracted from the user-provided PracticeBot-Source; strategy logic retained.
package com.mlc.mlcbot.practice.x;

import static com.mlc.mlcbot.nms.EntityGroundState.isOnGround;

import com.mlc.mlcbot.practice.BotTrait;
import com.mlc.mlcbot.practice.PracticeBotPlugin;
import com.mlc.mlcbot.practice.bridge.Navigator;
import com.mlc.mlcbot.practice.bridge.NPC;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/** 普通近战的地形、水中移动、侧移和退让控制。 原源码：x/x.java。 */
public class MeleeMovementController {
   private final PracticeBotPlugin t0;
   private final PracticeCombatConfig t1;

   public MeleeMovementController(PracticeBotPlugin var1) {
      this.t0 = var1;
      this.t1 = var1.getConfigManager();
   }

   public static boolean a(Entity var0) {
      if (isOnGround(var0)) {
         return true;
      } else {
         double var1 = var0.getVelocity().getY();
         if (Math.abs(var1) < 0.08) {
            Location var3 = var0.getLocation();
            Block var4 = var3.clone().add(0.0, -0.3, 0.0).getBlock();
            if (var4.getType().isSolid()) {
               return true;
            }
         }

         return false;
      }
   }

   public void a(Player var1, Player var2, BotTrait var3, NPC var4) {
      Location var5 = var1.getLocation();
      Block var6 = var5.getBlock();
      Block var7 = var5.clone().add(0.0, 0.8, 0.0).getBlock();
      Block var8 = var5.clone().add(0.0, 1.62, 0.0).getBlock();
      boolean var9 = var6.getType() == Material.WATER;
      boolean var10 = var7.getType() == Material.WATER;
      boolean var11 = var8.getType() == Material.WATER;
      var3.isInWater = true;
      Vector var12 = var1.getVelocity();
      Location var13 = null;
      boolean var14 = false;
      if (var2 != null && var2.isOnline() && var1.getWorld().equals(var2.getWorld()) && (var3.isPvpEnabled() || var3.isFollowOwner())) {
         var13 = var2.getLocation();
         var14 = true;
      }

      double var15 = var14 ? Math.sqrt(Math.pow(var13.getX() - var5.getX(), 2.0) + Math.pow(var13.getZ() - var5.getZ(), 2.0)) : 0.0;
      var4.getNavigator().cancelNavigation();
      Vector var17 = null;
      if (var14) {
         var17 = var13.toVector().subtract(var5.toVector());
         var17.setY(0);
         if (var17.lengthSquared() > 0.01) {
            var17.normalize();
         } else {
            var17 = null;
         }
      }

      if (var17 != null && (var9 || var10) && !var11) {
         Location var18 = var5.clone().add(var17.getX() * 1.0, 0.0, var17.getZ() * 1.0);
         Block var19 = var18.getBlock();
         Block var20 = var18.clone().add(0.0, 1.0, 0.0).getBlock();
         Block var21 = var18.clone().add(0.0, 2.0, 0.0).getBlock();
         boolean var22 = var19.getType().isSolid() && var19.getType() != Material.WATER && !var20.getType().isSolid() && !var21.getType().isSolid();
         if (var22 && var15 < 4.0) {
            var12.setY(0.45);
            var12.setX(var17.getX() * 0.4);
            var12.setZ(var17.getZ() * 0.4);
            var1.setVelocity(var12);
            return;
         }
      }

      if (var11) {
         var12.setY(Math.min(var12.getY() + 0.08, 0.35));
         if (var17 != null) {
            var12.setX(var12.getX() * 0.8 + var17.getX() * 0.06);
            var12.setZ(var12.getZ() * 0.8 + var17.getZ() * 0.06);
         }
      } else if (var10) {
         if (var12.getY() < 0.02) {
            var12.setY(0.04);
         }

         if (var17 != null) {
            double var29 = var3.isPvpEnabled() ? 0.12 : 0.08;
            var12.setX(var12.getX() * 0.7 + var17.getX() * var29);
            var12.setZ(var12.getZ() * 0.7 + var17.getZ() * var29);
            var1.setSprinting(true);
         }
      } else if (var9 && var17 != null) {
         var12.setX(var12.getX() * 0.9 + var17.getX() * 0.05);
         var12.setZ(var12.getZ() * 0.9 + var17.getZ() * 0.05);
      }

      var12.setX(var12.getX() * 0.88);
      var12.setZ(var12.getZ() * 0.88);
      var1.setVelocity(var12);
      if (var14) {
         Location var30 = var1.getEyeLocation();
         double var31 = var13.getX() - var30.getX();
         double var32 = var13.getY() - var30.getY();
         double var23 = var13.getZ() - var30.getZ();
         double var25 = Math.sqrt(var31 * var31 + var23 * var23);
         if (var25 < 0.001) {
            var25 = 0.001;
         }

         float var27 = (float)Math.toDegrees(Math.atan2(-var31, var23));
         float var28 = (float)(-Math.toDegrees(Math.atan2(var32, var25)));
         var28 = Math.max(-90.0F, Math.min(90.0F, var28));
         var1.setRotation(var27, var28);
      }
   }

   public void d(Player var1, Player var2, BotTrait var3, long var4) {
      if (isOnGround(var1)) {
         if (var4 - var3.lastJumpTime >= 350L) {
            Location var6 = var1.getLocation();
            double var7 = var6.getX() - var3.lastPosX;
            double var9 = var6.getZ() - var3.lastPosZ;
            double var11 = var7 * var7 + var9 * var9;
            var3.lastPosX = var6.getX();
            var3.lastPosZ = var6.getZ();
            if (var11 > 0.01) {
               var3.stuckTicks = 0;
            } else {
               var3.stuckTicks++;
               if (var3.stuckTicks >= 5) {
                  Vector var13 = this.c(var1, var2);
                  if (var13 != null) {
                     boolean var14 = this.c(var6, var13);
                     if (var14) {
                        Vector var15 = var1.getVelocity();
                        var15.setY(0.42);
                        var15.setX(var13.getX() * 0.2);
                        var15.setZ(var13.getZ() * 0.2);
                        var1.setVelocity(var15);
                        var3.lastJumpTime = var4;
                        var3.stuckTicks = 0;
                     }
                  }
               }
            }
         }
      }
   }

   public void e(Player var1, Player var2, BotTrait var3, long var4) {
      if (isOnGround(var1)) {
         if (var4 >= var3.sTapCooldown) {
            if (!var3.sTapActive) {
               Vector var6 = var1.getLocation().toVector().subtract(var2.getLocation().toVector());
               var6.setY(0);
               if (var6.lengthSquared() < 0.01) {
                  var6 = var1.getLocation().getDirection().multiply(-1);
                  var6.setY(0);
               }

               var6.normalize();
               var3.sTapActive = true;
               var3.sTapEndTime = var4 + 150L;
               var3.sTapDirection = var6.clone();
               var3.sTapCooldown = var4 + 350L;
               Vector var7 = var1.getVelocity();
               double var8 = var7.getY();
               double var10 = 0.28;
               var7.setX(var6.getX() * var10);
               var7.setZ(var6.getZ() * var10);
               var7.setY(var8);
               var1.setVelocity(var7);
               var1.setSprinting(false);
            }
         }
      }
   }

   public void a(Player var1, BotTrait var2, Navigator var3) {
      if (!isOnGround(var1)) {
         var2.sTapActive = false;
         var2.sTapDirection = null;
      } else if (var2.sTapDirection != null) {
         var3.cancelNavigation();
         Vector var4 = var1.getVelocity();
         double var5 = var4.getY();
         double var7 = 0.28;
         var4.setX(var2.sTapDirection.getX() * var7);
         var4.setZ(var2.sTapDirection.getZ() * var7);
         var4.setY(var5);
         var1.setVelocity(var4);
         var1.setSprinting(false);
      }
   }

   public void b(Player var1, Player var2, BotTrait var3, long var4, double var6, double var8) {
      if (isOnGround(var1)) {
         if (!var3.sTapActive) {
            long var10 = switch (var3.getPvpAggression()) {
               case 0 -> this.t1.aw();
               case 2 -> this.t1.ay();
               default -> this.t1.ax();
            };
            if (var4 - var3.lastStrafeSwitch > var10) {
               var3.strafeRight = !var3.strafeRight;
               var3.lastStrafeSwitch = var4;
            }

            Vector var12 = var2.getLocation().toVector().subtract(var1.getLocation().toVector());
            var12.setY(0);
            if (!(var12.lengthSquared() < 0.01)) {
               var12.normalize();
               Vector var13 = var3.strafeRight ? new Vector(-var12.getZ(), 0.0, var12.getX()) : new Vector(var12.getZ(), 0.0, -var12.getX());

               double var14 = switch (var3.getPvpAggression()) {
                  case 0 -> this.t1.at();
                  case 2 -> this.t1.av();
                  default -> this.t1.au();
               };
               double var16 = var13.getX() * var14;
               double var18 = var13.getZ() * var14;
               double var20 = var6 - var8;

               double var22 = switch (var3.getPvpAggression()) {
                  case 0 -> 0.08;
                  case 2 -> 0.14;
                  default -> 0.11;
               };
               if (var20 > 0.4) {
                  var16 += var12.getX() * var22;
                  var18 += var12.getZ() * var22;
               } else if (var20 < -0.2) {
                  var16 -= var12.getX() * (var22 * 0.6);
                  var18 -= var12.getZ() * (var22 * 0.6);
               }

               if (Math.random() < 0.15) {
                  double var24 = (Math.random() - 0.5) * 0.06;
                  var16 += var24;
                  var18 += var24;
               }

               Vector var26 = var1.getVelocity();
               var26.setX(var16);
               var26.setZ(var18);
               var1.setVelocity(var26);
            }
         }
      }
   }

   public void f(Player var1, Player var2, BotTrait var3, long var4) {
      if (isOnGround(var1)) {
         long var6 = switch (var3.getPvpAggression()) {
            case 0 -> this.t1.bc();
            case 2 -> this.t1.be();
            default -> this.t1.bd();
         };
         if (var4 - var3.lastStrafeSwitch > var6) {
            var3.strafeRight = !var3.strafeRight;
            var3.lastStrafeSwitch = var4;
         }

         Vector var8 = var2.getLocation().toVector().subtract(var1.getLocation().toVector());
         var8.setY(0);
         if (!(var8.lengthSquared() < 0.01)) {
            var8.normalize();
            Vector var9 = var3.strafeRight ? new Vector(-var8.getZ(), 0.0, var8.getX()) : new Vector(var8.getZ(), 0.0, -var8.getX());

            double var10 = switch (var3.getPvpAggression()) {
               case 0 -> this.t1.az();
               case 2 -> this.t1.bb();
               default -> this.t1.ba();
            };

            double var12 = switch (var3.getPvpAggression()) {
               case 0 -> this.t1.bf();
               case 2 -> this.t1.bh();
               default -> this.t1.bg();
            };
            double var14 = var8.getX() * var12 + var9.getX() * var10;
            double var16 = var8.getZ() * var12 + var9.getZ() * var10;
            Vector var18 = var1.getVelocity();
            var18.setX(var14);
            var18.setZ(var16);
            var1.setVelocity(var18);
         }
      }
   }

   public void a(Player var1, Player var2, double var3, double var5) {
      Vector var7 = var2.getLocation().toVector().subtract(var1.getLocation().toVector());
      var7.setY(0);
      if (!(var7.lengthSquared() < 0.01)) {
         var7.normalize();
         Vector var8 = var1.getVelocity();
         double var9 = var8.getY();
         double var11 = var3 - var5;
         if (var11 > 0.2) {
            var8.setX(var7.getX() * 0.08);
            var8.setZ(var7.getZ() * 0.08);
         } else if (var11 < -0.2) {
            var8.setX(-var7.getX() * 0.06);
            var8.setZ(-var7.getZ() * 0.06);
         } else {
            var8.setX(var8.getX() * 0.5);
            var8.setZ(var8.getZ() * 0.5);
         }

         var8.setY(var9);
         var1.setVelocity(var8);
      }
   }

   public void a(Player var1, Player var2, BotTrait var3, double var4, double var6) {
      long var8 = System.currentTimeMillis();
      Location var10 = this.b(var1, var2, var3, var4, var8);
      if (var10 != null) {
         Vector var11 = var10.toVector().subtract(var1.getLocation().toVector());
         var11.setY(0.0);
         if (!(var11.lengthSquared() < 1.0E-6)) {
            if (this.a(var1.getLocation(), var11) && var8 >= var3.overheadAvoidLockUntil - 120L) {
               var3.overheadOrbitClockwise = !var3.overheadOrbitClockwise;
               var3.overheadAvoidLockUntil = var8 + 650L;
               var10 = this.b(var1, var2, var3, var4, var8);
               if (var10 == null) {
                  return;
               }

               var11 = var10.toVector().subtract(var1.getLocation().toVector());
               var11.setY(0.0);
               if (var11.lengthSquared() < 1.0E-6) {
                  return;
               }
            }

            Vector var12 = var11.normalize().multiply(var6);
            Vector var13 = var1.getVelocity();
            if (a(var1)) {
               var13.setX(var13.getX() * 0.35 + var12.getX());
               var13.setZ(var13.getZ() * 0.35 + var12.getZ());
            } else {
               var13.setX(var13.getX() * 0.78 + var12.getX() * 0.55);
               var13.setZ(var13.getZ() * 0.78 + var12.getZ() * 0.55);
               this.a(var13, Math.max(var6 + 0.04, 0.24));
            }

            var1.setVelocity(var13);
         }
      }
   }

   public void a(Player var1, Player var2, BotTrait var3, Navigator var4, long var5, double var7, double var9) {
      var4.cancelNavigation();
      boolean var11 = a(var1);
      Vector var12 = var1.getLocation().toVector().subtract(var2.getLocation().toVector());
      var12.setY(0);
      if (var12.lengthSquared() < 0.01) {
         var12 = var2.getLocation().getDirection().multiply(-1);
         var12.setY(0);
      }

      var12.normalize();

      long var13 = switch (var3.getPvpAggression()) {
         case 0 -> 340L;
         case 2 -> 220L;
         default -> 280L;
      };
      if (var5 - var3.lastStrafeSwitch > var13) {
         var3.strafeRight = !var3.strafeRight;
         var3.lastStrafeSwitch = var5;
      }

      Vector var15 = var3.strafeRight ? new Vector(-var12.getZ(), 0.0, var12.getX()) : new Vector(var12.getZ(), 0.0, -var12.getX());

      double var16 = switch (var3.getPvpAggression()) {
         case 0 -> 0.19;
         case 2 -> 0.13;
         default -> 0.16;
      };
      double var18 = var7 >= var9 ? 0.03 : 0.055;
      double var20 = var7 >= var9 ? 0.55 : 1.0;
      Vector var22 = var12.clone().multiply(var16 * var20).add(var15.clone().multiply(var18));
      if (this.a(var1.getLocation(), var22)) {
         var3.strafeRight = !var3.strafeRight;
         var15 = var3.strafeRight ? new Vector(-var12.getZ(), 0.0, var12.getX()) : new Vector(var12.getZ(), 0.0, -var12.getX());
         var22 = var12.clone().multiply(var16 * 0.9).add(var15.clone().multiply(var18 * 0.5));
      }

      Vector var23 = var1.getVelocity();
      if (var11 && this.b(var1.getLocation(), var22) && var5 - var3.lastJumpTime >= 350L) {
         var23.setY(Math.max(var23.getY(), 0.42));
         var3.lastJumpTime = var5;
      }

      if (var11) {
         var23.setX(var22.getX());
         var23.setZ(var22.getZ());
      } else {
         var23.setX(var23.getX() * 0.82 + var22.getX() * 0.95);
         var23.setZ(var23.getZ() * 0.82 + var22.getZ() * 0.95);
         this.a(var23, Math.max(var16 + 0.09, 0.26));
      }

      var1.setVelocity(var23);
   }

   private Location b(Player var1, Player var2, BotTrait var3, double var4, long var6) {
      if (var1 != null && var2 != null && var3 != null) {
         Location var8 = var1.getLocation();
         Location var9 = var2.getLocation();
         if (var6 >= var3.overheadAvoidLockUntil) {
            var3.overheadOrbitClockwise = var3.strafeRight;
            var3.overheadAvoidLockUntil = var6 + 650L;
         } else {
            var3.overheadAvoidLockUntil = Math.max(var3.overheadAvoidLockUntil, var6 + 220L);
         }

         Vector var10 = var8.toVector().subtract(var9.toVector()).setY(0.0);
         double var11 = Math.sqrt(var10.getX() * var10.getX() + var10.getZ() * var10.getZ());
         if (var11 < 1.0E-4) {
            var10 = this.a(var8, var9, var3);
         } else {
            var10.normalize();
         }

         Vector var13 = var3.overheadOrbitClockwise ? new Vector(-var10.getZ(), 0.0, var10.getX()) : new Vector(var10.getZ(), 0.0, -var10.getX());
         double var14 = Math.max(var4, 1.0);
         if (var11 < var14 * 0.7) {
            var14 += 0.18;
         }

         double var16 = var11 < var14 ? 0.65 : 0.45;
         return var9.clone().add(var10.clone().multiply(var14)).add(var13.multiply(var16));
      } else {
         return null;
      }
   }

   private void a(Vector var1, double var2) {
      double var4 = Math.sqrt(var1.getX() * var1.getX() + var1.getZ() * var1.getZ());
      if (!(var4 <= var2) && !(var4 < 1.0E-6)) {
         double var6 = var2 / var4;
         var1.setX(var1.getX() * var6);
         var1.setZ(var1.getZ() * var6);
      }
   }

   private boolean a(Location var1, Vector var2) {
      Vector var3 = var2.clone().setY(0.0);
      if (var3.lengthSquared() < 1.0E-6) {
         return false;
      } else {
         var3.normalize().multiply(0.55);
         Location var4 = var1.clone().add(var3);
         Block var5 = var4.getBlock();
         Block var6 = var4.clone().add(0.0, 1.0, 0.0).getBlock();
         return var5.getType().isSolid() || var6.getType().isSolid();
      }
   }

   private boolean b(Location var1, Vector var2) {
      Vector var3 = var2.clone().setY(0.0);
      if (var3.lengthSquared() < 1.0E-6) {
         return false;
      } else {
         var3.normalize().multiply(0.6);
         Location var4 = var1.clone().add(var3);
         Block var5 = var4.getBlock();
         Block var6 = var4.clone().add(0.0, 1.0, 0.0).getBlock();
         Block var7 = var4.clone().add(0.0, 2.0, 0.0).getBlock();
         return var5.getType().isSolid() && !var6.getType().isSolid() && !var7.getType().isSolid();
      }
   }

   private Vector c(Player var1, Player var2) {
      Vector var3 = var1.getVelocity().clone().setY(0.0);
      if (var3.lengthSquared() > 0.0036) {
         return var3.normalize();
      } else {
         if (BotTrait.isLiveCombatTarget(var2) && var1.getWorld().equals(var2.getWorld())) {
            Location var4 = var1.getLocation();
            Location var5 = var2.getLocation();
            double var6 = this.d(var4, var5);
            double var8 = var5.getY() - var4.getY();
            if (!(var6 > 0.35) || var8 > 0.9 && var6 < 1.2) {
               return null;
            }

            Vector var10 = var5.toVector().subtract(var4.toVector()).setY(0.0);
            if (var10.lengthSquared() > 0.01) {
               return var10.normalize();
            }
         }

         Vector var11 = var1.getLocation().getDirection().setY(0.0);
         return var11.lengthSquared() > 0.01 ? var11.normalize() : null;
      }
   }

   private boolean c(Location var1, Vector var2) {
      if (var1 != null && var2 != null && !(var2.lengthSquared() < 1.0E-6)) {
         Vector var3 = var2.clone().setY(0.0).normalize().multiply(0.55);
         Location var4 = var1.clone().add(var3);
         Block var5 = var4.getBlock();
         Block var6 = var4.clone().add(0.0, 1.0, 0.0).getBlock();
         Block var7 = var4.clone().add(0.0, 2.0, 0.0).getBlock();
         if (var5.getType().isSolid() && !var6.getType().isSolid() && !var7.getType().isSolid()) {
            Block var8 = var4.clone().add(0.0, 0.9, 0.0).getBlock();
            return var8.getType().isSolid();
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private Vector a(Location var1, Location var2, BotTrait var3) {
      Vector var4 = var2.getDirection().clone().setY(0.0);
      if (var4.lengthSquared() < 1.0E-4) {
         var4 = var1.getDirection().clone().setY(0.0);
      }

      if (var4.lengthSquared() < 1.0E-4) {
         var4 = new Vector(1.0, 0.0, 0.0);
      }

      var4.normalize();
      return var3.overheadOrbitClockwise ? new Vector(-var4.getZ(), 0.0, var4.getX()) : new Vector(var4.getZ(), 0.0, -var4.getX());
   }

   private double d(Location var1, Location var2) {
      double var3 = var1.getX() - var2.getX();
      double var5 = var1.getZ() - var2.getZ();
      return Math.sqrt(var3 * var3 + var5 * var5);
   }
}
