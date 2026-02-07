plugins {
    `java-library`
    alias(libs.plugins.shadow)
    application
}

application {
    mainClass.set("hu.metavex.broadcaster.app.Main")
}

dependencies {
    implementation(project(":core"))
    implementation(project(":wrapper"))
    implementation(project(":installer"))

    implementation(libs.terminalconsoleappender)
    implementation(libs.bundles.jline)
    implementation(libs.log4j.core)
    implementation(libs.log4j.slf4j2.impl)
    implementation(libs.configurate.yaml)
}

tasks.shadowJar {
    manifest {
        attributes["Main-Class"] = "hu.metavex.broadcaster.app.Main"
    }
    // Merge Log4j2 plugin descriptor files from all dependencies
    transform(com.github.jengelman.gradle.plugins.shadow.transformers.Log4j2PluginsCacheFileTransformer::class.java)
}
