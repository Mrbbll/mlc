// Extracted from the user-provided PracticeBot-Source; strategy logic retained.
package com.mlc.mlcbot.practice.x;

import org.bukkit.block.Block;

/** 黑曜石底座的最终选择、评分及恢复标记。 原源码：x/bk.java。 */
public record ObsidianPlacementPlan(Block fD, Block fE, double fF, String fG, boolean fH) {
   public Block dq() {
      return this.fD;
   }

   public Block dr() {
      return this.fE;
   }

   public double ds() {
      return this.fF;
   }

   public String dt() {
      return this.fG;
   }

   public boolean dp() {
      return this.fH;
   }
}
