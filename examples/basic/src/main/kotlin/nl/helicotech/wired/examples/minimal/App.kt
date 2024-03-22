package nl.helicotech.wired.examples.minimal

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.html.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import kotlinx.html.*
import nl.helicotech.wired.assetmapper.fileName
import nl.helicotech.wired.assetmapper.hashedFile
import nl.helicotech.wired.assetmapper.traverseFiles
import nl.helicotech.wired.examples.minimal.assets.Assets
import nl.helicotech.wired.examples.minimal.assets.Vendors
import nl.helicotech.wired.ktor.assetmapper.staticTypedAssets
import nl.helicotech.wired.ktor.assetmapper.importMap
import nl.helicotech.wired.ktor.assetmapper.module

fun main() {
    embeddedServer(
        factory = Netty,
        port = 8080,
        host = "0.0.0.0",
        module = Application::minimal,
    ).start(wait = true)
}

fun Application.minimal() {
    routing {
        staticTypedAssets(Assets, Vendors)

        get("/") {
            call.respondHtml {
                head {
                    importMap(Assets, Vendors)
                    module(Assets.Js.My_script_js)
                }

                body {
                    h1 {
                        + "Wired Example"
                    }

                    h2 { + "Typed assets" }

                    listOf(Assets, Vendors).forEach { directory ->
                        h3 { + directory.fileName() }

                        ul {
                            directory.traverseFiles().forEach {
                                li {
                                    a(href = it.hashedFile().path) { + it.file.name }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}