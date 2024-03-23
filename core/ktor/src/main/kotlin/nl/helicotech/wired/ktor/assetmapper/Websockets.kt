package nl.helicotech.wired.ktor.assetmapper

import io.ktor.websocket.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.html.TagConsumer
import kotlinx.html.stream.appendHTML

suspend fun SendChannel<Frame>.sendHtml(block: TagConsumer<StringBuilder>.() -> Unit) {
    val text = buildString {
        appendHTML().block()
    }
    send(Frame.Text(text))
}