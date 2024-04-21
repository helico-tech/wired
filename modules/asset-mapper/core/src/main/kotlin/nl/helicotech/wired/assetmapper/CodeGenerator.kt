package nl.helicotech.wired.assetmapper

import com.squareup.kotlinpoet.*
import nl.helicotech.wired.assetmapper.js.JavascriptDependencyResolver
import java.nio.file.Path
import kotlin.io.path.name

class CodeGenerator(
    private val assetContainer: AssetContainer,
    private val packageName: String,
    private val fileName: String
) {
    private val nameAllocator = NameAllocator()

    private val dependencyResolver = RootDependencyResolver(JavascriptDependencyResolver).create(assetContainer)

    fun generate(): FileSpec {
        val builder = FileSpec.builder(packageName, fileName)

        val rootObjectBuilder = generateDirectoryObjectTree()

        return builder.build()
    }

    private fun generateDirectoryObjectTree(): TypeSpec.Builder {
        val allDirectories = assetContainer.assets
            .fold(setOf<Path>()) { acc, asset -> acc + asset.parentDirectories() }
            .sortedBy { it.toString() }

        val builder = TypeSpec.objectBuilder(nameAllocator.newName(assetContainer.logicalPath.name, assetContainer))

        return builder
    }

    private fun generateDirectory(directory: Path, directories: List<Path>, nameAllocator: NameAllocator) {

    }

    /*private fun TypeSpec.Builder.addAsset(asset: Asset, dependencies: List<Dependency>) = addProperty(asset(asset, dependencies))

    private fun TypeSpec.Builder.addAssets(assets: List<Asset>) {
        val assetsWithDependencies = assets.map { asset ->
            val dependencies = dependencyResolver.resolve(asset).toList()
            asset to dependencies
        }

        assetsWithDependencies.forEach { (asset, dependencies) ->
            addAsset(asset, dependencies)
        }
    }

    private fun assetContainer(container: AssetContainer): TypeSpec {
        val builder = TypeSpec.objectBuilder(containerName(container))

        builder.implementAssetContainer(container)

        return builder.build()
    }

    private fun TypeSpec.Builder.implementAssetContainer(container: AssetContainer): TypeSpec.Builder {
        val allDirectories = assetContainer.assets
            .fold(setOf<Path>(Path.of("."))) { acc, asset -> acc + asset.parentDirectories() }
            .sortedBy { it.toString() }

        val localAssets = container.assets.filterAtPath(container.logicalPath)

        addSuperinterface(AssetContainer::class)

        addProperty(
            propertySpec = PropertySpec.builder(
                name = "logicalPath",
                type = Path::class,
                modifiers = setOf(KModifier.OVERRIDE)
            )
            .initializer("%T.of(%S)", Path::class, container.logicalPath)
            .build()
        )

        addProperty(
            propertySpec = PropertySpec.builder(
                name = "mountPath",
                type = Path::class.asTypeName().copy(nullable = true),
                modifiers = setOf(KModifier.OVERRIDE)
            )
            .mutable()
            .initializer("null")
            .build()
        )

        addAssets(localAssets)

        addProperty(
            propertySpec = PropertySpec.builder(
                name = "assets",
                type = List::class.asTypeName().parameterizedBy(Asset::class.asTypeName()),
                modifiers = setOf(KModifier.OVERRIDE)
            )
            .initializer(
                CodeBlock.builder()
                    .add("listOf(")
                    .add(container.assets.joinToString(", ") { nameAllocator[it] })
                    .add(")")
                    .build()
            )
            .build()
        )

        return this
    }

    private fun List<Asset>.filterAtPath(path: Path) = filter { asset ->
        asset.absoluteLogicalPath.normalize().parent == path.normalize()
    }

    private fun containerName(container: AssetContainer): String {
        return nameAllocator.newName(container.logicalPath.name.camelCase(), container)
    }

    private fun asset(asset: Asset, dependencies: List<Dependency>): PropertySpec {
        val builder = PropertySpec.builder(
            name = assetName(asset),
            type = asset::class.asTypeName(),
        )

        val dependencyPaths = dependencies.map {
            CodeBlock.of("%T.of(%S)", Path::class, it.logicalPath.normalize())
        }.joinToCode(", ")

        when (asset) {
            is Asset.JavaScript -> {
                builder.initializer(
                    "%T(%S, %T.of(%S), %S, %N, listOf(%L))",
                    Asset.JavaScript::class,
                    asset.module ?: "",
                    Path::class,
                    asset.logicalPath,
                    asset.digest,
                    nameAllocator[asset.container],
                    dependencyPaths
                )
            }

            else -> {
                builder.initializer(
                    "%T(%T.of(%S), %S, %N, listOf(%L))",
                    asset::class,
                    Path::class,
                    asset.logicalPath,
                    asset.digest,
                    AssetContainer::class,
                    asset.container.logicalPath,
                    dependencyPaths
                )
            }
        }

        return builder.build()
    }

    private fun assetName(asset: Asset): String {
        return nameAllocator.newName(asset.logicalPath.nameWithoutExtension.camelCase(), asset)
    }

    private fun directoryName(path: Path): String {
        return nameAllocator.newName(path.name.camelCase(), path)
    }

    private fun String.camelCase(): String = replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }*/

    private fun Asset.parentDirectories(): Set<Path> {
        val parentDirectories = mutableSetOf<Path>()
        var parent = logicalPath.normalize().parent
        while (parent != null) {
            parentDirectories.add(parent)
            parent = parent.parent
        }
        return parentDirectories
    }
}