package nl.helicotech.testing.assets

import kotlinx.html.HEAD
import kotlinx.html.script
import kotlinx.html.unsafe

/*data class ImportMap(
    val imports: Map<String, String>
) {
    override fun toString(): String {
        return """{"imports": {${imports.entries.joinToString(",") { (key, value) -> "\"$key\": \"$value\"" }}}}"""
    }

    companion object {
        fun fromJavascriptAsset(asset: Asset.JavaScript): ImportMap {
            val imports = mutableMapOf<String, String>()

            imports[asset.module ?: asset.absoluteLogicalPath.toString()] = "/${asset.absoluteMountPath}"

            asset.dependencies().forEach { dependency ->
                if (dependency is Asset.JavaScript) {
                    imports[dependency.module ?: "/${dependency.absoluteLogicalPath}"] = "/${dependency.absoluteMountPath}"
                }
            }

            return ImportMap(imports)
        }

        fun merge(vararg importMaps: ImportMap): ImportMap {
            val imports = mutableMapOf<String, String>()

            importMaps.forEach { importMap ->
                imports.putAll(importMap.imports)
            }

            return ImportMap(imports)
        }
    }
}

fun HEAD.importMap(vararg assets: Asset.JavaScript) {

    val importMaps = assets.map { ImportMap.fromJavascriptAsset(it) }

    val importMap = ImportMap.merge(*importMaps.toTypedArray())

    val dependencies = assets.flatMap { it.dependencies() }.distinct()

    script(type = "importmap") { unsafe { +importMap.toString() } }

    dependencies.forEach { dependency ->
        preload(dependency)
    }

    assets.forEach {asset ->
        if (asset.module != null) {
            module(asset)
        }
    }
}*/