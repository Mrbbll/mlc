// Extracted from the user-provided PracticeBot-Source; strategy logic retained.
package com.mlc.mlcbot.practice.x;

import static com.mlc.mlcbot.nms.EntityGroundState.isOnGround;

import com.mlc.mlcbot.practice.bridge.NPC;
import com.mlc.mlcbot.practice.bridge.FollowTrait;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/** CPVP 安全移动方向选择与障碍绕行。 原源码：x/c1.java。 */
final class CpvpAvoidanceController {
   private final CpvpTerrainController ic;

   CpvpAvoidanceController(CpvpTerrainController var1) {
      this.ic = var1;
   }

   boolean a(Player var1, Vector var2, double var3) {
      CpvpCombatController var5 = this.ic.bZ();
      if (var1 != null && var2 != null && !(var2.lengthSquared() < 0.01)) {
         Location var6 = var1.getLocation().clone().add(var2.clone().multiply(var3));
         Block var7 = var6.getBlock();
         Block var8 = var7.getRelative(BlockFace.UP);
         if (!var7.getType().isSolid() && !var8.getType().isSolid()) {
            Block var9 = var6.clone().add(0.0, -0.1, 0.0).getBlock();
            boolean var10 = var9.getType().isSolid() || var6.clone().add(0.0, -1.1, 0.0).getBlock().getType().isSolid();
            return !var10 ? false : !var5.h(var9.getType());
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   Vector d(Player var1, Vector var2) {
      CpvpCombatController var3 = this.ic.bZ();
      if (var1 != null && var2 != null && !(var2.lengthSquared() < 0.01)) {
         Vector var4 = var2.clone().setY(0).normalize();
         Vector var5 = null;
         double var6 = -999.0;
         double[] var8 = new double[]{55.0, -55.0, 80.0, -80.0, 35.0, -35.0, 110.0, -110.0};

         for (double var12 : var8) {
            Vector var14 = var3.b(var4.clone(), Math.toRadians(var12)).setY(0);
            if (!(var14.lengthSquared() < 0.01)) {
               var14.normalize();
               boolean var15 = this.a(var1, var14, 0.8);
               if (var15) {
                  boolean var16 = this.a(var1, var14, 1.25);
                  double var17 = 0.0;
                  var17 += var14.dot(var4) * 3.0;
                  var17 += var16 ? 5.0 : 1.5;
                  var17 += this.e(var1, var14) ? -4.0 : 2.0;
                  Location var19 = var1.getLocation().clone().add(var14.clone().multiply(0.8)).add(0.0, -0.1, 0.0);
                  if (!var3.h(var19.getBlock().getType())) {
                     var17 += 2.0;
                  }

                  if (var17 > var6) {
                     var6 = var17;
                     var5 = var14;
                  }
               }
            }
         }

         return var5;
      } else {
         return null;
      }
   }

   boolean a(NPC var1, Player var2, Player var3, Vector var4, CpvpCombatState var5, long var6) {
      CpvpCombatController var8 = this.ic.bZ();
      if (var2 != null && var3 != null && var4 != null && !(var4.lengthSquared() < 0.01)) {
         World var9 = var2.getWorld();
         Vector var10 = var4.clone().setY(0.0);
         if (var9 != null && var10.lengthSquared() > 0.01) {
            var10.normalize();
            boolean var11 = var8.h(var2.getEyeLocation(), var3.getEyeLocation())
               && !this.e(var2, var10)
               && !this.ic.a(var2, var10, var9)
               && !this.ic.b(var2, var10)
               && !this.c(var2, var10);
            if (var11) {
               var5.oQ = null;
               var5.oR = 0L;
               return false;
            }
         }

         FollowTrait var16 = (FollowTrait)var1.getTraitNullable(FollowTrait.class);
         if (var16 != null && var16.getFollowing() != null) {
            var16.follow(null);
         }

         var1.getNavigator().cancelNavigation();
         Vector var12;
         if (var5.oQ != null && var6 < var5.oR) {
            var12 = var5.oQ.clone();
         } else {
            var12 = this.d(var2, var4);
            if (var12 == null || var12.lengthSquared() < 0.01) {
               var12 = var8.n(var2, var3);
            }

            if (var12 == null || var12.lengthSquared() < 0.01) {
               var12 = new Vector(-var4.getZ(), 0.0, var4.getX());
               if (var12.lengthSquared() < 0.01) {
                  return false;
               }

               var12.normalize();
               Location var13 = var2.getLocation().clone().add(var12.clone().multiply(1.0));
               Block var14 = var13.getBlock();
               Block var15 = var13.clone().add(0.0, -0.1, 0.0).getBlock();
               if (var14.getType().isSolid() || var8.h(var15.getType())) {
                  var12.multiply(-1);
               }
            }

            var5.oQ = var12.clone();
            var5.oR = var6 + 250L;
         }

         var12.setY(0);
         if (var12.lengthSquared() < 0.01) {
            return false;
         } else {
            var12.normalize();
            if (var6 - var5.oS < 110L) {
               return true;
            } else {
               Vector var17 = var2.getVelocity();
               var17.setX(var17.getX() * 0.55 + var12.getX() * 0.19);
               var17.setZ(var17.getZ() * 0.55 + var12.getZ() * 0.19);
               var2.setVelocity(var17);
               var5.kJ = var6;
               var5.oS = var6;
               return true;
            }
         }
      } else {
         return false;
      }
   }

   boolean c(Player var1, Vector var2) {
      if (var2 != null && !(var2.lengthSquared() < 0.01)) {
         Location var3 = var1.getLocation();
         World var4 = var3.getWorld();
         if (var4 == null) {
            return false;
         } else {
            Vector var5 = var2.clone().setY(0).normalize();
            Location var6 = var3.clone().add(var5.multiply(0.5));
            Block var7 = var6.getBlock();
            Block var8 = var7.getRelative(BlockFace.UP);
            return var7.getType().isSolid() && var8.getType().isSolid();
         }
      } else {
         return false;
      }
   }

   boolean e(Player var1, Vector var2) {
      if (var2 != null && !(var2.lengthSquared() < 0.01)) {
         Location var3 = var1.getLocation();
         Vector var4 = var2.clone().setY(0).normalize();
         Location var5 = var3.clone().add(var4.multiply(1.0));
         Block var6 = var5.getBlock();
         Block var7 = var6.getRelative(BlockFace.DOWN);
         Block var8 = var7.getRelative(BlockFace.DOWN);
         return !var6.getType().isSolid() && !var7.getType().isSolid() && !var8.getType().isSolid();
      } else {
         return false;
      }
   }

   boolean a(Player var1, Player var2, CpvpCombatState var3, double var4) {
      CpvpCombatController var6 = this.ic.bZ();
      if (!(var4 < 4.0) && !(var4 > 10.0)) {
         return !var6.h(var1.getEyeLocation(), var2.getEyeLocation()) ? true : var3.le > 600L;
      } else {
         return false;
      }
   }

   boolean as(Player var1) {
      CpvpCombatController var2 = this.ic.bZ();
      Location var3 = var1.getLocation();
      World var4 = var3.getWorld();
      if (var4 == null) {
         return isOnGround(var1);
      } else if (var2.ac(var1)) {
         return false;
      } else {
         double[][] var5 = new double[][]{{0.0, 0.0}, {0.3, 0.0}, {-0.3, 0.0}, {0.0, 0.3}, {0.0, -0.3}, {0.2, 0.2}, {-0.2, 0.2}, {0.2, -0.2}, {-0.2, -0.2}};

         for (double[] var9 : var5) {
            Location var10 = var3.clone().add(var9[0], -0.1, var9[1]);
            Block var11 = var10.getBlock();
            if (var11.getType().isSolid()) {
               double var12 = var11.getY() + 1.0;
               if (var3.getY() >= var12 - 0.1 && var3.getY() <= var12 + 0.5) {
                  return true;
               }
            }
         }

         return isOnGround(var1);
      }
   }

   boolean af(Player var1) {
      CpvpCombatController var2 = this.ic.bZ();
      Location var3 = var1.getLocation();
      Block var4 = var3.clone().add(0.0, -0.1, 0.0).getBlock();
      Block var5 = var3.clone().add(0.0, -1.0, 0.0).getBlock();
      return var2.i(var4.getType()) || var2.i(var5.getType());
   }

   boolean b(Player var1, Player var2, double var3) {
      return !this.af(var1) ? false : !(var3 > 5.0);
   }
}
