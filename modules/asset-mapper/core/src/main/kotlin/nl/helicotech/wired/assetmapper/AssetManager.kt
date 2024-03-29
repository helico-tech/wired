package nl.helicotech.wired.assetmapper

import java.io.File

interface AssetManager {

    companion object {
        operator fun invoke(
            digester: Digester = Digester.SHA1
        ): AssetManager = AssetManagerImpl(
            digester = digester
        )
    }

    val assets: Set<Asset>

    fun addAsset(file: File, mountPath: String = "/assets")

    fun resolve(path: String): Asset? {
        return assets.find { it.targetFile.path == path }
    }

    fun resolveRelative(from: Asset, path: String): Asset? {
        require(assets.contains(from)) { "Asset is not managed: $from" }
        return assets.find {
            it.targetFile.path == from.targetFile.parentFile.resolve(path).canonicalPath
        }
    }

    fun resolveRelative(from: String, path: String): Asset? {
        val fromAsset = resolve(from)
        requireNotNull(fromAsset) { "Asset is not managed: $from" }
        return resolveRelative(fromAsset, path)
    }
}

class AssetManagerImpl(
    private val digester: Digester = Digester.SHA1
) : AssetManager {
    override val assets = mutableSetOf<Asset>()

    override fun addAsset(file: File, mountPath: String) {
        require(file.exists()) { "Asset file does not exist: $file" }
        require(file.isFile) { "Asset file is not a file: $file" }
        require(file.canRead()) { "Asset file is not readable: $file" }
        require(assets.none { it.sourceFile == file }) { "Asset file is already added: $file" }
        require(mountPath.startsWith("/")) { "Mount path must start with a /: $mountPath" }

        val asset = Asset(
            sourceFile = file,
            targetFile = File(mountPath, file.name),
            digest = digester.digest(file.inputStream())
        )

        require(assets.none { it.targetFile == asset.targetFile }) { "Target file is already in use: ${asset.targetFile}" }

        assets.add(asset)
    }
}