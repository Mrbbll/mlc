plugins {
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.24"
}

// Still a library bundled into mlc-core; server internals are compile-time only.
dependencies {
    paperweight.paperDevBundle("26.3.build.+")
    implementation(libs.adventure.serializer.plain)
    compileOnly(libs.bluemap)
}
