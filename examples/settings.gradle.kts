rootProject.name = "wired-examples"

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

//includeBuild("../shared")
//include("basic")
include("testing")