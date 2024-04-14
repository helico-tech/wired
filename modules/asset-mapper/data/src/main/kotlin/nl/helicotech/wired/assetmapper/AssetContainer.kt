package nl.helicotech.wired.assetmapper

import java.nio.file.Path

fun mutableAssetContainer(logicalPath: Path, mountPath: Path? = null): AssetContainer.Mutable {
    return AssetContainer.Mutable(logicalPath, mountPath)
}

fun mutableAssetContainer(logicalPath: String, mountPath: String? = null): AssetContainer.Mutable {
    return mutableAssetContainer(Path.of(logicalPath), mountPath?.let { Path.of(it) })
}


interface AssetContainer : AssetResolver {

    class Mutable(
        override var logicalPath: Path,
        override var mountPath: Path?,
    ) : AssetContainer {
        override val assets: MutableList<Asset> = mutableListOf()
    }

    val logicalPath: Path
    var mountPath: Path?
    val assets: List<Asset>

    override fun resolve(path: Path): Asset? = assets.firstOrNull { asset ->
        when {
            path.isModule() -> asset is Asset.JavaScript && asset.module == path.toString()
            else -> asset.absoluteLogicalPath.normalize() == logicalPath.resolve(path).normalize()
        }
    }

    override fun resolveRelative(from: Asset, path: Path): Asset? {
        require(assets.any { it == from }) { "Asset is not in this container" }

        if (path.isModule()) return resolve(path)

        val absolutePathToResolve = from.absoluteLogicalPath.parent.resolve(path).normalize()

        val relativePathToResolve = Path.of(".").resolve(this.logicalPath.relativize(absolutePathToResolve))

        return resolve(relativePathToResolve)
    }

    override fun resolveRelative(from: Path, path: Path): Asset? {
        val fromAsset = requireNotNull(resolve(from)) { "Asset is not in this container" }
        return resolveRelative(fromAsset, path)
    }

    private fun Path.isRelative() = startsWith("/") || startsWith(".") || startsWith("..")

    private fun Path.isModule() = !isRelative()
}