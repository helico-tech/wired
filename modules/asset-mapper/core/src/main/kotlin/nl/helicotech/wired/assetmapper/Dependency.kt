package nl.helicotech.wired.assetmapper

import java.nio.file.Path

data class Dependency(
    val from: Asset,
    val to: Asset,
    val logicalPath: Path
)