package nl.helicotech.wired.assetmapper

class RootDependencyResolver(
    private val resolvers: List<DependencyResolver>
) : DependencyResolver {

    override fun accepts(asset: Asset): Boolean {
        return resolvers.any { it.accepts(asset) }
    }

    override fun resolve(asset: Asset): Set<Asset> {
        val resolvedAssets = mutableSetOf(asset)

        val assetsToResolve = ArrayDeque<Asset>()
        assetsToResolve.add(asset)

        while (assetsToResolve.isNotEmpty()) {
            val currentAsset = assetsToResolve.removeFirst()

            val resolver = resolvers.first { it.accepts(currentAsset) }
            val dependencies = resolver.resolve(currentAsset)

            require(dependencies.none { it == currentAsset }) { "Circular dependency detected: $currentAsset -> $dependencies" }

            resolvedAssets.addAll(dependencies)

            assetsToResolve.addAll(dependencies)
        }

        return resolvedAssets
    }
}