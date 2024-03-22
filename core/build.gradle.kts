allprojects {
    repositories {
        mavenCentral()
    }
    version = "0.0.1"
    group = "nl.helicotech.wired.core"
}

plugins {
    alias(libs.plugins.kotlin.jvm) apply false
}

