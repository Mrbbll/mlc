# MLC

MLC 是面向 Paper 26.3 的 Minecraft 综合插件，使用 Java 25 构建。项目采用“一个主插件 + 多个功能模块”的结构，服务器只需安装最终生成的一个 JAR。

## 功能

- **经济与领地**：通过 Vault 接入经济；支持领地管理和 BlueMap 标记。
- **传送石碑**：创建和管理传送点，并提供 GUI 操作。
- **聊天格式**：通过 Vault Chat 格式化聊天消息。
- **玩家传送**：支持 `/tpa`、`/tpahere`、`/tpaccept`、`/back`、`/rtp` 和家功能。
- **邮件与物品**：玩家邮件、自定义物品、抽奖箱、便携背包和功能菜单。
- **服务器工具**：PlaceholderAPI 占位符、资源包下发、坐下、右键收获、签到、MOTD 和睡眠投票等。
- **PvP 机器人**：内置练习机器人，支持近战、CPVP、锚、重锤、高级模式和练习靶。机器人模块不要求另外安装 Citizens 或 PracticeBot。

机器人命令示例：

```text
/mlcbot spawn normal normal
/mlcbot spawn cpvp hard
/mlcbot spawn advanced expert
/mlcbot spawn dummy easy
/mlcbot remove
```

生成和移除机器人的权限为 `mlc.bot`，默认仅 OP 拥有。模式和难度的完整说明见 [mlc-bot README](mlc-bot/README.md)。

## 运行依赖

| 依赖 | 是否必需 | 用途 |
| --- | --- | --- |
| Paper 26.3 | 必需 | Minecraft 服务端 |
| Java 25 | 必需 | 运行服务端及构建项目 |
| Vault | 必需 | 经济和聊天服务接口 |
| PlaceholderAPI | 必需 | 占位符功能 |
| BlueMap | 可选 | 领地和传送点地图标记 |

HikariCP、SQLite JDBC 和 Adventure 依赖会随 MLC 一起打包。安装服务器插件时不需要单独安装这些库。

## 项目结构

```text
mlc/
├── build.gradle.kts           # 公共构建配置、版本号和统一依赖
├── settings.gradle.kts        # Gradle 项目注册
├── gradle/libs.versions.toml  # 依赖版本目录
├── mlc-core/                  # 主插件、命令和核心功能
├── mlc-domain/                # 领地功能库
├── mlc-waystone/              # 传送石碑功能库
├── mlc-styte/                 # 聊天格式功能库
├── mlc-bot/                   # 内置 PvP 机器人功能库
└── target/                    # 最终插件 JAR
```

`mlc-domain`、`mlc-waystone`、`mlc-styte` 和 `mlc-bot` 都作为功能库打包进 `mlc-core` 的 Shadow JAR，并由主插件启动时初始化。它们不是需要分别安装的 Paper 插件。

## 构建

先安装并配置 JDK 25，然后在 Windows PowerShell 中运行：

```powershell
.\gradlew.bat projects
.\gradlew.bat :mlc-core:build
.\gradlew.bat build
```

完整构建会把 Shadow JAR 复制到 `target/`，文件名为 `mlc-版本号.jar`。根构建脚本会在 Gradle 配置时递增 `version.properties` 中的补丁版本，因此运行 Gradle 命令后该文件可能发生变化。

## 安装

将 `target/mlc-*.jar` 放入 Paper 服务端的 `plugins/` 目录，并确保 Vault 和 PlaceholderAPI 已安装。BlueMap 可选；未安装时其地图集成功能不会启用。

## 许可

MLC 项目许可为 GPL-3.0。`mlc-bot` 中的 Citizens 衍生部分遵循 OSL-3.0，相关许可证随 JAR 一同发布。
