// Extracted from the user-provided PracticeBot-Source; strategy logic retained.
package com.mlc.mlcbot.practice.x;

import java.util.Locale;
import org.bukkit.Location;
import org.bukkit.block.Block;

public record bg(
   Block eQ, Location eR, double eS, double eT, double eU, double eV, double eW, double eX, double eY, boolean eZ, String fa, boolean fb, String fc
) {
   public boolean cS() {
      return this.eZ && this.fb;
   }

   public double cT() {
      double var1 = l(this.eX);
      double var3 = l(this.eY);
      return Math.min(var1, var3);
   }

   public String cX() {
      if (!this.eZ) {
         return "isSelfDamageSafe";
      } else {
         return !this.fb ? "isCrystalSafeForBot" : "SAFE";
      }
   }

   public String cY() {
      if (!this.eZ) {
         return bf(this.fa);
      } else {
         return !this.fb ? bf(this.fc) : "SAFE";
      }
   }

   public String cZ() {
      return "method="
         + this.cX()
         + " reason="
         + this.cY()
         + " health="
         + k(this.eU)
         + " absorption="
         + k(this.eV)
         + " effectiveHealth="
         + k(this.eW)
         + " maxSelf="
         + k(this.cT())
         + " self="
         + k(this.eT)
         + " enemy="
         + k(this.eS)
         + " crystal="
         + e(this.eR);
   }

   private static double l(double var0) {
      return Double.isFinite(var0) ? var0 : Double.MAX_VALUE;
   }

   private static String bf(String var0) {
      return var0 != null && !var0.isBlank() ? var0 : "UNKNOWN";
   }

   private static String k(double var0) {
      return !Double.isFinite(var0) ? "nan" : String.format(Locale.US, "%.2f", var0);
   }

   private static String e(Location var0) {
      return var0 == null ? "null" : String.format(Locale.US, "%.2f,%.2f,%.2f", var0.getX(), var0.getY(), var0.getZ());
   }

   public Block da() {
      return this.eQ;
   }

   public Location db() {
      return this.eR;
   }

   public double dc() {
      return this.eS;
   }

   public double dd() {
      return this.eT;
   }

   public double i() {
      return this.eU;
   }

   public double de() {
      return this.eV;
   }

   public double df() {
      return this.eW;
   }

   public double dg() {
      return this.eX;
   }

   public double dh() {
      return this.eY;
   }

   public boolean di() {
      return this.eZ;
   }

   public String dj() {
      return this.fa;
   }

   public boolean dk() {
      return this.fb;
   }

   public String dl() {
      return this.fc;
   }
}
