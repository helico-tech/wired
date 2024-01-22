package nl.helicotech.wired

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File
import kotlin.io.path.exists
import kotlin.io.path.toPath

class AssetsPluginConfiguration {
    var assetFolder: AssetFolder? = null
}

val AssetsPlugin = createApplicationPlugin("assets", ::AssetsPluginConfiguration) {

    requireNotNull(pluginConfig.assetFolder) {
        "The asset folder is required for the assets plugin"
    }

    val assetPathName = pluginConfig.assetFolder!!.path.name


    application.routing {
        get(Regex("(?<path>${assetPathName}/.*)-(?<hash>[0-9a-fA-F]{40}).(?<ext>.*)")) {
            val path = call.parameters["path"]!!
            val hash = call.parameters["hash"]!!
            val ext = call.parameters["ext"]!!

            val actualName = "$path.$ext"

            val asset = this@createApplicationPlugin.pluginConfig.assetFolder!!.traverse().firstOrNull {
                it.path.path == actualName && it.hash == hash
            }

            if (asset == null) {
                call.respond(HttpStatusCode.NotFound)
                return@get
            }

            val uri = application.environment.classLoader.getResource(asset.path.toString())?.toURI()

            if (uri == null) {
                call.respond(HttpStatusCode.NotFound)
                return@get
            }

            val file = File(uri)
            if (file.exists()) {
                call.respondFile(file)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}