package nl.helicotech.wired.turbo

import kotlinx.html.*

class TURBOFRAME(consumer: TagConsumer<*>) :
    HTMLTag(
        tagName = "turbo-frame",
        consumer = consumer,
        initialAttributes = emptyMap(),
        inlineTag = false,
        emptyTag = false
    ), HtmlBlockTag
{
    var src: String
        get() = attributes["src"]!!
        set(value) {
            attributes["src"] = value
        }

    var lazy: Boolean?
        get() = attributes["lazy"]?.toBoolean() ?: false
        set(value) {
            if (value == null) {
                attributes.remove("lazy")
                return
            }
            attributes["lazy"] = value.toString()
        }

    var target: String?
        get() = attributes["target"]
        set(value) {
            if (value == null) {
                attributes.remove("target")
                return
            }
            attributes["target"] = value
        }

    var refresh: String?
        get() = attributes["refresh"]
        set(value) {
            if (value == null) {
                attributes.remove("refresh")
                return
            }
            attributes["refresh"] = value
        }

    fun refresh(method: TurboRefreshMethod) {
        refresh = method.value
    }
}

fun A.turboFrame(target: String) {
    attributes["data-turbo-frame"] = target
}

fun FORM.turboFrame(target: String) {
    attributes["data-turbo-frame"] = target
}

fun TURBOFRAME.turboAction(type: TurboActionType) {
    attributes["data-turbo-action"] = type.value
}

fun FlowContent.turboFrame(
    id: String,
    src: String? = null,
    lazy: Boolean = false,
    target: String? = null,
    block: TURBOFRAME.() -> Unit = {}
) {
    TURBOFRAME(consumer).apply {
        this.id = id
        src?.let {
            this.src = it

            if (lazy) {
                this.lazy = true
            }
        }

        target?.let {
            this.target = it
        }
    }.visit(block)
}

