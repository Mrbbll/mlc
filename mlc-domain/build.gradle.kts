// mlc-domain is a library, bundled into mlc-core's shadow jar
// Compiles against original HikariCP — relocation happens in mlc-core's shadowJar
dependencies {
    compileOnly(libs.vault) {
        exclude("org.bukkit", "bukkit")
    }
    compileOnly(libs.placeholderapi)
    compileOnly(libs.bluemap)
    compileOnly(libs.hikari)
    compileOnly(libs.sqlite)
}
