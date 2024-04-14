plugins {
    kotlin("jvm")
}

dependencies {
    api(project(":data"))
    implementation(libs.ktor.http)
    implementation(libs.kotlin.poet)
    testImplementation(libs.kotest.junit)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}