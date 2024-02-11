package nl.helicotech.wired.example

import io.ktor.server.html.*
import kotlinx.html.*
import nl.helicotech.wired.ImportMap
import nl.helicotech.wired.importMap

class LayoutTemplate : Template<HTML> {

    val scripts = Placeholder<HtmlHeadTag>()
    val body = Placeholder<HtmlBlockTag>()

    override fun HTML.apply() {
        head {
            insert(scripts)
        }

        body {
            insert(body)
        }
    }
}