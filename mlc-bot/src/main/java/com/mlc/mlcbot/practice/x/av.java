// Extracted from the user-provided PracticeBot-Source; strategy logic retained.
package com.mlc.mlcbot.practice.x;

import com.mlc.mlcbot.practice.BotTrait;
import com.mlc.mlcbot.practice.PracticeBotPlugin;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import com.mlc.mlcbot.practice.bridge.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public final class av {
   private static final long cV = 200L;
   private static final double cW = 13.0;
   private static final double cX = 26.0;
   private static final double cY = 12.0;
   private final PracticeBotPlugin cZ;
   private final Random da = new Random();
   private final Map<aw, Double> db = new HashMap<>();
   private int dc = Integer.MIN_VALUE;
   private final Map<aw, Double> dd = new HashMap<>();
   private int de = Integer.MIN_VALUE;
   private final Map<ay, bg> df = new HashMap<>();
   private int dg = Integer.MIN_VALUE;
   private int dh = Integer.MIN_VALUE;
   private int di = 0;
   private final Map<ax, List<EnderCrystal>> dj = new HashMap<>();
   private int dk = Integer.MIN_VALUE;
   private int dl = Integer.MIN_VALUE;
   private int dm = 0;
   private int dn = Integer.MIN_VALUE;
   private int do_counter = 0;
   private int dp = Integer.MIN_VALUE;
   private int dq = 0;
   private final n0 dr = new n0();
   private final m0 ds = new m0();
   private final i1 dt;
   private final k0 du;
   private final k1 dv;
   private final j0 dw;
   private final g1 dx;
   private final bs dy;
   private final bn dz;
   private final bv dA;
   private final f1 dB;
   private final g0 dC;
   private final bm dD;
   private final bo dE;
   private final bh dF;
   private final bf dG;
   private final as dH;
   private final f0 dI;
   private final e0 dJ;
   private final d1 dK;
   private final al dL;
   private final br dM;
   private final au dN;

   public av(PracticeBotPlugin var1) {
      this.cZ = var1;
      this.dt = new i1();
      this.du = new k0();
      this.dv = new k1();
      this.dw = new j0(this.dv);
      this.dx = new g1(this);
      this.dy = new bs(var1);
      this.dz = new bn(this);
      this.dA = new bv(this);
      this.dB = new f1(this);
      this.dC = new g0(this);
      this.dD = new bm(this);
      this.dE = new bo(this);
      this.dF = new bh(this);
      this.dG = new bf(this);
      this.dH = new as(this);
      this.dI = new f0(this);
      this.dJ = new e0(this);
      this.dK = new d1(this);
      this.dL = new al(this, this.dr, this.ds);
      this.dM = new br(this, this.ds);
      this.dN = new au(this);
   }

   public PracticeBotPlugin ca() {
      return this.cZ;
   }

   public boolean cj() {
      return this.cZ != null && this.cZ.getConfigManager() != null && this.cZ.getConfigManager().g1();
   }

   public Random cb() {
      return this.da;
   }

   public n0 ck() {
      return this.dr;
   }

   public m0 cl() {
      return this.ds;
   }

   public i1 cm() {
      return this.dt;
   }

   public k0 cn() {
      return this.du;
   }

   public k1 co() {
      return this.dv;
   }

   public j0 cp() {
      return this.dw;
   }

   public g1 cq() {
      return this.dx;
   }

   public bs cr() {
      return this.dy;
   }

   public bn cs() {
      return this.dz;
   }

   public bv ct() {
      return this.dA;
   }

   public f1 cu() {
      return this.dB;
   }

   public g0 cv() {
      return this.dC;
   }

   public bm cw() {
      return this.dD;
   }

   public bo cx() {
      return this.dE;
   }

   public bh cy() {
      return this.dF;
   }

   public bf cz() {
      return this.dG;
   }

   public as cA() {
      return this.dH;
   }

   public f0 cB() {
      return this.dI;
   }

   public e0 cC() {
      return this.dJ;
   }

   public d1 cD() {
      return this.dK;
   }

   public al cE() {
      return this.dL;
   }

   public br cF() {
      return this.dM;
   }

   public void a(NPC var1, Player var2, Player var3, BotTrait var4) {
      this.dN.a(var1, var2, var3, var4);
   }

   public String cG() {
      return "";
   }

   public void a(NPC var1, Player var2, Player var3, ae var4) {
      this.dL.a(var1, var2, var3, var4);
   }

   public void b(NPC var1, Player var2, Player var3, ae var4) {
      this.dL.b(var1, var2, var3, var4);
   }

   public void a(NPC var1, Player var2, Location var3, m1 var4) {
      this.dL.a(var1, var2, var3, var4);
   }

   public void a(NPC var1, Player var2, Location var3, m1 var4, boolean var5) {
      this.dL.a(var1, var2, var3, var4, var5);
   }

   public void b(NPC var1, Player var2, Location var3, m1 var4) {
      this.dL.b(var1, var2, var3, var4);
   }

   public boolean a(Player var1, Player var2, m1 var3) {
      return this.dL.a(var1, var2, var3);
   }

   public void w(NPC var1) {
      this.dL.w(var1);
   }

   public void a(Player var1, Location var2) {
      this.dL.a(var1, var2);
   }

   public void c(NPC var1, Player var2, Location var3, m1 var4) {
      this.dL.c(var1, var2, var3, var4);
   }

   public Location q(Player var1) {
      return this.dL.q(var1);
   }

   public void c(Player var1, BotTrait var2) {
      this.dy.c(var1, var2);
   }

   public void d(Player var1, BotTrait var2) {
      this.dy.d(var1, var2);
   }

   public void u(Player var1) {
      this.dB.u(var1);
   }

   public void d(UUID var1) {
      this.dr.g(var1);
      this.ds.g(var1);
   }

   public boolean e(ae var1) {
      return this.dt.e(var1);
   }

   public boolean f(ae var1) {
      return this.dt.f(var1);
   }

   public boolean g(ae var1) {
      return this.dt.g(var1);
   }

   public boolean h(ae var1) {
      return this.dt.h(var1);
   }

   public boolean i(ae var1) {
      return this.dt.i(var1);
   }

   public boolean j(ae var1) {
      return this.dt.j(var1);
   }

   public long k(ae var1) {
      return this.dt.k(var1);
   }

   public double l(ae var1) {
      return this.dM.l(var1);
   }

   public boolean a(Player var1, Location var2, ae var3) {
      return this.dv.a(var1, var2, var3);
   }

   public boolean a(Player var1, Block var2, ae var3) {
      return this.dv.a(var1, var2, var3);
   }

   public boolean g(Location var1, Location var2) {
      return this.dv.g(var1, var2);
   }

   public boolean h(Block var1) {
      return this.dv.h(var1);
   }

   public double d(Location var1, Location var2) {
      return this.dv.d(var1, var2);
   }

   public List<EnderCrystal> a(Location var1, double var2) {
      if (var1 != null && var1.getWorld() != null && !(var2 <= 0.0)) {
         int var4 = Bukkit.getCurrentTick();
         if (var4 != this.dk) {
            this.dk = var4;
            this.dj.clear();
         }

         double var5 = Math.max(0.1, var2);
         ax var7 = new ax(var1.getWorld().getUID(), this.h(var1.getX()), this.h(var1.getY()), this.h(var1.getZ()), this.h(var5));
         List var8 = this.dj.get(var7);
         if (var8 != null) {
            return var8;
         } else {
            ArrayList var9 = new ArrayList();

            for (Entity var11 : var1.getWorld().getNearbyEntities(var1, var5, var5, var5)) {
               if (var11 instanceof EnderCrystal var12 && var12.isValid() && !var12.isDead()) {
                  var9.add(var12);
               }
            }

            if (this.dj.size() < 2048) {
               this.dj.put(var7, var9);
            }

            return var9;
         }
      } else {
         return List.of();
      }
   }

   public List<EnderCrystal> a(Player var1, double var2) {
      return var1 == null ? List.of() : this.a(var1.getLocation(), var2);
   }

   public boolean h(Location var1, Location var2) {
      return this.dv.h(var1, var2);
   }

   public double b(Location var1, Player var2) {
      if (var1 != null && var2 != null && var2.getWorld() != null && var1.getWorld() != null) {
         int var3 = Bukkit.getCurrentTick();
         if (var3 != this.dc) {
            this.dc = var3;
            this.db.clear();
         }

         aw var4 = new aw(
            var1.getWorld().getUID(),
            this.h(var1.getX()),
            this.h(var1.getY()),
            this.h(var1.getZ()),
            var2.getUniqueId(),
            var2.getLocation().getBlockX(),
            var2.getLocation().getBlockY(),
            var2.getLocation().getBlockZ()
         );
         Double var5 = this.db.get(var4);
         if (var5 != null) {
            return var5;
         } else {
            double var6 = this.dw.b(var1, var2);
            if (this.db.size() < 4096) {
               this.db.put(var4, var6);
            }

            return var6;
         }
      } else {
         return this.dw.b(var1, var2);
      }
   }

   public double a(Location var1, Player var2) {
      if (var1 != null && var2 != null && var2.getWorld() != null && var1.getWorld() != null) {
         int var3 = Bukkit.getCurrentTick();
         if (var3 != this.de) {
            this.de = var3;
            this.dd.clear();
         }

         aw var4 = new aw(
            var1.getWorld().getUID(),
            this.h(var1.getX()),
            this.h(var1.getY()),
            this.h(var1.getZ()),
            var2.getUniqueId(),
            var2.getLocation().getBlockX(),
            var2.getLocation().getBlockY(),
            var2.getLocation().getBlockZ()
         );
         Double var5 = this.dd.get(var4);
         if (var5 != null) {
            return var5;
         } else {
            double var6 = this.dw.a(var1, var2);
            if (this.dd.size() < 8192) {
               this.dd.put(var4, var6);
            }

            return var6;
         }
      } else {
         return this.dw.a(var1, var2);
      }
   }

   public boolean a(double var1, double var3, double var5) {
      return this.dw.a(var1, var3, var5);
   }

   public j1 a(Player var1, double var2, double var4) {
      return this.dw.a(var1, var2, var4);
   }

   public boolean i(Block var1) {
      return this.du.i(var1);
   }

   public boolean j(Block var1) {
      return this.du.j(var1);
   }

   public boolean a(m1 var1, ae var2, long var3) {
      return this.dB.a(var1, var2, var3);
   }

   public void a(m1 var1, long var2, long var4) {
      this.dB.a(var1, var2, var4);
   }

   public void c(m1 var1, long var2) {
      this.dB.c(var1, var2);
   }

   public void d(m1 var1) {
      this.dB.d(var1);
   }

   public void b(m1 var1, ae var2, long var3) {
      this.dB.b(var1, var2, var3);
   }

   public void a(m1 var1, ae var2, long var3, EnderPearl var5) {
      this.dB.a(var1, var2, var3, var5);
   }

   public void e(m1 var1) {
      this.dB.e(var1);
   }

   public void d(m1 var1, long var2) {
      this.dB.d(var1, var2);
   }

   public void a(Player var1, m1 var2, long var3) {
      this.dB.a(var1, var2, var3);
   }

   public boolean e(m1 var1, long var2) {
      return this.dB.e(var1, var2);
   }

   public boolean c(m1 var1, ae var2, long var3) {
      return this.dB.c(var1, var2, var3);
   }

   public boolean a(m1 var1, ae var2, long var3, boolean var5) {
      return this.dB.a(var1, var2, var3, var5);
   }

   public boolean f(m1 var1, long var2) {
      return this.dB.f(var1, var2);
   }

   public boolean g(m1 var1, long var2) {
      return this.dB.g(var1, var2);
   }

   public EnderCrystal a(Player var1, ae var2) {
      return this.dB.a(var1, var2);
   }

   public int a(Player var1, ae var2, long var3, int var5) {
      return this.dB.a(var1, var2, var3, var5);
   }

   public boolean a(NPC var1, Player var2, Player var3, ae var4, m1 var5, Location var6, Vector var7) {
      return this.dB.a(var1, var2, var3, var4, var5, var6, var7);
   }

   public boolean c(NPC var1, Player var2, Player var3, ae var4, m1 var5, long var6) {
      return this.dC.c(var1, var2, var3, var4, var5, var6);
   }

   public boolean d(Player var1, Player var2, ae var3, m1 var4, long var5) {
      return this.dC.d(var1, var2, var3, var4, var5);
   }

   public boolean a(ae var1, m1 var2, Player var3, long var4, long var6) {
      return this.dC.a(var1, var2, var3, var4, var6);
   }

   public boolean a(Player var1, Player var2, ae var3, m1 var4, long var5, long var7) {
      return this.dC.a(var1, var2, var3, var4, var5, var7);
   }

   public boolean d(NPC var1, Player var2, Player var3, ae var4, m1 var5, long var6) {
      return this.dC.d(var1, var2, var3, var4, var5, var6);
   }

   public boolean e(NPC var1, Player var2, Player var3, ae var4, m1 var5, long var6) {
      return this.dC.e(var1, var2, var3, var4, var5, var6);
   }

   public boolean b(Player var1, m1 var2) {
      return this.aa(var1) != null;
   }

   public boolean a(double var1, double var3) {
      return this.dz.a(var1, var3);
   }

   public boolean f(Player var1, Player var2, Block var3) {
      return this.dz.f(var1, var2, var3);
   }

   public boolean a(Player var1, Player var2, Block var3, m1 var4, long var5) {
      return this.dz.a(var1, var2, var3, var4, var5);
   }

   public double g(Player var1, Player var2, Block var3) {
      return this.dz.g(var1, var2, var3);
   }

   public boolean c(Player var1, m1 var2) {
      return this.dz.c(var1, var2);
   }

   public boolean d(m1 var1, ae var2, long var3) {
      return this.dz.d(var1, var2, var3);
   }

   public void h(m1 var1, long var2) {
      this.dz.h(var1, var2);
   }

   public boolean f(m1 var1) {
      return this.dz.f(var1);
   }

   public void i(m1 var1, long var2) {
      this.dz.i(var1, var2);
   }

   public void g(m1 var1) {
      this.dz.g(var1);
   }

   public void c(m1 var1, Block var2, long var3) {
      this.dz.c(var1, var2, var3);
   }

   public void h(m1 var1) {
      this.dz.h(var1);
   }

   public boolean j(m1 var1, long var2) {
      return this.dz.j(var1, var2);
   }

   public void b(Player var1, m1 var2, long var3) {
      this.dz.b(var1, var2, var3);
   }

   public void i(m1 var1) {
      this.dz.i(var1);
   }

   public int j(Player var1, Player var2) {
      return this.dx.j(var1, var2);
   }

   public Integer b(Player var1, int var2) {
      return this.dx.b(var1, var2);
   }

   public Location c(Player var1, int var2) {
      return this.dx.c(var1, var2);
   }

   public Location d(Player var1, int var2) {
      return this.dx.d(var1, var2);
   }

   public bz c(Location var1, Player var2) {
      return this.dx.c(var1, var2);
   }

   public Location a(World var1, Location var2, double var3) {
      return this.dx.a(var1, var2, var3);
   }

   public Vector i(Location var1, Location var2) {
      return this.dx.i(var1, var2);
   }

   public Vector b(Vector var1, double var2) {
      return this.dx.b(var1, var2);
   }

   public Vector j(Location var1, Location var2) {
      return this.dx.j(var1, var2);
   }

   public boolean a(Vector var1) {
      return this.dx.a(var1);
   }

   public void j(m1 var1) {
      this.dA.j(var1);
   }

   public boolean k(m1 var1, long var2) {
      return this.dA.k(var1, var2);
   }

   public long a(Player var1, m1 var2, ae var3, long var4) {
      return this.dA.a(var1, var2, var3, var4);
   }

   public boolean b(m1 var1, long var2, long var4) {
      return this.dA.b(var1, var2, var4);
   }

   public void k(m1 var1) {
      this.dA.k(var1);
   }

   public boolean b(Player var1, m1 var2, ae var3, long var4) {
      return this.dA.b(var1, var2, var3, var4);
   }

   public long c(Player var1, m1 var2, ae var3, long var4) {
      return this.dA.c(var1, var2, var3, var4);
   }

   public boolean d(Player var1, m1 var2, ae var3, long var4) {
      return this.dA.d(var1, var2, var3, var4);
   }

   public boolean f(NPC var1, Player var2, Player var3, ae var4, m1 var5, long var6) {
      return this.dA.f(var1, var2, var3, var4, var5, var6);
   }

   public boolean b(NPC var1, Player var2, Player var3, m1 var4, long var5, ae var7) {
      return this.dA.b(var1, var2, var3, var4, var5, var7);
   }

   public boolean g(NPC var1, Player var2, Player var3, ae var4, m1 var5, long var6) {
      return this.dA.g(var1, var2, var3, var4, var5, var6);
   }

   public void h(NPC var1, Player var2, Player var3, ae var4, m1 var5, long var6) {
      this.dA.h(var1, var2, var3, var4, var5, var6);
   }

   public void d(Player var1, m1 var2) {
      this.dA.d(var1, var2);
   }

   public double v(Player var1) {
      return this.dA.v(var1);
   }

   public boolean c(NPC var1, Player var2, Player var3, m1 var4, long var5, ae var7) {
      return this.dA.c(var1, var2, var3, var4, var5, var7);
   }

   public boolean i(NPC var1, Player var2, Player var3, ae var4, m1 var5, long var6) {
      return this.dA.i(var1, var2, var3, var4, var5, var6);
   }

   public void a(m1 var1, Player var2, Player var3, long var4) {
      this.dA.a(var1, var2, var3, var4);
   }

   public boolean b(Block var1, Player var2) {
      return this.dD.b(var1, var2);
   }

   public boolean w(Player var1) {
      return this.dD.w(var1);
   }

   public Block x(Player var1) {
      return this.dD.x(var1);
   }

   public Block y(Player var1) {
      return this.dD.y(var1);
   }

   public boolean c(Block var1, Player var2) {
      return this.dD.c(var1, var2);
   }

   public Block k(Player var1, Player var2) {
      return this.dD.k(var1, var2);
   }

   public Block c(Player var1, Player var2, ae var3) {
      return this.dD.c(var1, var2, var3);
   }

   public Block d(Player var1, Player var2, ae var3) {
      return this.dD.e(var1, var2, var3, null, 0L);
   }

   public Block e(Player var1, Player var2, ae var3, m1 var4, long var5) {
      return this.dD.e(var1, var2, var3, var4, var5);
   }

   public boolean a(NPC var1, Player var2, Player var3, Block var4, ae var5, m1 var6, long var7) {
      return this.dF.a(var1, var2, var3, var4, var5, var6, var7);
   }

   public double g(double var1) {
      return this.dF.g(var1);
   }

   public boolean j(NPC var1, Player var2, Player var3, ae var4, m1 var5, long var6) {
      return this.dF.j(var1, var2, var3, var4, var5, var6);
   }

   public boolean k(Block var1) {
      return this.dF.k(var1);
   }

   public boolean a(Block var1, Player var2, m1 var3, long var4) {
      return this.dF.a(var1, var2, var3, var4);
   }

   public boolean a(Player var1, m1 var2, Block var3, long var4) {
      return this.dF.a(var1, var2, var3, var4);
   }

   public boolean k(NPC var1, Player var2, Player var3, ae var4, m1 var5, long var6) {
      return this.dF.k(var1, var2, var3, var4, var5, var6);
   }

   public boolean a(Entity var1, m1 var2, ae var3, long var4) {
      return this.dG.a(var1, var2, var3, var4);
   }

   public boolean a(Player var1, Player var2, ae var3, EnderCrystal var4, m1 var5, long var6) {
      return this.dG.a(var1, var2, var3, var4, var5, var6);
   }

   public boolean a(NPC var1, Player var2, EnderCrystal var3, Player var4, ae var5, m1 var6, long var7) {
      return this.dG.a(var1, var2, var3, var4, var5, var6, var7);
   }

   public void z(Player var1) {
      this.dB.z(var1);
   }

   public boolean b(Player var1, m1 var2, Block var3, long var4) {
      return this.dz.b(var1, var2, var3, var4);
   }

   public boolean a(Player var1, Player var2, ae var3, m1 var4, long var5, Location var7) {
      return this.dE.a(var1, var2, var3, var4, var5, var7);
   }

   public boolean a(NPC var1, Player var2, Block var3, Player var4, ae var5, m1 var6, long var7) {
      return this.dF.a(var1, var2, var4, var3, var5, var6, var7);
   }

   public EnderCrystal aa(Player var1) {
      return this.dG.aa(var1);
   }

   public boolean a(NPC var1, Player var2, Player var3, EnderCrystal var4, ae var5, m1 var6, long var7) {
      return this.dG.a(var1, var2, var3, var4, var5, var6, var7);
   }

   public bg h(Player var1, Player var2, Block var3) {
      if (var1 != null && var2 != null && var3 != null && var3.getWorld() != null) {
         int var4 = Bukkit.getCurrentTick();
         if (var4 != this.dg) {
            this.dg = var4;
            this.df.clear();
         }

         ay var5 = new ay(
            var3.getWorld().getUID(),
            var3.getX(),
            var3.getY(),
            var3.getZ(),
            var1.getUniqueId(),
            var2.getUniqueId(),
            var1.getLocation().getBlockX(),
            var1.getLocation().getBlockY(),
            var1.getLocation().getBlockZ(),
            var2.getLocation().getBlockX(),
            var2.getLocation().getBlockY(),
            var2.getLocation().getBlockZ()
         );
         bg var6 = this.df.get(var5);
         if (var6 != null) {
            return var6;
         } else {
            bg var7 = this.dG.h(var1, var2, var3);
            if (this.df.size() < 8192) {
               this.df.put(var5, var7);
            }

            return var7;
         }
      } else {
         return this.dG.h(var1, var2, var3);
      }
   }

   public boolean a(Player var1, Location var2, Block var3) {
      return this.dG.a(var1, var2, var3);
   }

   public boolean d(Player var1, Block var2) {
      return this.dG.d(var1, var2);
   }

   public boolean b(Player var1, Location var2) {
      return this.dG.b(var1, var2);
   }

   public boolean l(NPC var1, Player var2, Player var3, ae var4, m1 var5, long var6) {
      return this.dG.l(var1, var2, var3, var4, var5, var6);
   }

   public boolean a(NPC var1, Player var2, ae var3, m1 var4, long var5) {
      return this.dG.a(var1, var2, var3, var4, var5);
   }

   public boolean m(NPC var1, Player var2, Player var3, ae var4, m1 var5, long var6) {
      return this.dG.m(var1, var2, var3, var4, var5, var6);
   }

   public void f(Player var1, Player var2, ae var3, m1 var4, long var5) {
      this.dG.f(var1, var2, var3, var4, var5);
   }

   public boolean g(Player var1, Player var2, ae var3, m1 var4, long var5) {
      return this.dG.g(var1, var2, var3, var4, var5);
   }

   public boolean h(Player var1, Player var2, ae var3, m1 var4, long var5) {
      return this.dG.h(var1, var2, var3, var4, var5);
   }

   public boolean i(Player var1, Player var2, ae var3, m1 var4, long var5) {
      return this.dG.i(var1, var2, var3, var4, var5);
   }

   public boolean a(Player var1, Player var2, m1 var3, ae var4, long var5) {
      return this.dG.a(var1, var2, var3, var4, var5);
   }

   public boolean j(Player var1, Player var2, ae var3, m1 var4, long var5) {
      return this.dG.j(var1, var2, var3, var4, var5);
   }

   public bx n(NPC var1, Player var2, Player var3, ae var4, m1 var5, long var6) {
      return this.dG.n(var1, var2, var3, var4, var5, var6);
   }

   public bx k(Player var1, Player var2, ae var3, m1 var4, long var5) {
      return this.dG.k(var1, var2, var3, var4, var5);
   }

   public boolean l(Player var1, Player var2, ae var3, m1 var4, long var5) {
      return this.k(var1, var2, var3, var4, var5).dP();
   }

   public boolean m(Player var1, Player var2, ae var3, m1 var4, long var5) {
      return this.dG.m(var1, var2, var3, var4, var5);
   }

   public boolean n(Player var1, Player var2, ae var3, m1 var4, long var5) {
      return this.dG.n(var1, var2, var3, var4, var5);
   }

   public boolean o(Player var1, Player var2, ae var3, m1 var4, long var5) {
      return this.dG.o(var1, var2, var3, var4, var5);
   }

   public boolean p(Player var1, Player var2, ae var3, m1 var4, long var5) {
      return this.dG.p(var1, var2, var3, var4, var5);
   }

   public boolean q(Player var1, Player var2, ae var3, m1 var4, long var5) {
      return this.dG.q(var1, var2, var3, var4, var5);
   }

   public boolean r(Player var1, Player var2, ae var3, m1 var4, long var5) {
      return this.dG.r(var1, var2, var3, var4, var5);
   }

   public boolean o(NPC var1, Player var2, Player var3, ae var4, m1 var5, long var6) {
      return this.dG.o(var1, var2, var3, var4, var5, var6);
   }

   public double a(Player var1, Player var2, ae var3, Block var4) {
      return this.dH.a(var1, var2, var3, var4);
   }

   public double b(Player var1, Player var2, ae var3) {
      return this.dH.b(var1, var2, var3);
   }

   public boolean e(World var1) {
      return this.dH.e(var1);
   }

   public boolean a(Location var1, Block var2, double var3) {
      return this.dv.a(var1, var2, var3);
   }

   public boolean g(Player var1, Player var2) {
      return this.dH.g(var1, var2);
   }

   public Block h(Player var1, Player var2) {
      return this.dH.h(var1, var2);
   }

   public boolean b(Player var1, Player var2, m1 var3) {
      return this.dE.b(var1, var2, var3);
   }

   public long a(ae var1, boolean var2) {
      return this.dG.a(var1, var2);
   }

   public long b(ae var1, boolean var2) {
      return this.dG.b(var1, var2);
   }

   public boolean ab(Player var1) {
      return this.dG.ab(var1);
   }

   public boolean a(Block var1, Player var2, Player var3, ae var4, boolean var5) {
      return this.dE.a(var1, var2, var3, var4, var5);
   }

   public boolean a(Block var1, Player var2, Player var3, ae var4, boolean var5, boolean var6) {
      return this.dE.a(var1, var2, var3, var4, var5, var6);
   }

   public boolean e(Player var1, Block var2) {
      return this.dG.e(var1, var2);
   }

   public boolean a(Player var1, Player var2, ae var3, Block var4, double var5) {
      return this.dE.a(var1, var2, var3, var4, var5);
   }

   public boolean a(Player var1, Player var2, ae var3, Block var4, double var5, boolean var7, boolean var8, boolean var9) {
      return this.dE.a(var1, var2, var3, var4, var5, var7, var8, var9);
   }

   public boolean p(NPC var1, Player var2, Player var3, ae var4, m1 var5, long var6) {
      return this.dE.p(var1, var2, var3, var4, var5, var6);
   }

   public boolean a(NPC var1, Player var2, Player var3, ae var4, m1 var5, long var6) {
      return this.dH.a(var1, var2, var3, var4, var5, var6);
   }

   public boolean b(NPC var1, Player var2, Player var3, ae var4, m1 var5, long var6) {
      return this.dH.b(var1, var2, var3, var4, var5, var6);
   }

   public bw b(Player var1, Player var2, ae var3, m1 var4, long var5) {
      return this.dH.b(var1, var2, var3, var4, var5);
   }

   public boolean a(bw var1, double var2, Player var4, Player var5, ae var6, m1 var7, long var8) {
      return this.dH.a(var1, var2, var4, var5, var6, var7, var8);
   }

   public double a(Player var1, Player var2, ae var3, m1 var4, long var5) {
      return this.dH.a(var1, var2, var3, var4, var5);
   }

   public double a(Player var1, Player var2, ae var3) {
      return this.dH.a(var1, var2, var3);
   }

   public void a(Player var1, m1 var2) {
      this.dH.a(var1, var2);
   }

   public boolean b(Player var1, Block var2, double var3) {
      return this.dH.b(var1, var2, var3);
   }

   public boolean e(Player var1, Player var2, m1 var3, long var4) {
      return this.dH.e(var1, var2, var3, var4);
   }

   public Location a(Player var1, Player var2, Location var3) {
      return this.dH.a(var1, var2, var3);
   }

   public Location c(Player var1, Player var2, m1 var3) {
      return this.dI.c(var1, var2, var3);
   }

   public Vector a(Location var1, Location var2, Vector var3) {
      return this.dI.a(var1, var2, var3);
   }

   public boolean h(Material var1) {
      return var1 == Material.OBSIDIAN || var1 == Material.BEDROCK || var1 == Material.RESPAWN_ANCHOR;
   }

   public boolean i(Material var1) {
      return var1 == Material.OBSIDIAN || var1 == Material.BEDROCK;
   }

   public boolean ac(Player var1) {
      Block var2 = var1.getLocation().getBlock();
      Block var3 = var1.getLocation().add(0.0, 0.5, 0.0).getBlock();
      return var2.getType() == Material.WATER || var3.getType() == Material.WATER || var2.isLiquid() || var3.isLiquid();
   }

   public boolean ad(Player var1) {
      Block var2 = var1.getEyeLocation().getBlock();
      return var2.getType() == Material.WATER || var2.isLiquid();
   }

   public boolean ae(Player var1) {
      return this.w(var1);
   }

   public int L() {
      int var1 = Bukkit.getCurrentTick();
      if (var1 == this.dh) {
         return this.di;
      } else {
         int var2 = this.cZ != null && this.cZ.getBotManager() != null ? this.cZ.getBotManager().L() : 0;
         this.dh = var1;
         this.di = var2;
         return var2;
      }
   }

   public int h(boolean var1) {
      if (!var1 && this.cZ != null && this.cZ.getConfigManager() != null && this.cZ.getConfigManager().bx()) {
         int var2 = this.L();
         if (var2 >= this.cZ.getConfigManager().c0()) {
            return this.cZ.getConfigManager().d1();
         } else if (var2 >= this.cZ.getConfigManager().bz()) {
            return this.cZ.getConfigManager().d0();
         } else {
            return var2 >= this.cZ.getConfigManager().by() ? this.cZ.getConfigManager().c1() : 1;
         }
      } else {
         return 1;
      }
   }

   public int cH() {
      if (this.cZ != null && this.cZ.getConfigManager() != null && this.cZ.getConfigManager().bx()) {
         return this.L() < this.cZ.getConfigManager().bz() ? 1 : this.cZ.getConfigManager().e0();
      } else {
         return 1;
      }
   }

   public boolean a(Player var1, String var2, boolean var3) {
      int var4 = this.h(var3);
      if (var4 <= 1) {
         return true;
      } else {
         int var5 = var2 == null ? 0 : var2.hashCode();
         if (var1 != null) {
            var5 ^= var1.getUniqueId().hashCode();
         }

         return Math.floorMod(Bukkit.getCurrentTick() + var5, var4) == 0;
      }
   }

   public boolean b(Player var1, String var2, boolean var3) {
      if (!var3 && this.cZ != null && this.cZ.getConfigManager() != null && this.cZ.getConfigManager().bx()) {
         int var4 = this.L();
         if (var4 < this.cZ.getConfigManager().by()) {
            return true;
         } else {
            int var5 = this.i(false);
            if (var5 > 1) {
               int var6 = var2 == null ? 0 : var2.hashCode();
               if (var1 != null) {
                  var6 ^= var1.getUniqueId().hashCode();
               }

               if (Math.floorMod(Bukkit.getCurrentTick() + var6, var5) != 0) {
                  return false;
               }
            }

            int var8 = Bukkit.getCurrentTick();
            if (var8 != this.dl) {
               this.dl = var8;
               this.dm = 0;
            }

            int var7 = this.g(var4);
            if (this.dm >= var7) {
               return false;
            } else {
               this.dm++;
               return true;
            }
         }
      } else {
         return true;
      }
   }

   public int i(boolean var1) {
      if (!var1 && this.cZ != null && this.cZ.getConfigManager() != null && this.cZ.getConfigManager().bx()) {
         int var2 = this.L();
         if (var2 >= 150) {
            return 28;
         } else if (var2 >= 100) {
            return 20;
         } else if (var2 >= this.cZ.getConfigManager().c0()) {
            return 12;
         } else if (var2 >= this.cZ.getConfigManager().bz()) {
            return 8;
         } else {
            return var2 >= this.cZ.getConfigManager().by() ? 5 : 1;
         }
      } else {
         return 1;
      }
   }

   private int g(int var1) {
      if (var1 >= 150) {
         return 5;
      } else if (var1 >= 100) {
         return 7;
      } else if (this.cZ != null && this.cZ.getConfigManager() != null && var1 >= this.cZ.getConfigManager().c0()) {
         return 9;
      } else {
         return this.cZ != null && this.cZ.getConfigManager() != null && var1 >= this.cZ.getConfigManager().bz() ? 12 : 24;
      }
   }

   public boolean a(Player var1, Player var2, m1 var3, double var4, boolean var6) {
      if (!var6 && var1 != null && var2 != null && var3 != null && this.cZ != null && this.cZ.getConfigManager() != null && this.cZ.getConfigManager().bx()) {
         int var7 = this.L();
         if (var7 < this.cZ.getConfigManager().c0()) {
            return false;
         } else if (var3.inKnockback || var3.kZ || var3.oI || var3.le > 1300L || var3.oo > 6) {
            return false;
         } else if (var7 >= 150) {
            return var4 <= 18.0;
         } else {
            return var7 >= 100 ? var4 <= 16.0 : var4 <= 12.0;
         }
      } else {
         return false;
      }
   }

   public boolean c(Player var1, String var2, boolean var3) {
      if (!var3 && this.cZ != null && this.cZ.getConfigManager() != null && this.cZ.getConfigManager().bx()) {
         int var4 = this.L();
         if (var4 < this.cZ.getConfigManager().c0()) {
            return true;
         } else {
            int var5 = this.h(var4);
            if (var5 > 1) {
               int var6 = var2 == null ? 0 : var2.hashCode();
               if (var1 != null) {
                  var6 ^= var1.getUniqueId().hashCode();
               }

               if (Math.floorMod(Bukkit.getCurrentTick() + var6, var5) != 0) {
                  return false;
               }
            }

            int var8 = Bukkit.getCurrentTick();
            if (var8 != this.dn) {
               this.dn = var8;
               this.do_counter = 0;
            }

            int var7 = this.i(var4);
            if (this.do_counter >= var7) {
               return false;
            } else {
               this.do_counter++;
               return true;
            }
         }
      } else {
         return true;
      }
   }

   public boolean d(Player var1, String var2, boolean var3) {
      if (!var3 && this.cZ != null && this.cZ.getConfigManager() != null && this.cZ.getConfigManager().bx()) {
         int var4 = this.L();
         if (var4 < this.cZ.getConfigManager().c0()) {
            return true;
         } else {
            int var5 = var4 >= 150 ? 6 : (var4 >= 100 ? 5 : 3);
            int var6 = var2 == null ? 0 : var2.hashCode();
            if (var1 != null) {
               var6 ^= var1.getUniqueId().hashCode();
            }

            if (Math.floorMod(Bukkit.getCurrentTick() + var6, var5) != 0) {
               return false;
            } else {
               int var7 = Bukkit.getCurrentTick();
               if (var7 != this.dp) {
                  this.dp = var7;
                  this.dq = 0;
               }

               int var8 = var4 >= 150 ? 12 : (var4 >= 100 ? 14 : 18);
               if (this.dq >= var8) {
                  return false;
               } else {
                  this.dq++;
                  return true;
               }
            }
         }
      } else {
         return true;
      }
   }

   private int h(int var1) {
      if (var1 >= 150) {
         return 36;
      } else if (var1 >= 100) {
         return 28;
      } else {
         return this.cZ != null && this.cZ.getConfigManager() != null && var1 >= this.cZ.getConfigManager().c0() ? 14 : 8;
      }
   }

   private int i(int var1) {
      if (var1 >= 150) {
         return 2;
      } else if (var1 >= 100) {
         return 3;
      } else {
         return this.cZ != null && this.cZ.getConfigManager() != null && var1 >= this.cZ.getConfigManager().c0() ? 5 : 8;
      }
   }

   public long j(boolean var1) {
      int var2 = this.h(var1);
      int var3 = this.cZ != null && this.cZ.getConfigManager() != null ? Math.max(1, this.cZ.getConfigManager().v()) : 1;
      return Math.max(55L, Math.max(var2, var3) * 50L + 90L);
   }

   public long k(boolean var1) {
      int var2 = this.i(var1);
      int var3 = this.cZ != null && this.cZ.getConfigManager() != null ? Math.max(1, this.cZ.getConfigManager().v()) : 1;
      return Math.max(this.j(var1), Math.max(var2, var3) * 50L + 120L);
   }

   public long b(Player var1, Player var2, m1 var3, boolean var4) {
      if (this.cZ != null && this.cZ.getConfigManager() != null && this.cZ.getConfigManager().bx()) {
         if (!var4 && this.L() >= this.cZ.getConfigManager().bz()) {
            int var5 = this.L();
            long var6 = this.cZ.getConfigManager().f1();
            if (var5 >= 150) {
               return Math.max(var6, 3200L);
            } else if (var5 >= 100) {
               return Math.max(var6, 2400L);
            } else {
               return var5 >= this.cZ.getConfigManager().c0() ? Math.max(var6, 1600L) : Math.max(var6, 800L);
            }
         } else {
            return this.cZ.getConfigManager().f0();
         }
      } else {
         return var4 ? 90L : 160L;
      }
   }

   public int l(boolean var1) {
      if (this.cZ != null && this.cZ.getConfigManager() != null && this.cZ.getConfigManager().bx()) {
         int var2 = Math.max(1, this.cZ.getConfigManager().v());
         int var3 = this.cZ.getConfigManager().e1();
         int var4 = this.h(var1);
         int var5 = var2 + 1;
         return var1 ? Math.max(2, var5) : Math.max(var3, Math.max(var4, var5));
      } else {
         return 1;
      }
   }

   public boolean cI() {
      return this.cZ != null && this.cZ.getConfigManager() != null && this.cZ.getConfigManager().bx() && this.L() >= this.cZ.getConfigManager().bz();
   }

   public boolean l(Player var1, Player var2) {
      return this.a(var1, var2, 13.0);
   }

   public boolean m(Player var1, Player var2) {
      return this.a(var1, var2, 26.0);
   }

   private boolean a(Player var1, Player var2, double var3) {
      if (var1 != null && var2 != null && !var2.isOnGround()) {
         if (!var1.getWorld().equals(var2.getWorld())) {
            return false;
         } else {
            double var5 = var2.getLocation().getY() - var1.getLocation().getY();
            return !(var5 <= 0.5) && !(var5 > var3) ? this.d(var1.getLocation(), var2.getLocation()) <= 12.0 : false;
         }
      } else {
         return false;
      }
   }

   public int a(Location var1, int var2, int var3) {
      if (var1 != null && var1.getWorld() != null) {
         int var4 = 0;
         int var5 = var1.getBlockX();
         int var6 = var1.getBlockZ();
         World var7 = var1.getWorld();

         for (int var8 = -var2; var8 <= var2; var8++) {
            for (int var9 = -var2; var9 <= var2; var9++) {
               if (var7.getBlockAt(var5 + var8, var3, var6 + var9).getType() == Material.OBSIDIAN) {
                  var4++;
               }

               if (var7.getBlockAt(var5 + var8, var3 - 1, var6 + var9).getType() == Material.OBSIDIAN) {
                  var4++;
               }
            }
         }

         return var4;
      } else {
         return 0;
      }
   }

   public boolean a(Location var1, Location var2, double var3) {
      if (var1 != null && var2 != null) {
         double var5 = var1.getX() - var2.getX();
         double var7 = var1.getZ() - var2.getZ();
         return var5 * var5 + var7 * var7 <= var3 * var3;
      } else {
         return false;
      }
   }

   public int b(Block var1, int var2, int var3) {
      int var4 = 0;

      for (int var5 = -var2; var5 <= var2; var5++) {
         for (int var6 = -var3; var6 <= var3; var6++) {
            for (int var7 = -var2; var7 <= var2; var7++) {
               if (var1.getRelative(var5, var6, var7).getType() == Material.OBSIDIAN) {
                  var4++;
               }
            }
         }
      }

      return var4;
   }

   public int a(Location var1, double var2, Player var4, Player var5, m1 var6, ae var7, long var8) {
      return this.dG.a(var1, var2, var4, var5, var6, var7, var8);
   }

   public Vector n(Player var1, Player var2) {
      return this.dJ.n(var1, var2);
   }

   public Vector d(Player var1, Player var2, m1 var3) {
      return this.dI.d(var1, var2, var3);
   }

   public boolean a(NPC var1, Player var2, Player var3, m1 var4, Location var5, double var6) {
      return this.dI.a(var1, var2, var3, var4, var5, var6);
   }

   public boolean a(NPC var1, Player var2, Player var3, Vector var4, m1 var5, long var6) {
      return this.dI.a(var1, var2, var3, var4, var5, var6);
   }

   public boolean o(Player var1, Player var2) {
      return this.dI.o(var1, var2);
   }

   public Block a(Player var1, Player var2, Vector var3, World var4) {
      return this.dI.a(var1, var2, var3, var4);
   }

   public Vector a(Player var1, Block var2, Vector var3) {
      return this.dI.a(var1, var2, var3);
   }

   public Location l(Block var1) {
      return this.dI.l(var1);
   }

   public Vector a(Player var1, Vector var2) {
      return this.dI.a(var1, var2);
   }

   public boolean a(Block var1, m1 var2, long var3) {
      return this.dI.a(var1, var2, var3);
   }

   public boolean b(Player var1, Block var2, Vector var3) {
      return this.dI.b(var1, var2, var3);
   }

   public void e(Player var1, m1 var2) {
      this.dI.e(var1, var2);
   }

   public void l(m1 var1, long var2) {
      this.dI.l(var1, var2);
   }

   public void c(Player var1, m1 var2, long var3) {
      this.dI.c(var1, var2, var3);
   }

   public boolean a(NPC var1, Player var2, Block var3, Vector var4, m1 var5, long var6) {
      return this.dI.a(var1, var2, var3, var4, var5, var6);
   }

   public boolean a(Player var1, Vector var2, World var3) {
      return this.dI.a(var1, var2, var3);
   }

   public boolean b(Player var1, Vector var2, World var3) {
      return this.dI.b(var1, var2, var3);
   }

   public Block c(Player var1, Vector var2, World var3) {
      return this.dI.c(var1, var2, var3);
   }

   public Block a(Player var1, Vector var2, World var3, double var4) {
      return this.dI.a(var1, var2, var3, var4);
   }

   public boolean f(Player var1, Block var2) {
      return this.dI.f(var1, var2);
   }

   public boolean b(Player var1, Vector var2) {
      return this.dI.b(var1, var2);
   }

   public boolean c(Player var1, Vector var2) {
      return this.dI.c(var1, var2);
   }

   public boolean af(Player var1) {
      return this.dI.af(var1);
   }

   public boolean a(Player var1, Player var2, m1 var3, double var4) {
      return this.dI.a(var1, var2, var3, var4);
   }

   public boolean q(NPC var1, Player var2, Player var3, ae var4, m1 var5, long var6) {
      return this.dI.q(var1, var2, var3, var4, var5, var6);
   }

   public boolean r(NPC var1, Player var2, Player var3, ae var4, m1 var5, long var6) {
      return this.dI.r(var1, var2, var3, var4, var5, var6);
   }

   public boolean d(Player var1, Vector var2, World var3) {
      return this.dJ.d(var1, var2, var3);
   }

   public void s(NPC var1, Player var2, Player var3, ae var4, m1 var5, long var6) {
      this.dJ.s(var1, var2, var3, var4, var5, var6);
   }

   public void a(NPC var1, Player var2, Player var3, ae var4, m1 var5, long var6, boolean var8) {
      this.dJ.a(var1, var2, var3, var4, var5, var6, var8);
   }

   public boolean t(NPC var1, Player var2, Player var3, ae var4, m1 var5, long var6) {
      return this.dJ.t(var1, var2, var3, var4, var5, var6);
   }

   public void u(NPC var1, Player var2, Player var3, ae var4, m1 var5, long var6) {
      this.dJ.u(var1, var2, var3, var4, var5, var6);
   }

   public boolean ag(Player var1) {
      return this.dK.ag(var1);
   }

   public void f(Player var1, m1 var2) {
      this.dK.f(var1, var2);
   }

   public void a(Player var1, m1 var2, Vector var3, long var4, boolean var6) {
      this.dK.a(var1, var2, var3, var4, var6);
   }

   public void ah(Player var1) {
      this.dM.ah(var1);
   }

   public boolean a(Player var1, Material... var2) {
      return this.dy.a(var1, var2);
   }

   public boolean ai(Player var1) {
      return this.dy.ai(var1);
   }

   public boolean a(Player var1, Material var2) {
      return this.dy.a(var1, var2);
   }

   public int b(Player var1, Material var2) {
      return this.dy.b(var1, var2);
   }

   public void aj(Player var1) {
      this.dy.aj(var1);
   }

   public void c(Player var1, Material var2) {
      this.dy.c(var1, var2);
   }

   public boolean ak(Player var1) {
      return this.dy.ak(var1);
   }

   public int al(Player var1) {
      return this.dy.al(var1);
   }

   public boolean d(Player var1, m1 var2, long var3) {
      return this.dM.d(var1, var2, var3);
   }

   public boolean e(Player var1, m1 var2, long var3) {
      return this.dM.e(var1, var2, var3);
   }

   public void g(Player var1, m1 var2) {
      this.dM.g(var1, var2);
   }

   public void h(Player var1, m1 var2) {
      this.dM.h(var1, var2);
   }

   public boolean b(Player var1, Player var2, m1 var3, long var4, double var6) {
      return this.dM.b(var1, var2, var3, var4, var6);
   }

   public void f(Player var1, m1 var2, long var3) {
      this.dB.f(var1, var2, var3);
   }

   public void a(m1 var1, Player var2, long var3) {
      this.dz.a(var1, var2, var3);
   }

   public boolean b(m1 var1, Player var2, long var3) {
      return this.dz.b(var1, var2, var3);
   }

   public void l(m1 var1) {
      this.dz.l(var1);
   }

   public long a(ae var1, m1 var2) {
      return this.dG.e(var1, var2);
   }

   public long b(ae var1, m1 var2) {
      return this.dG.f(var1, var2);
   }

   public void c(ae var1, m1 var2) {
      this.dG.c(var1, var2);
   }

   public void d(ae var1, m1 var2) {
      this.dG.d(var1, var2);
   }

   public boolean a(ae var1, m1 var2, long var3, String var5, boolean var6) {
      return this.dG.a(var1, var2, var3, var5, var6);
   }

   public void m(m1 var1) {
      this.dG.m(var1);
   }

   public void a(m1 var1, ae var2, Player var3, long var4) {
      var1.lB = true;
      var1.mT = 0;
      var1.mU = 0L;
      this.j(var1);
      boolean var6 = var3 != null && this.ab(var3);
      long var7 = var2 == null ? 0L : Math.max(0L, this.a(var2, var6));
      long var9 = var2 == null ? 0L : Math.max(0L, this.b(var2, var6));
      long var11 = Math.max(var7, var9);
      long var13 = Math.max(200L, var11 + 110L);
      long var15 = Math.max(120L, Math.min(300L, var11 + 60L));
      var1.lA = Math.max(var1.lA, var4 + var13);
      var1.oE = Math.max(var1.oE, var4 + var15);
      this.a(var1, var3, var4 + Math.max(var13, var15));
   }

   public boolean s(Player var1, Player var2, ae var3, m1 var4, long var5) {
      if (var4 == null) {
         return false;
      } else if (var4.lB || var4.lt || var4.mq || var5 < var4.oE) {
         return true;
      } else if (var5 < var4.lA || this.j(var4, var5)) {
         return true;
      } else if (var5 < var4.mv && var4.mw != null) {
         return true;
      } else if (this.b(var4, var2, var5)) {
         return true;
      } else {
         if (var4.mp != null) {
            if (Bukkit.getEntity(var4.mp) instanceof EnderCrystal var8 && var8.isValid() && !var8.isDead()) {
               return true;
            }

            this.i(var4);
         }

         return false;
      }
   }

   public boolean t(Player var1, Player var2, ae var3, m1 var4, long var5) {
      if (var4 != null && !var4.os) {
         boolean var7 = this.l(var1, var2) || this.b(var1, var2, var4);
         boolean var8 = this.s(var1, var2, var3, var4, var5);
         if (!var8 && !var7) {
            return false;
         } else {
            return !var7 ? var8 : var8 || this.g(var1, var2, var3, var4, var5) || this.h(var1, var2, var3, var4, var5);
         }
      } else {
         return false;
      }
   }

   public void u(Player var1, Player var2, ae var3, m1 var4, long var5) {
      if (var4 != null && var3 != null) {
         long var7 = Math.max(0L, this.a(var3, this.ab(var2)));
         long var9 = Math.max(0L, this.b(var3, this.ab(var2)));
         long var11 = Math.max(140L, Math.max(var7, var9) + 90L);
         long var13 = var4.mr > 0L ? var4.mr : Math.max(var4.kD, var4.kE);
         if (var4.mq && (var13 <= 0L || var5 - var13 > Math.max(160L, var9 + 90L))) {
            this.g(var4);
         }

         if (var4.lt && !var4.mG) {
            long var15 = Math.max(Math.max(var4.kD, var4.kE), var4.oj);
            if (var15 <= 0L || var5 - var15 > var11) {
               var4.lt = false;
            }
         }

         if (var4.oE > 0L && var5 - var4.oE > var11) {
            var4.oE = 0L;
         }

         if (var4.mp != null && var4.ms > 0L && var5 - var4.ms > Math.max(260L, var9 + 180L) && !this.dG.z(var1, var2, var3, var4, var5)) {
            this.i(var4);
         }

         if (var5 > var4.lA + var11) {
            var4.lA = 0L;
         }

         if (var4.lC > 0L && var5 > var4.lC + var11) {
            this.l(var4);
         }

         if (var4.kM > 0L && var5 > var4.kM + 120L) {
            this.k(var4);
         }
      }
   }

   private int h(double var1) {
      return (int)Math.round(var1 * 1000.0);
   }

   private int i(double var1) {
      return (int)Math.round(var1 * 4.0);
   }
}
