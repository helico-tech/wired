plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    implementation(libs.ktor.server.core)

    api("nl.helicotech.wired.shared:asset-mapper")
}