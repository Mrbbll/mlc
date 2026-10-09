// Extracted from the user-provided PracticeBot-Source; strategy logic retained.
package com.mlc.mlcbot.practice.x;

import java.util.UUID;

/** 水晶位置、机器人与目标状态对应的安全评估缓存键。 原源码：x/ay.java。 */
record CrystalSafetyCacheKey(UUID eb, int ec, int ed, int ee, UUID ef, UUID eg, int eh, int ei, int ej, int ek, int el, int em) {
   public UUID bN() {
      return this.eb;
   }

   public int ce() {
      return this.ec;
   }

   public int cf() {
      return this.ed;
   }

   public int cg() {
      return this.ee;
   }

   public UUID cO() {
      return this.ef;
   }

   public UUID cJ() {
      return this.eg;
   }

   public int cP() {
      return this.eh;
   }

   public int cQ() {
      return this.ei;
   }

   public int cR() {
      return this.ej;
   }

   public int cK() {
      return this.ek;
   }

   public int cL() {
      return this.el;
   }

   public int cM() {
      return this.em;
   }
}
