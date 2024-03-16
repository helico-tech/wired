package nl.helicotech.wired.plugin.codegen

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import io.ktor.util.*
import java.io.File


class TypedAssetsCodeGenerator(
    val packageName: String,
    val fileName: String,
    vararg val assetFolders: File
) {

    companion object {
        val WIRED_PACKAGE = "nl.helicotech.wired"
        val ASSET_TYPE = "Asset"
        val ASSET_FOLDER_TYPE = "AssetFolder"
        val ASSET_FILE_TYPE = "AssetFile"
    }

    init {
        require(assetFolders.isNotEmpty()) {
            "At least one asset folder must be provided"
        }

        assetFolders.forEach { assetFolder ->
            require(assetFolder.exists()) {
                "Asset folder must exist"
            }
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

    private fun FileSpec.Builder.assets() = apply {
        addType(
            TypeSpec.objectBuilder("Assets")
                .apply {
                    assetFolders.forEach { assetFolder ->
                        asset(
                            root = assetFolder,
                            file = assetFolder
                        )
                    }
                }
                .build()
        )
    }

    private fun assetFolder(
        root: File,
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
            if (child.name.startsWith(".")) return@forEach
            builder.asset(
                root = root,
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
                    file.relativeTo(root.parentFile).path
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
        root: File,
        file: File,
        registerAsset: (String) -> Unit = {}
    ): TypeSpec.Builder = when {
        file.isDirectory -> assetFolder(root, file, registerAsset)
        else -> assetFile(root, file, registerAsset)
    }

    private fun TypeSpec.Builder.assetFolder(
        root: File,
        file: File,
        registerAsset: (String) -> Unit = {}
    ) = addType(
        this@TypedAssetsCodeGenerator.assetFolder(
            root = root,
            file = file,
            registerAsset = registerAsset
        )
    )

    private fun TypeSpec.Builder.assetFile(
        root: File,
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
                    file.relativeTo(root.parentFile).path,
                    createAssetName(file.relativeTo(root)),
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