// Extracted from the user-provided PracticeBot-Source; strategy logic retained.
package com.mlc.mlcbot.practice.x;

import java.util.Random;
import com.mlc.mlcbot.practice.bridge.NPC;
import com.mlc.mlcbot.practice.bridge.FollowTrait;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public final class e0 {
   private static final double ih = 3.0;
   private static final double ii = 4.0;
   private static final double ij = 4.0;
   private static final double ik = 2.25;
   private static final double il = 0.9;
   private static final double im = 0.22;
   private static final double in = 1.35;
   private static final double io = 1.8;
   private static final long ip = 400L;
   private final av iq;
   private final Random ir;

   public e0(av var1) {
      this.iq = var1;
      this.ir = var1.cb();
   }

   private boolean a(Player var1, Player var2, m1 var3, long var4, float var6, boolean var7) {
      if (var1 != null && var2 != null && var3 != null) {
         long var8 = this.iq.b(var1, var2, var3, var7);
         Location var10 = var2.getLocation();
         boolean var11 = var3.nA != null && var3.nA.equals(var2.getUniqueId());
         boolean var12 = var3.nB == null
            || var3.nB.getWorld() == null
            || !var3.nB.getWorld().equals(var10.getWorld())
            || this.iq.d(var3.nB, var10) > 0.85
            || Math.abs(var3.nB.getY() - var10.getY()) > 0.75;
         boolean var13 = Math.abs(var3.nE - var6) > 0.05F;
         return !var11 || var12 || var13 || var4 - var3.nC >= var8;
      } else {
         return true;
      }
   }

   private void a(Player var1, m1 var2, long var3, float var5) {
      if (var1 != null && var2 != null) {
         var2.nA = var1.getUniqueId();
         var2.nB = var1.getLocation().clone();
         var2.nC = var3;
         var2.nE = var5;
         var2.lastPathUpdate = var3;
      }
   }

   private void v(m1 var1) {
      if (var1 != null) {
         var1.nA = null;
         var1.nB = null;
      }
   }

   private void a(NPC var1, m1 var2, long var3) {
      if (var1 != null && var2 != null) {
         FollowTrait var5 = (FollowTrait)var1.getTraitNullable(FollowTrait.class);
         boolean var6 = var5 != null && var5.getFollowing() != null;
         boolean var7 = var1.getNavigator().isNavigating();
         if (!var6 && !var7) {
            this.v(var2);
         } else {
            if (var6) {
               var5.follow(null);
            }

            if (var7) {
               var1.getNavigator().cancelNavigation();
            }

            this.v(var2);
            var2.nD = var3;
         }
      }
   }

   private void a(Player var1, Vector var2, double var3, double var5, float var7) {
      if (var1 != null && var2 != null && !(var2.lengthSquared() < 1.0E-4)) {
         double var8 = Math.max(0.75, Math.min(1.35, (double)var7));
         Vector var10 = var1.getVelocity();
         var10.setX(var10.getX() * var3 + var2.getX() * var5 * var8);
         var10.setZ(var10.getZ() * var3 + var2.getZ() * var5 * var8);
         var1.setVelocity(var10);
      }
   }

   private boolean a(Player var1, Player var2, m1 var3, double var4, double var6, boolean var8) {
      if (var8) {
         return false;
      } else {
         return !this.iq.a(var1, var2, var3, var4, false)
            ? false
            : Math.abs(var6) <= 1.4 || var4 <= 5.0 && var6 <= 2.2 || this.iq.h(var1.getEyeLocation(), var2.getEyeLocation());
      }
   }

   private boolean p(m1 var1, long var2) {
      return var1 != null
         && (var1.lt || var1.lB || var1.mq || var1.mG || var1.mH || var1.mg || var1.lu || var1.ly || var1.os || var1.og || this.iq.k(var1, var2));
   }

   private void a(Player var1, Player var2, m1 var3, long var4, float var6, float var7) {
      if (var1 != null && var2 != null && var3 != null) {
         if (!this.p(var3, var4)) {
            if (var1.getWorld().equals(var2.getWorld())) {
               Location var8 = var1.getEyeLocation();
               Location var9 = var2.getLocation();
               double var10 = var9.getX() - var8.getX();
               double var12 = var9.getZ() - var8.getZ();
               if (!(var10 * var10 + var12 * var12 < 1.0E-4)) {
                  float var14 = var1.getLocation().getYaw();
                  float var15 = var1.getLocation().getPitch();
                  float var16 = (float)Math.toDegrees(Math.atan2(-var10, var12));
                  float var17 = var16 - var14;

                  while (var17 > 180.0F) {
                     var17 -= 360.0F;
                  }

                  while (var17 < -180.0F) {
                     var17 += 360.0F;
                  }

                  float var18 = var14 + var17 * var6;
                  float var19 = var15 * var7;
                  if (Math.abs(var19) < 0.35F) {
                     var19 = 0.0F;
                  }

                  var1.setRotation(var18, var19);
               }
            }
         }
      }
   }

   private void a(NPC var1, Player var2, m1 var3, long var4, Vector var6, Location var7) {
      if (var1 != null && var2 != null && var3 != null) {
         if (var7 != null) {
            this.iq.a(var1, var2, var7, var3);
         }

         FollowTrait var8 = (FollowTrait)var1.getTraitNullable(FollowTrait.class);
         if (var8 != null && var8.getFollowing() != null) {
            var8.follow(null);
         }

         var1.getNavigator().cancelNavigation();
         this.v(var3);
         Vector var9 = this.iq.a(var2, var6);
         var2.setSprinting(true);
         var2.setVelocity(var9);
         var3.kX = var4;
         var3.oI = true;
         var3.oJ = var6 == null ? null : var6.clone().setY(0.0);
         var3.oK = var7 == null ? null : var7.clone();
         var3.oL = var4;
         var3.oM = 0;
         var3.oN = var2.getLocation().getY();
         var3.kJ = var4;
         var3.oq = var4;
         var3.oQ = null;
         var3.oR = 0L;
      }
   }

   public void s(NPC var1, Player var2, Player var3, ae var4, m1 var5, long var6) {
      bx var8 = this.iq.k(var2, var3, var4, var5, var6);
      if (var8.dO()) {
         if (var8.dP()) {
            this.t(var1, var2, var3, var4, var5, var6);
         }
      } else if (var5.inKnockback || var5.kZ) {
         this.iq.a(var1, var2, var3, var4);
      } else if (!var5.mk) {
         if (this.iq.ac(var2)) {
            this.u(var1, var2, var3, var4, var5, var6);
         } else {
            Location var9 = var2.getLocation();
            Location var10 = var3.getLocation();
            World var11 = var9.getWorld();
            if (var11 != null) {
               Location var12 = this.iq.c(var2, var3, var5);
               Vector var13 = this.iq.a(var9, var12 != null ? var12 : var10, this.iq.d(var2, var3, var5));
               boolean var14 = this.iq.ag(var2);
               double var15 = var10.getY() - var9.getY();
               double var17 = this.iq.d(var9, var10);
               boolean var19 = var12 == null && this.iq.o(var2, var3);
               boolean var20 = var15 > 1.35;
               Block var21 = var12 == null ? this.iq.a(var2, var3, var13, var11) : null;
               boolean var22 = this.iq.a(var21, var5, var6);
               double var23 = this.iq.d(var9, var10);
               boolean var25 = var12 == null && var21 != null && var23 < 4.5;
               Block var26 = var12 == null ? this.iq.c(var2, var13, var11) : null;
               boolean var27 = var26 != null;
               if (!var14
                  || !this.d(var2, var13, var11)
                     && (var27 || !var20 || !this.iq.b(var2, var13, var11))
                     && (var27 || var25 || var19 || !this.e(var2, var13, var11))
                  || !this.iq.a(var1, var2, var3, var13, var5, var6)) {
                  if (var5.oI && !var14) {
                     var5.oM++;
                     this.iq.e(var2, var5);
                     if (var5.oM > 20) {
                        this.iq.l(var5, var6);
                     }
                  } else {
                     if (var5.oI && var14) {
                        this.iq.c(var2, var5, var6);
                     }

                     if (!var14) {
                        Vector var43 = var2.getVelocity();
                        if (var43.getY() > -0.5) {
                           var43.setX(var43.getX() * 0.95 + var13.getX() * 0.05);
                           var43.setZ(var43.getZ() * 0.95 + var13.getZ() * 0.05);
                           var2.setVelocity(var43);
                        }
                     } else if (var12 == null || !this.iq.a(var1, var2, var3, var5, var12, 0.19)) {
                        if (var5.op == null) {
                           var5.op = var9.clone();
                           var5.oq = var6;
                        }

                        double var28 = this.iq.d(var5.op, var9);
                        if (var28 > 0.28) {
                           var5.op = var9.clone();
                           var5.oq = var6;
                           var5.oo = 0;
                           var5.oQ = null;
                           var5.oR = 0L;
                        }

                        long var30 = var6 - var5.oq;
                        if (var30 <= 900L) {
                           var5.oo = 0;
                        }

                        boolean var32 = var6 - var5.kX > 400L;
                        Vector var33 = var25 ? this.iq.a(var2, var21, var13) : null;
                        boolean var34 = var25 && !var22 && this.iq.b(var2, var21, var33);
                        if (var32 && var34 && !var5.lt && !var5.mq && !var5.lB && !this.d(var2, var13, var11)) {
                           Location var45 = this.iq.l(var21);
                           this.a(var1, var2, var5, var6, var33, var45);
                        } else {
                           if (var25 && !var5.lt && !var5.mq && !var5.lB) {
                              double var35 = this.iq.d(var2.getLocation(), var21.getLocation().add(0.5, 0.0, 0.5));
                              if (var35 < 2.5 && this.iq.a(var1, var2, var21, var13, var5, var6)) {
                                 return;
                              }
                           }

                           var2.setSprinting(true);
                           boolean var44 = !var3.isOnGround() && var15 > 1.5 && var17 < 2.5 || var3.isOnGround() && var15 > 2.5 && var17 < 2.0;
                           if (!var44
                              && var14
                              && var32
                              && !var5.oI
                              && !var5.lt
                              && !var5.mq
                              && !var5.lB
                              && !var25
                              && var26 != null
                              && !this.d(var2, var13, var11)) {
                              Vector var46 = this.iq.a(var2, var26, var13);
                              Location var47 = this.iq.l(var26);
                              this.a(var1, var2, var5, var6, var46, var47);
                           } else {
                              float var36 = 1.0F;
                              if (var6 < var5.lf) {
                                 var36 = 1.2F;
                              }

                              boolean var37 = var12 == null && this.a(var2, var3, var5, var17, var15, var25);
                              boolean var38 = var37 || var12 == null && !var25 && var17 <= 8.5 && this.iq.h(var2.getEyeLocation(), var3.getEyeLocation());
                              if (var12 == null && var15 > 2.5 && var17 < 2.0) {
                                 var1.getNavigator().cancelNavigation();
                                 this.v(var5);
                                 FollowTrait var50 = (FollowTrait)var1.getTraitNullable(FollowTrait.class);
                                 if (var50 != null && var50.getFollowing() != null) {
                                    var50.follow(null);
                                 }

                                 Vector var52 = var2.getVelocity();
                                 var52.setX(var52.getX() * 0.6 + var13.getX() * 0.2);
                                 var52.setZ(var52.getZ() * 0.6 + var13.getZ() * 0.2);
                                 var2.setVelocity(var52);
                                 this.a(var2, var3, var5, var6, 0.5F, 0.55F);
                              } else if (this.iq.m(var2, var3, var4, var5, var6)) {
                                 var1.getNavigator().cancelNavigation();
                                 this.v(var5);
                                 FollowTrait var39 = (FollowTrait)var1.getTraitNullable(FollowTrait.class);
                                 if (var39 != null && var39.getFollowing() != null) {
                                    var39.follow(null);
                                 }

                                 Vector var40 = var2.getVelocity();
                                 var40.setX(var40.getX() * 0.7 + var13.getX() * 0.15);
                                 var40.setZ(var40.getZ() * 0.7 + var13.getZ() * 0.15);
                                 var2.setVelocity(var40);
                              } else if (var38) {
                                 this.a(var1, var5, var6);
                                 this.iq.w(var1);
                                 this.iq.a(var1, var2, var3, var4);
                                 double var48 = var37 && var30 <= 450L ? 0.2 : (var30 > 450L ? 0.22 : 0.18);
                                 this.a(var2, var13, 0.62, var48, var36);
                                 var5.lastPathUpdate = var6;
                              } else {
                                 FollowTrait var49 = (FollowTrait)var1.getOrAddTrait(FollowTrait.class);
                                 boolean var51 = this.a(var2, var3, var5, var6, var36, false);
                                 if (var49.getFollowing() != var3 || var51) {
                                    if (this.iq.c(var2, "cpvp-follow-target", false)) {
                                       if (var49.getFollowing() != var3) {
                                          var49.follow(var3);

                                          try {
                                             var49.getClass().getMethod("setFollowingMargin", double.class).invoke(var49, 0.5);
                                          } catch (Exception var42) {
                                          }
                                       }

                                       var1.getNavigator().getDefaultParameters().speedModifier(var36);
                                       this.a(var3, var5, var6, var36);
                                    } else if (var49.getFollowing() != var3) {
                                       this.a(var2, var13, 0.72, 0.12, var36);
                                       var5.lastPathUpdate = var6;
                                    }
                                 }

                                 this.a(var2, var3, var5, var6, 0.35F, 0.7F);
                              }

                              if (var30 > 900L && !var25 && !var19) {
                                 var5.oo++;
                                 if (var5.oo <= 4 || var6 - var5.kJ <= 220L || !this.iq.a(var1, var2, var3, var13, var5, var6)) {
                                    ;
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public boolean e(Player var1, Vector var2, World var3) {
      if (var2 == null || var2.lengthSquared() < 0.01) {
         return false;
      } else if (!this.iq.ag(var1)) {
         return false;
      } else {
         Location var4 = var1.getLocation();
         Vector var5 = var2.clone().setY(0);
         if (var5.lengthSquared() < 0.01) {
            return false;
         } else {
            var5.normalize();
            Vector var6 = var1.getVelocity();
            double var7 = Math.sqrt(var6.getX() * var6.getX() + var6.getZ() * var6.getZ());
            if (var7 > 0.1) {
               return false;
            } else {
               Block var9 = this.iq.a(var1, var5, var3, 0.35);
               if (var9 == null) {
                  return false;
               } else {
                  return this.iq.h(var9.getType()) ? false : this.iq.f(var1, var9);
               }
            }
         }
      }
   }

   public boolean d(Player var1, Vector var2, World var3) {
      if (var2 != null && !(var2.lengthSquared() < 0.01)) {
         Location var4 = var1.getLocation();
         Vector var5 = var2.clone().setY(0).normalize();
         Location var6 = var4.clone().add(var5.multiply(0.5));
         int var7 = var6.getBlockX();
         int var8 = var6.getBlockZ();
         int var9 = var4.getBlockY();
         Block var10 = var3.getBlockAt(var7, var9, var8);
         Block var11 = var3.getBlockAt(var7, var9 + 1, var8);
         return var10.getType().isSolid() && var11.getType().isSolid();
      } else {
         return false;
      }
   }

   public boolean t(NPC var1, Player var2, Player var3, ae var4, m1 var5, long var6) {
      bx var8 = this.iq.k(var2, var3, var4, var5, var6);
      if (!var8.dP()) {
         return false;
      } else {
         Block var9 = var8.dX();
         if (!bx.q(var9)) {
            return false;
         } else {
            this.iq.j(var5);
            this.iq.u(var2);
            this.iq.a(var5, var3, var6 + 320L);
            Location var10 = var8.dS();
            if (var10 == null) {
               var10 = var9.getLocation().add(0.5, 1.0, 0.5);
            }

            this.iq.c(var1, var2, var10, var5);
            if (this.iq.g(var4)
               && var5.ln < 3
               && !var5.lk
               && var2.getHealth() / var2.getAttribute(Attribute.MAX_HEALTH).getValue() < this.iq.l(var4)
               && var2.getLocation().distance(var3.getLocation()) > 4.0
               && this.iq.d(var2, var5, var6)) {
               return true;
            } else if (this.iq.h(var4)
               && this.iq.d(var5, var4, var6)
               && this.iq.a(var2, Material.OBSIDIAN)
               && this.iq.a(var2, Material.END_CRYSTAL)
               && !var5.lz
               && this.iq.e(var2, var3, var4, var5, var6) != null
               && this.iq.k(var1, var2, var3, var4, var5, var6)) {
               return true;
            } else {
               var1.getNavigator().cancelNavigation();
               FollowTrait var11 = (FollowTrait)var1.getTraitNullable(FollowTrait.class);
               if (var11 != null && var11.getFollowing() != null) {
                  var11.follow(null);
               }

               Location var12 = var2.getLocation();
               Vector var13 = var12.toVector().subtract(var10.toVector()).setY(0.0);
               if (var13.lengthSquared() < 0.01) {
                  var13 = var12.toVector().subtract(var3.getLocation().toVector()).setY(0.0);
               }

               if (var13.lengthSquared() < 0.01) {
                  var13 = this.iq.d(var2, var3, var5).multiply(-1.0);
               } else {
                  var13.normalize();
               }

               Vector var14 = var13.clone().multiply(this.iq.i(var4) ? 0.1 : 0.16);
               if (this.iq.i(var4)) {
                  Vector var15 = new Vector(-var13.getZ(), 0.0, var13.getX());
                  if (!var5.li) {
                     var15.multiply(-1.0);
                  }

                  var14.add(var15.multiply(0.18));
               }

               if (var14.lengthSquared() > 0.01) {
                  var14.normalize().multiply(0.22);
                  Vector var16 = var2.getVelocity();
                  var16.setX(var16.getX() * 0.35 + var14.getX());
                  var16.setZ(var16.getZ() * 0.35 + var14.getZ());
                  var2.setSprinting(true);
                  var2.setVelocity(var16);
               }

               var5.kJ = var6;
               return true;
            }
         }
      }
   }

   public void a(NPC var1, Player var2, Player var3, ae var4, m1 var5, long var6, boolean var8) {
      bx var9 = this.iq.k(var2, var3, var4, var5, var6);
      if (var9.dO()) {
         if (var9.dP()) {
            this.t(var1, var2, var3, var4, var5, var6);
         }
      } else if (var5.inKnockback || var5.kZ) {
         this.iq.a(var1, var2, var3, var4);
      } else if (!var5.mk) {
         if (this.iq.ac(var2)) {
            this.u(var1, var2, var3, var4, var5, var6);
         } else {
            Location var10 = var2.getLocation();
            Location var11 = var3.getLocation();
            World var12 = var10.getWorld();
            if (var12 != null) {
               boolean var13 = this.iq.ag(var2);
               Location var14 = this.iq.c(var2, var3, var5);
               Vector var15 = (var14 != null ? var14.toVector() : var11.toVector()).subtract(var10.toVector());
               var15.setY(0);
               Vector var16 = var15.lengthSquared() < 0.01
                  ? this.iq.a(var10, var14 != null ? var14 : var11, this.iq.d(var2, var3, var5))
                  : var15.clone().normalize();
               double var17 = var10.distance(var11);
               boolean var19 = this.a(var4, var5, var6);
               double var20 = var19 ? this.p(var2, var3, var5, var6) : 1.8;
               double var22 = var11.getY() - var10.getY();
               double var24 = this.iq.d(var10, var11);
               boolean var26 = var14 == null && this.iq.o(var2, var3);
               boolean var27 = var22 > 1.35;
               Block var28 = var14 == null ? this.iq.a(var2, var3, var16, var12) : null;
               boolean var29 = this.iq.a(var28, var5, var6);
               double var30 = this.iq.d(var10, var11);
               boolean var32 = var14 == null && var28 != null && var30 < 4.5;
               Block var33 = var14 == null ? this.iq.c(var2, var16, var12) : null;
               boolean var34 = var33 != null;
               if (var5.oI && !var13) {
                  var5.oM++;
                  this.iq.e(var2, var5);
                  if (var5.oM > 20) {
                     this.iq.l(var5, var6);
                  }
               } else {
                  if (var5.oI && var13) {
                     this.iq.c(var2, var5, var6);
                  }

                  if (!var13) {
                     Vector var46 = var2.getVelocity();
                     if (var46.getY() > -0.4) {
                        var46.setX(var46.getX() * 0.92 + var16.getX() * 0.04);
                        var46.setZ(var46.getZ() * 0.92 + var16.getZ() * 0.04);
                        var2.setVelocity(var46);
                     }
                  } else if (var14 == null || !this.iq.a(var1, var2, var3, var5, var14, 0.17)) {
                     if (this.a(var2, var3, var5, var6, var17, var16)) {
                        Vector var35 = this.q(var2, var3, var5, var6);
                        if (var35 != null && var35.lengthSquared() > 0.01) {
                           var1.getNavigator().cancelNavigation();
                           this.v(var5);
                           if (var6 - var5.oV >= 85L) {
                              Vector var36 = var2.getVelocity();
                              var36.setX(var36.getX() * 0.45 + var35.getX() * 0.18);
                              var36.setZ(var36.getZ() * 0.45 + var35.getZ() * 0.18);
                              var2.setVelocity(var36);
                              var5.kJ = var6;
                              var5.oV = var6;
                           }

                           return;
                        }
                     } else if (var5.oU > 0L && var6 >= var5.oU) {
                        var5.oT = null;
                        var5.oU = 0L;
                     }

                     var2.setSprinting(true);
                     boolean var45 = var6 - var5.kX > 400L && this.iq.ag(var2);
                     Vector var47 = var32 ? this.iq.a(var2, var28, var16) : null;
                     boolean var37 = var32 && !var29 && this.iq.b(var2, var28, var47);
                     if (var34 || !var27 || !(var24 < 2.2) || !this.iq.a(var1, var2, var3, var16, var5, var6)) {
                        if (!this.iq.c(var2, var16)
                              && (var34 || !var27 || !this.iq.b(var2, var16, var12))
                              && (var34 || var32 || var26 || !this.iq.b(var2, var16))
                           || !this.iq.a(var1, var2, var3, var16, var5, var6)) {
                           if (var45 && var37 && !var5.lt && !var5.lB && !var5.mq && !this.iq.c(var2, var16)) {
                              Location var48 = this.iq.l(var28);
                              this.a(var1, var2, var5, var6, var47, var48);
                           } else if (!var32 || !this.iq.a(var1, var2, var28, var16, var5, var6)) {
                              boolean var38 = !var3.isOnGround() && var22 > 1.5 || var3.isOnGround() && var22 > 2.5 && var24 < 2.0;
                              if (!var38
                                 && !var32
                                 && var13
                                 && var45
                                 && !var5.oI
                                 && !var5.lt
                                 && !var5.mq
                                 && !var5.lB
                                 && var33 != null
                                 && !this.iq.c(var2, var16)) {
                                 Vector var49 = this.iq.a(var2, var33, var16);
                                 Location var54 = this.iq.l(var33);
                                 this.a(var1, var2, var5, var6, var49, var54);
                              } else {
                                 if (var6 - var5.kJ > 60L) {
                                    Vector var39 = new Vector(0, 0, 0);
                                    if (var17 > var20 + 0.3) {
                                       double var40 = var19 ? 0.26 : 0.32;
                                       var39.add(var16.clone().multiply(var40));
                                    } else if (var17 < var20 - 0.5) {
                                       double var50 = var19 ? -0.13 : -0.1;
                                       var39.add(var16.clone().multiply(var50));
                                    }

                                    if (this.iq.i(var4)) {
                                       Vector var51 = new Vector(-var16.getZ(), 0.0, var16.getX());
                                       if (this.ir.nextInt(100) < 1) {
                                          var5.li = !var5.li;
                                       }

                                       if (!var5.li) {
                                          var51.multiply(-1);
                                       }

                                       Location var41 = var10.clone().add(var51.clone().multiply(1.0));
                                       Block var42 = var41.clone().add(0.0, -0.1, 0.0).getBlock();
                                       if (!this.at(var2) && (var42.getType() == Material.OBSIDIAN || var42.getType() == Material.BEDROCK)) {
                                          var51.multiply(-1);
                                          var5.li = !var5.li;
                                          Location var43 = var10.clone().add(var51.clone().multiply(1.0));
                                          Block var44 = var43.clone().add(0.0, -0.1, 0.0).getBlock();
                                          if (var44.getType() == Material.OBSIDIAN || var44.getType() == Material.BEDROCK) {
                                             var51 = new Vector(0, 0, 0);
                                             var39.add(var16.clone().multiply(0.2));
                                          }
                                       }

                                       double var55 = var17 > var20 + 0.5 ? 0.15 : 0.3;
                                       if (var19) {
                                          var55 *= this.w(var2, var3);
                                       }

                                       var39.add(var51.multiply(var55));
                                    }

                                    if (var39.lengthSquared() <= 0.01 && this.q(var5, var6) && !this.iq.g(var5, var6)) {
                                       if (this.iq.i(var4)) {
                                          Vector var52 = new Vector(-var16.getZ(), 0.0, var16.getX());
                                          if (!var5.li) {
                                             var52.multiply(-1);
                                          }

                                          var39.add(var52.multiply(0.18));
                                          if (var17 > var20 - 0.15) {
                                             var39.add(var16.clone().multiply(var19 ? 0.09 : 0.12));
                                          }
                                       } else if (var17 > var20 + 0.15) {
                                          var39.add(var16.clone().multiply(var19 ? 0.14 : 0.18));
                                       } else if (var17 < var20 - 0.35) {
                                          var39.add(var16.clone().multiply(var19 ? -0.1 : -0.12));
                                       }
                                    }

                                    if (var39.lengthSquared() > 0.01) {
                                       if (!this.iq.m(var2, var3, var4, var5, var6) && !this.iq.l(var2, var3)) {
                                          var39 = this.a(var2, var3, var39);
                                       }

                                       if (var39.lengthSquared() > 0.01) {
                                          var39.normalize().multiply(0.22);
                                          Vector var53 = var2.getVelocity();
                                          var53.setX(var53.getX() * 0.4 + var39.getX());
                                          var53.setZ(var53.getZ() * 0.4 + var39.getZ());
                                          var2.setVelocity(var53);
                                       }
                                    }

                                    var5.kJ = var6;
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private boolean a(ae var1, m1 var2, long var3) {
      return this.iq.j(var1) && var2 != null && (var2.mG || var2.mH || var3 - var2.my <= 900L || var3 < var2.mA || var2.mC != null && var3 <= var2.mD);
   }

   private double p(Player var1, Player var2, m1 var3, long var4) {
      long var6 = 0L;
      if (var1 != null && var1.getUniqueId() != null) {
         var6 ^= var1.getUniqueId().getMostSignificantBits();
         var6 ^= Long.rotateLeft(var1.getUniqueId().getLeastSignificantBits(), 17);
      }

      if (var2 != null && var2.getUniqueId() != null) {
         var6 ^= Long.rotateLeft(var2.getUniqueId().getMostSignificantBits(), 7);
         var6 ^= Long.rotateLeft(var2.getUniqueId().getLeastSignificantBits(), 29);
      }

      double var8 = Math.floorMod(var6, 1000L) / 1000.0;
      double var10 = 1.62 + var8 * 0.58;
      double var12 = var8 * Math.PI * 2.0;
      double var14 = Math.sin(var4 / 950.0 + var12) * 0.06;
      if (var3 != null && (var3.mG || var3.mH || var4 - var3.my <= 900L)) {
         var10 += 0.08 + (var8 - 0.5) * 0.08;
      }

      return Math.max(1.55, Math.min(2.35, var10 + var14));
   }

   private double w(Player var1, Player var2) {
      long var3 = 0L;
      if (var1 != null && var1.getUniqueId() != null) {
         var3 ^= var1.getUniqueId().getLeastSignificantBits();
      }

      if (var2 != null && var2.getUniqueId() != null) {
         var3 ^= Long.rotateLeft(var2.getUniqueId().getLeastSignificantBits(), 11);
      }

      double var5 = Math.floorMod(var3, 1000L) / 1000.0;
      return 0.82 + var5 * 0.36;
   }

   private boolean o(Material var1) {
      return var1 == Material.OBSIDIAN || var1 == Material.BEDROCK || var1 == Material.RESPAWN_ANCHOR;
   }

   private Block f(Location var1) {
      return var1 != null && var1.getWorld() != null ? var1.clone().add(0.0, -0.1, 0.0).getBlock() : null;
   }

   private boolean b(Location var1, Block var2) {
      return var1 != null
         && var2 != null
         && var1.getWorld() != null
         && var1.getWorld().equals(var2.getWorld())
         && var1.getBlockX() == var2.getX()
         && var1.getBlockY() == var2.getY()
         && var1.getBlockZ() == var2.getZ();
   }

   private boolean at(Player var1) {
      if (var1 == null) {
         return false;
      } else {
         Location var2 = var1.getLocation();
         Block var3 = this.f(var2);
         if (var3 != null && this.o(var3.getType())) {
            int var4 = 0;
            int var5 = 0;
            double var6 = var3.getY();
            double[][] var8 = new double[][]{
               {0.0, 0.0}, {0.85, 0.0}, {-0.85, 0.0}, {0.0, 0.85}, {0.0, -0.85}, {0.65, 0.65}, {-0.65, 0.65}, {0.65, -0.65}, {-0.65, -0.65}
            };

            for (double[] var12 : var8) {
               Location var13 = var2.clone().add(var12[0], 0.0, var12[1]);
               Block var14 = var13.getBlock();
               Block var15 = var14.getRelative(0, 1, 0);
               Block var16 = this.f(var13);
               if (var16 != null && !var14.getType().isSolid() && !var15.getType().isSolid()) {
                  var5++;
                  if (var16.getY() == var6 && this.o(var16.getType())) {
                     var4++;
                  }
               }
            }

            return var4 >= 5 && var5 >= 5;
         } else {
            return false;
         }
      }
   }

   private boolean i(Player var1, m1 var2, long var3) {
      if (var1 != null && var2 != null) {
         Block var5 = this.f(var1.getLocation());
         return var5 != null && this.o(var5.getType())
            ? this.b(var2.lT, var5) && var3 - var2.lU <= 1800L
               || this.b(var2.oi, var5) && var3 - var2.oj <= 1800L
               || this.b(var2.mP, var5) && var3 <= var2.mQ
               || this.b(var2.mw, var5) && var3 <= var2.mv
               || this.b(var2.mC, var5) && var3 <= var2.mD
               || this.b(var2.mE, var5) && var3 <= var2.mF
            : false;
      } else {
         return false;
      }
   }

   private boolean a(Player var1, Player var2, m1 var3, long var4, double var6, Vector var8) {
      if (var1 != null && var2 != null && var3 != null && !(var6 > 5.8)) {
         Block var9 = this.f(var1.getLocation());
         if (var9 != null && this.o(var9.getType())) {
            if (var9.getType() == Material.BEDROCK && !this.i(var1, var3, var4)) {
               return false;
            } else if (this.at(var1) && !this.i(var1, var3, var4)) {
               return false;
            } else if (!var3.mG && !var3.mH && !var3.lt && !var3.lB && !var3.mq) {
               return var8 != null && !(var8.lengthSquared() < 0.01)
                  ? var9.getType() == Material.OBSIDIAN || var9.getType() == Material.RESPAWN_ANCHOR || this.i(var1, var3, var4)
                  : false;
            } else {
               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private Vector q(Player var1, Player var2, m1 var3, long var4) {
      if (var3.oT != null && var4 < var3.oU) {
         Vector var6 = var3.oT.clone().setY(0.0);
         if (var6.lengthSquared() > 0.01) {
            return var6.normalize();
         }
      }

      Vector var7 = this.n(var1, var2);
      if (var7 != null && !(var7.lengthSquared() < 0.01)) {
         var7 = var7.clone().setY(0.0);
         if (var7.lengthSquared() < 0.01) {
            return null;
         } else {
            var7.normalize();
            var3.oT = var7.clone();
            var3.oU = var4 + 420L;
            return var7;
         }
      } else {
         return null;
      }
   }

   private boolean f(Player var1, Vector var2) {
      if (var1 == null || var2 == null || var2.lengthSquared() <= 0.01) {
         return false;
      } else if (!this.at(var1)) {
         return false;
      } else {
         Location var3 = var1.getLocation();
         Block var4 = this.f(var3);
         Location var5 = var3.clone().add(var2.clone().setY(0.0).normalize().multiply(0.9));
         Block var6 = this.f(var5);
         return var4 != null && var6 != null
            ? var4.getY() == var6.getY()
               && this.o(var4.getType())
               && this.o(var6.getType())
               && !var5.getBlock().getType().isSolid()
               && !var5.getBlock().getRelative(0, 1, 0).getType().isSolid()
            : false;
      }
   }

   private Vector a(Player var1, Player var2, Vector var3) {
      if (var1 != null && var3 != null && !(var3.lengthSquared() <= 0.01)) {
         Vector var4 = var3.clone().setY(0.0);
         if (var4.lengthSquared() <= 0.01) {
            return var3;
         } else {
            var4.normalize();
            if (this.f(var1, var4)) {
               return var3;
            } else {
               Location var5 = var1.getLocation().clone().add(var4.clone().multiply(0.9));
               Block var6 = var5.getBlock();
               Block var7 = var5.clone().add(0.0, -0.1, 0.0).getBlock();
               if (!this.iq.h(var6.getType()) && !this.iq.h(var7.getType())) {
                  return var3;
               } else {
                  Vector var8 = var2 == null ? null : this.n(var1, var2);
                  return var8 != null && var8.lengthSquared() > 0.01 ? var8 : new Vector(0, 0, 0);
               }
            }
         }
      } else {
         return new Vector(0, 0, 0);
      }
   }

   public boolean q(m1 var1, long var2) {
      if (var1 == null) {
         return false;
      } else if (!var1.lt && !var1.lB && !var1.mq && !var1.mG && !var1.mH) {
         long var4 = Math.max(Math.max(Math.max(var1.kD, var1.kE), Math.max(var1.kK, var1.my)), Math.max(Math.max(var1.kP, var1.kH), var1.oj));
         return var2 - var4 > 450L;
      } else {
         return false;
      }
   }

   public Vector n(Player var1, Player var2) {
      Location var3 = var1.getLocation();
      Location var4 = var2.getLocation();
      Vector var5 = var4.toVector().subtract(var3.toVector()).setY(0);
      if (var5.lengthSquared() > 0.01) {
         var5.normalize();
      } else {
         var5 = new Vector(1, 0, 0);
      }

      Vector var6 = null;
      double var7 = -999.0;
      double[] var9 = new double[]{0.0, 45.0, -45.0, 90.0, -90.0, 135.0, -135.0, 180.0};

      for (double var13 : var9) {
         Vector var15 = this.iq.b(var5.clone(), Math.toRadians(var13));
         Location var16 = var3.clone().add(var15.clone().multiply(1.2));
         Block var17 = var16.clone().add(0.0, -0.1, 0.0).getBlock();
         Block var18 = var16.getBlock();
         if (!var18.getType().isSolid()) {
            boolean var19 = !this.iq.h(var17.getType());
            boolean var20 = var17.getType().isSolid() || var16.clone().add(0.0, -1.1, 0.0).getBlock().getType().isSolid();
            double var21 = 0.0;
            if (var19) {
               var21 += 10.0;
            }

            if (var20) {
               var21 += 5.0;
            }

            double var23 = var15.dot(var5);
            var21 += var23 * 3.0;
            Location var25 = var3.clone().add(var15.clone().multiply(0.6));
            Block var26 = var25.clone().add(0.0, -0.1, 0.0).getBlock();
            if (!this.iq.h(var26.getType())) {
               var21 += 2.0;
            }

            if (var21 > var7) {
               var7 = var21;
               var6 = var15;
            }
         }
      }

      return var6 != null && var7 > 5.0 ? var6 : null;
   }

   public void u(NPC var1, Player var2, Player var3, ae var4, m1 var5, long var6) {
      Location var8 = var2.getLocation();
      Location var9 = var3.getLocation();
      Vector var10 = var9.toVector().subtract(var8.toVector());
      double var11 = Math.sqrt(var10.getX() * var10.getX() + var10.getZ() * var10.getZ());
      Vector var13 = var2.getVelocity();
      if (this.iq.ad(var2)) {
         var13.setY(0.35);
      } else if (this.iq.ac(var2) && var13.getY() < 0.05) {
         var13.setY(0.12);
      }

      if (var11 > 0.5) {
         Vector var14 = var10.clone().setY(0).normalize();
         var13.setX(var14.getX() * 0.35);
         var13.setZ(var14.getZ() * 0.35);
      }

      if (var9.getY() > var8.getY() + 1.5 && this.iq.ac(var2)) {
         var13.setY(Math.max(var13.getY(), 0.4));
      }

      var2.setVelocity(var13);
      var2.setSprinting(false);
      var1.getNavigator().cancelNavigation();
      this.v(var5);
      this.iq.a(var1, var2, var3, var4);
      var5.le = 0L;
   }
}
