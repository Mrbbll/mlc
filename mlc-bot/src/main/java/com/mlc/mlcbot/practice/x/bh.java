// Extracted from the user-provided PracticeBot-Source; strategy logic retained.
package com.mlc.mlcbot.practice.x;

import com.mlc.mlcbot.practice.PracticeBotPlugin;
import java.util.UUID;
import com.mlc.mlcbot.practice.bridge.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public final class bh {
   private static final double fd = 4.5;
   private static final double fe = 4.5;
   private static final long ff = 600L;
   private static final double fg = 0.45;
   private static final double fh = 13.0;
   private static final double fi = 26.0;
   private static final long fj = 6500L;
   private static final double fk = 12.0;
   private static final double fl = 2.85;
   private static final double fm = 1.85;
   private static final long fn = 980L;
   private static final long fo = 900L;
   private static final int fp = 24;
   private static final long fq = 260L;
   private final av fr;
   private final PracticeBotPlugin fs;

   public bh(av var1) {
      this.fr = var1;
      this.fs = var1.ca();
   }

   private boolean l(Player var1, Player var2, m1 var3, long var4) {
      return var3 == null ? true : var3.lt || var3.lB || var3.mq || var4 < var3.mv || var4 < var3.lC || this.fr.j(var3, var4) || this.q(var1, var2);
   }

   private boolean q(Player var1, Player var2) {
      return var1 != null && var2 != null && this.fr.l(var1, var2) ? var2.getVelocity().getY() < -0.08 || this.fr.v(var2) <= 3.25 : false;
   }

   private boolean a(Player var1, Player var2, m1 var3, long var4, boolean var6, boolean var7) {
      if (var3 == null) {
         return true;
      } else if (var3.lt || var3.lB || var3.mq || var4 < var3.mv || this.fr.j(var3, var4)) {
         return true;
      } else if (var2 != null && this.fr.c(var2, var3) && var4 <= var3.mJ + 120L) {
         return true;
      } else {
         return var6 && var2 != null ? var2.getVelocity().getY() < -0.08 || this.fr.v(var2) <= 3.25 : var7 && var2 != null && var2.getVelocity().getY() < -0.16;
      }
   }

   private boolean a(Player var1, m1 var2, long var3, boolean var5) {
      if (!var5 && this.fr.cI()) {
         long var6 = Math.max(260L, this.fr.i(false) * 50L);
         if (var2 != null && var3 - var2.kI < var6) {
            return false;
         } else {
            boolean var8 = this.fr.b(var1, "strict-obsidian-search", false);
            if (var8 && var2 != null) {
               var2.kI = var3;
            }

            return var8;
         }
      } else {
         return true;
      }
   }

   private int b(boolean var1, boolean var2) {
      if (!var1 && this.fr.cI()) {
         int var3 = this.fr.L();
         if (var3 >= 150) {
            return var2 ? 2 : 2;
         } else if (var3 >= 100) {
            return var2 ? 3 : 2;
         } else {
            return 3;
         }
      } else {
         return 4;
      }
   }

   private int m(boolean var1) {
      if (!var1 && this.fr.cI()) {
         int var2 = this.fr.L();
         if (var2 >= 150) {
            return 10;
         } else if (var2 >= 100) {
            return 14;
         } else {
            return var2 >= 35 ? 20 : 28;
         }
      } else {
         return Integer.MAX_VALUE;
      }
   }

   private int n(boolean var1) {
      if (!var1 && this.fr.cI()) {
         int var2 = this.fr.L();
         if (var2 >= 150) {
            return 8;
         } else if (var2 >= 100) {
            return 10;
         } else {
            return var2 >= 35 ? 14 : 18;
         }
      } else {
         return 24;
      }
   }

   private bk m(Player var1, Player var2, m1 var3, long var4) {
      if (var1 != null && var2 != null && var3 != null && var3.nj) {
         int var6 = Bukkit.getCurrentTick();
         int var7 = this.fr.l(this.l(var1, var2, var3, var4));
         if (var3.nk != Long.MIN_VALUE && var6 - var3.nk < var7) {
            UUID var8 = var2.getUniqueId();
            Location var9 = var1.getLocation();
            Location var10 = var2.getLocation();
            if (var3.nl != null
               && var3.nl.equals(var8)
               && var3.nm == var9.getBlockX()
               && var3.nn == var9.getBlockY()
               && var3.no == var9.getBlockZ()
               && var3.np == var10.getBlockX()
               && var3.nq == var10.getBlockY()
               && var3.nr == var10.getBlockZ()
               && var3.ns == var3.kD
               && var3.nt == var3.kE
               && var3.nu == var3.kH) {
               Block var11 = this.d(var1, var3.nv);
               Block var12 = this.d(var1, var3.nw);
               return new bk(var11, var12, var3.nx, var3.ny, var3.nz);
            } else {
               return null;
            }
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private bk a(Player var1, Player var2, m1 var3, bk var4) {
      if (var1 != null && var2 != null && var3 != null && var4 != null) {
         Location var5 = var1.getLocation();
         Location var6 = var2.getLocation();
         var3.nj = true;
         var3.nk = Bukkit.getCurrentTick();
         var3.nl = var2.getUniqueId();
         var3.nm = var5.getBlockX();
         var3.nn = var5.getBlockY();
         var3.no = var5.getBlockZ();
         var3.np = var6.getBlockX();
         var3.nq = var6.getBlockY();
         var3.nr = var6.getBlockZ();
         var3.ns = var3.kD;
         var3.nt = var3.kE;
         var3.nu = var3.kH;
         var3.nv = var4.dq() == null ? null : var4.dq().getLocation().clone();
         var3.nw = var4.dr() == null ? null : var4.dr().getLocation().clone();
         var3.nx = var4.ds();
         var3.ny = var4.dt();
         var3.nz = var4.dp();
         return var4;
      } else {
         return var4;
      }
   }

   private Block d(Player var1, Location var2) {
      return var1 != null && var2 != null && var2.getWorld() != null && var2.getWorld().equals(var1.getWorld()) ? var2.getBlock() : null;
   }

   private boolean a(Player var1, int var2, int var3, int var4) {
      if (var1 == null) {
         return false;
      } else {
         Location var5 = var1.getEyeLocation();
         double var6 = var2 + 0.5 - var5.getX();
         double var8 = var3 + 1.0 - var5.getY();
         double var10 = var4 + 0.5 - var5.getZ();
         double var12 = 4.62;
         return var6 * var6 + var8 * var8 + var10 * var10 <= var12 * var12;
      }
   }

   private boolean b(Player var1, int var2, int var3, int var4) {
      if (var1 == null) {
         return false;
      } else {
         Location var5 = var1.getEyeLocation();
         double var6 = var2 + 0.5 - var5.getX();
         double var8 = var3 + 0.5 - var5.getY();
         double var10 = var4 + 0.5 - var5.getZ();
         double var12 = 4.65;
         return var6 * var6 + var8 * var8 + var10 * var10 <= var12 * var12;
      }
   }

   private boolean p(Player var1, Player var2) {
      if (var1 != null && var2 != null && var2.isOnGround()) {
         int var3 = (int)Math.floor(var1.getLocation().getY());
         int var4 = (int)Math.floor(var2.getLocation().getY());
         return Math.abs(var3 - var4) <= 1;
      } else {
         return false;
      }
   }

   private boolean i(Player var1, Player var2, m1 var3, long var4) {
      return this.p(var1, var2) && !this.fr.cs().g(var2, var3, var4);
   }

   private boolean a(Player var1, Player var2, m1 var3, long var4, Block var6) {
      return this.p(var1, var2) && !this.fr.b(var2, var3, var6, var4);
   }

   private void a(m1 var1, Player var2, Block var3, long var4) {
      if (var1 != null && var3 != null && var3.getWorld() != null) {
         this.fr.c(var1, var3, var4);
         this.b(var1, var2, var3, var4);
         Location var6 = var3.getLocation().add(0.5, 1.0, 0.5);
         var1.lW = var6.clone();
         var1.lX = Math.max(var1.lX, var4 + 900L);
         if (var2 != null && this.fr.c(var2, var1) && var4 >= var1.mK && var4 <= var1.mJ + 120L) {
            var1.mP = var3.getLocation().clone();
            long var7 = var1.mK + 980L;
            var1.mQ = Math.max(var1.mQ, var7);
         }
      }
   }

   private boolean al(Player var1, Player var2, ae var3, m1 var4, long var5) {
      if (!this.fr.k(var4, var5)) {
         return false;
      } else {
         boolean var7 = this.fr.b(var1, var2, var4)
            || this.fr.l(var1, var2)
            || this.fr.j(var4, var5)
            || var4 != null && var5 < var4.mv && var4.mw != null
            || var2 != null && var4 != null && this.fr.c(var2, var4) && (var5 <= var4.mJ || var5 <= var4.mQ)
            || var2 != null && var4 != null && var4.lV != null && var4.lV.equals(var2.getUniqueId()) && var5 - var4.lU <= 6500L;
         if (var7) {
            this.fr.k(var4);
            return false;
         } else {
            return true;
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

   public boolean a(NPC var1, Player var2, Player var3, Block var4, ae var5, m1 var6, long var7) {
      if (this.fr.f(var6)) {
         return false;
      } else {
         bx var9 = this.fr.k(var2, var3, var5, var6, var7);
         if (var9.dP() && this.b(var9.dX(), var4)) {
            this.b(var6, var4);
            return false;
         } else if (this.al(var2, var3, var5, var6, var7)) {
            return false;
         } else if (!this.fr.a(var2, Material.END_CRYSTAL)) {
            return false;
         } else if (var4.getType() != Material.OBSIDIAN && var4.getType() != Material.BEDROCK) {
            return false;
         } else if (this.a(var2, var3, var6, var7, var4)) {
            return false;
         } else if (!this.a(var4, var3, var6, var7)) {
            this.b(var6, var4);
            return false;
         } else if (var6.mq) {
            return false;
         } else if (this.fr.g(var6, var7)) {
            return false;
         } else {
            boolean var10 = this.fr.l(var2, var3) && this.fr.j(var6, var7) && var6.oi != null && this.b(var6.oi.getBlock(), var4) && var7 - var6.oj <= 260L;
            boolean var11 = this.fr.l(var2, var3) && var7 < var6.mv && var6.mw != null && this.b(var6.mw.getBlock(), var4);
            long var12 = this.fr.a(var5, var6);
            if (!var10 && !var11 && var7 - var6.kD < var12) {
               return false;
            } else {
               if (var6.mp != null) {
                  Entity var14 = Bukkit.getEntity(var6.mp);
                  if (var14 != null && var14.isValid() && !var14.isDead()) {
                     if (!this.fr.a(var14, var6, var5, var7)) {
                        if (var14 instanceof EnderCrystal var29 && this.fr.a(var2, var3, var5, var29, var6, var7) && var7 - var6.kE >= this.fr.b(var5, var6)) {
                           return this.fr.a(var1, var2, var29, var3, var5, var6, var7);
                        }

                        return false;
                     }

                     this.fr.i(var6);
                  } else {
                     this.fr.i(var6);
                  }
               }

               if (this.fr.a(var2, var3, var6, var5, var7)) {
                  return false;
               } else {
                  Location var28 = var4.getLocation().add(0.5, 1.0, 0.5);
                  double var15 = var4.getY() + 1.0;
                  double var17 = var3.getLocation().getY();
                  if (!this.fr.a(var2, var28, var5)) {
                     return false;
                  } else if (!this.fr.a(var2, var3, var4, var6, var7) && !this.fr.a(var15, var17)) {
                     return false;
                  } else {
                     double var19 = var2.getEyeLocation().distance(var28);
                     if (var19 > 4.5) {
                        return false;
                     } else if (!this.fr.g(var2.getEyeLocation(), var28)) {
                        return false;
                     } else {
                        bg var21 = this.fr.h(var2, var3, var4);
                        if (this.b(var2, var3, var4, var21)) {
                           this.b(var6, var4);
                           return false;
                        } else if (!var21.cS()) {
                           this.b(var6, var4);
                           return false;
                        } else {
                           boolean var22 = this.fr.l(var2, var3) || this.fr.b(var2, var3, var6) || var6.oC || this.fr.c(var3, var6);
                           if (this.fr.a(var5, var6, var7, "PLACE", var22)) {
                              return false;
                           } else {
                              this.fr.a(var6, var5, var3, var7);
                              this.a(var6, var3, var4, var7);
                              this.fr.c(var1, var2, var28, var6);
                              if (!this.fr.a(var2, Material.END_CRYSTAL)) {
                                 var6.lt = false;
                                 return false;
                              } else {
                                 this.fr.z(var2);
                                 if (!this.a(var4, var3, var6, var7)) {
                                    var6.lt = false;
                                    this.b(var6, var4);
                                    return false;
                                 } else {
                                    Location var23 = var4.getLocation().add(0.5, 1.0, 0.5);

                                    try {
                                       EnderCrystal var24 = com.mlc.mlcbot.combat.CombatRuntime.spawnCrystal(var2, var23);
                                       if (var24 == null) { var6.lt = false; return false; }
                                       var2.swingMainHand();
                                       this.fr.c(var2, Material.END_CRYSTAL);
                                       this.fr.a(var2, Material.END_CRYSTAL);
                                       var6.mp = var24.getUniqueId();
                                       var6.ms = var7;
                                       var6.lj = var24.getUniqueId();
                                       var6.kD = var7;
                                       this.fr.c(var5, var6);
                                       this.a(var6, var3, var4, var7);
                                       if (var5.aF() != ah.HARD && var5.aF() != ah.PRO) {
                                          var6.lt = false;
                                       } else {
                                          Block var25 = var24.getLocation().clone().add(0.0, -1.0, 0.0).getBlock();
                                          bg var26 = this.fr.cz().h(var2, var3, var25);
                                          if (var26.cS()
                                             && !this.b(var2, var3, var25, var26)
                                             && (this.fr.a(var2, var3, var25, var6, var7) || this.fr.a(var15, var3.getLocation().getY()))) {
                                             if (var5.aF() == ah.PRO) {
                                                this.fr.c(var1, var2, var24.getLocation().add(0.0, 0.5, 0.0), var6);
                                                if (!this.fr.a(var2, Material.END_CRYSTAL)) {
                                                   var6.lt = false;
                                                   this.fr.i(var6);
                                                   return true;
                                                }

                                                var2.attack(var24);
                                                var2.swingMainHand();
                                                var6.kE = var7;
                                                this.fr.d(var5, var6);
                                                com.mlc.mlcbot.combat.CombatRuntime.runTaskLater(this.fs.plugin(), () -> {
                                                   this.fr.u(var2);
                                                   var6.lt = false;
                                                   var6.mq = false;
                                                   this.fr.i(var6);
                                                }, 1L);
                                             } else {
                                                this.fr.i(var6, var7);
                                                com.mlc.mlcbot.combat.CombatRuntime.runTaskLater(this.fs.plugin(), () -> {
                                                   if (var2.isValid() && !var2.isDead() && var3.isValid() && !var3.isDead()) {
                                                      if (var24.isValid() && !var24.isDead()) {
                                                         bg var8 = this.fr.cz().h(var2, var3, var25);
                                                         if (!var8.cS() || this.b(var2, var3, var25, var8)) {
                                                            var6.lt = false;
                                                            var6.mq = false;
                                                            this.fr.i(var6);
                                                            return;
                                                         }

                                                         this.fr.c(var1, var2, var24.getLocation().add(0.0, 0.5, 0.0), var6);
                                                         if (!this.fr.a(var2, Material.END_CRYSTAL)) {
                                                            var6.lt = false;
                                                            var6.mq = false;
                                                            this.fr.i(var6);
                                                            return;
                                                         }

                                                         var2.attack(var24);
                                                         var2.swingMainHand();
                                                         var6.kE = System.currentTimeMillis();
                                                         this.fr.d(var5, var6);
                                                      }

                                                      com.mlc.mlcbot.combat.CombatRuntime.runTaskLater(this.fs.plugin(), () -> {
                                                         this.fr.u(var2);
                                                         var6.lt = false;
                                                         var6.mq = false;
                                                         this.fr.i(var6);
                                                      }, 1L);
                                                   } else {
                                                      var6.lt = false;
                                                      var6.mq = false;
                                                      this.fr.i(var6);
                                                   }
                                                }, 1L);
                                             }
                                          } else {
                                             var6.lt = false;
                                             this.fr.i(var6);
                                          }
                                       }
                                    } catch (Exception var27) {
                                       var6.lt = false;
                                       var6.mq = false;
                                    }

                                    return true;
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

   public double g(double var1) {
      if (var1 <= 1.0) {
         return 1.0;
      } else if (var1 <= 2.0) {
         return 0.8;
      } else if (var1 <= 4.0) {
         return 0.5;
      } else if (var1 <= 8.0) {
         return 0.3;
      } else {
         return var1 <= 12.0 ? 0.2 : 0.1;
      }
   }

   public boolean k(NPC var1, Player var2, Player var3, ae var4, m1 var5, long var6) {
      if (this.fr.f(var5)) {
         return false;
      } else if (this.al(var2, var3, var4, var5, var6)) {
         return false;
      } else if (!this.fr.a(var2, Material.OBSIDIAN)) {
         return false;
      } else if (!this.fr.a(var2, Material.END_CRYSTAL)) {
         return false;
      } else if (!this.fr.d(var5, var4, var6)) {
         return false;
      } else if (this.fr.j(var5, var6)) {
         return false;
      } else if (var5.lz) {
         return false;
      } else {
         Location var8 = var3.getLocation();
         double var9 = var8.getY();
         boolean var11 = !var3.isOnGround() && this.fr.v(var3) >= 0.45 && this.fr.l(var2, var3);
         boolean var12 = !var11 && !var3.isOnGround() && this.fr.v(var3) >= 0.45 && this.fr.m(var2, var3);
         boolean var13 = var11 || var12;
         boolean var14 = this.a(var2, var3, var5, var6, var11, var12);
         if (!this.a(var2, var5, var6, var14)) {
            return false;
         } else {
            Block var15 = this.ao(var2, var3, var4, var5, var6);
            if (var15 == null && !this.fr.a(var2, var3, var4, var5, var6, var8)) {
               Block var16 = null;
               double var17 = -999.0;
               if (var12 && (var14 || !this.fr.cI())) {
                  if (var6 - var5.kI < 90L) {
                     return false;
                  }

                  var5.kI = var6;
               }

               if (var11 && this.fr.cx().t(var2, var3)) {
                  return false;
               } else if (var13 && this.fr.cx().b(var2, var3, var4, var5, var6, var8)) {
                  return false;
               } else {
                  int var19 = (int)Math.floor(var2.getLocation().getY());
                  int var20 = (int)Math.floor(var9);
                  boolean var21 = var3.isOnGround() && Math.abs(var20 - var19) <= 1;
                  if (var21 && this.i(var2, var3, var5, var6)) {
                     return false;
                  } else {
                     int var68;
                     int var69;
                     if (var13) {
                        double var24 = var12 ? 26.0 : 13.0;
                        var68 = (int)Math.ceil(var9 - var24);
                        var69 = (int)Math.floor(var9 - 0.85);
                        var68 = Math.max(var68, var19 - 2);
                        var69 = Math.min(var69, var19 + 4);
                     } else if (var21) {
                        var68 = Math.max(var8.getWorld().getMinHeight(), Math.min(var19, var20));
                        var69 = Math.min(var8.getWorld().getMaxHeight() - 1, Math.max(var19, var20) + 1);
                     } else {
                        var69 = var20 - 1;
                        var68 = var20 - 3;
                     }

                     Location var70 = var2.getEyeLocation();
                     int var25 = var8.getBlockX();
                     int var26 = var8.getBlockZ();
                     int var27 = this.b(var14, var13);
                     int var28 = this.m(var14);
                     int var29 = 0;

                     label279:
                     for (int var30 = -var27; var30 <= var27; var30++) {
                        for (int var31 = -var27; var31 <= var27; var31++) {
                           int var32 = var25 + var30;
                           int var33 = var26 + var31;

                           for (int var34 = var68; var34 <= var69; var34++) {
                              if (this.b(var2, var32, var34, var33)) {
                                 Block var35 = var8.getWorld().getBlockAt(var32, var34, var33);
                                 if (this.o(var35)) {
                                    Block var36 = var35.getRelative(BlockFace.DOWN);
                                    if (var36.getType() != Material.OBSIDIAN) {
                                       Block var37 = var35.getRelative(BlockFace.UP);
                                       Block var38 = var37.getRelative(BlockFace.UP);
                                       if (var37.getType().isAir() && var38.getType().isAir()) {
                                          double var39 = var32 + 0.5 - var70.getX();
                                          double var41 = var34 + 0.5 - var70.getY();
                                          double var43 = var33 + 0.5 - var70.getZ();
                                          double var45 = Math.sqrt(var39 * var39 + var41 * var41 + var43 * var43);
                                          if (!(var45 > 4.5) && !(var45 < 0.5)) {
                                             boolean var47 = this.fr.c(var3, var5) && var6 <= var5.mJ;
                                             boolean var48 = var13 || !var3.isOnGround() || this.fr.v(var3) >= 0.45;
                                             if (this.fr.e(var2, var35) && this.fr.a(var35, var2, var3, var4, var48, false)) {
                                                if (++var29 > var28) {
                                                   break label279;
                                                }

                                                Location var49 = var35.getLocation().add(0.5, 1.0, 0.5);
                                                double var50 = var35.getY() + 1.0;
                                                boolean var52 = this.fr.a(var2, var3, var35, var5, var6) || this.fr.a(var50, var9);
                                                if (var52 || var12) {
                                                   bg var53 = this.fr.h(var2, var3, var35);
                                                   double var54 = var53.dc();
                                                   double var56 = var9 - var50;
                                                   double var58 = var11 ? Math.min(0.05, this.g(var56)) : this.g(var56);
                                                   if ((var12 || !(var54 < var58))
                                                      && var53.cS()
                                                      && (
                                                         var12
                                                            ? this.fr.g(var2.getEyeLocation(), var49)
                                                            : this.fr.a(var2, var3, var4, var35, var58, var47, var48, false)
                                                      )
                                                      && (var12 || !this.fr.b(var3, var49))) {
                                                      double var60 = Math.sqrt(var30 * var30 + var31 * var31);
                                                      double var62 = var35.getY() == var19 ? 14.0 : 0.0;
                                                      if (var21 && var35.getY() >= Math.min(var19, var20)) {
                                                         var62 += 8.0;
                                                      }

                                                      var62 -= Math.max(0, var35.getY() - var19) * 4.0;
                                                      var62 -= Math.max(0, var19 - var35.getY()) * (var21 ? 8.0 : 1.25);
                                                      double var64 = Math.max(0.0, 3.0 - this.fr.d(var49, var8)) * 1.8;
                                                      double var66 = 18.0 - var60 + var62 + var64 + var54 * 1.5;
                                                      if (var12) {
                                                         var66 += 6.0;
                                                         var66 -= Math.max(0.0, var9 - (var19 + 13.0)) * 0.12;
                                                      }

                                                      if (var66 > var17) {
                                                         var17 = var66;
                                                         var16 = var35;
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
                     }

                     if (var16 != null && var17 > (var13 ? -50.0 : 5.0)) {
                        Location var71 = var16.getLocation().add(0.5, 0.5, 0.5);
                        this.fr.a(var2, var71);
                        this.fr.a(var2, Material.OBSIDIAN);
                        Block var72 = var16.getRelative(BlockFace.DOWN);
                        boolean var73 = this.fr.c(var3, var5) && var6 <= var5.mJ;
                        boolean var74 = var13 || !var3.isOnGround() || this.fr.v(var3) >= 0.45;
                        double var75 = var11 ? 0.01 : this.g(var9 - (var16.getY() + 1.0));
                        boolean var76 = var12
                           ? this.fr.g(var2.getEyeLocation(), var16.getLocation().add(0.5, 1.0, 0.5))
                           : this.fr.a(var2, var3, var4, var16, var75, var73, var74, false);
                        if (this.o(var16)
                           && this.fr.e(var2, var16)
                           && this.fr.a(var16, var2, var3, var4, var74, false)
                           && var72.getType() != Material.OBSIDIAN
                           && var76) {
                           if (!com.mlc.mlcbot.combat.CombatRuntime.place(var2, var16, Material.OBSIDIAN)) return false;
                           var2.swingMainHand();
                           this.fr.c(var2, Material.OBSIDIAN);
                           this.fr.a(var5, var4, var3, var6);
                           this.fr.h(var5, var6);
                           var5.mv = var6 + 600L;
                           var5.mw = var16.getLocation().add(0.5, 0.5, 0.5);
                           this.a(var5, var3, var16, var6);
                           this.fr.a(var2, var71);
                           com.mlc.mlcbot.combat.CombatRuntime.runTaskLater(this.fs.plugin(), () -> {
                              this.fr.u(var2);
                              var5.lt = false;
                           }, 1L);
                           return true;
                        }
                     }

                     return false;
                  }
               }
            } else {
               return false;
            }
         }
      }
   }

   public boolean j(NPC var1, Player var2, Player var3, ae var4, m1 var5, long var6) {
      if (this.fr.f(var5)) {
         return false;
      } else if (this.al(var2, var3, var4, var5, var6)) {
         return false;
      } else if (var5.mq) {
         return false;
      } else if (this.fr.g(var5, var6)) {
         return false;
      } else {
         long var8 = this.fr.a(var4, var5);
         if (var6 - var5.kD < var8) {
            return false;
         } else {
            if (var5.mp != null) {
               Entity var10 = Bukkit.getEntity(var5.mp);
               if (var10 != null && var10.isValid() && !var10.isDead()) {
                  double var11 = var2.getEyeLocation().distance(var10.getLocation());
                  if (var11 <= 4.5 && this.fr.g(var2.getEyeLocation(), var10.getLocation())) {
                     this.fr.i(var5);
                  } else {
                     this.fr.i(var5);
                  }
               } else {
                  this.fr.i(var5);
               }
            }

            double var22 = var3.getLocation().getY();
            bx var12 = this.fr.k(var2, var3, var4, var5, var6);
            Block var13 = this.ao(var2, var3, var4, var5, var6);
            if (var13 != null && this.g(var2, var3, var4, var5, var6, var13)) {
               this.q(var5);
               var13 = null;
               var12 = this.fr.k(var2, var3, var4, var5, var6);
            }

            if (var13 != null && var12.dU() != by.PLACE_SAFE && var12.dU() != by.BREAK_SAFE) {
               this.q(var5);
               var13 = null;
               var12 = this.fr.k(var2, var3, var4, var5, var6);
            }

            if (var12.dP()) {
               return false;
            } else if (var12.dU() == by.PLACE_SAFE && var12.dX() != null) {
               return this.a(var1, var2, var3, var12.dX(), var4, var5, var6);
            } else if (var12.dU() == by.CREATE_OBSIDIAN_PRESSURE && var12.dY() != null && this.a(var1, var2, var3, var4, var5, var6, var12.dY())) {
               return true;
            } else {
               Block var14 = var12.dX();
               if (var14 != null) {
                  Block var15 = this.fr.k(var2, var3);
                  if (var15 != null) {
                     return this.a(var1, var2, var3, var15, var4, var5, var6);
                  }

                  if (this.fr.d(var5, var4, var6)
                     && this.fr.a(var2, Material.OBSIDIAN)
                     && this.fr.a(var2, Material.END_CRYSTAL)
                     && !var5.lz
                     && !this.fr.cx().b(var2, var3, var3.getLocation())) {
                     Block var16 = this.fr.e(var2, var3, var4, var5, var6);
                     if (var16 != null) {
                        if (this.i(var2, var3, var5, var6)) {
                           return false;
                        }

                        if (this.fr.l(var2, var3) && this.fr.cx().t(var2, var3)) {
                           return false;
                        }

                        this.fr.a(var5, var4, var3, var6);
                        this.fr.h(var5, var6);
                        this.a(var5, var3, var16, var6);
                        this.fr.a(var2, var16.getLocation().add(0.5, 0.5, 0.5));
                        this.fr.a(var2, Material.OBSIDIAN);
                        boolean var31 = this.fr.l(var2, var3);
                        if (this.o(var16)
                           && this.fr.e(var2, var16)
                           && this.fr.a(var16, var2, var3, var4, var31, false)
                           && this.fr.a(var2, var3, var4, var16, var31 ? 0.01 : 0.5, false, var31, !var31)) {
                           if (!com.mlc.mlcbot.combat.CombatRuntime.place(var2, var16, Material.OBSIDIAN)) return false;
                           var2.swingMainHand();
                           this.fr.c(var2, Material.OBSIDIAN);
                           this.a(var5, var3, var16, var6);
                           com.mlc.mlcbot.combat.CombatRuntime.runTaskLater(this.fs.plugin(), () -> {
                              this.fr.u(var2);
                              var5.lt = false;
                           }, 1L);
                           return true;
                        }

                        var5.lt = false;
                        this.fr.h(var5);
                        return false;
                     }
                  }

                  if (this.fr.n(var2, var3, var4, var5, var6)) {
                     this.fr.u(var2);
                  }
               }

               if (this.fr.a(var2, var3, var5, var4, var6)) {
                  return false;
               } else {
                  if (this.fr.j(var5, var6)) {
                     Block var24 = var5.oi.getBlock();
                     if (var24.getType() == Material.OBSIDIAN && this.a(var24, var3, var5, var6)) {
                        Location var26 = var24.getLocation().add(0.5, 1.0, 0.5);
                        double var17 = var24.getY() + 1.0;
                        if (!this.fr.a(var17, var22)) {
                           this.fr.h(var5);
                        } else {
                           double var19 = var2.getEyeLocation().distance(var26);
                           if (var19 <= 4.5 && var19 >= 0.8) {
                              if (!this.fr.a(var2, var26, var4)) {
                                 this.fr.h(var5);
                              } else {
                                 bg var21 = this.fr.h(var2, var3, var24);
                                 if (var21.cS() && !this.b(var2, var3, var24, var21)) {
                                    if (this.a(var1, var2, var3, var24, var4, var5, var6)) {
                                       this.a(var5, var3, var24, var6);
                                       return true;
                                    }
                                 } else {
                                    this.b(var5, var24);
                                 }
                              }
                           }
                        }
                     }

                     this.fr.h(var5);
                  }

                  bk var25 = this.an(var2, var3, var4, var5, var6);
                  Block var27 = var25.dq();
                  if (var27 != null) {
                     double var30 = this.h(var2, var3, var4, var5, var6, var27);
                     return this.fr.a(var1, var2, var27, var3, var4, var5, var6);
                  } else {
                     String var28 = var25.dt();
                     if (this.fr.r(var2, var3, var4, var5, var6)) {
                        var28 = "TARGET_BASE_PRESSURE_NEEDS_RECOVERY:" + var28;
                     }

                     return false;
                  }
               }
            }
         }
      }
   }

   public Block am(Player var1, Player var2, ae var3, m1 var4, long var5) {
      return this.an(var1, var2, var3, var4, var5).dq();
   }

   private boolean a(NPC var1, Player var2, Player var3, ae var4, m1 var5, long var6, Block var8) {
      if (var8 != null && this.fr.d(var5, var4, var6) && this.fr.a(var2, Material.OBSIDIAN) && this.fr.a(var2, Material.END_CRYSTAL) && !var5.lz) {
         boolean var9 = this.fr.l(var2, var3);
         if (this.i(var2, var3, var5, var6)) {
            return false;
         } else if (var9 && this.fr.cx().t(var2, var3)) {
            return false;
         } else if (!var9 && this.fr.cx().b(var2, var3, var3.getLocation())) {
            return false;
         } else {
            this.fr.a(var5, var4, var3, var6);
            this.fr.h(var5, var6);
            this.a(var5, var3, var8, var6);
            this.fr.a(var2, var8.getLocation().add(0.5, 0.5, 0.5));
            this.fr.a(var2, Material.OBSIDIAN);
            if (this.o(var8)
               && this.fr.e(var2, var8)
               && this.fr.a(var8, var2, var3, var4, var9, false)
               && this.fr.a(var2, var3, var4, var8, var9 ? 0.01 : 0.5, false, var9, !var9)) {
               if (!com.mlc.mlcbot.combat.CombatRuntime.place(var2, var8, Material.OBSIDIAN)) return false;
               var2.swingMainHand();
               this.fr.c(var2, Material.OBSIDIAN);
               this.a(var5, var3, var8, var6);
               com.mlc.mlcbot.combat.CombatRuntime.runTaskLater(this.fs.plugin(), () -> {
                  this.fr.u(var2);
                  var5.lt = false;
               }, 1L);
               return true;
            } else {
               var5.lt = false;
               this.fr.h(var5);
               return false;
            }
         }
      } else {
         return false;
      }
   }

   public bk an(Player var1, Player var2, ae var3, m1 var4, long var5) {
      if (var1 == null || var2 == null || var3 == null || var4 == null) {
         return new bk(null, null, -999.0, "MISSING_CONTEXT", false);
      } else if (var1.isValid() && !var1.isDead() && var2.isValid() && !var2.isDead()) {
         if (!var1.getWorld().equals(var2.getWorld())) {
            return new bk(null, null, -999.0, "DIFFERENT_WORLD", false);
         } else if (!this.fr.a(var1, Material.END_CRYSTAL)) {
            return new bk(null, null, -999.0, "NO_END_CRYSTAL", false);
         } else {
            bk var7 = this.m(var1, var2, var4, var5);
            if (var7 != null) {
               return var7;
            } else {
               boolean var8 = this.l(var1, var2, var4, var5);
               if (!var8 && this.fr.cI() && !this.fr.b(var1, "crystal-placement-search", false)) {
                  return new bk(null, null, -999.0, "HIGH_LOAD_THROTTLED", false);
               } else {
                  bj var9 = new bj();
                  var9.fB = this.n(var8);
                  Block var10 = this.ao(var1, var2, var3, var4, var5);
                  if (var10 != null) {
                     bi var11 = this.j(var1, var2, var3, var4, var5, var10);
                     if (var11.dm()) {
                        double var12 = this.d(var10, var2);
                        if (!this.fr.l(var1, var2) || var12 <= 1.85) {
                           return this.a(var1, var2, var4, new bk(var10, null, Math.max(var11.do_val(), 2.25), "REUSABLE_PRESSURE_BASE", false));
                        }

                        this.a(var1, var2, var3, var4, var5, var10, 0.65, var9);
                     } else {
                        if (!this.fr.l(var1, var2)) {
                           return this.a(var1, var2, var4, new bk(null, var10, var11.do_val(), "LOCKED_SINGLE_PRESSURE_BASE:" + var11.cY(), var11.dp()));
                        }

                        this.q(var4);
                     }
                  }

                  Block var28 = this.fr.x(var2);
                  if (var28 != null) {
                     this.a(var1, var2, var3, var4, var5, var28, 2.5, var9);
                  }

                  Block var29 = this.fr.k(var1, var2);
                  if (var29 != null) {
                     this.a(var1, var2, var3, var4, var5, var29, 2.0, var9);
                  }

                  Location var13 = var2.getLocation();
                  int var14 = (int)Math.floor(var13.getY());
                  int var15 = var14 - 13;
                  Location var16 = var1.getEyeLocation();
                  int var17 = (int)Math.floor(var16.getY() - 4.5 - 1.1);
                  int var18 = (int)Math.ceil(var16.getY() + 4.5 - 0.85);
                  var15 = Math.max(var15, var17);
                  var14 = Math.min(var14, var18);
                  if (var15 <= var14) {
                     int var19 = var13.getBlockX();
                     int var20 = var13.getBlockZ();
                     int var21 = !var8 && this.fr.cI() ? (this.fr.L() >= 150 ? 2 : 3) : 4;

                     for (int var22 = -var21; var22 <= var21; var22++) {
                        for (int var23 = -var21; var23 <= var21; var23++) {
                           int var24 = var19 + var22;
                           int var25 = var20 + var23;

                           for (int var26 = var15; var26 <= var14; var26++) {
                              if (this.a(var1, var24, var26, var25)) {
                                 Block var27 = var13.getWorld().getBlockAt(var24, var26, var25);
                                 this.a(var1, var2, var3, var4, var5, var27, var9);
                              }
                           }
                        }
                     }
                  }

                  this.a(var1, var2, var3, var4, var5, var9);
                  if (var9.fx != null && var9.fy > (this.fr.l(var1, var2) ? -50.0 : 2.0)) {
                     return this.a(var1, var2, var4, new bk(var9.fx, null, var9.fy, "BEST_SCORE=" + var9.fy, false));
                  } else {
                     String var32;
                     if (var9.fw) {
                        var32 = var9.fa == null ? "CANDIDATE_REJECTED_SELF_DAMAGE_UNSAFE" : "CANDIDATE_REJECTED_SELF_DAMAGE_UNSAFE:" + var9.fa;
                     } else if (var9.fv != null) {
                        var32 = var9.fv;
                     } else if (var9.fz) {
                        var32 = "BEST_SCORE_TOO_LOW=" + var9.fy;
                     } else {
                        var32 = "NO_CANDIDATE_BASE";
                     }

                     return this.a(var1, var2, var4, new bk(null, var9.fA, var9.fy, var32, var9.fw));
                  }
               }
            }
         }
      } else {
         return new bk(null, null, -999.0, "INVALID_ENTITY", false);
      }
   }

   private void a(Player var1, Player var2, ae var3, m1 var4, long var5, Block var7, double var8, bj var10) {
      bi var11 = this.j(var1, var2, var3, var4, var5, var7);
      if (var11.dn()) {
         var10.fz = true;
         if (var11.dm()) {
            double var12 = var11.do_val() + var8;
            if (var12 > var10.fy) {
               var10.fy = var12;
               var10.fx = var7;
            }
         } else {
            if (var11.dp()) {
               var10.fw = true;
               if (var10.fa == null) {
                  var10.fa = var11.cY();
               }

               if (var10.fA == null) {
                  var10.fA = var7;
               }
            }

            if (var10.fv == null || this.i(var11.cY(), var10.fv)) {
               var10.fv = var11.cY();
               var10.fA = var7;
            }
         }
      }
   }

   private void a(Player var1, Player var2, ae var3, m1 var4, long var5, Block var7, bj var8) {
      bi var9 = this.i(var1, var2, var3, var4, var5, var7);
      if (var9.dn()) {
         var8.fz = true;
         if (var9.dm()) {
            this.a(var8, var7, var9.do_val());
         } else {
            if (var8.fv == null || this.i(var9.cY(), var8.fv)) {
               var8.fv = var9.cY();
               var8.fA = var7;
            }
         }
      }
   }

   private void a(Player var1, Player var2, ae var3, m1 var4, long var5, bj var7) {
      if (!var7.fC.isEmpty()) {
         for (bl var9 : var7.fC) {
            this.a(var1, var2, var3, var4, var5, var9.du(), 0.0, var7);
         }
      }
   }

   private void a(bj var1, Block var2, double var3) {
      for (int var5 = 0; var5 < var1.fC.size(); var5++) {
         bl var6 = var1.fC.get(var5);
         if (this.b(var6.du(), var2)) {
            if (var3 > var6.dv()) {
               var1.fC.set(var5, new bl(var2, var3));
            }

            return;
         }
      }

      if (var1.fC.size() < var1.fB) {
         var1.fC.add(new bl(var2, var3));
      } else {
         int var11 = -1;
         double var12 = Double.MAX_VALUE;

         for (int var8 = 0; var8 < var1.fC.size(); var8++) {
            double var9 = var1.fC.get(var8).dv();
            if (var9 < var12) {
               var12 = var9;
               var11 = var8;
            }
         }

         if (var11 >= 0 && var3 > var12) {
            var1.fC.set(var11, new bl(var2, var3));
         }
      }
   }

   private boolean i(String var1, String var2) {
      return this.bg(var1) > this.bg(var2);
   }

   private int bg(String var1) {
      if (var1 == null) {
         return 0;
      } else if (var1.startsWith("SELF_DAMAGE_UNSAFE")) {
         return 60;
      } else if (var1.startsWith("OUT_OF_REACH")) {
         return 50;
      } else if (var1.equals("NO_PLACEMENT_SPACE")) {
         return 45;
      } else if (var1.equals("LOS_FAIL")) {
         return 40;
      } else if (var1.equals("VERTICAL_INVALID")) {
         return 35;
      } else if (var1.equals("LOW_ENEMY_DAMAGE")) {
         return 30;
      } else {
         return var1.equals("BLOCKED_BY_PLAYER_POSITION") ? 25 : 10;
      }
   }

   public Block ao(Player var1, Player var2, ae var3, m1 var4, long var5) {
      if (var1 != null && var2 != null && var4 != null && var4.lT != null) {
         if (var4.lV != null && var4.lV.equals(var2.getUniqueId())) {
            long var7 = 6500L;
            if (var3 != null) {
               boolean var9 = this.fr.ab(var2);
               long var10 = this.fr.a(var3, var9);
               long var12 = this.fr.b(var3, var9);
               var7 = Math.max(var7, Math.max(var10, var12) + 260L);
            }

            if (var5 - var4.lU > var7) {
               this.p(var4);
               return null;
            } else {
               Block var17 = var4.lT.getBlock();
               if (this.q(var17) && var17.getWorld().equals(var2.getWorld())) {
                  Location var18 = var17.getLocation().add(0.5, 1.0, 0.5);
                  if (this.g(var1, var2, var3, var4, var5, var17)) {
                     this.q(var4);
                     return null;
                  } else {
                     double var11 = this.fr.d(var18, var2.getLocation());
                     double var13 = this.fr.l(var1, var2) ? 2.85 : 12.0;
                     if (var11 > var13) {
                        this.p(var4);
                        return null;
                     } else if (!this.fr.a(var1, var2, var17, var4, var5)) {
                        this.p(var4);
                        return null;
                     } else {
                        double var15 = var1.getEyeLocation().distance(var18);
                        if (!(var15 > 4.5) && !(var15 < 0.8)) {
                           if (!this.fr.g(var1.getEyeLocation(), var18)) {
                              this.p(var4);
                              return null;
                           } else {
                              return var17;
                           }
                        } else {
                           this.p(var4);
                           return null;
                        }
                     }
                  }
               } else {
                  this.p(var4);
                  return null;
               }
            }
         } else {
            this.p(var4);
            return null;
         }
      } else {
         return null;
      }
   }

   public boolean k(Block var1) {
      if (!this.p(var1)) {
         return false;
      } else {
         Location var2 = var1.getLocation().add(0.5, 1.0, 0.5);

         for (Entity var4 : var1.getWorld().getNearbyEntities(var2, 0.6, 0.9, 0.6)) {
            if (var4 instanceof EnderCrystal || var4 instanceof Player) {
               Location var5 = var4.getLocation();
               double var6 = Math.abs(var5.getY() - var2.getY());
               double var8 = Math.sqrt(Math.pow(var5.getX() - var2.getX(), 2.0) + Math.pow(var5.getZ() - var2.getZ(), 2.0));
               if (var6 < 1.2 && var8 < 0.6) {
                  return false;
               }
            }
         }

         return true;
      }
   }

   public boolean a(Block var1, Player var2, m1 var3, long var4) {
      if (!this.p(var1)) {
         return false;
      } else {
         Location var6 = var1.getLocation().add(0.5, 1.0, 0.5);

         for (Entity var8 : var1.getWorld().getNearbyEntities(var6, 0.6, 0.9, 0.6)) {
            if ((var8 instanceof EnderCrystal || var8 instanceof Player)
               && (!(var2 != null && var8 instanceof Player var9) || !var9.getUniqueId().equals(var2.getUniqueId()) || !this.a(var2, var3, var1, var4))) {
               Location var14 = var8.getLocation();
               double var10 = Math.abs(var14.getY() - var6.getY());
               double var12 = Math.sqrt(Math.pow(var14.getX() - var6.getX(), 2.0) + Math.pow(var14.getZ() - var6.getZ(), 2.0));
               if (var10 < 1.2 && var12 < 0.6) {
                  return false;
               }
            }
         }

         return true;
      }
   }

   private boolean p(Block var1) {
      if (var1 == null) {
         return false;
      } else {
         Block var2 = var1.getRelative(BlockFace.UP);
         Block var3 = var2.getRelative(BlockFace.UP);
         return var2.getType().isAir() && var3.getType().isAir();
      }
   }

   public boolean a(Player var1, m1 var2, Block var3, long var4) {
      if (var1 != null && var3 != null) {
         double var6 = var3.getY() + 1.0;
         return this.fr.b(var1, var2, var3, var4) ? true : !var1.isOnGround() && this.fr.v(var1) >= 0.45 && this.fr.a(var6, var1.getLocation().getY());
      } else {
         return false;
      }
   }

   private void b(m1 var1, Player var2, Block var3, long var4) {
      if (var1 != null && var3 != null && var3.getWorld() != null) {
         var1.lT = var3.getLocation().clone();
         var1.lU = var4;
         var1.lV = var2 == null ? null : var2.getUniqueId();
      }
   }

   private void p(m1 var1) {
      if (var1 != null) {
         var1.lT = null;
         var1.lU = 0L;
         var1.lV = null;
      }
   }

   private boolean g(Player var1, Player var2, ae var3, m1 var4, long var5, Block var7) {
      if (var1 == null || var2 == null || var4 == null || var7 == null) {
         return false;
      } else if (this.k(var1, var2, var3, var4, var5, var7)) {
         return false;
      } else if (!this.a(var7, var2, var4, var5)) {
         return true;
      } else if (!this.fr.a(var1, var2, var7, var4, var5)) {
         return true;
      } else {
         Location var8 = var7.getLocation().add(0.5, 1.0, 0.5);
         if (this.fr.l(var1, var2) && this.fr.d(var8, var2.getLocation()) > 2.85) {
            return true;
         } else {
            double var9 = var1.getEyeLocation().distance(var8);
            if (!(var9 > 4.5) && !(var9 < 0.8)) {
               if (!this.fr.g(var1.getEyeLocation(), var8)) {
                  return true;
               } else {
                  bg var11 = this.fr.h(var1, var2, var7);
                  return !var11.cS() || this.b(var1, var2, var7, var11);
               }
            } else {
               return true;
            }
         }
      }
   }

   private void q(m1 var1) {
      if (var1 != null) {
         this.p(var1);
         this.fr.h(var1);
         this.fr.i(var1);
         this.fr.l(var1);
         var1.lW = null;
         var1.lX = 0L;
         var1.lt = false;
         var1.lB = false;
         var1.mq = false;
      }
   }

   private void b(m1 var1, Block var2) {
      if (var1 != null && var2 != null) {
         if (var1.oi != null && this.b(var1.oi.getBlock(), var2)) {
            this.fr.h(var1);
         }

         if (var1.mw != null && this.b(var1.mw.getBlock(), var2)) {
            var1.mw = null;
            var1.mv = 0L;
         }

         if (var1.lT != null && this.b(var1.lT.getBlock(), var2)) {
            this.p(var1);
         }
      }
   }

   private double h(Player var1, Player var2, ae var3, m1 var4, long var5, Block var7) {
      return this.j(var1, var2, var3, var4, var5, var7).do_val();
   }

   private bi i(Player var1, Player var2, ae var3, m1 var4, long var5, Block var7) {
      if (var1 == null || var2 == null || var3 == null || var4 == null || var7 == null) {
         return new bi(false, -999.0, "MISSING_CONTEXT", false);
      } else if (!this.q(var7)) {
         return new bi(false, -999.0, "NO_CANDIDATE_BASE", false);
      } else if (!this.fr.a(var1, var2, var7, var4, var5)) {
         return new bi(true, -999.0, "VERTICAL_INVALID", false);
      } else if (this.a(var1, var2, var4, var5, var7)) {
         return new bi(true, -999.0, "WAITING_POST_MELEE_LIFT", false);
      } else {
         Location var8 = var7.getLocation().add(0.5, 1.0, 0.5);
         Location var9 = var1.getEyeLocation();
         double var10 = var8.getX() - var9.getX();
         double var12 = var8.getY() - var9.getY();
         double var14 = var8.getZ() - var9.getZ();
         double var16 = var10 * var10 + var12 * var12 + var14 * var14;
         if (var16 > 20.25 || var16 < 0.64) {
            return new bi(true, -999.0, "OUT_OF_REACH", false);
         } else if (this.fr.d(var1, var7) || this.fr.c(var7, var2)) {
            return new bi(true, -999.0, "BLOCKED_BY_PLAYER_POSITION", false);
         } else if (!this.p(var7)) {
            return new bi(true, -999.0, "NO_PLACEMENT_SPACE", false);
         } else {
            double var18 = this.fr.d(var8, var2.getLocation());
            double var20 = var2.getLocation().getY() - (var7.getY() + 1.0);
            double var22 = 16.0 - var18 * 3.0 - Math.abs(var20) * 0.85 - Math.sqrt(var16) * 0.2;
            if (var20 <= 1.0) {
               var22 += 4.0;
            }

            if (this.fr.l(var1, var2)) {
               var22 += var18 <= 1.85 ? 4.0 : 1.0;
            }

            if (this.fr.a(var1, var8, var3)) {
               var22 += 0.35;
            }

            return new bi(true, var22, "FAST_ACCEPTED", false);
         }
      }
   }

   private bi j(Player var1, Player var2, ae var3, m1 var4, long var5, Block var7) {
      if (var1 == null || var2 == null || var3 == null || var4 == null || var7 == null) {
         return new bi(false, -999.0, "MISSING_CONTEXT", false);
      } else if (!this.q(var7)) {
         return new bi(false, -999.0, "NO_CANDIDATE_BASE", false);
      } else {
         Location var8 = var7.getLocation().add(0.5, 1.0, 0.5);
         double var9 = var7.getY() + 1.0;
         if (!this.fr.a(var1, var2, var7, var4, var5)) {
            return new bi(true, -999.0, "VERTICAL_INVALID", false);
         } else if (this.a(var1, var2, var4, var5, var7)) {
            return new bi(true, -999.0, "WAITING_POST_MELEE_LIFT", false);
         } else {
            double var11 = var1.getEyeLocation().distance(var8);
            if (var11 > 4.5 || var11 < 0.8) {
               return new bi(true, -999.0, "OUT_OF_REACH", false);
            } else if (!this.fr.d(var1, var7) && !this.fr.c(var7, var2)) {
               if (!this.fr.g(var1.getEyeLocation(), var8)) {
                  return new bi(true, -999.0, "LOS_FAIL", false);
               } else if (!this.a(var7, var2, var4, var5)) {
                  return new bi(true, -999.0, "NO_PLACEMENT_SPACE", false);
               } else {
                  bg var13 = this.fr.h(var1, var2, var7);
                  if (this.b(var1, var2, var7, var13)) {
                     return new bi(true, -999.0, "BAD_SELF_DAMAGE_PLACEMENT " + var13.cZ(), true);
                  } else if (!var13.cS()) {
                     boolean var25 = !var13.di() || !var13.dk();
                     return new bi(true, -999.0, "SELF_DAMAGE_UNSAFE " + var13.cZ(), var25);
                  } else {
                     double var14 = var13.dc();
                     double var16 = var13.dd();
                     double var18 = var2.getLocation().getY() - var9;
                     boolean var20 = this.fr.l(var1, var2) && this.fr.a(var1, var2, var7, var4, var5);
                     double var21 = var20 ? Math.min(0.05, this.g(var18)) : this.g(var18);
                     if (var14 < var21) {
                        return new bi(true, -999.0, "LOW_ENEMY_DAMAGE", false);
                     } else {
                        double var23 = var14 * var3.aI() * 2.0 - var16 * var3.aJ() * 2.0;
                        if (var20) {
                           var23 += 3.0;
                        }

                        if (var18 <= 1.0) {
                           var23 += 5.0;
                        }

                        if (var14 >= var2.getHealth()) {
                           var23 += 150.0;
                        }

                        if (this.fr.a(var1, var8, var3)) {
                           var23 += 0.35;
                        }

                        return new bi(true, var23, "ACCEPTED", false);
                     }
                  }
               }
            } else {
               return new bi(true, -999.0, "BLOCKED_BY_PLAYER_POSITION", false);
            }
         }
      }
   }

   private double d(Block var1, Player var2) {
      return var1 != null && var2 != null ? this.fr.d(var1.getLocation().add(0.5, 1.0, 0.5), var2.getLocation()) : Double.MAX_VALUE;
   }

   private boolean b(Player var1, Player var2, Block var3, bg var4) {
      if (var1 != null && var2 != null && var3 != null && var4 != null) {
         Location var5 = var3.getLocation().add(0.5, 1.0, 0.5);
         boolean var6 = this.b(var1, var2, var5, var3, var4.dd(), var4.dc());
         if (this.b(var1, var5, var3) && !var6) {
            return true;
         } else {
            double var7 = Math.sqrt(Math.pow(var1.getLocation().getX() - var5.getX(), 2.0) + Math.pow(var1.getLocation().getZ() - var5.getZ(), 2.0));
            double var9 = Math.abs(var1.getLocation().getY() - var5.getY());
            boolean var11 = var7 < 1.15 && var9 < 2.1;
            if (this.fr.d(var1, var3) && var4.dd() > 0.35) {
               return true;
            } else if (var6 && var4.di()) {
               return false;
            } else if (var11 && var4.dd() > 0.65) {
               return true;
            } else {
               boolean var12 = this.fr.l(var1, var2) && this.fr.f(var1, var2, var3);
               if (var12 && var4.cS()) {
                  return false;
               } else {
                  return var4.dc() < 0.75 && var4.dd() > 0.35 ? true : var4.dc() < var2.getHealth() && var4.dd() > Math.max(1.25, var4.dc() * 1.35);
               }
            }
         }
      } else {
         return true;
      }
   }

   private boolean b(Player var1, Player var2, Location var3, Block var4, double var5, double var7) {
      if (var1 != null && var2 != null && var3 != null && var4 != null && var4.getWorld() != null) {
         if (var1.getWorld().equals(var2.getWorld()) && var1.getWorld().equals(var4.getWorld())) {
            Location var9 = var1.getLocation();
            Location var10 = var2.getLocation();
            double var11 = var10.getY() - var9.getY();
            if (var11 < 0.75 || var11 > 1.45) {
               return false;
            } else if (var4.getY() != (int)Math.floor(var9.getY())) {
               return false;
            } else {
               double var13 = this.fr.d(var9, var3);
               if (var13 < 0.85 || var13 > 1.35) {
                  return false;
               } else if (this.fr.d(var10, var3) > 2.35) {
                  return false;
               } else {
                  double var15 = var1.getHealth() + Math.max(0.0, var1.getAbsorptionAmount());
                  return var5 >= Math.max(0.0, var15 - 0.01) ? false : var7 >= Math.max(0.75, var5 * 0.65);
               }
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private boolean b(Player var1, Location var2, Block var3) {
      if (var1 != null && var2 != null && var3 != null && var3.getWorld() != null) {
         Location var4 = var1.getLocation();
         if (!var4.getWorld().equals(var3.getWorld())) {
            return true;
         } else if (this.g(var1, var3)) {
            return true;
         } else {
            double var5 = this.fr.d(var4, var2);
            double var7 = var2.getY() - var4.getY();
            return var5 < 1.25 && var7 > -0.75 && var7 < 2.05;
         }
      } else {
         return true;
      }
   }

   private boolean g(Player var1, Block var2) {
      if (var1 != null && var2 != null && var2.getWorld() != null) {
         Location var3 = var1.getLocation();
         if (!var3.getWorld().equals(var2.getWorld())) {
            return true;
         } else {
            double var4 = var2.getX() + 0.5 - var3.getX();
            double var6 = var2.getZ() + 0.5 - var3.getZ();
            if (var4 * var4 + var6 * var6 > 0.81) {
               return false;
            } else {
               int var8 = var2.getY();
               int var9 = (int)Math.floor(var3.getY());
               return var8 >= var9 - 1 && var8 <= var9 + 1;
            }
         }
      } else {
         return true;
      }
   }

   private boolean q(Block var1) {
      return var1 != null && (var1.getType() == Material.OBSIDIAN || var1.getType() == Material.BEDROCK);
   }

   private boolean b(Block var1, Block var2) {
      return var1 != null && var2 != null
         ? var1.getWorld().equals(var2.getWorld()) && var1.getX() == var2.getX() && var1.getY() == var2.getY() && var1.getZ() == var2.getZ()
         : false;
   }

   private boolean k(Player var1, Player var2, ae var3, m1 var4, long var5, Block var7) {
      if (var1 != null && var2 != null && var3 != null && var7 != null) {
         Location var8 = var7.getLocation().add(0.5, 1.0, 0.5);

         for (Entity var10 : var7.getWorld().getNearbyEntities(var8, 0.7, 1.1, 0.7)) {
            if (var10 instanceof EnderCrystal var11 && this.fr.a(var1, var2, var3, var11, var4, var5)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public boolean a(NPC var1, Player var2, Block var3, Player var4, ae var5, m1 var6, long var7) {
      return this.a(var1, var2, var4, var3, var5, var6, var7);
   }
}
