// Extracted from the user-provided PracticeBot-Source; strategy logic retained.
package com.mlc.mlcbot.practice.x;

import com.mlc.mlcbot.practice.BotTrait;
import com.mlc.mlcbot.practice.PracticeBotPlugin;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import com.mlc.mlcbot.practice.bridge.CitizensAPI;
import com.mlc.mlcbot.practice.bridge.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

public final class bs {
   private final PracticeBotPlugin gU;
   private final Map<bu, Integer> gV = new HashMap<>();
   private final Map<bu, Boolean> gW = new HashMap<>();
   private int gX = Integer.MIN_VALUE;

   public bs(PracticeBotPlugin var1) {
      this.gU = var1;
   }

   public boolean a(Player var1, Material... var2) {
      PlayerInventory var3 = var1.getInventory();

      for (Material var7 : var2) {
         this.d(var1, var7);

         for (int var8 = 0; var8 < 9; var8++) {
            ItemStack var9 = var3.getItem(var8);
            if (var9 != null && var9.getType() == var7) {
               var3.setHeldItemSlot(var8);
               return true;
            }
         }

         int var11 = this.a(var3, var7);
         if (var11 >= 9) {
            int var12 = this.k(var7);
            ItemStack var10 = var3.getItem(var12);
            var3.setItem(var12, var3.getItem(var11));
            var3.setItem(var11, var10);
            var3.setHeldItemSlot(var12);
            return true;
         }
      }

      return false;
   }

   private int a(PlayerInventory var1, Material var2) {
      for (int var3 = 9; var3 < var1.getSize(); var3++) {
         ItemStack var4 = var1.getItem(var3);
         if (var4 != null && var4.getType() == var2 && var4.getAmount() > 0) {
            return var3;
         }
      }

      return -1;
   }

   private int k(Material var1) {
      return switch (var1) {
         case NETHERITE_SWORD, DIAMOND_SWORD, IRON_SWORD -> 0;
         case MACE -> 1;
         case OBSIDIAN -> 2;
         case END_CRYSTAL -> 3;
         case RESPAWN_ANCHOR -> 4;
         case GOLDEN_APPLE -> 5;
         case NETHERITE_PICKAXE, DIAMOND_PICKAXE, IRON_PICKAXE -> 6;
         case ENDER_PEARL -> 7;
         case GLOWSTONE -> 8;
         default -> 3;
      };
   }

   public boolean ai(Player var1) {
      PlayerInventory var2 = var1.getInventory();

      for (int var3 = 0; var3 < 9; var3++) {
         ItemStack var4 = var2.getItem(var3);
         if (this.a(var4)) {
            var2.setHeldItemSlot(var3);
            return true;
         }
      }

      ItemStack var6 = var2.getItem(0);
      if (var6 != null
         && !var6.getType().isAir()
         && var6.getType() != Material.NETHERITE_SWORD
         && var6.getType() != Material.DIAMOND_SWORD
         && var6.getType() != Material.IRON_SWORD
         && var6.getType() != Material.END_CRYSTAL) {
         for (int var7 = 9; var7 < var2.getSize(); var7++) {
            ItemStack var5 = var2.getItem(var7);
            if (var5 == null || var5.getType().isAir()) {
               var2.setItem(var7, var6);
               var2.setItem(0, this.dx());
               var2.setHeldItemSlot(0);
               return true;
            }
         }

         var2.setItem(0, this.dx());
         var2.setHeldItemSlot(0);
         return true;
      } else {
         var2.setItem(0, this.dx());
         var2.setHeldItemSlot(0);
         return true;
      }
   }

   public boolean b(Player var1, Material... var2) {
      return this.a(var1, var2) && this.c(var1, var2);
   }

   public boolean ap(Player var1) {
      return this.ai(var1) && this.aq(var1);
   }

   public boolean c(Player var1, Material... var2) {
      if (var1 == null) {
         return false;
      } else {
         ItemStack var3 = var1.getInventory().getItemInMainHand();
         if (var3 != null && !var3.getType().isAir()) {
            for (Material var7 : var2) {
               if (var3.getType() == var7) {
                  return true;
               }
            }

            return false;
         } else {
            return false;
         }
      }
   }

   public boolean aq(Player var1) {
      return var1 == null ? false : this.a(var1.getInventory().getItemInMainHand());
   }

   public boolean a(Player var1, Material var2) {
      if (var1 == null || var2 == null) {
         return false;
      } else if (this.l(var2)) {
         this.d(var1, var2);
         return true;
      } else {
         return this.b(var1, var2) > 0;
      }
   }

   public int b(Player var1, Material var2) {
      if (var1 != null && var2 != null) {
         if (this.l(var2)) {
            this.d(var1, var2);
            return this.m(var2);
         } else {
            this.dw();
            bu var3 = new bu(var1.getUniqueId(), var2);
            Integer var4 = this.gV.get(var3);
            if (var4 != null) {
               return var4;
            } else {
               int var5 = 0;

               for (ItemStack var9 : var1.getInventory().getContents()) {
                  if (var9 != null && var9.getType() == var2 && var9.getAmount() > 0) {
                     var5 += var9.getAmount();
                  }
               }

               if (this.gV.size() < 2048) {
                  this.gV.put(var3, var5);
               }

               return var5;
            }
         }
      } else {
         return 0;
      }
   }

   public void aj(Player var1) {
      this.a(var1, Material.RESPAWN_ANCHOR, 4, 64);
      this.a(var1, Material.GLOWSTONE, 8, 64);
   }

   public void c(Player var1, Material var2) {
      if (this.l(var2)) {
         this.d(var1, var2);
      } else {
         PlayerInventory var3 = var1.getInventory();

         for (int var4 = 0; var4 < var3.getSize(); var4++) {
            ItemStack var5 = var3.getItem(var4);
            if (var5 != null && var5.getType() == var2) {
               if (var5.getAmount() > 1) {
                  var5.setAmount(var5.getAmount() - 1);
               } else {
                  var3.setItem(var4, null);
               }

               this.e(var1, var2);
               return;
            }
         }
      }
   }

   public void d(Player var1, Material var2) {
      if (var1 != null && this.l(var2)) {
         this.dw();
         bu var3 = new bu(var1.getUniqueId(), var2);
         if (!Boolean.TRUE.equals(this.gW.get(var3))) {
            this.a(var1, var2, this.k(var2), this.m(var2));
            if (this.gW.size() < 2048) {
               this.gW.put(var3, true);
            }

            if (this.gV.size() < 2048) {
               this.gV.put(var3, this.m(var2));
            }
         }
      }
   }

   private boolean l(Material var1) {
      return var1 == Material.END_CRYSTAL
         || var1 == Material.OBSIDIAN
         || var1 == Material.RESPAWN_ANCHOR
         || var1 == Material.GLOWSTONE
         || var1 == Material.ENDER_PEARL
         || var1 == Material.GOLDEN_APPLE;
   }

   private int m(Material var1) {
      return var1 == Material.ENDER_PEARL ? 16 : 64;
   }

   public boolean ak(Player var1) {
      return BotTrait.isLiveCombatTarget(var1);
   }

   public int al(Player var1) {
      NPC var2 = CitizensAPI.getNPCRegistry().getNPC(var1);
      return var2 != null ? var2.getId() : -1;
   }

   public void c(Player var1, BotTrait var2) {
      this.ar(var1);
      PlayerInventory var3 = var1.getInventory();
      if (!var2.hasStoredInventory()) {
         var2.setStoredInventory(this.a(var3.getContents()));
         var2.setStoredArmor(this.a(var3.getArmorContents()));
         ItemStack var4 = var3.getItemInOffHand();
         var2.setStoredOffhand(var4 != null && var4.getType() != Material.AIR ? var4.clone() : null);
      }

      var3.clear();
      boolean var11 = var2.getCpvpSettings().aQ();
      var3.setItem(0, this.dx());
      var3.setItem(1, this.dy());
      var3.setItem(2, new ItemStack(Material.OBSIDIAN, 64));
      var3.setItem(3, new ItemStack(Material.END_CRYSTAL, 64));
      var3.setItem(4, var11 ? new ItemStack(Material.RESPAWN_ANCHOR, 64) : new ItemStack(Material.END_CRYSTAL, 64));
      var3.setItem(5, new ItemStack(Material.GOLDEN_APPLE, 64));
      var3.setItem(6, this.dz());
      int var5 = Math.max(0, var2.getTotemCount());
      int var6 = var5 > 1 ? Math.min(64, var5 - 1) : 0;
      var3.setItem(7, new ItemStack(Material.ENDER_PEARL, 16));
      var3.setItem(8, var11 ? new ItemStack(Material.GLOWSTONE, 64) : new ItemStack(Material.OBSIDIAN, 64));

      for (int var7 = 9; var7 < 36; var7++) {
         if (var11 && var7 % 6 == 0) {
            var3.setItem(var7, new ItemStack(Material.RESPAWN_ANCHOR, 64));
         } else if (var11 && var7 % 6 == 1) {
            var3.setItem(var7, new ItemStack(Material.GLOWSTONE, 64));
         } else if (var7 % 4 == 0) {
            var3.setItem(var7, new ItemStack(Material.END_CRYSTAL, 64));
         } else if (var7 % 4 == 1) {
            var3.setItem(var7, new ItemStack(Material.OBSIDIAN, 64));
         } else if (var7 % 4 == 2) {
            var3.setItem(var7, new ItemStack(Material.GOLDEN_APPLE, 64));
         } else {
            var3.setItem(var7, new ItemStack(Material.ENDER_PEARL, 16));
         }
      }

      if (var6 > 0) {
         var3.setItem(35, new ItemStack(Material.TOTEM_OF_UNDYING, var6));
      }

      Material var12 = this.gU.getBotManager().H().g(var2.getHelmetMaterial(), "HELMET");
      var3.setHelmet(this.a(var12, var2.getHelmetEnchant(), var2));
      Material var8 = this.gU.getBotManager().H().g(var2.getChestplateMaterial(), "CHESTPLATE");
      var3.setChestplate(this.a(var8, var2.getChestplateEnchant(), var2));
      Material var9 = this.gU.getBotManager().H().g(var2.getLeggingsMaterial(), "LEGGINGS");
      var3.setLeggings(this.a(var9, "blast_protection", var2));
      Material var10 = this.gU.getBotManager().H().g(var2.getBootsMaterial(), "BOOTS");
      var3.setBoots(this.b(var10, var2.getBootsEnchant(), var2));
      var3.setItemInOffHand(var5 > 0 ? new ItemStack(Material.TOTEM_OF_UNDYING) : new ItemStack(Material.AIR));
      var3.setHeldItemSlot(3);
   }

   public void d(Player var1, BotTrait var2) {
      if (var2.hasStoredInventory()) {
         this.ar(var1);
         PlayerInventory var3 = var1.getInventory();
         var3.clear();
         ItemStack[] var4 = var2.getStoredInventory();
         if (var4 != null) {
            for (int var5 = 0; var5 < var4.length && var5 < var3.getSize(); var5++) {
               if (var4[var5] != null) {
                  var3.setItem(var5, var4[var5].clone());
               }
            }
         }

         ItemStack[] var7 = var2.getStoredArmor();
         if (var7 != null) {
            var3.setArmorContents(this.a(var7));
         }

         ItemStack var6 = var2.getStoredOffhand();
         if (var6 != null) {
            var3.setItemInOffHand(var6.clone());
         }

         var2.clearStoredInventory();
      }
   }

   private void dw() {
      int var1 = Bukkit.getCurrentTick();
      if (var1 != this.gX) {
         this.gX = var1;
         this.gV.clear();
         this.gW.clear();
      }
   }

   private void e(Player var1, Material var2) {
      if (var1 != null && var2 != null) {
         this.dw();
         this.gV.remove(new bu(var1.getUniqueId(), var2));
         this.gW.remove(new bu(var1.getUniqueId(), var2));
      }
   }

   private void ar(Player var1) {
      if (var1 != null) {
         this.dw();
         UUID var2 = var1.getUniqueId();
         this.gV.keySet().removeIf(var1x -> var1x.dA().equals(var2));
         this.gW.keySet().removeIf(var1x -> var1x.dA().equals(var2));
      }
   }

   private boolean a(ItemStack var1) {
      if (var1 == null) {
         return false;
      } else {
         Material var2 = var1.getType();
         return var2 != Material.NETHERITE_SWORD
            ? false
            : var1.getEnchantmentLevel(Enchantment.SHARPNESS) >= 5 && var1.getEnchantmentLevel(Enchantment.KNOCKBACK) >= 1;
      }
   }

   private void a(Player var1, Material var2, int var3, int var4) {
      PlayerInventory var5 = var1.getInventory();

      for (int var6 = 0; var6 < var5.getSize(); var6++) {
         ItemStack var7 = var5.getItem(var6);
         if (var7 != null && var7.getType() == var2) {
            if (var7.getAmount() < var4) {
               var7.setAmount(var4);
            }

            return;
         }
      }

      ItemStack var9 = var5.getItem(var3);
      if (var9 == null || var9.getType().isAir() || this.l(var9.getType())) {
         var5.setItem(var3, new ItemStack(var2, var4));
      } else if (this.l(var2)) {
         var5.setItem(var3, new ItemStack(var2, var4));
      } else {
         for (int var10 = 9; var10 < var5.getSize(); var10++) {
            ItemStack var8 = var5.getItem(var10);
            if (var8 == null || var8.getType().isAir()) {
               var5.setItem(var10, new ItemStack(var2, var4));
               return;
            }
         }
      }
   }

   private ItemStack a(Material var1, String var2, BotTrait var3) {
      ItemStack var4 = new ItemStack(var1);
      ItemMeta var5 = var4.getItemMeta();
      if (var5 instanceof ArmorMeta var6) {
         var6.setUnbreakable(true);
         var6.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ARMOR_TRIM});
         Enchantment var7 = this.bh(var2);
         if (var7 != null) {
            var6.addEnchant(var7, 4, true);
         }

         String var8 = this.n(var1);
         this.a(var6, var3, var8);
         var4.setItemMeta(var6);
      } else if (var5 != null) {
         var5.setUnbreakable(true);
         var5.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ENCHANTS});
         Enchantment var9 = this.bh(var2);
         if (var9 != null) {
            var5.addEnchant(var9, 4, true);
         }

         var4.setItemMeta(var5);
      }

      return var4;
   }

   private ItemStack b(Material var1, String var2, BotTrait var3) {
      ItemStack var4 = new ItemStack(var1);
      ItemMeta var5 = var4.getItemMeta();
      if (var5 instanceof ArmorMeta var6) {
         var6.setUnbreakable(true);
         var6.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ARMOR_TRIM});
         Enchantment var7 = this.bh(var2);
         if (var7 != null) {
            var6.addEnchant(var7, 4, true);
         }

         var6.addEnchant(Enchantment.FEATHER_FALLING, 4, true);
         this.a(var6, var3, "boots");
         var4.setItemMeta(var6);
      } else if (var5 != null) {
         var5.setUnbreakable(true);
         var5.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ENCHANTS});
         Enchantment var8 = this.bh(var2);
         if (var8 != null) {
            var5.addEnchant(var8, 4, true);
         }

         var5.addEnchant(Enchantment.FEATHER_FALLING, 4, true);
         var4.setItemMeta(var5);
      }

      return var4;
   }

   private Enchantment bh(String var1) {
      if (var1 == null) {
         return Enchantment.PROTECTION;
      } else {
         return switch (var1) {
            case "none" -> null;
            case "blast_protection" -> Enchantment.BLAST_PROTECTION;
            case "fire_protection" -> Enchantment.FIRE_PROTECTION;
            case "projectile_protection" -> Enchantment.PROJECTILE_PROTECTION;
            default -> Enchantment.PROTECTION;
         };
      }
   }

   private ItemStack dx() {
      ItemStack var1 = new ItemStack(Material.NETHERITE_SWORD);
      ItemMeta var2 = var1.getItemMeta();
      if (var2 != null) {
         var2.setDisplayName("CPvP Sword");
         var2.setUnbreakable(true);
         var2.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ENCHANTS});
         var2.addEnchant(Enchantment.KNOCKBACK, 1, true);
         var2.addEnchant(Enchantment.SHARPNESS, 5, true);
         var1.setItemMeta(var2);
      }

      return var1;
   }

   private ItemStack dy() {
      ItemStack var1 = new ItemStack(Material.MACE);
      ItemMeta var2 = var1.getItemMeta();
      if (var2 != null) {
         var2.setDisplayName("Breach Mace");
         var2.setUnbreakable(true);
         var2.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ENCHANTS});
         var2.addEnchant(Enchantment.BREACH, 4, true);
         var2.addEnchant(Enchantment.DENSITY, 5, true);
         var1.setItemMeta(var2);
      }

      return var1;
   }

   private ItemStack dz() {
      ItemStack var1 = new ItemStack(Material.NETHERITE_PICKAXE);
      ItemMeta var2 = var1.getItemMeta();
      if (var2 != null) {
         var2.setDisplayName("Breach Pickaxe");
         var2.setUnbreakable(true);
         var2.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ENCHANTS});
         var2.addEnchant(Enchantment.EFFICIENCY, 5, true);
         var1.setItemMeta(var2);
      }

      return var1;
   }

   private String n(Material var1) {
      String var2 = var1.name().toLowerCase();
      if (var2.contains("helmet")) {
         return "helmet";
      } else if (var2.contains("chestplate")) {
         return "chest";
      } else if (var2.contains("leggings")) {
         return "legs";
      } else {
         return var2.contains("boots") ? "boots" : "";
      }
   }

   private void a(ArmorMeta var1, BotTrait var2, String var3) {
      if (var1 != null && var3 != null && !var3.isEmpty()) {
         FileConfiguration var4 = this.gU.getDefaultInvConfig();
         if (var4 != null) {
            String var5 = this.a(var2, var4, var3);
            String var6 = this.b(var2, var4, var3);
            if (var5 != null && !var5.isEmpty() && var6 != null && !var6.isEmpty()) {
               try {
                  TrimPattern var7 = (TrimPattern)Registry.TRIM_PATTERN.get(NamespacedKey.minecraft(var5.toLowerCase()));
                  TrimMaterial var8 = (TrimMaterial)Registry.TRIM_MATERIAL.get(NamespacedKey.minecraft(var6.toLowerCase()));
                  if (var7 != null && var8 != null) {
                     ArmorTrim var9 = new ArmorTrim(var8, var7);
                     var1.setTrim(var9);
                  }
               } catch (Exception var10) {
               }
            }
         }
      }
   }

   private String a(BotTrait var1, FileConfiguration var2, String var3) {
      if (var1 != null) {
         return switch (var3) {
            case "helmet" -> this.j(var1.getHelmetTrimPattern(), var2.getString("helmet-trim-pattern", ""));
            case "chest" -> this.j(var1.getChestTrimPattern(), var2.getString("chest-trim-pattern", ""));
            case "legs" -> this.j(var1.getLegsTrimPattern(), var2.getString("legs-trim-pattern", ""));
            case "boots" -> this.j(var1.getBootsTrimPattern(), var2.getString("boots-trim-pattern", ""));
            default -> var2.getString(var3 + "-trim-pattern", "");
         };
      } else {
         return var2.getString(var3 + "-trim-pattern", "");
      }
   }

   private String b(BotTrait var1, FileConfiguration var2, String var3) {
      if (var1 != null) {
         return switch (var3) {
            case "helmet" -> this.j(var1.getHelmetTrimMaterial(), var2.getString("helmet-trim-material", ""));
            case "chest" -> this.j(var1.getChestTrimMaterial(), var2.getString("chest-trim-material", ""));
            case "legs" -> this.j(var1.getLegsTrimMaterial(), var2.getString("legs-trim-material", ""));
            case "boots" -> this.j(var1.getBootsTrimMaterial(), var2.getString("boots-trim-material", ""));
            default -> var2.getString(var3 + "-trim-material", "");
         };
      } else {
         return var2.getString(var3 + "-trim-material", "");
      }
   }

   private String j(String var1, String var2) {
      return var1 != null && !var1.isBlank() ? var1 : var2;
   }

   private ItemStack[] a(ItemStack[] var1) {
      if (var1 == null) {
         return null;
      } else {
         ItemStack[] var2 = new ItemStack[var1.length];

         for (int var3 = 0; var3 < var1.length; var3++) {
            if (var1[var3] != null) {
               var2[var3] = var1[var3].clone();
            }
         }

         return var2;
      }
   }
}
