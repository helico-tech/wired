package nl.helicotech.wired.assetmapper

interface AssetHandler {
    fun accepts(asset: Asset): Boolean
}