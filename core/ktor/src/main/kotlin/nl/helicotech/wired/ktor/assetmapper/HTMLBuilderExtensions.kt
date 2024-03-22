package nl.helicotech.wired.ktor.assetmapper

import kotlinx.html.HEAD
import kotlinx.html.HtmlHeadTag
import kotlinx.html.script
import kotlinx.html.unsafe
import nl.helicotech.wired.assetmapper.Asset
import nl.helicotech.wired.assetmapper.ImportMap
import nl.helicotech.wired.assetmapper.importMapKey

fun HtmlHeadTag.importMap(importMap: ImportMap) {
    script(type = "importmap") {
        unsafe {
            +importMap.toJson()
        }
    }
}

fun HtmlHeadTag.importMap(vararg asset: Asset) {
    importMap(ImportMap(*asset))
}

fun HtmlHeadTag.module(module: Asset.File) {
    script(type = "module") {
        unsafe {
            + """import '${module.importMapKey()}';"""
        }
    }
}