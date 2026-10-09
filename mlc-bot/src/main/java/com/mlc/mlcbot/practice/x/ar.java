// Extracted from the user-provided PracticeBot-Source; strategy logic retained.
package com.mlc.mlcbot.practice.x;

import com.mlc.mlcbot.practice.bridge.NPC;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

final class ar {
   private static final int[] ce = new int[]{0, 1, -1, 2, 3};
   private final as cf;

   ar(as var1) {
      this.cf = var1;
   }

   private boolean b(m1 var1, Block var2, long var3) {
      if (var1 != null && var2 != null && var1.mC != null) {
         return var3 <= var1.mD && var1.mC.getWorld() != null && var2.getWorld() != null && var1.mC.getWorld().equals(var2.getWorld())
            ? var1.mC.getBlockX() == var2.getX() && var1.mC.getBlockY() == var2.getY() && var1.mC.getBlockZ() == var2.getZ()
            : false;
      } else {
         return false;
      }
   }

   private boolean a(m1 var1, Block var2, long var3) {
      if (var1 != null && var2 != null && var1.mE != null) {
         return var3 <= var1.mF && var1.mE.getWorld() != null && var2.getWorld() != null && var1.mE.getWorld().equals(var2.getWorld())
            ? var1.mE.getBlockX() == var2.getX() && var1.mE.getBlockY() == var2.getY() && var1.mE.getBlockZ() == var2.getZ()
            : false;
      } else {
         return false;
      }
   }

   private boolean a(Player var1, Player var2, m1 var3, Block var4, long var5) {
      return (this.b(var3, var4, var5) || this.a(var3, var4, var5)) && this.cf.c(var1, var2, var4);
   }

   boolean a(NPC var1, Player var2, Player var3, ae var4, m1 var5, long var6) {
      av var8 = this.cf.bZ();
      if (!var8.j(var4)) {
         return false;
      } else if (!var5.mG && !var5.mH) {
         if (!this.cf.e(var2.getWorld())) {
            return false;
         } else {
            boolean var9 = this.b(var2, var3, var5, var6);
            boolean var10 = var5 != null && (var5.mT >= 3 || var6 <= var5.mW || var9);
            if (!var10) {
               if (var8.k(var5, var6)) {
                  return false;
               }

               if (var8.s(var2, var3, var4, var5, var6)) {
                  return false;
               }

               if (var8.g(var5, var6)) {
                  return false;
               }
            } else {
               var8.k(var5);
               var8.i(var5);
               var8.h(var5);
               var5.lt = false;
               var5.mq = false;
            }

            var8.aj(var2);
            boolean var11 = var8.b(var2, Material.RESPAWN_ANCHOR) >= 1 && var8.b(var2, Material.GLOWSTONE) >= 1;
            if (!var11 && !this.c(var2, var3, var5, var6)) {
               return false;
            } else {
               long var12 = var9 ? 35L : Math.max(110L, var8.a(var4, var8.ab(var3)));
               if (var6 - var5.my < var12) {
                  return false;
               } else {
                  bw var14 = this.cf.b(var2, var3, var4, var5, var6);
                  if (var14 == null) {
                     return false;
                  } else {
                     double var15 = this.cf.a(var2, var3, var4, var5, var6);
                     return !var10 && !this.cf.a(var14, var15, var2, var3, var4, var5, var6) ? false : this.cf.a(var1, var2, var3, var4, var5, var14, var6);
                  }
               }
            }
         }
      } else {
         return false;
      }
   }

   boolean b(NPC var1, Player var2, Player var3, ae var4, m1 var5, long var6) {
      av var8 = this.cf.bZ();
      if (!var8.j(var4)) {
         return false;
      } else if (var5 != null && !var5.mG && !var5.mH) {
         if (!this.cf.e(var2.getWorld())) {
            return false;
         } else {
            boolean var9 = this.b(var2, var3, var5, var6);
            boolean var10 = var9 || var6 <= var5.mW;
            if (!var10 && var6 - var5.my < 35L) {
               return false;
            } else if (!var10 && !var8.b(var2, "charged-anchor", false)) {
               return false;
            } else {
               bw var11 = this.c(var2, var3, var4, var5, var6);
               if (var11 == null) {
                  return false;
               } else {
                  var8.k(var5);
                  var8.i(var5);
                  var8.h(var5);
                  var5.lt = false;
                  var5.mq = false;
                  return this.cf.a(var1, var2, var3, var4, var5, var11, var6);
               }
            }
         }
      } else {
         return false;
      }
   }

   bw b(Player var1, Player var2, ae var3, m1 var4, long var5) {
      av var7 = this.cf.bZ();
      boolean var8 = var4 != null && (var4.mT >= 3 || var5 <= var4.mW);
      if (!var8 && !this.e(var1, var2, var4, var5)) {
         return null;
      } else {
         Location var9 = var2.getLocation();
         int var10 = var9.getBlockX();
         int var11 = (int)Math.floor(var9.getY());
         int var12 = var9.getBlockZ();
         boolean var13 = this.cf.r(var2) || this.cf.s(var2);
         boolean var14 = this.cf.f(var1, var2);
         boolean var15 = var7.b(var1, var4) || var1.getHealth() <= var1.getAttribute(Attribute.MAX_HEALTH).getValue() * 0.38;
         boolean var16 = var5 < var4.mA;
         bw var17 = null;
         double var18 = -999.0;
         bw var20 = this.a(var1, var2, var3, var4, var5, var13, var14, var15, var16);
         if (var20 == null || !var8 && !(var20.do_val() > -25.0)) {
            boolean var21 = var8 || var13 || var14 || var15 || var16 || var7.d(var1.getLocation(), var9) <= 3.2;
            int var22 = this.a(var21, var8);
            int var23 = 0;

            for (int var24 = 0; var24 <= 3; var24++) {
               for (int var25 = -var24; var25 <= var24; var25++) {
                  for (int var26 = -var24; var26 <= var24; var26++) {
                     if (Math.max(Math.abs(var25), Math.abs(var26)) == var24) {
                        for (int var30 : ce) {
                           Block var31 = var2.getWorld().getBlockAt(var10 + var25, var11 + var30, var12 + var26);
                           Location var32 = var31.getLocation().add(0.5, 0.5, 0.5);
                           double var33 = var1.getEyeLocation().distance(var32);
                           if (!(var33 > 4.15) && !(var33 < 0.95) && var7.a(var1, var32, var3)) {
                              if (++var23 <= var22) {
                                 boolean var35 = var31.getType() == Material.RESPAWN_ANCHOR;
                                 int var36 = 0;
                                 if (var35) {
                                    if (!this.a(var1, var2, var4, var31, var5)) {
                                       continue;
                                    }

                                    var36 = this.cf.a(var31, 1, 1);
                                 } else {
                                    var36 = this.cf.a(var31, 1, 1);
                                    if (var36 >= 1 || this.cf.a(var31, 3, 2) >= 1 || !this.cf.b(var1, var2, var31)) {
                                       continue;
                                    }
                                 }

                                 double var37 = this.cf.a(var32, var2);
                                 double var39 = this.cf.a(var32, var1);
                                 Block var41 = this.cf.e(var1, var2, var31);
                                 boolean var42 = this.cf.a(var39, var1.getHealth(), var37, var15);
                                 double var43 = var39 * 0.48;
                                 boolean var45 = var41 != null && this.cf.a(var43, var1.getHealth(), var37, var15);
                                 boolean var46 = this.cf.a(var1, var2, var3, var4, var5, var31, var37);
                                 boolean var47 = !var8 && this.cf.a(var39, var43, var37, var42, var45, var15, var46);
                                 if ((var8 || var42 || var45)
                                    && (!var47 || var41 != null)
                                    && (!var47 || var7.a(var1, var41.getLocation().add(0.5, 0.5, 0.5), var3))) {
                                    double var48 = !var46 && !var13 && !(var7.d(var1.getLocation(), var9) <= 3.1) ? 1.9 : 1.25;
                                    if (var8 || !(var37 < var48)) {
                                       double var50 = var47 ? var43 : var39;
                                       double var52 = var7.d(var32, var9);
                                       double var54 = var37 * var3.aI() * 2.4 - var50 * var3.aJ() * 2.1 - var52 * 0.45;
                                       if (var13) {
                                          var54 += 6.5;
                                       }

                                       if (var14) {
                                          var54 += 5.5;
                                       }

                                       if (var7.d(var1.getLocation(), var9) <= 3.1) {
                                          var54 += 3.0;
                                       }

                                       if (var16) {
                                          var54 += 2.75;
                                       }

                                       if (var15 && var47 && var50 <= 3.5) {
                                          var54 += 3.0;
                                       }

                                       if (var15 && !var47 && var50 <= 4.5) {
                                          var54++;
                                       }

                                       if (var46) {
                                          var54 += 6.0;
                                       }

                                       if (!var47) {
                                          var54++;
                                       }

                                       if (var35) {
                                          var54 += this.cf.g(var31) ? 24.0 : 14.0;
                                       }

                                       if (var35 && var36 >= 2) {
                                          var54 += 4.0;
                                       }

                                       if (var31.getY() >= var11 && var31.getY() <= var11 + 1) {
                                          var54++;
                                       }

                                       if (var37 >= var2.getHealth()) {
                                          var54 += 180.0;
                                       }

                                       if (var54 > var18) {
                                          var18 = var54;
                                          var17 = new bw(var41, var31, var32, var37, var50, var54, var15, var16, var13, var47, var46);
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

            if (var17 == null) {
               return null;
            } else {
               double var56 = var17.dL() ? 0.75 : (!var14 && !var17.dJ() && !var17.dH() && !var17.dI() ? 2.0 : 1.0);
               return !var8 && !(var17.do_val() > var56) ? null : var17;
            }
         } else {
            return var20;
         }
      }
   }

   private boolean b(Player var1, Player var2, m1 var3, long var4) {
      if (var1 != null && var2 != null && var3 != null && var3.mC != null) {
         if (var4 <= var3.mD && var3.mC.getWorld() != null && var1.getWorld().equals(var3.mC.getWorld())) {
            Block var6 = var3.mC.getBlock();
            return var6.getType() == Material.RESPAWN_ANCHOR && this.a(var1, var2, var3, var6, var4);
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private boolean c(Player var1, Player var2, m1 var3, long var4) {
      if (var1 != null && var2 != null && var2.getWorld() != null && var1.getWorld().equals(var2.getWorld())) {
         Location var6 = var2.getLocation();
         int var7 = var6.getBlockX();
         int var8 = (int)Math.floor(var6.getY());
         int var9 = var6.getBlockZ();

         for (int var10 = -4; var10 <= 4; var10++) {
            for (int var11 = -4; var11 <= 4; var11++) {
               for (int var12 = -2; var12 <= 4; var12++) {
                  Block var13 = var2.getWorld().getBlockAt(var7 + var10, var8 + var12, var9 + var11);
                  if (var13.getType() == Material.RESPAWN_ANCHOR && this.a(var1, var2, var3, var13, var4)) {
                     return true;
                  }
               }
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private boolean d(Player var1, Player var2, m1 var3, long var4) {
      if (var1 != null && var2 != null && var2.getWorld() != null && var1.getWorld().equals(var2.getWorld())) {
         Location var6 = var2.getLocation();
         int var7 = var6.getBlockX();
         int var8 = (int)Math.floor(var6.getY());
         int var9 = var6.getBlockZ();

         for (int var10 = -4; var10 <= 4; var10++) {
            for (int var11 = -4; var11 <= 4; var11++) {
               for (int var12 = -2; var12 <= 4; var12++) {
                  Block var13 = var2.getWorld().getBlockAt(var7 + var10, var8 + var12, var9 + var11);
                  if (var13.getType() == Material.RESPAWN_ANCHOR && this.cf.g(var13) && this.a(var1, var2, var3, var13, var4)) {
                     return true;
                  }
               }
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private bw c(Player var1, Player var2, ae var3, m1 var4, long var5) {
      boolean var7 = this.cf.r(var2) || this.cf.s(var2);
      boolean var8 = this.cf.f(var1, var2);
      boolean var9 = this.cf.bZ().b(var1, var4) || var1.getHealth() <= var1.getAttribute(Attribute.MAX_HEALTH).getValue() * 0.38;
      return this.a(var1, var2, var3, var4, var5, var7, var8, var9, true);
   }

   private bw a(Player var1, Player var2, ae var3, m1 var4, long var5, boolean var7, boolean var8, boolean var9, boolean var10) {
      boolean var11 = var4 != null && (var4.mT >= 3 || var5 <= var4.mW);
      Location var12 = var2.getLocation();
      int var13 = var12.getBlockX();
      int var14 = (int)Math.floor(var12.getY());
      int var15 = var12.getBlockZ();
      bw var16 = null;
      double var17 = -999.0;

      for (int var19 = -4; var19 <= 4; var19++) {
         for (int var20 = -4; var20 <= 4; var20++) {
            for (int var21 = -2; var21 <= 4; var21++) {
               Block var22 = var2.getWorld().getBlockAt(var13 + var19, var14 + var21, var15 + var20);
               if (var22.getType() == Material.RESPAWN_ANCHOR && this.a(var1, var2, var4, var22, var5)) {
                  Location var23 = var22.getLocation().add(0.5, 0.5, 0.5);
                  boolean var24 = this.b(var4, var22, var5);
                  double var25 = this.cf.a(var23, var2);
                  double var27 = this.cf.a(var23, var1);
                  boolean var29 = this.cf.g(var22);
                  boolean var30 = this.cf.a(var27, var1.getHealth(), var25, var9);
                  Block var31 = var29 ? null : this.cf.e(var1, var2, var22);
                  double var32 = var27 * 0.48;
                  boolean var34 = !var29 && var31 != null && this.cf.a(var32, var1.getHealth(), var25, var9);
                  if (var11 || var30 || var34) {
                     boolean var35 = this.cf.a(var1, var2, var3, var4, var5, var22, var25);
                     boolean var36 = !var11 && !var29 && this.cf.a(var27, var32, var25, var30, var34, var9, var35);
                     if (!var36 || var31 != null) {
                        double var37 = !var35 && !var7 && !(this.cf.bZ().d(var1.getLocation(), var12) <= 3.1) ? 1.25 : 0.9375;
                        if (var11 || var24 || !(var25 < var37) || var29) {
                           double var39 = var36 ? var32 : var27;
                           double var41 = this.cf.bZ().d(var23, var12);
                           double var43 = var25 * var3.aI() * 2.4 - var39 * var3.aJ() * 2.1 - var41 * 0.45 + (var29 ? 96.0 : 20.0) + (var24 ? 140.0 : 0.0);
                           if (var7) {
                              var43 += 6.5;
                           }

                           if (var8) {
                              var43 += 5.5;
                           }

                           if (var10) {
                              var43 += 4.0;
                           }

                           if (var9 && var39 <= 4.5) {
                              var43 += 2.5;
                           }

                           if (var35) {
                              var43 += 6.0;
                           }

                           if (var25 >= var2.getHealth()) {
                              var43 += 180.0;
                           }

                           if (var43 > var17) {
                              var17 = var43;
                              var16 = new bw(var31, var22, var23, var25, var39, var43, var9, var10, var7, var36, var35);
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      return var16;
   }

   private int a(boolean var1, boolean var2) {
      int var3 = this.cf.bZ().L();
      if (var2) {
         if (var3 >= 150) {
            return 96;
         } else if (var3 >= 100) {
            return 128;
         } else {
            return var3 >= 35 ? 180 : 245;
         }
      } else if (var3 < 10) {
         return 245;
      } else if (var1) {
         if (var3 >= 150) {
            return 42;
         } else if (var3 >= 100) {
            return 54;
         } else if (var3 >= 35) {
            return 80;
         } else {
            return var3 >= 20 ? 120 : 245;
         }
      } else if (var3 >= 150) {
         return 18;
      } else if (var3 >= 100) {
         return 24;
      } else if (var3 >= 35) {
         return 36;
      } else {
         return var3 >= 20 ? 56 : 112;
      }
   }

   boolean e(Player var1, Player var2, m1 var3, long var4) {
      av var6 = this.cf.bZ();
      double var7 = var6.d(var1.getLocation(), var2.getLocation());
      return var7 <= 4.6 || this.cf.r(var2) || this.cf.s(var2) || this.cf.f(var1, var2) || var6.b(var1, var3) || var4 < var3.mA || var2.getHealth() <= 10.0;
   }
}
