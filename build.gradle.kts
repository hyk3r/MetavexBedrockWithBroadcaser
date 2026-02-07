plugins {
    `java-library`
    alias(libs.plugins.shadow) apply false
}

allprojects {
    group = "hu.metavex"
    version = "1.0.0-SNAPSHOT"

    repositories {
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://repo.opencollab.dev/main/")
        maven("https://repo.opencollab.dev/maven-snapshots/")
        maven("https://repo.opencollab.dev/maven-releases/")
        // maven("https://repo.geysermc.org/releases")
        // maven("https://repo.geysermc.org/snapshots")
        // maven("https://repo.raphimc.net/repository/maven-snapshots/")
        // maven("https://repo.raphimc.net/repository/maven-releases/")
        maven("https://repo.viaversion.com")
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://mvn.lumine.io/repository/maven-public/")
    }
}

subprojects {
    apply(plugin = "java-library")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(21)
    }
}
