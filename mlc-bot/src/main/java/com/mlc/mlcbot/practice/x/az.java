// Extracted from the user-provided PracticeBot-Source; strategy logic retained.
package com.mlc.mlcbot.practice.x;

import com.mlc.mlcbot.practice.PracticeBotPlugin;
import com.mlc.mlcbot.practice.bridge.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

final class az {
   private final bf en;

   az(bf var1) {
      this.en = var1;
   }

   boolean a(NPC var1, Player var2, ae var3, m1 var4, long var5) {
      av var7 = this.en.bZ();
      PracticeBotPlugin var8 = this.en.ca();
      if (var7.f(var4)) {
         return false;
      } else if (var7.k(var4, var5)) {
         return false;
      } else {
         EnderCrystal var9 = this.en.b(var2, var3);
         if (var9 == null) {
            return false;
         } else {
            Location var10 = var9.getLocation();
            if (!var7.a(var2, var10, var3)) {
               return false;
            } else if (!var7.h(var2.getEyeLocation(), var10.clone().add(0.0, 0.5, 0.0))) {
               return false;
            } else {
               double var11 = var7.b(var10, var2);
               Block var13 = var10.clone().add(0.0, -1.0, 0.0).getBlock();
               double var14 = var7.d(var2.getLocation(), var10);
               double var16 = var10.getY() - var2.getLocation().getY();
               if (var14 < 1.25 && var16 > -0.75 && var16 < 2.05) {
                  return false;
               } else if (!var7.a(var2, var10, var13)) {
                  return false;
               } else {
                  j1 var18 = var7.a(var2, var11, 0.0);
                  if (!var18.cS()) {
                     return false;
                  } else {
                     var7.a(var4, var3, null, var5);
                     var7.c(var1, var2, var10.add(0.0, 0.5, 0.0), var4);
                     if (!var7.a(var2, Material.END_CRYSTAL)) {
                        var4.lt = false;
                        return false;
                     } else {
                        var2.attack(var9);
                        var2.swingMainHand();
                        var4.kE = var5;
                        var7.d(var3, var4);
                        com.mlc.mlcbot.combat.CombatRuntime.runTaskLater(var8.plugin(), () -> {
                           var7.u(var2);
                           var4.lt = false;
                           var4.mq = false;
                           var7.i(var4);
                        }, 1L);
                        return true;
                     }
                  }
               }
            }
         }
      }
   }

   boolean a(NPC var1, Player var2, Player var3, EnderCrystal var4, ae var5, m1 var6, long var7) {
      av var9 = this.en.bZ();
      PracticeBotPlugin var10 = this.en.ca();
      if (var9.f(var6)) {
         return false;
      } else if (var9.k(var6, var7)) {
         return false;
      } else {
         Location var11 = var2.getLocation();
         Location var12 = var4.getLocation();
         Vector var13 = var12.toVector().subtract(var11.toVector());
         var13.setY(0);
         if (var13.lengthSquared() < 0.01) {
            return false;
         } else {
            var13.normalize();
            Block var14 = null;

            for (double var15 = 0.5; var15 <= 1.5; var15 += 0.5) {
               Location var17 = var11.clone().add(var13.clone().multiply(var15));
               Block var18 = var17.getBlock();
               if (var18.getType().isAir() && this.en.e(var2, var18)) {
                  Block var19 = var18.getRelative(BlockFace.DOWN);
                  if (var19.getType() != Material.OBSIDIAN && var9.a(var18, var2, var3, var5, false)) {
                     var14 = var18;
                     break;
                  }
               }
            }

            if (var14 != null && var9.a(var2, Material.OBSIDIAN) && var9.d(var6, var5, var7)) {
               double var20 = var2.getEyeLocation().distance(var14.getLocation().add(0.5, 0.5, 0.5));
               if (var20 <= 4.5) {
                  var9.a(var6, var5, var3, var7);
                  var9.h(var6, var7);
                  var9.c(var1, var2, var14.getLocation().add(0.5, 0.5, 0.5), var6);
                  var9.a(var2, Material.OBSIDIAN);
                  if (var14.getType().isAir() && this.en.e(var2, var14) && var9.a(var14, var2, var3, var5, false)) {
                     if (!com.mlc.mlcbot.combat.CombatRuntime.place(var2, var14, Material.OBSIDIAN)) return false;
                     var2.swingMainHand();
                     var9.c(var2, Material.OBSIDIAN);
                  }

                  com.mlc.mlcbot.combat.CombatRuntime.runTaskLater(var10.plugin(), () -> {
                     var9.u(var2);
                     var6.lt = false;
                  }, 1L);
                  return true;
               }
            }

            return false;
         }
      }
   }

   boolean l(NPC var1, Player var2, Player var3, ae var4, m1 var5, long var6) {
      if (this.en.bZ().k(var5, var6)) {
         if (!this.en.bZ().l(var2, var3) && !this.en.bZ().b(var2, var3, var5) && !this.en.bZ().j(var5, var6)) {
            return false;
         }

         this.en.bZ().k(var5);
      }

      if (this.en.bZ().f(var5)) {
         return false;
      } else if (var5.mq) {
         return false;
      } else if (this.en.bZ().g(var5, var6)) {
         return false;
      } else {
         long var8 = this.en.f(var4, var5);
         if (var6 - var5.kE < var8) {
            return false;
         } else {
            EnderCrystal var10 = this.en.ak(var2, var3, var4, var5, var6);
            if (var10 == null) {
               return false;
            } else {
               av var11 = this.en.bZ();
               boolean var12 = var11.l(var2, var3) || var11.b(var2, var3, var5) || var5.oC || var11.b(var10.getLocation(), var2) >= 5.0;
               return var11.a(var4, var5, var6, "BREAK", var12) ? false : this.a(var1, var2, var10, var3, var4, var5, var6);
            }
         }
      }
   }

   boolean m(NPC var1, Player var2, Player var3, ae var4, m1 var5, long var6) {
      av var8 = this.en.bZ();
      if (var8.k(var5, var6)) {
         return false;
      } else if (!var8.g(var2, var3)) {
         return false;
      } else if (var8.g(var5, var6)) {
         return false;
      } else if (this.l(var1, var2, var3, var4, var5, var6)) {
         return true;
      } else if (!this.en.a(var2, var3, var5, var4, var6) && var8.j(var1, var2, var3, var4, var5, var6)) {
         return true;
      } else {
         return var8.j(var4) && var8.a(var1, var2, var3, var4, var5, var6) ? true : this.v(var1, var2, var3, var4, var5, var6);
      }
   }

   boolean v(NPC var1, Player var2, Player var3, ae var4, m1 var5, long var6) {
      av var8 = this.en.bZ();
      if (!var4.aO()) {
         return false;
      } else if (var6 - var5.mz < 300L) {
         return false;
      } else {
         Block var9 = var8.h(var2, var3);
         if (var9 == null) {
            return false;
         } else {
            Location var10 = var9.getLocation().add(0.5, 0.5, 0.5);
            if (!var8.a(var2, var10, var4)) {
               return false;
            } else if (var2.getEyeLocation().distance(var10) > 5.25) {
               return false;
            } else if (!var8.a(var2.getEyeLocation(), var9, 5.25)) {
               return false;
            } else {
               var8.w(var1);
               var8.a(var2, var10);
               if (!var8.a(var2, Material.NETHERITE_PICKAXE, Material.DIAMOND_PICKAXE, Material.IRON_PICKAXE)) {
                  return false;
               } else if (com.mlc.mlcbot.combat.CombatRuntime.breakBlock(var2, var9)) {
                  var2.swingMainHand();
                  var5.mz = var6;
                  var5.kE = var6;
                  var5.le = 0L;
                  var8.z(var2);
                  return true;
               } else {
                  var8.z(var2);
                  return false;
               }
            }
         }
      }
   }

   boolean a(NPC var1, Player var2, EnderCrystal var3, Player var4, ae var5, m1 var6, long var7) {
      av var9 = this.en.bZ();
      PracticeBotPlugin var10 = this.en.ca();
      if (var9.k(var6, var7)) {
         if (!var9.l(var2, var4) && !var9.b(var2, var4, var6) && !var9.j(var6, var7)) {
            return false;
         }

         var9.k(var6);
      }

      if (!var3.isValid() || var3.isDead()) {
         var9.i(var6);
         return false;
      } else if (var4.isValid() && !var4.isDead()) {
         Location var11 = var3.getLocation();
         Block var12 = var11.clone().add(0.0, -1.0, 0.0).getBlock();
         double var13 = var12.getY() + 1.0;
         double var15 = var4.getLocation().getY();
         if (!var9.a(var2, var11, var5) && !var9.l(var2, var4)) {
            var9.i(var6);
            return false;
         } else if (!var9.a(var13, var15)) {
            var9.i(var6);
            return false;
         } else {
            long var17 = this.en.f(var5, var6);
            if (var7 - var6.kE < var17) {
               return false;
            } else if (!var9.a(var2, var4, var5, var3, var6, var7)) {
               bg var19 = var9.h(var2, var4, var12);
               var9.i(var6);
               return false;
            } else {
               var9.a(var6, var5, var4, var7);
               var9.c(var1, var2, var11.add(0.0, 0.5, 0.0), var6);
               if (!var9.a(var2, Material.END_CRYSTAL)) {
                  var6.lt = false;
                  return false;
               } else {
                  var2.attack(var3);
                  var2.swingMainHand();
                  var6.kE = var7;
                  var9.d(var5, var6);
                  com.mlc.mlcbot.combat.CombatRuntime.runTaskLater(var10.plugin(), () -> {
                     var9.u(var2);
                     var6.lt = false;
                     var6.mq = false;
                     var9.i(var6);
                  }, 1L);
                  return true;
               }
            }
         }
      } else {
         var9.i(var6);
         return false;
      }
   }

   boolean o(NPC var1, Player var2, Player var3, ae var4, m1 var5, long var6) {
      av var8 = this.en.bZ();
      if (!var8.ak(var3)) {
         return false;
      } else {
         this.en.f(var2, var3, var4, var5, var6);
         if (!var5.lB && !var5.mq) {
            double var9 = var2.getLocation().distance(var3.getLocation());
            if (var9 > 3.35) {
               return false;
            } else if (!var8.h(var2.getEyeLocation(), var3.getEyeLocation())) {
               return false;
            } else {
               var8.l(var5);
               var8.j(var5);
               boolean var11 = this.en.cb().nextInt(100) < 55;
               if (var11) {
                  return var8.b(var1, var2, var3, var5, var6, var4) ? true : var8.c(var1, var2, var3, var5, var6, var4);
               } else {
                  return var8.c(var1, var2, var3, var5, var6, var4) ? true : var8.b(var1, var2, var3, var5, var6, var4);
               }
            }
         } else {
            return false;
         }
      }
   }
}
