// Extracted from the user-provided PracticeBot-Source; strategy logic retained.
package com.mlc.mlcbot.practice.x;

import com.mlc.mlcbot.practice.PracticeBotPlugin;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.mlc.mlcbot.practice.bridge.NPC;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.RespawnAnchor;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

final class ao {
   private static final long bG = 1200L;
   private static final long bH = 12000L;
   private static final long bI = 900L;
   private static final long bJ = 650L;
   private static final long bK = 500L;
   private static final long bL = 650L;
   private static final long bM = 500L;
   private static final int bN = 4096;
   private final Map<aq, Boolean> bO = new HashMap<>();
   private int bP = Integer.MIN_VALUE;
   private final as bQ;

   ao(as var1) {
      this.bQ = var1;
   }

   boolean a(NPC var1, Player var2, Player var3, ae var4, m1 var5, bw var6, long var7) {
      av var9 = this.bQ.bZ();
      PracticeBotPlugin var10 = this.bQ.ca();
      if (!this.b(var2, var3, var6)) {
         return false;
      } else {
         boolean var11 = var6.dF().getType() == Material.RESPAWN_ANCHOR;
         this.a(var5, var6.dC() ? var6.dD() : var6.dG());
         var9.b(var1, var2, var5.mB, var5);
         if (!var11 && !var9.a(var2, var6.dC() ? var6.dD() : var6.dG(), var4)) {
            return false;
         } else {
            boolean var12 = var5 != null && var7 <= var5.mW;
            this.a(var5, var6.dF(), var7, 1200L);
            var9.a(var5, var4, var3, var7);
            var5.mG = true;
            var5.mH = true;
            var5.lt = true;
            var5.my = var7;
            var5.kD = var7;
            var5.mT = 0;
            var5.mU = 0L;
            var9.i(var5);
            var9.h(var5);
            this.a(var5, var6.dC() ? var6.dD() : var6.dG());
            boolean var13 = var6.dF().getType() == Material.RESPAWN_ANCHOR && this.g(var6.dF());
            long var14 = this.b(var4);
            long var16 = var13 ? 0L : this.c(var4);
            long var18 = var13 ? this.a(var5, var7) : this.d(var4);
            Runnable var20 = () -> {
               if (var5.mG) {
                  this.a(var5, var6.dG());
                  var9.b(var1, var2, var5.mB, var5);
                  boolean var14x = var6.dF().getType() != Material.RESPAWN_ANCHOR;
                  if (var14x && !var9.a(var2, var6.dG(), var4)) {
                     this.a(var2, var5);
                  } else if (!this.a(var2, var3, var6, true)) {
                     this.a(var2, var5);
                  } else {
                     if (var6.dF().getType() != Material.RESPAWN_ANCHOR) {
                        var9.a(var2, Material.RESPAWN_ANCHOR);
                        if (!com.mlc.mlcbot.combat.CombatRuntime.place(var2, var6.dF(), Material.RESPAWN_ANCHOR)) { this.a(var2, var5); return; }
                        this.bQ.cc();
                        long var15 = System.currentTimeMillis();
                        this.a(var5, var6.dF(), var15, 1200L);
                        this.b(var5, var6.dF(), var15, 12000L);
                        var5.mW = Math.max(var5.mW, var15 + 900L);
                        var2.swingMainHand();
                     }

                     com.mlc.mlcbot.combat.CombatRuntime.runTaskLater(var10.plugin(), () -> {
                        if (var5.mG) {
                           if (!this.a(var2, var3, var6, false)) {
                              if (this.a(var2, var3, var6, var4, var5, var12)) {
                                 var5.kE = System.currentTimeMillis();
                              }

                              this.a(var2, var5);
                           } else {
                              this.a(var5, var6.dG());
                              var9.b(var1, var2, var5.mB, var5);
                              if (!this.g(var6.dF())) {
                                 var9.a(var2, Material.GLOWSTONE);
                                 this.e(var2, var6.dF());
                                 var2.swingMainHand();
                              }

                              long var12xx = System.currentTimeMillis();
                              this.a(var5, var6.dF(), var12xx, 1200L);
                              if (this.a(var5, var6.dF(), var12xx) || this.b(var5, var6.dF(), var12xx)) {
                                 this.b(var5, var6.dF(), var12xx, 12000L);
                              }

                              var5.mX = Math.max(var5.mX, var12xx + this.f(var18));
                              var5.mW = Math.max(var5.mW, var12xx + 650L);
                              com.mlc.mlcbot.combat.CombatRuntime.runTaskLater(var10.plugin(), () -> {
                                 if (var5.mG) {
                                    boolean var10xx = this.a(var2, var3, var6, false);
                                    if (!var10xx) {
                                       var9.k(var5);
                                    }

                                    this.a(var5, var6.dG());
                                    var9.b(var1, var2, var5.mB, var5);
                                    boolean var11xx = this.a(var2, var3, var6, var4, var5, var12);
                                    if (var11xx && var12 && var5 != null) {
                                       var5.mW = 0L;
                                    }

                                    if (var11xx && var6.dc() >= 2.75) {
                                       var5.mA = System.currentTimeMillis() + 900L;
                                    }

                                    if (var11xx) {
                                       var5.kE = System.currentTimeMillis();
                                       if (var6.dL() && this.bQ.a(var2, var3, var6)) {
                                          com.mlc.mlcbot.combat.CombatRuntime.runTaskLater(var10.plugin(), () -> this.a(var1, var2, var3, var4, var5, var6), 2L);
                                       }
                                    }

                                    this.a(var2, var5);
                                 }
                              }, var18);
                           }
                        }
                     }, var16);
                  }
               }
            };
            if (var6.dC()) {
               this.a(var5, var6.dD());
               if (!var9.a(var2, var5.mB, var4)) {
                  this.a(var2, var5);
                  return false;
               }

               var9.b(var1, var2, var5.mB, var5);
               var9.a(var2, Material.GLOWSTONE);
               if (!com.mlc.mlcbot.combat.CombatRuntime.place(var2, var6.dE(), Material.GLOWSTONE)) { this.a(var2, var5); return false; }
               var2.swingMainHand();
               com.mlc.mlcbot.combat.CombatRuntime.runTaskLater(var10.plugin(), var20, var14);
            } else {
               var20.run();
            }

            return true;
         }
      }
   }

   long b(ae var1) {
      return switch (var1.aF()) {
         case PRO -> this.j(2L, 3L);
         case HARD -> this.j(3L, 4L);
         case MEDIUM -> this.j(4L, 5L);
         default -> this.j(5L, 7L);
      };
   }

   long c(ae var1) {
      return switch (var1.aF()) {
         case PRO -> this.j(2L, 3L);
         case HARD -> this.j(3L, 4L);
         case MEDIUM -> this.j(4L, 6L);
         default -> this.j(6L, 8L);
      };
   }

   long d(ae var1) {
      return switch (var1.aF()) {
         case PRO -> this.j(2L, 4L);
         case HARD -> this.j(3L, 5L);
         case MEDIUM -> this.j(5L, 7L);
         default -> this.j(7L, 10L);
      };
   }

   private long a(m1 var1, long var2) {
      if (var1 != null && var1.mX > var2) {
         long var4 = var1.mX - var2;
         return Math.max(1L, (var4 + 49L) / 50L);
      } else {
         return 0L;
      }
   }

   private long f(long var1) {
      return Math.max(0L, var1) * 50L;
   }

   long j(long var1, long var3) {
      return var3 <= var1 ? var1 : var1 + this.bQ.cb().nextInt((int)(var3 - var1 + 1L));
   }

   boolean b(Player var1, Player var2, Block var3) {
      av var4 = this.bQ.bZ();
      if (var3 != null && this.d(var3)) {
         if (this.bQ.a(var3, 1, 1) >= 2) {
            return false;
         } else if (!var4.i(var3) && (!var4.j(var3) || this.c(var3))) {
            if (!this.b(var3)) {
               return false;
            } else if (!this.a(var1, var3, 4.15)) {
               return false;
            } else {
               return !var4.h(var3) ? false : !this.a(var3, var1) && !this.a(var3, var2);
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   boolean c(Player var1, Player var2, Block var3) {
      if (var1 == null || var2 == null || var3 == null || var3.getType() != Material.RESPAWN_ANCHOR) {
         return false;
      } else if (!this.i(var1, var2)) {
         return false;
      } else {
         return !this.a(var3, var1) && !this.a(var3, var2) ? this.b(var1, var3) || this.c(var1, var3) : false;
      }
   }

   boolean d(Player var1, Player var2, Block var3) {
      av var4 = this.bQ.bZ();
      if (var3 != null && this.d(var3)) {
         if (!var4.i(var3) && (!var4.j(var3) || this.c(var3))) {
            if (!this.b(var3)) {
               return false;
            } else if (!var4.h(var3)) {
               return false;
            } else {
               return !this.a(var3, var1) && !this.a(var3, var2) ? this.a(var1, var3, 3.15) : false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   boolean a(Player var1, Block var2, double var3) {
      av var5 = this.bQ.bZ();
      if (var1 != null && var2 != null && var2.getWorld() != null && var1.getWorld().equals(var2.getWorld())) {
         Location var6 = var2.getLocation().add(0.5, 0.5, 0.5);
         double var7 = var1.getEyeLocation().distance(var6);
         if (var7 > var3 || var7 < 0.95) {
            return false;
         } else {
            return var5.d(var1.getLocation(), var6) > var3 ? false : this.b(var1, var2, var3);
         }
      } else {
         return false;
      }
   }

   boolean b(Player var1, Block var2) {
      av var3 = this.bQ.bZ();
      if (var1 == null || var2 == null || var2.getType() != Material.RESPAWN_ANCHOR) {
         return false;
      } else if (var2.getWorld() != null && var1.getWorld().equals(var2.getWorld())) {
         Location var4 = var2.getLocation().add(0.5, 0.5, 0.5);
         double var5 = var1.getEyeLocation().distance(var4);
         if (var5 > 4.15 || var5 < 0.95) {
            return false;
         } else {
            return var3.d(var1.getLocation(), var4) > 4.15 ? false : this.a(var1.getEyeLocation(), var2, 4.15);
         }
      } else {
         return false;
      }
   }

   boolean c(Player var1, Block var2) {
      if (var1 != null && var2 != null && var2.getWorld() != null && var1.getWorld().equals(var2.getWorld())) {
         Location var3 = var2.getLocation().add(0.5, 0.5, 0.5);
         double var4 = 5.050000000000001;
         return var1.getEyeLocation().distance(var3) <= var4 && this.bQ.bZ().d(var1.getLocation(), var3) <= var4 && this.a(var1.getEyeLocation(), var2, var4);
      } else {
         return false;
      }
   }

   boolean a(Player var1, bw var2) {
      return this.b(var1, var2.dF()) ? true : var2.dC() && this.a(var1, var2.dF(), var2.dE());
   }

   boolean a(Player var1, Block var2, Block var3) {
      av var4 = this.bQ.bZ();
      if (var1 != null && var2 != null && var3 != null) {
         if (var2.getWorld() == null || !var1.getWorld().equals(var2.getWorld())) {
            return false;
         } else if (var3.getWorld() != null && var1.getWorld().equals(var3.getWorld())) {
            Location var5 = var2.getLocation().add(0.5, 0.5, 0.5);
            Location var6 = var3.getLocation().add(0.5, 0.5, 0.5);
            if (var1.getEyeLocation().distance(var5) > 4.15 || var4.d(var1.getLocation(), var5) > 4.15) {
               return false;
            } else if (var1.getEyeLocation().distance(var6) > 3.9) {
               return false;
            } else {
               return !this.b(var1, var3, var2) ? false : this.a(var1.getEyeLocation(), var3, 3.9);
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   boolean b(Player var1, Block var2, double var3) {
      av var5 = this.bQ.bZ();
      if (var1 != null && var2 != null && var2.getWorld() != null && var1.getWorld().equals(var2.getWorld())) {
         Location var6 = var1.getEyeLocation();

         for (BlockFace var10 : new BlockFace[]{BlockFace.DOWN, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST}) {
            Block var11 = var2.getRelative(var10);
            if (var11.getType().isSolid() && !var5.i(var11)) {
               BlockFace var12 = var10.getOppositeFace();

               for (Location var14 : this.a(var11, var12)) {
                  if (var6.distance(var14) <= var3 && this.a(var6, var14, var11, var12)) {
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

   boolean a(Location var1, Block var2, double var3) {
      Location var5 = var2.getLocation().add(0.5, 0.5, 0.5);
      double[][] var6 = new double[][]{
         {0.0, 0.0, 0.0}, {0.28, 0.0, 0.0}, {-0.28, 0.0, 0.0}, {0.0, 0.0, 0.28}, {0.0, 0.0, -0.28}, {0.0, 0.28, 0.0}, {0.0, -0.28, 0.0}
      };

      for (double[] var10 : var6) {
         Location var11 = var5.clone().add(var10[0], var10[1], var10[2]);
         if (var1.distance(var11) <= var3 && this.a(var1, var11, var2)) {
            return true;
         }
      }

      return false;
   }

   boolean a(Location var1, Location var2, Block var3) {
      if (var1 != null && var2 != null && var3 != null && var1.getWorld() != null && var1.getWorld() == var2.getWorld()) {
         Vector var4 = var2.toVector().subtract(var1.toVector());
         double var5 = var4.length();
         if (var5 < 0.1) {
            return true;
         } else {
            this.bM();
            aq var7 = aq.a(var1, var2, var5 + 0.02, var3, null);
            Boolean var8 = this.bO.get(var7);
            if (var8 != null) {
               return var8;
            } else {
               RayTraceResult var9 = var1.getWorld().rayTraceBlocks(var1, var4.normalize(), var5 + 0.02, FluidCollisionMode.NEVER, true);
               boolean var10;
               if (var9 == null) {
                  var10 = false;
               } else {
                  Block var11 = var9.getHitBlock();
                  var10 = var11 != null
                     && var11.getWorld().equals(var3.getWorld())
                     && var11.getX() == var3.getX()
                     && var11.getY() == var3.getY()
                     && var11.getZ() == var3.getZ();
               }

               this.a(var7, var10);
               return var10;
            }
         }
      } else {
         return false;
      }
   }

   private List<Location> a(Block var1, BlockFace var2) {
      Location var3 = var1.getLocation().add(0.5, 0.5, 0.5);
      Vector var4 = new Vector(var2.getModX(), var2.getModY(), var2.getModZ());
      Vector var5;
      Vector var6;
      if (var2 == BlockFace.UP || var2 == BlockFace.DOWN) {
         var5 = new Vector(1.0, 0.0, 0.0);
         var6 = new Vector(0.0, 0.0, 1.0);
      } else if (var2 != BlockFace.NORTH && var2 != BlockFace.SOUTH) {
         var5 = new Vector(0.0, 0.0, 1.0);
         var6 = new Vector(0.0, 1.0, 0.0);
      } else {
         var5 = new Vector(1.0, 0.0, 0.0);
         var6 = new Vector(0.0, 1.0, 0.0);
      }

      double[][] var7 = new double[][]{{0.0, 0.0}, {0.24, 0.0}, {-0.24, 0.0}, {0.0, 0.24}, {0.0, -0.24}};
      ArrayList var8 = new ArrayList(var7.length);

      for (double[] var12 : var7) {
         Vector var13 = var3.toVector().add(var4.clone().multiply(0.5)).add(var5.clone().multiply(var12[0])).add(var6.clone().multiply(var12[1]));
         var8.add(var13.toLocation(var1.getWorld()));
      }

      return var8;
   }

   private boolean a(Location var1, Location var2, Block var3, BlockFace var4) {
      if (var1 != null && var2 != null && var3 != null && var4 != null && var1.getWorld() != null && var1.getWorld() == var2.getWorld()) {
         Vector var5 = var2.toVector().subtract(var1.toVector());
         double var6 = var5.length();
         if (var6 < 0.1) {
            return true;
         } else {
            this.bM();
            aq var8 = aq.a(var1, var2, var6 + 0.03, var3, var4);
            Boolean var9 = this.bO.get(var8);
            if (var9 != null) {
               return var9;
            } else {
               RayTraceResult var10 = var1.getWorld().rayTraceBlocks(var1, var5.normalize(), var6 + 0.03, FluidCollisionMode.NEVER, true);
               boolean var11 = false;
               if (var10 != null) {
                  Block var12 = var10.getHitBlock();
                  if (var12 != null
                     && var12.getWorld().equals(var3.getWorld())
                     && var12.getX() == var3.getX()
                     && var12.getY() == var3.getY()
                     && var12.getZ() == var3.getZ()) {
                     var11 = var10.getHitBlockFace() == var4;
                  }
               }

               this.a(var8, var11);
               return var11;
            }
         }
      } else {
         return false;
      }
   }

   private void bM() {
      int var1 = Bukkit.getCurrentTick();
      if (var1 != this.bP) {
         this.bP = var1;
         this.bO.clear();
      }
   }

   private void a(aq var1, boolean var2) {
      if (this.bO.size() < 4096) {
         this.bO.put(var1, var2);
      }
   }

   static int f(double var0) {
      return (int)Math.round(var0 * 1000.0);
   }

   int a(Block var1, int var2, int var3) {
      if (var1 != null && var1.getWorld() != null) {
         int var4 = 0;
         World var5 = var1.getWorld();
         int var6 = Math.max(var5.getMinHeight(), var1.getY() - Math.max(0, var3));
         int var7 = Math.min(var5.getMaxHeight() - 1, var1.getY() + Math.max(0, var3));
         int var8 = Math.max(0, var2);

         for (int var9 = -var8; var9 <= var8; var9++) {
            for (int var10 = var6; var10 <= var7; var10++) {
               for (int var11 = -var8; var11 <= var8; var11++) {
                  if (var5.getBlockAt(var1.getX() + var9, var10, var1.getZ() + var11).getType() == Material.RESPAWN_ANCHOR) {
                     var4++;
                  }
               }
            }
         }

         return var4;
      } else {
         return 0;
      }
   }

   Block e(Player var1, Player var2, Block var3) {
      Location var4 = var3.getLocation().add(0.5, 0.5, 0.5);
      BlockFace var5 = this.f(var4, var1.getLocation());
      if (var5 == null) {
         return null;
      } else {
         Block var6 = var3.getRelative(var5);
         if (!this.d(var1, var2, var6)) {
            return null;
         } else if (!this.b(var1, var6, var3)) {
            return null;
         } else {
            Vector var7 = var6.getLocation().add(0.5, 0.5, 0.5).toVector().subtract(var4.toVector());
            Vector var8 = var2.getLocation().toVector().subtract(var4.toVector());
            var7.setY(0.0);
            var8.setY(0.0);
            return var7.lengthSquared() > 0.0 && var8.lengthSquared() > 0.0 && var7.normalize().dot(var8.normalize()) > 0.25 ? null : var6;
         }
      }
   }

   boolean b(Player var1, Block var2, Block var3) {
      av var4 = this.bQ.bZ();
      Location var5 = var1.getLocation();
      Location var6 = var2.getLocation().add(0.5, 0.5, 0.5);
      Location var7 = var3.getLocation().add(0.5, 0.5, 0.5);
      double var8 = var4.d(var5, var6);
      if (!(var8 < 0.55) && !(var8 > 1.85)) {
         Vector var10 = var6.toVector().subtract(var5.toVector());
         Vector var11 = var7.toVector().subtract(var6.toVector());
         var10.setY(0.0);
         var11.setY(0.0);
         return !(var10.lengthSquared() < 0.01) && !(var11.lengthSquared() < 0.01) ? var10.normalize().dot(var11.normalize()) >= 0.88 : false;
      } else {
         return false;
      }
   }

   BlockFace f(Location var1, Location var2) {
      if (var1 != null && var2 != null && var1.getWorld() == var2.getWorld()) {
         double var3 = var2.getX() - var1.getX();
         double var5 = var2.getZ() - var1.getZ();
         if (Math.abs(var3) < 0.001 && Math.abs(var5) < 0.001) {
            return null;
         } else if (Math.abs(var3) >= Math.abs(var5)) {
            return var3 >= 0.0 ? BlockFace.EAST : BlockFace.WEST;
         } else {
            return var5 >= 0.0 ? BlockFace.SOUTH : BlockFace.NORTH;
         }
      } else {
         return null;
      }
   }

   boolean b(Block var1) {
      av var2 = this.bQ.bZ();

      for (BlockFace var6 : new BlockFace[]{BlockFace.DOWN, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST}) {
         Block var7 = var1.getRelative(var6);
         if (var7.getType().isSolid() && !var2.i(var7)) {
            return true;
         }
      }

      return false;
   }

   private boolean c(Block var1) {
      Material var2 = var1.getType();
      return var2 == Material.FIRE || var2 == Material.SOUL_FIRE;
   }

   boolean d(Block var1) {
      Material var2 = var1.getType();
      return var2.isAir()
         || var2 == Material.FIRE
         || var2 == Material.SOUL_FIRE
         || var2 == Material.SHORT_GRASS
         || var2 == Material.TALL_GRASS
         || var2 == Material.SNOW;
   }

   boolean a(Block var1, Player var2) {
      if (var1 != null && var2 != null) {
         Location var3 = var2.getLocation();
         double var4 = var1.getX() + 0.5;
         double var6 = var1.getZ() + 0.5;
         double var8 = Math.sqrt(Math.pow(var4 - var3.getX(), 2.0) + Math.pow(var6 - var3.getZ(), 2.0));
         if (var8 > 0.82) {
            return false;
         } else {
            int var10 = var1.getY();
            int var11 = (int)Math.floor(var3.getY());
            return var10 >= var11 && var10 <= var11 + 1;
         }
      } else {
         return false;
      }
   }

   private void a(m1 var1, Block var2, long var3, long var5) {
      if (var1 != null && var2 != null && var2.getWorld() != null) {
         var1.mC = var2.getLocation().clone();
         var1.mD = Math.max(var1.mD, var3 + Math.max(500L, var5));
      }
   }

   private void b(m1 var1, Block var2, long var3, long var5) {
      if (var1 != null && var2 != null && var2.getWorld() != null) {
         var1.mE = var2.getLocation().clone();
         var1.mF = Math.max(var1.mF, var3 + Math.max(1000L, var5));
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

   private boolean b(m1 var1, Block var2, long var3) {
      if (var1 != null && var2 != null && var1.mC != null) {
         return var3 <= var1.mD && var1.mC.getWorld() != null && var2.getWorld() != null && var1.mC.getWorld().equals(var2.getWorld())
            ? var1.mC.getBlockX() == var2.getX() && var1.mC.getBlockY() == var2.getY() && var1.mC.getBlockZ() == var2.getZ()
            : false;
      } else {
         return false;
      }
   }

   private void a(m1 var1, Block var2) {
      if (var1 != null && var2 != null && this.b(var1, var2, System.currentTimeMillis())) {
         if (var2.getType() != Material.RESPAWN_ANCHOR) {
            var1.mC = null;
            var1.mD = 0L;
            if (var1.mE != null
               && var1.mE.getWorld() != null
               && var1.mE.getWorld().equals(var2.getWorld())
               && var1.mE.getBlockX() == var2.getX()
               && var1.mE.getBlockY() == var2.getY()
               && var1.mE.getBlockZ() == var2.getZ()) {
               var1.mE = null;
               var1.mF = 0L;
            }
         }
      }
   }

   boolean b(Player var1, Player var2, bw var3) {
      av var4 = this.bQ.bZ();
      var4.aj(var1);
      boolean var5 = var3.dF().getType() == Material.RESPAWN_ANCHOR;
      boolean var6 = var5 && this.g(var3.dF());
      boolean var7 = var6 || var4.b(var1, Material.GLOWSTONE) >= 1;
      return this.i(var1, var2)
         && var7
         && (var5 ? this.c(var1, var2, var3.dF()) : var4.b(var1, Material.RESPAWN_ANCHOR) >= 1 && this.b(var1, var2, var3.dF()))
         && (!var3.dC() || this.d(var1, var2, var3.dE()));
   }

   boolean a(Player var1, Player var2, bw var3, boolean var4) {
      av var5 = this.bQ.bZ();
      if (!this.i(var1, var2)) {
         return false;
      } else {
         var5.aj(var1);
         if (var4) {
            return (!var3.dC() || var3.dE().getType() == Material.GLOWSTONE)
               && (var3.dF().getType() == Material.RESPAWN_ANCHOR ? this.c(var1, var2, var3.dF()) : this.b(var1, var2, var3.dF()));
         } else {
            boolean var6 = var3.dF().getType() == Material.RESPAWN_ANCHOR && this.g(var3.dF());
            return (!var3.dC() || var3.dE().getType() == Material.GLOWSTONE)
               && var3.dF().getType() == Material.RESPAWN_ANCHOR
               && this.a(var1, var3)
               && (var6 || var5.b(var1, Material.GLOWSTONE) >= 1);
         }
      }
   }

   boolean i(Player var1, Player var2) {
      return var1 != null
         && var2 != null
         && var1.isValid()
         && var2.isValid()
         && !var1.isDead()
         && !var2.isDead()
         && var1.getWorld().equals(var2.getWorld())
         && this.bQ.e(var1.getWorld());
   }

   boolean a(Player var1, Player var2, bw var3, ae var4) {
      av var5 = this.bQ.bZ();
      if (var1 == null || var2 == null || var3 == null) {
         return false;
      } else if (!this.i(var1, var2)) {
         return false;
      } else if (var3.dF().getType() == Material.RESPAWN_ANCHOR && this.g(var3.dF())) {
         if (var3.dG().distance(var2.getLocation().add(0.0, 1.0, 0.0)) > 8.0) {
            return var3.dC() ? this.a(var1, var3.dF(), var3.dE()) : this.b(var1, var3.dF());
         } else {
            double var6 = this.bQ.a(var3.dG(), var2);
            double var8 = this.bQ.a(var3.dG(), var1);
            double var10 = var3.dC() ? var8 * 0.48 : var8;
            double var12 = var1.getHealth() + Math.max(0.0, var1.getAbsorptionAmount());
            if (var10 >= var12 - (var3.dH() ? 2.0 : 1.0)) {
               return false;
            } else {
               return !this.bQ.a(var10, var12, Math.max(var6, var3.dc()), var3.dH()) && var10 > Math.max(1.5, var6 * 1.35)
                  ? false
                  : var6 >= 0.05 || var3.dc() >= 0.05 || var10 <= 0.75;
            }
         }
      } else {
         return false;
      }
   }

   private boolean a(Player var1, Player var2, bw var3, ae var4, m1 var5, boolean var6) {
      av var7 = this.bQ.bZ();
      if (var1 != null && var3 != null && var3.dF() != null) {
         if (var3.dF().getType() != Material.RESPAWN_ANCHOR) {
            return false;
         } else if (!this.bQ.e(var3.dF().getWorld())) {
            return false;
         } else {
            boolean var8 = this.b(var1, var3);
            boolean var9 = this.a(var1, var3) || var8;
            if (!var9) {
               if (var5 != null) {
                  long var18 = System.currentTimeMillis();
                  this.a(var5, var3.dF(), var18, 1200L);
                  if (this.g(var3.dF())) {
                     var5.mW = Math.max(var5.mW, var18 + 500L);
                  }
               }

               return false;
            } else {
               boolean var10 = var5 != null && (this.a(var5, var3.dF(), System.currentTimeMillis()) || this.b(var5, var3.dF(), System.currentTimeMillis()));
               boolean var11 = false;
               if (!this.g(var3.dF())) {
                  if (var7.b(var1, Material.GLOWSTONE) < 1) {
                     return false;
                  }

                  var7.a(var1, Material.GLOWSTONE);
                  this.e(var1, var3.dF());
                  var1.swingMainHand();
                  if (var10) {
                     this.b(var5, var3.dF(), System.currentTimeMillis(), 12000L);
                  }

                  var11 = true;
               }

               if (var5 != null) {
                  long var12 = System.currentTimeMillis();
                  this.a(var5, var3.dF(), var12, 1200L);
                  if (var11) {
                     long var14 = this.f(this.d(var4));
                     var5.mX = Math.max(var5.mX, var12 + var14);
                  }

                  var5.mW = Math.max(var5.mW, var12 + 650L);
               }

               if (!this.g(var3.dF())) {
                  return false;
               } else {
                  long var19 = System.currentTimeMillis();
                  if (var5 != null && var5.mX > var19) {
                     var5.mW = Math.max(var5.mW, var5.mX + 500L);
                     return false;
                  } else {
                     boolean var20 = this.c(var1, var2, var3);
                     boolean var15 = var5 != null && (this.b(var5, var3.dF(), var19) || this.a(var5, var3.dF(), var19));
                     boolean var16 = this.a(var1, var2, var3, var4) || this.d(var1, var2, var3) || var15 && var20 || var6 && var20;
                     if (!var16) {
                        if (var5 != null && var20) {
                           var5.mW = Math.max(var5.mW, System.currentTimeMillis() + 650L);
                        }

                        return false;
                     } else {
                        var7.a(var1, Material.RESPAWN_ANCHOR, Material.GLOWSTONE, Material.END_CRYSTAL);
                        var1.swingMainHand();
                        boolean var17 = this.c(var1, var3);
                        if (var17) {
                           if (var5 != null) {
                              var5.mX = 0L;
                           }

                           this.a(var5, var3.dF());
                        }

                        if (!var17 && var5 != null && var3.dF().getType() == Material.RESPAWN_ANCHOR) {
                           var5.mW = Math.max(var5.mW, System.currentTimeMillis() + 500L);
                        }

                        return var17;
                     }
                  }
               }
            }
         }
      } else {
         return false;
      }
   }

   private boolean b(Player var1, bw var2) {
      if (var1 != null && var2 != null && var2.dF() != null && var2.dF().getWorld() != null) {
         if (!var1.getWorld().equals(var2.dF().getWorld())) {
            return false;
         } else {
            Location var3 = var2.dG();
            if (var3 == null) {
               var3 = var2.dF().getLocation().add(0.5, 0.5, 0.5);
            }

            double var4 = 5.050000000000001;
            return var1.getEyeLocation().distance(var3) <= var4
               && this.bQ.bZ().d(var1.getLocation(), var3) <= var4
               && this.a(var1.getEyeLocation(), var2.dF(), var4);
         }
      } else {
         return false;
      }
   }

   private boolean c(Player var1, Player var2, bw var3) {
      if (var1 != null && var3 != null && var3.dF() != null && var3.dF().getType() == Material.RESPAWN_ANCHOR) {
         Location var4 = var3.dG();
         if (var4 == null) {
            var4 = var3.dF().getLocation().add(0.5, 0.5, 0.5);
         }

         double var5 = var2 != null && var2.isValid() && !var2.isDead() && var1.getWorld().equals(var2.getWorld()) ? this.bQ.a(var4, var2) : 0.0;
         double var7 = this.bQ.a(var4, var1);
         double var9 = var3.dC() ? var7 * 0.48 : var7;
         double var11 = var1.getHealth() + Math.max(0.0, var1.getAbsorptionAmount());
         return var9 >= var11 - (var3.dH() ? 2.0 : 1.0)
            ? false
            : this.bQ.a(var9, var11, Math.max(var5, var3.dc()), var3.dH()) || var9 <= Math.max(1.5, Math.max(var5, var3.dc()) * 1.35);
      } else {
         return false;
      }
   }

   private boolean d(Player var1, Player var2, bw var3) {
      if (var1 != null && var3 != null && var3.dF() != null && var3.dF().getType() == Material.RESPAWN_ANCHOR) {
         Location var4 = var3.dG();
         if (var4 == null) {
            var4 = var3.dF().getLocation().add(0.5, 0.5, 0.5);
         }

         double var5 = var2 != null && var2.isValid() && !var2.isDead() && var1.getWorld().equals(var2.getWorld()) ? this.bQ.a(var4, var2) : 0.0;
         double var7 = this.bQ.a(var4, var1);
         double var9 = var3.dC() ? var7 * 0.48 : var7;
         double var11 = var1.getHealth() + Math.max(0.0, var1.getAbsorptionAmount());
         if (var9 >= var11 - (var3.dH() ? 2.0 : 1.0)) {
            return false;
         } else {
            return !this.bQ.a(var9, var11, Math.max(var5, var3.dc()), var3.dH()) && var9 > Math.max(1.5, Math.max(var5, var3.dc()) * 1.35)
               ? false
               : var3.dc() >= 0.05 || var5 >= 0.05 || var9 <= 0.75 || var3.dL() || var3.dJ() || var3.dI();
         }
      } else {
         return false;
      }
   }

   boolean c(Player var1, bw var2) {
      World var3 = var2.dG().getWorld();
      if (var3 != null && this.bQ.e(var3)) {
         if (var2.dF().getType() == Material.RESPAWN_ANCHOR && this.g(var2.dF())) {
            boolean var5 = com.mlc.mlcbot.combat.CombatRuntime.explodeAnchor(var1, var2.dF(), var2.dG());
            this.bQ.cc();
            return var5;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   void e(Player bot, Block var1) {
      boolean wasCharged = this.g(var1);
      if (com.mlc.mlcbot.combat.CombatRuntime.charge(bot, var1) && !wasCharged) this.f(var1);
   }

   private void f(Block var1) {
      if (var1 != null && var1.getWorld() != null) {
         var1.getWorld().playSound(var1.getLocation().add(0.5, 0.5, 0.5), Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, SoundCategory.BLOCKS, 1.0F, 1.0F);
      }
   }

   boolean g(Block var1) {
      return var1.getType() == Material.RESPAWN_ANCHOR && var1.getBlockData() instanceof RespawnAnchor var2 && var2.getCharges() > 0;
   }

   void a(m1 var1, Location var2) {
      var1.mB = var2 != null ? var2.clone() : null;
   }

   void a(Player var1, m1 var2) {
      av var3 = this.bQ.bZ();
      if (var1 != null && var1.isValid() && !var1.isDead()) {
         var3.aj(var1);
         var3.a(var1, Material.END_CRYSTAL, Material.RESPAWN_ANCHOR);
      }

      var2.mG = false;
      var2.mB = null;
      var2.lt = false;
      var2.mq = false;
   }

   Location a(Player var1, Player var2, Location var3) {
      av var4 = this.bQ.bZ();
      if (var3 != null && var3.getWorld() != null) {
         World var5 = var3.getWorld();
         Location var6 = null;
         double var7 = -999.0;

         for (int var9 = -2; var9 <= 2; var9++) {
            for (int var10 = -2; var10 <= 2; var10++) {
               Location var11 = var3.clone().add(var9, -1.0, var10);
               Location var12 = var4.a(var5, var11, var3.getY() - 3.0);
               if (var12 != null && !(var12.distance(var1.getLocation()) > 12.0) && var4.h(var1.getEyeLocation(), var12.clone().add(0.0, 1.0, 0.0))) {
                  double var13 = var4.d(var12, var2.getLocation());
                  double var15 = var4.d(var12, var3);
                  double var17 = 8.0 - var13 - var15 * 0.5;
                  if (this.bQ.r(var2) && var13 <= 2.2) {
                     var17 += 3.0;
                  }

                  if (var17 > var7) {
                     var7 = var17;
                     var6 = var12;
                  }
               }
            }
         }

         return var6;
      } else {
         return null;
      }
   }

   void a(NPC var1, Player var2, Player var3, ae var4, m1 var5, bw var6) {
      av var7 = this.bQ.bZ();
      long var8 = System.currentTimeMillis();
      if (this.i(var2, var3)) {
         if (var7.a(var5, var4, var8) && !var7.c(var5, var4, var8)) {
            if (!var7.e(var5, var8)) {
               if (var7.a(var2, Material.ENDER_PEARL)) {
                  Location var10 = this.a(var2, var3, var6.dG());
                  if (var10 != null) {
                     Vector var11 = var7.i(var2.getEyeLocation(), var10);
                     if (var11 == null && var7.h(var2.getEyeLocation(), var10.clone().add(0.0, 1.0, 0.0))) {
                        var11 = var7.j(var2.getEyeLocation(), var10);
                     }

                     if (var11 != null && var7.a(var11)) {
                        var7.a(var1, var2, var3, var4, var5, var10, var11);
                     }
                  }
               }
            }
         }
      }
   }
}
