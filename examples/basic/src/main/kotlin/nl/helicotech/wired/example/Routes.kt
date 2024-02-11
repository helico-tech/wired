package nl.helicotech.wired.example

import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.routing.*
import kotlinx.html.*
import nl.helicotech.wired.AssetsPlugin
import nl.helicotech.wired.example.assets.Assets
import nl.helicotech.wired.importMap
import nl.helicotech.wired.module

fun Application.configureRoutes() {
    install(AssetsPlugin) {
        assetFolder = Assets
    }

    routing {
        get("/") {
            call.respondHtmlTemplate(LayoutTemplate()) {
                scripts {
                    importMap(Assets)
                    module {
                        """
                        import * as Turbo from "${Assets.vendor.`@hotwired`.turbo}";
                        """
                    }
                }

                body {
                    h1 { +"Hello from Ktor!" }
                    p {  + "Page 1" }
                    a(href = "/2") { +"Go to page 2" }
                }
            }
        }

        get("/2") {
            call.respondHtmlTemplate(LayoutTemplate()) {
                body {
                    p {  + "Page 2" }
                    a(href = "/") { +"Go to page 1" }
                }
            }
        }
    }
}