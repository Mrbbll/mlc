plugins {
    `java-library`
    alias(libs.plugins.runPaper)
    alias(libs.plugins.shadow)
}

// ============================================================
// Auto-increment patch version (每次构建版本号+1)
// ============================================================
val versionPropsFile = file("version.properties")
var currentVersion = "1.0.0"
if (versionPropsFile.exists()) {
    versionPropsFile.readLines().forEach { line ->
        if (line.startsWith("version=")) {
            currentVersion = line.substringAfter("version=").trim()
        }
    }
}
val parts = currentVersion.split(".").map { it.toInt() }
val newVersion = "${parts[0]}.${parts[1]}.${parts[2] + 1}"
versionPropsFile.writeText("version=$newVersion\n")

// Root project produces no jar
tasks.withType<Jar> {
    enabled = false
}

// ============================================================
// Common settings for ALL subprojects
// ============================================================
subprojects {
    apply(plugin = "java-library")
    apply(plugin = "java")

    group = "com.mlc"
    version = newVersion

    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://repo.extendedclip.com/releases/")
        maven("https://jitpack.io")
        maven("https://repo.bluecolored.de/releases/")
    }

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.compilerArgs.add("-Xlint:deprecation")
        options.release.set(21)
    }

    // All subprojects need Paper API
    dependencies {
        "compileOnly"("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    }
}

// ============================================================
// runServer configuration
// ============================================================
tasks {
    runServer {
        minecraftVersion("1.21.8")
    }
}
