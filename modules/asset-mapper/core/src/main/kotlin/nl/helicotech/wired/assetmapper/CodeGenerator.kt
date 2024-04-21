package nl.helicotech.wired.assetmapper

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import nl.helicotech.wired.assetmapper.js.JavascriptDependencyResolver
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.io.path.isDirectory
import kotlin.io.path.name
import kotlin.io.path.nameWithoutExtension

class CodeGenerator(
    private val assetContainer: AssetContainer,
    private val packageName: String,
    private val fileName: String
) {

    private val nameAllocators = mutableMapOf<Path, NameAllocator>()

    private val dependencyResolver = RootDependencyResolver(JavascriptDependencyResolver).create(assetContainer)

    fun generate(): FileSpec {
        nameAllocators.clear()

        val builder = FileSpec.builder(packageName, fileName)

        val assetContainerBuilder = createContainerObjectSpecBuilder(assetContainer.logicalPath)
            .implementAssetContainer(assetContainer)

        builder.addType(assetContainerBuilder.build())

        return builder.build()
    }

    private fun createContainerObjectSpecBuilder(path: Path): TypeSpec.Builder {

        val nameAllocator = nameAllocators.computeIfAbsent(path) {
            when {
                path.parent != null && nameAllocators.containsKey(path.parent) -> nameAllocators[path.parent]!!.copy()
                else -> NameAllocator()
            }
        }

        val name = nameAllocator.newName(path.name.camelCase(), path)

        val builder = TypeSpec.objectBuilder(name)
        builder.tag(nameAllocator)
        builder.tag(path)

        if (path.fromResources().isDirectory()) {
            Files.list(path.fromResources()).filter { it.isDirectory() }.forEach { child ->
                val subPath = path.resolve(child.name)
                val spec = createContainerObjectSpecBuilder(subPath)
                builder.addType(spec.build())
            }
        }

        val assets = getAssetsForPath(path)

        builder.addAssets(assets)

        return builder
    }

    private fun getAssetsForPath(path: Path): List<Asset> {
        return assetContainer.assets.filter { asset ->
            asset.absoluteLogicalPath.normalize().parent == path.normalize()
        }
    }

    private fun TypeSpec.Builder.addAsset(asset: Asset, dependencies: List<Dependency>) = addProperty(asset(asset, dependencies))

    private fun TypeSpec.Builder.addAssets(assets: List<Asset>) {
        val assetsWithDependencies = assets.map { asset ->
            val dependencies = dependencyResolver.resolve(asset).toList()
            asset to dependencies
        }

        assetsWithDependencies.forEach { (asset, dependencies) ->
            addAsset(asset, dependencies)
        }
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
                    nameAllocators[asset.container.logicalPath]!![asset.container.logicalPath],
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
        val nameAllocator = nameAllocators[asset.absoluteLogicalPath.parent]!!
        return nameAllocator.newName(asset.logicalPath.nameWithoutExtension.camelCase(), asset)
    }

    private fun String.camelCase(): String = replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

    private fun TypeSpec.Builder.implementAssetContainer(container: AssetContainer): TypeSpec.Builder {

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

        val assetObjectPaths = container.assets.map { getAssetObjectPath(it).joinToString(".") }

        addProperty(
            propertySpec = PropertySpec.builder(
                name = "assets",
                type = List::class.asTypeName().parameterizedBy(Asset::class.asTypeName()),
                modifiers = setOf(KModifier.OVERRIDE)
            )
            .initializer(
                CodeBlock.builder()
                    .add("listOf(")
                    .add(assetObjectPaths.joinToString(", "))
                    .add(")")
                    .build()
            )
            .build()
        )

        return this
    }

    private fun getAssetObjectPath(asset: Asset): List<String> {
        val assetName = nameAllocators[asset.absoluteLogicalPath.parent]!![asset]

        var currentPath = asset.absoluteLogicalPath.parent
        val paths = mutableListOf<Path>()

        while (currentPath != asset.container.logicalPath) {
            paths.add(currentPath)
            currentPath = currentPath.parent
        }

        paths.reverse()

        val pathNames = paths.map { nameAllocators[it]!![it] }
        return pathNames + assetName
    }
}

