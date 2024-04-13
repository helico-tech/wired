rootProject.name = "asset-mapper"

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../../gradle/libs.versions.toml"))
        }
    }
}

include("data")
include("core")
include("server")
include("codegen")