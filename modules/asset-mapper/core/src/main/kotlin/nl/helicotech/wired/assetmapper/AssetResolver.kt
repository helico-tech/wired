package nl.helicotech.wired.assetmapper

interface AssetResolver {
    fun resolve(path: String): Asset?

    fun resolveRelative(from: Asset, path: String): Asset?

    fun resolveRelative(from: String, path: String): Asset?
}