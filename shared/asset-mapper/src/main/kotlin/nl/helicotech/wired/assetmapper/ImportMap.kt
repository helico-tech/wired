package nl.helicotech.wired.assetmapper

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


@Serializable
data class ImportMap(
    val imports: Map<String, String>,
) {
    constructor(vararg assets: Asset) : this(assets.asIterable().traverseFiles().associate { it.importMapKey() to it.importMapValue() })
    fun toJson(): String = Json.encodeToString(this)
}