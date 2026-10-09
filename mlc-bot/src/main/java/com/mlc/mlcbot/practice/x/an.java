// Extracted from the user-provided PracticeBot-Source; strategy logic retained.
package com.mlc.mlcbot.practice.x;

import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

final class an {
   private final as bF;

   an(as var1) {
      this.bF = var1;
   }

   boolean a(double var1, double var3, double var5, boolean var7, boolean var8, boolean var9, boolean var10) {
      if (!var8) {
         return false;
      } else if (!var7) {
         return true;
      } else {
         double var11 = var1 - var3;
         if (var11 < 1.25) {
            return false;
         } else if (var9 && var1 >= 3.25) {
            return true;
         } else {
            return var10 && var1 >= 4.25 && var5 >= 1.5 ? true : var1 >= 5.25 && var5 >= 2.0;
         }
      }
   }

   boolean a(bw var1, double var2, Player var4, Player var5, ae var6, m1 var7, long var8) {
      av var10 = this.bF.bZ();
      if (var1 == null) {
         return false;
      } else if (var1.dc() >= var5.getHealth()) {
         return true;
      } else {
         boolean var11 = this.f(var4, var5);
         boolean var12 = var1.dJ() || this.r(var5) || this.s(var5);
         boolean var13 = var10.c(var5, var7) && var7.mK > 0L && var8 - var7.mK <= 980L;
         if (var13 && var2 > 1.5 && !var12 && !var11) {
            return false;
         } else if (!var12 && !var11 && var2 > 1.5 && !this.a(var1, var7, var8)) {
            return false;
         } else {
            double var14 = var1.do_val();
            if (var1.dL()) {
               var14 += 9.5;
            }

            if (var1.dJ()) {
               var14 += 7.5;
            }

            if (var11) {
               var14 += 6.0;
            }

            if (var1.dI()) {
               var14 += 4.0;
            }

            if (var1.dH()) {
               var14 += 3.0;
            }

            if (var1.dC()) {
               var14 += 0.75;
            }

            if (!var1.dC()) {
               var14++;
            }

            if (var10.d(var4.getLocation(), var5.getLocation()) <= 3.25) {
               var14++;
            }

            if (var2 <= 0.0) {
               return true;
            } else if (var1.dI() && !var12 && !var11 && var2 >= var14 + 2.0) {
               return false;
            } else {
               double var16 = var1.dL() ? -2.5 : (!var1.dJ() && !var11 ? (var1.dI() ? -0.5 : (var1.dH() ? 0.0 : 4.0)) : -1.5);
               return var14 + var16 >= var2;
            }
         }
      }
   }

   boolean a(bw var1, m1 var2, long var3) {
      if (var1 == null || var2 == null) {
         return false;
      } else if (!var1.dH() && !var1.dL() && !var1.dI() && var3 >= var2.mA) {
         long var5 = Math.floorDiv(var3, 750L);
         long var7 = var1.dF().getX() * 31L + var1.dF().getY() * 17L + var1.dF().getZ() * 13L;
         return Math.floorMod(var5 + var7, 2L) == 0L;
      } else {
         return true;
      }
   }

   double a(Player var1, Player var2, ae var3, m1 var4, long var5) {
      av var7 = this.bF.bZ();
      double var8 = this.b(var1, var2, var3);
      if (var7.j(var4, var5)) {
         Block var10 = var4.oi.getBlock();
         var8 = Math.max(var8, this.a(var1, var2, var3, var10) + 1.5);
      }

      if (var5 < var4.mv && var4.mw != null) {
         Block var11 = var4.mw.getBlock();
         var8 = Math.max(var8, this.a(var1, var2, var3, var11) + 2.0);
      }

      if (var7.h(var3) && var7.d(var4, var3, var5) && var7.a(var1, Material.OBSIDIAN) && !var4.lz && !var7.a(var1, var2, var4, var3, var5)) {
         var8 = Math.max(var8, this.a(var1, var2, var3));
      }

      if (var7.h(var1, var2, var3, var4, var5)) {
         var8 = Math.max(var8, 18.0);
      }

      return var8;
   }

   double a(Player var1, Player var2, ae var3, Block var4) {
      av var5 = this.bF.bZ();
      if (var4 == null) {
         return -999.0;
      } else if (var4.getType() != Material.OBSIDIAN && var4.getType() != Material.BEDROCK) {
         return -999.0;
      } else if (var5.a(var4, var2, null, 0L) && !var5.d(var1, var4) && !var5.c(var4, var2)) {
         Location var6 = var4.getLocation().add(0.5, 1.0, 0.5);
         if (!(var1.getEyeLocation().distance(var6) > 4.5) && !(var1.getEyeLocation().distance(var6) < 0.8) && var5.g(var1.getEyeLocation(), var6)) {
            bg var7 = var5.h(var1, var2, var4);
            if (!var7.cS()) {
               return -999.0;
            } else {
               double var8 = var7.dc();
               double var10 = var7.dd();
               double var12 = var5.g(var1, var2, var4);
               if (var8 < var5.g(var12)) {
                  return -999.0;
               } else {
                  double var14 = var8 * var3.aI() * 2.0 - var10 * var3.aJ() * 2.0;
                  if (var12 <= 1.0) {
                     var14 += 5.0;
                  }

                  if (var8 >= var2.getHealth()) {
                     var14 += 150.0;
                  }

                  return var14;
               }
            }
         } else {
            return -999.0;
         }
      } else {
         return -999.0;
      }
   }

   double a(Player var1, Player var2, ae var3) {
      av var4 = this.bF.bZ();
      Location var5 = var2.getLocation();
      double var6 = var5.getY();
      double var8 = -999.0;

      for (int var10 = -3; var10 <= 3; var10++) {
         for (int var11 = -3; var11 <= 3; var11++) {
            for (int var12 = -3; var12 <= 2; var12++) {
               Block var13 = var5.getWorld().getBlockAt(var5.getBlockX() + var10, var5.getBlockY() + var12, var5.getBlockZ() + var11);
               if (var13.getType().isAir()
                  && var4.e(var1, var13)
                  && var4.a(var13, var1, var2, var3, true, false)
                  && var4.a(var1, var2, var3, var13, 0.75, false, false, false)) {
                  double var14 = var1.getEyeLocation().distance(var13.getLocation().add(0.5, 0.5, 0.5));
                  if (!(var14 > 4.5) && !(var14 < 0.8)) {
                     Block var16 = var13.getRelative(BlockFace.UP);
                     Block var17 = var16.getRelative(BlockFace.UP);
                     if (var16.getType().isAir() && var17.getType().isAir()) {
                        Location var18 = var13.getLocation().add(0.5, 1.0, 0.5);
                        bg var19 = var4.h(var1, var2, var13);
                        if (var19.cS()) {
                           double var20 = var19.dc();
                           double var22 = var19.dd();
                           double var24 = var4.g(var1, var2, var13);
                           if (!(var20 < var4.g(var24))) {
                              double var26 = 1.75;
                              double var28 = var20 * var3.aI() * 1.9 - var22 * var3.aJ() * 1.75 - var4.d(var13.getLocation(), var5) * 0.8 - var26;
                              if (this.r(var2) || this.s(var2)) {
                                 var28 += 2.0;
                              }

                              if (var4.d(var1.getLocation(), var5) <= 3.0) {
                                 var28++;
                              }

                              var8 = Math.max(var8, var28);
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      return var8;
   }

   boolean a(Player var1, Player var2, ae var3, m1 var4, long var5, Block var7, double var8) {
      av var10 = this.bF.bZ();
      if (var8 < 1.75) {
         return false;
      } else {
         boolean var11 = this.g(var1, var2);
         boolean var12 = this.r(var2) || this.s(var2) || var11 || this.a(var7, 1) >= 3;
         if (!var12) {
            return false;
         } else {
            double var13 = var10.d(var1.getLocation(), var2.getLocation());
            if (var11) {
               return var13 <= 7.0;
            } else if (!var10.a(var4, var3, var5) || var10.c(var4, var3, var5)) {
               return false;
            } else if (var10.e(var4, var5)) {
               return false;
            } else {
               return !var10.a(var1, Material.ENDER_PEARL) ? false : var13 >= 2.2 && var13 <= 7.0 && var5 >= var4.ok;
            }
         }
      }
   }

   int a(Block var1, int var2) {
      av var3 = this.bF.bZ();
      if (var1 == null) {
         return 0;
      } else {
         int var4 = 0;

         for (int var5 = -var2; var5 <= var2; var5++) {
            for (int var6 = -var2; var6 <= var2; var6++) {
               for (int var7 = -var2; var7 <= var2; var7++) {
                  Block var8 = var1.getRelative(var5, var6, var7);
                  Material var9 = var8.getType();
                  if (!var9.isAir() && !var3.i(var8) && !this.e(var9) && var9.isSolid()) {
                     var4++;
                  }
               }
            }
         }

         return var4;
      }
   }

   boolean e(Material var1) {
      return var1 == Material.OBSIDIAN
         || var1 == Material.BEDROCK
         || var1 == Material.CRYING_OBSIDIAN
         || var1 == Material.ANCIENT_DEBRIS
         || var1 == Material.RESPAWN_ANCHOR
         || var1 == Material.NETHERITE_BLOCK
         || var1 == Material.REINFORCED_DEEPSLATE;
   }

   boolean a(double var1, double var3, double var5, boolean var7) {
      av var8 = this.bF.bZ();
      double var9 = var7 ? 5.0 : 4.0;
      if (var1 >= var3 - var9) {
         return false;
      } else {
         return var1 > 9.0 ? false : var8.a(var1, var3, var5);
      }
   }

   double a(Location var1, Player var2) {
      return this.bF.bZ().a(var1, var2);
   }

   double b(Player var1, Player var2, ae var3) {
      av var4 = this.bF.bZ();
      Location var5 = var2.getLocation();
      int var6 = (int)Math.floor(var5.getY());
      double var7 = -999.0;
      Block var9 = var4.x(var2);
      if (var9 != null) {
         var7 = Math.max(var7, this.a(var1, var2, var3, var9) + 2.5);
      }

      Block var10 = var4.k(var1, var2);
      if (var10 != null) {
         var7 = Math.max(var7, this.a(var1, var2, var3, var10) + 2.0);
      }

      for (int var11 = -3; var11 <= 3; var11++) {
         for (int var12 = -3; var12 <= 3; var12++) {
            for (int var13 = var6 - 5; var13 <= var6; var13++) {
               Block var14 = var2.getWorld().getBlockAt(var5.getBlockX() + var11, var13, var5.getBlockZ() + var12);
               if ((var14.getType() == Material.OBSIDIAN || var14.getType() == Material.BEDROCK)
                  && var4.a(var14, var2, null, 0L)
                  && !var4.d(var1, var14)
                  && !var4.c(var14, var2)) {
                  Location var15 = var14.getLocation().add(0.5, 1.0, 0.5);
                  if (var4.f(var1, var2, var14) && !(var1.getEyeLocation().distance(var15) > 4.5) && var4.g(var1.getEyeLocation(), var15)) {
                     bg var16 = var4.h(var1, var2, var14);
                     if (var16.cS()) {
                        double var17 = var16.dc();
                        double var19 = var16.dd();
                        double var21 = var17 * var3.aI() * 2.0 - var19 * var3.aJ() * 2.0;
                        if (var17 >= var2.getHealth()) {
                           var21 += 150.0;
                        }

                        var7 = Math.max(var7, var21);
                     }
                  }
               }
            }
         }
      }

      return var7;
   }

   boolean r(Player var1) {
      Location var2 = var1.getLocation();
      Block var3 = var2.getBlock();
      Block var4 = var3.getRelative(BlockFace.DOWN);
      if (!var4.getType().isSolid()) {
         return false;
      } else {
         int var5 = 0;

         for (BlockFace var9 : new BlockFace[]{BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST}) {
            Block var10 = var3.getRelative(var9);
            if (var10.getType().isSolid()) {
               var5++;
            }
         }

         return var5 >= 3;
      }
   }

   boolean s(Player var1) {
      Location var2 = var1.getLocation();
      Block var3 = var2.getBlock();
      int var4 = 0;

      for (BlockFace var8 : new BlockFace[]{BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST}) {
         Block var9 = var3.getRelative(var8);
         Block var10 = var9.getRelative(BlockFace.UP);
         if (var9.getType().isSolid() || var10.getType().isSolid()) {
            var4++;
         }
      }

      return var4 >= 2 || var3.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getType().isSolid();
   }

   boolean f(Player var1, Player var2) {
      av var3 = this.bF.bZ();
      if (var1 == null || var2 == null || !var1.getWorld().equals(var2.getWorld())) {
         return false;
      } else if (this.r(var2) || this.s(var2)) {
         return true;
      } else {
         return !var3.h(var1.getEyeLocation(), var2.getEyeLocation()) ? true : this.t(var2) >= 3;
      }
   }

   int t(Player var1) {
      if (var1 == null) {
         return 0;
      } else {
         Block var2 = var1.getLocation().getBlock();
         int var3 = 0;

         for (BlockFace var7 : new BlockFace[]{BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST}) {
            Block var8 = var2.getRelative(var7);
            Block var9 = var8.getRelative(BlockFace.UP);
            if (this.f(var8.getType()) || this.f(var9.getType())) {
               var3++;
            }
         }

         if (this.f(var2.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getType())) {
            var3++;
         }

         return var3;
      }
   }

   boolean f(Material var1) {
      return var1.isSolid() && !var1.isAir();
   }

   boolean g(Material var1) {
      if (var1 == null || var1.isAir() || !var1.isSolid()) {
         return false;
      } else {
         return this.e(var1)
            ? false
            : var1 != Material.BARRIER
               && var1 != Material.COMMAND_BLOCK
               && var1 != Material.CHAIN_COMMAND_BLOCK
               && var1 != Material.REPEATING_COMMAND_BLOCK
               && var1 != Material.STRUCTURE_BLOCK
               && var1 != Material.JIGSAW
               && var1 != Material.END_PORTAL_FRAME;
      }
   }

   boolean g(Player var1, Player var2) {
      return this.h(var1, var2) != null;
   }

   boolean a(Player var1, Block var2) {
      if (var1 != null && var2 != null && this.g(var2.getType())) {
         Location var3 = var1.getLocation();
         return var3.getBlockX() == var2.getX() && (var3.getBlockY() == var2.getY() || var3.getBlockY() + 1 == var2.getY()) && var3.getBlockZ() == var2.getZ();
      } else {
         return false;
      }
   }

   boolean a(Player var1, Player var2, Block var3) {
      av var4 = this.bF.bZ();
      if (var1 != null && var2 != null && var3 != null) {
         Location var5 = var1.getEyeLocation();
         Location var6 = var2.getEyeLocation();
         Location var7 = var3.getLocation().add(0.5, 0.5, 0.5);
         double var8 = var5.distance(var6);
         double var10 = var5.distance(var7);
         if (var10 > Math.min(var8 + 0.35, 6.0)) {
            return false;
         } else if (!this.bF.a(var5, var7, var3)) {
            return false;
         } else {
            Vector var12 = var6.toVector().subtract(var5.toVector());
            Vector var13 = var7.toVector().subtract(var5.toVector());
            if (!(var12.lengthSquared() < 0.01) && !(var13.lengthSquared() < 0.01)) {
               double var14 = var12.normalize().dot(var13.normalize());
               if (var14 < 0.72) {
                  return false;
               } else {
                  Vector var16 = var5.toVector().subtract(var2.getLocation().toVector()).setY(0.0);
                  Vector var17 = var7.toVector().subtract(var2.getLocation().toVector()).setY(0.0);
                  if (var16.lengthSquared() > 0.01 && var17.lengthSquared() > 0.01) {
                     double var18 = var16.normalize().dot(var17.normalize());
                     if (var18 < 0.35) {
                        return false;
                     }
                  }

                  return true;
               }
            } else {
               return false;
            }
         }
      } else {
         return false;
      }
   }

   Block h(Player var1, Player var2) {
      av var3 = this.bF.bZ();
      if (var1 != null && var2 != null && var1.getWorld().equals(var2.getWorld())) {
         Location var4 = var2.getLocation();
         Block var5 = var4.getBlock();
         Block var6 = var5.getRelative(BlockFace.UP);
         if (this.a(var2, var5)) {
            return var5;
         } else if (this.a(var2, var6)) {
            return var6;
         } else {
            Location var7 = var1.getEyeLocation();
            Location var8 = var2.getEyeLocation();
            Vector var9 = var8.toVector().subtract(var7.toVector());
            double var10 = var9.length();
            if (var10 > 0.1) {
               RayTraceResult var12 = var1.getWorld().rayTraceBlocks(var7, var9.normalize(), Math.min(var10, 6.5), FluidCollisionMode.NEVER, true);
               if (var12 != null && var12.getHitBlock() != null && this.g(var12.getHitBlock().getType()) && this.a(var1, var2, var12.getHitBlock())) {
                  return var12.getHitBlock();
               }
            }

            Block var32 = null;
            double var13 = -999.0;

            for (BlockFace var18 : new BlockFace[]{BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST}) {
               for (int var19 = 0; var19 <= 1; var19++) {
                  Block var20 = var5.getRelative(var18).getRelative(0, var19, 0);
                  if (this.g(var20.getType()) && this.a(var1, var2, var20)) {
                     Location var21 = var20.getLocation().add(0.5, 0.5, 0.5);
                     double var22 = var1.getEyeLocation().distance(var21);
                     double var24 = var3.d(var21, var4);
                     Vector var26 = var1.getLocation().toVector().subtract(var4.toVector()).setY(0.0);
                     Vector var27 = var21.toVector().subtract(var4.toVector()).setY(0.0);
                     double var28 = 0.0;
                     if (var26.lengthSquared() > 0.01 && var27.lengthSquared() > 0.01) {
                        var28 = var26.normalize().dot(var27.normalize());
                     }

                     double var30 = 7.0 - var22 - var24 * 1.35 + var28 * 2.5;
                     if (var30 > var13) {
                        var13 = var30;
                        var32 = var20;
                     }
                  }
               }
            }

            return var13 > -4.0 ? var32 : null;
         }
      } else {
         return null;
      }
   }

   boolean a(Player var1, Player var2, bw var3) {
      if (var3 == null) {
         return false;
      } else {
         return this.g(var1, var2) && !var3.dJ() ? false : var3.dJ() || this.r(var2) || this.s(var2);
      }
   }
}
