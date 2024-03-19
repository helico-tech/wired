plugins {
    alias(libs.plugins.kotlin.jvm)
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

dependencies {
    implementation("nl.helicotech.wired.shared:asset-mapper")
}