package nl.helicotech.wired.assetmapper

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import java.io.File

class CodeGenerator(
    val rootAsset: Asset.Directory,
    val rootDirectoryOverride: File? = null,
    val rootObjectNameOverride: String? = null,
    val types: Types = Types.DEFAULT,
) {

    private val nameAllocator: NameAllocator = NameAllocator()

    data class Types(
        val wiredAssetMapperPackage: String,
        val assetType: String,
        val assetDirectoryType: String,
        val assetFileType: String
    ) {
        companion object {
            val DEFAULT = Types(
                wiredAssetMapperPackage = Asset::class.java.packageName,
                assetType = Asset::class.simpleName!!,
                assetDirectoryType = Asset.Directory::class.simpleName!!,
                assetFileType = Asset.File::class.simpleName!!,
            )
        }
    }

    private val assetDirectoryClassName = ClassName(this.types.wiredAssetMapperPackage, this.types.assetType, this.types.assetDirectoryType)
    private val assetFileClassName = ClassName(this.types.wiredAssetMapperPackage, this.types.assetType, this.types.assetFileType)

    fun generate(
        fileName: String,
        packageName: String,
    ) = FileSpec
        .builder(packageName, fileName)
        .assetDirectory(rootAsset)
        .build()

    fun generateString(
        fileName: String,
        packageName: String,
    ) = generate(fileName, packageName).toString()

    private fun FileSpec.Builder.assetDirectory(assetDirectory: Asset.Directory) = addType(assetDirectoryType(assetDirectory))
    private fun TypeSpec.Builder.assetDirectory(assetDirectory: Asset.Directory) = addType(assetDirectoryType(assetDirectory))
    private fun TypeSpec.Builder.assetFile(assetFile: Asset.File) = addType(assetFileType(assetFile))

    private fun assetDirectoryType(assetDirectory: Asset.Directory): TypeSpec {
        val builder = TypeSpec.objectBuilder(assetName(assetDirectory))

        assetDirectory.directories.forEach { directory ->
            builder.assetDirectory(directory)
        }

        assetDirectory.files.forEach { file ->
            builder.assetFile(file)
        }

        builder.implementAssetDirectory(assetDirectory)

        return builder.build()
    }

    private fun assetFileType(assetFile: Asset.File): TypeSpec {
        val builder = TypeSpec.objectBuilder(assetName(assetFile))

        builder.implementAssetFile(assetFile)

        return builder.build()
    }

    private fun TypeSpec.Builder.implementAssetDirectory(assetDirectory: Asset.Directory) {
        addSuperinterface(assetDirectoryClassName)

        addProperty(nameProperty(assetDirectory))
        addProperty(fileProperty(assetDirectory))
        addProperty(directoriesProperty(assetDirectory))
        addProperty(filesProperty(assetDirectory))
    }

    private fun directoriesProperty(assetDirectory: Asset.Directory): PropertySpec {
        val builder = PropertySpec.builder(
            name = "directories",
            type = List::class.asClassName().parameterizedBy(assetDirectoryClassName),
            modifiers = setOf(KModifier.PUBLIC, KModifier.OVERRIDE)
        ).initializer(
            format = "listOf(%L)",
            assetDirectory.directories.joinToString(separator = ", ") { nameAllocator.get(it.file) }
        )

        return builder.build()
    }

    private fun filesProperty(assetDirectory: Asset.Directory): PropertySpec {
        val builder = PropertySpec.builder(
            name = "files",
            type = List::class.asClassName().parameterizedBy(assetFileClassName),
            modifiers = setOf(KModifier.PUBLIC, KModifier.OVERRIDE)
        ).initializer(
            format = "listOf(%L)",
            assetDirectory.files.joinToString(separator = ", ") { nameAllocator.get(it.file) }
        )

        return builder.build()
    }

    private fun TypeSpec.Builder.implementAssetFile(assetFile: Asset.File) {
        addSuperinterface(assetFileClassName)

        addProperty(nameProperty(assetFile))
        addProperty(fileProperty(assetFile))
        addProperty(hashProperty(assetFile))
    }

    private fun nameProperty(asset: Asset): PropertySpec {
        val builder = PropertySpec.builder(
            name = "name",
            type = String::class,
            modifiers = setOf(KModifier.PUBLIC, KModifier.OVERRIDE)
        ).initializer("%S", asset.name)

        return builder.build()
    }

    private fun fileProperty(asset: Asset): PropertySpec {
        val file = when (asset.file) {
            rootAsset.file -> rootDirectoryOverride ?: asset.file
            else -> asset.file.relativeTo(rootAsset.file)
        }

        val builder = PropertySpec.builder(
            name = "file",
            type = File::class,
            modifiers = setOf(KModifier.PUBLIC, KModifier.OVERRIDE)
        ).initializer(
            format = "%T(%S)",
            File::class,
            file
        )

        return builder.build()
    }

    private fun hashProperty(asset: Asset.File): PropertySpec {
        val builder = PropertySpec.builder(
            name = "hash",
            type = String::class,
            modifiers = setOf(KModifier.PUBLIC, KModifier.OVERRIDE)
        ).initializer("%S", asset.hash)

        return builder.build()
    }

    private fun assetName(asset: Asset): String {
        val name = when (asset) {
            rootAsset -> rootObjectNameOverride ?: asset.file.name
            else -> asset.file.name
        }
        return nameAllocator.newName(name, asset.file)
    }
}