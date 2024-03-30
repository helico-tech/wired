package nl.helicotech.wired.assetmapper


interface DependencyResolver : AssetHandler {
    fun resolve(asset: Asset): Set<Dependency>

    fun interface Factory {
        fun create(assetResolver: AssetResolver): DependencyResolver
    }
}