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
    implementation("nl.helicotech.wired.shared:vendors")

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlin.poet)
    implementation(libs.ktor.http)

    testImplementation(libs.kotlin.test.junit)
    testImplementation(kotlin("test"))
}