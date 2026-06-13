# mlc

Mlc 是一个 Minecraft Paper 1.21 综合插件，集成了经济、领地、传送石碑等核心功能。

## 功能

- **经济系统** — 基于 Vault + SQLite / HikariCP，自定义货币物品
- **领地系统** — 区块圈地、4 级权限、BlueMap 地图标记
- **传送石碑** — 自定义传送点、GUI 操作
- **传送请求** — `/tpa` `/tpahere` `/tpaccept` `/back` `/rtp`
- **家设置** — `/sethome` `/delhome` `/home`
- **邮件系统** — 玩家间邮件、GUI 邮箱
- **自定义物品** — YAML 驱动，支持属性、附魔、Lore、PDC
- **抽奖箱** — 自定义奖池
- **背包** — 便携背包 GUI
- **资源包** — 自动下发
- **PlaceholderAPI** — `%mlc_*%` 占位符
- 更多：坐下、右键收获、签到、MOTD、睡眠投票等

## 依赖

| 插件 | 必需 | 说明 |
|------|------|------|
| Vault | ✅ | 经济 API |
| PlaceholderAPI | ✅ | 占位符 |
| BlueMap | ❌ | 领地地图标记（可选） |

## 项目结构

```
mlc/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle/libs.versions.toml
│
├── mlc-core/                  # 插件主模块
│   ├── build.gradle.kts
│   └── src/                   # 核心 + 入口
│
├── mlc-domain/                # 领地
│   └── src/
│
├── mlc-waystone/              # 传送石碑
│   └── src/
│
└── target/                    # 构建产物
    └── mlc-{version}.jar
```

## 构建

```bash
# 构建（版本号自动 +1）
./gradlew build

# 产物在 target/ 目录
ls target/
# mlc-1.0.xxx.jar
```

## 安装

将 `target/mlc-*.jar` 放入服务器的 `plugins/` 文件夹，确保已安装 Vault 和 PlaceholderAPI，Bluemap

## 许可证

GPL-3.0
