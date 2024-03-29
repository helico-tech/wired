package nl.helicotech.wired.assetmapper

import io.ktor.http.*
import java.io.InputStream

class JavascriptDependencyResolver : DependencyResolver {

    companion object {
        private val LINES_THAT_START_WITH_COMMENTS = "^(?:\\/\\/.*)"
        private val STRINGS_ENCLOSED_IN_SINGLE_QUOTES = "\\'(?:[^\\'\\\\\\n]|\\\\.)*+\\'"
        private val STRINGS_ENCLOSED_IN_DOUBLE_QUOTES = "\"(?:[^\"\\\\\\n]|\\\\.)*+\""
        private val IMPORT_STATEMENTS = "(?:import\\s*(?:(?:\\*\\s*as\\s+\\w+|\\s+[\\w\\s{},*]+)\\s*from\\s*)?|\\bimport\\()"
        private val REST = "\\s*['\"`](\\./[^'\"`\\n]++|(\\.\\./)*+[^'\"`\\n]++)['\"`]\\s*[;)]?"
        private val IMPORT_REGEX = Regex(
            """$LINES_THAT_START_WITH_COMMENTS|$STRINGS_ENCLOSED_IN_SINGLE_QUOTES|$STRINGS_ENCLOSED_IN_DOUBLE_QUOTES|$IMPORT_STATEMENTS$REST"""
        )
    }

    override fun accepts(asset: Asset): Boolean {
        return asset.contentType.match(ContentType.Application.JavaScript)
    }

    override fun resolve(source: InputStream): Set<String> {
        return source.reader().readLines().fold(mutableSetOf()) { dependencies, line ->
            dependencies.addAll(IMPORT_REGEX.findAll(line).map { it.groupValues[1] }.filterNot { it.isBlank() })
            dependencies
        }
    }
}