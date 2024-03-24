package nl.helicotech.wired.plugin.assetmapper.transformer

import nl.helicotech.wired.assetmapper.Asset

class UnitTransformer : Transformer {
    override fun accepts(asset: Asset.File): Boolean {
        return true
    }

    override fun transform(asset: Asset.File): String {
        return asset.file.readText()
    }
}