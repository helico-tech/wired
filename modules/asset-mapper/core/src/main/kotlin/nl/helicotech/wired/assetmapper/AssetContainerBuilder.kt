package nl.helicotech.wired.assetmapper

import io.ktor.http.*
import nl.helicotech.wired.assetmapper.js.JavascriptDependencyResolver
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.walk

class AssetContainerBuilder(
    private val javascriptModuleResolver: JavascriptModuleResolver = JavascriptModuleResolver.EMPTY
) {

    interface JavascriptModuleResolver {
        fun resolveModule(path: Path): String?

        companion object {
            val EMPTY = object : JavascriptModuleResolver {
                override fun resolveModule(path: Path): String? = null
            }

            fun fromMap(map: Map<Path, String>): JavascriptModuleResolver {
                return object : JavascriptModuleResolver {
                    override fun resolveModule(path: Path): String? {
                        return map[path.normalize()]
                    }
                }
            }
        }
    }

    val dependencyResolverFactory = RootDependencyResolver(
        JavascriptDependencyResolver
    )

    @OptIn(ExperimentalPathApi::class)
    fun fromPath(path: Path): AssetContainer {

        val container = mutableAssetContainer(
            logicalPath = path,
        )

        val dependencyResolver = dependencyResolverFactory.create(container)

        path.walk().forEach { handlePath(container, it) }

        return container
    }

    private fun handlePath(container: AssetContainer.Mutable, path: Path) {
        if (Files.isDirectory(path)) return

        val relativePath = container.logicalPath.relativize(path).normalize()

        val contentType = ContentType.defaultForFilePath(path.toString())

        val asset = mutableAsset(
            module = javascriptModuleResolver.resolveModule(relativePath),
            logicalPath = relativePath,
            digest = Files.readAllBytes(path).contentHashCode().toString(),
            container = container,
            contentType = contentType,
            dependencies = { emptyList() }
        )

        container.assets.add(asset)
    }
}