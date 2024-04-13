plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":core"))
    implementation(libs.kotlin.poet)
    testImplementation(libs.kotest.junit)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}