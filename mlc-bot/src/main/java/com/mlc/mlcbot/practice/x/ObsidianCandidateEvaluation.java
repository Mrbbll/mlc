// Extracted from the user-provided PracticeBot-Source; strategy logic retained.
package com.mlc.mlcbot.practice.x;

/** 单个黑曜石底座候选的评分及拒绝原因。 原源码：x/bi.java。 */
record ObsidianCandidateEvaluation(boolean ft, double fu, String fv, boolean fw) {
   boolean dm() {
      return this.fu > -900.0;
   }

   public boolean dn() {
      return this.ft;
   }

   public double do_val() {
      return this.fu;
   }

   public String cY() {
      return this.fv;
   }

   public boolean dp() {
      return this.fw;
   }
}
