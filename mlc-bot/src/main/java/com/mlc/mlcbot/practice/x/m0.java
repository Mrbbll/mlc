// Extracted from the user-provided PracticeBot-Source; strategy logic retained.
package com.mlc.mlcbot.practice.x;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public final class m0 {
   private final Map<UUID, float[]> kC = new HashMap<>();

   public float[] a(UUID var1, float var2, float var3) {
      return this.kC.computeIfAbsent(var1, var2x -> new float[]{var2, var3});
   }

   public float[] a(UUID var1, Function<UUID, float[]> var2) {
      return this.kC.computeIfAbsent(var1, var2);
   }

   public float[] f(UUID var1) {
      return this.kC.get(var1);
   }

   public void g(UUID var1) {
      this.kC.remove(var1);
   }
}
