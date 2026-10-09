// Extracted from the user-provided PracticeBot-Source; strategy logic retained.
package com.mlc.mlcbot.practice.x;

import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

record l1(UUID kp, int kq, int kr, int ks, int kt, int ku, int kv, int kw, int kx, int ky, int kz, int kA, boolean kB) {
   static l1 c(Location var0, Location var1, double var2) {
      return new l1(
         var0.getWorld().getUID(),
         k1.f(var0.getX()),
         k1.f(var0.getY()),
         k1.f(var0.getZ()),
         k1.f(var1.getX()),
         k1.f(var1.getY()),
         k1.f(var1.getZ()),
         k1.f(var2),
         Integer.MIN_VALUE,
         Integer.MIN_VALUE,
         Integer.MIN_VALUE,
         Integer.MIN_VALUE,
         true
      );
   }

   static l1 a(Location var0, Location var1, double var2, Block var4, BlockFace var5, boolean var6) {
      return new l1(
         var0.getWorld().getUID(),
         k1.f(var0.getX()),
         k1.f(var0.getY()),
         k1.f(var0.getZ()),
         k1.f(var1.getX()),
         k1.f(var1.getY()),
         k1.f(var1.getZ()),
         k1.f(var2),
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
