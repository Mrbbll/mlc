// Extracted from the user-provided PracticeBot-Source; strategy logic retained.
package com.mlc.mlcbot.practice.x;

import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/** 锚交互射线检测的缓存键。 原源码：x/aq.java。 */
record AnchorRayTraceCacheKey(UUID bS, int bT, int bU, int bV, int bW, int bX, int bY, int bZ, int ca, int cb, int cc, int cd) {
   static AnchorRayTraceCacheKey a(Location var0, Location var1, double var2, Block var4, BlockFace var5) {
      return new AnchorRayTraceCacheKey(
         var0.getWorld().getUID(),
         AnchorActionExecutor.f(var0.getX()),
         AnchorActionExecutor.f(var0.getY()),
         AnchorActionExecutor.f(var0.getZ()),
         AnchorActionExecutor.f(var1.getX()),
         AnchorActionExecutor.f(var1.getY()),
         AnchorActionExecutor.f(var1.getZ()),
         AnchorActionExecutor.f(var2),
         var4.getX(),
         var4.getY(),
         var4.getZ(),
         var5 == null ? Integer.MIN_VALUE : var5.ordinal()
      );
   }
}
