package nl.helicotech.wired.example

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(
        factory = Netty,
        port = 8080,
        host = "0.0.0.0",
        module = Application::module,
        watchPaths = listOf("classes", "resources", "generated/wired")
    ).start(wait = true)
}

fun Application.module() {
    configureRoutes()
}
