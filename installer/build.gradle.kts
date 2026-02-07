plugins {
    `java-library`
}

dependencies {
    api(libs.methanol)
    api(libs.gson)
    api(libs.log4j.api)
    implementation(project(":core"))
}
