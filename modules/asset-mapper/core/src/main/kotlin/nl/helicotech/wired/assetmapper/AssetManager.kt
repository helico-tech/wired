package nl.helicotech.wired.assetmapper

import java.io.File

interface AssetManager {

    companion object {
        operator fun invoke(): AssetManager = AssetManagerImpl()
    }

    val assets: Set<Asset>

    fun addAsset(file: File, mountPath: String = "/assets")
}

class AssetManagerImpl : AssetManager {
    override val assets = mutableSetOf<Asset>()

    override fun addAsset(file: File, mountPath: String) {
        require(file.exists()) { "Asset file does not exist: $file" }
        require(file.isFile) { "Asset file is not a file: $file" }
        require(file.canRead()) { "Asset file is not readable: $file" }
        require(assets.none { it.sourceFile == file }) { "Asset file is already added: $file" }
        require(mountPath.startsWith("/")) { "Mount path must start with a /: $mountPath" }

        val targetFile = File(mountPath, file.name)

        require(assets.none { it.targetFile == targetFile }) { "Target file is already in use: $targetFile" }
        this.assets.add(Asset(file, targetFile))
    }
}