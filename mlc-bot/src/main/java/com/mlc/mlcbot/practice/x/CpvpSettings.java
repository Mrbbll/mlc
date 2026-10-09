// Extracted from the user-provided PracticeBot-Source; strategy logic retained.
package com.mlc.mlcbot.practice.x;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Logger;

/** 每个机器人的 CPVP 行为开关和难度时序参数。 原源码：x/ae.java。 */
public class CpvpSettings {
   private static final List<String> F = Arrays.asList("EASY", "MEDIUM", "HARD", "PRO");
   private CpvpDifficulty G = CpvpDifficulty.MEDIUM;
   private String H = null;
   private double aggressionWeight = 1.3;
   private double I = 1.5;
   private boolean usePearls = true;
   private boolean useMace = false;
   private boolean useGoldenApples = false;
   private boolean placeObsidian = true;
   private boolean breakBlocks = false;
   private boolean strafingEnabled = true;
   private boolean anchoringMode = false;
   private double healThreshold = 0.4;
   private double lowHpCrystalLethalReserve = 0.0;
   private long pearlCooldownMs = 1800L;
   private double J = 100.0;
   private long K = 500L;
   private long L = 450L;
   private long M = 500L;
   private long N = 500L;
   private long O = 450L;
   private long P = 450L;
   private long Q = 800L;
   private int R = 0;
   private long S = 0L;
   private long T = 0L;
   private int U = 0;
   private long V = 0L;
   private long W = 0L;
   private int X = 0;
   private static Map<String, CpvpDifficultyPreset> Y = new LinkedHashMap<>();
   private static volatile boolean Z = false;
   private static final Logger aA = Logger.getLogger("PracticeBot");

   public static void a(Map<String, CpvpDifficultyPreset> var0) {
      if (var0 != null && !var0.isEmpty()) {
         Y.clear();
         Y.putAll(var0);
         Z = true;
         aA.fine("Loaded " + Y.size() + " CPVP difficulty levels from config");

         for (Entry var2 : Y.entrySet()) {
            aA.fine("  - " + (String)var2.getKey() + ": " + var2.getValue());
         }
      }
   }

   public static boolean aD() {
      return Z;
   }

   public static Set<String> aE() {
      return new LinkedHashSet<>(Y.keySet());
   }

   public static boolean av(String var0) {
      return Y.containsKey(var0.toUpperCase());
   }

   public static CpvpDifficultyPreset aw(String var0) {
      return Y.get(var0.toUpperCase());
   }

   public CpvpSettings() {
      this.ay("MEDIUM");
   }

   public CpvpSettings(CpvpSettings var1) {
      if (var1 != null) {
         this.G = var1.G;
         this.H = var1.H;
         this.aggressionWeight = var1.aggressionWeight;
         this.I = var1.I;
         this.usePearls = var1.usePearls;
         this.useMace = var1.useMace;
         this.useGoldenApples = var1.useGoldenApples;
         this.placeObsidian = var1.placeObsidian;
         this.breakBlocks = var1.breakBlocks;
         this.strafingEnabled = var1.strafingEnabled;
         this.anchoringMode = var1.anchoringMode;
         this.healThreshold = var1.healThreshold;
         this.lowHpCrystalLethalReserve = var1.lowHpCrystalLethalReserve;
         this.pearlCooldownMs = var1.pearlCooldownMs;
         this.J = var1.J;
         this.K = var1.K;
         this.L = var1.L;
         this.M = var1.M;
         this.N = var1.N;
         this.O = var1.O;
         this.P = var1.P;
         this.Q = var1.Q;
         this.R = var1.R;
         this.S = var1.S;
         this.T = var1.T;
         this.U = var1.U;
         this.V = var1.V;
         this.W = var1.W;
         this.X = var1.X;
      }
   }

   public CpvpDifficulty aF() {
      return this.G;
   }

   public String aG() {
      return this.G == CpvpDifficulty.CUSTOM && this.H != null ? this.H : this.G.name();
   }

   public void a(CpvpDifficulty var1) {
      this.G = var1;
      this.H = null;
      this.ay(var1.name());
   }

   public void ax(String var1) {
      if (var1 != null && !var1.isEmpty()) {
         String var2 = var1.toUpperCase();

         try {
            CpvpDifficulty var3 = CpvpDifficulty.valueOf(var2);
            if (var3 != CpvpDifficulty.CUSTOM) {
               this.G = var3;
               this.H = null;
               this.ay(var2);
               return;
            }
         } catch (IllegalArgumentException var4) {
         }

         if (Y.containsKey(var2)) {
            this.G = CpvpDifficulty.CUSTOM;
            this.H = var2;
            this.ay(var2);
         } else {
            this.a(CpvpDifficulty.EASY);
         }
      } else {
         this.a(CpvpDifficulty.EASY);
      }
   }

   public void aH() {
      ArrayList var1 = new ArrayList();

      for (String var3 : F) {
         if (Y.containsKey(var3)) {
            var1.add(var3);
         }
      }

      for (String var8 : Y.keySet()) {
         if (!F.contains(var8)) {
            var1.add(var8);
         }
      }

      if (var1.isEmpty()) {
         var1.add("EASY");
      }

      String var7 = this.aG();
      int var9 = var1.indexOf(var7);
      if (var9 == -1) {
         var9 = 0;
      }

      int var4 = (var9 + 1) % var1.size();
      String var5 = (String)var1.get(var4);
      this.ax(var5);
   }

   public double aI() {
      return this.aggressionWeight;
   }

   public void b(double var1) {
      this.aggressionWeight = Math.max(0.5, Math.min(2.5, var1));
   }

   public double aJ() {
      return this.I;
   }

   public boolean aK() {
      return this.usePearls;
   }

   public void a(boolean var1) {
      this.usePearls = var1;
   }

   public boolean aL() {
      return this.useMace;
   }

   public void b(boolean var1) {
      this.useMace = var1;
   }

   public boolean aM() {
      return this.useGoldenApples;
   }

   public void c(boolean var1) {
      this.useGoldenApples = var1;
   }

   public boolean aN() {
      return this.placeObsidian;
   }

   public void d(boolean var1) {
      this.placeObsidian = var1;
   }

   public boolean aO() {
      return this.breakBlocks;
   }

   public void e(boolean var1) {
      this.breakBlocks = var1;
   }

   public boolean aP() {
      return this.strafingEnabled;
   }

   public void f(boolean var1) {
      this.strafingEnabled = var1;
   }

   public boolean aQ() {
      return this.anchoringMode;
   }

   public void g(boolean var1) {
      this.anchoringMode = var1;
   }

   public double aR() {
      return this.healThreshold;
   }

   public void c(double var1) {
      this.healThreshold = Math.max(0.2, Math.min(0.8, var1));
   }

   public double aS() {
      return 0.0;
   }

   public void d(double var1) {
      this.lowHpCrystalLethalReserve = 0.0;
   }

   public long aT() {
      return this.pearlCooldownMs;
   }

   public void b(long var1) {
      this.pearlCooldownMs = Math.max(500L, Math.min(3000L, var1));
   }

   public double aU() {
      return this.J;
   }

   public void e(double var1) {
      this.J = Math.max(1.0, Math.min(360.0, var1));
   }

   public long aV() {
      return this.N;
   }

   public long aW() {
      return this.M;
   }

   public long aX() {
      return this.N;
   }

   public void c(long var1) {
      this.a(var1, var1);
   }

   public void a(long var1, long var3) {
      long var5 = Math.max(1L, Math.min(var1, var3));
      long var7 = Math.max(1L, Math.max(var1, var3));
      this.M = var5;
      this.N = var7;
      this.K = var7;
   }

   public long aY() {
      return this.P;
   }

   public long aZ() {
      return this.O;
   }

   public long bA() {
      return this.P;
   }

   public void d(long var1) {
      this.b(var1, var1);
   }

   public void b(long var1, long var3) {
      long var5 = Math.max(1L, Math.min(var1, var3));
      long var7 = Math.max(1L, Math.max(var1, var3));
      this.O = var5;
      this.P = var7;
      this.L = var7;
   }

   public long a(Random var1) {
      return this.a(var1, this.M, this.N);
   }

   public long b(Random var1) {
      return this.a(var1, this.O, this.P);
   }

   private long a(Random var1, long var2, long var4) {
      long var6 = Math.max(1L, Math.min(var2, var4));
      long var8 = Math.max(1L, Math.max(var2, var4));
      if (var8 <= var6) {
         return var6;
      } else {
         Random var10 = var1 == null ? new Random() : var1;
         long var11 = var8 - var6 + 1L;
         return var6 + Math.floorMod(var10.nextLong(), var11);
      }
   }

   private long b(Random var1, long var2, long var4) {
      long var6 = Math.max(0L, Math.min(var2, var4));
      long var8 = Math.max(0L, Math.max(var2, var4));
      if (var8 <= var6) {
         return var6;
      } else {
         Random var10 = var1 == null ? new Random() : var1;
         long var11 = var8 - var6 + 1L;
         return var6 + Math.floorMod(var10.nextLong(), var11);
      }
   }

   public void b(int var1) {
      this.R = e(var1);
   }

   public void c(long var1, long var3) {
      this.S = e(var1, var3);
      this.T = f(var1, var3);
   }

   public void c(int var1) {
      this.U = e(var1);
   }

   public void d(long var1, long var3) {
      this.V = e(var1, var3);
      this.W = f(var1, var3);
   }

   public void d(int var1) {
      this.X = e(var1);
   }

   private static int e(int var0) {
      return Math.max(0, Math.min(100, var0));
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

   public int bB() {
      return this.R;
   }

   public long bC() {
      return this.S;
   }

   public long bD() {
      return this.T;
   }

   public long c(Random var1) {
      return this.b(var1, this.S, this.T);
   }

   public int bE() {
      return this.U;
   }

   public long bF() {
      return this.V;
   }

   public long bG() {
      return this.W;
   }

   public long d(Random var1) {
      return this.b(var1, this.V, this.W);
   }

   public int bH() {
      return this.X;
   }

   public boolean bI() {
      return this.R > 0 || this.U > 0 || this.X > 0;
   }

   public long bJ() {
      return this.Q;
   }

   public void e(long var1) {
      this.Q = Math.max(100L, var1);
   }

   public void bK() {
      this.G = CpvpDifficulty.MEDIUM;
      this.H = null;
      this.ay("MEDIUM");
      this.usePearls = true;
      this.useMace = false;
      this.useGoldenApples = false;
      this.placeObsidian = true;
      this.breakBlocks = false;
      this.strafingEnabled = true;
      this.anchoringMode = false;
      this.healThreshold = 0.4;
      this.lowHpCrystalLethalReserve = 0.0;
      this.pearlCooldownMs = 1800L;
      this.J = 100.0;
   }

   public void a(CpvpDefaults var1) {
      if (var1 != null) {
         this.ax(var1.skillLevel);
         this.usePearls = var1.usePearls;
         this.useMace = var1.useMace;
         this.useGoldenApples = var1.useGoldenApples;
         this.placeObsidian = var1.placeObsidian;
         this.breakBlocks = var1.breakBlocks;
         this.strafingEnabled = var1.strafingEnabled;
         this.anchoringMode = var1.anchoringMode;
         this.healThreshold = var1.healThreshold;
         this.d(var1.lowHpCrystalLethalReserve);
         this.pearlCooldownMs = var1.pearlCooldownMs;
         this.e(var1.J);
      }
   }

   private void ay(String var1) {
      CpvpDifficultyPreset var2 = Y.get(var1.toUpperCase());
      if (var2 != null) {
         this.aggressionWeight = var2.aB;
         this.I = var2.aC;
         this.M = var2.aF;
         this.N = var2.aG;
         this.O = var2.aH;
         this.P = var2.aI;
         this.K = var2.aG;
         this.L = var2.aI;
         this.Q = var2.aJ;
         this.R = var2.aK;
         this.S = var2.aL;
         this.T = var2.aM;
         this.U = var2.aN;
         this.V = var2.aO;
         this.W = var2.aP;
         this.X = var2.aQ;
      } else {
         this.aggressionWeight = 1.3;
         this.I = 1.0;
         this.a(120L, 190L);
         this.b(80L, 135L);
         this.Q = 600L;
         this.R = 14;
         this.S = 70L;
         this.T = 160L;
         this.U = 7;
         this.V = 90L;
         this.W = 210L;
         this.X = 18;
      }
   }

   static {
      Y.put("EASY", new CpvpDifficultyPreset(0.7, 1.5, 220L, 320L, 130L, 190L, 800L, 35, 180L, 380L, 18, 160L, 360L, 45));
      Y.put("MEDIUM", new CpvpDifficultyPreset(1.3, 1.0, 120L, 190L, 80L, 135L, 600L, 14, 70L, 160L, 7, 90L, 210L, 18));
      Y.put("HARD", new CpvpDifficultyPreset(1.5, 0.8, 15L, 35L, 13L, 45L, 500L, 4, 20L, 55L, 2, 40L, 90L, 5));
      Y.put("PRO", new CpvpDifficultyPreset(2.0, 0.5, 3L, 14L, 2L, 18L, 500L, 0, 0L, 0L, 0, 0L, 0L, 0));
   }
}
