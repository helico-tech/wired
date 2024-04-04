package nl.helicotech.wired.assetmapper

import java.io.File
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.isReadable
import kotlin.io.path.isRegularFile


interface AssetManager : AssetResolver {

    companion object {
        operator fun invoke(
            root: Path,
            digester: Digester = Digester.SHA1
        ): AssetManager = AssetManagerImpl(
            root = root,
            digester = digester
        )
    }

    val assets: Set<Asset>

    fun addAsset(file: Path, module: String?)
}


class AssetManagerImpl(
    private val root: Path,
    private val digester: Digester = Digester.SHA1
) : AssetManager {
    override val assets = mutableSetOf<Asset>()

    override fun addAsset(
        path: Path,
        module: String?
    ) {

        require(file.exists()) { "Asset file does not exist: $file" }
        require(file.isRegularFile()) { "Asset file is not a file: $file" }
        require(file.isReadable()) { "Asset file is not readable: $file" }
        require(assets.none { it.absoluteLogicalPath == path }) { "Asset file is already added: $file" }

        val asset = Asset(
            sourceFile = file,
            targetFile = File(mountPath, file.name),
            digest = digester.digest(file.inputStream()),
            moduleName = module
        )

        require(assets.none { it.targetFile == asset.targetFile }) { "Target file is already in use: ${asset.targetFile}" }

        assets.add(asset)
    }

    override fun resolve(path: String): Asset? {
        if (path.pathIsModule()) return assets.find { it.moduleName == path }

        return assets.find { it.targetFile == File(path).canonicalFile }
    }

    override fun resolveRelative(from: Asset, path: String): Asset? {
        if (path.pathIsModule()) return assets.find { it.moduleName == path }

        require(assets.contains(from)) { "Asset is not managed: $from" }

        return assets.find {
            it.targetFile == from.targetFile.parentFile.resolve(path).canonicalFile
        }
    }

    override fun resolveRelative(from: String, path: String): Asset? {
        if (path.pathIsModule()) return assets.find { it.moduleName == path }

        val fromAsset = resolve(from)

        requireNotNull(fromAsset) { "Asset is not managed: $from" }

        return resolveRelative(fromAsset, path)
    }

    private fun String.pathIsRelative() = startsWith("/") || startsWith(".")

    private fun String.pathIsModule() = !pathIsRelative()
}