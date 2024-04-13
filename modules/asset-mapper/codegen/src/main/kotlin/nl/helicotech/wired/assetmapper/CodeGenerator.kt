package nl.helicotech.wired.assetmapper

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.NameAllocator
import com.squareup.kotlinpoet.TypeSpec
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

    private fun assetContainer(assetContainer: AssetContainer): TypeSpec {
        val builder = TypeSpec.objectBuilder(containerName(assetContainer))

        return builder.build()
    }

    private fun containerName(container: AssetContainer): String {
        return nameAllocator.newName(container.logicalPath.name.camelCase(), container)
    }

    private fun String.camelCase(): String = replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
}