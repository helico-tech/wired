rootProject.name = "wired-core"

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

includeBuild("../shared")

include("ktor")
include("kotlinx-html-turbo")