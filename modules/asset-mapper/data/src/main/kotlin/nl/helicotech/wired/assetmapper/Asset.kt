package nl.helicotech.wired.assetmapper

import io.ktor.http.*
import io.ktor.util.*
import java.nio.file.Path
import kotlin.io.path.nameWithoutExtension
import kotlin.io.path.readText

sealed class Asset {
    abstract val logicalPath: Path
    abstract val digest: String
    abstract val container: AssetContainer
    abstract val contentType: ContentType
    abstract val dependencies: () -> List<Asset>

    val digestName get() = "${logicalPath.nameWithoutExtension}-$digest.${logicalPath.extension}"
    val absoluteLogicalPath get() = container.logicalPath.resolve(logicalPath)
    val absoluteMountPath get() =  container.mountPath?.resolve(digestName)

    class Generic(
        override val logicalPath: Path,
        override val digest: String,
        override val container: AssetContainer,
        override val contentType: ContentType,
        override val dependencies: () -> List<Asset>
    ) : Asset()

    class JavaScript(
        val module: String?,
        override val logicalPath: Path,
        override val digest: String,
        override val container: AssetContainer,
        override val dependencies: () -> List<Asset>
    ) : Asset() {
        override val contentType = ContentType.Application.JavaScript
    }

    class Mutable(
        override var logicalPath: Path,
        override var digest: String,
        override var container: AssetContainer,
        override var contentType: ContentType,
        override var dependencies: () -> List<Asset>
    ) : Asset()
}

fun AssetContainer.Mutable.addGenericAsset(logicalPath: Path, digest: String, contentType: ContentType, dependencies: () -> List<Asset> = { emptyList() }): Asset.Generic {
    val asset = Asset.Generic(logicalPath, digest, this, contentType, dependencies)
    assets.add(asset)
    return asset
}

fun AssetContainer.Mutable.addJavaScriptAsset(logicalPath: Path, digest: String, module: String?, dependencies: () -> List<Asset> = { emptyList() }): Asset.JavaScript {
    val asset = Asset.JavaScript(module, logicalPath, digest, this, dependencies)
    assets.add(asset)
    return asset
}