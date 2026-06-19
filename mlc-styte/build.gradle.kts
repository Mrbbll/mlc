// mlc-styte is a library, bundled into mlc-core
dependencies {
    compileOnly(libs.vault) {
        exclude("org.bukkit", "bukkit")
    }
    compileOnly(libs.adventure.serializer.legacy)
}
