// Extracted from the user-provided PracticeBot-Source; strategy logic retained.
package com.mlc.mlcbot.practice.x;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/** 估算爆炸伤害并评估自伤与目标伤害的取舍。 原源码：x/j0.java。 */
public final class ExplosionDamageEvaluator {
   private final CombatGeometry ka;

   public ExplosionDamageEvaluator(CombatGeometry var1) {
      this.ka = var1;
   }

   public ExplosionTradeEvaluation a(Player var1, double var2, double var4) {
      return var1 == null ? new ExplosionTradeEvaluation(false, "NO_BOT", 0.0, 0.0) : this.a(var2, var1.getHealth(), Math.max(0.0, var1.getAbsorptionAmount()), var4);
   }

   public boolean a(double var1, double var3, double var5) {
      return this.a(var1, var3, 0.0, var5).cS();
   }

   private ExplosionTradeEvaluation a(double var1, double var3, double var5, double var7) {
      double var9 = Math.max(0.0, var3) + Math.max(0.0, var5);
      double var11 = Math.max(0.0, var9 - 0.01);
      if (var1 >= var11) {
         return new ExplosionTradeEvaluation(false, "LETHAL_SELF_DAMAGE", var9, var11);
      } else if (var7 < 1.5 && var1 > 3.0) {
         return new ExplosionTradeEvaluation(false, "LOW_VALUE_TRADE", var9, Math.min(var11, 3.0));
      } else if (var7 < 4.0 && var1 > 5.0) {
         return new ExplosionTradeEvaluation(false, "MEDIUM_VALUE_TRADE", var9, Math.min(var11, 5.0));
      } else if (var1 > 4.0 && var7 < var1 * 0.75) {
         return new ExplosionTradeEvaluation(false, "BAD_DAMAGE_TRADE", var9, Math.min(var11, Math.max(3.0, var7 / 0.75)));
      } else {
         double var13 = Math.max(8.0, var7 / 1.8);
         return var1 > 8.0 && var7 < var1 * 1.8
            ? new ExplosionTradeEvaluation(false, "HIGH_SELF_DAMAGE_RATIO", var9, Math.min(var11, var13))
            : new ExplosionTradeEvaluation(true, "SELF_DAMAGE_SAFE", var9, Math.min(var11, var13));
      }
   }

   public double b(Location var1, Player var2) {
      Location var3 = var2.getLocation().add(0.0, 1.0, 0.0);
      double var4 = var1.distance(var3);
      if (var4 > 12.0) {
         return 0.0;
      } else {
         double var6 = this.e(var1, var2);
         if (var6 < 0.1) {
            return 0.0;
         } else {
            double var8 = (1.0 - var4 / 12.0) * var6;
            double var10 = (var8 * var8 + var8) * 42.0 + 1.0;

            try {
               double var12 = var2.getAttribute(Attribute.ARMOR).getValue();
               double var14 = var2.getAttribute(Attribute.ARMOR_TOUGHNESS).getValue();
               double var16 = Math.min(20.0, Math.max(var12 / 5.0, var12 - 4.0 * var10 / (var14 + 8.0)));
               var10 *= 1.0 - var16 / 25.0;
            } catch (Exception var18) {
               var10 *= 0.5;
            }

            int var20 = this.ay(var2);
            if (var20 > 0) {
               double var13 = Math.min(20, var20) * 0.04;
               var10 *= 1.0 - Math.min(0.8, var13);
            }

            var10 *= 0.52;
            return Math.max(0.0, var10);
         }
      }
   }

   public double a(Location var1, Player var2) {
      return this.b(var1, var2) * 0.9;
   }

   public int ay(Player var1) {
      int var2 = 0;

      for (ItemStack var6 : var1.getInventory().getArmorContents()) {
         if (var6 != null && var6.hasItemMeta()) {
            var2 += var6.getEnchantmentLevel(Enchantment.PROTECTION);
            var2 += var6.getEnchantmentLevel(Enchantment.BLAST_PROTECTION) * 2;
         }
      }

      return Math.min(20, var2);
   }

   public int az(Player var1) {
      int var2 = 0;

      for (ItemStack var6 : var1.getInventory().getArmorContents()) {
         if (var6 != null && var6.hasItemMeta()) {
            var2 += var6.getEnchantmentLevel(Enchantment.BLAST_PROTECTION);
         }
      }

      return var2;
   }

   public boolean f(Player var1, Location var2) {
      if (var1 != null && var2 != null) {
         Location var3 = var1.getLocation();
         World var4 = var3.getWorld();
         if (var4 == null) {
            return false;
         } else {
            int[] var5 = new int[]{0, 1};
            boolean var6 = false;
            boolean var7 = false;
            boolean var8 = false;
            boolean var9 = false;

            for (int var13 : var5) {
               Location var14 = var3.clone().add(0.0, var13, 0.0);
               Block var15 = var14.clone().add(0.0, 0.0, -0.6).getBlock();
               if (var15.getType().isSolid() && this.p(var15.getType())) {
                  var6 = true;
               }

               Block var16 = var14.clone().add(0.0, 0.0, 0.6).getBlock();
               if (var16.getType().isSolid() && this.p(var16.getType())) {
                  var7 = true;
               }

               Block var17 = var14.clone().add(0.6, 0.0, 0.0).getBlock();
               if (var17.getType().isSolid() && this.p(var17.getType())) {
                  var8 = true;
               }

               Block var18 = var14.clone().add(-0.6, 0.0, 0.0).getBlock();
               if (var18.getType().isSolid() && this.p(var18.getType())) {
                  var9 = true;
               }
            }

            int var19 = 0;
            if (var6) {
               var19++;
            }

            if (var7) {
               var19++;
            }

            if (var8) {
               var19++;
            }

            if (var9) {
               var19++;
            }

            if (var19 >= 2) {
               double var20 = var2.getX() - var3.getX();
               double var21 = var2.getZ() - var3.getZ();
               if (var20 > 0.3 && var8) {
                  return true;
               }

               if (var20 < -0.3 && var9) {
                  return true;
               }

               if (var21 > 0.3 && var7) {
                  return true;
               }

               if (var21 < -0.3 && var6) {
                  return true;
               }

               if (var20 > 0.1 && var21 > 0.1 && var8 && var7) {
                  return true;
               }

               if (var20 > 0.1 && var21 < -0.1 && var8 && var6) {
                  return true;
               }

               if (var20 < -0.1 && var21 > 0.1 && var9 && var7) {
                  return true;
               }

               if (var20 < -0.1 && var21 < -0.1 && var9 && var6) {
                  return true;
               }
            }

            return false;
         }
      } else {
         return false;
      }
   }

   public boolean p(Material var1) {
      return var1 == Material.OBSIDIAN
         || var1 == Material.BEDROCK
         || var1 == Material.CRYING_OBSIDIAN
         || var1 == Material.ANCIENT_DEBRIS
         || var1 == Material.RESPAWN_ANCHOR
         || var1 == Material.NETHERITE_BLOCK
         || var1 == Material.REINFORCED_DEEPSLATE
         || var1 == Material.END_STONE
         || var1 == Material.ENDER_CHEST;
   }

   public double e(Location var1, Player var2) {
      Location var3 = var2.getLocation();
      if (var3 != null && var1 != null) {
         if (this.f(var2, var1)) {
            return 0.0;
         } else {
            int var4 = 0;
            int var5 = 0;
            double[][] var6 = new double[][]{
               {0.0, 0.1, 0.0},
               {-0.3, 0.1, 0.0},
               {0.3, 0.1, 0.0},
               {0.0, 0.1, -0.3},
               {0.0, 0.1, 0.3},
               {-0.3, 0.1, -0.3},
               {0.3, 0.1, 0.3},
               {-0.3, 0.1, 0.3},
               {0.3, 0.1, -0.3},
               {0.0, 0.9, 0.0},
               {-0.3, 0.9, 0.0},
               {0.3, 0.9, 0.0},
               {0.0, 0.9, -0.3},
               {0.0, 0.9, 0.3},
               {-0.3, 0.9, -0.3},
               {0.3, 0.9, 0.3},
               {-0.3, 0.9, 0.3},
               {0.3, 0.9, -0.3},
               {0.0, 1.7, 0.0},
               {-0.3, 1.5, 0.0},
               {0.3, 1.5, 0.0},
               {0.0, 1.5, -0.3},
               {0.0, 1.5, 0.3},
               {-0.3, 1.5, -0.3},
               {0.3, 1.5, 0.3},
               {-0.3, 1.5, 0.3},
               {0.3, 1.5, -0.3}
            };

            for (double[] var10 : var6) {
               var4++;
               Location var11 = var3.clone().add(var10[0], var10[1], var10[2]);
               if (this.ka.h(var1, var11)) {
                  var5++;
               }
            }

            return var4 > 0 ? (double)var5 / var4 : 0.0;
         }
      } else {
         return 0.0;
      }
   }
}
