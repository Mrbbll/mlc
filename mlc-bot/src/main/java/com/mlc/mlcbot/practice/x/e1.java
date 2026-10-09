// Extracted from the user-provided PracticeBot-Source; strategy logic retained.
package com.mlc.mlcbot.practice.x;

import com.mlc.mlcbot.practice.PracticeBotPlugin;
import com.mlc.mlcbot.practice.bridge.NPC;
import com.mlc.mlcbot.practice.bridge.FollowTrait;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

final class e1 {
   private final f0 is;

   e1(f0 var1) {
      this.is = var1;
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

   Vector d(Player var1, Player var2, m1 var3) {
      if (var1 == null) {
         return new Vector(1, 0, 0);
      } else {
         if (var2 != null) {
            Vector var4 = var2.getLocation().toVector().subtract(var1.getLocation().toVector()).setY(0);
            if (var4.lengthSquared() > 0.01) {
               return var4.normalize();
            }
         }

         if (var3 != null && var3.oQ != null) {
            Vector var5 = var3.oQ.clone().setY(0);
            if (var5.lengthSquared() > 0.01) {
               return var5.normalize();
            }
         }

         Vector var6 = var1.getLocation().getDirection().setY(0);
         return var6.lengthSquared() > 0.01 ? var6.normalize() : new Vector(1, 0, 0);
      }
   }

   Location c(Player var1, Player var2, m1 var3) {
      av var4 = this.is.bZ();
      if (var1 != null && var2 != null) {
         Location var5 = var1.getLocation();
         Location var6 = var2.getLocation();
         long var7 = System.currentTimeMillis();
         double var9 = var6.getY() - var5.getY();
         double var11 = var4.d(var5, var6);
         if (var9 <= 1.15) {
            if (var3 != null) {
               var3.overheadAvoidLockUntil = 0L;
            }

            return null;
         } else {
            boolean var13 = var3 != null && var7 < var3.overheadAvoidLockUntil;
            if (!(var11 >= 1.55) || var13 && var11 <= 2.55) {
               if (var3 != null) {
                  if (!var13) {
                     var3.overheadOrbitClockwise = var3.li;
                  }

                  var3.overheadAvoidLockUntil = var7 + 900L;
               }

               Vector var14 = var5.toVector().subtract(var6.toVector()).setY(0.0);
               double var15 = Math.sqrt(var14.getX() * var14.getX() + var14.getZ() * var14.getZ());
               if (var15 < 1.0E-4) {
                  var14 = this.a(var5, var6, var3);
               } else {
                  var14.normalize();
               }

               double var17 = var15 < 1.47 ? 2.2800000000000002 : 2.1;
               if (!(var15 >= var17 - 0.12)) {
                  return var6.clone().add(var14.clone().multiply(var17));
               } else {
                  Vector var19 = var3 != null && !var3.overheadOrbitClockwise
                     ? new Vector(var14.getZ(), 0.0, -var14.getX())
                     : new Vector(-var14.getZ(), 0.0, var14.getX());
                  if (var19.lengthSquared() < 1.0E-4) {
                     var19 = this.a(var5, var6, var3);
                  } else {
                     var19.normalize();
                  }

                  return var5.clone().add(var19.multiply(1.15));
               }
            } else {
               return null;
            }
         }
      } else {
         return null;
      }
   }

   Vector a(Location var1, Location var2, Vector var3) {
      if (var1 != null && var2 != null) {
         Vector var4 = var2.toVector().subtract(var1.toVector()).setY(0.0);
         if (var4.lengthSquared() > 0.01) {
            return var4.normalize();
         }
      }

      return var3 != null && var3.lengthSquared() > 0.01 ? var3.clone().setY(0.0).normalize() : new Vector(1.0, 0.0, 0.0);
   }

   boolean a(NPC var1, Player var2, Player var3, m1 var4, Location var5, double var6) {
      av var8 = this.is.bZ();
      if (var1 != null && var2 != null && var5 != null) {
         var1.getNavigator().cancelNavigation();
         FollowTrait var9 = (FollowTrait)var1.getTraitNullable(FollowTrait.class);
         if (var9 != null && var9.getFollowing() != null) {
            var9.follow(null);
         }

         Vector var10 = var5.toVector().subtract(var2.getLocation().toVector()).setY(0.0);
         if (var10.lengthSquared() < 1.0E-6) {
            return false;
         } else {
            long var11 = System.currentTimeMillis();
            if (var4 != null && this.is.a(var2, var10, var2.getWorld()) && var11 >= var4.overheadAvoidLockUntil - 120L) {
               var4.overheadOrbitClockwise = !var4.overheadOrbitClockwise;
               var4.overheadAvoidLockUntil = var11 + 900L;
               Location var13 = this.c(var2, var3, var4);
               if (var13 != null) {
                  var10 = var13.toVector().subtract(var2.getLocation().toVector()).setY(0.0);
                  if (var10.lengthSquared() < 1.0E-6) {
                     return false;
                  }
               }
            }

            Vector var19 = var10.normalize().multiply(var6);
            Vector var14 = var2.getVelocity();
            boolean var15 = var8.ag(var2);
            if (var15) {
               var14.setX(var14.getX() * 0.55 + var19.getX() * 0.82);
               var14.setZ(var14.getZ() * 0.55 + var19.getZ() * 0.82);
            } else {
               var14.setX(var14.getX() * 0.88 + var19.getX() * 0.26);
               var14.setZ(var14.getZ() * 0.88 + var19.getZ() * 0.26);
            }

            var2.setVelocity(var14);
            float var16 = var2.getLocation().getYaw();
            float var17 = var2.getLocation().getPitch();
            if (Math.abs(var17) > 1.5F) {
               float var18 = var17 * 0.55F;
               var2.setRotation(var16, var18);
            }

            if (var4 != null) {
               var4.kJ = System.currentTimeMillis();
               var4.oQ = var10.clone().setY(0.0).normalize();
               var4.oR = System.currentTimeMillis() + 250L;
            }

            return true;
         }
      } else {
         return false;
      }
   }

   Vector a(Location var1, Location var2, m1 var3) {
      if (var3 != null && var3.oQ != null) {
         Vector var4 = var3.oQ.clone().setY(0.0);
         if (var4.lengthSquared() > 1.0E-4) {
            return var4.normalize();
         }
      }

      Vector var6 = var2.getDirection().clone().setY(0.0);
      if (var6.lengthSquared() < 1.0E-4) {
         var6 = var1.getDirection().clone().setY(0.0);
      }

      if (var6.lengthSquared() < 1.0E-4) {
         var6 = new Vector(1.0, 0.0, 0.0);
      }

      var6.normalize();
      boolean var5 = var3 == null || var3.overheadOrbitClockwise;
      return var5 ? new Vector(-var6.getZ(), 0.0, var6.getX()) : new Vector(var6.getZ(), 0.0, -var6.getX());
   }

   boolean q(NPC var1, Player var2, Player var3, ae var4, m1 var5, long var6) {
      av var8 = this.is.bZ();
      PracticeBotPlugin var9 = this.is.ca();
      if (var8.f(var5)) {
         return false;
      } else if (var8.k(var5, var6) && !var8.l(var2, var3)) {
         return false;
      } else if (!var8.a(var2, Material.OBSIDIAN)) {
         return false;
      } else if (!var8.a(var2, Material.END_CRYSTAL)) {
         return false;
      } else if (!var8.d(var5, var4, var6)) {
         return false;
      } else if (var8.j(var5, var6)) {
         return false;
      } else if (var5.lz) {
         return false;
      } else {
         Location var10 = var2.getLocation();
         Location var11 = var3.getLocation();
         double var12 = var11.getY() - var10.getY();
         if (var12 < 0.8) {
            return false;
         } else if (!var8.ag(var2)) {
            return false;
         } else {
            double var14 = var8.d(var10, var11);
            if (var14 > 12.0) {
               return false;
            } else {
               Block var16 = null;
               double var17 = -999.0;
               double var19 = var11.getY();
               int var21 = (int)Math.floor(var10.getY());
               int var22 = Math.min(var21 + 1, (int)Math.floor(var19) - 1);
               int var23 = Math.max(var2.getWorld().getMinHeight(), var21 - 1);
               if (var22 < var23) {
                  return false;
               } else {
                  for (int var24 = var23; var24 <= var22; var24++) {
                     for (int var25 = -2; var25 <= 2; var25++) {
                        for (int var26 = -2; var26 <= 2; var26++) {
                           Block var27 = var2.getWorld().getBlockAt(var11.getBlockX() + var25, var24, var11.getBlockZ() + var26);
                           if (this.o(var27)
                              && var8.a(var27, var2, var3, var4, true)
                              && var8.e(var2, var27)
                              && var8.a(var2, var3, var4, var27, 0.5, false, true, true)) {
                              double var28 = var2.getEyeLocation().distance(var27.getLocation().add(0.5, 0.5, 0.5));
                              if (!(var28 > 4.5) && !(var28 < 0.5)) {
                                 Block var30 = var27.getRelative(BlockFace.UP);
                                 Block var31 = var30.getRelative(BlockFace.UP);
                                 if (var30.getType().isAir() && var31.getType().isAir()) {
                                    Location var32 = var27.getLocation().add(0.5, 1.0, 0.5);
                                    double var33 = var27.getY() + 1.0;
                                    if (var8.a(var2, var3, var27, var5, var6) || var8.a(var33, var19)) {
                                       double var35 = var8.b(var32, var3);
                                       double var37 = var8.b(var32, var2);
                                       if (!(var35 < (var8.l(var2, var3) ? 0.01 : 0.5))
                                          && var8.a(var37, var2.getHealth() + Math.max(0.0, var2.getAbsorptionAmount()), var35)) {
                                          double var39 = Math.sqrt(
                                             Math.pow(var27.getX() + 0.5 - var11.getX(), 2.0) + Math.pow(var27.getZ() + 0.5 - var11.getZ(), 2.0)
                                          );
                                          double var41 = var35 * 4.0 - var37 * 3.0 - var39 * 2.0 - Math.max(0, var27.getY() - var21) * 1.25;
                                          if (var39 < 1.0) {
                                             var41 += 10.0;
                                          }

                                          if (var41 > var17) {
                                             var17 = var41;
                                             var16 = var27;
                                          }
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }

                  if (var16 != null && !(var17 < (var8.l(var2, var3) ? -50.0 : 2.0))) {
                     var8.a(var5, var4, var3, var6);
                     var8.h(var5, var6);
                     var5.mv = var6 + 600L;
                     var5.mw = var16.getLocation().add(0.5, 0.5, 0.5);
                     var8.c(var5, var16, var6);
                     var8.c(var1, var2, var16.getLocation().add(0.5, 0.5, 0.5), var5);
                     var8.a(var2, Material.OBSIDIAN);
                     if (this.o(var16)
                        && var8.e(var2, var16)
                        && var8.a(var16, var2, var3, var4, true)
                        && var8.a(var2, var3, var4, var16, 0.5, false, true, true)) {
                        if (!com.mlc.mlcbot.combat.CombatRuntime.place(var2, var16, Material.OBSIDIAN)) return false;
                        var2.swingMainHand();
                        var8.c(var2, Material.OBSIDIAN);
                        com.mlc.mlcbot.combat.CombatRuntime.runTaskLater(var9.plugin(), () -> {
                           var8.u(var2);
                           var5.lt = false;
                        }, 1L);
                        return true;
                     } else {
                        var5.mv = 0L;
                        var5.mw = null;
                        var8.h(var5);
                        var5.lt = false;
                        return false;
                     }
                  } else {
                     return false;
                  }
               }
            }
         }
      }
   }

   boolean r(NPC var1, Player var2, Player var3, ae var4, m1 var5, long var6) {
      av var8 = this.is.bZ();
      PracticeBotPlugin var9 = this.is.ca();
      if (var8.f(var5)) {
         return false;
      } else if (var8.k(var5, var6) && !var8.l(var2, var3)) {
         return false;
      } else if (!var8.a(var2, Material.OBSIDIAN)) {
         return false;
      } else if (!var8.a(var2, Material.END_CRYSTAL)) {
         return false;
      } else if (!var8.d(var5, var4, var6)) {
         return false;
      } else if (var8.j(var5, var6)) {
         return false;
      } else if (var5.lz) {
         return false;
      } else {
         Location var10 = var3.getLocation();
         Location var11 = var2.getLocation();
         double var12 = var11.distance(var10);
         if (!(var12 > 4.5) && !(var12 < 1.0)) {
            if (var8.a(var2, var3, var4, var5, var6, var10)) {
               return false;
            } else {
               Block var14 = null;
               double var15 = -999.0;
               double var17 = var10.getY();
               int var19 = (int)Math.floor(var11.getY());
               int var20 = (int)Math.floor(var17);
               int var21 = var3.isOnGround() && Math.abs(var20 - var19) <= 1 ? 0 : -1;

               for (int var22 = var21; var22 <= 0; var22++) {
                  for (int var23 = -1; var23 <= 1; var23++) {
                     for (int var24 = -1; var24 <= 1; var24++) {
                        Location var25 = var10.clone().add(var23, var22, var24);
                        Block var26 = var25.getBlock();
                        if (this.o(var26)
                           && var8.e(var2, var26)
                           && var8.a(var26, var2, var3, var4, false)
                           && var8.a(var2, var3, var4, var26, 1.0, false, false, true)) {
                           double var27 = var2.getEyeLocation().distance(var26.getLocation().add(0.5, 0.5, 0.5));
                           if (!(var27 > 4.5) && !(var27 < 0.5)) {
                              Block var29 = var26.getRelative(BlockFace.UP);
                              Block var30 = var29.getRelative(BlockFace.UP);
                              if (var29.getType().isAir() && var30.getType().isAir()) {
                                 Location var31 = var26.getLocation().add(0.5, 1.0, 0.5);
                                 double var32 = var26.getY() + 1.0;
                                 if (var8.a(var2, var3, var26, var5, var6) || var8.a(var32, var17)) {
                                    double var34 = var8.b(var31, var3);
                                    double var36 = var8.b(var31, var2);
                                    if (!(var34 < 1.0) && var8.a(var36, var2.getHealth() + Math.max(0.0, var2.getAbsorptionAmount()), var34)) {
                                       double var38 = Math.sqrt(var23 * var23 + var24 * var24);
                                       double var40 = var34 * 3.0 - var36 * 2.0 - var38;
                                       if (var40 > var15) {
                                          var15 = var40;
                                          var14 = var26;
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }

               if (var14 != null && !(var15 < 1.0)) {
                  var8.a(var5, var4, var3, var6);
                  var8.h(var5, var6);
                  var5.mv = var6 + 600L;
                  var5.mw = var14.getLocation().add(0.5, 0.5, 0.5);
                  var8.c(var5, var14, var6);
                  var8.c(var1, var2, var14.getLocation().add(0.5, 0.5, 0.5), var5);
                  var8.a(var2, Material.OBSIDIAN);
                  if (this.o(var14)
                     && var8.e(var2, var14)
                     && var8.a(var14, var2, var3, var4, false)
                     && var8.a(var2, var3, var4, var14, 1.0, false, false, true)) {
                     if (!com.mlc.mlcbot.combat.CombatRuntime.place(var2, var14, Material.OBSIDIAN)) return false;
                     var2.swingMainHand();
                     var8.c(var2, Material.OBSIDIAN);
                     com.mlc.mlcbot.combat.CombatRuntime.runTaskLater(var9.plugin(), () -> {
                        var8.u(var2);
                        var5.lt = false;
                     }, 1L);
                     return true;
                  } else {
                     var5.mv = 0L;
                     var5.mw = null;
                     var8.h(var5);
                     var5.lt = false;
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
   }

   boolean b(Player var1, Player var2, Vector var3) {
      av var4 = this.is.bZ();
      if (var2 != null && var1 != null && var3 != null) {
         if (var3.lengthSquared() < 0.01) {
            return false;
         } else {
            Location var5 = var1.getLocation();
            Location var6 = var2.getLocation();
            if (var5 != null && var6 != null) {
               if (var6.getY() >= var5.getY() - 0.5) {
                  return false;
               } else {
                  Vector var7 = var6.toVector().subtract(var5.toVector());
                  var7.setY(0);
                  if (var7.lengthSquared() < 0.01) {
                     return false;
                  } else {
                     Vector var8 = var3.clone().setY(0).normalize();
                     Vector var9 = var7.clone().normalize();
                     double var10 = var8.dot(var9);
                     if (var10 < 0.5) {
                        return false;
                     } else {
                        double var12 = var4.d(var5, var6);
                        return var12 < 5.0;
                     }
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
}
