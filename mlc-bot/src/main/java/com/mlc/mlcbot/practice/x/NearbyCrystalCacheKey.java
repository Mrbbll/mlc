// Extracted from the user-provided PracticeBot-Source; strategy logic retained.
package com.mlc.mlcbot.practice.x;

import java.util.UUID;

/** 按世界、位置和搜索范围缓存附近水晶。 原源码：x/ax.java。 */
record NearbyCrystalCacheKey(UUID dW, int dX, int dY, int dZ, int ea) {
   public UUID bN() {
      return this.dW;
   }

   public int ce() {
      return this.dX;
   }

   public int cf() {
      return this.dY;
   }

   public int cg() {
      return this.dZ;
   }

   public int cN() {
      return this.ea;
   }
}
