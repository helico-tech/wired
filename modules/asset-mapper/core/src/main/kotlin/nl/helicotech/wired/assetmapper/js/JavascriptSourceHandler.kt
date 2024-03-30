package nl.helicotech.wired.assetmapper.js

import nl.helicotech.wired.assetmapper.SourceHandler

class JavascriptSourceHandler(
    private val lines: List<String>
) : SourceHandler {
    companion object : SourceHandler.Factory {
        private val LINES_THAT_START_WITH_COMMENTS = "^(?:\\/\\/.*)"
        private val STRINGS_ENCLOSED_IN_SINGLE_QUOTES = "\\'(?:[^\\'\\\\\\n]|\\\\.)*+\\'"
        private val STRINGS_ENCLOSED_IN_DOUBLE_QUOTES = "\"(?:[^\"\\\\\\n]|\\\\.)*+\""
        private val IMPORT_STATEMENTS = "(?:import\\s*(?:(?:\\*\\s*as\\s+\\w+|\\s+[\\w\\s{},*]+)\\s*from\\s*)?|\\bimport\\()"
        private val REST = "\\s*['\"`](\\./[^'\"`\\n]++|(\\.\\./)*+[^'\"`\\n]++)['\"`]\\s*[;)]?"
        private val IMPORT_REGEX = Regex(
            """$LINES_THAT_START_WITH_COMMENTS|$STRINGS_ENCLOSED_IN_SINGLE_QUOTES|$STRINGS_ENCLOSED_IN_DOUBLE_QUOTES|$IMPORT_STATEMENTS$REST"""
        )

        override fun create(lines: List<String>): SourceHandler {
            return JavascriptSourceHandler(lines)
        }
    }

    override fun getSourceDependencies(): Set<String> {
        return lines.fold(mutableSetOf()) { dependencies, line ->
            dependencies.addAll(IMPORT_REGEX.findAll(line).map { it.groupValues[1] }.filterNot { it.isBlank() })
            dependencies
        }
    }
}