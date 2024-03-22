package nl.helicotech.wired.turbo

import io.ktor.server.application.*
import kotlinx.html.*

class TEMPLATE(
    initialAttributes : Map<String, String> = emptyMap(),
    consumer: TagConsumer<*>) :
    HTMLTag(
        tagName = "template",
        consumer = consumer,
        initialAttributes = initialAttributes,
        inlineTag = false,
        emptyTag = false
    ), HtmlBlockTag

class TURBOFRAME(
    initialAttributes : Map<String, String> = emptyMap(),
    consumer: TagConsumer<*>) :
    HTMLTag(
        tagName = "turbo-frame",
        consumer = consumer,
        initialAttributes = initialAttributes,
        inlineTag = false,
        emptyTag = false
    ), HtmlBlockTag

class TURBOSTREAM(
    initialAttributes : Map<String, String> = emptyMap(),
    consumer: TagConsumer<*>) :
    HTMLTag(
        tagName = "turbo-stream",
        consumer = consumer,
        initialAttributes = initialAttributes,
        inlineTag = false,
        emptyTag = false
    ), HtmlBlockTag

class TURBOSTREAMSOURCE(
    initialAttributes : Map<String, String> = emptyMap(),
    consumer: TagConsumer<*>) :
    HTMLTag(
        tagName = "turbo-stream-source",
        consumer = consumer,
        initialAttributes = initialAttributes,
        inlineTag = true,
        emptyTag = false,
    ), HtmlBlockTag

fun A.targetTurboFrame(target: String) {
    attributes["data-turbo-frame"] = target
}

fun FORM.targetTurboFrame(target: String) {
    attributes["data-turbo-frame"] = target
}

fun TURBOFRAME.turboAction(type: TurboActionType) {
    attributes["data-turbo-action"] = type.value
}

fun TURBOFRAME.refresh(method: TurboRefreshMethod) {
    attributes["refresh"] = method.value
}

fun FlowContent.turboFrame(
    id: String,
    src: String? = null,
    lazy: Boolean? = null,
    target: String? = null,
    block: TURBOFRAME.() -> Unit = {}
): Unit =  TURBOFRAME(attributesMapOf("id", id, "src", src, "lazy", lazy?.toString(), "target", target), consumer).visit(block)

fun FlowContent.turboStreamSource(
    src: String
) = TURBOSTREAMSOURCE(attributesMapOf("src", src), consumer).visit {  }

fun FlowContent.template(
    block: TEMPLATE.() -> Unit
) = TEMPLATE(consumer = consumer).visit(block)

fun <T, C : TagConsumer<T>> C.turboStream(
    action: TurboStreamActionType,
    target: String? = null,
    targets: String? = null,
    block: TEMPLATE.() -> Unit
) = TURBOSTREAM(attributesMapOf("action", action.value, "target", target, "targets", targets), this).visitAndFinalize(this) {
    template(block)
}