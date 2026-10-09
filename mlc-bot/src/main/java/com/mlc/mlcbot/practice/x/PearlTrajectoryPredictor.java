// Extracted from the user-provided PracticeBot-Source; strategy logic retained.
package com.mlc.mlcbot.practice.x;

import static com.mlc.mlcbot.nms.EntityGroundState.isOnGround;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

/** 珍珠弹道求解、碰撞模拟和玩家落点预测。 原源码：x/g1.java。 */
public final class PearlTrajectoryPredictor {
   private static final int jd = 3;
   private static final double je = 0.31;
   private static final double jf = 0.08;
   private static final double jg = 0.98;
   private static final double jh = 0.91;
   private static final double ji = 0.03;
   private static final double jj = 0.99;
   private static final double jk = 1.5;
   private static final double jl = 0.65;
   private static final int jm = 120;
   private final CpvpCombatController jn;
   private final Map<PearlAimCacheKey, PearlTrajectorySolution> jo = new HashMap<>();
   private int jp = Integer.MIN_VALUE;
   private final Map<PlayerLandingCacheKey, Location> jq = new HashMap<>();
   private int jr = Integer.MIN_VALUE;
   private final Map<SimulatedLandingCacheKey, Location> js = new HashMap<>();
   private int jt = Integer.MIN_VALUE;

   public PearlTrajectoryPredictor(CpvpCombatController var1) {
      this.jn = var1;
   }

   public Location a(Player var1, CpvpCombatState var2, int var3) {
      if (var1 == null) {
         return null;
      } else {
         Location var4 = var1.getLocation().clone();
         Vector var5 = var1.getVelocity() != null ? var1.getVelocity().clone() : new Vector();
         Vector var6 = var2 != null && var2.mO != null ? var2.mO.clone() : null;
         if (var6 != null && var6.lengthSquared() > 0.001) {
            var6.setY(0.0).normalize();
            double var7 = Math.sqrt(var5.getX() * var5.getX() + var5.getZ() * var5.getZ());
            if (var7 < 0.12) {
               var5.setX(var5.getX() + var6.getX() * 0.32);
               var5.setZ(var5.getZ() + var6.getZ() * 0.32);
            }
         }

         if (var2 != null && var2.mK > 0L && var5.getY() < 0.24) {
            var5.setY(0.36);
         }

         int var9 = Math.max(0, Math.min(3, var3));

         for (int var8 = 0; var8 < var9; var8++) {
            var4.add(var5);
            var5.setX(var5.getX() * 0.91);
            var5.setZ(var5.getZ() * 0.91);
            var5.setY((var5.getY() - 0.08) * 0.98);
         }

         return var4;
      }
   }

   public int j(Player var1, Player var2) {
      if (var2 != null && var2.getWorld() != null) {
         Location var3 = var2.getLocation();
         int var4 = (int)Math.floor(var3.getY());
         Integer var5 = this.b(var2, 5);
         if (var5 == null && var1 != null && var1.getWorld().equals(var2.getWorld())) {
            int var6 = (int)Math.floor(var1.getLocation().getY());
            if (Math.abs(var6 - var4) <= 1 && this.jn.co().d(var1.getLocation(), var2.getLocation()) <= 3.6) {
               var5 = var6;
            }
         }

         return var5 != null && var5 <= var4 ? var5 : var4;
      } else {
         return Integer.MIN_VALUE;
      }
   }

   public Integer b(Player var1, int var2) {
      if (var1 != null && var1.getWorld() != null) {
         Location var3 = var1.getLocation();
         World var4 = var3.getWorld();
         int var5 = (int)Math.floor(var3.getY());
         int var6 = Math.max(var4.getMinHeight() + 1, var5 - Math.max(1, var2));
         int[] var7 = this.m(var3.getX());
         int[] var8 = this.m(var3.getZ());

         for (int var9 = var5; var9 >= var6; var9--) {
            for (int var13 : var7) {
               for (int var17 : var8) {
                  Block var18 = var4.getBlockAt(var13, var9, var17);
                  Block var19 = var4.getBlockAt(var13, var9 - 1, var17);
                  if (!var18.getType().isSolid() && !this.jn.cn().i(var18) && var19.getType().isSolid() && !this.jn.cn().i(var19)) {
                     return var9;
                  }
               }
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private int[] m(double var1) {
      int var3 = (int)Math.floor(var1 - 0.31);
      int var4 = (int)Math.floor(var1 + 0.31);
      return var3 == var4 ? new int[]{var3} : new int[]{var3, var4};
   }

   public Location c(Player var1, int var2) {
      PlayerLandingCacheKey var3 = this.e(var1, var2);
      if (var3 != null) {
         int var4 = Bukkit.getCurrentTick();
         if (var4 != this.jr) {
            this.jr = var4;
            this.jq.clear();
         }

         if (this.jq.containsKey(var3)) {
            Location var10 = this.jq.get(var3);
            return var10 == null ? null : var10.clone();
         }
      }

      Location var9 = var1.getLocation().clone().add(0.0, 1.0, 0.0);
      Vector var5 = var1.getVelocity().clone();
      boolean var6 = isOnGround(var1);

      for (int var7 = 0; var7 < var2; var7++) {
         var9.add(var5);
         if (!var6) {
            var5.setY(var5.getY() - 0.08);
            var5.setY(var5.getY() * 0.98);
         } else {
            var5.setY(0);
         }

         var5.setX(var5.getX() * 0.91);
         var5.setZ(var5.getZ() * 0.91);
         if (var9.getWorld() != null) {
            Block var8 = var9.clone().add(0.0, -1.5, 0.0).getBlock();
            if (var8.getType().isSolid() && var5.getY() < 0.0) {
               var6 = true;
               var5.setY(0);
               var9.setY(var8.getY() + 1.0 + 1.0);
            }
         }
      }

      if (var3 != null && this.jq.size() < 4096) {
         this.jq.put(var3, var9.clone());
      }

      return var9;
   }

   public Location d(Player var1, int var2) {
      if (var1 == null) {
         return null;
      } else {
         World var3 = var1.getWorld();
         if (var3 == null) {
            return null;
         } else {
            Location var4 = var1.getLocation().clone();
            if (var4 == null) {
               return null;
            } else {
               Vector var5 = var1.getVelocity().clone();
               if (isOnGround(var1)) {
                  return null;
               } else {
                  SimulatedLandingCacheKey var6 = this.a(var1, var2, var4, var5);
                  int var7 = Bukkit.getCurrentTick();
                  if (var7 != this.jt) {
                     this.jt = var7;
                     this.js.clear();
                  }

                  if (var6 != null && this.js.containsKey(var6)) {
                     Location var16 = this.js.get(var6);
                     return var16 == null ? null : var16.clone();
                  } else {
                     for (int var8 = 0; var8 < var2; var8++) {
                        Location var9 = var4.clone();
                        var4.add(var5);
                        var5.setY(var5.getY() - 0.08);
                        var5.setX(var5.getX() * 0.91);
                        var5.setZ(var5.getZ() * 0.91);
                        var5.setY(var5.getY() * 0.98);
                        Block var10 = var4.getBlock();
                        if (var10.getType().isSolid()) {
                           var4.setX(var9.getX());
                           var4.setZ(var9.getZ());
                           var5.setX(0);
                           var5.setZ(0);
                        }

                        Block var11 = var4.clone().add(0.0, -0.1, 0.0).getBlock();
                        if (var11.getType().isSolid() && var5.getY() <= 0.0) {
                           Location var17 = new Location(var3, var4.getX(), var11.getY() + 1.0, var4.getZ());
                           Block var13 = var17.getBlock();
                           Block var14 = var17.clone().add(0.0, 1.0, 0.0).getBlock();
                           if (!var13.getType().isSolid() && !var14.getType().isSolid()) {
                              return this.a(var6, var17);
                           }

                           Location var15 = this.a(var3, var17, var17.getY());
                           if (var15 != null) {
                              return this.a(var6, var15);
                           }

                           return this.a(var6, var17);
                        }

                        Block var12 = var4.clone().add(0.0, 1.8, 0.0).getBlock();
                        if (var12.getType().isSolid() && var5.getY() > 0.0) {
                           var5.setY(0);
                        }

                        if (var4.getY() < var3.getMinHeight()) {
                           return this.a(var6, null);
                        }
                     }

                     return this.a(var6, null);
                  }
               }
            }
         }
      }
   }

   public PearlTrajectoryTrace a(Location var1, Vector var2, Player var3, int var4) {
      PearlTrajectoryTrace var5 = new PearlTrajectoryTrace();
      if (var1 != null && var2 != null) {
         World var6 = var1.getWorld();
         if (var6 == null) {
            return var5;
         } else {
            Location var7 = var1.clone();
            Vector var8 = var2.clone();

            for (int var9 = 0; var9 < var4; var9++) {
               Location var10 = var7.clone();
               var7.add(var8);
               var8.setY(var8.getY() - 0.03);
               var8.multiply(0.99);
               Vector var11 = var7.toVector().subtract(var10.toVector());
               double var12 = var11.length();
               if (var12 > 0.01) {
                  RayTraceResult var14 = var6.rayTraceBlocks(var10, var11.normalize(), var12 + 0.1, FluidCollisionMode.NEVER, true);
                  if (var14 != null && var14.getHitBlock() != null) {
                     var5.hV = true;
                     var5.hZ = var9;
                     var5.ia = var14.getHitPosition().toLocation(var6);
                     var5.ib = var5.ia;
                     break;
                  }
               }

               if (var3 != null) {
                  Location var17 = this.c(var3, var9);
                  double var15 = var7.distance(var17);
                  if (var15 < var5.hX) {
                     var5.hX = var15;
                     var5.hY = var9;
                  }

                  if (var15 <= 0.65) {
                     var5.hU = true;
                     var5.ib = var7.clone();
                     break;
                  }
               }

               if (var7.getY() < var6.getMinHeight() || var7.distance(var1) > 80.0) {
                  var5.ib = var7.clone();
                  break;
               }

               var5.ib = var7.clone();
            }

            return var5;
         }
      } else {
         return var5;
      }
   }

   public Vector b(Location var1, Location var2, double var3) {
      double var5 = var2.getX() - var1.getX();
      double var7 = var2.getZ() - var1.getZ();
      double var9 = Math.atan2(-var5, var7);
      double var11 = Math.toRadians(var3);
      Vector var13 = new Vector(-Math.sin(var9) * Math.cos(var11), Math.sin(var11), Math.cos(var9) * Math.cos(var11));
      return var13.normalize().multiply(1.5);
   }

   public PearlTrajectorySolution c(Location var1, Player var2) {
      if (var1 != null && var2 != null) {
         World var3 = var1.getWorld();
         if (var3 == null) {
            return null;
         } else {
            Location var4 = var2.getLocation();
            double var5 = var1.distance(var4);
            if (!(var5 < 2.0) && !(var5 > 50.0)) {
               int var7 = Bukkit.getCurrentTick();
               if (var7 != this.jp) {
                  this.jp = var7;
                  this.jo.clear();
               }

               Vector var8 = var2.getVelocity();
               PearlAimCacheKey var9 = new PearlAimCacheKey(
                  var3.getUID(),
                  this.n(var1.getX()),
                  this.n(var1.getY()),
                  this.n(var1.getZ()),
                  var2.getUniqueId(),
                  this.n(var4.getX()),
                  this.n(var4.getY()),
                  this.n(var4.getZ()),
                  this.o(var8.getX()),
                  this.o(var8.getY()),
                  this.o(var8.getZ()),
                  isOnGround(var2)
               );
               PearlTrajectorySolution var10 = this.jo.get(var9);
               if (var10 != null) {
                  return this.a(var10);
               } else {
                  PearlTrajectorySolution var11 = null;
                  double var12 = -Double.MAX_VALUE;
                  int var14 = (int)Math.ceil(var5 / 1.5) + 5;
                  var14 = Math.min(var14, 60);

                  for (byte var15 = 3; var15 <= var14 + 15; var15 += 2) {
                     Location var16 = this.f(var2, var15);
                     if (var16 != null) {
                        Vector var17 = this.a(var1, var16, var15);
                        if (var17 != null && this.a(var17)) {
                           PearlTrajectoryTrace var18 = this.b(var1.clone(), var17.clone(), var2, var15 + 20);
                           if (!var18.hW || var18.hZ >= var15 - 2) {
                              double var19;
                              if (var18.hU) {
                                 var19 = 1000 - var18.hY;
                              } else {
                                 var19 = -var18.hX * 100.0;
                              }

                              if (var18.hV && var18.hZ < var18.hY) {
                                 var19 -= 500.0;
                              }

                              if (var19 > var12) {
                                 var12 = var19;
                                 var11 = new PearlTrajectorySolution(var17, var18.hX, var18.hU, var18.hY, var18.hZ);
                              }
                           }
                        }
                     }
                  }

                  if (var11 == null || !var11.hQ && var11.hP > 1.5) {
                     PearlTrajectorySolution var22 = this.d(var1, var2);
                     if (var22 != null && (var11 == null || var22.hP < var11.hP)) {
                        var11 = var22;
                     }
                  }

                  if (var11 != null && this.jo.size() < 2048) {
                     this.jo.put(var9, this.a(var11));
                  }

                  return var11;
               }
            } else {
               return null;
            }
         }
      } else {
         return null;
      }
   }

   private int n(double var1) {
      return (int)Math.round(var1 * 16.0);
   }

   private int o(double var1) {
      return (int)Math.round(var1 * 100.0);
   }

   private PlayerLandingCacheKey e(Player var1, int var2) {
      if (var1 != null && var1.getWorld() != null) {
         Location var3 = var1.getLocation();
         Vector var4 = var1.getVelocity();
         return new PlayerLandingCacheKey(
            var1.getWorld().getUID(),
            var1.getUniqueId(),
            this.n(var3.getX()),
            this.n(var3.getY()),
            this.n(var3.getZ()),
            this.o(var4.getX()),
            this.o(var4.getY()),
            this.o(var4.getZ()),
            isOnGround(var1),
            var2
         );
      } else {
         return null;
      }
   }

   private SimulatedLandingCacheKey a(Player var1, int var2, Location var3, Vector var4) {
      return var1 != null && var1.getWorld() != null && var3 != null && var4 != null
         ? new SimulatedLandingCacheKey(
            var1.getWorld().getUID(),
            var1.getUniqueId(),
            this.n(var3.getX()),
            this.n(var3.getY()),
            this.n(var3.getZ()),
            this.o(var4.getX()),
            this.o(var4.getY()),
            this.o(var4.getZ()),
            isOnGround(var1),
            var2
         )
         : null;
   }

   private Location a(SimulatedLandingCacheKey var1, Location var2) {
      if (var1 != null && this.js.size() < 1024) {
         this.js.put(var1, var2 == null ? null : var2.clone());
      }

      return var2;
   }

   private PearlTrajectorySolution a(PearlTrajectorySolution var1) {
      return var1 == null ? null : new PearlTrajectorySolution(var1.hO == null ? null : var1.hO.clone(), var1.hP, var1.hQ, var1.hR, var1.hS);
   }

   public Location f(Player var1, int var2) {
      if (var1 == null) {
         return null;
      } else {
         World var3 = var1.getWorld();
         if (var3 == null) {
            return null;
         } else {
            Location var4 = var1.getLocation().clone();
            Vector var5 = var1.getVelocity().clone();
            boolean var6 = isOnGround(var1);

            for (int var7 = 0; var7 < var2; var7++) {
               var4.add(var5);
               if (!var6) {
                  var5.setY(var5.getY() - 0.08);
                  var5.setY(var5.getY() * 0.98);
               }

               var5.setX(var5.getX() * 0.91);
               var5.setZ(var5.getZ() * 0.91);
               Block var8 = var4.clone().add(0.0, -0.1, 0.0).getBlock();
               if (var8.getType().isSolid() && var5.getY() < 0.0) {
                  var6 = true;
                  var5.setY(0);
                  var4.setY(var8.getY() + 1.0);
               } else if (!var8.getType().isSolid()) {
                  var6 = false;
               }

               Block var9 = var4.clone().add(0.0, 1.8, 0.0).getBlock();
               if (var9.getType().isSolid() && var5.getY() > 0.0) {
                  var5.setY(0);
               }

               if (var4.getY() < var3.getMinHeight() || var4.getY() > var3.getMaxHeight()) {
                  return null;
               }
            }

            return var4.add(0.0, 1.0, 0.0);
         }
      }
   }

   public Vector a(Location var1, Location var2, int var3) {
      if (var1 != null && var2 != null && var3 > 0) {
         double var4 = var2.getX() - var1.getX();
         double var6 = var2.getY() - var1.getY();
         double var8 = var2.getZ() - var1.getZ();
         double var10 = Math.sqrt(var4 * var4 + var8 * var8);
         if (var10 < 0.1) {
            return null;
         } else {
            double var12 = Math.pow(0.99, var3);
            double var14 = (1.0 - var12) / 0.010000000000000009;
            if (var14 < 0.1) {
               return null;
            } else {
               double var16 = var10 / var14;
               double var18 = this.a(var6, var3);
               double var20 = var16;
               double var22 = Math.sqrt(var16 * var16 + var18 * var18);
               if (var22 > 2.25) {
                  double var24 = 1.7999999999999998 / var22;
                  var20 = var16 * var24;
                  var18 *= var24;
               }

               double var29 = Math.atan2(-var4, var8);
               Vector var26 = new Vector(-Math.sin(var29) * var20, var18, Math.cos(var29) * var20);
               double var27 = var26.length();
               if (var27 > 0.01) {
                  var26.normalize().multiply(1.5);
               }

               return var26;
            }
         }
      } else {
         return null;
      }
   }

   public double a(double var1, int var3) {
      double var4 = -1.0;
      double var6 = 1.5;

      for (int var8 = 0; var8 < 20; var8++) {
         double var9 = (var4 + var6) / 2.0;
         double var11 = this.b(var9, var3);
         if (var11 < var1) {
            var4 = var9;
         } else {
            var6 = var9;
         }
      }

      return (var4 + var6) / 2.0;
   }

   public double b(double var1, int var3) {
      double var4 = 0.0;
      double var6 = var1;

      for (int var8 = 0; var8 < var3; var8++) {
         var4 += var6;
         var6 -= 0.03;
         var6 *= 0.99;
      }

      return var4;
   }

   public PearlTrajectoryTrace b(Location var1, Vector var2, Player var3, int var4) {
      PearlTrajectoryTrace var5 = new PearlTrajectoryTrace();
      if (var1 != null && var2 != null) {
         World var6 = var1.getWorld();
         if (var6 == null) {
            return var5;
         } else {
            Location var7 = var1.clone();
            Vector var8 = var2.clone();

            for (int var9 = 0; var9 < var4; var9++) {
               Location var10 = var7.clone();
               var7.add(var8);
               var8.setY(var8.getY() - 0.03);
               var8.multiply(0.99);
               Vector var11 = var7.toVector().subtract(var10.toVector());
               double var12 = var11.length();
               if (var12 > 0.01) {
                  RayTraceResult var14 = var6.rayTraceBlocks(var10, var11.normalize(), var12 + 0.1, FluidCollisionMode.NEVER, true);
                  if (var14 != null && var14.getHitBlock() != null) {
                     var5.hV = true;
                     var5.hZ = var9;
                     var5.ia = var14.getHitPosition().toLocation(var6);
                     var5.hW = true;
                     if (var5.hX == Double.MAX_VALUE) {
                        var5.ib = var5.ia;
                     }
                     break;
                  }
               }

               if (var3 != null) {
                  Location var21 = this.f(var3, var9);
                  if (var21 != null) {
                     double var15 = var7.distance(var21);
                     if (var15 < var5.hX) {
                        var5.hX = var15;
                        var5.hY = var9;
                     }

                     double var17 = Math.sqrt(Math.pow(var7.getX() - var21.getX(), 2.0) + Math.pow(var7.getZ() - var21.getZ(), 2.0));
                     double var19 = Math.abs(var7.getY() - var21.getY());
                     if (var17 <= 0.8 && var19 <= 1.2) {
                        var5.hU = true;
                        var5.ib = var7.clone();
                        break;
                     }
                  }
               }

               if (var7.getY() < var6.getMinHeight() || var7.distance(var1) > 80.0) {
                  var5.ib = var7.clone();
                  break;
               }

               var5.ib = var7.clone();
            }

            return var5;
         }
      } else {
         return var5;
      }
   }

   public PearlTrajectorySolution d(Location var1, Player var2) {
      if (var1 != null && var2 != null) {
         PearlTrajectorySolution var3 = null;

         for (byte var4 = 5; var4 <= 30; var4 += 5) {
            Location var5 = this.f(var2, var4);
            if (var5 != null) {
               for (double var6 = -20.0; var6 <= 60.0; var6 += 2.0) {
                  Vector var8 = this.b(var1, var5, var6);
                  if (var8 != null && this.a(var8)) {
                     PearlTrajectoryTrace var9 = this.b(var1.clone(), var8.clone(), var2, 80);
                     if ((!var9.hW || var9.hZ >= var4 - 3) && !(var9.hX > 2.0)) {
                        PearlTrajectorySolution var10 = new PearlTrajectorySolution(var8, var9.hX, var9.hU, var9.hY, var9.hZ);
                        if (var3 == null || var10.hQ && !var3.hQ || !var10.hQ && !var3.hQ && var10.hP < var3.hP) {
                           var3 = var10;
                        }

                        if (var10.hQ) {
                           break;
                        }
                     }
                  }
               }

               if (var3 != null && var3.hQ) {
                  break;
               }
            }
         }

         return var3;
      } else {
         return null;
      }
   }

   public Location a(World var1, Location var2, double var3) {
      if (var1 != null && var2 != null) {
         int var5 = var2.getBlockX();
         int var6 = var2.getBlockZ();
         int var7 = (int)var3;

         for (int var8 = var7; var8 <= var7 + 10 && var8 < var1.getMaxHeight() - 2; var8++) {
            Location var9 = this.a(var1, var5, var8, var6);
            if (var9 != null) {
               return var9;
            }
         }

         for (int var10 = var7 - 1; var10 >= var7 - 15 && var10 > var1.getMinHeight(); var10--) {
            Location var11 = this.a(var1, var5, var10, var6);
            if (var11 != null) {
               return var11;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public Location a(World var1, int var2, int var3, int var4) {
      Block var5 = var1.getBlockAt(var2, var3, var4);
      Block var6 = var1.getBlockAt(var2, var3 + 1, var4);
      Block var7 = var1.getBlockAt(var2, var3 + 2, var4);
      if (!var5.getType().isSolid()) {
         return null;
      } else if (this.jn.cn().i(var5) || this.jn.cn().j(var5)) {
         return null;
      } else {
         return var6.getType().isAir() && var7.getType().isAir() ? new Location(var1, var2 + 0.5, var3 + 1, var4 + 0.5) : null;
      }
   }

   public Vector i(Location var1, Location var2) {
      if (var1 != null && var2 != null) {
         double var3 = var2.getX() - var1.getX();
         double var5 = var2.getZ() - var1.getZ();
         double var7 = Math.sqrt(var3 * var3 + var5 * var5);
         if (var7 < 1.0) {
            return null;
         } else {
            for (double var9 = 15.0; var9 <= 50.0; var9 += 5.0) {
               Vector var11 = this.b(var1, var2, var9);
               if (var11 != null && this.a(var11)) {
                  PearlTrajectoryTrace var12 = this.a(var1.clone(), var11.clone(), null, 120);
                  if (var12.ib != null) {
                     double var13 = var12.ib.distance(var2);
                     if (var12.hV) {
                        if (var12.ia != null) {
                           double var15 = var12.ia.distance(var2);
                           if (var15 < 3.0) {
                              return var11;
                           }
                        }
                     } else if (var13 < 5.0) {
                        return var11;
                     }
                  }
               }
            }

            return null;
         }
      } else {
         return null;
      }
   }

   public Vector b(Vector var1, double var2) {
      double var4 = Math.cos(var2);
      double var6 = Math.sin(var2);
      double var8 = var1.getX() * var4 - var1.getZ() * var6;
      double var10 = var1.getX() * var6 + var1.getZ() * var4;
      return new Vector(var8, var1.getY(), var10);
   }

   public Vector j(Location var1, Location var2) {
      if (var1 == null || var2 == null) {
         return null;
      } else if (var1.getWorld() != null && var2.getWorld() != null) {
         if (!var1.getWorld().equals(var2.getWorld())) {
            return null;
         } else {
            double var3 = var2.getX() - var1.getX();
            double var5 = var2.getY() - var1.getY();
            double var7 = var2.getZ() - var1.getZ();
            if (Double.isFinite(var3) && Double.isFinite(var5) && Double.isFinite(var7)) {
               double var9 = Math.sqrt(var3 * var3 + var7 * var7);
               if (var9 < 0.5) {
                  return null;
               } else if (var9 > 100.0) {
                  return null;
               } else {
                  double var11 = 1.5;
                  double var13 = var9 / var11;
                  if (!(var13 <= 0.0) && Double.isFinite(var13)) {
                     double var15 = var5 / var13 + 0.03 * var13 / 2.0;
                     var15 = Math.max(-0.3, Math.min(0.8, var15));
                     Vector var17 = new Vector(var3, 0.0, var7);
                     if (var17.lengthSquared() < 0.001) {
                        return null;
                     } else {
                        var17.normalize();
                        var17.multiply(var11 * 0.9);
                        var17.setY(var15);
                        return !this.a(var17) ? null : var17;
                     }
                  } else {
                     return null;
                  }
               }
            } else {
               return null;
            }
         }
      } else {
         return null;
      }
   }

   public boolean a(Vector var1) {
      return var1 != null && Double.isFinite(var1.getX()) && Double.isFinite(var1.getY()) && Double.isFinite(var1.getZ());
   }
}
