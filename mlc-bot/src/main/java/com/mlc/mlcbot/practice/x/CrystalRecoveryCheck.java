// Extracted from the user-provided PracticeBot-Source; strategy logic retained.
package com.mlc.mlcbot.practice.x;

import org.bukkit.block.Block;

/** 水晶恢复检查结果及可用恢复方块。 原源码：x/bd.java。 */
record CrystalRecoveryCheck(boolean ev, String ew, Block ex) {
   static CrystalRecoveryCheck be(String var0) {
      return new CrystalRecoveryCheck(false, var0, null);
   }

   static CrystalRecoveryCheck a(String var0, Block var1) {
      return new CrystalRecoveryCheck(true, var0, var1);
   }

   public boolean cW() {
      return this.ev;
   }

   public String B() {
      return this.ew;
   }

   public Block cV() {
      return this.ex;
   }
}
