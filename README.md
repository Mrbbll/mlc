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

## 在线时长排行榜 API

1. 在游戏内执行 `/papi ecloud download Statistic` 和 `/papi reload`，再用 `/papi parse me %statistic_seconds_played%` 确认变量返回累计秒数。
2. MLC 默认监听 `127.0.0.1:8090`。可在 `plugins/mlc/config.yml` 的 `web` 下设置 `enabled`、`bind-address` 和 `port`，修改后重启。穿透客户端与服务端同机时，将公网 TCP 端口映射到 `127.0.0.1:8090`。

本地可用以下命令检查接口。网站通过同源入口转发 API 时，浏览器可使用相对路径调用。

```powershell
curl.exe -i "http://127.0.0.1:8090/api/v1/leaderboard/playtime?limit=10&period=all"
```

接口公开只读，`limit` 默认 100、范围 1–500，`period` 默认 `all` 且只接受 `all`。成功响应包含快照时间 `updatedAt`、全量有效玩家数 `totalPlayers` 和截断后的 `players`。每个玩家条目包含 `uuid`、`name` 和整数 `seconds`。成功响应支持最长 5 分钟的 HTTP 缓存。

MLC 约每分钟分批读取在线和离线玩家的 PAPI 累计秒数，按时长降序、同秒按名称升序生成快照。玩家优先使用当前登录名或最后已知名称，缺少名称时显示 UUID。

错误响应包含 `error.code` 和 `error.message`。无效条数返回 `400 INVALID_LIMIT`，不支持的周期返回 `400 UNSUPPORTED_PERIOD`，其他 HTTP 方法返回 `405 METHOD_NOT_ALLOWED`。首次采集完成前或 Statistic 扩展不可用时，接口返回 `500 INTERNAL_ERROR`。

时长口径与存盘由 Statistic 扩展底层的 Minecraft 统计决定，包含挂机时间。MLC 只读取并缓存结果，因此崩溃后能恢复到哪个时点取决于服务端何时保存统计。

## 许可

MLC 项目许可为 GPL-3.0。`mlc-bot` 中的 Citizens 衍生部分遵循 OSL-3.0，相关许可证随 JAR 一同发布。

## 资源包托管

将资源包文件放在服务器的 `plugins/respack/` 目录中，确保 `pack.mcmeta` 和 `assets/` 直接位于该目录下。插件启动时会异步压缩并上传到 MCPacks，成功后自动发送给在线玩家，之后加入的玩家也会收到资源包。资源包沿用强制接受设置。

管理员可以执行 `/respack` 重新压缩、上传并向在线玩家分发；`/mlcreload` 也会触发上传。这两个命令需要 `mlc.op` 权限（默认 OP）。上传失败时保留上一次成功的资源包信息，缓存在 `plugins/mlc/resource-pack-cache.yml`。首次上传前没有缓存时，上传完成后才会发送资源包。

MCPacks 要求 ZIP 根目录包含有效的 `pack.mcmeta`，且不能包含可执行文件；服务的上传上限为 250 MiB。旧配置项 `resourcepack` 不再使用。
