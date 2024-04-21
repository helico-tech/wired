package nl.helicotech.testing

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.html.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import kotlinx.html.*

fun main() {
    /*embeddedServer(
        factory = Netty,
        port = 8080,
        host = "0.0.0.0",
        module = Application::testing,
    ).start(wait = true)*/
}

/*fun Application.testing() {
    routing {
        staticAssets(Assets)

        get("/") {
            call.respondHtml {
                head {
                    title { +"Hello from Ktor!" }
                    importMap(Assets.AppJs, Assets.Vendors.Hotwired.Turbo)
                }
                body {
                    h1 { +"Hello from Ktor!" }
                }
            }
        }
    }
}*/