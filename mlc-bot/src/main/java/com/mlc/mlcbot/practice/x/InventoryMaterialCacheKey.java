// Extracted from the user-provided PracticeBot-Source; strategy logic retained.
package com.mlc.mlcbot.practice.x;

import java.util.UUID;
import org.bukkit.Material;

/** 按玩家和物品材质缓存库存查询。 原源码：x/bu.java。 */
record InventoryMaterialCacheKey(UUID gZ, Material ha) {
   public UUID dA() {
      return this.gZ;
   }

   public Material dB() {
      return this.ha;
   }
}
