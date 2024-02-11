package nl.helicotech.wired

import io.ktor.server.application.*
import io.ktor.util.*
import io.ktor.util.pipeline.*
import kotlinx.html.HEAD
import kotlinx.html.script
import kotlinx.html.unsafe
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class ImportMap(
    val imports: Map<String, String>,
) {
    fun toJson(): String = Json.encodeToString(this)

    companion object {
        val Key = AttributeKey<ImportMap>("importMap")

        operator fun invoke(vararg assets: Asset): ImportMap {
            val allAssets = assets.asIterable().all()
            val imports = allAssets.associate { it.name to "/" + it.pathWithHash }
            return ImportMap(imports)
        }
    }
}
