plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    id("nl.helicotech.wired.plugin")
}

wired {
    assetMapper {
        packageName.set("nl.helicotech.wired.examples.minimal.assets")

        include("src/main/resources/assets")
        include("build/wired/vendors")
    }

    vendors {
        include("@hotwired/turbo", "8.0.4")
        include("@hotwired/stimulus", "3.2.2")
    }
}

application {
    mainClass.set("nl.helicotech.wired.examples.minimal.AppKt")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=true")
}

dependencies {
    implementation("nl.helicotech.wired:wired-core")

    implementation(libs.logback.classic)
    implementation(libs.ktor.server.netty)
}