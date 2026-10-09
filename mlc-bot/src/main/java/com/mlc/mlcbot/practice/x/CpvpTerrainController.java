// Extracted from the user-provided PracticeBot-Source; strategy logic retained.
package com.mlc.mlcbot.practice.x;

import com.mlc.mlcbot.practice.PracticeBotPlugin;
import com.mlc.mlcbot.practice.bridge.NPC;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/** 地形分析、障碍绕行和撤退脱困的统一入口。 原源码：x/f0.java。 */
public final class CpvpTerrainController {
   static final double it = 4.5;
   static final double iu = 1.55;
   static final double iv = 2.55;
   static final double iw = 2.1;
   static final double ix = 1.15;
   static final long iy = 900L;
   static final double iz = 0.55;
   static final double iA = 1.35;
   static final long iB = 250L;
   static final long iC = 110L;
   static final double iD = 0.22;
   static final double iE = 0.26;
   static final double iF = 0.34;
   static final double iG = 0.45;
   static final double iH = 1.15;
   static final double iI = 0.34;
   static final long iJ = 500L;
   static final double iK = 0.45;
   static final long iL = 600L;
   static final double iM = 12.0;
   static final double iN = 4.0;
   static final double iO = 10.0;
   static final long iP = 600L;
   private final CpvpCombatController iQ;
   private final PracticeBotPlugin iR;
   private final CpvpTerrainAnalyzer iS;
   private final CpvpAvoidanceController iT;
   private final CpvpEscapeController iU;

   public CpvpTerrainController(CpvpCombatController var1) {
      this.iQ = var1;
      this.iR = var1.ca();
      this.iS = new CpvpTerrainAnalyzer(this);
      this.iT = new CpvpAvoidanceController(this);
      this.iU = new CpvpEscapeController(this);
   }

   CpvpCombatController bZ() {
      return this.iQ;
   }

   PracticeBotPlugin ca() {
      return this.iR;
   }

   public Block a(Player var1, Vector var2, World var3, double var4) {
      return this.iS.a(var1, var2, var3, var4);
   }

   public boolean f(Player var1, Block var2) {
      return this.iS.f(var1, var2);
   }

   public boolean a(Player var1, Vector var2, World var3) {
      return this.iS.a(var1, var2, var3);
   }

   public boolean o(Player var1, Player var2) {
      return this.iS.o(var1, var2);
   }

   public boolean v(Player var1, Player var2) {
      return this.iS.v(var1, var2);
   }

   public boolean b(Player var1, Vector var2, World var3) {
      return this.iS.b(var1, var2, var3);
   }

   public Vector d(Player var1, Player var2, CpvpCombatState var3) {
      return this.iU.d(var1, var2, var3);
   }

   public Location c(Player var1, Player var2, CpvpCombatState var3) {
      return this.iU.c(var1, var2, var3);
   }

   public Vector a(Location var1, Location var2, Vector var3) {
      return this.iU.a(var1, var2, var3);
   }

   public boolean a(NPC var1, Player var2, Player var3, CpvpCombatState var4, Location var5, double var6) {
      return this.iU.a(var1, var2, var3, var4, var5, var6);
   }

   public Vector a(Location var1, Location var2, CpvpCombatState var3) {
      return this.iU.a(var1, var2, var3);
   }

   public boolean a(Player var1, Vector var2, double var3) {
      return this.iT.a(var1, var2, var3);
   }

   public Vector d(Player var1, Vector var2) {
      return this.iT.d(var1, var2);
   }

   public Location l(Block var1) {
      return this.iS.l(var1);
   }

   public Block a(Player var1, Player var2, Vector var3, World var4) {
      return this.iS.a(var1, var2, var3, var4);
   }

   public Vector a(Player var1, Block var2, Vector var3) {
      return this.iS.a(var1, var2, var3);
   }

   public Vector a(Player var1, Vector var2) {
      return this.iS.a(var1, var2);
   }

   public boolean a(Block var1, CpvpCombatState var2, long var3) {
      return this.iS.a(var1, var2, var3);
   }

   public boolean b(Player var1, Block var2, Vector var3) {
      return this.iS.b(var1, var2, var3);
   }

   public void u(CpvpCombatState var1) {
      this.iS.u(var1);
   }

   public void l(CpvpCombatState var1, long var2) {
      this.iS.l(var1, var2);
   }

   public void c(Player var1, CpvpCombatState var2, long var3) {
      this.iS.c(var1, var2, var3);
   }

   public void e(Player var1, CpvpCombatState var2) {
      this.iS.e(var1, var2);
   }

   public boolean a(NPC var1, Player var2, Block var3, Vector var4, CpvpCombatState var5, long var6) {
      return this.iS.a(var1, var2, var3, var4, var5, var6);
   }

   public boolean a(NPC var1, Player var2, Player var3, Vector var4, CpvpCombatState var5, long var6) {
      return this.iT.a(var1, var2, var3, var4, var5, var6);
   }

   public boolean c(Player var1, Vector var2) {
      return this.iT.c(var1, var2);
   }

   public boolean b(Player var1, Vector var2) {
      return this.iS.b(var1, var2);
   }

   public boolean e(Player var1, Vector var2) {
      return this.iT.e(var1, var2);
   }

   public boolean a(Player var1, Player var2, CpvpCombatState var3, double var4) {
      return this.iT.a(var1, var2, var3, var4);
   }

   public boolean q(NPC var1, Player var2, Player var3, CpvpSettings var4, CpvpCombatState var5, long var6) {
      return this.iU.q(var1, var2, var3, var4, var5, var6);
   }

   public boolean r(NPC var1, Player var2, Player var3, CpvpSettings var4, CpvpCombatState var5, long var6) {
      return this.iU.r(var1, var2, var3, var4, var5, var6);
   }

   public boolean b(Player var1, Player var2, Vector var3) {
      return this.iU.b(var1, var2, var3);
   }

   public boolean as(Player var1) {
      return this.iT.as(var1);
   }

   public boolean af(Player var1) {
      return this.iT.af(var1);
   }

   public boolean b(Player var1, Player var2, double var3) {
      return this.iT.b(var1, var2, var3);
   }

   public Block c(Player var1, Vector var2, World var3) {
      return this.iS.c(var1, var2, var3);
   }
}
