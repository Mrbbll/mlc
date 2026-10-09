// Extracted from the user-provided PracticeBot-Source; strategy logic retained.
package com.mlc.mlcbot.practice.x;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public final class n0 {
   private final Map<UUID, m1> oW = new HashMap<>();

   public m1 h(UUID var1) {
      return this.oW.computeIfAbsent(var1, var0 -> new m1());
   }

   public m1 b(UUID var1, Function<UUID, m1> var2) {
      return this.oW.computeIfAbsent(var1, var2);
   }

   public m1 i(UUID var1) {
      return this.oW.get(var1);
   }

   public void g(UUID var1) {
      this.oW.remove(var1);
   }
}
