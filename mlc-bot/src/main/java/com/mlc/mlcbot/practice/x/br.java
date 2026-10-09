// Extracted from the user-provided PracticeBot-Source; strategy logic retained.
package com.mlc.mlcbot.practice.x;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class br {
   private static final int gP = 2;
   private static final long gQ = 1600L;
   private static final double gR = 4.0;
   private final av gS;
   private final m0 gT;

   public br(av var1, m0 var2) {
      this.gS = var1;
      this.gT = var2;
   }

   public double l(ae var1) {
      return var1.aR();
   }

   public void ah(Player var1) {
      var1.setFoodLevel(20);
      var1.setSaturation(20.0F);
      var1.setExhaustion(0.0F);
      double var2 = var1.getAttribute(Attribute.MAX_HEALTH).getValue();
      double var4 = var1.getHealth();
      if (var4 < var2) {
         double var6 = 0.15;
         double var8 = Math.min(var2, var4 + var6);
         var1.setHealth(var8);
      }
   }

   public boolean b(Player var1, Player var2, m1 var3, long var4, double var6) {
      if (var1 == null || var2 == null || var3 == null) {
         return true;
      } else {
         return var2.isValid() && !var2.isDead() ? var6 <= 4.0 || var3.lt || var3.lB || var3.mq || var3.lu || var3.mg : false;
      }
   }

   public boolean d(Player var1, m1 var2, long var3) {
      PlayerInventory var5 = var1.getInventory();
      int var6 = -1;

      for (int var7 = 0; var7 < 9; var7++) {
         ItemStack var8 = var5.getItem(var7);
         if (var8 != null && var8.getType() == Material.GOLDEN_APPLE) {
            var6 = var7;
            break;
         }
      }

      if (var6 == -1) {
         for (int var10 = 9; var10 < var5.getSize(); var10++) {
            ItemStack var11 = var5.getItem(var10);
            if (var11 != null && var11.getType() == Material.GOLDEN_APPLE) {
               ItemStack var9 = var5.getItem(5);
               var5.setItem(5, var11);
               var5.setItem(var10, var9);
               var6 = 5;
               break;
            }
         }
      }

      if (var6 == -1) {
         var2.ln = 2;
         var2.lo = 2;
         return false;
      } else {
         if (var2.lo <= 0 || var2.ln <= 0) {
            var2.lo = 1 + this.gS.cb().nextInt(2);
         }

         if (var2.ln >= var2.lo) {
            this.gS.u(var1);
            return false;
         } else {
            this.gS.j(var2);
            var1.setSprinting(false);
            var5.setHeldItemSlot(var6);
            var2.lk = true;
            var2.lp++;
            var2.ll = var3;
            var2.lm = 0L;
            this.a(var1, var2, 1.0F);
            return true;
         }
      }
   }

   public boolean e(Player var1, m1 var2, long var3) {
      long var5 = var3 - var2.ll;
      if (var5 >= 1600L) {
         this.g(var1, var2);
         return false;
      } else {
         this.a(var1, var2, 0.42F);
         if (var3 - var2.lm >= 200L) {
            var1.getWorld().playSound(var1.getLocation(), Sound.ENTITY_GENERIC_EAT, 0.8F, 1.0F + this.gS.cb().nextFloat() * 0.2F);
            var2.lm = var3;
            Location var7 = var1.getEyeLocation().add(var1.getLocation().getDirection().multiply(0.3));
            var1.getWorld().spawnParticle(Particle.ITEM, var7, 5, 0.1, 0.1, 0.1, 0.05, new ItemStack(Material.GOLDEN_APPLE));
         }

         return true;
      }
   }

   public void g(Player var1, m1 var2) {
      this.gS.cr().d(var1, Material.GOLDEN_APPLE);
      var1.getWorld().playSound(var1.getLocation(), Sound.ENTITY_PLAYER_BURP, 0.8F, 1.0F);
      var1.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 1, false, true));
      var1.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 2400, 0, false, true));
      var2.lk = false;
      var2.ln++;
      double var3 = var1.getAttribute(Attribute.MAX_HEALTH).getValue();
      double var5 = var1.getHealth() / var3;
      int var7 = var2.lo <= 0 ? 1 : Math.min(2, var2.lo);
      if (var5 < 0.5 && var2.ln < var7) {
         long var8 = var2.lp;
         com.mlc.mlcbot.combat.CombatRuntime.runTaskLater(
               this.gS.ca().plugin(),
               () -> {
                  if (var1 != null && var1.isValid() && !var1.isDead()) {
                     if (var2.lp == var8) {
                        if (!var2.lk) {
                           if ((var2.lg == null ? null : Bukkit.getEntity(var2.lg)) instanceof Player var6
                              && var6.isValid()
                              && !var6.isDead()
                              && var6.getWorld().equals(var1.getWorld())) {
                              double var7x = var1.getLocation().distance(var6.getLocation());
                              if (this.b(var1, var6, var2, System.currentTimeMillis(), var7x)) {
                                 this.gS.u(var1);
                                 return;
                              }
                           }

                           this.d(var1, var2, System.currentTimeMillis());
                        }
                     }
                  }
               },
               2L
            );
      } else {
         this.gS.u(var1);
      }
   }

   private void a(Player var1, m1 var2, float var3) {
      if (var1 != null) {
         Player var4 = this.t(var2);
         if (var4 != null && var4.isValid() && !var4.isDead() && var4.getWorld().equals(var1.getWorld())) {
            Location var22 = var4.getEyeLocation();
            Location var23 = var1.getEyeLocation();
            double var24 = var22.getX() - var23.getX();
            double var9 = var22.getY() - var23.getY();
            double var11 = var22.getZ() - var23.getZ();
            double var13 = Math.sqrt(var24 * var24 + var11 * var11);
            if (var13 < 0.001) {
               var13 = 0.001;
            }

            float var15 = (float)Math.toDegrees(Math.atan2(-var24, var11));
            float var16 = (float)(-Math.toDegrees(Math.atan2(var9, var13)));
            var16 = Math.max(-65.0F, Math.min(65.0F, var16));
            float[] var17 = this.gT.a(var1.getUniqueId(), var1x -> new float[]{var1.getLocation().getYaw(), var1.getLocation().getPitch()});
            float var18 = var15 - var17[0];

            while (var18 > 180.0F) {
               var18 -= 360.0F;
            }

            while (var18 < -180.0F) {
               var18 += 360.0F;
            }

            float var19 = Math.max(0.05F, Math.min(1.0F, var3));
            float var20 = var17[0] + var18 * var19;
            float var21 = var17[1] + (var16 - var17[1]) * var19;
            var21 = Math.max(-65.0F, Math.min(65.0F, var21));
            var17[0] = var20;
            var17[1] = var21;
            var1.setRotation(var20, var21);
         } else {
            float var5 = var1.getLocation().getYaw();
            float var6 = 0.0F;
            var1.setRotation(var5, var6);
            float[] var7 = this.gT.a(var1.getUniqueId(), var2x -> new float[]{var5, var6});
            var7[0] = var5;
            var7[1] = var6;
         }
      }
   }

   private Player t(m1 var1) {
      if (var1 != null && var1.lg != null) {
         return Bukkit.getEntity(var1.lg) instanceof Player var3 ? var3 : null;
      } else {
         return null;
      }
   }

   public void h(Player var1, m1 var2) {
      var2.lk = false;
      var2.lp++;
      var2.lm = 0L;
      this.gS.u(var1);
   }
}
