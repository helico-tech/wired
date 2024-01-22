repositories {
    gradlePluginPortal()
    mavenCentral()
}

plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
}

sourceSets {
    main {
        kotlin {
            srcDir("src/main/kotlin")
        }
    }
}

gradlePlugin {
    plugins {
        create("wiredPlugin") {
            id = "nl.helicotech.wired"
            implementationClass = "nl.helicotech.wired.plugin.WiredPlugin"
        }
    }
}

dependencies {
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.gson)
    implementation(libs.gson)
    implementation(libs.kotlin.poet)

    testImplementation(libs.kotlin.test.junit)
    testImplementation(kotlin("test"))
}