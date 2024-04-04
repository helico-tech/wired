package nl.helicotech.wired.assetmapper.js

import io.ktor.http.*
import nl.helicotech.wired.assetmapper.AbstractDependencyResolver
import nl.helicotech.wired.assetmapper.AssetResolver
import nl.helicotech.wired.assetmapper.DependencyResolver

class JavascriptDependencyResolver(
    assetResolver: AssetResolver
) : AbstractDependencyResolver(
    assetResolver = assetResolver,
    sourceHandlerFactory = JavascriptSourceHandler
) {
    override fun accepts(asset: Asset): Boolean {
        return asset.contentType.match(ContentType.Application.JavaScript)
    }

    companion object : DependencyResolver.Factory {
        override fun create(assetResolver: AssetResolver): JavascriptDependencyResolver {
            return JavascriptDependencyResolver(assetResolver)
        }
    }
}