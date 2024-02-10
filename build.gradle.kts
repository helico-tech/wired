allprojects {
    repositories {
        mavenCentral()
    }
    version = "0.0.1"
}

plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlinx.serialization) apply false
    alias(libs.plugins.ktor) apply false
}