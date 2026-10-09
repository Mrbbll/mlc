// Extracted from the user-provided PracticeBot-Source; strategy logic retained.
package com.mlc.mlcbot.practice.x;

import org.bukkit.block.Block;

/** 一个黑曜石候选方块及其评分。 原源码：x/bl.java。 */
record ScoredObsidianCandidate(Block fI, double fJ) {
   public Block du() {
      return this.fI;
   }

   public double dv() {
      return this.fJ;
   }
}
