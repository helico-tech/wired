allprojects {
    repositories {
        mavenCentral()
    }

    version = "0.0.1"
    group = "nl.helicotech.wired.examples"
}

plugins {
    alias(libs.plugins.kotlin.jvm) apply false
}