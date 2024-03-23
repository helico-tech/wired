group = "nl.helicotech.wired"

repositories {
    gradlePluginPortal()
    mavenCentral()
}

plugins {
    alias(libs.plugins.kotlin.jvm)
    `java-gradle-plugin`
}

gradlePlugin {
    plugins {
        create("wiredPlugin") {
            id = "nl.helicotech.wired.plugin"
            implementationClass = "nl.helicotech.wired.plugin.WiredPlugin"
        }
    }
}

dependencies {
    implementation("nl.helicotech.wired.shared:asset-mapper")

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlin.poet)
    implementation(libs.ktor.http)

    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.gson)
    implementation(libs.gson)

    testImplementation(libs.kotlin.test.junit)
    testImplementation(kotlin("test"))
}