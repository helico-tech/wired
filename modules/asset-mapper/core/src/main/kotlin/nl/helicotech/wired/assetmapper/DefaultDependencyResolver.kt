package nl.helicotech.wired.assetmapper

import io.ktor.http.*

abstract class AbstractDependencyResolver(
    private val assetResolver: AssetResolver,
    private val sourceHandlerFactory: SourceHandler.Factory
) : DependencyResolver {

    override fun resolve(asset: Asset): Set<Asset> {
        val sourceHandler = sourceHandlerFactory.create(asset.sourceFile.readLines())

        val imports = sourceHandler.getSourceDependencies()

        val assets = imports.map { it to assetResolver.resolveRelative(asset, it) }

        val missing = assets.filter { it.second == null }.map { it.first }

        require(missing.isEmpty()) { "Missing dependencies: $missing" }

        return assets.mapNotNull { it.second }.toSet()
    }
}

