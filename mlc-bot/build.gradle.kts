plugins {
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.24"
}

// Server internals are compile-time only; no Citizens/PracticeBot plugin is required.
dependencies {
    paperweight.paperDevBundle("26.3.build.+")
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}
