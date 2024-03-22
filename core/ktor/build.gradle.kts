plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    api(libs.ktor.server.core)
    api(libs.ktor.server.websockets)
    api(libs.kotlinx.html)
    api("nl.helicotech.wired.shared:asset-mapper")
}