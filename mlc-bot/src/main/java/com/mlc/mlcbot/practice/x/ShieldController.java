// Extracted from the user-provided PracticeBot-Source; strategy logic retained.
package com.mlc.mlcbot.practice.x;

import com.mlc.mlcbot.practice.BotTrait;
import com.mlc.mlcbot.practice.PracticeBotPlugin;
import java.lang.reflect.Method;
import com.mlc.mlcbot.practice.bridge.NPC;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

/** 盾牌格挡决策、朝向判断及格挡状态控制。 原源码：x/y.java。 */
public class ShieldController {
   private final PracticeBotPlugin u0;
   private final PracticeCombatConfig u1;

   public ShieldController(PracticeBotPlugin var1) {
      this.u0 = var1;
      this.u1 = var1.getConfigManager();
   }

   public void a(NPC var1, Player var2, BotTrait var3, Player var4, long var5) {
      ItemStack var7 = var2.getInventory().getItemInOffHand();
      ItemStack var8 = var2.getInventory().getItemInMainHand();
      boolean var9 = var7 != null && var7.getType() == Material.SHIELD;
      boolean var10 = var8 != null && var8.getType() == Material.SHIELD;
      boolean var11 = var9 || var10;
      if (var11 && var3.isUseShield() && !var3.isShieldDisabled(var5)) {
         if (var5 < var3.canBlockAfterAttackTime) {
            if (var3.isBlocking || var3.wasBlocking) {
               var3.isBlocking = false;
               var3.wasBlocking = false;
               this.m(var2);
            }
         } else {
            if (var3.isPvpEnabled() && var4 != null && var2.getWorld().equals(var4.getWorld())) {
               this.g(var2, var4, var3, var5);
            } else {
               if (!var3.isBlocking) {
                  var3.isBlocking = true;
                  var3.blockStartTime = var5;
               }

               if (!var3.wasBlocking) {
                  this.b(var2, var3);
                  var3.wasBlocking = true;
               }
            }
         }
      } else {
         if (var3.isBlocking || var3.wasBlocking) {
            var3.isBlocking = false;
            var3.wasBlocking = false;
            this.m(var2);
         }
      }
   }

   private void g(Player var1, Player var2, BotTrait var3, long var4) {
      boolean var6 = this.d(var1, var2);
      double var7 = var1.getLocation().distance(var2.getLocation());
      boolean var9 = var7 < 4.0;
      boolean var10 = var2.isSprinting();
      boolean var11 = var4 - var3.lastHitByPlayerTime < 800L;
      if (var3.reactiveBlockTriggered && var4 < var3.reactiveBlockUntil) {
         if (!var3.isBlocking) {
            var3.isBlocking = true;
            var3.blockStartTime = var4;
         }

         if (!var3.wasBlocking) {
            this.b(var1, var3);
            var3.wasBlocking = true;
         }
      } else {
         var3.reactiveBlockTriggered = false;

         double var12 = switch (var3.getPvpAggression()) {
            case 0 -> 0.55;
            case 2 -> 0.2;
            default -> 0.35;
         };
         if (var6 && var9 && var10) {
            var12 += 0.25;
         }

         if (var11) {
            var12 += 0.15;
         }

         if (var3.isBlocking && var4 - var3.blockStartTime < 300L) {
            if (!var3.wasBlocking) {
               this.b(var1, var3);
               var3.wasBlocking = true;
            }
         } else if (var3.isBlocking && var4 - var3.blockStartTime > 600L) {
            var3.isBlocking = false;
            var3.wasBlocking = false;
            this.m(var1);
         } else {
            if (Math.random() < var12 && !var3.isBlocking && var6) {
               var3.isBlocking = true;
               var3.blockStartTime = var4;
               if (!var3.wasBlocking) {
                  this.b(var1, var3);
                  var3.wasBlocking = true;
               }
            } else if (var3.isBlocking && Math.random() > var12) {
               var3.isBlocking = false;
               var3.wasBlocking = false;
               this.m(var1);
            } else if (var3.isBlocking && !var3.wasBlocking) {
               this.b(var1, var3);
               var3.wasBlocking = true;
            }
         }
      }
   }

   public void m(Player var1) {
      try {
         var1.clearActiveItem();
      } catch (Exception var3) {
      }
   }

   public void b(Player var1, BotTrait var2) {
      if (this.u0.isShieldVisualSupported()) {
         Method var3 = this.u0.getStartUsingItemMethod();
         if (var3 != null) {
            try {
               if (var1.isHandRaised()) {
                  ItemStack var4 = var1.getActiveItem();
                  if (var4 != null && var4.getType() == Material.SHIELD) {
                     return;
                  }
               }
            } catch (Exception var9) {
            }

            ItemStack var10 = var1.getInventory().getItemInOffHand();
            ItemStack var5 = var1.getInventory().getItemInMainHand();
            EquipmentSlot var6 = null;
            if (var10 != null && var10.getType() == Material.SHIELD) {
               var6 = EquipmentSlot.OFF_HAND;
            } else if (var5 != null && var5.getType() == Material.SHIELD) {
               var6 = EquipmentSlot.HAND;
            }

            if (var6 != null) {
               try {
                  var3.invoke(var1, var6);
               } catch (Exception var8) {
               }
            }
         }
      }
   }

   public boolean d(Player var1, Player var2) {
      Vector var3 = var2.getLocation().getDirection();
      Vector var4 = var1.getLocation().toVector().subtract(var2.getLocation().toVector());
      var3.setY(0).normalize();
      var4.setY(0);
      if (var4.lengthSquared() < 0.01) {
         return true;
      } else {
         var4.normalize();
         return var3.dot(var4) > 0.6;
      }
   }

   public boolean a(Player var1, LivingEntity var2) {
      Vector var3 = var1.getLocation().getDirection();
      var3.setY(0);
      Vector var4 = var2.getLocation().toVector().subtract(var1.getLocation().toVector());
      var4.setY(0);
      if (var4.lengthSquared() < 0.001) {
         return true;
      } else {
         var3.normalize();
         var4.normalize();
         double var5 = var3.dot(var4);
         double var7 = Math.toDegrees(Math.acos(Math.max(-1.0, Math.min(1.0, var5))));
         return var7 <= this.u1.bj() / 2.0;
      }
   }
}
