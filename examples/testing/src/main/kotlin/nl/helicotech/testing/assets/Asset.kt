package nl.helicotech.testing.assets

import io.ktor.http.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import io.ktor.util.*
import kotlinx.html.*
import java.nio.file.Path
import kotlin.io.path.nameWithoutExtension

abstract class AssetContainer(
    val logicalPath: Path,
    val mountPath: Path? = null,
    val parent: AssetContainer? = null
) {
    open val assets: List<Asset> = listOf()

    open val containers: List<AssetContainer> = listOf()

    constructor(logicalPath: String, mountPath: String? = null, parent: AssetContainer? = null) :
            this(Path.of(logicalPath), mountPath?.let { Path.of(it) }, parent)

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

    val absoluteMountPath: Path = traverseUp().map { it.mountPath ?: it.logicalPath }.joinToString("/").let { Path.of(it) }
}

sealed class Asset(
    val logicalPath: Path,
    val digest: String,
    val container: AssetContainer,
    val contentType: ContentType
) {
    class Generic(
        logicalPath: Path,
        digest: String,
        contentType: ContentType,
        container: AssetContainer
    ) : Asset(logicalPath, digest, container, contentType)  {
        constructor(logicalPath: String, digest: String, contentType: ContentType, container: AssetContainer) :
                this(Path.of(logicalPath), digest, contentType, container)

    }

    class JavaScript(
        val module: String? = null,
        logicalPath: Path,
        digest: String,
        container: AssetContainer,
        val dependencies: () -> List<Asset> = { emptyList() }
    ) : Asset(logicalPath, digest, container, ContentType.Application.JavaScript) {
        constructor(module: String? = null, logicalPath: String, digest: String, container: AssetContainer, dependencies: () -> List<Asset> = { emptyList() }) : this(module, Path.of(logicalPath), digest, container, dependencies)
    }

    val digestName = "${logicalPath.nameWithoutExtension}-$digest.${logicalPath.extension}"

    val absoluteLogicalPath = container.absoluteMountPath.resolve(logicalPath)

    val absoluteMountPath = container.absoluteMountPath.resolve(digestName)
}

fun Routing.staticAssets(container: AssetContainer) {
    staticResources(container.mountPath.toString(), container.logicalPath.toString())
}

fun HEAD.module(asset: Asset.JavaScript) {
    require(asset.module != null) { "Asset must have a module name" }
    script(type = "module") { unsafe {  + """import "${asset.module}";"""  } }
}

fun HEAD.preload(asset: Asset) {
    link(rel = "modulepreload", href = asset.absoluteMountPath.toString())
}