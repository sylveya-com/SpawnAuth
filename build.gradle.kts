plugins {
    `java`
    alias(libs.plugins.shadow)
    alias(libs.plugins.attache)
}

group = "me.lokspel"
version = "1.1.6"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.codemc.org/repository/maven-public/")
    maven("https://repo.nickuc.com/maven-releases/")
    maven("https://repo.tcoded.com/releases")
    maven("https://repo.faststats.dev/releases")
    maven("https://maven.whereareiam.me/release")
}

dependencies {
    // Platform API (provided by server)
    compileOnly(libs.paper.api)

    // Soft dependencies (provided by server plugins)
    compileOnly(libs.authme)
    compileOnly(libs.nlogin)
    compileOnly(libs.openlogin)
    compileOnly(files("libs/LoginSecurity-Spigot-3.3.1.jar"))
    compileOnly(files("libs/LoginTo-4.1.0.jar"))

    // Shaded dependencies
    implementation(libs.bstats.bukkit)
    implementation(libs.faststats.bukkit)
    implementation(libs.folialib)
    implementation(libs.bundles.attache)

}

attache {
    group("sqlite") {
        library(libs.sqlite.jdbc)
    }

    group("mysql") {
        library(libs.mysql.connector)
        library(libs.hikaricp)
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

tasks.processResources {
    filteringCharset = "UTF-8"
    val props = mapOf(
        "project" to mapOf("version" to project.version),
        "version" to project.version
    )
    inputs.properties(props)
    filesMatching("plugin.yml") {
        expand(props)
    }
}

tasks.shadowJar {
    archiveBaseName.set("SpawnAuth")
    archiveClassifier.set("")

    relocate("org.bstats", "me.lokspel.spawnauth.thirdparty")
    relocate("dev.faststats", "me.lokspel.spawnauth.thirdparty.faststats")
    relocate("com.tcoded.folialib", "me.lokspel.spawnauth.thirdparty.folialib")
    relocate("me.whereareiam.attache", "me.lokspel.spawnauth.thirdparty.attache")

    exclude("META-INF/*.SF")
    exclude("META-INF/*.DSA")
    exclude("META-INF/*.RSA")
    exclude("META-INF/versions/*/module-info.class")
    exclude("module-info.class")
}

tasks.jar {
    enabled = false
}
