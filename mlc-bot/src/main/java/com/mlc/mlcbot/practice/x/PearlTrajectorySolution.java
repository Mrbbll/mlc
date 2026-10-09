// Extracted from the user-provided PracticeBot-Source; strategy logic retained.
package com.mlc.mlcbot.practice.x;

import org.bukkit.util.Vector;

/** 珍珠发射速度、误差、飞行 tick 和碰撞信息。 原源码：x/bz.java。 */
public final class PearlTrajectorySolution {
   public final Vector hO;
   public final double hP;
   public final boolean hQ;
   public final int hR;
   public final int hS;
   public final boolean hT;

   public PearlTrajectorySolution(Vector var1, double var2, boolean var4, int var5, int var6) {
      this.hO = var1;
      this.hP = var2;
      this.hQ = var4;
      this.hR = var5;
      this.hS = var6;
      this.hT = var6 > 0 && var6 < var5 - 2;
   }
}
