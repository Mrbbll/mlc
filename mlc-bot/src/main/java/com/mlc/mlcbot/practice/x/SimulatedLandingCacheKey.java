// Extracted from the user-provided PracticeBot-Source; strategy logic retained.
package com.mlc.mlcbot.practice.x;

import java.util.UUID;

/** 按给定位置和速度缓存模拟落点。 原源码：x/h1.java。 */
record SimulatedLandingCacheKey(UUID jG, UUID jH, int jI, int jJ, int jK, int jL, int jM, int jN, boolean jO, int jP) {
   public UUID bN() {
      return this.jG;
   }

   public UUID cJ() {
      return this.jH;
   }

   public int eh() {
      return this.jI;
   }

   public int ei() {
      return this.jJ;
   }

   public int ej() {
      return this.jK;
   }

   public int ek() {
      return this.jL;
   }

   public int el() {
      return this.jM;
   }

   public int em() {
      return this.jN;
   }

   public boolean en() {
      return this.jO;
   }

   public int eo() {
      return this.jP;
   }
}
