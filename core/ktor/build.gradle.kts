plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    api(libs.ktor.server.core)
    api(libs.ktor.server.html.builder)
    api("nl.helicotech.wired.shared:asset-mapper")
}