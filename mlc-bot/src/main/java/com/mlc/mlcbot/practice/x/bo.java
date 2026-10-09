// Extracted from the user-provided PracticeBot-Source; strategy logic retained.
package com.mlc.mlcbot.practice.x;

import com.mlc.mlcbot.practice.PracticeBotPlugin;
import java.util.Random;
import com.mlc.mlcbot.practice.bridge.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public final class bo {
   private static final double gf = 4.5;
   private static final double gg = 4.5;
   private static final double gh = 0.45;
   private static final double gi = 12.0;
   private static final double gj = 13.0;
   private static final double gk = 26.0;
   private static final double gl = 2.85;
   private static final long gm = 50L;
   private static final long gn = 30L;
   private static final long go = 420L;
   private static final long gp = 980L;
   private static final int gq = 3;
   private static final long gr = 600L;
   private static final int gs = 2;
   private static final int gt = 2;
   private static final long gu = 50L;
   private static final double gv = 3.0;
   private static final double gw = 15.0;
   private static final double gx = 2.35;
   private static final double gy = 18.0;
   private static final double gz = 4.75;
   private static final long gA = 2500L;
   private static final long gB = 200L;
   private static final long gC = 700L;
   private static final int gD = 3;
   private static final double gE = 3.0;
   private static final int gF = 1;
   private static final double gG = 3.0;
   private static final int gH = 4;
   private static final int gI = 3;
   private final av gJ;
   private final PracticeBotPlugin gK;
   private final Random gL;
   private final bn gM;
   private final bv gN;
   private final g1 gO;

   public bo(av var1) {
      this.gJ = var1;
      this.gK = var1.ca();
      this.gL = var1.cb();
      this.gM = var1.cs();
      this.gN = var1.ct();
      this.gO = var1.cq();
   }

   private int o(boolean var1) {
      if (!this.gJ.cI()) {
         return 4;
      } else {
         int var2 = this.gJ.L();
         if (var2 >= 150) {
            return var1 ? 3 : 2;
         } else if (var2 >= 100) {
            return 3;
         } else {
            return var1 ? 4 : 3;
         }
      }
   }

   private int p(boolean var1) {
      if (!this.gJ.cI()) {
         return Integer.MAX_VALUE;
      } else {
         int var2 = this.gJ.L();
         if (var2 >= 150) {
            return var1 ? 28 : 14;
         } else if (var2 >= 100) {
            return var1 ? 36 : 18;
         } else {
            return var1 ? 48 : 24;
         }
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

   public boolean b(Player var1, Player var2, m1 var3) {
      if (var1 == null || var2 == null || var3 == null) {
         return false;
      } else {
         return var2.isOnGround() ? false : var3.oC || var3.mk || this.gJ.l(var1, var2);
      }
   }

   public boolean a(Block var1, Player var2, Player var3) {
      if (var1 != null && var2 != null && var3 != null) {
         Location var4 = var2.getLocation();
         Location var5 = var3.getLocation();
         int var6 = (int)Math.floor(var5.getY());
         int var7 = (int)Math.floor(var4.getY());
         int var8 = var1.getY();
         if (var8 == var6 || var8 == var6 - 1) {
            int var9 = this.gJ.a(var5, 1, var6);
            if (var9 >= 3) {
               return false;
            }
         }

         Location var10 = var1.getLocation().add(0.5, 0.5, 0.5);
         return !this.gJ.a(var10, var4, 1.0) || this.gJ.a(var4, 1, var7) < 2;
      } else {
         return false;
      }
   }

   private boolean j(Player var1, Block var2) {
      if (var1 != null && var2 != null && var2.getWorld() != null) {
         Location var3 = var1.getLocation();
         if (!var2.getWorld().equals(var3.getWorld())) {
            return true;
         } else {
            double var4 = var2.getX() + 0.5 - var3.getX();
            double var6 = var2.getZ() + 0.5 - var3.getZ();
            int var8 = var2.getY();
            int var9 = (int)Math.floor(var3.getY());
            if (var4 * var4 + var6 * var6 <= 0.81 && var8 >= var9 - 1 && var8 <= var9 + 1) {
               return true;
            } else {
               Location var10 = var2.getLocation().add(0.5, 1.0, 0.5);
               double var11 = this.gJ.d(var3, var10);
               double var13 = var10.getY() - var3.getY();
               return var11 < 1.25 && var13 > -0.75 && var13 < 2.05;
            }
         }
      } else {
         return true;
      }
   }

   public int m(ae var1) {
      return var1 != null && this.gJ.j(var1) ? 2 : 3;
   }

   public boolean a(Block var1, ae var2) {
      return var1 == null ? false : this.gJ.b(var1, 1, 1) < this.m(var2);
   }

   private boolean s(Player var1, Player var2) {
      if (var1 != null && var2 != null && var2.isOnGround()) {
         int var3 = (int)Math.floor(var1.getLocation().getY());
         int var4 = (int)Math.floor(var2.getLocation().getY());
         return Math.abs(var3 - var4) <= 1;
      } else {
         return false;
      }
   }

   private boolean a(Player var1, Player var2, Block var3, Location var4) {
      if (var1 != null && var2 != null && var3 != null && var4 != null && var3.getWorld() != null) {
         if (!var3.getWorld().equals(var2.getWorld()) || !var3.getWorld().equals(var4.getWorld())) {
            return false;
         } else if (var3.getType() != Material.OBSIDIAN && var3.getType() != Material.BEDROCK) {
            return false;
         } else {
            int var5 = (int)Math.floor(var1.getLocation().getY());
            int var6 = (int)Math.floor(var2.getLocation().getY());
            int var7 = Math.min(var5, var6);
            int var8 = Math.max(var5, var6) + 1;
            if (var3.getY() >= var7 && var3.getY() <= var8) {
               Location var9 = var3.getLocation().add(0.5, 1.0, 0.5);
               double var10 = this.gJ.d(var9, var2.getLocation());
               if (var10 < 0.65) {
                  return false;
               } else if (this.gJ.d(var9, var4) > 2.85 && var10 > 2.85) {
                  return false;
               } else if (!this.gJ.a(var3.getY() + 1.0, var2.getLocation().getY())) {
                  return false;
               } else {
                  Block var12 = var3.getRelative(BlockFace.UP);
                  Block var13 = var12.getRelative(BlockFace.UP);
                  return var12.getType().isAir() && var13.getType().isAir();
               }
            } else {
               return false;
            }
         }
      } else {
         return false;
      }
   }

   private int a(Player var1, Player var2, Location var3, int var4) {
      if (this.s(var1, var2) && var3 != null && var3.getWorld() != null) {
         if (var1.getWorld().equals(var2.getWorld()) && var1.getWorld().equals(var3.getWorld())) {
            World var5 = var3.getWorld();
            Location var6 = var2.getLocation();
            int var7 = (int)Math.floor(var1.getLocation().getY());
            int var8 = (int)Math.floor(var6.getY());
            int var9 = Math.max(var5.getMinHeight(), Math.min(var7, var8));
            int var10 = Math.min(var5.getMaxHeight() - 1, Math.max(var7, var8) + 1);
            int var11 = Math.min(var6.getBlockX(), var3.getBlockX()) - 3;
            int var12 = Math.max(var6.getBlockX(), var3.getBlockX()) + 3;
            int var13 = Math.min(var6.getBlockZ(), var3.getBlockZ()) - 3;
            int var14 = Math.max(var6.getBlockZ(), var3.getBlockZ()) + 3;
            int var15 = 0;

            for (int var16 = var11; var16 <= var12; var16++) {
               for (int var17 = var13; var17 <= var14; var17++) {
                  for (int var18 = var9; var18 <= var10; var18++) {
                     if (this.a(var1, var2, var5.getBlockAt(var16, var18, var17), var3)) {
                        if (++var15 >= var4) {
                           return var15;
                        }
                     }
                  }
               }
            }

            return var15;
         } else {
            return 0;
         }
      } else {
         return 0;
      }
   }

   public boolean b(Player var1, Player var2, Location var3) {
      return this.a(var1, var2, var3, 1) >= 1;
   }

   private boolean i(Player var1, Player var2, m1 var3, long var4) {
      return this.s(var1, var2) && !this.gJ.cs().g(var2, var3, var4);
   }

   private void a(m1 var1, Player var2, Block var3, long var4) {
      if (var1 != null && var3 != null && var3.getWorld() != null) {
         this.gJ.c(var1, var3, var4);
         var1.lT = var3.getLocation().clone();
         var1.lU = var4;
         var1.lV = var2 == null ? null : var2.getUniqueId();
         Location var6 = var3.getLocation().add(0.5, 1.0, 0.5);
         var1.lW = var6.clone();
         var1.lX = Math.max(var1.lX, var4 + 900L);
         if (var2 != null && this.gJ.c(var2, var1) && var4 >= var1.mK && var4 <= var1.mJ + 120L) {
            var1.mP = var3.getLocation().clone();
            var1.mQ = Math.max(var1.mQ, var1.mK + 980L);
         }
      }
   }

   public boolean a(Player var1, Player var2, Block var3, boolean var4) {
      if (var3 != null && var2 != null) {
         int var5 = this.gJ.j(var1, var2);
         if (var5 == Integer.MIN_VALUE) {
            return true;
         } else {
            return var3.getY() <= var5 ? true : var4 && this.m(var1, var2, var3);
         }
      } else {
         return false;
      }
   }

   public boolean a(Block var1, Player var2, Player var3, ae var4, boolean var5) {
      return this.a(var1, var2, var3, var4, var5, true);
   }

   public boolean a(Block var1, Player var2, Player var3, ae var4, boolean var5, boolean var6) {
      if (!this.a(var1, var2, var3)) {
         return false;
      } else if (!this.a(var2, var3, var1, var5)) {
         return false;
      } else {
         return !this.a(var1, var4) ? false : !var6 || this.gJ.a(var2, var1, var4);
      }
   }

   public boolean a(Player var1, Player var2, ae var3, m1 var4, long var5, Location var7) {
      if (var1 != null && var2 != null && var3 != null && var7 != null && var7.getWorld() != null) {
         if (!var1.getWorld().equals(var2.getWorld()) || !var1.getWorld().equals(var7.getWorld())) {
            return false;
         } else if (this.b(var1, var2, var7)) {
            return true;
         } else {
            boolean var8 = var4 != null && !var2.isOnGround() && (var4.oC || var2.getVelocity().getY() < -0.06 || this.gJ.v(var2) <= 3.25);
            Location var9 = var4 != null && var4.oB != null ? var4.oB : var7;
            Block var10 = this.gJ.cy().ao(var1, var2, var3, var4, var5);
            if (var10 != null && var10.getWorld() != null && var10.getWorld().equals(var7.getWorld())) {
               double var11 = this.gJ.d(var10.getLocation().add(0.5, 1.0, 0.5), var7);
               double var13 = this.gJ.l(var1, var2) ? 2.85 : 12.0;
               if (var11 <= var13 && (!var8 || this.a(var10, var2, var9))) {
                  return true;
               }
            }

            World var29 = var7.getWorld();
            int var12 = var7.getBlockX();
            int var30 = var7.getBlockY();
            int var14 = var7.getBlockZ();
            boolean var15 = this.gJ.l(var1, var2);
            int var16 = var15 ? var30 - 13 : var30 - 2;
            int var17 = var15 ? var30 : var30 + 2;
            double var18 = -999.0;

            for (int var20 = -4; var20 <= 4; var20++) {
               for (int var21 = var16; var21 <= var17; var21++) {
                  if (var21 >= var29.getMinHeight() && var21 < var29.getMaxHeight()) {
                     for (int var22 = -4; var22 <= 4; var22++) {
                        Block var23 = var29.getBlockAt(var12 + var20, var21, var14 + var22);
                        if ((var23.getType() == Material.OBSIDIAN || var23.getType() == Material.BEDROCK)
                           && (!var8 || this.a(var23, var2, var9))
                           && this.gJ.a(var1, var2, var23, var4, var5)
                           && this.gJ.a(var23, var2, var4, var5)) {
                           double var24 = this.gJ.d(var23.getLocation().add(0.5, 1.0, 0.5), var2.getLocation());
                           if (!var15 || !(var24 > 2.85)) {
                              bg var26 = this.gJ.h(var1, var2, var23);
                              if (var26.cS()) {
                                 double var27 = this.gJ.a(var1, var2, var3, var23);
                                 if (var15) {
                                    var27 += 2.0;
                                 }

                                 var18 = Math.max(var18, var27);
                              }
                           }
                        }
                     }
                  }
               }
            }

            if (this.j(var4, var5)) {
               Block var31 = var4.oi.getBlock();
               if (!var8 || this.a(var31, var2, var9)) {
                  var18 = Math.max(var18, this.gJ.a(var1, var2, var3, var31) + 2.0);
               }
            }

            if (var5 < var4.mv && var4.mw != null) {
               Block var32 = var4.mw.getBlock();
               if (!var8 || this.a(var32, var2, var9)) {
                  var18 = Math.max(var18, this.gJ.a(var1, var2, var3, var32) + 2.0);
               }
            }

            return var15 ? var18 > -900.0 : var18 > 1.0;
         }
      } else {
         return false;
      }
   }

   public boolean b(Block var1, Player var2, Player var3) {
      if (var1 == null || var2 == null || var3 == null) {
         return false;
      } else if (!this.m(var2, var3, var1)) {
         return false;
      } else {
         Location var4 = var2.getLocation();
         Location var5 = var3.getLocation();
         Location var6 = var1.getLocation().add(0.5, 0.5, 0.5);
         int var7 = this.gJ.b(var1, 1, 1);
         if (var7 >= 2) {
            return false;
         } else if (this.gJ.a(var6, var5, 1.2) && var7 >= 2) {
            return false;
         } else {
            return this.gJ.a(var6, var4, 1.2) && var7 >= 2 ? false : var1.getY() < Math.floor(var5.getY());
         }
      }
   }

   public boolean a(Block var1, Player var2, Player var3, ae var4) {
      return this.b(var1, var2, var3) && this.a(var1, var4) && this.gJ.a(var2, var1, var4);
   }

   public boolean l(Player var1, Player var2, Block var3) {
      if (var1 == null || var2 == null || var3 == null) {
         return false;
      } else if (!this.o(var3) || this.gJ.i(var3)) {
         return false;
      } else if (!this.m(var1, var2, var3)) {
         return false;
      } else {
         Block var4 = var3.getRelative(BlockFace.DOWN);
         if (var4.getType() == Material.OBSIDIAN) {
            return false;
         } else {
            if (!var4.getType().isSolid() || this.gJ.i(var4)) {
               boolean var5 = false;

               for (BlockFace var9 : new BlockFace[]{BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST, BlockFace.DOWN}) {
                  Block var10 = var3.getRelative(var9);
                  if (var10.getType().isSolid() && !this.gJ.i(var10) && var10.getType() != Material.OBSIDIAN) {
                     var5 = true;
                     break;
                  }
               }

               if (!var5) {
                  return false;
               }
            }

            Location var11 = var3.getLocation().add(0.5, 0.5, 0.5);
            double var12 = var1.getEyeLocation().distance(var11);
            if (var12 > 4.5 || var12 < 0.5) {
               return false;
            } else {
               return !this.gJ.b(var1, var3, 4.5) ? false : this.e(var3, var2);
            }
         }
      }
   }

   public boolean e(Block var1, Player var2) {
      Location var3 = var1.getLocation().add(0.5, 0.5, 0.5);

      for (Entity var5 : var1.getWorld().getNearbyEntities(var3, 0.5, 0.5, 0.5)) {
         if (var5 instanceof EnderCrystal) {
            return false;
         }

         if (var5 instanceof Player var6
            && (var2 == null || !var6.getUniqueId().equals(var2.getUniqueId()) || !(var2.getLocation().getY() >= var1.getY() + 1.04))) {
            return false;
         }
      }

      return true;
   }

   public boolean a(Player var1, Player var2, ae var3, Block var4, double var5) {
      return this.a(var1, var2, var3, var4, var5, false, false, true);
   }

   public boolean a(Player var1, Player var2, ae var3, Block var4, double var5, boolean var7) {
      return this.a(var1, var2, var3, var4, var5, var7, false, true);
   }

   public boolean a(Player var1, Player var2, ae var3, Block var4, double var5, boolean var7, boolean var8) {
      return this.a(var1, var2, var3, var4, var5, var7, var8, true);
   }

   public boolean a(Player var1, Player var2, ae var3, Block var4, double var5, boolean var7, boolean var8, boolean var9) {
      if (var1 != null && var2 != null && var3 != null && var4 != null) {
         boolean var10 = var4.getType() == Material.OBSIDIAN || var4.getType() == Material.BEDROCK;
         if (!var10 && !this.o(var4)) {
            return false;
         } else if (!var10 && !this.a(var4, var3)) {
            return false;
         } else if (!this.a(var1, var2, var4, var8)) {
            return false;
         } else {
            Block var11 = var4.getRelative(BlockFace.UP);
            Block var12 = var11.getRelative(BlockFace.UP);
            if (var11.getType().isAir() && var12.getType().isAir()) {
               int var13 = this.gJ.j(var1, var2);
               if (var13 == Integer.MIN_VALUE || var4.getY() <= var13 || var8 && this.m(var1, var2, var4)) {
                  Location var14 = var4.getLocation().add(0.5, 1.0, 0.5);
                  double var15 = var4.getY() + 1.0;
                  double var17 = var2.getLocation().getY();
                  if (var7 && this.n(var1, var2, var4)) {
                     var17 = Math.max(var17, var15);
                  }

                  if (!this.gJ.f(var1, var2, var4) && !this.gJ.a(var15, var17)) {
                     return false;
                  } else if (var1.getEyeLocation().distance(var14) > 4.5) {
                     return false;
                  } else if (var1.getEyeLocation().distance(var14) < 0.8) {
                     return false;
                  } else if (var9 && !this.gJ.a(var1, var14, var3)) {
                     return false;
                  } else if (!this.gJ.g(var1.getEyeLocation(), var14)) {
                     return false;
                  } else {
                     double var19 = this.gJ.b(var14, var2);
                     double var21 = var17 - var15;
                     double var23 = var8 ? Math.min(Math.max(0.01, var5), this.gJ.g(var21)) : Math.max(var5, this.gJ.g(var21));
                     if (var19 < var23) {
                        return false;
                     } else {
                        return this.gJ.b(var2, var14) ? false : this.gJ.h(var1, var2, var4).cS();
                     }
                  }
               } else {
                  return false;
               }
            } else {
               return false;
            }
         }
      } else {
         return false;
      }
   }

   private boolean b(Player var1, Player var2, ae var3, Block var4, double var5, boolean var7, boolean var8) {
      if (var1 != null && var2 != null && var3 != null && var4 != null) {
         boolean var9 = var4.getType() == Material.OBSIDIAN || var4.getType() == Material.BEDROCK;
         if (!var9 && !this.o(var4)) {
            return false;
         } else if (!var9 && !this.a(var4, var3)) {
            return false;
         } else if (!this.a(var1, var2, var4, var7)) {
            return false;
         } else {
            Block var10 = var4.getRelative(BlockFace.UP);
            Block var11 = var10.getRelative(BlockFace.UP);
            if (var10.getType().isAir() && var11.getType().isAir()) {
               Location var12 = var4.getLocation().add(0.5, 1.0, 0.5);
               double var13 = var4.getY() + 1.0;
               double var15 = var2.getLocation().getY();
               if (!this.gJ.f(var1, var2, var4) && !this.gJ.a(var13, var15)) {
                  return false;
               } else if (var1.getEyeLocation().distance(var12) > 4.5) {
                  return false;
               } else if (var1.getEyeLocation().distance(var12) < 0.8) {
                  return false;
               } else if (var8 && !this.gJ.a(var1, var12, var3)) {
                  return false;
               } else if (!this.gJ.g(var1.getEyeLocation(), var12)) {
                  return false;
               } else {
                  double var17 = this.gJ.b(var12, var2);
                  double var19 = var15 - var13;
                  double var21 = var7 ? Math.min(Math.max(0.01, var5), this.gJ.g(var19)) : Math.max(var5, this.gJ.g(var19));
                  return var17 < var21 ? false : !this.gJ.b(var2, var12);
               }
            } else {
               return false;
            }
         }
      } else {
         return false;
      }
   }

   public boolean m(Player var1, Player var2, Block var3) {
      if (var1 == null || var2 == null || var3 == null || !var1.getWorld().equals(var2.getWorld())) {
         return false;
      } else if (!var2.isOnGround() && !(this.gJ.v(var2) < 0.45)) {
         Location var4 = var2.getLocation();
         if (var3.getY() >= Math.floor(var4.getY())) {
            return false;
         } else {
            Location var5 = var3.getLocation().add(0.5, 1.0, 0.5);
            double var6 = var4.getY() - var3.getY();
            return this.gJ.d(var5, var4) <= 2.75 && var4.getY() >= var3.getY() + 0.85 && var6 <= 13.0;
         }
      } else {
         return false;
      }
   }

   public boolean n(Player var1, Player var2, Block var3) {
      if (var1 != null && var2 != null && var3 != null && var1.getWorld().equals(var2.getWorld())) {
         int var4 = this.gJ.j(var1, var2);
         if (var4 != Integer.MIN_VALUE && var3.getY() <= var4) {
            Location var5 = var3.getLocation().add(0.5, 0.5, 0.5);
            return this.gJ.d(var5, var2.getLocation()) <= 1.75;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public void c(m1 var1, Block var2, long var3) {
      this.gM.c(var1, var2, var3);
   }

   public void h(m1 var1) {
      this.gM.h(var1);
   }

   public boolean j(m1 var1, long var2) {
      return this.gM.j(var1, var2);
   }

   public boolean d(Player var1, m1 var2, ae var3, long var4) {
      return this.gN.d(var1, var2, var3, var4);
   }

   public Location c(Player var1, int var2) {
      return this.gO.c(var1, var2);
   }

   public Location d(Player var1, int var2) {
      return this.gO.d(var1, var2);
   }

   private boolean ap(Player var1, Player var2, ae var3, m1 var4, long var5) {
      if (var1 == null || var2 == null || var3 == null || var4 == null) {
         return false;
      } else if (!this.gJ.l(var1, var2)) {
         return false;
      } else if (this.j(var4, var5) && var4.oi != null && this.c(var1, var2, var4, var5, var4.oi.getBlock())) {
         return true;
      } else {
         return var4.lT != null && var4.lV != null && var4.lV.equals(var2.getUniqueId()) && var5 - var4.lU <= 1200L
            ? this.c(var1, var2, var4, var5, var4.lT.getBlock())
            : false;
      }
   }

   private boolean c(Player var1, Player var2, m1 var3, long var4, Block var6) {
      if (var1 != null && var2 != null && var6 != null && var6.getWorld() != null) {
         if (!var1.getWorld().equals(var2.getWorld()) || !var1.getWorld().equals(var6.getWorld())) {
            return false;
         } else if (var6.getType() != Material.OBSIDIAN && var6.getType() != Material.BEDROCK) {
            return false;
         } else if (!this.gJ.a(var6, var2, var3, var4)) {
            return false;
         } else if (!this.gJ.a(var1, var2, var6, var3, var4) && !this.gJ.a(var6.getY() + 1.0, var2.getLocation().getY())) {
            return false;
         } else {
            Location var7 = var6.getLocation().add(0.5, 1.0, 0.5);
            double var8 = var1.getEyeLocation().distance(var7);
            if (!(var8 > 4.5) && !(var8 < 0.8)) {
               if (!this.gJ.g(var1.getEyeLocation(), var7)) {
                  return false;
               } else {
                  bg var10 = this.gJ.h(var1, var2, var6);
                  return var10.cS() && !this.gJ.b(var2, var7);
               }
            } else {
               return false;
            }
         }
      } else {
         return false;
      }
   }

   public boolean w(NPC var1, Player var2, Player var3, ae var4, m1 var5, long var6) {
      boolean var8 = this.gJ.l(var2, var3);
      boolean var9 = !var8 && this.gJ.m(var2, var3);
      if (!var8 && !var9) {
         return false;
      } else {
         if (this.gJ.k(var5, var6)) {
            this.gJ.k(var5);
         }

         if (var8 && !this.gJ.g(var5, var6)) {
            if (this.gJ.l(var1, var2, var3, var4, var5, var6)) {
               return true;
            }

            if (this.c(var1, var2, var3, var3.getLocation(), var4, var5, var6)) {
               return true;
            }

            if (this.ap(var2, var3, var4, var5, var6)) {
               return true;
            }
         }

         if (this.gJ.d(var5, var4, var6) && this.gJ.a(var2, Material.OBSIDIAN)) {
            if (this.gJ.k(var1, var2, var3, var4, var5, var6)) {
               return true;
            }

            if (var8 && this.gJ.q(var1, var2, var3, var4, var5, var6)) {
               return true;
            }

            if (var8 && this.gJ.r(var1, var2, var3, var4, var5, var6)) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean p(NPC var1, Player var2, Player var3, ae var4, m1 var5, long var6) {
      bx var8 = this.gJ.k(var2, var3, var4, var5, var6);
      if (var8.dO()) {
         return false;
      } else {
         if (this.gJ.k(var5, var6)) {
            if (!this.gJ.l(var2, var3) && !this.b(var2, var3, var5)) {
               return false;
            }

            this.gJ.k(var5);
         }

         if (!var3.isOnGround() && this.gJ.v(var3) > 0.45) {
            if (var6 - var5.oA > 50L || var5.oB == null) {
               var5.oB = this.d(var3, 80);
               var5.oA = var6;
            }

            if (var5.oB != null) {
               Location var9 = var5.oB;
               double var10 = var2.getLocation().distance(var9);
               double var12 = var2.getLocation().distance(var3.getLocation());
               boolean var14 = this.a(var2, var3, var9, var5, var6);
               if (var14 && var10 <= 4.75) {
                  var5.oC = true;
                  var5.oD = var6;
                  if (this.a(var1, var2, var3, var9, var4, var5, var6)) {
                     return true;
                  }

                  if (!this.gJ.g(var5, var6)) {
                     if (this.gJ.l(var1, var2, var3, var4, var5, var6)) {
                        return true;
                     }

                     if (this.c(var1, var2, var3, var9, var4, var5, var6)) {
                        return true;
                     }
                  }
               }

               double var15 = var14 ? 2.35 : 3.0;
               double var17 = var14 ? 18.0 : 15.0;
               boolean var19 = this.gJ.t(var2, var3, var4, var5, var6);
               if (var10 > var15
                  && var10 < var17
                  && this.gJ.ag(var2)
                  && this.gJ.a(var5, var4, var6)
                  && !this.gJ.c(var5, var4, var6)
                  && this.gJ.a(var2, Material.ENDER_PEARL)
                  && this.gJ.e(var4)
                  && !this.gJ.e(var5, var6)
                  && (!var19 || var14)) {
                  Vector var20 = this.gJ.i(var2.getEyeLocation(), var9);
                  if (var20 == null && this.gJ.h(var2.getEyeLocation(), var9.clone().add(0.0, 1.0, 0.0))) {
                     var20 = this.gJ.j(var2.getEyeLocation(), var9);
                  }

                  if (var20 != null && this.gJ.a(var20)) {
                     EnderCrystal var41 = this.gJ.a(var2, var4);
                     if (var41 != null) {
                        final Location finalVar9 = var9;
                        final Vector finalVar20 = var20;
                        this.gJ.c(var5, var6);
                        Location var22 = var41.getLocation();
                        this.gJ.c(var1, var2, var22.add(0.0, 0.5, 0.0), var5);
                        com.mlc.mlcbot.combat.CombatRuntime.runTaskLater(this.gK.plugin(), () -> {
                           if (var2.isValid() && !var2.isDead()) {
                              int var8x = this.gJ.a(var2, var4, System.currentTimeMillis(), 4);
                              if (var8x > 0) {
                                 var5.kE = System.currentTimeMillis();
                                 this.gJ.d(var4, var5);
                              }

                              com.mlc.mlcbot.combat.CombatRuntime.runTaskLater(this.gK.plugin(), () -> {
                                 if (var2.isValid() && !var2.isDead()) {
                                    this.gJ.a(var1, var2, var3, var4, var5, finalVar9, finalVar20);
                                    var5.oC = true;
                                    var5.oD = System.currentTimeMillis();
                                 } else {
                                    this.gJ.d(var5);
                                 }
                              }, 1L);
                           } else {
                              this.gJ.d(var5);
                           }
                        }, 1L);
                        return true;
                     }

                     this.gJ.a(var1, var2, var3, var4, var5, var9, var20);
                     var5.oC = true;
                     var5.oD = var6;
                     return true;
                  }
               }

               if (this.w(var1, var2, var3, var4, var5, var6)) {
                  return true;
               }

               if (var10 <= 4.5) {
                  var5.oC = true;
                  var5.oD = var6;
                  this.gJ.a(var1, var2, var9, var5);
                  if (var14 && this.a(var1, var2, var3, var9, var4, var5, var6)) {
                     return true;
                  }

                  if (!this.gJ.g(var5, var6)) {
                     if (this.gJ.l(var1, var2, var3, var4, var5, var6)) {
                        return true;
                     }

                     if (this.c(var1, var2, var3, var9, var4, var5, var6)) {
                        return true;
                     }

                     if (this.gJ.d(var5, var4, var6) && this.gJ.a(var2, Material.OBSIDIAN)) {
                        boolean var40 = false;
                        double var21 = var3.getLocation().getY();

                        for (int var23 = -2; var23 <= 2 && !var40; var23++) {
                           for (int var24 = -2; var24 <= 2 && !var40; var24++) {
                              for (int var25 = -2; var25 <= 0 && !var40; var25++) {
                                 Block var26 = var9.clone().add(var23, var25, var24).getBlock();
                                 if ((var26.getType() == Material.OBSIDIAN || var26.getType() == Material.BEDROCK) && this.gJ.a(var26, var3, var5, var6)) {
                                    double var27 = var26.getY() + 1.0;
                                    if (this.gJ.f(var2, var3, var26) && this.gJ.h(var2, var3, var26).cS()) {
                                       var40 = true;
                                    }
                                 }
                              }
                           }
                        }

                        if (!var40 && this.b(var1, var2, var3, var9, var4, var5, var6)) {
                           return true;
                        }
                     }
                  }
               }
            }
         } else {
            var5.oC = false;
            var5.oB = null;
         }

         if (!var5.mk) {
            if (this.aq(var2, var3, var4, var5, var6)) {
               var5.mk = true;
               var5.ml = var6 + 2500L;
               var5.mm = this.d(var3, 60);
               var5.mn = var6;
            }

            return false;
         } else if (var6 <= var5.ml && !var3.isOnGround() && var5.mm != null) {
            if (var6 - var5.mn > 200L) {
               var5.mm = this.d(var3, 60);
               var5.mn = var6;
               if (var5.mm == null) {
                  var5.mk = false;
                  return false;
               }
            }

            Location var29 = var5.mm;
            double var30 = var2.getLocation().distance(var29);
            if (var30 > 4.5
               && this.gJ.ag(var2)
               && var6 - var5.mo > 700L
               && this.gJ.a(var5, var4, var6)
               && this.gJ.a(var2, Material.ENDER_PEARL)
               && !this.gJ.t(var2, var3, var4, var5, var6)) {
               Vector var31 = this.gJ.i(var2.getEyeLocation(), var29);
               if (var31 == null && this.gJ.h(var2.getEyeLocation(), var29.clone().add(0.0, 1.0, 0.0))) {
                  var31 = this.gJ.j(var2.getEyeLocation(), var29);
               }

               if (var31 != null && this.gJ.a(var31)) {
                  this.gJ.a(var1, var2, var29, var5);
                  this.gJ.a(var2, Material.ENDER_PEARL);
                  Vector var35 = var31;
                  com.mlc.mlcbot.combat.CombatRuntime.runTaskLater(this.gK.plugin(), () -> {
                     if (var2.isValid() && !var2.isDead()) {
                        EnderPearl var5x = (EnderPearl)var2.launchProjectile(EnderPearl.class, var35);
                        var2.swingMainHand();
                        this.gJ.a(var5, var4, System.currentTimeMillis(), var5x);
                     } else {
                        this.gJ.e(var5);
                     }
                  }, 1L);
                  var5.mo = var6;
                  return true;
               }
            }

            if (var30 <= 4.5) {
               this.gJ.a(var1, var2, var29, var5);
               if (!this.gJ.g(var5, var6)) {
                  if (this.gJ.l(var1, var2, var3, var4, var5, var6)) {
                     return true;
                  }

                  if (this.j(var5, var6)) {
                     Block var32 = var5.oi.getBlock();
                     if (var32.getType() == Material.OBSIDIAN && this.gJ.a(var32, var3, var5, var6)) {
                        double var13 = var32.getY() + 1.0;
                        double var36 = var3.getLocation().getY();
                        if (this.gJ.f(var2, var3, var32) && this.c(var1, var2, var3, var29, var4, var5, var6)) {
                           return true;
                        }
                     }
                  }

                  if (this.c(var1, var2, var3, var29, var4, var5, var6)) {
                     return true;
                  }

                  if (this.gJ.d(var5, var4, var6) && !this.j(var5, var6)) {
                     boolean var33 = false;
                     double var34 = var3.getLocation().getY();

                     for (int var37 = -2; var37 <= 2 && !var33; var37++) {
                        for (int var16 = -2; var16 <= 2 && !var33; var16++) {
                           for (int var38 = -2; var38 <= 0 && !var33; var38++) {
                              Block var18 = var29.clone().add(var37, var38, var16).getBlock();
                              if ((var18.getType() == Material.OBSIDIAN || var18.getType() == Material.BEDROCK) && this.gJ.a(var18, var3, var5, var6)) {
                                 double var39 = var18.getY() + 1.0;
                                 if (this.gJ.f(var2, var3, var18) && this.gJ.h(var2, var3, var18).cS()) {
                                    var33 = true;
                                 }
                              }
                           }
                        }
                     }

                     if (!var33 && this.b(var1, var2, var3, var29, var4, var5, var6)) {
                        return true;
                     }
                  }
               }
            }

            return false;
         } else {
            var5.mk = false;
            var5.mm = null;
            return false;
         }
      }
   }

   public boolean b(NPC var1, Player var2, Player var3, m1 var4, long var5, ae var7) {
      return this.gN.b(var1, var2, var3, var4, var5, var7);
   }

   public boolean aq(Player var1, Player var2, ae var3, m1 var4, long var5) {
      if (!this.gJ.e(var3)) {
         return false;
      } else if (!this.gJ.f(var3)) {
         return false;
      } else if (var2.isOnGround()) {
         return false;
      } else {
         double var7 = this.gJ.v(var2);
         if (!(var7 < 1.5) && !(var7 > 20.0)) {
            double var9 = var2.getVelocity().getY();
            boolean var11 = var9 >= 0.15 || var9 < 0.0 && var7 > 2.0;
            if (!var11) {
               return false;
            } else if (!this.gJ.a(var1, Material.ENDER_PEARL)) {
               return false;
            } else {
               return !this.gJ.a(var1, Material.OBSIDIAN) ? false : this.gJ.a(var1, Material.END_CRYSTAL);
            }
         } else {
            return false;
         }
      }
   }

   private boolean a(Player var1, Player var2, Location var3, m1 var4, long var5) {
      if (var1 != null && var2 != null && var3 != null && var4 != null) {
         if (!var1.getWorld().equals(var2.getWorld()) || !var1.getWorld().equals(var3.getWorld())) {
            return false;
         } else if (var2.isOnGround()) {
            return false;
         } else {
            double var7 = this.gJ.v(var2);
            double var9 = var2.getLocation().getY() - var1.getLocation().getY();
            double var11 = var2.getVelocity().getY();
            boolean var13 = var11 < -0.06 || var7 <= 3.25;
            boolean var14 = var9 >= 0.45 && var9 <= 14.0;
            return var13 && var14;
         }
      } else {
         return false;
      }
   }

   private boolean a(Block var1, Player var2, Location var3) {
      if (var1 != null && var2 != null && var3 != null && var1.getWorld() != null && var3.getWorld() != null) {
         if (!var1.getWorld().equals(var2.getWorld()) || !var1.getWorld().equals(var3.getWorld())) {
            return false;
         } else if (var1.getType() != Material.OBSIDIAN && var1.getType() != Material.BEDROCK) {
            return false;
         } else {
            Location var4 = var1.getLocation().add(0.5, 1.0, 0.5);
            Location var5 = var2.getLocation();
            double var6 = this.gJ.d(var4, var5);
            double var8 = this.gJ.d(var4, var3);
            double var10 = Math.min(var5.getY(), var3.getY() + 1.0);
            double var12 = Math.abs(var4.getY() - var10);
            boolean var14 = var1.getX() == var5.getBlockX() && var1.getZ() == var5.getBlockZ();
            boolean var15 = var1.getX() == var3.getBlockX() && var1.getZ() == var3.getBlockZ();
            boolean var16 = var6 <= 0.95 || var8 <= 0.95;
            return (var14 || var15 || var16) && var6 <= 1.55 && var8 <= 1.75 && var12 <= 2.35;
         }
      } else {
         return false;
      }
   }

   public boolean b(Player var1, Player var2, ae var3, m1 var4, long var5, Location var7) {
      return this.a(var1, var2, var3, var4, var5, var7, 1) >= 1;
   }

   public boolean t(Player var1, Player var2) {
      if (var1 != null && var2 != null && var1.getWorld() != null && var1.getWorld().equals(var2.getWorld())) {
         if (!var2.isOnGround() && this.gJ.l(var1, var2)) {
            World var3 = var1.getWorld();
            Location var4 = var1.getLocation();
            int var5 = var4.getBlockX();
            int var6 = (int)Math.floor(var4.getY());
            int var7 = var4.getBlockZ();
            byte var8 = 3;
            int var9 = Math.max(var3.getMinHeight(), var6 - var8);
            int var10 = Math.min(var3.getMaxHeight() - 1, var6 + var8);
            int var11 = 0;

            for (int var12 = var5 - var8; var12 <= var5 + var8; var12++) {
               for (int var13 = var9; var13 <= var10; var13++) {
                  for (int var14 = var7 - var8; var14 <= var7 + var8; var14++) {
                     if (var3.getBlockAt(var12, var13, var14).getType() == Material.OBSIDIAN) {
                        if (++var11 >= 4) {
                           return true;
                        }
                     }
                  }
               }
            }

            return false;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private int a(Player var1, Player var2, ae var3, m1 var4, long var5, Location var7, int var8) {
      if (var1 != null && var2 != null && var3 != null && var7 != null && var7.getWorld() != null) {
         if (var1.getWorld().equals(var2.getWorld()) && var1.getWorld().equals(var7.getWorld())) {
            if (!var2.isOnGround() && this.gJ.l(var1, var2)) {
               World var9 = var2.getWorld();
               Location var10 = var2.getLocation();
               int var11 = (int)Math.floor(var1.getLocation().getY());
               int var12 = (int)Math.floor(var10.getY());
               int var13 = Math.max(var9.getMinHeight(), Math.max(var11 - 2, (int)Math.ceil(var10.getY() - 13.0)));
               int var14 = Math.min(var9.getMaxHeight() - 1, Math.min(var11 + 4, var12 - 1));
               if (var13 > var14) {
                  return 0;
               } else {
                  int var15 = Math.min(var10.getBlockX(), var7.getBlockX()) - 3;
                  int var16 = Math.max(var10.getBlockX(), var7.getBlockX()) + 3;
                  int var17 = Math.min(var10.getBlockZ(), var7.getBlockZ()) - 3;
                  int var18 = Math.max(var10.getBlockZ(), var7.getBlockZ()) + 3;
                  int var19 = 0;

                  for (int var20 = var15; var20 <= var16; var20++) {
                     for (int var21 = var17; var21 <= var18; var21++) {
                        for (int var22 = var13; var22 <= var14; var22++) {
                           Block var23 = var9.getBlockAt(var20, var22, var21);
                           if (var23.getType() == Material.OBSIDIAN || var23.getType() == Material.BEDROCK) {
                              Location var24 = var23.getLocation().add(0.5, 1.0, 0.5);
                              double var25 = this.gJ.d(var24, var10);
                              double var27 = this.gJ.d(var24, var7);
                              if ((!(var25 > 3.0) || !(var27 > 3.0))
                                 && (this.gJ.a(var1, var2, var23, var4, var5) || this.gJ.a(var23.getY() + 1.0, var10.getY()))
                                 && this.b(var1, var2, var3, var23, 0.01, true, true)) {
                                 if (++var19 >= var8) {
                                    return var19;
                                 }
                              }
                           }
                        }
                     }
                  }

                  return var19;
               }
            } else {
               return 0;
            }
         } else {
            return 0;
         }
      } else {
         return 0;
      }
   }

   private void k(Player var1, Block var2) {
      if (var1 != null && var2 != null) {
         this.gJ.a(var1, var2.getLocation().add(0.5, 0.5, 0.5));
      }
   }

   private boolean a(NPC var1, Player var2, Player var3, Location var4, ae var5, m1 var6, long var7) {
      if (this.gJ.f(var6)) {
         return false;
      } else if (!this.gJ.a(var2, Material.OBSIDIAN)) {
         return false;
      } else if (!this.gJ.a(var2, Material.END_CRYSTAL)) {
         return false;
      } else if (!this.gJ.d(var6, var5, var7)) {
         return false;
      } else if (var6.lz) {
         return false;
      } else if (!this.t(var2, var3) && !this.b(var2, var3, var5, var6, var7, var4)) {
         if (var7 < var6.mv && var6.mw != null) {
            Block var9 = var6.mw.getBlock();
            if (this.a(var9, var3, var4)
               && var9.getType() == Material.OBSIDIAN
               && this.gJ.a(var9, var3, var6, var7)
               && this.gJ.a(var2, var3, var9, var6, var7)
               && this.gJ.h(var2, var3, var9).cS()) {
               return false;
            }
         }

         if (this.gJ.j(var6, var7)) {
            Block var10 = var6.oi.getBlock();
            if (this.a(var10, var3, var4)
               && var10.getType() == Material.OBSIDIAN
               && this.gJ.a(var10, var3, var6, var7)
               && this.gJ.a(var2, var3, var10, var6, var7)
               && this.gJ.h(var2, var3, var10).cS()) {
               return false;
            }

            this.h(var6);
         }

         Block var11 = this.a(var2, var3, var4, var5, var6, var7);
         if (var11 == null) {
            return false;
         } else {
            this.gJ.a(var6, var5, var3, var7);
            this.gJ.h(var6, var7);
            var6.oC = true;
            var6.oD = var7;
            var6.mv = var7 + 600L;
            var6.mw = var11.getLocation().add(0.5, 0.5, 0.5);
            this.a(var6, var3, var11, var7);
            this.k(var2, var11);
            this.gJ.a(var2, Material.OBSIDIAN);
            if (this.o(var11) && this.gJ.e(var2, var11) && this.a(var11, var2, var3, var5, true, true) && this.b(var2, var3, var5, var11, 0.01, true, true)) {
               if (!com.mlc.mlcbot.combat.CombatRuntime.place(var2, var11, Material.OBSIDIAN)) return false;
               var2.swingMainHand();
               this.gJ.c(var2, Material.OBSIDIAN);
               com.mlc.mlcbot.combat.CombatRuntime.runTaskLater(this.gK.plugin(), () -> {
                  this.gJ.u(var2);
                  var6.lt = false;
               }, 1L);
               return true;
            } else {
               var6.mv = 0L;
               var6.mw = null;
               this.h(var6);
               var6.lt = false;
               return false;
            }
         }
      } else {
         return false;
      }
   }

   private Block a(Player var1, Player var2, Location var3, ae var4, m1 var5, long var6) {
      if (var1 != null && var2 != null && var3 != null && var3.getWorld() != null) {
         World var8 = var3.getWorld();
         Location var9 = var2.getLocation();
         int var10 = (int)Math.floor(var1.getLocation().getY());
         int var11 = var3.getBlockY();
         int var12 = (int)Math.floor(var9.getY());
         int var13 = Math.max(var8.getMinHeight(), Math.min(var10 - 1, var11 - 1));
         int var14 = Math.min(var8.getMaxHeight() - 1, Math.max(var10 + 2, var12 - 1));
         Block var15 = null;
         double var16 = -9999.0;
         int var18 = Math.min(var3.getBlockX(), var9.getBlockX()) - 3;
         int var19 = Math.max(var3.getBlockX(), var9.getBlockX()) + 3;
         int var20 = Math.min(var3.getBlockZ(), var9.getBlockZ()) - 3;
         int var21 = Math.max(var3.getBlockZ(), var9.getBlockZ()) + 3;

         for (int var22 = var18; var22 <= var19; var22++) {
            for (int var23 = var20; var23 <= var21; var23++) {
               for (int var24 = var13; var24 <= var14; var24++) {
                  Block var25 = var8.getBlockAt(var22, var24, var23);
                  if (this.o(var25) && this.gJ.e(var1, var25) && this.a(var25, var1, var2, var4, true, true)) {
                     Location var26 = var25.getLocation().add(0.5, 1.0, 0.5);
                     double var27 = this.gJ.d(var26, var9);
                     double var29 = this.gJ.d(var26, var3);
                     boolean var31 = var25.getX() == var9.getBlockX() && var25.getZ() == var9.getBlockZ();
                     boolean var32 = var25.getX() == var3.getBlockX() && var25.getZ() == var3.getBlockZ();
                     if (!var31 && !var32 && !(var27 <= 0.95) && !(var29 <= 0.95)) {
                        boolean var51 = false;
                     } else {
                        boolean var10000 = true;
                     }

                     int var34 = this.gJ.b(var25, 1, 1);
                     if (var34 < 1) {
                        Block var35 = var25.getRelative(BlockFace.UP);
                        Block var36 = var35.getRelative(BlockFace.UP);
                        if (var35.getType().isAir() && var36.getType().isAir()) {
                           Location var37 = var25.getLocation().add(0.5, 1.0, 0.5);
                           double var38 = var1.getEyeLocation().distance(var37);
                           if (!(var38 > 4.5) && !(var38 < 0.8) && this.gJ.g(var1.getEyeLocation(), var37) && this.b(var1, var2, var4, var25, 0.01, true, true)
                              )
                            {
                              bg var40 = this.gJ.h(var1, var2, var25);
                              double var41 = this.gJ.d(var37, var9);
                              double var43 = this.gJ.d(var37, var3);
                              double var45 = this.gJ.d(var37, var1.getLocation());
                              double var47 = Math.abs(var25.getY() + 1.0 - Math.min(var9.getY(), var3.getY() + 1.0));
                              double var49 = var40.dc() * 3.0
                                 - var40.dd() * (var4 == null ? 1.0 : var4.aJ())
                                 - var41 * 2.45
                                 - var43 * 1.65
                                 - var47 * 0.35
                                 - var34 * 1.1;
                              if (var31) {
                                 var49 += 18.0;
                              }

                              if (var32) {
                                 var49 += 12.0;
                              }

                              if (var41 <= 0.95) {
                                 var49 += 14.0;
                              } else if (var41 <= 1.35) {
                                 var49 += 8.0;
                              }

                              if (var43 <= 0.95) {
                                 var49 += 8.0;
                              }

                              if (var25.getY() == var10 || var25.getY() == var10 + 1) {
                                 var49 += 3.0;
                              }

                              if (var45 < 1.55) {
                                 var49 -= 2.5;
                              }

                              if (var5 != null && var5.oB != null && this.gJ.d(var37, var5.oB) <= 1.75) {
                                 var49 += 2.0;
                              }

                              if (var49 > var16) {
                                 var16 = var49;
                                 var15 = var25;
                              }
                           }
                        }
                     }
                  }
               }
            }
         }

         return var16 > -20.0 ? var15 : null;
      } else {
         return null;
      }
   }

   public boolean b(NPC var1, Player var2, Player var3, Location var4, ae var5, m1 var6, long var7) {
      if (this.gJ.f(var6)) {
         return false;
      } else if (this.gJ.k(var6, var7) && !this.gJ.l(var2, var3)) {
         return false;
      } else if (!this.gJ.a(var2, Material.OBSIDIAN)) {
         return false;
      } else if (!this.gJ.a(var2, Material.END_CRYSTAL)) {
         return false;
      } else if (!this.gJ.d(var6, var5, var7)) {
         return false;
      } else if (var6.lz) {
         return false;
      } else {
         World var9 = var4.getWorld();
         if (var9 == null) {
            return false;
         } else {
            boolean var10 = this.gJ.l(var2, var3);
            boolean var11 = !var10 && this.gJ.m(var2, var3);
            boolean var12 = var10 || var11;
            if (var11) {
               if (var7 - var6.kI < 90L) {
                  return false;
               }

               var6.kI = var7;
            }

            if (var10 && this.t(var2, var3)) {
               return false;
            } else if (var12 && this.b(var2, var3, var5, var6, var7, var4)) {
               return false;
            } else {
               Location var13 = var3.getLocation();
               if (!var12 && this.b(var2, var3, var13)) {
                  return false;
               } else {
                  double var14 = var13.getY();
                  int var16 = (int)Math.floor(var14);
                  int var17 = (int)Math.floor(var2.getLocation().getY());
                  boolean var18 = var3.isOnGround() && Math.abs(var16 - var17) <= 1;
                  if (var18 && this.i(var2, var3, var6, var7)) {
                     return false;
                  } else {
                     double var19 = var11 ? 26.0 : 13.0;
                     int var21 = var12
                        ? Math.max(var9.getMinHeight(), Math.max((int)Math.ceil(var14 - var19), var17 - 2))
                        : (var18 ? Math.max(var9.getMinHeight(), Math.min(var16, var17)) : var4.getBlockY() - 1);
                     int var22 = var12
                        ? Math.min(var9.getMaxHeight() - 1, Math.min((int)Math.floor(var14 - 0.85), var17 + 4))
                        : (var18 ? Math.min(var9.getMaxHeight() - 1, Math.max(var16, var17) + 1) : var4.getBlockY() + 1);
                     Block var23 = null;
                     double var24 = -999.0;
                     int var26 = this.o(var12);
                     int var27 = this.p(var12);
                     int var28 = 0;
                     Location var29 = var2.getEyeLocation();

                     label201:
                     for (int var30 = -var26; var30 <= var26; var30++) {
                        for (int var31 = -var26; var31 <= var26; var31++) {
                           for (int var32 = var21; var32 <= var22; var32++) {
                              Block var33 = var9.getBlockAt(var13.getBlockX() + var30, var32, var13.getBlockZ() + var31);
                              if (this.o(var33)
                                 && this.gJ.e(var2, var33)
                                 && (var12 || !var18 || this.a(var2, var3, var13, 1) < 1)
                                 && this.a(var33, var2, var3, var5, var12, true)) {
                                 double var34 = var33.getX() + 0.5 - var29.getX();
                                 double var36 = var33.getY() + 0.5 - var29.getY();
                                 double var38 = var33.getZ() + 0.5 - var29.getZ();
                                 double var40 = Math.sqrt(var34 * var34 + var36 * var36 + var38 * var38);
                                 if (!(var40 > 4.5) && !(var40 < 0.5)) {
                                    Block var42 = var33.getRelative(BlockFace.UP);
                                    Block var43 = var42.getRelative(BlockFace.UP);
                                    if (var42.getType().isAir() && var43.getType().isAir()) {
                                       if (++var28 > var27) {
                                          break label201;
                                       }

                                       if (var12
                                          ? this.b(var2, var3, var5, var33, var10 ? 0.01 : 0.05, var12, true)
                                          : this.a(var2, var3, var5, var33, 0.5, false, false, true)) {
                                          double var44 = this.gJ.d(var33.getLocation().add(0.5, 1.0, 0.5), var13);
                                          double var46 = 20.0 - var44;
                                          if (var33.getY() == var17) {
                                             var46 += 14.0;
                                          }

                                          if (var18 && var33.getY() >= Math.min(var16, var17)) {
                                             var46 += 8.0;
                                          }

                                          if (var11) {
                                             var46 += 6.0;
                                          }

                                          var46 -= Math.max(0, var33.getY() - var17) * 4.0;
                                          var46 -= Math.max(0, var17 - var33.getY()) * (var18 ? 8.0 : 1.25);
                                          var46 -= Math.abs(var33.getY() - var17) * 0.1;
                                          if (var46 > var24) {
                                             var24 = var46;
                                             var23 = var33;
                                          }
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }

                     if (var23 == null) {
                        return false;
                     } else {
                        this.gJ.a(var6, var5, var3, var7);
                        this.gJ.h(var6, var7);
                        this.a(var6, var3, var23, var7);
                        this.k(var2, var23);
                        this.gJ.a(var2, Material.OBSIDIAN);
                        boolean var48 = var12
                           ? this.b(var2, var3, var5, var23, var10 ? 0.01 : 0.05, var12, true)
                           : this.a(var2, var3, var5, var23, 0.5, false, false, true);
                        if (this.o(var23) && this.gJ.e(var2, var23) && this.a(var23, var2, var3, var5, var12, true) && var48) {
                           if (!com.mlc.mlcbot.combat.CombatRuntime.place(var2, var23, Material.OBSIDIAN)) return false;
                           var2.swingMainHand();
                           this.gJ.c(var2, Material.OBSIDIAN);
                           com.mlc.mlcbot.combat.CombatRuntime.runTaskLater(this.gK.plugin(), () -> {
                              this.gJ.u(var2);
                              var6.lt = false;
                           }, 1L);
                           return true;
                        } else {
                           this.h(var6);
                           var6.lt = false;
                           return false;
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public boolean c(NPC var1, Player var2, Player var3, Location var4, ae var5, m1 var6, long var7) {
      if (this.gJ.f(var6)) {
         return false;
      } else if (this.gJ.k(var6, var7)) {
         return false;
      } else if (var6.mq) {
         return false;
      } else if (this.gJ.g(var6, var7)) {
         return false;
      } else {
         long var9 = this.gJ.a(var5, var6);
         boolean var11 = this.gJ.l(var2, var3) && this.ap(var2, var3, var5, var6, var7) && (var7 - var6.oj <= 520L || var7 - var6.lU <= 520L);
         if (!var11 && var7 - var6.kD < var9) {
            return false;
         } else if (this.gJ.a(var4, 3.0, var2, var3, var6, var5, var7) >= 3) {
            return false;
         } else {
            double var12 = var3.getLocation().getY();
            if (this.j(var6, var7)) {
               Block var14 = var6.oi.getBlock();
               if (var14.getType() == Material.OBSIDIAN && this.gJ.a(var14, var3, var6, var7)) {
                  Location var15 = var14.getLocation().add(0.5, 1.0, 0.5);
                  double var16 = var14.getY() + 1.0;
                  if (this.gJ.a(var16, var12)) {
                     double var18 = var2.getEyeLocation().distance(var15);
                     if (var18 <= 4.5 && var18 >= 0.8) {
                        if (!this.gJ.a(var2, var15, var5)) {
                           this.h(var6);
                        } else {
                           bg var20 = this.gJ.h(var2, var3, var14);
                           if (var20.cS() && !this.gJ.d(var2, var14)) {
                              boolean var44 = this.gJ.a(var1, var2, var14, var3, var5, var6, var7);
                              if (var44) {
                                 this.h(var6);
                              }

                              return var44;
                           }

                           this.h(var6);
                        }
                     }
                  }
               } else {
                  this.h(var6);
               }
            }

            Block var40 = null;
            double var41 = -999.0;
            int var17 = var4.getBlockY();
            int var42 = var17 - 13;
            int var19 = var17;

            for (int var43 = -3; var43 <= 3; var43++) {
               for (int var21 = -3; var21 <= 3; var21++) {
                  for (int var22 = var42; var22 <= var19; var22++) {
                     Block var23 = var4.clone().add(var43, var22 - var17, var21).getBlock();
                     if (var23.getType() == Material.OBSIDIAN || var23.getType() == Material.BEDROCK) {
                        Location var24 = var23.getLocation().add(0.5, 1.0, 0.5);
                        double var25 = var23.getY() + 1.0;
                        if (this.gJ.a(var25, var12)) {
                           double var27 = var2.getEyeLocation().distance(var24);
                           if (!(var27 > 4.5) && !(var27 < 0.8) && this.gJ.a(var2, var24, var5) && this.gJ.a(var23, var3, var6, var7)) {
                              bg var29 = this.gJ.h(var2, var3, var23);
                              if (var29.cS()) {
                                 double var30 = var29.dc();
                                 double var32 = var29.dd();
                                 double var34 = this.gJ.d(var24, var3.getLocation());
                                 double var36 = var12 - var25;
                                 double var38 = var30 * 2.0 - var34 * 0.65 - Math.max(0.0, var36) * 0.05 - var32 * var5.aJ();
                                 if (this.gJ.l(var2, var3)) {
                                    var38 += 4.0;
                                 }

                                 if (var38 > var41) {
                                    var41 = var38;
                                    var40 = var23;
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }

            return var40 != null && var41 > (this.gJ.l(var2, var3) ? -50.0 : 0.0) ? this.gJ.a(var1, var2, var40, var3, var5, var6, var7) : false;
         }
      }
   }
}
