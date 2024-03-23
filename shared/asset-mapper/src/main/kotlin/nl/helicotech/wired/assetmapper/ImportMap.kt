package nl.helicotech.wired.assetmapper

import io.ktor.http.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


@Serializable
data class ImportMap(
    val imports: Map<String, String>,
) {
    constructor(vararg assets: Asset) : this(assets.asIterable().ofType(ContentType.Application.JavaScript).associate { it.importMapKey() to "/${it.url()}" })
    fun toJson(): String = Json.encodeToString(this)
}