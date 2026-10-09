// Extracted from the user-provided PracticeBot-Source; strategy logic retained.
package com.mlc.mlcbot.practice.x;

public class ag {
   public final double aB;
   public final double aC;
   public final long aD;
   public final long aE;
   public final long aF;
   public final long aG;
   public final long aH;
   public final long aI;
   public final long aJ;
   public final int aK;
   public final long aL;
   public final long aM;
   public final int aN;
   public final long aO;
   public final long aP;
   public final int aQ;

   public ag(double var1, double var3, long var5, long var7, long var9) {
      this(var1, var3, var5, var5, var7, var7, var9);
   }

   public ag(double var1, double var3, long var5, long var7, long var9, long var11, long var13) {
      this(var1, var3, var5, var7, var9, var11, var13, 0, 0L, 0L, 0, 0L, 0L, 0);
   }

   public ag(
      double var1,
      double var3,
      long var5,
      long var7,
      long var9,
      long var11,
      long var13,
      int var15,
      long var16,
      long var18,
      int var20,
      long var21,
      long var23,
      int var25
   ) {
      this.aB = var1;
      this.aC = var3;
      this.aF = g(var5, var7);
      this.aG = h(var5, var7);
      this.aH = g(var9, var11);
      this.aI = h(var9, var11);
      this.aD = this.aG;
      this.aE = this.aI;
      this.aJ = Math.max(100L, var13);
      this.aK = e(var15);
      this.aL = e(var16, var18);
      this.aM = f(var16, var18);
      this.aN = e(var20);
      this.aO = e(var21, var23);
      this.aP = f(var21, var23);
      this.aQ = e(var25);
   }

   private static long g(long var0, long var2) {
      long var4 = Math.max(1L, var0);
      long var6 = Math.max(1L, var2);
      return Math.min(var4, var6);
   }

   private static long h(long var0, long var2) {
      long var4 = Math.max(1L, var0);
      long var6 = Math.max(1L, var2);
      return Math.max(var4, var6);
   }

   private static long e(long var0, long var2) {
      long var4 = Math.max(0L, var0);
      long var6 = Math.max(0L, var2);
      return Math.min(var4, var6);
   }

   private static long f(long var0, long var2) {
      long var4 = Math.max(0L, var0);
      long var6 = Math.max(0L, var2);
      return Math.max(var4, var6);
   }

   private static int e(int var0) {
      return Math.max(0, Math.min(100, var0));
   }

   @Override
   public String toString() {
      return "DifficultyPreset{aggression="
         + this.aB
         + ", place="
         + this.aF
         + "-"
         + this.aG
         + ", break="
         + this.aH
         + "-"
         + this.aI
         + ", sword="
         + this.aJ
         + ", reaction="
         + this.aK
         + "%/"
         + this.aL
         + "-"
         + this.aM
         + ", miss="
         + this.aN
         + "%/"
         + this.aO
         + "-"
         + this.aP
         + ", pearl-restraint="
         + this.aQ
         + "%}";
   }
}
