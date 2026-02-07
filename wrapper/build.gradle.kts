plugins {
    `java-library`
}

dependencies {
    api(libs.log4j.api)
    api(libs.configurate.`interface`)
    implementation(libs.configurate.yaml)
    implementation(project(":core"))
}
