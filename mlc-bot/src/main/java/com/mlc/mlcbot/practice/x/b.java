package com.mlc.mlcbot.practice.x;
import org.bukkit.configuration.file.FileConfiguration;
public final class b {
 private final FileConfiguration f;
 public b(FileConfiguration f){this.f=f;}
 public FileConfiguration g(){return f;}
 public af e(){
    af defaults=new af();
    defaults.skillLevel=f.getString("cpvp.defaults.skill-level","MEDIUM");
    defaults.usePearls=f.getBoolean("cpvp.defaults.use-pearls",true);
    defaults.useMace=f.getBoolean("cpvp.defaults.use-mace",false);
    defaults.useGoldenApples=f.getBoolean("cpvp.defaults.use-golden-apples",false);
    defaults.placeObsidian=f.getBoolean("cpvp.defaults.place-obsidian",true);
    defaults.breakBlocks=f.getBoolean("cpvp.defaults.break-blocks",false);
    defaults.strafingEnabled=f.getBoolean("cpvp.defaults.strafing-enabled",true);
    defaults.anchoringMode=f.getBoolean("cpvp.defaults.anchoring-mode",false);
    defaults.healThreshold=f.getDouble("cpvp.defaults.heal-threshold",.4);
    defaults.lowHpCrystalLethalReserve=f.getDouble("cpvp.defaults.low-hp-crystal-lethal-reserve",.05);
    defaults.pearlCooldownMs=f.getLong("cpvp.defaults.pearl-cooldown-ms",1800);
    defaults.J=f.getDouble("cpvp.defaults.fov-degrees",120);
    return defaults;
 }

   public int v() {
      return Math.max(1, this.f.getInt("ai.tick-interval", 1));
   }

   public double ah() {
      return Math.max(1.0, this.f.getDouble("pvp.crit-multiplier", 1.5));
   }
   public double ai() {
      return Math.max(0.5, this.f.getDouble("pvp.reach-distances.short", 2.0));
   }
   public double aj() {
      return Math.max(0.5, this.f.getDouble("pvp.reach-distances.normal", 3.0));
   }
   public double ak() {
      return Math.max(0.5, this.f.getDouble("pvp.reach-distances.extended", 3.5));
   }
   public double al() {
      return Math.max(0.5, this.f.getDouble("pvp.reach-distances.advanced", 4.0));
   }
   public float am() {
      return (float)Math.max(0.1, this.f.getDouble("pvp.chase-speeds.low", 1.0));
   }
   public float an() {
      return (float)Math.max(0.1, this.f.getDouble("pvp.chase-speeds.medium", 1.2));
   }
   public float ao() {
      return (float)Math.max(0.1, this.f.getDouble("pvp.chase-speeds.high", 1.4));
   }

   public double at() {
      return this.f.getDouble("pvp.strafe.speeds.low", 0.16);
   }
   public double au() {
      return this.f.getDouble("pvp.strafe.speeds.medium", 0.2);
   }
   public double av() {
      return this.f.getDouble("pvp.strafe.speeds.high", 0.24);
   }
   public long aw() {
      return this.f.getLong("pvp.strafe.switch-intervals.low", 800L);
   }
   public long ax() {
      return this.f.getLong("pvp.strafe.switch-intervals.medium", 550L);
   }
   public long ay() {
      return this.f.getLong("pvp.strafe.switch-intervals.high", 350L);
   }
   public double az() {
      return this.f.getDouble("pvp.approach-strafe.speeds.low", 0.1);
   }
   public double ba() {
      return this.f.getDouble("pvp.approach-strafe.speeds.medium", 0.13);
   }
   public double bb() {
      return this.f.getDouble("pvp.approach-strafe.speeds.high", 0.16);
   }
   public long bc() {
      return this.f.getLong("pvp.approach-strafe.switch-intervals.low", 700L);
   }
   public long bd() {
      return this.f.getLong("pvp.approach-strafe.switch-intervals.medium", 500L);
   }
   public long be() {
      return this.f.getLong("pvp.approach-strafe.switch-intervals.high", 350L);
   }
   public double bf() {
      return this.f.getDouble("pvp.approach-strafe.chase-speeds.low", 0.16);
   }
   public double bg() {
      return this.f.getDouble("pvp.approach-strafe.chase-speeds.medium", 0.2);
   }
   public double bh() {
      return this.f.getDouble("pvp.approach-strafe.chase-speeds.high", 0.24);
   }

   public double bj() {
      return Math.min(360.0, Math.max(0.0, this.f.getDouble("shield.block-angle", 180.0)));
   }

   public boolean bx() {
      return this.f.getBoolean("cpvp.performance.adaptive-enabled", true);
   }
   public int by() {
      return Math.max(1, this.f.getInt("cpvp.performance.low-bot-threshold", 10));
   }
   public int bz() {
      return Math.max(this.by(), this.f.getInt("cpvp.performance.medium-bot-threshold", 20));
   }
   public int c0() {
      return Math.max(this.bz(), this.f.getInt("cpvp.performance.high-bot-threshold", 35));
   }
   public int c1() {
      return Math.max(1, this.f.getInt("cpvp.performance.low-scan-period-ticks", 3));
   }
   public int d0() {
      return Math.max(1, this.f.getInt("cpvp.performance.medium-scan-period-ticks", 4));
   }
   public int d1() {
      return Math.max(1, this.f.getInt("cpvp.performance.high-scan-period-ticks", 6));
   }
   public int e0() {
      return Math.max(1, Math.min(4, this.f.getInt("cpvp.performance.anchor-count-cache-ticks", 3)));
   }
   public int e1() {
      return Math.max(1, Math.min(4, this.f.getInt("cpvp.performance.crystal-search-cache-ticks", 2)));
   }
   public int f0() {
      return Math.max(50, Math.min(1000, this.f.getInt("cpvp.performance.navigation-retarget-ms", 250)));
   }
   public int f1() {
      return Math.max(this.f0(), Math.min(3500, this.f.getInt("cpvp.performance.high-load-navigation-retarget-ms", 1200)));
   }

   public boolean g1() {
      return this.f.getBoolean("debug.verbose-pvp", false);
   }

}
