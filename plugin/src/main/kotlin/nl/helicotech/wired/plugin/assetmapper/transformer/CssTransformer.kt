package nl.helicotech.wired.plugin.assetmapper.transformer

import io.ktor.http.*
import nl.helicotech.wired.assetmapper.Asset

class CssTransformer(val assets: List<Asset>) : Transformer {
    override fun accepts(asset: Asset.File): Boolean {
        return asset.contentType.match(ContentType.Text.CSS)
    }

    override fun transform(asset: Asset.File): String {
        return asset.file.readText()
    }
}