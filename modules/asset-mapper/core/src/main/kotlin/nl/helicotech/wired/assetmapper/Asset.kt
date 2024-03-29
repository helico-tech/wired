package nl.helicotech.wired.assetmapper

import java.io.File

data class Asset(
    val sourceFile: File,
    val targetFile: File,
    val digest: String
) {
    val mappedPath = "${targetFile.nameWithoutExtension}-${digest}.${targetFile.extension}"
}