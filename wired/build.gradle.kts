group = "nl.helicotech.wired.ktor"

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinx.serialization)
}

dependencies {
    implementation(libs.ktor.server.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.html)
}