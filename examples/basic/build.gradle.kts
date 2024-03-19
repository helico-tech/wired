plugins {
    alias(libs.plugins.kotlin.jvm)
    id("nl.helicotech.wired.plugin")
}

wired {
    assetMapper {
        include("src/main/resources/assets")
    }

    vendors {
        include("@hotwired/turbo", "8.0.4")
    }
}