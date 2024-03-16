allprojects {
    repositories {
        mavenCentral()
    }

    version = "0.0.1"
    group = "nl.helicotech.wired.examples"
}

plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.ktor) apply false
    alias(libs.plugins.kotlinx.serialization) apply false
}