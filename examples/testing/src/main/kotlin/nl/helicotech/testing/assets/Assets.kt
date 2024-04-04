package nl.helicotech.testing.assets

object Assets : AssetContainer(logicalPath = "assets", mountPath = "static") {

    val AppJs = Asset.JavaScript(module = "app",  logicalPath = "app.js", digest = "xxx", container = Assets, dependencies = { listOf(DuckJs) })

    val DuckJs = Asset.JavaScript(logicalPath = "duck.js", digest = "yyy", container = Assets)

    object Vendors : AssetContainer(logicalPath = "vendors", parent = Assets) {
        object Hotwired : AssetContainer(logicalPath = "@hotwired", parent = Vendors) {
            val Turbo = Asset.JavaScript(module = "@hotwired/turbo", logicalPath = "turbo.js", digest = "zzz", container = Hotwired)
        }
    }

    override val assets: List<Asset> = listOf(AppJs, DuckJs)

    override val containers: List<AssetContainer> = listOf(Vendors)
}