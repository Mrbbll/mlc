// Extracted from the user-provided PracticeBot-Source; strategy logic retained.
package com.mlc.mlcbot.practice.x;

import java.util.HashSet;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/** 扫描附近黑曜石、水晶和战斗障碍。 原源码：x/be.java。 */
final class CrystalEnvironmentScanner {
   private final CrystalCombatController ey;

   CrystalEnvironmentScanner(CrystalCombatController var1) {
      this.ey = var1;
   }

   int a(Location var1, int var2, int var3) {
      if (var1 != null && var1.getWorld() != null) {
         int var4 = 0;
         int var5 = var1.getBlockX();
         int var6 = var1.getBlockZ();
         World var7 = var1.getWorld();

         for (int var8 = -var2; var8 <= var2; var8++) {
            for (int var9 = -var2; var9 <= var2; var9++) {
               Block var10 = var7.getBlockAt(var5 + var8, var3, var6 + var9);
               if (var10.getType() == Material.OBSIDIAN) {
                  var4++;
               }

               Block var11 = var7.getBlockAt(var5 + var8, var3 - 1, var6 + var9);
               if (var11.getType() == Material.OBSIDIAN) {
                  var4++;
               }
            }
         }

         return var4;
      } else {
         return 0;
      }
   }

   int a(Location var1, int var2, int var3, int var4) {
      if (var1 != null && var1.getWorld() != null) {
         int var5 = 0;
         int var6 = var1.getBlockX();
         int var7 = var1.getBlockZ();
         World var8 = var1.getWorld();

         for (int var9 = var3; var9 <= var4; var9++) {
            for (int var10 = -var2; var10 <= var2; var10++) {
               for (int var11 = -var2; var11 <= var2; var11++) {
                  Block var12 = var8.getBlockAt(var6 + var10, var9, var7 + var11);
                  if (var12.getType() == Material.OBSIDIAN) {
                     var5++;
                  }
               }
            }
         }

         return var5;
      } else {
         return 0;
      }
   }

   int b(Location var1, double var2) {
      return this.a(var1, var2, null, null, 0L);
   }

   int a(Location var1, double var2, CpvpCombatState var4, CpvpSettings var5, long var6) {
      if (var1 != null && var1.getWorld() != null) {
         int var8 = 0;

         for (EnderCrystal var10 : this.ey.bZ().a(var1, var2)) {
            if (!this.a(var10, var4, var5, var6)) {
               var8++;
            }
         }

         return var8;
      } else {
         return 0;
      }
   }

   int b(Player var1, double var2) {
      return var1 == null ? 0 : this.b(var1.getLocation(), var2);
   }

   EnderCrystal b(Player var1, CpvpSettings var2) {
      CpvpCombatController var3 = this.ey.bZ();
      if (var1 == null) {
         return null;
      } else {
         Location var4 = var1.getLocation();
         double var5 = var4.getY();

         for (EnderCrystal var8 : var3.a(var1, 6.0)) {
            if (!(Math.abs(var8.getLocation().getX() - var4.getX()) > 4.0) && !(Math.abs(var8.getLocation().getZ() - var4.getZ()) > 4.0)) {
               Location var9 = var8.getLocation();
               double var10 = var9.getY();
               if (var10 > var5 + 0.5) {
                  double var12 = var3.d(var4, var9);
                  if (var12 < 3.0) {
                     double var14 = var1.getEyeLocation().distance(var9);
                     if (var14 <= 4.5 && (var2 == null || var3.a(var1, var9, var2))) {
                        return var8;
                     }
                  }
               }
            }
         }

         return null;
      }
   }

   boolean a(Entity var1, CpvpCombatState var2, CpvpSettings var3, long var4) {
      if (var1 != null && var2 != null && var3 != null) {
         if (var3.aF() != CpvpDifficulty.PRO) {
            return false;
         } else {
            return var2.lj != null && var2.lj.equals(var1.getUniqueId()) ? var4 >= var2.kE && var4 - var2.kE <= 125L : false;
         }
      } else {
         return false;
      }
   }

   boolean b(Player var1, Player var2, CpvpSettings var3, CpvpCombatState var4, EnderCrystal var5, long var6) {
      CpvpCombatController var8 = this.ey.bZ();
      if (var1 == null || var2 == null || var3 == null || var4 == null || var5 == null) {
         return false;
      } else if (!var5.isValid() || var5.isDead()) {
         return false;
      } else if (this.a(var5, var4, var3, var6)) {
         return false;
      } else if (!var1.getWorld().equals(var5.getWorld()) || !var2.getWorld().equals(var5.getWorld())) {
         return false;
      } else if (this.ey.a(var1, var2, var3, var5, var4, var6)) {
         return true;
      } else {
         Location var9 = var5.getLocation();
         double var10 = var1.getEyeLocation().distance(var9);
         if (var10 > 5.15) {
            return false;
         } else if (!var8.a(var1, var9, var3)) {
            return false;
         } else if (!var8.g(var1.getEyeLocation(), var9)) {
            return false;
         } else {
            double var12 = var8.b(var9, var2);
            double var14 = var8.b(var9, var1);
            Block var16 = var9.clone().add(0.0, -1.0, 0.0).getBlock();
            double var17 = var8.l(var1, var2) && var8.a(var1, var2, var16, var4, var6) ? 0.05 : 0.75;
            return var12 >= var17 && var8.a(var14, var1.getHealth() + Math.max(0.0, var1.getAbsorptionAmount()), var12);
         }
      }
   }

   int a(Location var1, double var2, Player var4, Player var5, CpvpCombatState var6, CpvpSettings var7, long var8) {
      if (var1 != null && var1.getWorld() != null) {
         int var10 = 0;

         for (EnderCrystal var12 : this.ey.bZ().a(var1, var2)) {
            if (this.b(var4, var5, var7, var6, var12, var8)) {
               var10++;
            }
         }

         return var10;
      } else {
         return 0;
      }
   }

   boolean a(Player var1, Player var2, CpvpCombatState var3, CpvpSettings var4, long var5) {
      HashSet var7 = new HashSet();

      for (EnderCrystal var9 : this.ey.bZ().a(var1, 3.0)) {
         if (this.b(var1, var2, var4, var3, var9, var5)) {
            var7.add(var9.getUniqueId());
         }
      }

      for (EnderCrystal var11 : this.ey.bZ().a(var2, 3.0)) {
         if (this.b(var1, var2, var4, var3, var11, var5)) {
            var7.add(var11.getUniqueId());
         }
      }

      return var7.size() >= 3;
   }

   boolean j(Player var1, Player var2, CpvpSettings var3, CpvpCombatState var4, long var5) {
      return this.ey.y(var1, var2, var3, var4, var5) || this.ey.h(var1, var2, var3, var4, var5);
   }

   EnderCrystal aa(Player var1) {
      CpvpCombatController var2 = this.ey.bZ();
      Location var3 = var1.getLocation();
      double var4 = var3.getY();

      for (EnderCrystal var7 : var2.a(var1, 5.0)) {
         Location var8 = var7.getLocation();
         double var9 = var8.getY();
         double var11 = var2.d(var3, var8);
         double var13 = var9 - var4;
         if (var11 < 3.0 && var13 >= -0.5 && var13 <= 1.5) {
            double var15 = var2.b(var8, var1);
            if (var15 > var1.getHealth() * 0.4 || var15 > 8.0) {
               return var7;
            }
         }
      }

      return null;
   }

   EnderCrystal ak(Player var1, Player var2, CpvpSettings var3, CpvpCombatState var4, long var5) {
      CpvpCombatController var7 = this.ey.bZ();
      double var8 = var2.getLocation().getY();
      if (var4.mp != null) {
         Entity var10 = Bukkit.getEntity(var4.mp);
         if (var10 != null && var10.isValid() && !var10.isDead() && var10 instanceof EnderCrystal var11) {
            Location var12 = var11.getLocation();
            double var13 = var1.getEyeLocation().distance(var12);
            if (!(var13 > 4.5) && var7.a(var1, var12, var3) && var7.g(var1.getEyeLocation(), var12)) {
               Block var15 = var12.clone().add(0.0, -1.0, 0.0).getBlock();
               double var16 = var15.getY() + 1.0;
               if (!var7.a(var1, var2, var15, var4, var5) && !var7.a(var16, var8)) {
                  var7.i(var4);
               } else {
                  double var18 = var7.b(var12, var2);
                  double var20 = var7.b(var12, var1);
                  if (var7.a(var20, var1.getHealth() + Math.max(0.0, var1.getAbsorptionAmount()), var18) && this.ey.a(var1, var12, var15)) {
                     return var11;
                  }

                  var7.i(var4);
               }
            } else {
               var7.i(var4);
            }
         } else {
            var7.i(var4);
         }
      }

      EnderCrystal var27 = null;
      double var28 = -999.0;

      for (EnderCrystal var14 : var7.a(var1, 6.0)) {
         Location var30 = var14.getLocation();
         double var31 = var1.getEyeLocation().distance(var30);
         if (!(var31 > 4.5) && var7.a(var1, var30, var3) && var7.g(var1.getEyeLocation(), var30)) {
            Block var32 = var30.clone().add(0.0, -1.0, 0.0).getBlock();
            double var19 = var32.getY() + 1.0;
            if ((var7.a(var1, var2, var32, var4, var5) || var7.a(var19, var8)) && this.ey.a(var1, var30, var32)) {
               double var21 = var7.b(var30, var2);
               double var23 = var7.b(var30, var1);
               if (var7.a(var23, var1.getHealth() + Math.max(0.0, var1.getAbsorptionAmount()), var21) && !(var21 < 1.0)) {
                  double var25 = var21 * 2.0 - var23 * var3.aJ() * 1.5;
                  if (var21 >= var2.getHealth()) {
                     var25 += 200.0;
                  }

                  if (var25 > var28) {
                     var28 = var25;
                     var27 = var14;
                  }
               }
            }
         }
      }

      if (var27 != null && var28 > 1.5) {
         var4.mp = var27.getUniqueId();
         var4.ms = var5;
         return var27;
      } else {
         return null;
      }
   }
}
