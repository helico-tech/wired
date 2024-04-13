plugins {
    kotlin("jvm")
}

dependencies {
    implementation(libs.ktor.http)

    testImplementation(libs.kotest.junit)
}

tasks.test {
    useJUnitPlatform()
}