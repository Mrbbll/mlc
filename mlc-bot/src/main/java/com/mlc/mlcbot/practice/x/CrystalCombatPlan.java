// Extracted from the user-provided PracticeBot-Source; strategy logic retained.
package com.mlc.mlcbot.practice.x;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderCrystal;

/** 水晶行动状态、底座、伤害收益和候选实体。 原源码：x/bx.java。 */
public record CrystalCombatPlan(
   CrystalPlanStatus hv,
   String hw,
   Block hx,
   Block hy,
   Block hz,
   Block hA,
   Location hB,
   double hC,
   double hD,
   double hE,
   double hF,
   double hG,
   boolean hH,
   boolean hI,
   boolean hJ,
   boolean hK,
   boolean hL,
   boolean hM,
   EnderCrystal hN
) {
   public static CrystalCombatPlan bi(String var0) {
      return new CrystalCombatPlan(CrystalPlanStatus.NONE, var0, null, null, null, null, null, 0.0, 0.0, 0.0, 0.0, 0.0, false, false, false, false, false, false, null);
   }

   public boolean dM() {
      return this.hv == CrystalPlanStatus.PLACE_SAFE || this.hv == CrystalPlanStatus.BREAK_SAFE || this.hv == CrystalPlanStatus.CREATE_OBSIDIAN_PRESSURE || this.hv == CrystalPlanStatus.SELF_DAMAGE_UNSAFE_RECOVERY;
   }

   public boolean dN() {
      return this.dO();
   }

   public boolean dO() {
      return this.dM();
   }

   public boolean dP() {
      return this.hv == CrystalPlanStatus.SELF_DAMAGE_UNSAFE_RECOVERY;
   }

   public boolean dQ() {
      return q(this.hz);
   }

   public boolean dR() {
      return this.hA != null;
   }

   public Location dS() {
      if (this.hB != null) {
         return this.hB.clone();
      } else if (this.hz != null) {
         return this.hz.getLocation().add(0.5, 1.0, 0.5);
      } else if (this.hA != null) {
         return this.hA.getLocation().add(0.5, 1.0, 0.5);
      } else {
         return this.hy == null ? null : this.hy.getLocation().add(0.5, 0.5, 0.5);
      }
   }

   public boolean dT() {
      return this.hz == null || q(this.hz);
   }

   public static boolean q(Block var0) {
      return var0 != null && j(var0.getType());
   }

   public static boolean j(Material var0) {
      return var0 == Material.OBSIDIAN || var0 == Material.BEDROCK;
   }

   public CrystalPlanStatus dU() {
      return this.hv;
   }

   public String B() {
      return this.hw;
   }

   public Block dV() {
      return this.hx;
   }

   public Block dW() {
      return this.hy;
   }

   public Block dX() {
      return this.hz;
   }

   public Block dY() {
      return this.hA;
   }

   public Location db() {
      return this.hB;
   }

   public double dd() {
      return this.hC;
   }

   public double dc() {
      return this.hD;
   }

   public double i() {
      return this.hE;
   }

   public double dZ() {
      return this.hF;
   }

   public double cT() {
      return this.hG;
   }

   public boolean ea() {
      return this.hH;
   }

   public boolean eb() {
      return this.hI;
   }

   public boolean ec() {
      return this.hJ;
   }

   public boolean ed() {
      return this.hK;
   }

   public boolean ee() {
      return this.hL;
   }

   public boolean ef() {
      return this.hM;
   }

   public EnderCrystal eg() {
      return this.hN;
   }
}
