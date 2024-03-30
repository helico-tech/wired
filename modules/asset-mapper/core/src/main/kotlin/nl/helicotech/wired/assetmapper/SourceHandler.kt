package nl.helicotech.wired.assetmapper

interface SourceHandler {
    fun getSourceDependencies(): Set<String>

    interface Factory {
        fun create(lines: List<String>): SourceHandler
    }
}