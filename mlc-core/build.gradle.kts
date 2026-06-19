plugins {
    alias(libs.plugins.shadow)
}

dependencies {
    compileOnly(libs.vault) {
        exclude("org.bukkit", "bukkit")
    }
    compileOnly(libs.placeholderapi)
    implementation(libs.adventure.minimessage)
    implementation(libs.hikari)
    implementation(libs.sqlite)

    // Bundle subproject code into this plugin
    implementation(project(":mlc-domain"))
    implementation(project(":mlc-waystone"))
    implementation(project(":mlc-styte"))
}

tasks {
    shadowJar {
        relocate("com.zaxxer.hikari", "com.mlc.lib.hikari")
        // SQLite 不能用 relocate，因为它的 JNI 原生库无法重定位
        // relocate("org.sqlite", "com.mlc.lib.sqlite")
    }
    build {
        dependsOn("copyJar")
    }
    processResources {
        inputs.property("version", version)
        filesMatching("**/plugin.yml") {
            expand(project.properties)
        }
    }
}

// Copy shadow jar to target/
tasks.register<Copy>("copyJar") {
    dependsOn(tasks.shadowJar)
    from(tasks.shadowJar.flatMap { it.archiveFile })
    into(rootProject.layout.projectDirectory.dir("target"))
    rename { "mlc-${project.version}.jar" }
}
