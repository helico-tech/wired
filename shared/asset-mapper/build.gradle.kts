plugins {
    kotlin("jvm")
}

dependencies {
    testImplementation(libs.kotest.junit)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}