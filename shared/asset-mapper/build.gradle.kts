plugins {
    kotlin("jvm")
    alias(libs.plugins.kotlinx.serialization)
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.ktor.http)
    testImplementation(libs.kotest.junit)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}