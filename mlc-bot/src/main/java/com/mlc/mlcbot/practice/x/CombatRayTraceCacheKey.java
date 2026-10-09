// Extracted from the user-provided PracticeBot-Source; strategy logic retained.
package com.mlc.mlcbot.practice.x;

import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/** 视线和方块交互射线检测的缓存键。 原源码：x/l1.java。 */
record CombatRayTraceCacheKey(UUID kp, int kq, int kr, int ks, int kt, int ku, int kv, int kw, int kx, int ky, int kz, int kA, boolean kB) {
   static CombatRayTraceCacheKey c(Location var0, Location var1, double var2) {
      return new CombatRayTraceCacheKey(
         var0.getWorld().getUID(),
         CombatGeometry.f(var0.getX()),
         CombatGeometry.f(var0.getY()),
         CombatGeometry.f(var0.getZ()),
         CombatGeometry.f(var1.getX()),
         CombatGeometry.f(var1.getY()),
         CombatGeometry.f(var1.getZ()),
         CombatGeometry.f(var2),
         Integer.MIN_VALUE,
         Integer.MIN_VALUE,
         Integer.MIN_VALUE,
         Integer.MIN_VALUE,
         true
      );
   }

   static CombatRayTraceCacheKey a(Location var0, Location var1, double var2, Block var4, BlockFace var5, boolean var6) {
      return new CombatRayTraceCacheKey(
         var0.getWorld().getUID(),
         CombatGeometry.f(var0.getX()),
         CombatGeometry.f(var0.getY()),
         CombatGeometry.f(var0.getZ()),
         CombatGeometry.f(var1.getX()),
         CombatGeometry.f(var1.getY()),
         CombatGeometry.f(var1.getZ()),
         CombatGeometry.f(var2),
         var4.getX(),
         var4.getY(),
         var4.getZ(),
         var5 == null ? Integer.MIN_VALUE : var5.ordinal(),
         var6
      );
   }

   public UUID bN() {
      return this.kp;
   }

   public int bO() {
      return this.kq;
   }

   public int bP() {
      return this.kr;
   }

   public int bQ() {
      return this.ks;
   }

   public int bR() {
      return this.kt;
   }

   public int bS() {
      return this.ku;
   }

   public int bT() {
      return this.kv;
   }

   public int bU() {
      return this.kw;
   }

   public int bV() {
      return this.kx;
   }

   public int bW() {
      return this.ky;
   }

   public int bX() {
      return this.kz;
   }

   public int bY() {
      return this.kA;
   }

   public boolean es() {
      return this.kB;
   }
}
