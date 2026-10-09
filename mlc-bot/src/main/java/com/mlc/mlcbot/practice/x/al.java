// Extracted from the user-provided PracticeBot-Source; strategy logic retained.
package com.mlc.mlcbot.practice.x;

import com.mlc.mlcbot.practice.BotTrait;
import java.util.Locale;
import java.util.UUID;
import com.mlc.mlcbot.practice.bridge.NPC;
import com.mlc.mlcbot.practice.bridge.FollowTrait;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;

public final class al {
   private static final double aZ = 12.0;
   private static final double bA = 0.9;
   private final av bB;
   private final n0 bC;
   private final m0 bD;

   public al(av var1, n0 var2, m0 var3) {
      this.bB = var1;
      this.bC = var2;
      this.bD = var3;
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public Location q(Player var1) {
      if (var1 == null) {
         return null;
      } else {
         Location var2 = var1.getLocation();
         if (var2 == null) {
            return null;
         } else {
            Pose var5 = var1.getPose();

            return var2.clone().add(0.0, switch (var5) {
               case SWIMMING, FALL_FLYING, SPIN_ATTACK -> 0.4;
               case SNEAKING -> 1.27;
               case SLEEPING -> 0.2;
               default -> 1.62;
            }, 0.0);
         }
      }
   }

   public void a(NPC var1, Player var2, Player var3, ae var4) {
      if (var1 != null && var2 != null) {
         m1 var5 = this.bC.i(var2.getUniqueId());
         Player var6 = this.a(var5, var3);
         long var7 = System.currentTimeMillis();
         if (var5 != null && var6 != null && this.bB.m(var2, var6, var4, var5, var7)) {
            if (var5.mG && var5.mB != null) {
               this.b(var1, var2, var5.mB, var5);
               return;
            }

            UUID var9 = var5.mp;
            if (var9 != null) {
               if (Bukkit.getEntity(var9) instanceof EnderCrystal var11 && var11.isValid() && !var11.isDead()) {
                  if (!this.a(var2, var6, var11, var5, var7)) {
                     this.c(var1, var2, var11.getLocation().clone().add(0.0, 0.5, 0.0), var5);
                     return;
                  }

                  this.bB.i(var5);
               } else {
                  this.bB.i(var5);
               }
            }

            Location var13 = this.a(var2, var6, var5, var7);
            if (var13 != null) {
               this.c(var1, var2, var13, var5);
               return;
            }

            this.b(var5);
         }

         if (var5 != null && var6 != null && this.e(var2, var6) && this.a(var2, var6, var5)) {
            this.w(var1);
            this.a(var1, var2, var6, var5, "OVERHEAD_FORWARD_PRIORITY");
         } else {
            this.w(var1);
            if (!this.e(var2, var6)) {
               float[] var12 = this.bD.f(var2.getUniqueId());
               if (var12 != null) {
                  var12[0] = var2.getLocation().getYaw();
                  var12[1] = var2.getLocation().getPitch();
               }
            } else if (var5 == null) {
               this.a(var2, this.q(var6));
            } else {
               this.a(var1, var2, this.q(var6), var5, true);
            }
         }
      }
   }

   public void b(NPC var1, Player var2, Player var3, ae var4) {
      if (var1 != null && var2 != null) {
         m1 var5 = this.bC.i(var2.getUniqueId());
         Player var6 = this.a(var5, var3);
         long var7 = System.currentTimeMillis();
         if (var5 == null || !this.bB.cI() || var7 - var5.kS >= 140L) {
            if (var5 != null) {
               var5.kS = var7;
               if (var5.mG && var5.mB != null) {
                  this.b(var1, var2, var5.mB, var5);
                  return;
               }

               UUID var9 = var5.mp;
               if (var9 != null) {
                  if (Bukkit.getEntity(var9) instanceof EnderCrystal var11 && var11.isValid() && !var11.isDead()) {
                     this.c(var1, var2, var11.getLocation().clone().add(0.0, 0.5, 0.0), var5);
                     return;
                  }

                  this.bB.i(var5);
               }

               if (var5.lW != null && var7 <= var5.lX) {
                  this.c(var1, var2, var5.lW, var5);
                  return;
               }
            }

            this.w(var1);
            if (this.e(var2, var6)) {
               if (var5 == null) {
                  this.a(var2, this.q(var6));
               } else {
                  this.a(var1, var2, this.q(var6), var5, true);
               }
            }
         }
      }
   }

   public void a(NPC var1, Player var2, Location var3, m1 var4) {
      this.a(var1, var2, var3, var4, false);
   }

   public void a(NPC var1, Player var2, Location var3, m1 var4, boolean var5) {
      if (var3 != null && var2 != null && var1 != null) {
         long var6 = System.currentTimeMillis();
         float var8 = var2.getLocation().getYaw();
         float var9 = var2.getLocation().getPitch();
         String var10 = this.bB.cG();
         boolean var11 = this.ba(var10);
         if (var11 && var4 != null) {
            this.b(var4);
         }

         Location var12 = var2.getEyeLocation();
         double var13 = var3.getX() - var12.getX();
         double var15 = var3.getY() - var12.getY();
         double var17 = var3.getZ() - var12.getZ();
         double var19 = Math.sqrt(var13 * var13 + var17 * var17);
         if (var19 < 0.001) {
            var19 = 0.001;
         }

         float var21 = (float)Math.toDegrees(Math.atan2(-var13, var17));
         float var22 = (float)(-Math.toDegrees(Math.atan2(var15, var19)));
         var22 = Math.max(-75.0F, Math.min(75.0F, var22));
         if (!var5 && var4 != null && !var4.lt) {
            Player var23 = this.a(var4, null);
            if (var23 != null && this.e(var2, var23) && this.a(var2, var23, var4)) {
               var22 = Math.max(var22, -8.0F);
            }
         }

         float[] var30 = this.bD.a(var2.getUniqueId(), var1x -> new float[]{var2.getLocation().getYaw(), var2.getLocation().getPitch()});
         if (var5 || !var4.lt) {
            var30[0] = var2.getLocation().getYaw();
            var30[1] = var2.getLocation().getPitch();
         }

         float var24 = var4.lt && !var5 ? 0.5F : 0.62F;
         float var25 = var21 - var30[0];

         while (var25 > 180.0F) {
            var25 -= 360.0F;
         }

         while (var25 < -180.0F) {
            var25 += 360.0F;
         }

         float var26 = var30[0] + var25 * var24;
         float var27 = var30[1] + (var22 - var30[1]) * var24;
         var27 = Math.max(-75.0F, Math.min(75.0F, var27));
         var30[0] = var26;
         var30[1] = var27;
         var2.setRotation(var26, var27);
         Player var28 = this.a(var4, null);
      }
   }

   public void b(NPC var1, Player var2, Location var3, m1 var4) {
      this.c(var1, var2, var3, var4);
   }

   public boolean a(Player var1, Player var2, m1 var3) {
      if (var1 == null || var2 == null) {
         return false;
      } else if (var3 != null && var3.lt) {
         return false;
      } else {
         Location var4 = this.bB.c(var1, var2, var3);
         if (var4 != null) {
            return true;
         } else {
            double var5 = var2.getLocation().getY() - var1.getLocation().getY();
            double var7 = this.bB.d(var1.getLocation(), var2.getLocation());
            return var5 > 0.9 && var7 <= 12.0;
         }
      }
   }

   public void w(NPC var1) {
      var1.getNavigator().cancelNavigation();
      FollowTrait var2 = (FollowTrait)var1.getTraitNullable(FollowTrait.class);
      if (var2 != null && var2.getFollowing() != null) {
         var2.follow(null);
      }
   }

   public void a(Player var1, Location var2) {
      if (var1 != null && var2 != null) {
         long var3 = System.currentTimeMillis();
         float var5 = var1.getLocation().getYaw();
         float var6 = var1.getLocation().getPitch();
         m1 var7 = this.bC.i(var1.getUniqueId());
         Location var8 = var1.getEyeLocation();
         double var9 = var2.getX() - var8.getX();
         double var11 = var2.getY() - var8.getY();
         double var13 = var2.getZ() - var8.getZ();
         double var15 = Math.sqrt(var9 * var9 + var13 * var13);
         if (var15 < 0.001) {
            var15 = 0.001;
         }

         float var17 = (float)Math.toDegrees(Math.atan2(-var9, var13));
         float var18 = (float)(-Math.toDegrees(Math.atan2(var11, var15)));
         float var19 = Math.max(-75.0F, Math.min(75.0F, var18));
         var1.setRotation(var17, var19);
         String var20 = this.bB.cG();
      }
   }

   public void c(NPC var1, Player var2, Location var3, m1 var4) {
      if (var3 != null && var2 != null && var1 != null && var4 != null) {
         long var5 = System.currentTimeMillis();
         float var7 = var2.getLocation().getYaw();
         float var8 = var2.getLocation().getPitch();
         var4.lt = true;
         this.w(var1);
         Location var9 = this.a(var2, var3, var4, var5);
         Location var10 = var2.getEyeLocation();
         double var11 = var9.getX() - var10.getX();
         double var13 = var9.getY() - var10.getY();
         double var15 = var9.getZ() - var10.getZ();
         double var17 = Math.sqrt(var11 * var11 + var15 * var15);
         if (var17 < 0.001) {
            var17 = 0.001;
         }

         float var19 = (float)Math.toDegrees(Math.atan2(-var11, var15));
         float var20 = (float)(-Math.toDegrees(Math.atan2(var13, var17)));
         var20 = Math.max(-75.0F, Math.min(75.0F, var20));
         float[] var21 = this.bD.a(var2.getUniqueId(), var1x -> new float[]{var2.getLocation().getYaw(), var2.getLocation().getPitch()});
         float var22 = var19 - var21[0];

         while (var22 > 180.0F) {
            var22 -= 360.0F;
         }

         while (var22 < -180.0F) {
            var22 += 360.0F;
         }

         float var23 = 0.34F;
         float var24 = var21[0] + var22 * var23;
         float var25 = var21[1] + (var20 - var21[1]) * var23;
         var25 = Math.max(-75.0F, Math.min(75.0F, var25));
         var21[0] = var24;
         var21[1] = var25;
         var2.setRotation(var24, var25);
         String var26 = this.bB.cG();
      }
   }

   private Location a(Player var1, Location var2, m1 var3, long var4) {
      Location var6 = this.a(var3);
      if (var6 != null) {
         var3.lW = var6.clone();
         var3.lX = var4 + 450L;
         return var6;
      } else {
         if (var3.oi != null && var4 - var3.oj <= 3200L) {
            Location var7 = var3.oi.getBlock().getLocation().add(0.5, 1.0, 0.5);
            boolean var8 = var3.lt || var3.lB || var3.mq || var3.mp != null || var4 < var3.lC || var4 < var3.mv && var3.mw != null;
            if (var8 || this.e(var7, var2) && this.c(var7, var2) <= 1.44) {
               var3.lW = var7.clone();
               var3.lX = var4 + (var8 ? 650L : 450L);
               return var7;
            }

            this.b(var3);
         }

         if (var3.lW != null && var4 <= var3.lX && this.e(var3.lW, var2) && this.c(var3.lW, var2) <= 1.44) {
            return var3.lW.clone();
         } else {
            Location var9 = var2.clone();
            var3.lW = var9.clone();
            var3.lX = var4 + 300L;
            return var9;
         }
      }
   }

   private Location a(m1 var1) {
      if (var1 != null && var1.mp != null) {
         if (Bukkit.getEntity(var1.mp) instanceof EnderCrystal var3 && var3.isValid() && !var3.isDead()) {
            return var3.getLocation().clone().add(0.0, 0.5, 0.0);
         } else {
            this.bB.i(var1);
            return null;
         }
      } else {
         return null;
      }
   }

   private Location a(Player var1, Player var2, m1 var3, long var4) {
      if (var1 != null && var2 != null && var3 != null && var1.getWorld().equals(var2.getWorld())) {
         Location var6 = this.a(var1, var2, var3.oi, var3.oj, var4, 900L);
         if (var6 != null) {
            return var6;
         } else {
            return var3.lV != null && var3.lV.equals(var2.getUniqueId()) ? this.a(var1, var2, var3.lT, var3.lU, var4, 750L) : null;
         }
      } else {
         return null;
      }
   }

   private Location a(Player var1, Player var2, Location var3, long var4, long var6, long var8) {
      if (var3 != null && var4 > 0L && var6 - var4 <= var8 && var1.getWorld().equals(var3.getWorld())) {
         Block var10 = var3.getBlock();
         Material var11 = var10.getType();
         if (var11 != Material.OBSIDIAN && var11 != Material.BEDROCK) {
            return null;
         } else {
            Location var12 = var10.getLocation().add(0.5, 1.0, 0.5);
            if (!var2.getWorld().equals(var12.getWorld())) {
               return null;
            } else if (var1.getEyeLocation().distance(var12) > 5.35) {
               return null;
            } else {
               return this.d(var1.getLocation(), var2.getLocation()) > 14.0 && this.d(var12, var2.getLocation()) > 5.2 ? null : var12;
            }
         }
      } else {
         return null;
      }
   }

   private double d(Location var1, Location var2) {
      double var3 = var1.getX() - var2.getX();
      double var5 = var1.getZ() - var2.getZ();
      return Math.sqrt(var3 * var3 + var5 * var5);
   }

   private boolean e(Location var1, Location var2) {
      return var1 != null && var2 != null && var1.getWorld() != null && var2.getWorld() != null && var1.getWorld().equals(var2.getWorld());
   }

   private double c(Location var1, Location var2) {
      double var3 = var1.getX() - var2.getX();
      double var5 = var1.getZ() - var2.getZ();
      return var3 * var3 + var5 * var5;
   }

   private boolean a(Player var1, Player var2, EnderCrystal var3, m1 var4, long var5) {
      if (var1 != null && var2 != null && var3 != null && var4 != null) {
         if (!var1.getWorld().equals(var2.getWorld()) || !var1.getWorld().equals(var3.getWorld())) {
            return false;
         } else if (!var4.mq && var5 - Math.max(var4.kD, var4.kE) >= 180L) {
            Location var7 = var1.getLocation();
            Location var8 = var2.getLocation();
            Location var9 = var3.getLocation();
            double var10 = var8.getX() - var7.getX();
            double var12 = var8.getZ() - var7.getZ();
            double var14 = var9.getX() - var7.getX();
            double var16 = var9.getZ() - var7.getZ();
            double var18 = var10 * var10 + var12 * var12;
            double var20 = var14 * var14 + var16 * var16;
            if (!(var18 < 1.0E-4) && !(var20 < 1.0E-4)) {
               double var22 = Math.sqrt(var18);
               if (var22 <= 3.35) {
                  return false;
               } else {
                  double var24 = this.d(var9, var8);
                  if (var24 <= 4.25) {
                     return false;
                  } else {
                     double var26 = (var10 * var14 + var12 * var16) / Math.sqrt(var18 * var20);
                     if (var26 < -0.18) {
                        double var28 = this.bB.b(var9, var1);
                        return var28 < 5.0;
                     } else {
                        return var24 > var22 + 1.25 && this.d(var7, var9) <= 5.25;
                     }
                  }
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private void a(NPC var1, Player var2, Player var3, m1 var4, String var5) {
      if (var2 != null) {
         float var6 = var2.getLocation().getYaw();
         float var7 = var2.getLocation().getPitch();
         float var8 = var6;
         if (var3 != null && var3.isValid() && !var3.isDead() && var2.getWorld().equals(var3.getWorld())) {
            Location var9 = var2.getEyeLocation();
            Location var10 = var3.getLocation();
            double var11 = var10.getX() - var9.getX();
            double var13 = var10.getZ() - var9.getZ();
            if (var11 * var11 + var13 * var13 > 1.0E-4) {
               var8 = (float)Math.toDegrees(Math.atan2(-var11, var13));
            }
         }

         float var15 = var8 - var6;

         while (var15 > 180.0F) {
            var15 -= 360.0F;
         }

         while (var15 < -180.0F) {
            var15 += 360.0F;
         }

         float var16 = var6 + var15 * 0.55F;
         float var17 = var7 * 0.45F;
         if (Math.abs(var17) < 0.35F) {
            var17 = 0.0F;
         }

         var2.setRotation(var16, var17);
         float var18 = var17;
         float[] var14 = this.bD.a(var2.getUniqueId(), var2x -> new float[]{var16, var18});
         var14[0] = var16;
         var14[1] = var17;
      }
   }

   private boolean ba(String var1) {
      String var2 = var1 == null ? "" : var1.toLowerCase(Locale.ROOT);
      return var2.contains("melee")
         || var2.contains("dtap")
         || var2.contains("sword")
         || var2.contains("forceidleswordhit")
         || var2.contains("pendingsword")
         || var2.contains("hitwithkb");
   }

   private void b(m1 var1) {
      if (var1 != null) {
         var1.lW = null;
         var1.lX = 0L;
         var1.lt = false;
         var1.lB = false;
      }
   }

   private Player a(m1 var1, Player var2) {
      return var1 != null && var1.lg != null && Bukkit.getEntity(var1.lg) instanceof Player var4 && BotTrait.isLiveCombatTarget(var4) ? var4 : var2;
   }

   private boolean e(Player var1, Player var2) {
      return BotTrait.isLiveCombatTarget(var2) && var1.getWorld().equals(var2.getWorld());
   }

   private String a(String var1, boolean var2, boolean var3) {
      if (var3) {
         return "anchor";
      } else if (var2) {
         return "crystal";
      } else {
         String var4 = var1 == null ? "" : var1.toLowerCase(Locale.ROOT);
         if (var4.contains("anchor")) {
            return "anchor";
         } else if (var4.contains("pearl")) {
            return "pearl";
         } else if (var4.contains("overhead")) {
            return "overhead";
         } else if (var4.contains("movement") || var4.contains("traversal") || var4.contains("jump")) {
            return "movement";
         } else if (var4.contains("melee") || var4.contains("dtap") || var4.contains("sword") || var4.contains("mace")) {
            return "melee";
         } else {
            return !var4.contains("crystal") && !var4.contains("obsidian") ? "target" : "crystal";
         }
      }
   }
}
