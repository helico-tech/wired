package nl.helicotech.wired.assetmapper.js

import io.ktor.http.*
import nl.helicotech.wired.assetmapper.Asset
import nl.helicotech.wired.assetmapper.AssetResolver
import nl.helicotech.wired.assetmapper.DependencyResolver

class JavascriptDependencyResolver(
    private val assetResolver: AssetResolver
) : DependencyResolver {

    override fun accepts(asset: Asset): Boolean {
        return asset.contentType.match(ContentType.Application.JavaScript)
    }

    override fun resolve(asset: Asset): Set<Asset> {
        val sourceHandler = JavascriptSourceHandler(asset.sourceFile.readLines())

        val imports = sourceHandler.getImports()

        val assets = imports.map { it to assetResolver.resolveRelative(asset, it) }

        val missing = assets.filter { it.second == null }.map { it.first }

        require(missing.isEmpty()) { "Missing dependencies: $missing" }

        return assets.mapNotNull { it.second }.toSet()
    }
}