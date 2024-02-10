group = "nl.helicotech.wired.examples.basic"

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    id("nl.helicotech.wired")
}

wired {
    packageName.set("nl.helicotech.wired.example.assets")

    vendor("@hotwired/turbo", "7.3.0")
    vendor("@hotwired/stimulus", "3.2.2")
    vendor("zod", "3.22.4")
}

application {
    mainClass.set("nl.helicotech.wired.example.ApplicationKt")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=true")
}

dependencies {
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.html.builder)
    implementation(libs.logback.classic)

    implementation(project(":library:core"))

    testImplementation(libs.ktor.server.tests)
    testImplementation(libs.kotlin.test.junit)
}
