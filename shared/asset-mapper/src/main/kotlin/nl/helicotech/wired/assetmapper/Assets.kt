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

    fun merge(other: AssetCollection): AssetCollection {
        require(roots.distinctBy { it.name }.size == 1) { "Root names must be the same" }
        require(other.roots.distinctBy { it.name }.size == 1) { "Root names must be the same" }

        return AssetCollection(
            roots + other.roots,
            directories + other.directories,
            files + other.files
        )
    }
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