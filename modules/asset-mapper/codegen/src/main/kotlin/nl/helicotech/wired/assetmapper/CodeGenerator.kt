package nl.helicotech.wired.assetmapper

import com.squareup.kotlinpoet.*
import java.nio.file.Path
import java.util.*
import kotlin.io.path.name

class CodeGenerator(
    private val assetContainer: AssetContainer,
    private val packageName: String,
    private val fileName: String
) {
    private val nameAllocator = NameAllocator()

    fun generate(): FileSpec = FileSpec.builder(packageName, fileName)
        .addAssetContainer(assetContainer)
        .build()

    private fun FileSpec.Builder.addAssetContainer(assetContainer: AssetContainer) = addType(assetContainer(assetContainer))

    private fun assetContainer(container: AssetContainer): TypeSpec {
        val builder = TypeSpec.objectBuilder(containerName(container))

        builder.implementAssetContainer(container)

        return builder.build()
    }

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

        return this
    }

    private fun containerName(container: AssetContainer): String {
        return nameAllocator.newName(container.logicalPath.name.camelCase(), container)
    }

    private fun String.camelCase(): String = replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
}