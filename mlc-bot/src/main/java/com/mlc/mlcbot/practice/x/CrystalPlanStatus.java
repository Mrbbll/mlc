// Extracted from the user-provided PracticeBot-Source; strategy logic retained.
package com.mlc.mlcbot.practice.x;

/** 水晶计划的可执行状态或不可执行原因。 原源码：x/by.java。 */
public enum CrystalPlanStatus {
   NONE,
   PLACE_SAFE,
   BREAK_SAFE,
   CREATE_OBSIDIAN_PRESSURE,
   SELF_DAMAGE_UNSAFE_RECOVERY,
   INVALID_NO_BASE,
   INVALID_NO_LOS,
   INVALID_OUT_OF_REACH,
   INVALID_VERTICAL,
   INVALID_NO_RESOURCES,
   INVALID_NO_SPACE,
   INVALID_LOW_DAMAGE,
   INVALID_STALE;
}
