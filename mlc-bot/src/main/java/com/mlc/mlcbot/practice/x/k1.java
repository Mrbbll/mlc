// Extracted from the user-provided PracticeBot-Source; strategy logic retained.
package com.mlc.mlcbot.practice.x;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public final class k1 {
   private static final int kf = 8192;
   private static final int kg = 4096;
   private final Map<l1, Boolean> kh = new HashMap<>();
   private final Map<l0, Boolean> ki = new HashMap<>();
   private int kj = Integer.MIN_VALUE;
   private int kk = Integer.MIN_VALUE;

   public boolean a(Player var1, Location var2, ae var3) {
      if (var1 != null && var2 != null && var3 != null) {
         double var4 = var3.aU();
         if (var4 >= 359.99) {
            return true;
         } else if (var4 <= 0.0) {
            return false;
         } else {
            Location var6 = var1.getEyeLocation();
            if (var6.getWorld() != null && var2.getWorld() != null && var6.getWorld().equals(var2.getWorld())) {
               Vector var7 = var2.toVector().subtract(var6.toVector());
               if (var7.lengthSquared() < 1.0E-4) {
                  return true;
               } else {
                  Vector var8 = var6.getDirection();
                  if (var8.lengthSquared() < 1.0E-4) {
                     return true;
                  } else {
                     double var9 = var8.normalize().dot(var7.normalize());
                     var9 = Math.max(-1.0, Math.min(1.0, var9));
                     double var11 = Math.toDegrees(Math.acos(var9));
                     return var11 <= var4 * 0.5 + 1.0E-4;
                  }
               }
            } else {
               return false;
            }
         }
      } else {
         return false;
      }
   }

   public boolean a(Player var1, Block var2, ae var3) {
      return var2 != null && this.a(var1, var2.getLocation().add(0.5, 0.5, 0.5), var3);
   }

   public boolean g(Location var1, Location var2) {
      if (this.h(var1, var2.clone().add(0.0, 0.5, 0.0))) {
         return true;
      } else {
         double[][] var3 = new double[][]{{0.3, 0.5, 0.0}, {-0.3, 0.5, 0.0}, {0.0, 0.5, 0.3}, {0.0, 0.5, -0.3}, {0.0, 0.2, 0.0}, {0.0, 0.8, 0.0}};
         int var4 = 0;

         for (double[] var8 : var3) {
            Location var9 = var2.clone().add(var8[0], var8[1], var8[2]);
            if (this.h(var1, var9)) {
               if (++var4 >= 2) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   public boolean h(Block var1) {
      if (var1 != null && var1.getWorld() != null) {
         this.er();
         l0 var2 = new l0(var1.getWorld().getUID(), var1.getX(), var1.getY(), var1.getZ());
         Boolean var3 = this.ki.get(var2);
         if (var3 != null) {
            return var3;
         } else {
            Location var4 = var1.getLocation().add(0.5, 0.5, 0.5);
            boolean var5 = true;

            for (Entity var7 : var1.getWorld().getNearbyEntities(var4, 0.5, 0.5, 0.5)) {
               if (var7 instanceof Player || var7 instanceof EnderCrystal) {
                  var5 = false;
                  break;
               }
            }

            if (this.ki.size() < 4096) {
               this.ki.put(var2, var5);
            }

            return var5;
         }
      } else {
         return false;
      }
   }

   public double d(Location var1, Location var2) {
      double var3 = var1.getX() - var2.getX();
      double var5 = var1.getZ() - var2.getZ();
      return Math.sqrt(var3 * var3 + var5 * var5);
   }

   public boolean h(Location var1, Location var2) {
      if (var1 != null && var2 != null && var1.getWorld() != null && var1.getWorld() == var2.getWorld()) {
         Vector var3 = var2.toVector().subtract(var1.toVector());
         double var4 = var3.length();
         if (var4 < 0.1) {
            return true;
         } else {
            this.eq();
            l1 var6 = l1.c(var1, var2, var4);
            Boolean var7 = this.kh.get(var6);
            if (var7 != null) {
               return var7;
            } else {
               RayTraceResult var8 = var1.getWorld().rayTraceBlocks(var1, var3.normalize(), var4, FluidCollisionMode.NEVER, true);
               boolean var9 = var8 == null;
               this.a(var6, var9);
               return var9;
            }
         }
      } else {
         return false;
      }
   }

   public boolean a(Player var1, Block var2, double var3, BlockFace[] var5, k0 var6) {
      for (BlockFace var10 : var5) {
         Block var11 = var2.getRelative(var10);
         if (var11.getType().isSolid() && !var6.i(var11)) {
            Location var12 = var2.getLocation().add(0.5 + var10.getModX() * 0.5, 0.5 + var10.getModY() * 0.5, 0.5 + var10.getModZ() * 0.5);
            if (var1.getEyeLocation().distance(var12) <= var3 && this.a(var1.getEyeLocation(), var12, var11)) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean a(Location var1, Block var2, double var3) {
      Location var5 = var2.getLocation().add(0.5, 0.5, 0.5);
      double[][] var6 = new double[][]{
         {0.0, 0.0, 0.0}, {0.28, 0.0, 0.0}, {-0.28, 0.0, 0.0}, {0.0, 0.0, 0.28}, {0.0, 0.0, -0.28}, {0.0, 0.28, 0.0}, {0.0, -0.28, 0.0}
      };

      for (double[] var10 : var6) {
         Location var11 = var5.clone().add(var10[0], var10[1], var10[2]);
         if (var1.distance(var11) <= var3 && this.a(var1, var11, var2)) {
            return true;
         }
      }

      return false;
   }

   public boolean a(Location var1, Location var2, Block var3) {
      if (var1 != null && var2 != null && var3 != null && var1.getWorld() != null && var1.getWorld() == var2.getWorld()) {
         Vector var4 = var2.toVector().subtract(var1.toVector());
         double var5 = var4.length();
         if (var5 < 0.1) {
            return true;
         } else {
            this.eq();
            l1 var7 = l1.a(var1, var2, var5 + 0.02, var3, null, true);
            Boolean var8 = this.kh.get(var7);
            if (var8 != null) {
               return var8;
            } else {
               RayTraceResult var9 = var1.getWorld().rayTraceBlocks(var1, var4.normalize(), var5 + 0.02, FluidCollisionMode.NEVER, true);
               boolean var10;
               if (var9 == null) {
                  var10 = true;
               } else {
                  Block var11 = var9.getHitBlock();
                  var10 = var11 != null
                     && var11.getWorld().equals(var3.getWorld())
                     && var11.getX() == var3.getX()
                     && var11.getY() == var3.getY()
                     && var11.getZ() == var3.getZ();
               }

               this.a(var7, var10);
               return var10;
            }
         }
      } else {
         return false;
      }
   }

   private void eq() {
      int var1 = Bukkit.getCurrentTick();
      if (var1 != this.kj) {
         this.kj = var1;
         this.kh.clear();
      }
   }

   private void er() {
      int var1 = Bukkit.getCurrentTick();
      if (var1 != this.kk) {
         this.kk = var1;
         this.ki.clear();
      }
   }

   private void a(l1 var1, boolean var2) {
      if (this.kh.size() < 8192) {
         this.kh.put(var1, var2);
      }
   }

   static int f(double var0) {
      return (int)Math.round(var0 * 1000.0);
   }
}
