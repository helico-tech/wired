package nl.helicotech.wired.ktor

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.html.*
import kotlinx.html.stream.appendHTML

typealias HTMLFragmentBuilder = TagConsumer<*>.() -> Unit

fun buildHTMLFragment(block: HTMLFragmentBuilder): String {
    return buildString {
        appendHTML().block()
    }
}

suspend fun SendChannel<Frame>.sendHtmlFragment(block: HTMLFragmentBuilder) {
    send(Frame.Text(buildHTMLFragment(block)))
}

suspend fun SendChannel<Frame>.sendHtml(block: HTML.() -> Unit) {
    send(Frame.Text(buildString {
        appendHTML().html { block() }
    }))
}

suspend fun ApplicationCall.respondHtmlFragment(status: HttpStatusCode = HttpStatusCode.OK, block: HTMLFragmentBuilder) {
    respond(TextContent(buildHTMLFragment(block), ContentType.Text.Html.withCharset(Charsets.UTF_8), status))
}