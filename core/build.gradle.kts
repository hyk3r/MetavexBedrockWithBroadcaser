plugins {
    `java-library`
}

dependencies {
    api(libs.gson)
    api(libs.nimbus.jose.jwt)
    api(libs.java.websocket)
    api(libs.methanol)
    api(libs.minecraftauth)
    api(libs.bundles.protocol)
    api(libs.netty.transport.nethernet)
    api(libs.webrtc)
    
    // Configurate for configuration management
    api(libs.configurate.`interface`)
    implementation(libs.configurate.yaml)

    // Logging
    api(libs.log4j.api)
}
