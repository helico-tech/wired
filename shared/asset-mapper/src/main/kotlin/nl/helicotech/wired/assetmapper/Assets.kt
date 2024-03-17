package nl.helicotech.wired.assetmapper

import java.io.File

sealed interface Asset {
    val name: String
}

data class AssetCollection(
    val roots: List<File>,
    val directories: List<AssetDirectory>,
    val files: List<AssetFile>
) : Asset {
    override val name = roots.first().name
}

data class AssetDirectory(
    val directory: File,
    val directories: List<AssetDirectory>,
    val files: List<AssetFile>
) : Asset {
    override val name = directory.name
}

data class AssetFile(
    val file: File,
    val hash: String
) : Asset {
    override val name = file.name
}