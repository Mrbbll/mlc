// Extracted from the user-provided PracticeBot-Source; strategy logic retained.
package com.mlc.mlcbot.practice.x;

import java.util.UUID;

/** 按玩家当前位置和速度缓存预测落点。 原源码：x/i0.java。 */
record PlayerLandingCacheKey(UUID jQ, UUID jR, int jS, int jT, int jU, int jV, int jW, int jX, boolean jY, int jZ) {
   public UUID bN() {
      return this.jQ;
   }

   public UUID cJ() {
      return this.jR;
   }

   public int eh() {
      return this.jS;
   }

   public int ei() {
      return this.jT;
   }

   public int ej() {
      return this.jU;
   }

   public int ek() {
      return this.jV;
   }

   public int el() {
      return this.jW;
   }

   public int em() {
      return this.jX;
   }

   public boolean en() {
      return this.jY;
   }

   public int ep() {
      return this.jZ;
   }
}
