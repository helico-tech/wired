plugins {
    alias(libs.plugins.kotlin.jvm)
    id("nl.helicotech.wired.plugin")
}

wired {
    assetMapper {
        packageName.set("my.package.assets")

        include("src/main/resources/assets")
        include("build/wired/vendors")
    }

    vendors {
        include("@hotwired/turbo", "8.0.4")
        include("@hotwired/stimulus", "3.2.2")
    }
}