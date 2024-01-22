allprojects {
    repositories {
        mavenCentral()
    }

    version = "0.0.1"
    group = "nl.helicotech.ktorwired.${project.name}"

    println("Project: ${project.name} - Version: ${project.version} - Group: ${project.group}")
}

plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlinx.serialization) apply false
    alias(libs.plugins.ktor) apply false
}