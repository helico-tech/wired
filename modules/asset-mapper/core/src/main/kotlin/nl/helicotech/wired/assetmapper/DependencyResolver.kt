package nl.helicotech.wired.assetmapper

interface DependencyResolver : AssetHandler {
    fun resolveDependencies(asset: Asset): Set<Asset>
}