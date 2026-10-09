plugins {
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.24"
}

val mockitoAgent = configurations.create("mockitoAgent") {
    isTransitive = false
}

// Server internals are compile-time only; no Citizens/PracticeBot plugin is required.
dependencies {
    paperweight.paperDevBundle("26.3.build.+")
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    testImplementation("org.mockito:mockito-core:5.24.0")
    mockitoAgent("org.mockito:mockito-core:5.24.0")
    // Native ground-state references are needed only by the mocked combat-path tests.
    testRuntimeOnly(files(sourceSets.main.get().compileClasspath))
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
    jvmArgumentProviders.add(CommandLineArgumentProvider {
        listOf("-javaagent:${mockitoAgent.singleFile.absolutePath}")
    })
}
