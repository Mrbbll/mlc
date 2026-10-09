// Extracted from the user-provided PracticeBot-Source; strategy logic retained.
package com.mlc.mlcbot.practice.x;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.block.Block;

/** 黑曜石候选搜索过程中的最优结果和候选列表。 原源码：x/bj.java。 */
final class ObsidianSearchState {
   Block fx;
   double fy = -999.0;
   boolean fz;
   boolean fw;
   String fa;
   String fv;
   Block fA;
   int fB = 24;
   final List<ScoredObsidianCandidate> fC = new ArrayList<>(24);
}
