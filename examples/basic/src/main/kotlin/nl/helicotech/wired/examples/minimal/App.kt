package nl.helicotech.wired.examples.minimal

import io.ktor.server.application.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import kotlinx.html.*
import nl.helicotech.wired.assetmapper.fileName
import nl.helicotech.wired.assetmapper.hashedFile
import nl.helicotech.wired.assetmapper.traverseFiles
import nl.helicotech.wired.examples.minimal.assets.Assets
import nl.helicotech.wired.examples.minimal.assets.Vendors
import nl.helicotech.wired.ktor.assetmapper.assets

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
        assets(Assets, Vendors)

        get("/") {
            call.respondHtml {
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