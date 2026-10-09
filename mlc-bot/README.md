# mlc-bot

Paper **26.3 / Java 25** 的内部功能模块，随 mlc-core 的 Shadow JAR 打包。服务器无需另装 Citizens 或 PracticeBot。

## 使用

默认仅 OP 拥有 `mlc.bot` 权限，也可通过权限插件授予玩家。

```text
/mlcbot spawn normal normal
/mlcbot spawn cpvp hard
/mlcbot spawn anchor pro
/mlcbot spawn mace hard
/mlcbot spawn advanced expert
/mlcbot spawn dummy easy
/mlcbot remove
```

| 类型 | 行为 |
| --- | --- |
| `normal` / `sword` | 原版近战控制器：侧移、W-tap、S-tap、跳跃暴击、切斧破盾、受击退让和水中移动，按强度开启相应功能 |
| `cpvp` / `crystal` | 原版水晶放置/击破、黑曜石布局、剑术补刀、珍珠追击/撤退/落点预测、金苹果回血、空中与水中处理 |
| `anchor` / `cpvp-anchor` | CPVP 加原版高级锚策略：选址、自伤和收益评估、遮挡萤石、放置、充能、引爆、近距离压制及防守调度 |
| `mace` / `cpvp-mace` | CPVP 加原版重锤策略：下降换锤、剑/锤切换、珍珠衔接及空中攻击窗口判断 |
| `advanced` / `full` / `cpvp-full` | 同时启用水晶、锚、重锤、珍珠、回血、侧移和原版软障碍拆除策略，由原状态机根据战况选择动作 |
| `dummy` | 可受伤、不主动攻击的练习靶子 |

锚和重锤是原插件 CPVP 的策略开关，上表新增的是固定预设。因此 `mace` 也会使用水晶，`anchor` 也会按收益选择水晶；不是纯锤或纯锚机器人。锚按原版逻辑仅在可以爆炸的维度使用，下界不会引爆。

强度映射：`easy` / `1` → **EASY**，`normal` / `medium` / `2` → **MEDIUM**，`hard` / `3` → **HARD**，`expert` / `pro` / `4` → **PRO**。CPVP 使用原版时序表、犹豫概率、水晶失误间隔和非紧急珍珠克制参数。NORMAL 使用原版 aggression、移动、暴击概率和暴击速度参数组合；攻击距离统一为 NORMAL 的 3 格。NORMAL 保留 PracticeBot 的伤害和击退计算（包括 aggression 伤害倍率），CPVP 剑/锤、水晶和锚使用服务器实体攻击/爆炸机制。dummy 不使用强度。

机器人以生成者为唯一目标，每人一个，全服最多 32 个。重新生成成功后替换旧机器人。玩家退出、死亡、换世界、离开超过 48 格，以及模块卸载时自动清理。玩家切换创造/旁观模式时停止攻击并取消尚未执行的战斗动作，恢复后重新建立战斗状态。

## 固定 kits 与世界交互

NORMAL/DUMMY 使用默认下界合金保护 IV 套装、锋利 V 剑、破盾斧和 99 次有限图腾储备。CPVP 各预设直接使用 `CpvpInventoryController.java`（原版 `x/bs.java`）的内置装备生成：爆炸保护 IV 裤子、摔落保护 IV 靴子、锋利 V/击退 I 剑、破甲 IV/致密 V 重锤、效率 V 镐、水晶、黑曜石、金苹果及末影珍珠。锚预设的第 4/8 槽分别放锚和萤石，背包布局沿用原版。装备不可破坏；CPVP 普通消耗品按原版自动补充，图腾仍由模块限制为总共 99 次。

只在内存加载 JAR 内的固定默认战斗参数，不向服务器生成 config.yml、kits 文件、模板文件或 NPC 存档。机器人死亡不会掉落装备/经验，也不会触发 MLC 的假人回溯文件与死亡扣血逻辑。

原策略的方块放置、水晶使用、锚充能/引爆通过 Bukkit 放置/交互事件；拆障通过玩家破坏接口及方块破坏事件，禁用机器人拆障掉落和经验。保护插件可以取消这些动作。机器人自己的水晶和锚爆炸保留实体伤害/击退，保留竞技场方块。

所有战斗延迟任务均属于当前机器人，移除时取消整个动作链并清除尚存水晶/珍珠。放置和拆除的临时方块记录原状态，清理时仅还原仍符合机器人最后修改状态的已加载方块，避免覆盖玩家后续改动。每个机器人最多记录 256 个修改位置、同时保留 128 个水晶实体；原有多机器人自适应扫描节流继续生效。

## 移植来源与适配范围

直接从用户提供的 `PracticeBot-Source` 提取战斗策略/状态类，保持原算法。`practice/x` 中的 68 个类现已按职责重命名，类顶部标注用途及原文件名；方法与字段仍保留原名。完整对照见 [战斗类命名说明](src/main/java/com/mlc/mlcbot/practice/x/README.md)。主要入口如下：

| 原源码 | 当前类名 | 模块中的作用 |
| --- | --- | --- |
| `BotTrait`、`x/u` 的战斗前置 tick | `BotTrait`、`CombatBrain` | 战斗状态、受击历史、暴击阶段与落地状态；去除持久化和编辑器生命周期 |
| `x/v`、`x/x`、`x/y` | `MeleeCombatController`、`MeleeMovementController`、`ShieldController` | NORMAL 近战、地形/水中移动、破盾及盾牌控制 |
| `x/av`、`x/au` | `CpvpCombatController`、`CpvpCombatLoop` | CPVP 策略门面与主状态机 |
| `x/as`、`x/an`、`x/ao`、`x/ar` | `AnchorCombatController`、`AnchorCombatEvaluator`、`AnchorActionExecutor`、`AnchorCombatPlanner` | 高级锚计划、评估、动作链和恢复 |
| `x/bv`、`x/f1` | `SwordMaceController`、`AerialMaceController` | 剑/重锤、空中状态与珍珠衔接 |
| `x/g0`、`x/g1` | `PearlCombatController`、`PearlTrajectoryPredictor` | 珍珠决策、弹道与落点预测 |
| `x/bo`、`x/bh`、`x/bf`、`x/ba` | `CrystalActionExecutor`、`ObsidianPlacementController`、`CrystalCombatController`、`CrystalCombatPlanner` | 黑曜石、水晶、拆障、爆炸收益与战斗计划 |
| `x/br`、`x/bs`、`x/ae`、`x/ag` | `GoldenAppleController`、`CpvpInventoryController`、`CpvpSettings`、`CpvpDifficultyPreset` | 回血、原版固定 kits、难度和行为参数 |

本地源码的顶层类型只有 NORMAL、CPVP、DUMMY，模板资源也围绕这三类。未找到 UHC、矿车、弓箭或风弹的独立控制器，因此没有将宣传内容当作已实现模式。GUI、模板编辑、管理员命令、独立插件初始化和外部配置系统不在本模块移植范围。

NPC 与导航沿用此前的 Citizens 提取实现：

- `nms/EmptyChannel`、`nms/EmptyPacketListener` 直接来自本地 Citizens2；空连接与 `BotPlayer` 按其 26.3 HumanController/EntityHumanNPC 流程适配。
- `practice/bridge/` 提供原策略所需 NPC、FollowTrait、Navigator 接口，连接现有内置假人和 A*；不是外部 Citizens 插件依赖。FollowTrait 保留导航取消后的重新跟随行为。
- `navigation/BotNavigator` 按 Citizens 分 tick A* 结构裁剪，每 tick 最多 96 个节点，总上限 2048、水平范围 24 格，只查询已加载区块。主要适用于完整方块地形，支持一格跳跃、最多三格下降；不包含 Citizens 所有楼梯、门和异步导航特性。

`combat/CombatBrain` 负责入口和前置状态，`combat/PracticeProfiles` 负责指令预设，`combat/CombatRuntime` 负责世界保护事件、任务归属和清理。原策略放置/爆炸出口经过这些适配，故世界破坏与持久化行为和独立原插件存在差别。

`nms/EntityGroundState` 从当前 Paper 26.3 的实体句柄读取着地标记，替代弃用的 `Player.isOnGround()`，沿用 CraftEntity 使用的原生标记。机器人身份由 `BotIdentity` 使用 PDC 的 `mlc:bot` 标记识别；标记在生成前写入，死亡和传送监听也使用同一入口，不依赖旧的 `FixedMetadataValue`。内置 NPC 注册表仍兼容外部插件已有的 `NPC` 元数据标记。

Citizens 的 OSL-3.0 许可证随 JAR 保存于 `META-INF/mlc-bot/CITIZENS-LICENSE.txt`。Citizens 衍生部分遵循该许可证；PracticeBot 策略来源为用户提供的本地源码，未找到单独的许可证文件。

## 构建与验证

```powershell
.\gradlew.bat :mlc-bot:test :mlc-core:build
```

Paperweight 2.0.0-beta.24 需要 Gradle 9.7.1，wrapper 已同步。Paper 开发包仅用于编译，服务器 NMS/CraftBukkit 类不会打进最终 JAR。

15 项自动测试覆盖绕墙导航、动态障碍重规划、搜索预算、原版难度时序与失误参数、机器人之间参数隔离、爆炸自伤取舍、珍珠重力/阻力积分和弹道边界，以及 NPC 注册前的机器人识别、真人目标筛选和外部 NPC 兼容。新增回归测试覆盖 CPVP、锚、重锤和高级模式共用的悬空目标水晶底座搜索，防止将候选高度的 LinkedHashSet 强制转换为 List。已通过完整 Gradle 构建。

尚未运行实际 Paper 服务器实测。上线前仍需检查假人显示、追击和伤害、锚的完整充能/引爆链、重锤下降与珍珠落地衔接、领地保护取消以及退出/死亡/切换模式后的清理；编译和单元测试不能验证这些游戏内行为。
