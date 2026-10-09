// Extracted from the user-provided PracticeBot-Source; strategy logic retained.
package com.mlc.mlcbot.practice.x;

import static com.mlc.mlcbot.nms.EntityGroundState.isOnGround;

import java.util.Locale;
import java.util.Objects;
import com.mlc.mlcbot.practice.bridge.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/** 生成水晶放置、击破、黑曜石压制和恢复计划。 原源码：x/ba.java。 */
final class CrystalCombatPlanner {
   private final CrystalCombatController eo;
   private final CpvpCombatController ep;

   CrystalCombatPlanner(CrystalCombatController var1) {
      this.eo = var1;
      this.ep = var1.bZ();
   }

   CrystalCombatPlan k(Player var1, Player var2, CpvpSettings var3, CpvpCombatState var4, long var5) {
      CrystalCombatPlan var7 = this.g(var1, var2, var4, var5);
      if (var7 != null) {
         return var7;
      } else {
         return !this.a(var1, var2, var4, var5, "crystal-pressure-plan") ? CrystalCombatPlan.bi("HIGH_LOAD_THROTTLED") : this.n(null, var1, var2, var3, var4, var5);
      }
   }

   CrystalCombatPlan n(NPC var1, Player var2, Player var3, CpvpSettings var4, CpvpCombatState var5, long var6) {
      CrystalCombatPlan var8 = this.g(var2, var3, var5, var6);
      if (var8 != null) {
         return var8;
      } else {
         CrystalCombatPlan var9 = this.v(var2, var3, var4, var5, var6);
         this.a(var9, var5, var6);
         if (var5 != null) {
            var5.lE = var9;
            var5.lF = var6;
            var5.lG = var3 == null ? null : var3.getUniqueId();
            Location var10 = var2 == null ? null : var2.getLocation();
            Location var11 = var3 == null ? null : var3.getLocation();
            var5.lH = var10 == null ? Integer.MIN_VALUE : var10.getBlockX();
            var5.lI = var10 == null ? Integer.MIN_VALUE : var10.getBlockY();
            var5.lJ = var10 == null ? Integer.MIN_VALUE : var10.getBlockZ();
            var5.lK = var11 == null ? Integer.MIN_VALUE : var11.getBlockX();
            var5.lL = var11 == null ? Integer.MIN_VALUE : var11.getBlockY();
            var5.lM = var11 == null ? Integer.MIN_VALUE : var11.getBlockZ();
            var5.lN = var5.kD;
            var5.lO = var5.kE;
            var5.lP = var5.kH;
            var5.lQ = this.am(var2);
            var5.lR = this.an(var2);
            var5.lS = this.am(var3);
         }

         return var9;
      }
   }

   private CrystalCombatPlan g(Player var1, Player var2, CpvpCombatState var3, long var4) {
      if (var1 != null && var2 != null && var3 != null && var3.lE != null) {
         if (!Objects.equals(var3.lG, var2.getUniqueId())) {
            return null;
         } else if (var3.lF == var4) {
            return this.b(var3.lE) ? var3.lE : null;
         } else {
            long var6 = var4 - var3.lF;
            if (var6 >= 0L && var6 <= this.ep.k(this.m(var3, var4))) {
               Location var8 = var1.getLocation();
               Location var9 = var2.getLocation();
               if (var3.lH == var8.getBlockX()
                  && var3.lI == var8.getBlockY()
                  && var3.lJ == var8.getBlockZ()
                  && var3.lK == var9.getBlockX()
                  && var3.lL == var9.getBlockY()
                  && var3.lM == var9.getBlockZ()
                  && var3.lN == var3.kD
                  && var3.lO == var3.kE
                  && var3.lP == var3.kH
                  && var3.lQ == this.am(var1)
                  && var3.lR == this.an(var1)
                  && var3.lS == this.am(var2)) {
                  return this.b(var3.lE) ? var3.lE : null;
               } else {
                  return null;
               }
            } else {
               return null;
            }
         }
      } else {
         return null;
      }
   }

   private int am(Player var1) {
      return var1 == null ? Integer.MIN_VALUE : (int)Math.round(var1.getHealth() * 10.0);
   }

   private int an(Player var1) {
      return var1 == null ? Integer.MIN_VALUE : (int)Math.round(Math.max(0.0, var1.getAbsorptionAmount()) * 10.0);
   }

   private boolean m(CpvpCombatState var1, long var2) {
      return var1.mq || var1.lB || var1.lt || var1.mp != null || var2 < var1.mv;
   }

   private boolean a(Player var1, Player var2, CpvpCombatState var3, long var4, String var6) {
      if (var3 == null || this.m(var3, var4)) {
         return true;
      } else if (!this.ep.cI()) {
         return true;
      } else {
         return !this.ep.b(var1, var3) && (var2 == null || !(var2.getHealth() <= 8.0)) ? this.ep.b(var1, var6, false) : true;
      }
   }

   private boolean a(CrystalCombatPlan var1) {
      return var1 != null && var1.dU() == CrystalPlanStatus.NONE && "HIGH_LOAD_THROTTLED".equals(var1.B());
   }

   private boolean b(CrystalCombatPlan var1) {
      if (var1 == null) {
         return false;
      } else {
         EnderCrystal var2 = var1.eg();
         return var2 == null || var2.isValid() && !var2.isDead() ? var1.dT() : false;
      }
   }

   private CrystalCombatPlan v(Player var1, Player var2, CpvpSettings var3, CpvpCombatState var4, long var5) {
      Block var7 = var2 == null ? null : this.ep.y(var2);
      if (var1 == null || var2 == null || var3 == null || var4 == null) {
         return this.a(CrystalPlanStatus.INVALID_STALE, "MISSING_CONTEXT", var7, var7, null, null, null, null, 0.0, 0.0, 0.0, 0.0, false, false, false, false, false, false);
      } else if (var1.isValid() && !var1.isDead() && var2.isValid() && !var2.isDead()) {
         if (!var1.getWorld().equals(var2.getWorld())) {
            return this.a(
               CrystalPlanStatus.INVALID_STALE,
               "DIFFERENT_WORLD",
               var7,
               var7,
               null,
               null,
               null,
               null,
               var1.getHealth(),
               Math.max(0.0, var1.getAbsorptionAmount()),
               0.0,
               0.0,
               false,
               false,
               false,
               false,
               false,
               false
            );
         } else {
            CrystalCandidate var9 = this.a(var1, var2, var3, var4, var5, null);
            if (var9 != null) {
               CrystalSafetyEvaluation var15 = this.a(var1, var2, var9.cU().getLocation(), var9.cV());
               return new CrystalCombatPlan(
                  CrystalPlanStatus.BREAK_SAFE,
                  "BREAK_SAFE",
                  var7,
                  var7 != null ? var7 : var9.cV(),
                  var9.cV(),
                  null,
                  var9.cU().getLocation().clone(),
                  var15.dd(),
                  var15.dc(),
                  var15.i(),
                  var15.de(),
                  var15.cT(),
                  true,
                  this.ep.a(var1, var9.cU().getLocation(), var3),
                  true,
                  true,
                  true,
                  true,
                  var9.cU()
               );
            } else {
               ObsidianPlacementPlan var10 = this.ep.cy().an(var1, var2, var3, var4, var5);
               Block var11 = this.a(var10.dq(), var10.dr());
               if (var11 != null) {
                  CrystalCandidate var16 = this.a(var1, var2, var3, var4, var5, var11);
                  if (var16 != null) {
                     CrystalSafetyEvaluation var18 = this.a(var1, var2, var16.cU().getLocation(), var16.cV());
                     return new CrystalCombatPlan(
                        CrystalPlanStatus.BREAK_SAFE,
                        "BREAK_SAFE",
                        var7,
                        var7 != null ? var7 : var16.cV(),
                        var16.cV(),
                        null,
                        var16.cU().getLocation().clone(),
                        var18.dd(),
                        var18.dc(),
                        var18.i(),
                        var18.de(),
                        var18.cT(),
                        true,
                        this.ep.a(var1, var16.cU().getLocation(), var3),
                        true,
                        true,
                        true,
                        true,
                        var16.cU()
                     );
                  } else {
                     return this.a(var1, var2, var3, var4, var5, var7, var7, var11);
                  }
               } else {
                  Block var12 = this.w(var1, var2, var3, var4, var5);
                  if (var12 != null) {
                     Location var17 = var12.getLocation().add(0.5, 1.0, 0.5);
                     CrystalSafetyEvaluation var19 = this.a(var1, var2, var17, var12);
                     return new CrystalCombatPlan(
                        CrystalPlanStatus.CREATE_OBSIDIAN_PRESSURE,
                        "CREATE_OBSIDIAN_PRESSURE",
                        var7,
                        var7 != null ? var7 : var12,
                        null,
                        var12,
                        var17,
                        var19.dd(),
                        var19.dc(),
                        var19.i(),
                        var19.de(),
                        var19.cT(),
                        this.ep.g(var1.getEyeLocation(), var17),
                        this.ep.a(var1, var17, var3),
                        this.ep.a(var12.getY() + 1.0, var2.getLocation().getY()),
                        var1.getEyeLocation().distance(var12.getLocation().add(0.5, 0.5, 0.5)) <= 4.5,
                        true,
                        this.ep.a(var1, Material.OBSIDIAN) && this.ep.a(var1, Material.END_CRYSTAL),
                        null
                     );
                  } else {
                     String var13 = var10.dt() != null && !var10.dt().isBlank() ? var10.dt() : "INVALID_NO_BASE";
                     CrystalPlanStatus var14 = this.bb(var13);
                     return this.a(
                        var14,
                        var13,
                        var7,
                        var7,
                        null,
                        null,
                        null,
                        null,
                        var1.getHealth(),
                        Math.max(0.0, var1.getAbsorptionAmount()),
                        0.0,
                        0.0,
                        false,
                        false,
                        false,
                        false,
                        false,
                        this.ep.a(var1, Material.END_CRYSTAL)
                     );
                  }
               }
            }
         }
      } else {
         return this.a(
            CrystalPlanStatus.INVALID_STALE,
            "INVALID_ENTITY",
            var7,
            var7,
            null,
            null,
            null,
            null,
            var1.getHealth(),
            Math.max(0.0, var1.getAbsorptionAmount()),
            0.0,
            0.0,
            false,
            false,
            false,
            false,
            false,
            false
         );
      }
   }

   private CrystalCombatPlan a(Player var1, Player var2, CpvpSettings var3, CpvpCombatState var4, long var5, Block var7, Block var8, Block var9) {
      Location var10 = var9.getLocation().add(0.5, 1.0, 0.5);
      boolean var11 = this.ep.a(var1, Material.END_CRYSTAL);
      boolean var12 = this.a(var1, var2, var4, var5, var9);
      boolean var13 = this.ep.a(var9, var2, var4, var5);
      boolean var14 = var1.getEyeLocation().distance(var10) <= 4.5 && var1.getEyeLocation().distance(var10) >= 0.8;
      boolean var15 = this.ep.g(var1.getEyeLocation(), var10);
      boolean var16 = this.ep.a(var1, var10, var3);
      boolean var17 = this.ep.a(var1, var2, var9, var4, var5);
      CrystalSafetyEvaluation var18 = this.a(var1, var2, var10, var9);
      double var19 = var2.getLocation().getY() - (var9.getY() + 1.0);
      boolean var21 = this.b(var1, var2, var4, var5, var9);
      double var22 = var21 ? Math.min(0.05, this.ep.g(var19)) : this.ep.g(var19);
      boolean var24 = var18.dc() >= var22;
      boolean var25 = this.a(var1, var2, var9, var18);
      CrystalPlanStatus var26;
      String var27;
      if (!var11) {
         var26 = CrystalPlanStatus.INVALID_NO_RESOURCES;
         var27 = "INVALID_NO_RESOURCES";
      } else if (var12) {
         var26 = CrystalPlanStatus.INVALID_STALE;
         var27 = "WAITING_POST_MELEE_LIFT";
      } else if (!var13) {
         var26 = CrystalPlanStatus.INVALID_NO_SPACE;
         var27 = "INVALID_NO_SPACE";
      } else if (!var14) {
         var26 = CrystalPlanStatus.INVALID_OUT_OF_REACH;
         var27 = "INVALID_OUT_OF_REACH";
      } else if (!var15) {
         var26 = CrystalPlanStatus.INVALID_NO_LOS;
         var27 = "INVALID_NO_LOS";
      } else if (!var17) {
         var26 = CrystalPlanStatus.INVALID_VERTICAL;
         var27 = "INVALID_VERTICAL";
      } else if (!var24) {
         var26 = CrystalPlanStatus.INVALID_LOW_DAMAGE;
         var27 = "INVALID_LOW_DAMAGE";
      } else if (var25) {
         var26 = CrystalPlanStatus.INVALID_STALE;
         var27 = "BAD_SELF_DAMAGE_PLACEMENT:" + var18.cZ();
      } else if (var18.cS()) {
         var26 = CrystalPlanStatus.PLACE_SAFE;
         var27 = "PLACE_SAFE";
      } else if (this.c(var18) && this.a(var2, var9, var18)) {
         var26 = CrystalPlanStatus.SELF_DAMAGE_UNSAFE_RECOVERY;
         var27 = "SELF_DAMAGE_UNSAFE:" + var18.cX() + ":" + var18.cY();
      } else {
         var26 = CrystalPlanStatus.INVALID_STALE;
         var27 = "INVALID_STALE:" + var18.cX() + ":" + var18.cY();
      }

      return new CrystalCombatPlan(
         var26,
         var27,
         var7,
         var8 != null ? var8 : var9,
         var9,
         null,
         var10,
         var18.dd(),
         var18.dc(),
         var18.i(),
         var18.de(),
         var18.cT(),
         var15,
         var16,
         var17,
         var14,
         var13,
         var11,
         null
      );
   }

   private CrystalCombatPlan a(
      CrystalPlanStatus var1,
      String var2,
      Block var3,
      Block var4,
      Block var5,
      Block var6,
      Location var7,
      EnderCrystal var8,
      double var9,
      double var11,
      double var13,
      double var15,
      boolean var17,
      boolean var18,
      boolean var19,
      boolean var20,
      boolean var21,
      boolean var22
   ) {
      return new CrystalCombatPlan(
         var1, var2, var3, var4, CrystalCombatPlan.q(var5) ? var5 : null, var6, var7, var13, var15, var9, var11, 0.0, var17, var18, var19, var20, var21, var22, var8
      );
   }

   private Block a(Block var1, Block var2) {
      if (CrystalCombatPlan.q(var1)) {
         return var1;
      } else {
         return CrystalCombatPlan.q(var2) ? var2 : null;
      }
   }

   private Block w(Player var1, Player var2, CpvpSettings var3, CpvpCombatState var4, long var5) {
      if (this.i(var1, var2, var4, var5)) {
         return null;
      } else {
         return this.ep.h(var3) && this.ep.d(var4, var3, var5) && !var4.lz && this.ep.a(var1, Material.OBSIDIAN) && this.ep.a(var1, Material.END_CRYSTAL)
            ? this.ep.e(var1, var2, var3, var4, var5)
            : null;
      }
   }

   private CrystalPlanStatus bb(String var1) {
      if (var1 == null) {
         return CrystalPlanStatus.INVALID_NO_BASE;
      } else if (var1.contains("NO_RESOURCE") || var1.contains("NO_END_CRYSTAL")) {
         return CrystalPlanStatus.INVALID_NO_RESOURCES;
      } else if (var1.contains("LOS")) {
         return CrystalPlanStatus.INVALID_NO_LOS;
      } else if (var1.contains("OUT_OF_REACH")) {
         return CrystalPlanStatus.INVALID_OUT_OF_REACH;
      } else if (var1.contains("VERTICAL")) {
         return CrystalPlanStatus.INVALID_VERTICAL;
      } else if (var1.contains("SPACE") || var1.contains("COLLISION")) {
         return CrystalPlanStatus.INVALID_NO_SPACE;
      } else {
         return var1.contains("LOW_ENEMY_DAMAGE") ? CrystalPlanStatus.INVALID_LOW_DAMAGE : CrystalPlanStatus.INVALID_NO_BASE;
      }
   }

   private CrystalCandidate a(Player var1, Player var2, CpvpSettings var3, CpvpCombatState var4, long var5, Block var7) {
      if (var1 != null && var2 != null && var4 != null) {
         if (var4.mp != null && Bukkit.getEntity(var4.mp) instanceof EnderCrystal var9 && var9.isValid() && !var9.isDead()) {
            Block var10 = var9.getLocation().clone().add(0.0, -1.0, 0.0).getBlock();
            if (CrystalCombatPlan.q(var10) && (var7 == null || this.b(var10, var7)) && !this.a(var1, var2, var4, var5, var10) && this.ep.a(var1, var2, var3, var9, var4, var5)
               )
             {
               return new CrystalCandidate(var9, var10);
            }
         }

         if (var7 != null && var7.getWorld() != null) {
            Location var11 = var7.getLocation().add(0.5, 1.0, 0.5);

            for (EnderCrystal var13 : this.ep.a(var11, 1.1)) {
               if (Math.abs(var13.getLocation().getX() - var11.getX()) <= 0.7
                  && Math.abs(var13.getLocation().getZ() - var11.getZ()) <= 0.7
                  && !this.a(var1, var2, var4, var5, var7)
                  && this.ep.a(var1, var2, var3, var13, var4, var5)) {
                  return new CrystalCandidate(var13, var7);
               }
            }

            return null;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private void a(CrystalCombatPlan var1, CpvpCombatState var2, long var3) {
      if (var1 != null) {
         if (!var1.dT()) {
         }

         if (var1.dP() && !var1.dM()) {
         }
      }
   }

   boolean ab(Player var1) {
      if (var1 == null) {
         return false;
      } else {
         return isOnGround(var1) ? false : this.eo.bZ().v(var1) > 0.45;
      }
   }

   boolean l(Player var1, Player var2) {
      if (var1 == null || var2 == null) {
         return false;
      } else if (isOnGround(var2)) {
         return false;
      } else {
         double var3 = var2.getLocation().getY() - var1.getLocation().getY();
         return !(var3 <= 0.5) && !(var3 > 13.0) ? this.ep.d(var1.getLocation(), var2.getLocation()) <= 12.0 : false;
      }
   }

   long a(CpvpSettings var1, boolean var2) {
      return var1.aX();
   }

   long b(CpvpSettings var1, boolean var2) {
      return var1.bA();
   }

   long e(CpvpSettings var1, CpvpCombatState var2) {
      if (var1 == null) {
         return 0L;
      } else if (var2 == null) {
         return var1.a(this.eo.cb());
      } else {
         if (var2.kF < 1L || var2.kF < var1.aW() || var2.kF > var1.aX()) {
            var2.kF = var1.a(this.eo.cb());
         }

         return var2.kF;
      }
   }

   long f(CpvpSettings var1, CpvpCombatState var2) {
      if (var1 == null) {
         return 0L;
      } else if (var2 == null) {
         return var1.b(this.eo.cb());
      } else {
         if (var2.kG < 1L || var2.kG < var1.aZ() || var2.kG > var1.bA()) {
            var2.kG = var1.b(this.eo.cb());
         }

         return var2.kG;
      }
   }

   void c(CpvpSettings var1, CpvpCombatState var2) {
      if (var2 != null) {
         var2.kF = var1 == null ? -1L : var1.a(this.eo.cb());
         this.m(var2);
      }
   }

   void d(CpvpSettings var1, CpvpCombatState var2) {
      if (var2 != null) {
         var2.kG = var1 == null ? -1L : var1.b(this.eo.cb());
         this.m(var2);
      }
   }

   boolean a(CpvpSettings var1, CpvpCombatState var2, long var3, String var5, boolean var6) {
      if (var1 == null || var2 == null || var6 || var5 == null || var5.isBlank()) {
         return false;
      } else if (!var1.bI()) {
         this.m(var2);
         return false;
      } else if (var5.equals(var2.mb) && var3 < var2.lY) {
         return true;
      } else if (var5.equals(var2.mb) && var3 < var2.lZ) {
         return false;
      } else if (var3 < var2.ma) {
         return false;
      } else {
         var2.ma = var3 + 120L;
         int var7 = var1.bE();
         if (var7 > 0 && this.eo.cb().nextInt(100) < var7) {
            long var8 = var1.d(this.eo.cb());
            if (var8 > 0L) {
               var2.mb = var5;
               var2.lY = var3 + var8;
               var2.lZ = var2.lY + 220L;
               return true;
            }
         }

         int var11 = var1.bB();
         if (var11 > 0 && this.eo.cb().nextInt(100) < var11) {
            long var9 = var1.c(this.eo.cb());
            if (var9 > 0L) {
               var2.mb = var5;
               var2.lY = var3 + var9;
               var2.lZ = var2.lY + 220L;
               return true;
            }
         }

         return false;
      }
   }

   void m(CpvpCombatState var1) {
      if (var1 != null) {
         var1.lY = 0L;
         var1.lZ = 0L;
         var1.ma = 0L;
         var1.mb = "";
      }
   }

   boolean e(Player var1, Block var2) {
      if (var1 != null && var2 != null && var2.getWorld() != null) {
         if (!var1.getWorld().equals(var2.getWorld())) {
            return false;
         } else {
            Block var3 = var2.getRelative(BlockFace.DOWN);
            if (var3.getType() == Material.OBSIDIAN) {
               return false;
            } else {
               if (!var3.getType().isSolid() || this.i(var3)) {
                  boolean var4 = false;

                  for (BlockFace var8 : new BlockFace[]{BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST, BlockFace.DOWN}) {
                     Block var9 = var2.getRelative(var8);
                     if (var9.getType().isSolid() && !this.i(var9) && var9.getType() != Material.OBSIDIAN) {
                        var4 = true;
                        break;
                     }
                  }

                  if (!var4) {
                     return false;
                  }
               }

               if (this.i(var2)) {
                  return false;
               } else {
                  Location var10 = var2.getLocation().add(0.5, 0.5, 0.5);
                  double var11 = var1.getEyeLocation().distance(var10);
                  if (var11 > 4.5) {
                     return false;
                  } else {
                     return !this.ep.b(var1, var2, 4.5) ? false : this.ep.h(var2);
                  }
               }
            }
         }
      } else {
         return false;
      }
   }

   boolean i(Block var1) {
      return this.eo.bZ().i(var1);
   }

   boolean j(Block var1) {
      return this.eo.bZ().j(var1);
   }

   CrystalSafetyEvaluation h(Player var1, Player var2, Block var3) {
      Location var4 = var3 == null ? null : var3.getLocation().add(0.5, 1.0, 0.5);
      return this.a(var1, var2, var4, var3);
   }

   CrystalSafetyEvaluation a(Player var1, Player var2, Location var3, Block var4) {
      double var5 = 0.0;
      double var7 = 0.0;
      double var9 = var1 == null ? 0.0 : var1.getHealth();
      double var11 = var1 == null ? 0.0 : Math.max(0.0, var1.getAbsorptionAmount());
      if (var2 != null && var3 != null) {
         var5 = this.ep.b(var3, var2);
      }

      if (var1 != null && var3 != null) {
         var7 = this.a(var1, var3, this.ep.b(var3, var1));
      }

      ExplosionTradeEvaluation var13 = this.ep.a(var1, var7, var5);
      CrystalSafetyCheck var14 = this.a(var1, var2, var3, var4, var7, var5);
      boolean var15 = var14.cS();
      String var16 = var14.B();
      if (this.b(var1, var2, var3, var4)) {
         var15 = false;
         var16 = "UNCOVERED_EXPOSED_LOWER_BASE";
      }

      return new CrystalSafetyEvaluation(var4, var3, var5, var7, var9, var11, var13.df(), var13.cT(), var14.cT(), var13.cS(), var13.B(), var15, var16);
   }

   private boolean b(Player var1, Player var2, Location var3, Block var4) {
      if (var1 != null && var2 != null && var3 != null && var4 != null && var4.getWorld() != null) {
         if (!var1.getWorld().equals(var4.getWorld()) || !var2.getWorld().equals(var4.getWorld())) {
            return true;
         } else if (Math.floor(var2.getLocation().getY()) <= var4.getY()) {
            return false;
         } else {
            double var5 = var3.getY() - var1.getLocation().getY();
            return !(var5 <= -0.75) && !(var5 >= 1.75) ? !this.c(var1, var3) : false;
         }
      } else {
         return true;
      }
   }

   private boolean c(Player var1, Location var2) {
      Location var3 = var1.getLocation();
      double[][] var4 = new double[][]{
         {0.0, 0.2, 0.0}, {0.0, 0.9, 0.0}, {0.0, 1.6, 0.0}, {0.32, 0.9, 0.0}, {-0.32, 0.9, 0.0}, {0.0, 0.9, 0.32}, {0.0, 0.9, -0.32}
      };
      int var5 = 0;

      for (double[] var9 : var4) {
         Location var10 = var3.clone().add(var9[0], var9[1], var9[2]);
         if (!this.ep.h(var2, var10)) {
            var5++;
         }
      }

      return var5 >= 2;
   }

   boolean a(Player var1, Location var2, Block var3) {
      double var4 = var1 != null && var2 != null ? this.a(var1, var2, this.eo.bZ().b(var2, var1)) : 0.0;
      return this.a(var1, null, var2, var3, var4, 0.0).cS();
   }

   private double a(Player var1, Location var2, double var3) {
      if (var1 != null && var2 != null && var2.getWorld() != null) {
         Location var5 = var1.getLocation();
         if (var5.getWorld() != null && var5.getWorld().equals(var2.getWorld())) {
            double var6 = Math.max(0.0, var3) * 1.35 + (var3 > 0.0 ? 0.5 : 0.0);
            double var8 = this.ep.d(var5, var2);
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
         } else {
            return Double.MAX_VALUE;
         }
      } else {
         return Math.max(0.0, var3);
      }
   }

   boolean d(Player var1, Block var2) {
      Location var3 = var1.getLocation();
      double var4 = Math.abs(var3.getX() - (var2.getX() + 0.5));
      double var6 = Math.abs(var3.getZ() - (var2.getZ() + 0.5));
      double var8 = var3.getY() - var2.getY();
      return var4 < 0.8 && var6 < 0.8 && var8 > 0.8 && var8 < 1.6;
   }

   boolean b(Player var1, Location var2) {
      Location var3 = var1.getLocation().add(0.0, 1.0, 0.0);
      Vector var4 = var3.toVector().subtract(var2.toVector());
      double var5 = var4.length();
      if (var5 < 0.5) {
         return false;
      } else {
         var4.normalize();

         for (double var7 = 0.3; var7 < var5; var7 += 0.3) {
            Location var9 = var2.clone().add(var4.clone().multiply(var7));
            Block var10 = var9.getBlock();
            Material var11 = var10.getType();
            if (var11 == Material.OBSIDIAN
               || var11 == Material.BEDROCK
               || var11 == Material.CRYING_OBSIDIAN
               || var11 == Material.ANCIENT_DEBRIS
               || var11 == Material.RESPAWN_ANCHOR) {
               return true;
            }
         }

         return false;
      }
   }

   void f(Player var1, Player var2, CpvpSettings var3, CpvpCombatState var4, long var5) {
      if (var4 != null) {
         if (this.ep.j(var4, var5) && !this.x(var1, var2, var3, var4, var5)) {
            long var7 = this.n(var4);
            long var9 = this.a(var3, var2);
            if (var7 <= 0L || var5 - var7 > var9) {
               this.ep.h(var4);
            }
         }

         if (var4.mp != null && !this.z(var1, var2, var3, var4, var5)) {
            this.ep.i(var4);
         }

         boolean var11 = var4.lt || var4.mq || var5 < var4.oE;
         if (var11) {
            long var8 = Math.max(Math.max(var4.kD, var4.kE), var4.oj);
            if (var8 <= 0L || var5 - var8 >= 180L) {
               if (!this.g(var1, var2, var3, var4, var5)) {
                  var4.lt = false;
                  var4.mq = false;
                  var4.oE = 0L;
                  this.ep.i(var4);
                  this.ep.h(var4);
               }
            }
         }
      }
   }

   boolean x(Player var1, Player var2, CpvpSettings var3, CpvpCombatState var4, long var5) {
      if (!this.ep.j(var4, var5)) {
         return false;
      } else {
         Block var7 = var4.oi.getBlock();
         if (this.ep.cs().d(var4, var7, var5)) {
            if (this.ep.cs().c(var2, var4, var7, var5)) {
               return true;
            } else {
               Location var8 = var7.getLocation().add(0.5, 1.0, 0.5);
               CrystalSafetyEvaluation var9 = this.a(var1, var2, var8, var7);
               return this.ep.a(var7, var2, var4, var5) && this.ep.a(var1, var2, var7, var4, var5) && var9.cS();
            }
         } else {
            return this.b(var1, var2, var3, var4, var5, var7);
         }
      }
   }

   boolean g(Player var1, Player var2, CpvpSettings var3, CpvpCombatState var4, long var5) {
      if (this.ep.cI() && var4 != null && !this.m(var4, var5) && !this.ep.b(var1, "safe-crystal-action", false)) {
         CrystalCombatPlan var7 = this.g(var1, var2, var4, var5);
         return var7 != null && var7.dM();
      } else {
         return this.h(var1, var2, var3, var4, var5) ? true : this.i(var1, var2, var3, var4, var5);
      }
   }

   boolean y(Player var1, Player var2, CpvpSettings var3, CpvpCombatState var4, long var5) {
      return var4 != null && var4.mp != null ? Bukkit.getEntity(var4.mp) instanceof EnderCrystal var8 && this.a(var1, var2, var3, var8, var4, var5) : false;
   }

   boolean z(Player var1, Player var2, CpvpSettings var3, CpvpCombatState var4, long var5) {
      if (var4 != null && var4.mp != null) {
         if (!(Bukkit.getEntity(var4.mp) instanceof EnderCrystal var8 && var8.isValid() && !var8.isDead())) {
            this.ep.i(var4);
            return false;
         } else if (var1 != null && var2 != null && var1.getWorld().equals(var8.getWorld()) && var1.getWorld().equals(var2.getWorld())) {
            Location var9 = var8.getLocation();
            if (var1.getEyeLocation().distance(var9) > 4.5) {
               return false;
            } else if (!this.ep.g(var1.getEyeLocation(), var9)) {
               return false;
            } else {
               Block var10 = var9.clone().add(0.0, -1.0, 0.0).getBlock();
               return !this.ep.a(var1, var2, var10, var4, var5) ? false : this.a(var1, var2, var3, var8, var4, var5);
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   boolean h(Player var1, Player var2, CpvpSettings var3, CpvpCombatState var4, long var5) {
      if (this.y(var1, var2, var3, var4, var5)) {
         return true;
      } else if (var1 != null && var2 != null && var1.getWorld().equals(var2.getWorld())) {
         for (EnderCrystal var8 : this.ep.a(var1, 6.0)) {
            if (this.a(var1, var2, var3, var8, var4, var5)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private boolean p(Player var1, Player var2) {
      if (var1 != null && var2 != null && isOnGround(var2)) {
         int var3 = (int)Math.floor(var1.getLocation().getY());
         int var4 = (int)Math.floor(var2.getLocation().getY());
         return Math.abs(var3 - var4) <= 1;
      } else {
         return false;
      }
   }

   private boolean h(Player var1, Player var2, CpvpCombatState var3, long var4) {
      if (!this.p(var1, var2)) {
         return false;
      } else {
         return this.ep.cs().g(var2, var3, var4) ? false : var3 == null || var4 - var3.mK > 980L;
      }
   }

   private boolean i(Player var1, Player var2, CpvpCombatState var3, long var4) {
      return this.p(var1, var2) && !this.ep.cs().g(var2, var3, var4);
   }

   private boolean a(Player var1, Player var2, CpvpCombatState var3, long var4, Block var6) {
      return this.p(var1, var2) && !this.ep.b(var2, var3, var6, var4);
   }

   private boolean b(Player var1, Player var2, CpvpCombatState var3, long var4, Block var6) {
      return var1 != null && var2 != null && var3 != null && var6 != null && this.ep.l(var1, var2) && this.ep.a(var1, var2, var6, var3, var4);
   }

   boolean a(Player var1, Player var2, CpvpSettings var3, EnderCrystal var4, CpvpCombatState var5, long var6) {
      if (var1 == null || var2 == null || var3 == null || var4 == null) {
         return false;
      } else if (var4.isValid() && !var4.isDead()) {
         if (var1.getWorld().equals(var4.getWorld()) && var1.getWorld().equals(var2.getWorld())) {
            Location var8 = var4.getLocation();
            if (var1.getEyeLocation().distance(var8) > 4.5) {
               return false;
            } else if (!this.ep.a(var1, var8, var3) && !this.ep.l(var1, var2)) {
               return false;
            } else if (!this.ep.g(var1.getEyeLocation(), var8)) {
               return false;
            } else {
               Block var9 = var8.clone().add(0.0, -1.0, 0.0).getBlock();
               if (!this.ep.a(var1, var2, var9, var5, var6)) {
                  return false;
               } else {
                  CrystalSafetyEvaluation var10 = this.a(var1, var2, var8, var9);
                  boolean var11 = this.b(var1, var2, var5, var6, var9);
                  double var12 = var2.getLocation().getY() - (var9.getY() + 1.0);
                  double var14 = var11 ? Math.min(0.05, this.ep.g(var12)) : 0.75;
                  if (var10.dc() < var14) {
                     return false;
                  } else {
                     return this.a(var1, var2, var9, var10) ? false : var10.cS();
                  }
               }
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   boolean i(Player var1, Player var2, CpvpSettings var3, CpvpCombatState var4, long var5) {
      CrystalCombatPlan var7 = this.k(var1, var2, var3, var4, var5);
      if (var7.dU() == CrystalPlanStatus.PLACE_SAFE || var7.dU() == CrystalPlanStatus.CREATE_OBSIDIAN_PRESSURE) {
         return true;
      } else if (this.a(var7)) {
         return false;
      } else if (var7.dP()) {
         return false;
      } else if (this.eo.a(var1, var2, var4, var3, var5)) {
         return false;
      } else if (this.ep.cy().am(var1, var2, var3, var4, var5) != null) {
         return true;
      } else {
         double var8 = this.ep.b(var1, var2, var3);
         if (this.ep.j(var4, var5)) {
            Block var10 = var4.oi.getBlock();
            if (this.b(var1, var2, var3, var4, var5, var10)) {
               return true;
            }

            this.ep.h(var4);
         }

         if (var5 < var4.mv && var4.mw != null) {
            var8 = Math.max(var8, this.ep.a(var1, var2, var3, var4.mw.getBlock()));
         }

         if (this.ep.h(var3) && this.ep.d(var4, var3, var5) && this.ep.a(var1, Material.OBSIDIAN) && !var4.lz && !this.eo.a(var1, var2, var4, var3, var5)) {
            var8 = Math.max(var8, this.ep.a(var1, var2, var3));
         }

         return var8 > 1.5;
      }
   }

   boolean j(double var1) {
      return Double.isFinite(var1) && var1 > -900.0;
   }

   boolean aa(Player var1, Player var2, CpvpSettings var3, CpvpCombatState var4, long var5) {
      CrystalCombatPlan var7 = this.k(var1, var2, var3, var4, var5);
      if (var7.dU() == CrystalPlanStatus.PLACE_SAFE || var7.dU() == CrystalPlanStatus.BREAK_SAFE || var7.dU() == CrystalPlanStatus.CREATE_OBSIDIAN_PRESSURE) {
         return true;
      } else if (this.a(var7)) {
         return false;
      } else if (var7.dP()) {
         return false;
      } else if (var1 == null || var2 == null || var3 == null || var4 == null) {
         return false;
      } else if (!var1.isValid() || var1.isDead() || !var2.isValid() || var2.isDead()) {
         return false;
      } else if (!var1.getWorld().equals(var2.getWorld()) || !this.ep.a(var1, Material.END_CRYSTAL)) {
         return false;
      } else if (this.eo.a(var1, var2, var4, var3, var5)) {
         return false;
      } else {
         if (this.ep.j(var4, var5)) {
            Block var8 = var4.oi.getBlock();
            if (this.b(var1, var2, var3, var4, var5, var8)) {
               return true;
            }

            this.ep.h(var4);
         }

         if (var5 < var4.mv && var4.mw != null) {
            Block var9 = var4.mw.getBlock();
            if (this.j(this.ep.a(var1, var2, var3, var9))) {
               return true;
            }
         }

         if (this.ae(var1, var2, var3, var4, var5)) {
            return true;
         } else {
            return this.ep.cy().am(var1, var2, var3, var4, var5) != null ? true : this.j(this.ep.b(var1, var2, var3));
         }
      }
   }

   boolean ab(Player var1, Player var2, CpvpSettings var3, CpvpCombatState var4, long var5) {
      if (var1 == null || var2 == null || var3 == null || var4 == null) {
         return false;
      } else if (var1.isValid() && !var1.isDead() && var2.isValid() && !var2.isDead()) {
         if (!var1.getWorld().equals(var2.getWorld())) {
            return false;
         } else if (this.x(var1, var2, var3, var4, var5)) {
            return true;
         } else if (var5 < var4.mv && var4.mw != null) {
            return true;
         } else {
            if (var4.mp != null) {
               if (Bukkit.getEntity(var4.mp) instanceof EnderCrystal var8 && var8.isValid() && !var8.isDead()) {
                  return true;
               }

               this.ep.i(var4);
            }

            if (this.ae(var1, var2, var3, var4, var5)) {
               return true;
            } else if (this.ep.cI() && !this.m(var4, var5) && !this.ep.b(var1, "crystal-spam-continuation", false)) {
               CrystalCombatPlan var9 = this.g(var1, var2, var4, var5);
               return var9 != null && var9.dM();
            } else {
               return this.h(var1, var2, var3, var4, var5) ? true : this.aa(var1, var2, var3, var4, var5);
            }
         }
      } else {
         return false;
      }
   }

   boolean ac(Player var1, Player var2, CpvpSettings var3, CpvpCombatState var4, long var5) {
      if (var1 != null && var2 != null && var3 != null && var4 != null) {
         if (this.eo.bZ().k(var4, var5)) {
            return false;
         } else if (var4.lt || var4.lB || var4.mq || var5 < var4.oE) {
            return true;
         } else if (var5 < var4.lA) {
            return true;
         } else if (this.z(var1, var2, var3, var4, var5)) {
            return true;
         } else if (this.ep.cI() && !this.m(var4, var5) && !this.ep.b(var1, "live-crystal-spam", false)) {
            CrystalCombatPlan var7 = this.g(var1, var2, var4, var5);
            return var7 != null && var7.dM();
         } else {
            return this.h(var1, var2, var3, var4, var5) ? true : this.aa(var1, var2, var3, var4, var5);
         }
      } else {
         return false;
      }
   }

   boolean m(Player var1, Player var2, CpvpSettings var3, CpvpCombatState var4, long var5) {
      if (var1 == null || var2 == null || var3 == null || var4 == null) {
         return false;
      } else if (var1.isValid() && !var1.isDead() && var2.isValid() && !var2.isDead()) {
         if (!var1.getWorld().equals(var2.getWorld())) {
            return false;
         } else {
            this.f(var1, var2, var3, var4, var5);
            CrystalCombatPlan var7 = this.k(var1, var2, var3, var4, var5);
            if (this.a(var7)) {
               boolean var12 = var4.lt || var4.lB || var4.mq || var4.mp != null || this.ep.j(var4, var5) || var5 < var4.mv && var4.mw != null;
               if (var12) {
                  this.ep.a(var4, var2, var5 + this.a(var3, var2));
                  return true;
               } else {
                  this.ep.l(var4);
                  return false;
               }
            } else if (var7.dM()) {
               this.ep.a(var4, var2, var5 + this.a(var3, var2));
               return true;
            } else if (var7.dP()) {
               this.ep.a(var4, var2, var5 + this.a(var3, var2));
               return true;
            } else if (var4.mG || var4.mH) {
               return true;
            } else if (this.p(var1, var2, var3, var4, var5)) {
               return true;
            } else {
               long var8 = this.a(var3, var2);
               boolean var10 = this.x(var1, var2, var3, var4, var5);
               boolean var11 = var4.lt || var4.lB || var4.mq || var10 || var5 < var4.mv && var4.mw != null || this.z(var1, var2, var3, var4, var5);
               if (var11) {
                  this.ep.a(var4, var2, var5 + var8);
                  return true;
               } else {
                  this.ep.l(var4);
                  return false;
               }
            }
         }
      } else {
         return false;
      }
   }

   boolean n(Player var1, Player var2, CpvpSettings var3, CpvpCombatState var4, long var5) {
      return this.m(var1, var2, var3, var4, var5);
   }

   boolean o(Player var1, Player var2, CpvpSettings var3, CpvpCombatState var4, long var5) {
      if (var1 == null || var2 == null || var3 == null || var4 == null) {
         return false;
      } else if (var1.isValid() && !var1.isDead() && var2.isValid() && !var2.isDead()) {
         if (!var1.getWorld().equals(var2.getWorld())) {
            return false;
         } else {
            this.f(var1, var2, var3, var4, var5);
            CrystalCombatPlan var7 = this.k(var1, var2, var3, var4, var5);
            if (var7.dN()) {
               return true;
            } else if (this.a(var7)) {
               return false;
            } else {
               Block var8 = this.ag(var1, var2, var3, var4, var5);
               if (this.b(var1, var2, var3, var4, var5, var8)) {
                  return true;
               } else if (this.c(var1, var2, var3, var4, var5, var8).cW()) {
                  return true;
               } else {
                  Block var9 = this.ep.x(var2);
                  return !this.b(var8, var9) && (this.b(var1, var2, var3, var4, var5, var9) || this.c(var1, var2, var3, var4, var5, var9).cW());
               }
            }
         }
      } else {
         return false;
      }
   }

   boolean q(Player var1, Player var2, CpvpSettings var3, CpvpCombatState var4, long var5) {
      if (this.j(var1, var2, var4, var5)) {
         return var4.nO;
      } else {
         CrystalCombatPlan var8 = this.k(var1, var2, var3, var4, var5);
         boolean var7;
         if (var8.dN() || var8.dP()) {
            var7 = true;
         } else if (this.a(var8)) {
            var7 = false;
         } else {
            var7 = this.o(var1, var2, var3, var4, var5) || this.m(var1, var2, var3, var4, var5);
         }

         this.b(var1, var2, var4, var5, var7);
         return var7;
      }
   }

   boolean r(Player var1, Player var2, CpvpSettings var3, CpvpCombatState var4, long var5) {
      if (var1 == null || var2 == null || var3 == null || var4 == null) {
         return false;
      } else if (var1.isValid() && !var1.isDead() && var2.isValid() && !var2.isDead()) {
         return !var1.getWorld().equals(var2.getWorld()) ? false : this.k(var1, var2, var3, var4, var5).dP();
      } else {
         return false;
      }
   }

   boolean p(Player var1, Player var2, CpvpSettings var3, CpvpCombatState var4, long var5) {
      if (this.k(var1, var2, var4, var5)) {
         return var4.nX;
      } else if (this.ep.cI() && var4 != null && !var4.mG && !var4.mH && var5 > var4.mW && var4.mT < 3 && !this.ep.b(var1, "anchor-melee-suppression", false)) {
         this.c(var1, var2, var4, var5, false);
         return false;
      } else {
         boolean var7 = this.ad(var1, var2, var3, var4, var5);
         this.c(var1, var2, var4, var5, var7);
         return var7;
      }
   }

   private boolean ad(Player var1, Player var2, CpvpSettings var3, CpvpCombatState var4, long var5) {
      if (!this.ep.j(var3)) {
         return false;
      } else if (var1 != null && var2 != null && var4 != null) {
         if (!this.ep.e(var1.getWorld())) {
            return false;
         } else {
            boolean var7 = var4.mT >= 3 || var5 <= var4.mW;
            boolean var8 = !this.ep.h(var3);
            boolean var9 = var7
               || var8
               || var4.mG
               || var4.mH
               || var5 < var4.mA
               || var2.getHealth() <= 10.0
               || this.ep.d(var1.getLocation(), var2.getLocation()) <= 4.6;
            if (!var9) {
               return false;
            } else if (this.ep.b(var1, Material.RESPAWN_ANCHOR) > 0 && this.ep.b(var1, Material.GLOWSTONE) > 0) {
               boolean var10 = var7 || var8 || this.ep.e(var1, var2, var4, var5);
               if (!var10) {
                  return false;
               } else {
                  boolean var11 = this.ep.a(var3, var4, var2, var5, 180L);
                  if (!var11 && !var7 && !var8) {
                     return false;
                  } else {
                     AnchorCombatPlan var12 = this.ep.b(var1, var2, var3, var4, var5);
                     if (var12 == null) {
                        return false;
                     } else {
                        double var13 = this.ep.a(var1, var2, var3, var4, var5);
                        boolean var15 = var7 || this.ep.a(var12, var13, var1, var2, var3, var4, var5);
                        if (var11 && var15) {
                           return true;
                        } else if (var8) {
                           return !this.d(var1, var2, var4, var5, var11);
                        } else {
                           return var15 && var13 <= 1.5 ? !this.d(var1, var2, var4, var5, var11) : false;
                        }
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

   private boolean j(Player var1, Player var2, CpvpCombatState var3, long var4) {
      return var1 != null
         && var2 != null
         && var3 != null
         && var3.nP == var4
         && var2.getUniqueId().equals(var3.nQ)
         && var1.getLocation().getBlockX() == var3.nR
         && var1.getLocation().getBlockY() == var3.nS
         && var1.getLocation().getBlockZ() == var3.nT
         && var2.getLocation().getBlockX() == var3.nU
         && var2.getLocation().getBlockY() == var3.nV
         && var2.getLocation().getBlockZ() == var3.nW;
   }

   private void b(Player var1, Player var2, CpvpCombatState var3, long var4, boolean var6) {
      if (var1 != null && var2 != null && var3 != null) {
         var3.nO = var6;
         var3.nP = var4;
         var3.nQ = var2.getUniqueId();
         var3.nR = var1.getLocation().getBlockX();
         var3.nS = var1.getLocation().getBlockY();
         var3.nT = var1.getLocation().getBlockZ();
         var3.nU = var2.getLocation().getBlockX();
         var3.nV = var2.getLocation().getBlockY();
         var3.nW = var2.getLocation().getBlockZ();
      }
   }

   private boolean k(Player var1, Player var2, CpvpCombatState var3, long var4) {
      return var1 != null
         && var2 != null
         && var3 != null
         && var3.nY == var4
         && var2.getUniqueId().equals(var3.nZ)
         && var1.getLocation().getBlockX() == var3.oa
         && var1.getLocation().getBlockY() == var3.ob
         && var1.getLocation().getBlockZ() == var3.oc
         && var2.getLocation().getBlockX() == var3.od
         && var2.getLocation().getBlockY() == var3.oe
         && var2.getLocation().getBlockZ() == var3.of;
   }

   private void c(Player var1, Player var2, CpvpCombatState var3, long var4, boolean var6) {
      if (var1 != null && var2 != null && var3 != null) {
         var3.nX = var6;
         var3.nY = var4;
         var3.nZ = var2.getUniqueId();
         var3.oa = var1.getLocation().getBlockX();
         var3.ob = var1.getLocation().getBlockY();
         var3.oc = var1.getLocation().getBlockZ();
         var3.od = var2.getLocation().getBlockX();
         var3.oe = var2.getLocation().getBlockY();
         var3.of = var2.getLocation().getBlockZ();
      }
   }

   boolean d(Player var1, Player var2, CpvpCombatState var3, long var4, boolean var6) {
      if (var6) {
         return false;
      } else if (var1 != null && var2 != null && var3 != null) {
         if (var1.getLocation().distance(var2.getLocation()) > 3.25) {
            return false;
         } else if (!this.ep.h(var1.getEyeLocation(), var2.getEyeLocation())) {
            return false;
         } else if (var4 - var3.kK < 500L) {
            return false;
         } else if (var4 < var3.mS) {
            return true;
         } else if (var4 - var3.mR < 350L) {
            return false;
         } else {
            var3.mR = var4;
            if (this.eo.cb().nextInt(100) < 45) {
               var3.mS = var4 + 240L;
               return true;
            } else {
               return false;
            }
         }
      } else {
         return false;
      }
   }

   private long n(CpvpCombatState var1) {
      return Math.max(Math.max(Math.max(var1.kD, var1.kE), Math.max(var1.kH, var1.oj)), Math.max(var1.my, var1.mK));
   }

   private boolean ae(Player var1, Player var2, CpvpSettings var3, CpvpCombatState var4, long var5) {
      if (var1 == null || var2 == null || var3 == null || var4 == null) {
         return false;
      } else if (this.h(var1, var2, var4, var5)) {
         return false;
      } else {
         Block var7 = this.ep.x(var2);
         if (var7 == null || !this.h(var2, var7)) {
            return false;
         } else {
            return this.ep.a(var1, Material.END_CRYSTAL) && this.ep.k(var1, var2) != null ? true : this.af(var1, var2, var3, var4, var5);
         }
      }
   }

   private boolean af(Player var1, Player var2, CpvpSettings var3, CpvpCombatState var4, long var5) {
      if (var1 == null || var2 == null || var3 == null || var4 == null) {
         return false;
      } else if (this.i(var1, var2, var4, var5)) {
         return false;
      } else {
         return this.ep.h(var3) && this.ep.d(var4, var3, var5) && !var4.lz && this.ep.a(var1, Material.END_CRYSTAL) && this.ep.a(var1, Material.OBSIDIAN)
            ? this.ep.e(var1, var2, var3, var4, var5) != null
            : false;
      }
   }

   private Block ag(Player var1, Player var2, CpvpSettings var3, CpvpCombatState var4, long var5) {
      CrystalCombatPlan var7 = this.k(var1, var2, var3, var4, var5);
      if (CrystalCombatPlan.q(var7.dX())) {
         return var7.dX();
      } else {
         if (this.ep.j(var4, var5) && var4.oi != null) {
            Block var8 = var4.oi.getBlock();
            if (this.m(var8) && this.b(var1, var2, var3, var4, var5, var8)) {
               return var8;
            }

            this.ep.h(var4);
         }

         if (var4.mp != null) {
            if (Bukkit.getEntity(var4.mp) instanceof EnderCrystal var9 && var9.isValid() && !var9.isDead()) {
               return var9.getLocation().clone().add(0.0, -1.0, 0.0).getBlock();
            }

            this.ep.i(var4);
         }

         Block var13 = this.ep.x(var2);
         if (CrystalCombatPlan.q(var13)) {
            return var13;
         } else {
            Block var14 = this.ep.cy().ao(var1, var2, var3, var4, var5);
            if (var14 != null) {
               return var14;
            } else {
               Block var10 = this.ep.cy().am(var1, var2, var3, var4, var5);
               if (var10 != null) {
                  return var10;
               } else {
                  if (var5 < var4.mv && var4.mw != null) {
                     Block var11 = var4.mw.getBlock();
                     if (this.b(var1, var2, var3, var4, var5, var11)) {
                        return var11;
                     }
                  }

                  return null;
               }
            }
         }
      }
   }

   private boolean b(Player var1, Player var2, CpvpSettings var3, CpvpCombatState var4, long var5, Block var7) {
      if (var7 == null || !this.h(var2, var7)) {
         return false;
      } else if (this.a(var1, var2, var4, var5, var7)) {
         return false;
      } else if (this.e(var1, var2, var3, var4, var5, var7)) {
         return true;
      } else if (this.d(var1, var2, var3, var4, var5, var7)) {
         return true;
      } else if (this.c(var1, var2, var3, var4, var5, var7).cW()) {
         return true;
      } else {
         Block var8 = this.ep.x(var2);
         return this.b(var7, var8) && this.ae(var1, var2, var3, var4, var5);
      }
   }

   private CrystalRecoveryCheck c(Player var1, Player var2, CpvpSettings var3, CpvpCombatState var4, long var5, Block var7) {
      if (var1 == null || var2 == null || var3 == null || var4 == null) {
         return CrystalRecoveryCheck.be("MISSING_CONTEXT");
      } else if (var7 == null) {
         return CrystalRecoveryCheck.be("NO_BASE");
      } else if (!this.h(var2, var7)) {
         return CrystalRecoveryCheck.be("TARGET_NOT_ABOVE_BASE_PLANE");
      } else if (!this.ep.a(var1, Material.END_CRYSTAL)) {
         return CrystalRecoveryCheck.be("NO_END_CRYSTAL");
      } else if (!this.ep.a(var7, var2, var4, var5)) {
         return CrystalRecoveryCheck.be("NO_PLACEMENT_SPACE");
      } else {
         Location var8 = var7.getLocation().add(0.5, 1.0, 0.5);
         double var9 = var1.getEyeLocation().distance(var8);
         if (!(var9 > 4.5) && !(var9 < 0.8)) {
            if (!this.ep.g(var1.getEyeLocation(), var8)) {
               return CrystalRecoveryCheck.be("LOS_FAIL");
            } else if (!this.ep.a(var1, var8, var3)) {
               return CrystalRecoveryCheck.be("FOV_FAIL");
            } else if (!this.ep.a(var1, var2, var7, var4, var5)) {
               return CrystalRecoveryCheck.be("VERTICAL_INVALID");
            } else {
               CrystalSafetyEvaluation var11 = this.a(var1, var2, var8, var7);
               if (var11.cS()) {
                  return CrystalRecoveryCheck.be("PLACEMENT_SAFE");
               } else if (!this.a(var2, var7, var11)) {
                  return CrystalRecoveryCheck.be("LOW_VALUE_UNSAFE_CANDIDATE");
               } else {
                  return !this.c(var11)
                     ? CrystalRecoveryCheck.be("NON_SELF_DAMAGE_REJECTION")
                     : CrystalRecoveryCheck.a(
                        "TARGET_BASE_PRESSURE_UNSAFE_SELF_RECOVERY:"
                           + var11.cX()
                           + ":"
                           + var11.cY()
                           + " enemy="
                           + this.k(var11.dc())
                           + " self="
                           + this.k(var11.dd()),
                        var7
                     );
               }
            }
         } else {
            return CrystalRecoveryCheck.be("OUT_OF_REACH");
         }
      }
   }

   private boolean a(CpvpSettings var1, Player var2, Block var3, CrystalSafetyEvaluation var4) {
      if (var1 == null || var2 == null || var4 == null || !CrystalCombatPlan.q(var3)) {
         return false;
      } else if (!this.h(var2, var3)) {
         return false;
      } else {
         return !this.a(var2, var3, var4) ? false : this.a(var4);
      }
   }

   private boolean a(Player var1, Player var2, Block var3, CrystalSafetyEvaluation var4) {
      if (var1 != null && var2 != null && var3 != null && var4 != null) {
         Location var5 = var3.getLocation().add(0.5, 1.0, 0.5);
         boolean var6 = this.b(var1, var2, var5, var3, var4.dd(), var4.dc());
         if (this.b(var1, var5, var3) && !var6) {
            return true;
         } else {
            double var7 = this.ep.d(var1.getLocation(), var5);
            double var9 = Math.abs(var1.getLocation().getY() - var5.getY());
            boolean var11 = var7 < 1.15 && var9 < 2.1;
            if (this.d(var1, var3) && var4.dd() > 0.35) {
               return true;
            } else if (var6 && var4.di()) {
               return false;
            } else if (var11 && var4.dd() > 0.65) {
               return true;
            } else {
               boolean var12 = this.ep.l(var1, var2) && this.ep.f(var1, var2, var3);
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

   private boolean a(CrystalSafetyEvaluation var1) {
      if (var1 == null || var1.cS() || var1.di()) {
         return false;
      } else {
         return !this.bc(var1.dj()) ? false : var1.dk() || "LETHAL_BUFFER".equals(var1.dl());
      }
   }

   private boolean bc(String var1) {
      return "EFFECTIVE_HEALTH_BUFFER".equals(var1) || "HIGH_SELF_DAMAGE_RATIO".equals(var1);
   }

   private double b(CrystalSafetyEvaluation var1) {
      return var1.i() + var1.de() - var1.dd();
   }

   private boolean a(Player var1, Block var2, CrystalSafetyEvaluation var3) {
      if (var1 != null && var2 != null && var3 != null) {
         double var4 = var1.getLocation().getY() - (var2.getY() + 1.0);
         double var6 = Math.max(4.0, this.eo.bZ().g(var4));
         return var3.dc() >= var6 || var3.dc() >= var1.getHealth();
      } else {
         return false;
      }
   }

   private boolean c(CrystalSafetyEvaluation var1) {
      return var1 != null && !var1.cS() ? !var1.di() || !var1.dk() && this.bd(var1.dl()) : false;
   }

   private boolean bd(String var1) {
      return var1 == null
         ? false
         : var1.equals("EFFECTIVE_HEALTH_BUFFER")
            || var1.equals("LETHAL_BUFFER")
            || var1.equals("LOWER_LEVEL_CLOSE_RANGE")
            || var1.equals("HIGH_SELF_DAMAGE_RATIO");
   }

   private boolean d(Player var1, Player var2, CpvpSettings var3, CpvpCombatState var4, long var5, Block var7) {
      if (var7 == null || !this.ep.a(var1, Material.END_CRYSTAL)) {
         return false;
      } else if (this.a(var1, var2, var4, var5, var7)) {
         return false;
      } else if (!this.ep.a(var7, var2, var4, var5)) {
         return false;
      } else {
         Location var8 = var7.getLocation().add(0.5, 1.0, 0.5);
         if (var1.getEyeLocation().distance(var8) > 4.5) {
            return false;
         } else if (!this.ep.g(var1.getEyeLocation(), var8)) {
            return false;
         } else if (!this.ep.a(var1, var2, var7, var4, var5)) {
            return false;
         } else {
            CrystalSafetyEvaluation var9 = this.a(var1, var2, var8, var7);
            double var10 = this.b(var1, var2, var4, var5, var7) ? 0.05 : 0.5;
            return var9.dc() < var10 ? false : var9.cS();
         }
      }
   }

   private boolean e(Player var1, Player var2, CpvpSettings var3, CpvpCombatState var4, long var5, Block var7) {
      if (var7 == null) {
         return false;
      } else {
         if (var4.mp != null) {
            if (Bukkit.getEntity(var4.mp) instanceof EnderCrystal var9 && var9.isValid() && !var9.isDead()) {
               Block var10 = var9.getLocation().clone().add(0.0, -1.0, 0.0).getBlock();
               if (this.b(var7, var10) && this.a(var1, var2, var3, var4, var9, var5)) {
                  return true;
               }
            } else {
               this.ep.i(var4);
            }
         }

         Location var11 = var7.getLocation().add(0.5, 1.0, 0.5);

         for (EnderCrystal var13 : this.ep.a(var11, 1.1)) {
            if (Math.abs(var13.getLocation().getX() - var11.getX()) <= 0.7
               && Math.abs(var13.getLocation().getZ() - var11.getZ()) <= 0.7
               && this.a(var1, var2, var3, var4, var13, var5)) {
               return true;
            }
         }

         return false;
      }
   }

   private boolean a(Player var1, Player var2, CpvpSettings var3, CpvpCombatState var4, EnderCrystal var5, long var6) {
      if (var5 != null && var5.isValid() && !var5.isDead()) {
         if (var1.getWorld().equals(var5.getWorld()) && var1.getWorld().equals(var2.getWorld())) {
            Location var8 = var5.getLocation();
            if (var1.getEyeLocation().distance(var8) > 4.5) {
               return false;
            } else if (!this.ep.g(var1.getEyeLocation(), var8)) {
               return false;
            } else {
               Block var9 = var8.clone().add(0.0, -1.0, 0.0).getBlock();
               if (!this.ep.a(var1, var2, var9, var4, var6)) {
                  return false;
               } else {
                  double var10 = this.ep.b(var8, var2);
                  double var12 = this.b(var1, var2, var4, var6, var9) ? 0.05 : 0.75;
                  if (var10 < var12) {
                     return false;
                  } else {
                     CrystalSafetyEvaluation var14 = this.a(var1, var2, var8, var9);
                     return this.a(var1, var2, var9, var14) ? false : var14.cS();
                  }
               }
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private Block ah(Player var1, Player var2, CpvpSettings var3, CpvpCombatState var4, long var5) {
      CrystalCombatPlan var7 = this.k(var1, var2, var3, var4, var5);
      if (CrystalCombatPlan.q(var7.dX())) {
         return var7.dX();
      } else if (var4 == null) {
         Block var14 = var2 == null ? null : this.eo.bZ().x(var2);
         return CrystalCombatPlan.q(var14) ? var14 : null;
      } else {
         if (this.n(var4, var5) && var4.oi != null) {
            Block var8 = var4.oi.getBlock();
            if (this.m(var8)) {
               return var8;
            }
         }

         EnderCrystal var13 = this.o(var4);
         if (var13 != null) {
            return var13.getLocation().clone().add(0.0, -1.0, 0.0).getBlock();
         } else {
            Block var9 = var2 == null ? null : this.eo.bZ().x(var2);
            if (CrystalCombatPlan.q(var9)) {
               return var9;
            } else {
               Block var10 = this.eo.bZ().cy().ao(var1, var2, var3, var4, var5);
               if (var10 != null) {
                  return var10;
               } else {
                  Block var11 = this.eo.bZ().cy().am(var1, var2, var3, var4, var5);
                  if (var11 != null) {
                     return var11;
                  } else {
                     if (var5 < var4.mv && var4.mw != null) {
                        Block var12 = var4.mw.getBlock();
                        if (this.b(var1, var2, var3, var4, var5, var12)) {
                           return var12;
                        }
                     }

                     return null;
                  }
               }
            }
         }
      }
   }

   private boolean n(CpvpCombatState var1, long var2) {
      if (var1 != null && var1.oi != null) {
         if (var2 - var1.oj > 800L) {
            return false;
         } else {
            Material var4 = var1.oi.getBlock().getType();
            return var4 == Material.OBSIDIAN || var4 == Material.BEDROCK;
         }
      } else {
         return false;
      }
   }

   private EnderCrystal o(CpvpCombatState var1) {
      if (var1 != null && var1.mp != null) {
         return Bukkit.getEntity(var1.mp) instanceof EnderCrystal var3 && var3.isValid() && !var3.isDead() ? var3 : null;
      } else {
         return null;
      }
   }

   private boolean ai(Player var1, Player var2, CpvpSettings var3, CpvpCombatState var4, long var5) {
      EnderCrystal var7 = this.o(var4);
      return var7 != null && this.a(var1, var2, var3, var7, var4, var5);
   }

   private boolean f(Player var1, Player var2, CpvpSettings var3, CpvpCombatState var4, long var5, Block var7) {
      if (var7 == null) {
         return false;
      } else {
         EnderCrystal var8 = this.o(var4);
         if (var8 != null) {
            Block var9 = var8.getLocation().clone().add(0.0, -1.0, 0.0).getBlock();
            if (this.b(var7, var9) && this.a(var1, var2, var3, var4, var8, var5)) {
               return true;
            }
         }

         Location var12 = var7.getLocation().add(0.5, 1.0, 0.5);

         for (EnderCrystal var11 : this.ep.a(var12, 1.1)) {
            if (Math.abs(var11.getLocation().getX() - var12.getX()) <= 0.7
               && Math.abs(var11.getLocation().getZ() - var12.getZ()) <= 0.7
               && this.a(var1, var2, var3, var4, var11, var5)) {
               return true;
            }
         }

         return false;
      }
   }

   private boolean aj(Player var1, Player var2, CpvpSettings var3, CpvpCombatState var4, long var5) {
      if (this.n(var4, var5) && var4.oi != null) {
         Block var7 = var4.oi.getBlock();
         if (this.ep.cs().d(var4, var7, var5)) {
            if (this.ep.cs().c(var2, var4, var7, var5)) {
               return true;
            } else {
               Location var8 = var7.getLocation().add(0.5, 1.0, 0.5);
               CrystalSafetyEvaluation var9 = this.a(var1, var2, var8, var7);
               return this.ep.a(var7, var2, var4, var5) && this.ep.a(var1, var2, var7, var4, var5) && var9.cS();
            }
         } else {
            return this.b(var1, var2, var3, var4, var5, var7);
         }
      } else {
         return false;
      }
   }

   private CrystalSafetyCheck a(Player var1, Player var2, Location var3, Block var4, double var5, double var7) {
      if (var1 != null && var3 != null && var4 != null) {
         double var9 = var1.getHealth() + Math.max(0.0, var1.getAbsorptionAmount());
         double var11 = Math.max(0.0, var9 - 0.01);
         if (var5 >= var11) {
            return new CrystalSafetyCheck(false, "LETHAL_SELF_DAMAGE", var11);
         } else {
            return this.b(var1, var3, var4) && !this.b(var1, var2, var3, var4, var5, var7)
               ? new CrystalSafetyCheck(false, "CRYSTAL_TOO_CLOSE_TO_BOT_BODY", var11)
               : new CrystalSafetyCheck(true, "SAFE_FOR_BOT", var11);
         }
      } else {
         return new CrystalSafetyCheck(false, "MISSING_CONTEXT", 0.0);
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
               double var13 = this.ep.d(var9, var3);
               if (var13 < 0.85 || var13 > 1.35) {
                  return false;
               } else if (this.ep.d(var10, var3) > 2.35) {
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
            double var5 = this.ep.d(var4, var2);
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
            double var4 = var2.getX() + 0.5;
            double var6 = var2.getZ() + 0.5;
            double var8 = var4 - var3.getX();
            double var10 = var6 - var3.getZ();
            if (var8 * var8 + var10 * var10 > 0.81) {
               return false;
            } else {
               int var12 = var2.getY();
               int var13 = (int)Math.floor(var3.getY());
               return var12 >= var13 - 1 && var12 <= var13 + 1;
            }
         }
      } else {
         return true;
      }
   }

   private boolean c(CpvpCombatState var1, Player var2, long var3) {
      return var1 != null && var2 != null && var1.lD != null && var1.lD.equals(var2.getUniqueId()) && var3 <= var1.lC;
   }

   private boolean i(Player var1, Player var2, Block var3) {
      if (var1 != null && var2 != null && var3 != null) {
         return Math.floor(var2.getLocation().getY()) <= var3.getY() ? false : Math.abs(var1.getLocation().getY() - (var3.getY() + 1.0)) <= 0.75;
      } else {
         return false;
      }
   }

   private boolean m(Block var1) {
      return var1 != null && (var1.getType() == Material.OBSIDIAN || var1.getType() == Material.BEDROCK);
   }

   private String n(Block var1) {
      return var1 == null ? "null" : var1.getX() + "," + var1.getY() + "," + var1.getZ();
   }

   private String k(double var1) {
      return String.format(Locale.ROOT, "%.2f", var1);
   }

   private boolean h(Player var1, Block var2) {
      return var1 != null && var2 != null ? Math.floor(var1.getLocation().getY()) > var2.getY() : false;
   }

   private boolean b(Block var1, Block var2) {
      return var1 != null && var2 != null
         ? var1.getWorld() != null
            && var1.getWorld().equals(var2.getWorld())
            && var1.getX() == var2.getX()
            && var1.getY() == var2.getY()
            && var1.getZ() == var2.getZ()
         : false;
   }

   private long a(CpvpSettings var1, Player var2) {
      boolean var3 = this.ab(var2);
      long var4 = this.a(var1, var3);
      long var6 = this.b(var1, var3);
      return Math.max(260L, Math.max(var4, var6) + 140L);
   }
}
