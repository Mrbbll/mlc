# MLC 模块开发说明

本文说明当前项目的 Gradle 多模块结构、模块之间的依赖关系，以及如何新增功能模块。

## 1. 项目结构

```text
mlc/
├── build.gradle.kts              # 根项目和所有子项目的公共配置
├── settings.gradle.kts           # Gradle 模块注册
├── gradle/libs.versions.toml     # 依赖和插件版本
├── mlc-core/                     # 主插件和核心功能
├── mlc-domain/                   # 领地功能库
├── mlc-waystone/                 # 传送石碑功能库
├── mlc-styte/                    # 聊天格式功能库
├── mlc-craftengine/              # 当前 settings 中声明，但目录尚不存在
└── target/                       # 最终插件 JAR
```

`settings.gradle.kts` 当前声明了以下模块：

```kotlin
include(":mlc-core")
include(":mlc-domain")
include(":mlc-waystone")
include(":mlc-styte")
include(":mlc-craftengine")
```

其中 IntelliJ 显示的 `*.main` 和 `*.test` 是 Gradle 的 source set 对应的 IDE 模块，不是额外的 Gradle 项目。例如：

```text
mlc.mlc-core.main  -> mlc-core/src/main
mlc.mlc-core.test  -> mlc-core/src/test
```

`.idea` 下的 `.iml` 文件由 IDE/Gradle 同步生成，不建议手工维护。

## 2. 根项目的公共配置

根目录的 `build.gradle.kts` 通过 `subprojects { ... }` 给所有子项目统一配置：

- Java 和 `java-library` 插件
- Java 21 toolchain
- Paper API 依赖
- Maven 仓库
- UTF-8 编码和 Java 21 编译目标
- 项目 group 和版本号

因此普通子模块一般只需要声明自己的额外依赖，不需要重复声明 Java 插件或 Paper API。

## 3. 当前模块依赖关系

当前实际的项目依赖方向是：

```text
mlc-core
├── implementation(project(":mlc-domain"))
├── implementation(project(":mlc-waystone"))
└── implementation(project(":mlc-styte"))
```

这些依赖定义在 `mlc-core/build.gradle.kts` 中。

这意味着：

- `mlc-core` 可以直接使用其他模块的 Java 类。
- `mlc-domain`、`mlc-waystone`、`mlc-styte` 的代码会被合并进 `mlc-core` 的 Shadow JAR。
- 最终服务器只安装一个 `target/mlc-版本.jar`。
- 这些功能模块不是独立的 Paper 插件，没有自己的 `plugin.yml` 和 `JavaPlugin` 主类。

### `implementation` 和 `compileOnly`

`implementation(...)` 的依赖会参与编译，并通常被 Shadow JAR 打包。

`compileOnly(...)` 只在编译时提供，不会被打包，运行时需要由服务器或其他插件提供。

当前项目中：

| 依赖 | 配置 | 运行时来源 |
|---|---|---|
| Paper API | `compileOnly`（根项目统一配置） | Paper 服务端 |
| Vault | `compileOnly` | Vault 插件 |
| PlaceholderAPI | `compileOnly` | PlaceholderAPI 插件 |
| BlueMap | `compileOnly` | BlueMap 插件，可选 |
| HikariCP | `implementation` | 打包进 mlc JAR，并重定位 |
| SQLite | `implementation` | 打包进 mlc JAR |
| Adventure | `implementation` | 由相关模块和主模块打包 |

HikariCP 在 `mlc-core` 的 Shadow 配置中会重定位为 `com.mlc.lib.hikari`，避免和服务器中的其他版本冲突。

## 4. 模块运行时初始化

Paper 实际加载的是 `mlc-core/src/main/resources/plugin.yml`：

```yaml
name: mlc
main: com.mlc.mlc.Mlc
depend: ["PlaceholderAPI", "Vault"]
softdepend: ["BlueMap"]
```

`com.mlc.mlc.Mlc` 的 `onEnable()` 会手动初始化功能模块：

```java
MlcDomain.init(this);
MlcWaystone.init(this);
MlcStyte.init(this);
```

模块中的 Listener、Command、Placeholder 等注册逻辑应由各自的 `init(JavaPlugin plugin)` 入口完成。Gradle 依赖只负责编译和打包，不会自动执行模块代码。

## 5. 新建一个被主插件打包的功能模块

下面以 `mlc-auction` 为例。

### 5.1 创建目录

```text
mlc-auction/
├── build.gradle.kts
└── src/
    └── main/
        ├── java/
        │   └── com/mlc/mlcauction/
        └── resources/
```

如果需要测试，使用标准测试目录：

```text
mlc-auction/src/test/java/
```

不需要在 `settings.gradle.kts` 中单独注册 `test` 模块。

### 5.2 注册 Gradle 模块

在 `settings.gradle.kts` 添加：

```kotlin
include(":mlc-auction")
```

### 5.3 编写模块构建脚本

```kotlin
// mlc-auction/build.gradle.kts

dependencies {
    // 仅编译需要、由服务器提供的插件 API
    compileOnly(libs.vault) {
        exclude("org.bukkit", "bukkit")
    }

    // 如果第三方库需要随 mlc 一起发布，则使用 implementation
    // implementation(libs.someLibrary)
}
```

### 5.4 将模块加入主插件

编辑 `mlc-core/build.gradle.kts`：

```kotlin
dependencies {
    implementation(project(":mlc-domain"))
    implementation(project(":mlc-waystone"))
    implementation(project(":mlc-styte"))
    implementation(project(":mlc-auction"))
}
```

这是让主插件能够编译并打包新模块的关键步骤。

### 5.5 添加模块入口

建议每个功能库提供一个静态初始化入口：

```java
package com.mlc.mlcauction;

import org.bukkit.plugin.java.JavaPlugin;

public final class MlcAuction {

    private MlcAuction() {
    }

    public static void init(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(
                new AuctionListener(),
                plugin
        );
        plugin.getLogger().info("mlc-auction loaded");
    }
}
```

然后在 `mlc-core` 的 `Mlc.onEnable()` 中调用：

```java
MlcAuction.init(this);
```

### 5.6 添加资源文件

模块资源放在：

```text
mlc-auction/src/main/resources/
```

资源会随模块进入最终 JAR。不同模块应避免使用相同的资源路径，以免打包时互相覆盖。

## 6. 独立插件模块与功能库模块的区别

当前项目采用的是“一个主插件 + 多个功能库模块”的模式。

如果新模块需要作为独立 Paper 插件安装，则不能只添加 `implementation(project(...))`。它还需要：

- 独立的 `plugin.yml`
- 自己的 `JavaPlugin` 主类
- 独立的 Shadow/发布配置
- 明确的插件依赖和安装方式

除非确实需要独立安装，否则建议沿用当前项目的功能库模块模式。

## 7. `mlc-craftengine` 当前状态

`settings.gradle.kts` 已经声明：

```kotlin
include(":mlc-craftengine")
```

但当前工作区没有以下目录和文件：

```text
mlc-craftengine/
mlc-craftengine/build.gradle.kts
```

需要二选一：

1. 确实开发 CraftEngine 功能：创建目录、构建脚本，并在 `mlc-core` 中添加项目依赖。
2. 暂时不使用：从 `settings.gradle.kts` 删除 `include(":mlc-craftengine")`。

否则 Gradle 或 IDE 同步时可能出现空项目或项目目录不存在的问题。

## 8. 构建命令

Windows：

```powershell
.\gradlew.bat projects
.\gradlew.bat :mlc-core:build
.\gradlew.bat build
```

完整构建会生成：

```text
target/mlc-版本.jar
```

构建前需要配置 Java 21，并确保 `JAVA_HOME` 指向 JDK 21。

