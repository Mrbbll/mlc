// Extracted from the user-provided PracticeBot-Source; strategy logic retained.
package com.mlc.mlcbot.practice.x;

import java.util.UUID;

/** 水晶底座方块有效性检查的缓存键。 原源码：x/l0.java。 */
record CrystalBaseValidityCacheKey(UUID kl, int km, int kn, int ko) {
   public UUID bN() {
      return this.kl;
   }

   public int ce() {
      return this.km;
   }

   public int cf() {
      return this.kn;
   }

   public int cg() {
      return this.ko;
   }
}
