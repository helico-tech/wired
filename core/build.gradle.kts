allprojects {
    repositories {
        mavenCentral()
    }
    version = "0.1.0"
    group = "nl.helicotech.wired.core"
}

plugins {
    alias(libs.plugins.kotlin.jvm) apply false
}

