package nl.helicotech.wired.assetmapper

class RootDependencyResolver(
    private val resolvers: List<DependencyResolver>
) : DependencyResolver {

    override fun accepts(asset: Asset): Boolean {
        return resolvers.any { it.accepts(asset) }
    }

    override fun resolve(asset: Asset): Set<Dependency> {
        val activeAssets = mutableSetOf(asset)

        val resolvedDependencies = mutableSetOf<Dependency>()

        val assetsToResolve = ArrayDeque<Asset>()

        assetsToResolve.add(asset)

        while (assetsToResolve.isNotEmpty()) {
            val currentAsset = assetsToResolve.removeFirst()

            val resolver = resolvers.first { it.accepts(currentAsset) }

            val dependencies = resolver.resolve(currentAsset)

            require(dependencies.none { activeAssets.contains(it.to) }) { "Circular dependency detected: $currentAsset -> $dependencies" }

            resolvedDependencies.addAll(dependencies)

            activeAssets.addAll(dependencies.map { it.to })

            assetsToResolve.addAll(dependencies.map { it.to })
        }

        return resolvedDependencies
    }
}