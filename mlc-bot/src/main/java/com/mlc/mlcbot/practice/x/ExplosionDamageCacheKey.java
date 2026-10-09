// Extracted from the user-provided PracticeBot-Source; strategy logic retained.
package com.mlc.mlcbot.practice.x;

import java.util.UUID;

/** 爆炸位置与受伤玩家对应的伤害计算缓存键。 原源码：x/aw.java。 */
record ExplosionDamageCacheKey(UUID dO, int dP, int dQ, int dR, UUID dS, int dT, int dU, int dV) {
   public UUID bN() {
      return this.dO;
   }

   public int ce() {
      return this.dP;
   }

   public int cf() {
      return this.dQ;
   }

   public int cg() {
      return this.dR;
   }

   public UUID cJ() {
      return this.dS;
   }

   public int cK() {
      return this.dT;
   }

   public int cL() {
      return this.dU;
   }

   public int cM() {
      return this.dV;
   }
}
