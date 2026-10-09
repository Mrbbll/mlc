// Extracted from the user-provided PracticeBot-Source; strategy logic retained.
package com.mlc.mlcbot.practice.x;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

/** 按机器人 UUID 创建、查询和清理 CPVP 运行状态。 原源码：x/n0.java。 */
public final class CpvpStateStore {
   private final Map<UUID, CpvpCombatState> oW = new HashMap<>();

   public CpvpCombatState h(UUID var1) {
      return this.oW.computeIfAbsent(var1, var0 -> new CpvpCombatState());
   }

   public CpvpCombatState b(UUID var1, Function<UUID, CpvpCombatState> var2) {
      return this.oW.computeIfAbsent(var1, var2);
   }

   public CpvpCombatState i(UUID var1) {
      return this.oW.get(var1);
   }

   public void g(UUID var1) {
      this.oW.remove(var1);
   }
}
