// Extracted from the user-provided PracticeBot-Source; strategy logic retained.
package com.mlc.mlcbot.practice.x;

import org.bukkit.Material;
import org.bukkit.block.Block;

public final class k0 {
   public boolean i(Block var1) {
      Material var2 = var1.getType();
      return var2 == Material.WATER || var2 == Material.LAVA || var2 == Material.BUBBLE_COLUMN || var1.isLiquid();
   }

   public boolean j(Block var1) {
      Material var2 = var1.getType();
      return var2 == Material.LAVA
         || var2 == Material.FIRE
         || var2 == Material.SOUL_FIRE
         || var2 == Material.MAGMA_BLOCK
         || var2 == Material.CACTUS
         || var2 == Material.SWEET_BERRY_BUSH
         || var2 == Material.WITHER_ROSE
         || var2 == Material.POWDER_SNOW;
   }
}
