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
}

tasks.shadowJar {
    manifest {
        attributes["Main-Class"] = "hu.metavex.broadcaster.app.Main"
    }
}
