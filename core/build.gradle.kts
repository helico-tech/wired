repositories {
    mavenCentral()
}

version = "0.1.0"
group = "nl.helicotech.wired"

plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    api("nl.helicotech.wired.shared:asset-mapper")

    api(libs.ktor.server.core)
    api(libs.ktor.server.html.builder)
    api(libs.ktor.server.websockets)
}