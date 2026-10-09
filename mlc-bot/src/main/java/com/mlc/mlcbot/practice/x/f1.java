// Extracted from the user-provided PracticeBot-Source; strategy logic retained.
package com.mlc.mlcbot.practice.x;

import com.mlc.mlcbot.practice.bridge.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public final class f1 {
   private static final double iV = 4.5;
   private static final long iW = 3000L;
   private static final long iX = 100L;
   private static final long iY = 90L;
   private final av iZ;

   public f1(av var1) {
      this.iZ = var1;
   }

   public boolean a(m1 var1, ae var2, long var3) {
      return !this.iZ.cm().e(var2) ? false : var3 >= var1.ok;
   }

   public void a(m1 var1, long var2, long var4) {
      if (var1 != null) {
         long var6 = var2 + Math.max(0L, var4);
         var1.ou = Math.max(var1.ou, var6);
      }
   }

   public void c(m1 var1, long var2) {
      if (var1 != null) {
         this.iZ.j(var1);
         var1.os = true;
         var1.ot = var2;
         this.a(var1, var2, 100L);
      }
   }

   public void d(m1 var1) {
      if (var1 != null) {
         var1.os = false;
         var1.ot = 0L;
      }
   }

   public void b(m1 var1, ae var2, long var3) {
      this.iZ.j(var1);
      var1.ok = var3 + this.iZ.cm().k(var2);
      var1.og = true;
      var1.oh = var3;
      var1.kP = var3;
      this.d(var1);
      var1.ov = var3 + 90L;
      this.a(var1, var3, 90L);
      var1.ow = true;
      var1.oz = null;
   }

   public void a(m1 var1, ae var2, long var3, EnderPearl var5) {
      this.b(var1, var2, var3);
      var1.oz = var5 != null ? var5.getUniqueId() : null;
   }

   public void e(m1 var1) {
      var1.og = false;
      var1.ow = false;
      var1.oz = null;
   }

   public void d(m1 var1, long var2) {
      if (var1.og || var1.ow) {
         if (var1.oz != null) {
            Entity var4 = Bukkit.getEntity(var1.oz);
            if (var4 == null || var4.isDead() || !var4.isValid()) {
               this.e(var1);
               return;
            }
         } else if (var1.oh <= 0L || var2 - var1.oh > 250L) {
            this.e(var1);
            return;
         }

         if (var2 - var1.oh > 3000L) {
            this.e(var1);
         }
      }
   }

   public boolean e(m1 var1, long var2) {
      if (var1 == null) {
         return false;
      } else {
         this.d(var1, var2);
         if (var1.os) {
            if (var1.ot > 0L && var2 - var1.ot <= 100L) {
               return true;
            }

            this.d(var1);
         }

         return var2 < var1.ou || var2 < var1.ov;
      }
   }

   public boolean a(m1 var1, ae var2, long var3, boolean var5) {
      if (!this.iZ.cm().e(var2)) {
         return true;
      } else {
         return var5 && var1.mk ? false : !this.a(var1, var2, var3);
      }
   }

   public boolean c(m1 var1, ae var2, long var3) {
      return this.a(var1, var2, var3, false);
   }

   public boolean f(m1 var1, long var2) {
      if (var1 == null) {
         return false;
      } else {
         this.d(var1, var2);
         return var1.og || var1.ow;
      }
   }

   public boolean g(m1 var1, long var2) {
      return this.e(var1, var2);
   }

   public EnderCrystal au(Player var1) {
      return this.a(var1, null);
   }

   public EnderCrystal a(Player var1, ae var2) {
      EnderCrystal var3 = null;
      double var4 = Double.MAX_VALUE;

      for (Entity var7 : var1.getNearbyEntities(6.0, 6.0, 6.0)) {
         if (var7 instanceof EnderCrystal var8 && !var8.isDead()) {
            Location var9 = var8.getLocation();
            double var10 = var1.getEyeLocation().distance(var9);
            if (var10 <= 4.5
               && var10 < var4
               && (var2 == null || this.iZ.co().a(var1, var9, var2))
               && this.e(var1, var9)
               && this.iZ.co().g(var1.getEyeLocation(), var9)) {
               var4 = var10;
               var3 = var8;
            }
         }
      }

      return var3;
   }

   private boolean e(Player var1, Location var2) {
      if (var1 != null && var2 != null) {
         double var3 = this.iZ.d(var1.getLocation(), var2);
         double var5 = var2.getY() - var1.getLocation().getY();
         if (var3 < 1.25 && var5 > -0.75 && var5 < 2.05) {
            return false;
         } else {
            double var7 = this.b(var1, var2, this.iZ.cp().b(var2, var1));
            return this.iZ.a(var1, var7, 0.0).cS();
         }
      } else {
         return false;
      }
   }

   private double b(Player var1, Location var2, double var3) {
      Location var5 = var1.getLocation();
      double var6 = Math.max(0.0, var3) * 1.35 + (var3 > 0.0 ? 0.5 : 0.0);
      double var8 = this.iZ.d(var5, var2);
      double var10 = var2.getY() - var5.getY();
      if (var10 > -0.75 && var10 < 2.5) {
         if (var8 < 1.75) {
            var6 = Math.max(var6, 12.0);
         } else if (var8 < 2.5) {
            var6 = Math.max(var6, 8.0);
         } else if (var8 < 3.25) {
            var6 = Math.max(var6, 5.0);
         }
      }

      return var6;
   }

   public int av(Player var1) {
      int var2 = 0;

      for (Entity var4 : var1.getNearbyEntities(6.0, 6.0, 6.0)) {
         if (var4 instanceof EnderCrystal var5 && !var5.isDead()) {
            Location var6 = var5.getLocation();
            double var7 = var1.getEyeLocation().distance(var6);
            if (var7 <= 4.5 && this.iZ.co().g(var1.getEyeLocation(), var6)) {
               var2++;
            }
         }
      }

      return var2;
   }

   public int a(Player var1, long var2, int var4) {
      return this.a(var1, null, var2, var4);
   }

   public int a(Player var1, ae var2, long var3, int var5) {
      if (var1 != null && var5 > 0) {
         int var6 = 0;
         EnderCrystal var7 = null;

         for (int var8 = 0; var8 < var5; var8++) {
            EnderCrystal var9 = this.a(var1, var2);
            if (var9 == null
               || !var9.isValid()
               || var9.isDead()
               || var7 != null && var7.getUniqueId().equals(var9.getUniqueId()) && var6 > 0
               || !this.iZ.cr().a(var1, Material.END_CRYSTAL)) {
               break;
            }

            var1.attack(var9);
            var1.swingMainHand();
            var7 = var9;
            var6++;
         }

         return var6;
      } else {
         return 0;
      }
   }

   public boolean a(NPC var1, Player var2, Player var3, ae var4, m1 var5, Location var6, Vector var7) {
      long var8 = System.currentTimeMillis();
      if (this.iZ.t(var2, var3, var4, var5, var8)) {
         this.d(var5);
         return false;
      } else {
         bx var10 = var5 != null && var5.lE != null && var3 != null && var3.getUniqueId().equals(var5.lG) ? var5.lE : this.iZ.k(var2, var3, var4, var5, var8);
         if (var10.dO()) {
            this.d(var5);
            return false;
         } else {
            int var11 = this.a(var2, var4, var8, 2);
            if (var11 > 0) {
               var5.kE = var8;
               this.iZ.d(var4, var5);
            }

            if (var7 != null && this.iZ.cq().a(var7)) {
               if (!this.iZ.cr().a(var2, Material.ENDER_PEARL)) {
                  this.d(var5);
                  return false;
               } else {
                  this.iZ.a(var1, var2, var6, var5);
                  float[] var12 = this.iZ.cl().f(var2.getUniqueId());
                  if (var12 != null) {
                     var12[0] = var2.getLocation().getYaw();
                     var12[1] = var2.getLocation().getPitch();
                  }

                  EnderPearl var13 = (EnderPearl)var2.launchProjectile(EnderPearl.class, var7);
                  var2.swingMainHand();
                  this.a(var5, var4, var8, var13);
                  com.mlc.mlcbot.combat.CombatRuntime.runTaskLater(this.iZ.ca().plugin(), () -> {
                     if (var2.isValid() && !var2.isDead()) {
                        this.iZ.u(var2);
                     }
                  }, 1L);
                  return true;
               }
            } else {
               this.d(var5);
               return false;
            }
         }
      }
   }

   public void a(Player var1, m1 var2, long var3) {
      this.d(var2, var3);
   }

   public void u(Player var1) {
      if (var1 != null && var1.isValid() && !var1.isDead()) {
         m1 var2 = this.iZ.ck().i(var1.getUniqueId());
         if (!this.r(var2, System.currentTimeMillis())) {
            this.iZ.cr().a(var1, Material.END_CRYSTAL);
         }
      }
   }

   public void z(Player var1) {
      com.mlc.mlcbot.combat.CombatRuntime.runTaskLater(this.iZ.ca().plugin(), () -> {
         if (var1 != null && var1.isValid() && !var1.isDead()) {
            this.u(var1);
         }
      }, 1L);
   }

   public void f(Player var1, m1 var2, long var3) {
      if (var1 != null && var2 != null && var1.isValid() && !var1.isDead()) {
         if (!this.r(var2, var3)) {
            this.u(var1);
         }
      }
   }

   private boolean r(m1 var1, long var2) {
      return var1 != null && (var1.lk || var1.mg || var1.lu || var1.ly || this.iZ.k(var1, var2) || var1.os || this.e(var1, var2) || var1.mG || var1.mH);
   }

   public boolean a(NPC var1, Player var2, Location var3, m1 var4, long var5) {
      Location var7 = var2.getEyeLocation();
      Vector var8 = var3.toVector().subtract(var7.toVector());
      double var9 = var8.length();
      if (var9 < 1.0) {
         return false;
      } else {
         var8.normalize();

         for (Entity var12 : var2.getNearbyEntities(8.0, 8.0, 8.0)) {
            if (var12 instanceof EnderCrystal var13 && !var13.isDead()) {
               Location var14 = var13.getLocation();
               Vector var15 = var14.toVector().subtract(var7.toVector());
               double var16 = var15.normalize().dot(var8);
               if (var16 > 0.7) {
                  double var18 = var7.distance(var14);
                  if (var18 < var9 + 2.0 && var18 <= 4.5 && this.iZ.co().g(var7, var14) && this.e(var2, var14)) {
                     this.c(var4, var5);
                     this.iZ.c(var1, var2, var14.clone().add(0.0, 0.5, 0.0), var4);
                     if (!this.iZ.cr().a(var2, Material.END_CRYSTAL)) {
                        this.d(var4);
                        return false;
                     }

                     com.mlc.mlcbot.combat.CombatRuntime.runTaskLater(this.iZ.ca().plugin(), () -> {
                        if (var13.isValid() && !var13.isDead()) {
                           if (!this.e(var2, var13.getLocation()) || !this.iZ.cr().a(var2, Material.END_CRYSTAL)) {
                              this.d(var4);
                              return;
                           }

                           var2.attack(var13);
                           var2.swingMainHand();
                        }

                        var4.kE = System.currentTimeMillis();
                        this.iZ.d((org.bukkit.entity.Player) null, var4);
                     }, 1L);
                     return true;
                  }
               }
            }
         }

         return false;
      }
   }
}
