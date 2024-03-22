package nl.helicotech.wired.turbo

import kotlinx.html.*

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

