package nl.helicotech.wired.assetmapper

import kotlin.io.path.Path
import kotlin.io.path.readLines

abstract class AbstractDependencyResolver(
    private val assetResolver: AssetResolver,
    private val sourceHandlerFactory: SourceHandler.Factory
) : DependencyResolver {

    override fun resolve(asset: Asset): Set<Dependency> {
        return setOf()
        val sourceHandler = sourceHandlerFactory.create(asset.absoluteLogicalPath.readLines())

        val imports = sourceHandler.getSourceDependencies()

        val assets = imports.map { it to assetResolver.resolveRelative(asset, Path(it)) }

        val missing = assets.filter { it.second == null }.map { it.first }

        require(missing.isEmpty()) { "Missing dependencies: $missing" }

        return assets.map { Dependency(asset, it.second!!, Path(it.first)) }.toSet()
    }
}

