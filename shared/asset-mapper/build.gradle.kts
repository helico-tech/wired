plugins {
    kotlin("jvm")
}

dependencies {
    implementation(libs.kotlin.poet)
    testImplementation(libs.kotest.junit)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}