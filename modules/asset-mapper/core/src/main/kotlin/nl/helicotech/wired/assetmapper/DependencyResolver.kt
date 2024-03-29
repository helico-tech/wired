package nl.helicotech.wired.assetmapper


interface DependencyResolver : AssetHandler {
    fun resolve(source: Collection<String>): Set<String>
}