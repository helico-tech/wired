package nl.helicotech.wired

import kotlinx.html.HtmlHeadTag
import kotlinx.html.script
import kotlinx.html.unsafe
import org.intellij.lang.annotations.Language

fun HtmlHeadTag.importMap(vararg assets: Asset) {

    val importMap = ImportMap(*assets)

    script(type = "importmap") {
        unsafe {
            +importMap.toJson()
        }
    }
}

fun HtmlHeadTag.module(block: () -> String) {
    script(type = "module") {
        unsafe {
            +block().trimIndent()
        }
    }
}