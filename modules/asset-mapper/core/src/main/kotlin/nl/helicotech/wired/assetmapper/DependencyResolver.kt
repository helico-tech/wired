package nl.helicotech.wired.assetmapper


interface DependencyResolver : AssetHandler {
    fun resolve(asset: Asset): Set<Asset>
}