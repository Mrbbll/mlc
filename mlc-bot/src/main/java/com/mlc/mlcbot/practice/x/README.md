# PracticeBot 战斗类命名对照

本目录的短类名沿用自 PracticeBot-Source，原源码中也保留了反编译痕迹。现根据代码职责重命名全部 68 个类，文件名与 Java 类名保持一致。

保留 practice.x 包路径，避免把本次命名整理扩展成目录重组。每个类顶部都标注了用途和原文件名。方法、字段、枚举值和战斗算法保持原样；后续查阅原源码时使用下表定位。

建议从 MeleeCombatController（普通近战）或 CpvpCombatController（水晶战斗）开始阅读；CpvpCombatLoop 负责每 tick 的策略调度，CpvpSettings 与 CpvpCombatState 分别保存配置和运行状态。

小文件中，Controller/Executor/Planner 负责行为，Settings/Defaults/Preset 保存参数，State/Store 保存运行状态，Plan/Evaluation/Check/Candidate 保存计算结果，CacheKey 用作缓存索引，避免重复扫描和计算。

| 原文件 | 现文件 | 作用 |
| --- | --- | --- |
| `a.java` | [PracticeBotMode.java](PracticeBotMode.java) | 原版机器人模式：普通近战、水晶战斗、练习靶子。 |
| `b.java` | [PracticeCombatConfig.java](PracticeCombatConfig.java) | 读取内置配置中的近战、盾牌和 CPVP 默认参数。 |
| `ae.java` | [CpvpSettings.java](CpvpSettings.java) | 每个机器人的 CPVP 行为开关和难度时序参数。 |
| `af.java` | [CpvpDefaults.java](CpvpDefaults.java) | 从配置加载的 CPVP 默认行为参数。 |
| `ag.java` | [CpvpDifficultyPreset.java](CpvpDifficultyPreset.java) | 单个难度等级的攻击、延迟和失误参数预设。 |
| `ah.java` | [CpvpDifficulty.java](CpvpDifficulty.java) | CPVP 难度等级，包括自定义难度。 |
| `al.java` | [CpvpAimController.java](CpvpAimController.java) | CPVP 视线、转向、瞄准和战斗动作朝向控制。 |
| `an.java` | [AnchorCombatEvaluator.java](AnchorCombatEvaluator.java) | 评估锚方案收益、目标状态及是否优先使用锚。 |
| `ao.java` | [AnchorActionExecutor.java](AnchorActionExecutor.java) | 执行锚放置、充能、引爆及延迟动作链。 |
| `aq.java` | [AnchorRayTraceCacheKey.java](AnchorRayTraceCacheKey.java) | 锚交互射线检测的缓存键。 |
| `ar.java` | [AnchorCombatPlanner.java](AnchorCombatPlanner.java) | 选择锚候选位置并安排近距离压制与恢复方案。 |
| `as.java` | [AnchorCombatController.java](AnchorCombatController.java) | 锚策略入口，协调评估、规划和执行。 |
| `at.java` | [NearbyAnchorCountCacheKey.java](NearbyAnchorCountCacheKey.java) | 按世界、方块位置和扫描范围缓存附近锚的数量。 |
| `au.java` | [CpvpCombatLoop.java](CpvpCombatLoop.java) | 每次 tick 的 CPVP 主决策和策略调度。 |
| `av.java` | [CpvpCombatController.java](CpvpCombatController.java) | CPVP 策略总入口及共享工具、状态和缓存。 |
| `aw.java` | [ExplosionDamageCacheKey.java](ExplosionDamageCacheKey.java) | 爆炸位置与受伤玩家对应的伤害计算缓存键。 |
| `ax.java` | [NearbyCrystalCacheKey.java](NearbyCrystalCacheKey.java) | 按世界、位置和搜索范围缓存附近水晶。 |
| `ay.java` | [CrystalSafetyCacheKey.java](CrystalSafetyCacheKey.java) | 水晶位置、机器人与目标状态对应的安全评估缓存键。 |
| `az.java` | [CrystalRecoveryController.java](CrystalRecoveryController.java) | 清理危险水晶并执行水晶战斗恢复动作。 |
| `ba.java` | [CrystalCombatPlanner.java](CrystalCombatPlanner.java) | 生成水晶放置、击破、黑曜石压制和恢复计划。 |
| `bb.java` | [CrystalSafetyCheck.java](CrystalSafetyCheck.java) | 水晶安全检查的结果、原因和自伤上限。 |
| `bc.java` | [CrystalCandidate.java](CrystalCandidate.java) | 候选水晶实体及其底座方块。 |
| `bd.java` | [CrystalRecoveryCheck.java](CrystalRecoveryCheck.java) | 水晶恢复检查结果及可用恢复方块。 |
| `be.java` | [CrystalEnvironmentScanner.java](CrystalEnvironmentScanner.java) | 扫描附近黑曜石、水晶和战斗障碍。 |
| `bf.java` | [CrystalCombatController.java](CrystalCombatController.java) | 水晶规划、环境扫描、拆障和恢复策略入口。 |
| `bg.java` | [CrystalSafetyEvaluation.java](CrystalSafetyEvaluation.java) | 水晶安全性、自伤、目标伤害及拒绝原因的详细结果。 |
| `bh.java` | [ObsidianPlacementController.java](ObsidianPlacementController.java) | 寻找水晶底座并安排黑曜石放置与压制。 |
| `bi.java` | [ObsidianCandidateEvaluation.java](ObsidianCandidateEvaluation.java) | 单个黑曜石底座候选的评分及拒绝原因。 |
| `bj.java` | [ObsidianSearchState.java](ObsidianSearchState.java) | 黑曜石候选搜索过程中的最优结果和候选列表。 |
| `bk.java` | [ObsidianPlacementPlan.java](ObsidianPlacementPlan.java) | 黑曜石底座的最终选择、评分及恢复标记。 |
| `bl.java` | [ScoredObsidianCandidate.java](ScoredObsidianCandidate.java) | 一个黑曜石候选方块及其评分。 |
| `bm.java` | [CrystalBaseSelector.java](CrystalBaseSelector.java) | 寻找目标周围可用于水晶的黑曜石或基岩底座。 |
| `bn.java` | [CrystalTargetTracker.java](CrystalTargetTracker.java) | 跟踪目标高度、水晶底座锁定与击飞后的攻击窗口。 |
| `bo.java` | [CrystalActionExecutor.java](CrystalActionExecutor.java) | 执行水晶放置、击破和相关连续战斗动作。 |
| `br.java` | [GoldenAppleController.java](GoldenAppleController.java) | 金苹果回血决策、食用动作和生命状态维护。 |
| `bs.java` | [CpvpInventoryController.java](CpvpInventoryController.java) | 生成 CPVP 装备并管理物品切换、补充和库存缓存。 |
| `bu.java` | [InventoryMaterialCacheKey.java](InventoryMaterialCacheKey.java) | 按玩家和物品材质缓存库存查询。 |
| `bv.java` | [SwordMaceController.java](SwordMaceController.java) | CPVP 剑与重锤攻击、切换及攻击窗口控制。 |
| `bw.java` | [AnchorCombatPlan.java](AnchorCombatPlan.java) | 锚候选方块、交互位置、伤害评分和战术标记。 |
| `bx.java` | [CrystalCombatPlan.java](CrystalCombatPlan.java) | 水晶行动状态、底座、伤害收益和候选实体。 |
| `by.java` | [CrystalPlanStatus.java](CrystalPlanStatus.java) | 水晶计划的可执行状态或不可执行原因。 |
| `bz.java` | [PearlTrajectorySolution.java](PearlTrajectorySolution.java) | 珍珠发射速度、误差、飞行 tick 和碰撞信息。 |
| `c0.java` | [PearlTrajectoryTrace.java](PearlTrajectoryTrace.java) | 珍珠轨迹模拟中的命中、距离及落点记录。 |
| `c1.java` | [CpvpAvoidanceController.java](CpvpAvoidanceController.java) | CPVP 安全移动方向选择与障碍绕行。 |
| `d0.java` | [CpvpTerrainAnalyzer.java](CpvpTerrainAnalyzer.java) | 分析前方障碍、地形和移动通道。 |
| `d1.java` | [CpvpMotionTracker.java](CpvpMotionTracker.java) | 维护生命变化、着地判断和受击移动状态。 |
| `e0.java` | [CpvpMovementController.java](CpvpMovementController.java) | CPVP 追击、跟随、侧移和战斗距离控制。 |
| `e1.java` | [CpvpEscapeController.java](CpvpEscapeController.java) | 选择撤退方向、脱困位置并执行逃离移动。 |
| `f0.java` | [CpvpTerrainController.java](CpvpTerrainController.java) | 地形分析、障碍绕行和撤退脱困的统一入口。 |
| `f1.java` | [AerialMaceController.java](AerialMaceController.java) | 重锤空中阶段、珍珠衔接及落地攻击时序。 |
| `g0.java` | [PearlCombatController.java](PearlCombatController.java) | 珍珠追击、撤退、拦截和投掷时机决策。 |
| `g1.java` | [PearlTrajectoryPredictor.java](PearlTrajectoryPredictor.java) | 珍珠弹道求解、碰撞模拟和玩家落点预测。 |
| `h0.java` | [PearlAimCacheKey.java](PearlAimCacheKey.java) | 发射位置、目标位置与弹道参数对应的瞄准缓存键。 |
| `h1.java` | [SimulatedLandingCacheKey.java](SimulatedLandingCacheKey.java) | 按给定位置和速度缓存模拟落点。 |
| `i0.java` | [PlayerLandingCacheKey.java](PlayerLandingCacheKey.java) | 按玩家当前位置和速度缓存预测落点。 |
| `i1.java` | [CpvpFeaturePolicy.java](CpvpFeaturePolicy.java) | 查询 CPVP 配置中的行为开关及珍珠冷却。 |
| `j0.java` | [ExplosionDamageEvaluator.java](ExplosionDamageEvaluator.java) | 估算爆炸伤害并评估自伤与目标伤害的取舍。 |
| `j1.java` | [ExplosionTradeEvaluation.java](ExplosionTradeEvaluation.java) | 爆炸自伤取舍的允许标记、原因和伤害结果。 |
| `k0.java` | [BlockHazardClassifier.java](BlockHazardClassifier.java) | 判断液体方块和危险地形方块。 |
| `k1.java` | [CombatGeometry.java](CombatGeometry.java) | 战斗距离、可见性、射线检测和交互空间判断。 |
| `l0.java` | [CrystalBaseValidityCacheKey.java](CrystalBaseValidityCacheKey.java) | 水晶底座方块有效性检查的缓存键。 |
| `l1.java` | [CombatRayTraceCacheKey.java](CombatRayTraceCacheKey.java) | 视线和方块交互射线检测的缓存键。 |
| `m0.java` | [BotRotationStore.java](BotRotationStore.java) | 按机器人 UUID 保存瞄准时使用的 yaw 和 pitch。 |
| `m1.java` | [CpvpCombatState.java](CpvpCombatState.java) | 单个机器人的 CPVP 计时器、目标、动作链和战术状态。 |
| `n0.java` | [CpvpStateStore.java](CpvpStateStore.java) | 按机器人 UUID 创建、查询和清理 CPVP 运行状态。 |
| `v.java` | [MeleeCombatController.java](MeleeCombatController.java) | 普通近战攻击、暴击、破盾和攻击节奏控制。 |
| `x.java` | [MeleeMovementController.java](MeleeMovementController.java) | 普通近战的地形、水中移动、侧移和退让控制。 |
| `y.java` | [ShieldController.java](ShieldController.java) | 盾牌格挡决策、朝向判断及格挡状态控制。 |
