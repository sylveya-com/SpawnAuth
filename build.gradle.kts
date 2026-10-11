plugins {
    `java`
    id("com.gradleup.shadow") version "9.6.1"
    id("me.whereareiam.attache") version "1.0.0"
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
    compileOnly("io.papermc.paper:paper-api:[26.2.build,)")

    // Soft dependencies (provided by server plugins)
    compileOnly("fr.xephi:authme:5.7.0-SNAPSHOT")
    compileOnly("com.nickuc.login:api:2.0")
    compileOnly("com.nickuc.openlogin:openlogin-universal:1.3")
    compileOnly(files("libs/LoginSecurity-Spigot-3.3.1.jar"))
    compileOnly(files("libs/LoginTo-4.1.0.jar"))

    // Shaded dependencies
    implementation("org.bstats:bstats-bukkit:3.2.1")
    implementation("dev.faststats.metrics:bukkit:0.30.1")
    implementation("com.tcoded:FoliaLib:0.5.1")
    implementation("me.whereareiam:attache-bukkit:1.0.0")
    implementation("me.whereareiam:attache-common:1.0.0")

    // Dependencies loaded at runtime by Attache (NOT shaded)
    attache("com.mysql:mysql-connector-j:9.4.0")
    attache("org.xerial:sqlite-jdbc:3.45.1.0")
    attache("com.zaxxer:HikariCP:7.1.0")
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

tasks.build {
    dependsOn(tasks.shadowJar)
}
