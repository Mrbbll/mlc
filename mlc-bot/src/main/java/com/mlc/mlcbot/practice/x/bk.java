// Extracted from the user-provided PracticeBot-Source; strategy logic retained.
package com.mlc.mlcbot.practice.x;

import org.bukkit.block.Block;

public record bk(Block fD, Block fE, double fF, String fG, boolean fH) {
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
