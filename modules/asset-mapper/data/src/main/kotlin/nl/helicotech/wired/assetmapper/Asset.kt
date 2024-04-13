package nl.helicotech.wired.assetmapper

import io.ktor.http.*
import io.ktor.util.*
import java.nio.file.Path
import kotlin.io.path.nameWithoutExtension

fun mutableAsset(
    module: String? = null,
    logicalPath: Path,
    digest: String,
    container: AssetContainer,
    contentType: ContentType,
    dependencies: () -> List<Asset> = { emptyList() }
) = Asset.Mutable(module, logicalPath, digest, container, contentType, dependencies)

sealed class Asset {
    abstract val logicalPath: Path
    abstract val digest: String
    abstract val container: AssetContainer
    abstract val contentType: ContentType
    abstract val dependencies: () -> List<Asset>

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
        var module: String?,
        override var logicalPath: Path,
        override var digest: String,
        override var container: AssetContainer,
        override var contentType: ContentType,
        override var dependencies: () -> List<Asset>
    ) : Asset()

    val digestName get() = "${logicalPath.nameWithoutExtension}-$digest.${logicalPath.extension}"
    val absoluteLogicalPath get() = container.logicalPath.resolve(logicalPath)
    val absoluteMountPath get() =  container.mountPath?.resolve(digestName)

    fun asMutable() = when {
        this is JavaScript -> Mutable(module, logicalPath, digest, container, ContentType.Application.JavaScript,  dependencies)
        this is Mutable -> this
        else -> Mutable(null, logicalPath, digest, container, contentType, dependencies)
    }

    fun asImmutable() = when {
        this !is Mutable -> this
        this.contentType == ContentType.Application.JavaScript -> JavaScript(module, logicalPath, digest, container, dependencies)
        else -> Generic(logicalPath, digest, container, contentType, dependencies)
    }
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