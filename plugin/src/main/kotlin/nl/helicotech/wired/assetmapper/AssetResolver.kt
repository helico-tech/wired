package nl.helicotech.wired.assetmapper

import java.io.File

class AssetResolver {

    private val folders = mutableListOf<File>()

    fun addFolder(file: File): AssetResolver {
        require(file.exists()) { "File does not exist: $file" }
        require(file.isDirectory) { "File is not a directory: $file" }
        folders.add(file)
        return this
    }

    fun build(): Asset {

    }
}