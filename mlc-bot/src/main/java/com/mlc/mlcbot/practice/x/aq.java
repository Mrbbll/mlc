// Extracted from the user-provided PracticeBot-Source; strategy logic retained.
package com.mlc.mlcbot.practice.x;

import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

record aq(UUID bS, int bT, int bU, int bV, int bW, int bX, int bY, int bZ, int ca, int cb, int cc, int cd) {
   static aq a(Location var0, Location var1, double var2, Block var4, BlockFace var5) {
      return new aq(
         var0.getWorld().getUID(),
         ao.f(var0.getX()),
         ao.f(var0.getY()),
         ao.f(var0.getZ()),
         ao.f(var1.getX()),
         ao.f(var1.getY()),
         ao.f(var1.getZ()),
         ao.f(var2),
         var4.getX(),
         var4.getY(),
         var4.getZ(),
         var5 == null ? Integer.MIN_VALUE : var5.ordinal()
      );
   }
}
