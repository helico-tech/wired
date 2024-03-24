package nl.helicotech.wired.plugin.assetmapper.transformer

import nl.helicotech.wired.assetmapper.Asset

interface Transformer {
    fun accepts(asset: Asset.File): Boolean
    fun transform(asset: Asset.File): String
}