package nl.helicotech.wired.turbo

import kotlinx.html.*

sealed class TurboMethod(val value: String) {
    data object Get : TurboMethod("get")
    data object Post : TurboMethod("post")
    data object Put : TurboMethod("put")
    data object Delete : TurboMethod("delete")
    data object Patch : TurboMethod("patch")
}

sealed class TurboActionType(val value: String) {
    data object Replace : TurboActionType("replace")
    data object Advance : TurboActionType("advance")
}

sealed class TurboRefreshMethod(val value: String) {
    data object Morph : TurboRefreshMethod("morph")
    data object Replace : TurboRefreshMethod("replace")
}

sealed class TurboRefreshScrollType(val value: String) {
    data object Preserve : TurboRefreshScrollType("preserve")
    data object Reset : TurboRefreshScrollType("reset")
}

sealed class TurboStreamActionType(val value: String) {
    data object Append : TurboStreamActionType("append")
    data object Prepend : TurboStreamActionType("prepend")
    data object Replace : TurboStreamActionType("replace")
    data object Update : TurboStreamActionType("update")
    data object Remove : TurboStreamActionType("remove")
    data object Before : TurboStreamActionType("before")
    data object After : TurboStreamActionType("after")
    data object Morph : TurboStreamActionType("morph")
    data object Refresh : TurboStreamActionType("refresh")
}

fun HEAD.enableViewTransition() {
    meta(name = "view-transition", content = "same-origin")
}

fun HEAD.turboForceReload() {
    meta(name = "turbo-visit-control", content = "reload")
}

fun HEAD.turboRoot(url: String) {
    meta(name = "turbo-root", content = url)
}

fun HEAD.turboPrefetch(boolean: Boolean = true) {
    meta(name = "turbo-prefetch", content = boolean.toString())
}

fun HEAD.turboRefreshMethod(method: TurboRefreshMethod) {
    meta(name = "turbo-refresh-method", content = method.value)
}

fun HEAD.turboRefreshScroll(type: TurboRefreshScrollType) {
    meta(name = "turbo-refresh-scroll", content = type.value)
}

fun HEAD.turboCSRF(token: String) {
    meta(name = "csrf-token", content = token)
}

fun FlowContent.turbo(boolean: Boolean) {
    attributes["data-turbo"] = boolean.toString()
}

fun FlowContent.turboPrefetch(boolean: Boolean = true) {
    attributes["data-turbo-prefetch"] = boolean.toString()
}

fun FlowContent.turboPermanent(boolean: Boolean = true) {
    attributes["data-turbo-permanent"] = boolean.toString()
}

fun A.turboAction(type: TurboActionType) {
    attributes["data-turbo-action"] = type.value
}
fun A.turboMethod(method: TurboMethod) {
    attributes["data-turbo-method"] = method.value
}

fun A.turboConfirm(message: String) {
    attributes["data-turbo-confirm"] = message
}

fun A.turboPreload(boolean: Boolean = true) {
    attributes["data-turbo-preload"] = boolean.toString()
}

fun SCRIPT.reload() {
    attributes["data-turbo-track"] = "reload"
}