// Extracted from the user-provided PracticeBot-Source; strategy logic retained.
package com.mlc.mlcbot.practice.x;

import java.util.UUID;

/** 按世界、方块位置和扫描范围缓存附近锚的数量。 原源码：x/at.java。 */
record NearbyAnchorCountCacheKey(UUID cz, int cA, int cB, int cC, int cD, int cE) {
   public UUID bN() {
      return this.cz;
   }

   public int ce() {
      return this.cA;
   }

   public int cf() {
      return this.cB;
   }

   public int cg() {
      return this.cC;
   }

   public int ch() {
      return this.cD;
   }

   public int ci() {
      return this.cE;
   }
}
