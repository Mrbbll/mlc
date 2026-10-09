// Extracted from the user-provided PracticeBot-Source; strategy logic retained.
package com.mlc.mlcbot.practice.x;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public final class d1 {
   private static final int ie = 3;
   private static final long IF_DELAY = 250L;
   private final av ig;

   public d1(av var1) {
      this.ig = var1;
   }

   public void f(Player var1, m1 var2) {
      double var3 = var1.getHealth();
      if (var3 < var2.lb - 0.5) {
         var2.la = 0;
      } else {
         var2.la++;
      }

      var2.lb = var3;
   }

   public boolean ag(Player var1) {
      Location var2 = var1.getLocation();
      World var3 = var2.getWorld();
      if (var3 == null) {
         return var1.isOnGround();
      } else if (this.ig.ac(var1)) {
         return false;
      } else {
         double var4 = var1.getVelocity().getY();
         if (var4 < -0.4) {
            return false;
         } else {
            double var6 = var2.getY();
            double[][] var8 = new double[][]{{0.0, 0.0}, {0.3, 0.0}, {-0.3, 0.0}, {0.0, 0.3}, {0.0, -0.3}, {0.2, 0.2}, {-0.2, 0.2}, {0.2, -0.2}, {-0.2, -0.2}};

            for (double[] var12 : var8) {
               Location var13 = var2.clone().add(var12[0], -0.05, var12[1]);
               Block var14 = var13.getBlock();
               if (var14.getType().isSolid()) {
                  double var15 = var14.getY() + 1.0;
                  if (var6 >= var15 - 0.03 && var6 <= var15 + 0.3) {
                     return true;
                  }
               }
            }

            return var1.isOnGround();
         }
      }
   }

   public void a(Player var1, m1 var2, Vector var3, long var4, boolean var6) {
      double var7 = var3.length();
      double var9 = Math.sqrt(var3.getX() * var3.getX() + var3.getZ() * var3.getZ());
      if (var2.inKnockback) {
         if (var6 && var9 < 0.4) {
            var2.inKnockback = false;
            var2.kV = 0L;
            var2.kZ = false;
            var2.om = false;
            var2.kW = var3.clone();
            var2.ol = var7;
         } else {
            if (var4 - var2.kV > 2500L) {
               var2.inKnockback = false;
               var2.kV = 0L;
               var2.kZ = false;
               var2.om = false;
            }

            var2.kW = var3.clone();
            var2.ol = var7;
         }
      } else if (var6 && var4 - var2.oS < 250L) {
         var2.kW = var3.clone();
         var2.ol = var7;
      } else {
         boolean var11 = false;
         boolean var12 = var4 - var2.kX < 400L;
         if (var2.kW != null && !var12) {
            double var13 = var3.getY() - var2.kW.getY();
            double var15 = Math.sqrt(var2.kW.getX() * var2.kW.getX() + var2.kW.getZ() * var2.kW.getZ());
            double var17 = var9 - var15;
            if (var13 > 0.7) {
               var11 = true;
               var2.om = true;
            }

            if (var17 > 0.7) {
               var11 = true;
               var2.om = true;
            }
         }

         if ((var3.getY() > 0.9 || var7 > 1.3) && !var12) {
            var11 = true;
            var2.om = true;
         }

         if (var11) {
            var2.inKnockback = true;
            var2.kV = var4;
            var2.kU = 0;
            var2.airTicks = 0;
            var2.or = 0;
         }

         var2.kW = var3.clone();
         var2.ol = var7;
      }
   }
}
