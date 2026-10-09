// Extracted from the user-provided PracticeBot-Source; strategy logic retained.
package com.mlc.mlcbot.practice.x;

import org.bukkit.Location;
import org.bukkit.block.Block;

public record bw(Block hk, Block hl, Location hm, double hn, double ho, double hp, boolean hq, boolean hr, boolean hs, boolean ht, boolean hu) {
   public boolean dC() {
      return this.ht && this.hk != null;
   }

   public Location dD() {
      return this.hk.getLocation().add(0.5, 0.5, 0.5);
   }

   public Block dE() {
      return this.hk;
   }

   public Block dF() {
      return this.hl;
   }

   public Location dG() {
      return this.hm;
   }

   public double dc() {
      return this.hn;
   }

   public double dd() {
      return this.ho;
   }

   public double do_val() {
      return this.hp;
   }

   public boolean dH() {
      return this.hq;
   }

   public boolean dI() {
      return this.hr;
   }

   public boolean dJ() {
      return this.hs;
   }

   public boolean dK() {
      return this.ht;
   }

   public boolean dL() {
      return this.hu;
   }
}
