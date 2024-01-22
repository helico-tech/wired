package nl.helicotech.wired.plugin.codegen

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import io.ktor.util.*
import java.io.File


class TypedAssetsCodeGenerator(
    private val packageName: String,
    private val fileName: String,
    private val assetFolder: File,
) {

    companion object {
        val WIRED_PACKAGE = "nl.helicotech.wired"
        val ASSET_TYPE = "Asset"
        val ASSET_FOLDER_TYPE = "AssetFolder"
        val ASSET_FILE_TYPE = "AssetFile"
    }

    init {
        require(assetFolder.isDirectory) {
            "Asset folder must be a directory"
        }
    }

    fun generate() = FileSpec
        .builder(
            packageName = packageName,
            fileName = fileName
        )
        .suppressWarningTypes("RedundantVisibilityModifier")
        .assets()
        .build()

    private fun FileSpec.Builder.assets() = addType(
        typeSpec = assetFolder(
            file = assetFolder,
            name = "Assets"
        )
    )

    private fun assetFolder(
        file: File,
        name: String? = null,
        registerAsset: (String) -> Unit = {},
    ): TypeSpec {
        val assets = mutableListOf<String>()

        val containerName = name ?: createObjectName(file)
        val builder = TypeSpec.objectBuilder(containerName)
            .addSuperinterface(ClassName(WIRED_PACKAGE, ASSET_FOLDER_TYPE))

        registerAsset(containerName)

        file.listFiles()?.forEach { child ->
            builder.asset(
                file = child,
                registerAsset = { assets.add(it) }
            )
        }

        builder.addProperty(
            propertySpec = PropertySpec.builder(
                name = "path",
                type = File::class,
                modifiers = setOf(KModifier.PUBLIC, KModifier.OVERRIDE)
            )
                .initializer(
                    format = "%T(%S)",
                    File::class,
                    file.relativeTo(assetFolder.parentFile).path
                )
                .build()
        )

        builder.addProperty(
            propertySpec = PropertySpec.builder(
                name = "assets",
                type = List::class.asTypeName().parameterizedBy(ClassName(WIRED_PACKAGE, ASSET_TYPE)),
                modifiers = setOf(KModifier.OVERRIDE)
            )
                .initializer(
                    format = "listOf(%L)",
                    assets.joinToString(separator = ", ")
                )
                .build()
        )

        return builder.build()
    }

    private fun TypeSpec.Builder.asset(
        file: File,
        registerAsset: (String) -> Unit = {}
    ): TypeSpec.Builder = when {
        file.isDirectory -> assetFolder(file, registerAsset)
        else -> assetFile(file, registerAsset)
    }

    private fun TypeSpec.Builder.assetFolder(
        file: File,
        registerAsset: (String) -> Unit = {}
    ) = addType(
        this@TypedAssetsCodeGenerator.assetFolder(
            file = file,
            registerAsset = registerAsset
        )
    )

    private fun TypeSpec.Builder.assetFile(
        file: File,
        registerAsset: (String) -> Unit = {}
    ): TypeSpec.Builder {
        val name = createObjectName(file)
        registerAsset(name)

        addProperty(
            propertySpec = PropertySpec.builder(
                name = name,
                type = ClassName(WIRED_PACKAGE, ASSET_FILE_TYPE),
            )
                .initializer(
                    format = "%T(path=%T(%S), name=%S, hash=%S)",
                    ClassName(WIRED_PACKAGE, ASSET_FILE_TYPE),
                    ClassName("java.io", "File"),
                    file.relativeTo(assetFolder.parentFile).path,
                    createAssetName(file.relativeTo(assetFolder)),
                    file.sha1()
                )
                .build()
        )

        return this
    }

    private fun File.sha1(): String = sha1(readBytes()).joinToString(separator = "") { eachByte -> "%02x".format(eachByte) }

    private fun createObjectName(file: File): String = "`${file.nameWithoutExtension}`"

    private fun createAssetName(file: File): String = when {
        file.parentFile.name.startsWith("@") -> "${file.parentFile.name}/${file.nameWithoutExtension}"
        else -> file.nameWithoutExtension
    }
}

internal fun FileSpec.Builder.suppressWarningTypes(vararg types: String): FileSpec.Builder {
    if (types.isEmpty()) {
        return this
    }

    val format = "%S,".repeat(types.count()).trimEnd(',')
    addAnnotation(
        AnnotationSpec.builder(ClassName("", "Suppress"))
            .addMember(format, *types)
            .build()
    )

    return this
}