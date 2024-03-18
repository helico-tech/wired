package nl.helicotech.wired.assetmapper

import java.io.File

sealed interface Asset {
    val file: java.io.File
    val name get() = file.name

    interface Directory : Asset {
        val directories: List<Directory>
        val files: List<File>
    }

    interface File : Asset {
        val hash: String
    }
}

fun assetDirectory(
    directory: File,
    directories: List<Asset.Directory> = emptyList(),
    files: List<Asset.File> = emptyList()
) : Asset.Directory {
    require(directory.exists() && directory.isDirectory) { "Root does not exist or is not a directory" }

    return object : Asset.Directory {
        override val file: File = directory
        override val directories = directories
        override val files = files
    }
}

fun assetFile(
    file: File,
    hash: String
) : Asset.File {
    require(file.exists() && file.isFile) { "File does not exist or is not a file" }

    return object : Asset.File {
        override val hash = hash
        override val file = file
    }
}