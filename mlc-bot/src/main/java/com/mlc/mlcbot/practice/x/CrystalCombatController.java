// Extracted from the user-provided PracticeBot-Source; strategy logic retained.
package com.mlc.mlcbot.practice.x;

import com.mlc.mlcbot.practice.PracticeBotPlugin;
import java.util.Random;
import com.mlc.mlcbot.practice.bridge.NPC;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/** 水晶规划、环境扫描、拆障和恢复策略入口。 原源码：x/bf.java。 */
public final class CrystalCombatController {
   static final double ez = 4.5;
   static final double eA = 4.5;
   static final double eB = 3.0;
   static final int eC = 3;
   static final long eD = 125L;
   static final long eE = 300L;
   static final double eF = 3.0;
   static final long eG = 500L;
   static final double eH = 0.45;
   static final double eI = 12.0;
   static final double eJ = 13.0;
   private final CpvpCombatController eK;
   private final PracticeBotPlugin eL;
   private final Random eM;
   private final CrystalEnvironmentScanner eN;
   private final CrystalRecoveryController eO;
   private final CrystalCombatPlanner eP;

   public CrystalCombatController(CpvpCombatController var1) {
      this.eK = var1;
      this.eL = var1.ca();
      this.eM = var1.cb();
      this.eN = new CrystalEnvironmentScanner(this);
      this.eO = new CrystalRecoveryController(this);
      this.eP = new CrystalCombatPlanner(this);
   }

   CpvpCombatController bZ() {
      return this.eK;
   }

   PracticeBotPlugin ca() {
      return this.eL;
   }

   Random cb() {
      return this.eM;
   }

   EnderCrystal ak(Player var1, Player var2, CpvpSettings var3, CpvpCombatState var4, long var5) {
      return this.eN.ak(var1, var2, var3, var4, var5);
   }

   public int a(Location var1, int var2, int var3) {
      return this.eN.a(var1, var2, var3);
   }

   public int a(Location var1, int var2, int var3, int var4) {
      return this.eN.a(var1, var2, var3, var4);
   }

   public int b(Location var1, double var2) {
      return this.eN.b(var1, var2);
   }

   public int a(Location var1, double var2, CpvpCombatState var4, CpvpSettings var5, long var6) {
      return this.eN.a(var1, var2, var4, var5, var6);
   }

   public int b(Player var1, double var2) {
      return this.eN.b(var1, var2);
   }

   public EnderCrystal b(Player var1, CpvpSettings var2) {
      return this.eN.b(var1, var2);
   }

   public boolean a(NPC var1, Player var2, CpvpSettings var3, CpvpCombatState var4, long var5) {
      return this.eO.a(var1, var2, var3, var4, var5);
   }

   public boolean ab(Player var1) {
      return this.eP.ab(var1);
   }

   public boolean l(Player var1, Player var2) {
      return this.eP.l(var1, var2);
   }

   public long a(CpvpSettings var1, boolean var2) {
      return this.eP.a(var1, var2);
   }

   public long b(CpvpSettings var1, boolean var2) {
      return this.eP.b(var1, var2);
   }

   public long e(CpvpSettings var1, CpvpCombatState var2) {
      return this.eP.e(var1, var2);
   }

   public long f(CpvpSettings var1, CpvpCombatState var2) {
      return this.eP.f(var1, var2);
   }

   public void c(CpvpSettings var1, CpvpCombatState var2) {
      this.eP.c(var1, var2);
   }

   public void d(CpvpSettings var1, CpvpCombatState var2) {
      this.eP.d(var1, var2);
   }

   public boolean a(CpvpSettings var1, CpvpCombatState var2, long var3, String var5, boolean var6) {
      return this.eP.a(var1, var2, var3, var5, var6);
   }

   public void m(CpvpCombatState var1) {
      this.eP.m(var1);
   }

   public boolean a(Entity var1, CpvpCombatState var2, CpvpSettings var3, long var4) {
      return this.eN.a(var1, var2, var3, var4);
   }

   public boolean b(Player var1, Player var2, CpvpSettings var3, CpvpCombatState var4, EnderCrystal var5, long var6) {
      return this.eN.b(var1, var2, var3, var4, var5, var6);
   }

   public int a(Location var1, double var2, Player var4, Player var5, CpvpCombatState var6, CpvpSettings var7, long var8) {
      return this.eN.a(var1, var2, var4, var5, var6, var7, var8);
   }

   public boolean a(Player var1, Player var2, CpvpCombatState var3, CpvpSettings var4, long var5) {
      return this.eN.a(var1, var2, var3, var4, var5);
   }

   public boolean j(Player var1, Player var2, CpvpSettings var3, CpvpCombatState var4, long var5) {
      return this.eN.j(var1, var2, var3, var4, var5);
   }

   public EnderCrystal aa(Player var1) {
      return this.eN.aa(var1);
   }

   public boolean a(NPC var1, Player var2, Player var3, EnderCrystal var4, CpvpSettings var5, CpvpCombatState var6, long var7) {
      return this.eO.a(var1, var2, var3, var4, var5, var6, var7);
   }

   public boolean e(Player var1, Block var2) {
      return this.eP.e(var1, var2);
   }

   public boolean i(Block var1) {
      return this.eP.i(var1);
   }

   public boolean j(Block var1) {
      return this.eP.j(var1);
   }

   public CrystalSafetyEvaluation h(Player var1, Player var2, Block var3) {
      return this.eP.h(var1, var2, var3);
   }

   public boolean a(Player var1, Location var2, Block var3) {
      return this.eP.a(var1, var2, var3);
   }

   public boolean d(Player var1, Block var2) {
      return this.eP.d(var1, var2);
   }

   public boolean b(Player var1, Location var2) {
      return this.eP.b(var1, var2);
   }

   public boolean l(NPC var1, Player var2, Player var3, CpvpSettings var4, CpvpCombatState var5, long var6) {
      return this.eO.l(var1, var2, var3, var4, var5, var6);
   }

   public boolean m(NPC var1, Player var2, Player var3, CpvpSettings var4, CpvpCombatState var5, long var6) {
      return this.eO.m(var1, var2, var3, var4, var5, var6);
   }

   public boolean v(NPC var1, Player var2, Player var3, CpvpSettings var4, CpvpCombatState var5, long var6) {
      return this.eO.v(var1, var2, var3, var4, var5, var6);
   }

   public boolean a(NPC var1, Player var2, EnderCrystal var3, Player var4, CpvpSettings var5, CpvpCombatState var6, long var7) {
      return this.eO.a(var1, var2, var3, var4, var5, var6, var7);
   }

   public boolean o(NPC var1, Player var2, Player var3, CpvpSettings var4, CpvpCombatState var5, long var6) {
      return this.eO.o(var1, var2, var3, var4, var5, var6);
   }

   public void f(Player var1, Player var2, CpvpSettings var3, CpvpCombatState var4, long var5) {
      this.eP.f(var1, var2, var3, var4, var5);
   }

   public boolean x(Player var1, Player var2, CpvpSettings var3, CpvpCombatState var4, long var5) {
      return this.eP.x(var1, var2, var3, var4, var5);
   }

   public boolean g(Player var1, Player var2, CpvpSettings var3, CpvpCombatState var4, long var5) {
      return this.eP.g(var1, var2, var3, var4, var5);
   }

   public boolean y(Player var1, Player var2, CpvpSettings var3, CpvpCombatState var4, long var5) {
      return this.eP.y(var1, var2, var3, var4, var5);
   }

   public boolean z(Player var1, Player var2, CpvpSettings var3, CpvpCombatState var4, long var5) {
      return this.eP.z(var1, var2, var3, var4, var5);
   }

   public boolean h(Player var1, Player var2, CpvpSettings var3, CpvpCombatState var4, long var5) {
      return this.eP.h(var1, var2, var3, var4, var5);
   }

   public boolean a(Player var1, Player var2, CpvpSettings var3, EnderCrystal var4, CpvpCombatState var5, long var6) {
      return this.eP.a(var1, var2, var3, var4, var5, var6);
   }

   public boolean i(Player var1, Player var2, CpvpSettings var3, CpvpCombatState var4, long var5) {
      return this.eP.i(var1, var2, var3, var4, var5);
   }

   public boolean j(double var1) {
      return this.eP.j(var1);
   }

   public boolean aa(Player var1, Player var2, CpvpSettings var3, CpvpCombatState var4, long var5) {
      return this.eP.aa(var1, var2, var3, var4, var5);
   }

   public boolean ab(Player var1, Player var2, CpvpSettings var3, CpvpCombatState var4, long var5) {
      return this.eP.ab(var1, var2, var3, var4, var5);
   }

   public boolean ac(Player var1, Player var2, CpvpSettings var3, CpvpCombatState var4, long var5) {
      return this.eP.ac(var1, var2, var3, var4, var5);
   }

   public CrystalCombatPlan n(NPC var1, Player var2, Player var3, CpvpSettings var4, CpvpCombatState var5, long var6) {
      return this.eP.n(var1, var2, var3, var4, var5, var6);
   }

   public CrystalCombatPlan k(Player var1, Player var2, CpvpSettings var3, CpvpCombatState var4, long var5) {
      return this.eP.k(var1, var2, var3, var4, var5);
   }

   public boolean m(Player var1, Player var2, CpvpSettings var3, CpvpCombatState var4, long var5) {
      return this.eP.m(var1, var2, var3, var4, var5);
   }

   public boolean n(Player var1, Player var2, CpvpSettings var3, CpvpCombatState var4, long var5) {
      return this.eP.n(var1, var2, var3, var4, var5);
   }

   public boolean o(Player var1, Player var2, CpvpSettings var3, CpvpCombatState var4, long var5) {
      return this.eP.o(var1, var2, var3, var4, var5);
   }

   public boolean q(Player var1, Player var2, CpvpSettings var3, CpvpCombatState var4, long var5) {
      return this.eP.q(var1, var2, var3, var4, var5);
   }

   public boolean r(Player var1, Player var2, CpvpSettings var3, CpvpCombatState var4, long var5) {
      return this.eP.r(var1, var2, var3, var4, var5);
   }

   public boolean p(Player var1, Player var2, CpvpSettings var3, CpvpCombatState var4, long var5) {
      return this.eP.p(var1, var2, var3, var4, var5);
   }

   public boolean d(Player var1, Player var2, CpvpCombatState var3, long var4, boolean var6) {
      return this.eP.d(var1, var2, var3, var4, var6);
   }
}
