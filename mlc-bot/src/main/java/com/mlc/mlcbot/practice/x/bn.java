// Extracted from the user-provided PracticeBot-Source; strategy logic retained.
package com.mlc.mlcbot.practice.x;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public final class bn {
   private static final double fT = -1.15;
   private static final double fU = 0.75;
   private static final double fV = 13.0;
   private static final long fW = 420L;
   private static final long fX = 980L;
   private static final double fY = 0.45;
   private static final double fZ = 0.34;
   private static final double ga = 1.08;
   private static final long gb = 200L;
   private static final long gc = 150L;
   private static final long gd = 800L;
   private final av ge;

   public bn(av var1) {
      this.ge = var1;
   }

   public boolean a(double var1, double var3) {
      double var5 = var3 - var1;
      double var7 = var3 - (var1 - 1.0);
      return var5 >= -1.15 && var7 >= 0.75 && var7 <= 13.0 && var5 <= 13.0;
   }

   public double k(Player var1, Player var2, Block var3) {
      return var2 == null ? 0.0 : var2.getLocation().getY();
   }

   public boolean f(Player var1, Player var2, Block var3) {
      if (var3 == null) {
         return false;
      } else {
         double var4 = this.k(var1, var2, var3);
         double var6 = var3.getY() + 1.0;
         if (this.a(var6, var4)) {
            return true;
         } else if (var1 != null && var2 != null && this.ge.l(var1, var2)) {
            double var8 = var4 - var6;
            double var10 = var4 - var3.getY();
            double var12 = var3.getY() - Math.floor(var1.getLocation().getY());
            return var8 >= -1.15 && var10 >= 0.75 && var12 >= -2.0 && var12 <= 4.0;
         } else {
            return false;
         }
      }
   }

   public double g(Player var1, Player var2, Block var3) {
      return var3 == null ? 999.0 : this.k(var1, var2, var3) - (var3.getY() + 1.0);
   }

   public boolean d(m1 var1, Block var2, long var3) {
      if (var1 == null || var2 == null || var1.mP == null) {
         return false;
      } else if (var3 > var1.mQ) {
         this.r(var1);
         return false;
      } else {
         Location var5 = var1.mP;
         return var5.getWorld() != null
            && var5.getWorld().equals(var2.getWorld())
            && var5.getBlockX() == var2.getX()
            && var5.getBlockY() == var2.getY()
            && var5.getBlockZ() == var2.getZ();
      }
   }

   public void r(m1 var1) {
      if (var1 != null) {
         var1.mP = null;
         var1.mQ = 0L;
      }
   }

   public void s(m1 var1) {
      if (var1 != null) {
         var1.mI = null;
         var1.mJ = 0L;
         var1.mK = 0L;
         var1.mL = Double.NaN;
         var1.mM = null;
         var1.mN = null;
         var1.mO = null;
         this.r(var1);
      }
   }

   public double n(Player var1, m1 var2) {
      return var1 != null && var2 != null && !Double.isNaN(var2.mL) ? Math.max(0.0, var1.getLocation().getY() - var2.mL) : 0.0;
   }

   public boolean c(Player var1, m1 var2) {
      return var1 != null && var2 != null && var2.mI != null && var2.mI.equals(var1.getUniqueId());
   }

   public boolean g(Player var1, m1 var2, long var3) {
      if (!this.c(var1, var2)) {
         return false;
      } else if (var2.mK <= 0L || var3 < var2.mK) {
         return false;
      } else if (var3 > var2.mJ) {
         return false;
      } else {
         return var3 - var2.mK > 420L ? false : this.n(var1, var2) <= 0.45 || var3 - var2.mK <= 85L;
      }
   }

   public boolean b(Player var1, m1 var2, Block var3, long var4) {
      if (!this.d(var2, var3, var4)) {
         return false;
      } else if (!this.c(var1, var2)) {
         return false;
      } else if (var2.mK > 0L && var4 >= var2.mK) {
         if (var4 - var2.mK > 980L) {
            return false;
         } else {
            double var6 = this.n(var1, var2);
            if (var6 >= 0.34 && var6 <= 1.08) {
               return true;
            } else {
               if (var4 - var2.mK <= 160L) {
                  for (int var8 = 1; var8 <= 3; var8++) {
                     Location var9 = this.ge.cq().a(var1, var2, var8);
                     if (var9 != null) {
                        double var10 = Math.max(0.0, var9.getY() - var2.mL);
                        if (var10 >= 0.34 && var10 <= 1.08) {
                           return true;
                        }
                     }
                  }
               }

               return false;
            }
         }
      } else {
         return false;
      }
   }

   public boolean c(Player var1, m1 var2, Block var3, long var4) {
      if (!this.d(var2, var3, var4)) {
         return false;
      } else if (!this.c(var1, var2)) {
         return false;
      } else if (var2.mK > 0L && var4 >= var2.mK) {
         if (var4 - var2.mK > 980L) {
            return false;
         } else {
            return this.b(var1, var2, var3, var4) ? false : this.n(var1, var2) < 0.34;
         }
      } else {
         return false;
      }
   }

   public double b(Player var1, Player var2, Block var3, m1 var4, long var5) {
      double var7 = var2 == null ? 0.0 : var2.getLocation().getY();
      return this.b(var2, var4, var3, var5) ? Math.max(var7, var3.getY() + 1.0) : var7;
   }

   public boolean a(Player var1, Player var2, Block var3, m1 var4, long var5) {
      if (var3 == null) {
         return false;
      } else {
         double var7 = this.b(var1, var2, var3, var4, var5);
         double var9 = var3.getY() + 1.0;
         if (this.a(var9, var7)) {
            return true;
         } else if (var1 != null && var2 != null && this.ge.l(var1, var2)) {
            double var11 = var7 - var9;
            double var13 = var7 - var3.getY();
            double var15 = var3.getY() - Math.floor(var1.getLocation().getY());
            return var11 >= -1.15 && var13 >= 0.75 && var15 >= -2.0 && var15 <= 4.0;
         } else {
            return false;
         }
      }
   }

   public boolean d(m1 var1, ae var2, long var3) {
      if (!this.ge.cm().h(var2)) {
         return false;
      } else {
         return var1.lz ? false : var3 >= var1.mu;
      }
   }

   public void h(m1 var1, long var2) {
      var1.lz = true;
      var1.kH = var2;
      var1.mu = var2 + 200L;
   }

   public boolean f(m1 var1) {
      return var1 != null && (var1.mG || var1.mH);
   }

   public void i(m1 var1, long var2) {
      var1.mq = true;
      var1.mr = var2;
   }

   public void g(m1 var1) {
      var1.mq = false;
      var1.mr = 0L;
   }

   public void a(m1 var1, Player var2, long var3) {
      if (var1 != null && var2 != null) {
         var1.lD = var2.getUniqueId();
         var1.lC = Math.max(var1.lC, var3);
      }
   }

   public boolean b(m1 var1, Player var2, long var3) {
      if (var1 != null && var2 != null) {
         if (var1.lD == null || !var1.lD.equals(var2.getUniqueId())) {
            this.l(var1);
            return false;
         } else if (var3 > var1.lC) {
            this.l(var1);
            return false;
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   public void l(m1 var1) {
      if (var1 != null) {
         var1.lC = 0L;
         var1.lD = null;
      }
   }

   public void c(m1 var1, Block var2, long var3) {
      if (var1 != null) {
         var1.oi = var2 == null ? null : var2.getLocation().clone();
         var1.oj = var2 == null ? 0L : var3;
      }
   }

   public void h(m1 var1) {
      if (var1 != null) {
         var1.oi = null;
         var1.oj = 0L;
      }
   }

   public boolean j(m1 var1, long var2) {
      if (var1.oi == null) {
         return false;
      } else if (var2 - var1.oj > 800L) {
         this.h(var1);
         return false;
      } else {
         Block var4 = var1.oi.getBlock();
         if (var4.getType() != Material.OBSIDIAN) {
            this.h(var1);
            return false;
         } else {
            return true;
         }
      }
   }

   public void b(Player var1, m1 var2, long var3) {
      if (var2.mp != null) {
         Entity var5 = Bukkit.getEntity(var2.mp);
         if (var5 instanceof EnderCrystal var6 && !var5.isDead()) {
            double var7 = var1.getEyeLocation().distance(var6.getLocation());
            if (var7 > 5.5) {
               this.i(var2);
            } else {
               if (!this.ge.co().g(var1.getEyeLocation(), var6.getLocation())) {
                  if (var2.mt == 0L) {
                     var2.mt = var3;
                  } else if (var3 - var2.mt > 150L) {
                     this.i(var2);
                  }
               } else {
                  var2.mt = 0L;
               }
            }
         } else {
            this.i(var2);
         }
      }
   }

   public void i(m1 var1) {
      if (var1 != null) {
         var1.mp = null;
         var1.ms = 0L;
         var1.mt = 0L;
         var1.mq = false;
         var1.lW = null;
         var1.lX = 0L;
         var1.lt = false;
         var1.lB = false;
      }
   }
}
