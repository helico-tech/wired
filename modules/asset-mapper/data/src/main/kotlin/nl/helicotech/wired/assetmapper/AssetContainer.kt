package nl.helicotech.wired.assetmapper

import java.nio.file.Path

interface AssetContainer : AssetResolver {

    companion object {
        fun createMutable(logicalPath: Path, mountPath: Path? = null, parent: AssetContainer? = null): Mutable = Mutable(logicalPath, mountPath, parent)
        fun createMutable(logicalPath: String, mountPath: String? = null, parent: AssetContainer? = null): Mutable = createMutable(Path.of(logicalPath), mountPath?.let { Path.of(it) }, parent)
        fun create(logicalPath: Path, mountPath: Path? = null, parent: AssetContainer? = null): AssetContainer = createMutable(logicalPath, mountPath, parent)
        fun create(logicalPath: String, mountPath: String? = null, parent: AssetContainer? = null): AssetContainer = createMutable(logicalPath, mountPath, parent)
    }

    class Mutable(
        override var logicalPath: Path,
        override var mountPath: Path?,
        override var parent: AssetContainer?
    ) : AssetContainer {
        override val assets: MutableList<Asset> = mutableListOf()
        override val containers: MutableList<AssetContainer> = mutableListOf()

        fun addContainer(logicalPath: Path): AssetContainer.Mutable {
            val container = createMutable(logicalPath, null, this)
            containers.add(container)
            return container
        }
    }

    val logicalPath: Path
    val mountPath: Path?
    val parent: AssetContainer?

    val assets: List<Asset>
    val containers: List<AssetContainer>

    val absoluteMountPath: Path get() = traverseUp().map { it.mountPath ?: it.logicalPath }.joinToString("/").let { Path.of(it) }

    val absoluteLogicalPath get() = traverseUp().map { it.logicalPath }.joinToString("/").let { Path.of(it) }

    override fun resolve(path: Path): Asset? = traverse().firstOrNull { asset ->
        when {
            path.isModule() -> asset is Asset.JavaScript && asset.module == path.toString()
            else -> asset.absoluteLogicalPath == logicalPath.resolve(path).normalize()
        }
    }

    override fun resolveRelative(from: Asset, path: Path): Asset? {
        require(traverse().any { it == from }) { "Asset is not in this container" }

        if (path.isModule()) return resolve(path)

        val basePath = Path.of(".").resolve(from.absoluteLogicalPath.first().relativize(from.absoluteLogicalPath).parent ?: Path.of(""))

        val pathToResolve = Path.of(".").resolve(basePath.resolve(path).normalize())

        return resolve(pathToResolve)
    }

    override fun resolveRelative(from: Path, path: Path): Asset? {
        val fromAsset = requireNotNull(resolve(from)) { "Asset is not in this container" }
        return resolveRelative(fromAsset, path)
    }

    fun traverse(filter: (Asset) -> Boolean = { true }): Sequence<Asset> = sequence {
        for (asset in assets) {
            if (filter(asset)) {
                yield(asset)
            }
        }

        for (container in containers) {
            yieldAll(container.traverse(filter))
        }
    }

    fun traverseUp(): Sequence<AssetContainer> = sequence {
        parent?.let {
            yieldAll(it.traverseUp())
        }

        yield(this@AssetContainer)
    }

    private fun Path.isRelative() = startsWith("/") || startsWith(".") || startsWith("..")

    private fun Path.isModule() = !isRelative()
}