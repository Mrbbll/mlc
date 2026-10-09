// Extracted from the user-provided PracticeBot-Source; strategy logic retained.
package com.mlc.mlcbot.practice.x;

import org.bukkit.block.Block;
import org.bukkit.entity.EnderCrystal;

/** 候选水晶实体及其底座方块。 原源码：x/bc.java。 */
record CrystalCandidate(EnderCrystal et, Block eu) {
   public EnderCrystal cU() {
      return this.et;
   }

   public Block cV() {
      return this.eu;
   }
}
