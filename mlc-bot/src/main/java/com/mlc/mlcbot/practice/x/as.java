// Extracted from the user-provided PracticeBot-Source; strategy logic retained.
package com.mlc.mlcbot.practice.x;

import com.mlc.mlcbot.practice.PracticeBotPlugin;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import com.mlc.mlcbot.practice.bridge.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

public final class as {
   static final double cg = 4.5;
   static final double ch = 4.5;
   static final double ci = 4.15;
   static final double cj = 3.15;
   static final double ck = 0.95;
   static final long cl = 110L;
   static final long cm = 900L;
   static final double cn = 5.0;
   static final double co = 1.25;
   static final double cp = 0.48;
   static final long cq = 980L;
   private final av cr;
   private final PracticeBotPlugin cs;
   private final Random ct;
   private final ar cu;
   private final an cv;
   private final ao cw;
   private final Map<at, Integer> cx = new HashMap<>();
   private int cy = Integer.MIN_VALUE;

   public as(av var1) {
      this.cr = var1;
      this.cs = var1.ca();
      this.ct = var1.cb();
      this.cu = new ar(this);
      this.cv = new an(this);
      this.cw = new ao(this);
   }

   av bZ() {
      return this.cr;
   }

   PracticeBotPlugin ca() {
      return this.cs;
   }

   Random cb() {
      return this.ct;
   }

   public boolean a(NPC var1, Player var2, Player var3, ae var4, m1 var5, long var6) {
      return this.cu.a(var1, var2, var3, var4, var5, var6);
   }

   public boolean b(NPC var1, Player var2, Player var3, ae var4, m1 var5, long var6) {
      return this.cu.b(var1, var2, var3, var4, var5, var6);
   }

   public bw b(Player var1, Player var2, ae var3, m1 var4, long var5) {
      if (var1 != null && var2 != null && var4 != null) {
         boolean var7 = var4.mT >= 3 || var5 <= var4.mW;
         boolean var8 = this.cr.cI();
         boolean var9 = this.cu.e(var1, var2, var4, var5) && this.cr.d(var1.getLocation(), var2.getLocation()) <= 3.4;
         boolean var10 = var8
            && var9
            && this.cr.a(var3, var4, var2, var5, 160L)
            && this.cr.a(var1, Material.RESPAWN_ANCHOR)
            && this.cr.a(var1, Material.GLOWSTONE);
         boolean var11 = var7 || var4.mG || var4.mH || var5 < var4.mA || var2.getHealth() <= 8.0 || var10 || !var8 && var9;
         if (this.a(var1, var2, var4, var5, var7)) {
            return var4.mY;
         } else if (var10 && !this.cr.d(var1, "anchor-close-plan", false)) {
            return this.a(var1, var2, var4, var5, var7, 900L) ? var4.mY : null;
         } else if (var11 || this.cr.b(var1, "anchor-plan", false)) {
            bw var12 = this.cu.b(var1, var2, var3, var4, var5);
            this.a(var1, var2, var4, var5, var7, var12);
            return var12;
         } else {
            return this.a(var1, var2, var4, var5, var7, this.cr.k(false)) ? var4.mY : null;
         }
      } else {
         return this.cu.b(var1, var2, var3, var4, var5);
      }
   }

   public boolean a(NPC var1, Player var2, Player var3, ae var4, m1 var5, bw var6, long var7) {
      return this.cw.a(var1, var2, var3, var4, var5, var6, var7);
   }

   public boolean e(Player var1, Player var2, m1 var3, long var4) {
      return this.cu.e(var1, var2, var3, var4);
   }

   public boolean a(double var1, double var3, double var5, boolean var7, boolean var8, boolean var9, boolean var10) {
      return this.cv.a(var1, var3, var5, var7, var8, var9, var10);
   }

   public long b(ae var1) {
      return this.cw.b(var1);
   }

   public long c(ae var1) {
      return this.cw.c(var1);
   }

   public long d(ae var1) {
      return this.cw.d(var1);
   }

   public long j(long var1, long var3) {
      return this.cw.j(var1, var3);
   }

   public boolean a(bw var1, double var2, Player var4, Player var5, ae var6, m1 var7, long var8) {
      return this.cv.a(var1, var2, var4, var5, var6, var7, var8);
   }

   public boolean a(bw var1, m1 var2, long var3) {
      return this.cv.a(var1, var2, var3);
   }

   public double a(Player var1, Player var2, ae var3, m1 var4, long var5) {
      if (var1 == null || var2 == null || var4 == null) {
         return this.cv.a(var1, var2, var3, var4, var5);
      } else if (this.f(var1, var2, var4, var5)) {
         return var4.nF;
      } else {
         double var7 = this.cv.a(var1, var2, var3, var4, var5);
         this.a(var1, var2, var4, var5, var7);
         return var7;
      }
   }

   public double a(Player var1, Player var2, ae var3, Block var4) {
      return this.cv.a(var1, var2, var3, var4);
   }

   public double a(Player var1, Player var2, ae var3) {
      return this.cv.a(var1, var2, var3);
   }

   public boolean a(Player var1, Player var2, ae var3, m1 var4, long var5, Block var7, double var8) {
      return this.cv.a(var1, var2, var3, var4, var5, var7, var8);
   }

   public int a(Block var1, int var2) {
      return this.cv.a(var1, var2);
   }

   public boolean e(Material var1) {
      return this.cv.e(var1);
   }

   public boolean e(World var1) {
      return var1 != null && var1.getEnvironment() != Environment.NETHER;
   }

   public boolean b(Player var1, Player var2, Block var3) {
      return this.cw.b(var1, var2, var3);
   }

   public boolean c(Player var1, Player var2, Block var3) {
      return this.cw.c(var1, var2, var3);
   }

   public boolean d(Player var1, Player var2, Block var3) {
      return this.cw.d(var1, var2, var3);
   }

   public boolean a(Player var1, Block var2, double var3) {
      return this.cw.a(var1, var2, var3);
   }

   public boolean b(Player var1, Block var2) {
      return this.cw.b(var1, var2);
   }

   public boolean a(Player var1, bw var2) {
      return this.cw.a(var1, var2);
   }

   public boolean a(Player var1, Block var2, Block var3) {
      return this.cw.a(var1, var2, var3);
   }

   public boolean b(Player var1, Block var2, double var3) {
      return this.cw.b(var1, var2, var3);
   }

   public boolean a(Location var1, Block var2, double var3) {
      return this.cw.a(var1, var2, var3);
   }

   public boolean a(Location var1, Location var2, Block var3) {
      return this.cw.a(var1, var2, var3);
   }

   public int a(Block var1, int var2, int var3) {
      if (var1 != null && var1.getWorld() != null) {
         this.cd();
         at var4 = new at(var1.getWorld().getUID(), var1.getX(), var1.getY(), var1.getZ(), Math.max(0, var2), Math.max(0, var3));
         Integer var5 = this.cx.get(var4);
         if (var5 != null) {
            return var5;
         } else {
            int var6 = this.cw.a(var1, var2, var3);
            this.cx.put(var4, var6);
            return var6;
         }
      } else {
         return 0;
      }
   }

   public void cc() {
      this.cx.clear();
      this.cy = Bukkit.getCurrentTick();
   }

   public Block e(Player var1, Player var2, Block var3) {
      return this.cw.e(var1, var2, var3);
   }

   public boolean b(Player var1, Block var2, Block var3) {
      return this.cw.b(var1, var2, var3);
   }

   public BlockFace f(Location var1, Location var2) {
      return this.cw.f(var1, var2);
   }

   public boolean b(Block var1) {
      return this.cw.b(var1);
   }

   public boolean d(Block var1) {
      return this.cw.d(var1);
   }

   public boolean a(Block var1, Player var2) {
      return this.cw.a(var1, var2);
   }

   public boolean b(Player var1, Player var2, bw var3) {
      return this.cw.b(var1, var2, var3);
   }

   public boolean a(Player var1, Player var2, bw var3, boolean var4) {
      return this.cw.a(var1, var2, var3, var4);
   }

   public boolean i(Player var1, Player var2) {
      return this.cw.i(var1, var2);
   }

   public boolean a(Player var1, Player var2, bw var3, ae var4) {
      return this.cw.a(var1, var2, var3, var4);
   }

   public boolean c(Player var1, bw var2) {
      return this.cw.c(var1, var2);
   }

   public boolean a(double var1, double var3, double var5, boolean var7) {
      return this.cv.a(var1, var3, var5, var7);
   }

   public double a(Location var1, Player var2) {
      return this.cv.a(var1, var2);
   }

   public double b(Player var1, Player var2, ae var3) {
      return this.cv.b(var1, var2, var3);
   }

   public boolean r(Player var1) {
      return this.cv.r(var1);
   }

   public boolean s(Player var1) {
      return this.cv.s(var1);
   }

   public boolean f(Player var1, Player var2) {
      return this.cv.f(var1, var2);
   }

   public int t(Player var1) {
      return this.cv.t(var1);
   }

   public boolean f(Material var1) {
      return this.cv.f(var1);
   }

   public boolean g(Material var1) {
      return this.cv.g(var1);
   }

   public boolean g(Player var1, Player var2) {
      return this.cv.g(var1, var2);
   }

   public boolean a(Player var1, Block var2) {
      return this.cv.a(var1, var2);
   }

   public boolean a(Player var1, Player var2, Block var3) {
      return this.cv.a(var1, var2, var3);
   }

   public Block h(Player var1, Player var2) {
      return this.cv.h(var1, var2);
   }

   public boolean a(Player var1, Player var2, bw var3) {
      return this.cv.a(var1, var2, var3);
   }

   public boolean g(Block var1) {
      return this.cw.g(var1);
   }

   public void a(m1 var1, Location var2) {
      this.cw.a(var1, var2);
   }

   public void a(Player var1, m1 var2) {
      this.cw.a(var1, var2);
   }

   public Location a(Player var1, Player var2, Location var3) {
      return this.cw.a(var1, var2, var3);
   }

   public void a(NPC var1, Player var2, Player var3, ae var4, m1 var5, bw var6) {
      this.cw.a(var1, var2, var3, var4, var5, var6);
   }

   private void cd() {
      int var1 = Bukkit.getCurrentTick();
      int var2 = this.cr.cH();
      if (this.cy == Integer.MIN_VALUE || var1 - this.cy >= var2) {
         this.cy = var1;
         this.cx.clear();
      }
   }

   private boolean a(Player var1, Player var2, m1 var3, long var4, boolean var6) {
      return this.a(var1, var2, var3, var6) && var3.mZ == var4;
   }

   private boolean a(Player var1, Player var2, m1 var3, long var4, boolean var6, long var7) {
      return this.a(var1, var2, var3, var6) && var4 - var3.mZ >= 0L && var4 - var3.mZ <= var7;
   }

   private boolean a(Player var1, Player var2, m1 var3, boolean var4) {
      return var3.ni
         && var4 == var3.nh
         && var2.getUniqueId().equals(var3.na)
         && var1.getLocation().getBlockX() == var3.nb
         && var1.getLocation().getBlockY() == var3.nc
         && var1.getLocation().getBlockZ() == var3.nd
         && var2.getLocation().getBlockX() == var3.ne
         && var2.getLocation().getBlockY() == var3.nf
         && var2.getLocation().getBlockZ() == var3.ng;
   }

   private void a(Player var1, Player var2, m1 var3, long var4, boolean var6, bw var7) {
      var3.mY = var7;
      var3.mZ = var4;
      var3.na = var2.getUniqueId();
      var3.nb = var1.getLocation().getBlockX();
      var3.nc = var1.getLocation().getBlockY();
      var3.nd = var1.getLocation().getBlockZ();
      var3.ne = var2.getLocation().getBlockX();
      var3.nf = var2.getLocation().getBlockY();
      var3.ng = var2.getLocation().getBlockZ();
      var3.nh = var6;
      var3.ni = true;
   }

   private boolean f(Player var1, Player var2, m1 var3, long var4) {
      return var3.nG == var4
         && var2.getUniqueId().equals(var3.nH)
         && var1.getLocation().getBlockX() == var3.nI
         && var1.getLocation().getBlockY() == var3.nJ
         && var1.getLocation().getBlockZ() == var3.nK
         && var2.getLocation().getBlockX() == var3.nL
         && var2.getLocation().getBlockY() == var3.nM
         && var2.getLocation().getBlockZ() == var3.nN;
   }

   private void a(Player var1, Player var2, m1 var3, long var4, double var6) {
      var3.nF = var6;
      var3.nG = var4;
      var3.nH = var2.getUniqueId();
      var3.nI = var1.getLocation().getBlockX();
      var3.nJ = var1.getLocation().getBlockY();
      var3.nK = var1.getLocation().getBlockZ();
      var3.nL = var2.getLocation().getBlockX();
      var3.nM = var2.getLocation().getBlockY();
      var3.nN = var2.getLocation().getBlockZ();
   }
}
