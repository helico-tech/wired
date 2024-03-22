package nl.helicotech.wired.ktor.assetmapper

import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import nl.helicotech.wired.assetmapper.Asset
import nl.helicotech.wired.assetmapper.fileName

fun Routing.asset(asset: Asset, basePackage: String = "asset-mapper") {
    staticResources("/${asset.fileName()}", "${basePackage}/${asset.fileName()}")
}

fun Routing.assets(vararg assets: Asset, basePackage: String = "asset-mapper") {
    assets.forEach { asset(it, basePackage) }
}