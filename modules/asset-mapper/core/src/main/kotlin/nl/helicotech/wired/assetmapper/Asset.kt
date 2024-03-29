package nl.helicotech.wired.assetmapper

import java.io.File

data class Asset(
    val sourceFile: File,
    val targetFile: File,
    val digest: String,
    val moduleName: String?
) {
    val mappedFile = File(targetFile.parent, "${targetFile.nameWithoutExtension}-${digest}.${targetFile.extension}")
}