// Extracted from the user-provided PracticeBot-Source; strategy logic retained.
package com.mlc.mlcbot.practice.x;

import static com.mlc.mlcbot.nms.EntityGroundState.isOnGround;

import java.util.HashSet;
import com.mlc.mlcbot.practice.bridge.NPC;
import com.mlc.mlcbot.practice.bridge.FollowTrait;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/** 分析前方障碍、地形和移动通道。 原源码：x/d0.java。 */
final class CpvpTerrainAnalyzer {
   private final CpvpTerrainController id;

   CpvpTerrainAnalyzer(CpvpTerrainController var1) {
      this.id = var1;
   }

   private boolean r(Block var1) {
      if (var1 == null) {
         return true;
      } else {
         Material var2 = var1.getType();
         return var2 == Material.RESPAWN_ANCHOR
            || var2 == Material.CACTUS
            || var2 == Material.MAGMA_BLOCK
            || var2 == Material.CAMPFIRE
            || var2 == Material.SOUL_CAMPFIRE;
      }
   }

   Block a(Player var1, Vector var2, World var3, double var4) {
      if (var1 != null && var3 != null && var2 != null && !(var2.lengthSquared() < 0.01)) {
         Location var6 = var1.getLocation();
         Vector var7 = var2.clone().setY(0);
         if (var7.lengthSquared() < 0.01) {
            return null;
         } else {
            var7.normalize();
            Location var8 = var6.clone().add(var7.multiply(var4));
            int var9 = var8.getBlockX();
            int var10 = var8.getBlockZ();
            return var9 == var6.getBlockX() && var10 == var6.getBlockZ() ? null : var3.getBlockAt(var9, var6.getBlockY(), var10);
         }
      } else {
         return null;
      }
   }

   boolean f(Player var1, Block var2) {
      if (var1 == null || var2 == null) {
         return false;
      } else if (!var2.getType().isSolid()) {
         return false;
      } else {
         Block var3 = var2.getRelative(BlockFace.UP);
         Block var4 = var3.getRelative(BlockFace.UP);
         if (!var3.getType().isSolid() && !var4.getType().isSolid()) {
            double var5 = var2.getY() + 1.0;
            double var7 = var5 - var1.getLocation().getY();
            return var7 >= 0.3 && var7 <= 1.0;
         } else {
            return false;
         }
      }
   }

   boolean a(Player var1, Vector var2, World var3) {
      CpvpCombatController var4 = this.id.bZ();
      if (!var4.ag(var1)) {
         return false;
      } else {
         Block var5 = this.a(var1, var2, var3, 0.35);
         return var5 != null && var4.h(var5.getType()) && this.f(var1, var5);
      }
   }

   boolean o(Player var1, Player var2) {
      if (var1 == null || var2 == null) {
         return false;
      } else if (!isOnGround(var2)) {
         return false;
      } else {
         double var3 = var2.getLocation().getY() - var1.getLocation().getY();
         return var3 >= 0.55 && var3 <= 1.35;
      }
   }

   boolean v(Player var1, Player var2) {
      if (var1 == null || var2 == null) {
         return false;
      } else if (!isOnGround(var2)) {
         return false;
      } else {
         double var3 = var2.getLocation().getY() - var1.getLocation().getY();
         return var3 > 1.35;
      }
   }

   boolean b(Player var1, Vector var2, World var3) {
      CpvpCombatController var4 = this.id.bZ();
      if (var2 == null || var2.lengthSquared() < 0.01) {
         return false;
      } else if (var3 != null && var4.ag(var1)) {
         Vector var5 = var2.clone().setY(0);
         if (var5.lengthSquared() < 0.01) {
            return false;
         } else {
            var5.normalize();
            Block var6 = this.a(var1, var5, var3, 0.5);
            if (var6 == null) {
               return false;
            } else {
               return this.r(var6) ? false : this.f(var1, var6);
            }
         }
      } else {
         return false;
      }
   }

   Location l(Block var1) {
      return var1 == null ? null : var1.getLocation().add(0.5, 1.0, 0.5);
   }

   Block a(Player var1, Player var2, Vector var3, World var4) {
      CpvpCombatController var5 = this.id.bZ();
      if (var1 == null || var2 == null || var4 == null || var3 == null || var3.lengthSquared() < 0.01) {
         return null;
      } else if (!var5.ag(var1)) {
         return null;
      } else {
         double var6 = var2.getLocation().getY() - var1.getLocation().getY();
         if (!this.id.e(var1, var3) && !var5.d(var1, var3, var4)) {
            Location var8 = var1.getLocation();
            Location var9 = var2.getLocation();
            Vector var10 = var9.toVector().subtract(var8.toVector()).setY(0);
            if (var10.lengthSquared() < 0.04) {
               return null;
            } else {
               boolean var11 = isOnGround(var2) && var6 >= 0.55 && var6 <= 1.35;
               Vector var12 = var10.clone().normalize();
               Vector var13 = new Vector(-var12.getZ(), 0.0, var12.getX());
               double var14 = var10.lengthSquared();
               Block var16 = null;
               double var17 = Double.MAX_VALUE;
               HashSet var19 = new HashSet();
               double[] var20 = new double[]{0.35, 0.5, 0.65, 0.8};

               for (double var24 : var20) {
                  Block var26 = this.a(var1, var3, var4, var24);
                  if (var26 != null) {
                     String var27 = var26.getX() + ":" + var26.getY() + ":" + var26.getZ();
                     if (var19.add(var27) && !this.r(var26) && this.f(var1, var26)) {
                        Location var28 = this.l(var26);
                        if (var28 != null) {
                           Vector var29 = var28.toVector().subtract(var8.toVector()).setY(0);
                           if (!(var29.lengthSquared() < 0.04)) {
                              Vector var30 = var29.clone().normalize();
                              double var31 = var30.dot(var12);
                              double var33 = Math.abs(var29.dot(var13));
                              boolean var35 = var31 > 0.86 && var29.lengthSquared() <= var14 + 0.15;
                              if (!(var31 < 0.78) && !(var33 > 0.85) && (var11 || var35)) {
                                 double var36 = Math.sqrt(var29.lengthSquared());
                                 double var38 = var33 * 3.0 + Math.abs(var36 - 0.85) + (1.0 - var31) * 2.5;
                                 if (!var35) {
                                    var38 += 0.4;
                                 }

                                 if (var16 == null || var38 < var17) {
                                    var16 = var26;
                                    var17 = var38;
                                 }
                              }
                           }
                        }
                     }
                  }
               }

               return var16;
            }
         } else {
            return null;
         }
      }
   }

   Vector a(Player var1, Block var2, Vector var3) {
      if (var1 != null && var2 != null) {
         Location var4 = this.l(var2);
         if (var4 == null) {
            return var3 == null ? new Vector(1, 0, 0) : var3.clone().setY(0).normalize();
         } else {
            Vector var5 = var4.toVector().subtract(var1.getLocation().toVector()).setY(0);
            if (var5.lengthSquared() < 0.01) {
               return var3 == null ? new Vector(1, 0, 0) : var3.clone().setY(0).normalize();
            } else {
               return var5.normalize();
            }
         }
      } else {
         return var3 == null ? new Vector(1, 0, 0) : var3.clone().setY(0).normalize();
      }
   }

   Vector a(Player var1, Vector var2) {
      Vector var3 = var2 == null ? new Vector(1, 0, 0) : var2.clone().setY(0);
      if (var3.lengthSquared() < 0.01) {
         var3 = var1.getLocation().getDirection().setY(0);
      }

      if (var3.lengthSquared() < 0.01) {
         var3 = new Vector(1, 0, 0);
      }

      var3.normalize();
      Vector var4 = var1.getVelocity().clone().setY(0);
      double var5 = Math.max(0.26, var4.dot(var3));
      var5 = Math.min(0.34, var5);
      Vector var7 = var3.multiply(var5);
      var7.setY(0.42);
      return var7;
   }

   boolean a(Block var1, CpvpCombatState var2, long var3) {
      CpvpCombatController var5 = this.id.bZ();
      if (var1 != null && var2 != null && var3 < var2.oP && var2.oO != null) {
         Location var6 = this.l(var1);
         if (var6 != null && var6.getWorld() != null && var2.oO.getWorld() != null) {
            return !var6.getWorld().equals(var2.oO.getWorld()) ? false : Math.abs(var6.getY() - var2.oO.getY()) < 0.6 && var5.d(var6, var2.oO) < 0.35;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   boolean b(Player var1, Block var2, Vector var3) {
      if (var1 == null || var2 == null || var3 == null || var3.lengthSquared() < 0.01) {
         return false;
      } else if (!this.id.bZ().ag(var1)) {
         return false;
      } else {
         Location var4 = this.l(var2);
         if (var4 == null) {
            return false;
         } else {
            Vector var5 = var3.clone().setY(0).normalize();
            Vector var6 = var4.toVector().subtract(var1.getLocation().toVector()).setY(0);
            double var7 = var6.dot(var5);
            Vector var9 = new Vector(-var5.getZ(), 0.0, var5.getX());
            double var10 = Math.abs(var6.dot(var9));
            if (var7 < 0.45 || var7 > 1.15) {
               return false;
            } else if (var10 > 0.34) {
               return false;
            } else {
               Vector var12 = var1.getLocation().getDirection().setY(0);
               return !(var12.lengthSquared() > 0.01) || !(var12.normalize().dot(var5) < 0.82);
            }
         }
      }
   }

   void u(CpvpCombatState var1) {
      if (var1 != null) {
         var1.oI = false;
         var1.oJ = null;
         var1.oK = null;
         var1.oL = 0L;
         var1.oM = 0;
         var1.oN = 0.0;
      }
   }

   void l(CpvpCombatState var1, long var2) {
      if (var1 != null) {
         if (var1.oK != null) {
            var1.oO = var1.oK.clone();
            var1.oP = var2 + 500L;
         }

         this.u(var1);
      }
   }

   void c(Player var1, CpvpCombatState var2, long var3) {
      CpvpCombatController var5 = this.id.bZ();
      if (var1 != null && var2 != null) {
         boolean var6 = var1.getLocation().getY() >= var2.oN + 0.45;
         if (!var6 && var2.oK != null && var2.oK.getWorld() != null && var2.oK.getWorld().equals(var1.getWorld())) {
            var6 = var5.d(var1.getLocation(), var2.oK) < 0.85 && var1.getLocation().getY() >= var2.oK.getY() - 0.35;
         }

         if (!var6) {
            this.l(var2, var3);
         } else {
            var2.oO = null;
            var2.oP = 0L;
            this.u(var2);
         }
      }
   }

   void e(Player var1, CpvpCombatState var2) {
      if (var1 != null && var2 != null && var2.oJ != null && !(var2.oJ.lengthSquared() < 0.01)) {
         Vector var3 = var2.oJ.clone().setY(0);
         if (var2.oK != null && var2.oK.getWorld() != null && var2.oK.getWorld().equals(var1.getWorld())) {
            Vector var4 = var2.oK.toVector().subtract(var1.getLocation().toVector()).setY(0.0);
            if (var4.lengthSquared() > 0.04) {
               var3 = var4;
            }
         }

         if (var3.lengthSquared() < 0.01) {
            var3 = var2.oJ.clone().setY(0);
         }

         if (!(var3.lengthSquared() < 0.01)) {
            var3.normalize();
            var1.setSprinting(true);
            Vector var10 = var1.getVelocity();
            Vector var5 = var10.clone().setY(0);
            double var6 = var5.dot(var3);
            double var8 = var2.oM <= 10 ? Math.min(0.34, 0.3) : 0.26;
            if (var6 < var8) {
               var5.add(var3.multiply(var8 - var6));
            }

            if (var5.lengthSquared() > 0.11560000000000002) {
               var5.normalize().multiply(0.34);
            }

            var10.setX(var5.getX());
            var10.setZ(var5.getZ());
            var1.setVelocity(var10);
         }
      }
   }

   boolean a(NPC var1, Player var2, Block var3, Vector var4, CpvpCombatState var5, long var6) {
      CpvpCombatController var8 = this.id.bZ();
      if (var2 != null && var3 != null) {
         Location var9 = this.l(var3);
         if (var9 == null) {
            return false;
         } else {
            Vector var10 = this.a(var2, var3, var4);
            if (var10.lengthSquared() < 0.01) {
               return false;
            } else {
               var8.a(var1, var2, var9, var5);
               FollowTrait var11 = (FollowTrait)var1.getTraitNullable(FollowTrait.class);
               if (var11 != null && var11.getFollowing() != null) {
                  var11.follow(null);
               }

               var1.getNavigator().cancelNavigation();
               var2.setSprinting(true);
               Vector var12 = new Vector(-var10.getZ(), 0.0, var10.getX());
               Vector var13 = var2.getVelocity();
               Vector var14 = var13.clone().setY(0);
               double var15 = var14.dot(var10);
               double var17 = var14.dot(var12);
               double var19 = var8.d(var2.getLocation(), var9);
               double var21 = var19 > 0.9 ? 0.22 : 0.15;
               double var23 = Math.max(0.12, var15 * 0.4 + var21);
               var23 = Math.min(0.26, var23);
               double var25 = var17 * 0.2;
               Vector var27 = var10.clone().multiply(var23).add(var12.multiply(var25));
               var13.setX(var27.getX());
               var13.setZ(var27.getZ());
               var2.setVelocity(var13);
               var5.kJ = var6;
               var5.oq = var6;
               var5.oQ = null;
               var5.oR = 0L;
               return true;
            }
         }
      } else {
         return false;
      }
   }

   boolean b(Player var1, Vector var2) {
      CpvpCombatController var3 = this.id.bZ();
      if (var2 == null || var2.lengthSquared() < 0.01) {
         return false;
      } else if (!var3.ag(var1)) {
         return false;
      } else {
         Location var4 = var1.getLocation();
         World var5 = var4.getWorld();
         if (var5 == null) {
            return false;
         } else {
            Vector var6 = var2.clone().setY(0);
            if (var6.lengthSquared() < 0.01) {
               return false;
            } else {
               var6.normalize();
               Vector var7 = var1.getVelocity();
               double var8 = Math.sqrt(var7.getX() * var7.getX() + var7.getZ() * var7.getZ());
               if (var8 > 0.12) {
                  return false;
               } else {
                  Block var10 = this.a(var1, var6, var5, 0.35);
                  if (var10 == null) {
                     return false;
                  } else {
                     return this.r(var10) ? false : this.f(var1, var10);
                  }
               }
            }
         }
      }
   }

   Block c(Player var1, Vector var2, World var3) {
      CpvpCombatController var4 = this.id.bZ();
      if (!var4.ag(var1)) {
         return null;
      } else {
         Vector var5 = var2.clone().setY(0);
         if (var5.lengthSquared() < 0.01) {
            return null;
         } else {
            var5.normalize();

            for (double var9 : new double[]{0.35, 0.5, 0.65}) {
               Block var11 = this.a(var1, var5, var3, var9);
               if (var11 != null && var11.getType().isSolid() && !this.r(var11) && this.f(var1, var11)) {
                  return var11;
               }
            }

            return null;
         }
      }
   }
}
