// Extracted from the user-provided PracticeBot-Source; strategy logic retained.
package com.mlc.mlcbot.practice.x;

import static com.mlc.mlcbot.nms.EntityGroundState.isOnGround;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/** 寻找目标周围可用于水晶的黑曜石或基岩底座。 原源码：x/bm.java。 */
public final class CrystalBaseSelector {
   private static final double fK = 4.5;
   private static final double fL = 4.5;
   private static final double fM = 0.31;
   private static final double fN = 1.85;
   private static final double fO = 2.6;
   private static final long fP = 900L;
   private static final int fQ = 2;
   private static final int fR = 4;
   private final CpvpCombatController fS;

   public CrystalBaseSelector(CpvpCombatController var1) {
      this.fS = var1;
   }

   private boolean p(Player var1, Player var2) {
      if (var1 != null && var2 != null && isOnGround(var2)) {
         int var3 = (int)Math.floor(var1.getLocation().getY());
         int var4 = (int)Math.floor(var2.getLocation().getY());
         return Math.abs(var3 - var4) <= 1;
      } else {
         return false;
      }
   }

   private boolean o(Block var1) {
      if (var1 == null) {
         return false;
      } else {
         Material var2 = var1.getType();
         return var2.isAir()
            || var2 == Material.FIRE
            || var2 == Material.SOUL_FIRE
            || var2 == Material.SHORT_GRASS
            || var2 == Material.TALL_GRASS
            || var2 == Material.SNOW;
      }
   }

   public boolean b(Block var1, Player var2) {
      if (var1 != null && var2 != null) {
         Location var3 = var2.getLocation();
         Location var4 = var1.getLocation().add(0.5, 0.5, 0.5);
         double var5 = Math.sqrt(Math.pow(var4.getX() - var3.getX(), 2.0) + Math.pow(var4.getZ() - var3.getZ(), 2.0));
         if (var5 > 1.0) {
            return false;
         } else {
            int var7 = var1.getY();
            int var8 = (int)Math.floor(var3.getY());
            if (var7 == var8 - 1) {
               return true;
            } else if (var7 == var8) {
               return true;
            } else {
               int var9 = var8 + 1;
               return var7 >= var9 && var7 <= var9 + 2;
            }
         }
      } else {
         return false;
      }
   }

   public boolean w(Player var1) {
      return this.ao(var1) != null;
   }

   public Block x(Player var1) {
      if (var1 != null && var1.getWorld() != null) {
         Block var2 = this.ao(var1);
         if (var2 != null) {
            return var2;
         } else {
            Location var3 = var1.getLocation();
            World var4 = var3.getWorld();
            int var5 = (int)Math.floor(var3.getY());
            Integer var6 = this.fS.b(var1, 3);
            return this.a(var4, var3, var6, var5);
         }
      } else {
         return null;
      }
   }

   public Block y(Player var1) {
      if (var1 != null && var1.getWorld() != null) {
         Location var2 = var1.getLocation();
         World var3 = var2.getWorld();
         int var4 = (int)Math.floor(var2.getY());
         Integer var5 = this.fS.b(var1, 3);
         int var6 = var5 != null ? var5 - 1 : var4 - 1;
         int[] var7 = this.m(var2.getX());
         int[] var8 = this.m(var2.getZ());
         Block var9 = null;
         double var10 = Double.MAX_VALUE;

         for (int var15 : var7) {
            for (int var19 : var8) {
               Block var20 = var3.getBlockAt(var15, var6, var19);
               if (var20.getType().isSolid() && !this.fS.i(var20)) {
                  double var21 = var20.getX() + 0.5 - var2.getX();
                  double var23 = var20.getZ() + 0.5 - var2.getZ();
                  double var25 = var21 * var21 + var23 * var23;
                  if (var25 < var10) {
                     var10 = var25;
                     var9 = var20;
                  }
               }
            }
         }

         return var9;
      } else {
         return null;
      }
   }

   public boolean c(Block var1, Player var2) {
      if (var1 != null && var2 != null) {
         Location var3 = var2.getLocation();
         int var4 = var1.getY();
         int var5 = (int)Math.floor(var3.getY());
         if (var4 != var5 - 1) {
            return false;
         } else {
            int[] var6 = this.m(var3.getX());
            int[] var7 = this.m(var3.getZ());

            for (int var11 : var6) {
               for (int var15 : var7) {
                  if (var1.getX() == var11 && var1.getZ() == var15) {
                     return true;
                  }
               }
            }

            return false;
         }
      } else {
         return false;
      }
   }

   public Block k(Player var1, Player var2) {
      Block var3 = this.x(var2);
      if (var3 == null) {
         return null;
      } else {
         Block var4 = null;
         double var5 = -999.0;

         for (int var7 = -2; var7 <= 2; var7++) {
            for (int var8 = -2; var8 <= 2; var8++) {
               for (int var9 = -1; var9 <= 1; var9++) {
                  Block var10 = var3.getRelative(var7, var9, var8);
                  double var11 = this.j(var1, var2, var10);
                  if (!(var11 <= -900.0) && var11 > var5) {
                     var5 = var11;
                     var4 = var10;
                  }
               }
            }
         }

         return var4;
      }
   }

   public Block r(Player var1, Player var2) {
      return this.e(var1, var2, null, null, 0L);
   }

   public Block e(Player var1, Player var2, CpvpSettings var3, CpvpCombatState var4, long var5) {
      if (var1 != null && var2 != null && var2.getWorld() != null) {
         Location var7 = var2.getLocation();
         World var8 = var7.getWorld();
         double var9 = var7.getY();
         Block var11 = this.x(var2);
         Block var12 = null;
         double var13 = -999.0;
         List<Location> var15 = this.o(var1, var2, var4, var5);
         Set<Integer> var16 = this.n(var1, var2, var4, var5);
         Set<String> var17 = new HashSet<>();
         boolean var18 = var4 != null && this.fS.c(var2, var4) && var5 <= var4.mJ;
         boolean var19 = this.fS.l(var1, var2);
         boolean var20 = !var19 && this.fS.m(var1, var2);
         boolean var21 = var19 || var20;
         if (!var21 && this.p(var1, var2) && !var18) {
            return null;
         } else if (!var21 && this.fS.cx().b(var1, var2, var7)) {
            return null;
         } else {
            for (int var23 : var16) {
               if (var23 >= var8.getMinHeight() && var23 < var8.getMaxHeight()) {
                  for (Location var25 : var15) {
                     int var26 = var25.getBlockX();
                     int var27 = var25.getBlockZ();

                     for (int var28 = -2; var28 <= 2; var28++) {
                        for (int var29 = -2; var29 <= 2; var29++) {
                           Block var30 = var8.getBlockAt(var26 + var28, var23, var27 + var29);
                           String var31 = var30.getX() + ":" + var30.getY() + ":" + var30.getZ();
                           if (var17.add(var31)
                              && this.o(var30)
                              && (!this.a(var30, var7) || this.i(var2, var30) || this.a(var2, var4, var5, var30))
                              && !this.a(var30, var1.getLocation())
                              && this.fS.e(var1, var30)
                              && (var3 == null || this.fS.a(var30, var1, var2, var3, var21, false))
                              && (var3 == null || this.fS.a(var1, var2, var3, var30, var19 ? 0.01 : 1.0, var18, var21, false))) {
                              Block var32 = var30.getRelative(BlockFace.UP);
                              Block var33 = var32.getRelative(BlockFace.UP);
                              if (var32.getType().isAir() && var33.getType().isAir() && this.a(var7, var30)) {
                                 Location var34 = var30.getLocation().add(0.5, 1.0, 0.5);
                                 if ((var20 || this.fS.a(var30.getY() + 1.0, var9)) && !(this.d(var7, var34) > 2.6)) {
                                    double var35 = var1.getEyeLocation().distance(var30.getLocation().add(0.5, 0.5, 0.5));
                                    if (!(var35 > 4.5) && !(var35 < 0.8)) {
                                       CrystalSafetyEvaluation var37 = this.fS.h(var1, var2, var30);
                                       double var38 = var19 ? 0.01 : 1.0;
                                       if (var37.cS() && !(var37.dc() < var38)) {
                                          double var40 = var37.dc() * 3.0 - var37.dd() * 2.0 - this.d(var7, var34) + this.b(var2, var4, var5, var30);
                                          if (var19) {
                                             var40 += 4.0;
                                          } else if (var20) {
                                             var40 += 2.5;
                                          }

                                          if (var11 != null && var23 == var11.getY()) {
                                             var40++;
                                          }

                                          if (var4 != null
                                             && var4.lT != null
                                             && var4.lV != null
                                             && var4.lV.equals(var2.getUniqueId())
                                             && var5 > 0L
                                             && var5 - var4.lU <= 900L
                                             && var23 == var4.lT.getBlockY()) {
                                             var40 += 0.75;
                                          }

                                          if (var40 > var13) {
                                             var13 = var40;
                                             var12 = var30;
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

            return var12;
         }
      } else {
         return null;
      }
   }

   public Block c(Player var1, Player var2, CpvpSettings var3) {
      return this.k(var1, var2);
   }

   private int[] m(double var1) {
      int var3 = (int)Math.floor(var1 - 0.31);
      int var4 = (int)Math.floor(var1 + 0.31);
      return var3 == var4 ? new int[]{var3} : new int[]{var3, var4};
   }

   private Block ao(Player var1) {
      if (var1 != null && var1.getWorld() != null) {
         Location var2 = var1.getLocation();
         World var3 = var2.getWorld();
         int var4 = (int)Math.floor(var2.getY());
         Integer var5 = this.fS.b(var1, 3);
         int[] var6 = this.m(var2.getX());
         int[] var7 = this.m(var2.getZ());
         return this.a(var3, var2, var6, var7, this.a(var5, var4));
      } else {
         return null;
      }
   }

   private Block a(World var1, Location var2, int[] var3, int[] var4, int[] var5) {
      Block var6 = null;
      double var7 = Double.MAX_VALUE;

      for (int var12 : var5) {
         for (int var16 : var3) {
            for (int var20 : var4) {
               Block var21 = var1.getBlockAt(var16, var12, var20);
               if (this.j(var21.getType())) {
                  double var22 = var21.getX() + 0.5 - var2.getX();
                  double var24 = var21.getZ() + 0.5 - var2.getZ();
                  double var26 = var22 * var22 + var24 * var24;
                  if (var26 < var7) {
                     var7 = var26;
                     var6 = var21;
                  }
               }
            }
         }
      }

      return var6;
   }

   private Block a(World var1, Location var2, Integer var3, int var4) {
      Block var5 = null;
      double var6 = -999.0;
      int var9 = Math.max(var1.getMinHeight(), var4);
      int var10 = Math.min(var1.getMaxHeight() - 1, var4 + 1);

      for (int var11 = var9; var11 <= var10; var11++) {
         for (int var12 = -2; var12 <= 2; var12++) {
            for (int var13 = -2; var13 <= 2; var13++) {
               Block var14 = var1.getBlockAt(var2.getBlockX() + var12, var11, var2.getBlockZ() + var13);
               double var15 = this.a(var2, var3, var4, var14);
               if (var14.getY() == var4) {
                  var15 += 2.0;
               }

               if (var15 > var6) {
                  var6 = var15;
                  var5 = var14;
               }
            }
         }
      }

      return var5;
   }

   private double j(Player var1, Player var2, Block var3) {
      if (var1 == null || var2 == null || var3 == null) {
         return -999.0;
      } else if (!this.j(var3.getType())) {
         return -999.0;
      } else if (!this.a(var2.getLocation(), var3)) {
         return -999.0;
      } else if (this.c(var3, var2) || this.fS.d(var1, var3)) {
         return -999.0;
      } else if (!this.fS.k(var3)) {
         return -999.0;
      } else {
         Location var4 = var3.getLocation().add(0.5, 1.0, 0.5);
         if (!this.fS.a(var3.getY() + 1.0, var2.getLocation().getY())) {
            return -999.0;
         } else {
            double var5 = var1.getEyeLocation().distance(var4);
            if (!(var5 > 4.5) && !(var5 < 0.8)) {
               if (!this.fS.g(var1.getEyeLocation(), var4)) {
                  return -999.0;
               } else {
                  CrystalSafetyEvaluation var7 = this.fS.h(var1, var2, var3);
                  if (var7.cS() && !(var7.dc() < 0.5)) {
                     double var8 = this.d(var2.getLocation(), var4);
                     double var10 = var7.dc() * 4.0 - var7.dd() * 2.0 - var8;
                     if (var8 <= 1.5) {
                        var10 += 5.0;
                     }

                     return var10;
                  } else {
                     return -999.0;
                  }
               }
            } else {
               return -999.0;
            }
         }
      }
   }

   private double a(Location var1, Integer var2, int var3, Block var4) {
      if (var4 == null || !this.j(var4.getType())) {
         return -999.0;
      } else if (!this.a(var1, var4)) {
         return -999.0;
      } else if (!this.fS.a(var4.getY() + 1.0, var1.getY())) {
         return -999.0;
      } else {
         Location var5 = var4.getLocation().add(0.5, 1.0, 0.5);
         double var6 = this.d(var1, var5);
         if (var6 > 1.85) {
            return -999.0;
         } else {
            double var9 = 8.0 - var6 * 3.0 - Math.abs(var4.getY() - var3) * 1.5;
            if (var4.getY() == var3) {
               var9++;
            }

            if (var4.getY() == var3 - 2) {
               var9 += 0.5;
            }

            if (this.a(var4, var1)) {
               var9 += 0.75;
            }

            return var9;
         }
      }
   }

   private int[] a(Integer var1, int var2) {
      int var4 = var1 != null ? var1 - 1 : var2 - 1;
      return var2 == var4 ? new int[]{var2} : new int[]{var2, var4};
   }

   private boolean j(Material var1) {
      return var1 == Material.OBSIDIAN || var1 == Material.BEDROCK;
   }

   private Set<Integer> n(Player var1, Player var2, CpvpCombatState var3, long var4) {
      Set<Integer> var6 = new LinkedHashSet<>();
      Location var7 = var2.getLocation();
      World var8 = var7.getWorld();
      int var9 = (int)Math.floor(var7.getY());
      Integer var10 = this.fS.b(var2, 3);
      int var11 = (int)Math.floor(var1.getLocation().getY());
      boolean var12 = isOnGround(var2) && Math.abs(var9 - var11) <= 1;
      var6.add(var9);
      var6.add(var11);
      if (!var12) {
         for (int var16 : this.a(var10, var9)) {
            var6.add(var16);
         }

         var6.add(var9 - 2);
      }

      boolean var22 = this.fS.l(var1, var2);
      boolean var23 = !var22 && this.fS.m(var1, var2);
      if (var22 || var23) {
         double var24 = var23 ? 26.0 : 13.0;
         int var17 = Math.max(var8.getMinHeight(), Math.max((int)Math.ceil(var7.getY() - var24), var11 - 2));
         int var18 = Math.min(var8.getMaxHeight() - 1, Math.min((int)Math.floor(var7.getY() - 0.85), var11 + 4));

         for (int var19 = var17; var19 <= var18; var19++) {
            var6.add(var19);
         }
      }

      Block var25 = this.x(var2);
      if (var25 != null) {
         var6.add(var25.getY());
      }

      int var26 = Math.max(var8.getMinHeight(), var12 ? var9 : var9 - 4);
      int var27 = Math.min(var8.getMaxHeight() - 1, var12 ? var9 + 1 : var9 - 1);

      for (int var28 = var26; var28 <= var27; var28++) {
         for (int var29 = -2; var29 <= 2; var29++) {
            for (int var20 = -2; var20 <= 2; var20++) {
               Block var21 = var8.getBlockAt(var7.getBlockX() + var29, var28, var7.getBlockZ() + var20);
               if (this.j(var21.getType()) && this.a(var7, var10, var9, var21) > -900.0) {
                  var6.add(var28);
               }
            }
         }
      }

      if (var3 != null && var3.lT != null && var3.lV != null && var3.lV.equals(var2.getUniqueId()) && var4 > 0L && var4 - var3.lU <= 900L) {
         var6.add(var3.lT.getBlockY());
      }

      return var6;
   }

   private List<Location> o(Player var1, Player var2, CpvpCombatState var3, long var4) {
      List<Location> var6 = new ArrayList<>();
      var6.add(var2.getLocation());
      Block var7 = this.x(var2);
      if (var7 != null) {
         var6.add(var7.getLocation().add(0.5, 0.5, 0.5));
      }

      var6.add(var1.getLocation());
      if (var3 != null && var3.lT != null && var3.lV != null && var3.lV.equals(var2.getUniqueId()) && var4 > 0L && var4 - var3.lU <= 900L) {
         var6.add(var3.lT.clone().add(0.5, 0.5, 0.5));
      }

      return var6;
   }

   private boolean a(Location var1, Block var2) {
      return var1 != null && var2 != null && Math.floor(var1.getY()) >= var2.getY();
   }

   private boolean a(Block var1, Location var2) {
      if (var1 != null && var2 != null) {
         int[] var3 = this.m(var2.getX());
         int[] var4 = this.m(var2.getZ());

         for (int var8 : var3) {
            for (int var12 : var4) {
               if (var1.getX() == var8 && var1.getZ() == var12) {
                  return true;
               }
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private boolean i(Player var1, Block var2) {
      if (var1 != null && var2 != null && !isOnGround(var1)) {
         return this.fS.v(var1) < 0.45 ? false : Math.floor(var1.getLocation().getY()) > var2.getY();
      } else {
         return false;
      }
   }

   private boolean a(Player var1, CpvpCombatState var2, long var3, Block var5) {
      return var1 != null && var2 != null && var5 != null && this.fS.c(var1, var2) && var3 <= var2.mJ && Math.floor(var1.getLocation().getY()) > var5.getY();
   }

   private double b(Player var1, CpvpCombatState var2, long var3, Block var5) {
      if (var1 != null && var2 != null && var5 != null && this.fS.c(var1, var2) && var3 <= var2.mJ && var2.mO != null) {
         Location var6 = var1.getLocation();
         Vector var7 = var5.getLocation().add(0.5, 0.5, 0.5).toVector().subtract(var6.toVector()).setY(0.0);
         if (var7.lengthSquared() < 0.05) {
            return 0.75;
         } else {
            Vector var8 = var2.mO.clone().setY(0.0);
            if (var8.lengthSquared() < 0.01) {
               return 0.0;
            } else {
               var7.normalize();
               var8.normalize();
               Vector var9 = new Vector(-var8.getZ(), 0.0, var8.getX());
               double var10 = Math.abs(var7.dot(var9));
               double var12 = Math.abs(var7.dot(var8));
               if (var10 >= 0.62) {
                  return 6.0;
               } else {
                  return var12 >= 0.62 ? 3.0 : 1.0;
               }
            }
         }
      } else {
         return 0.0;
      }
   }

   private double d(Location var1, Location var2) {
      double var3 = var1.getX() - var2.getX();
      double var5 = var1.getZ() - var2.getZ();
      return Math.sqrt(var3 * var3 + var5 * var5);
   }
}
