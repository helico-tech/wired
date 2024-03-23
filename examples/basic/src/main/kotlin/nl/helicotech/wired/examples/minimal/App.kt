package nl.helicotech.wired.examples.minimal

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.html.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.delay
import kotlinx.html.*
import nl.helicotech.wired.assetmapper.*
import nl.helicotech.wired.examples.minimal.assets.Assets
import nl.helicotech.wired.examples.minimal.assets.Vendors
import nl.helicotech.wired.ktor.sendHtml
import nl.helicotech.wired.ktor.sendHtmlFragment
import nl.helicotech.wired.turbo.*
import java.time.Duration

fun main() {
    embeddedServer(
        factory = Netty,
        port = 8080,
        host = "0.0.0.0",
        module = Application::minimal,
    ).start(wait = true)
}

fun Application.minimal() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    routing {
        staticTypedAssets(Assets, Vendors)

        get("/") {
            call.respondTemplate {
                turboStreamSource("ws://${call.application.environment.config.host}:${call.application.environment.config.port}/turbo-stream")

                h1 {
                    +"Wired Example"
                }

                a(href = "/assets") {
                    targetTurboFrame("assets")
                    +"Assets"
                }

                turboFrame("stream") {
                    h2 { +"Area for stream" }
                }

                turboFrame("assets") {
                    h2 { +"Area for assets" }
                }
            }
        }

        get("/assets") {
            call.respondTemplate {
                turboFrame("assets") {
                    h2 { +"Typed assets" }
                    listOf(Assets, Vendors).forEach { directory ->
                        h3 { +directory.fileName() }

                        ul {
                            directory.traverseFiles().forEach {
                                li {
                                    a(href = it.url()) {
                                        +it.fileName()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        webSocket(path = "/turbo-stream") {
            while (true) {
                outgoing.sendHtmlFragment {
                    turboStream(
                        action = TurboStreamActionType.Replace,
                        target = "stream"
                    ) {
                        turboFrame("stream") {
                            p { +"Hello, World!" }
                            p { +"Current time: ${System.currentTimeMillis()}" }
                        }
                    }
                }
                delay(1000)
            }
        }
    }
}

suspend fun ApplicationCall.respondTemplate(content: BODY.() -> Unit) {
    respondHtml {
        template(content)
    }
}

fun HTML.template(content: BODY.() -> Unit) {
    head {
        importMap(Assets, Vendors)
        module(Vendors.Js.Hotwired.Turbo_js)
    }

    body {
        content()
    }
}