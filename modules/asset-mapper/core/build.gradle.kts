plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":data"))
    implementation(libs.ktor.http)
    testImplementation(libs.kotest.junit)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}