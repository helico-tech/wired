group = "nl.helicotech.wired.gradle-plugin"

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
    implementation("nl.helicotech.wired.shared:vendors")

    implementation(libs.kotlinx.coroutines.core)

    testImplementation(libs.kotlin.test.junit)
    testImplementation(kotlin("test"))
}