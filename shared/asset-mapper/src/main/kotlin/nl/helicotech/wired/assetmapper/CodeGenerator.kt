package nl.helicotech.wired.assetmapper

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec

class CodeGenerator(
    val settings: Settings = Settings.DEFAULT
) {

    data class Settings(
        val wiredAssetMapperPackage: String,
        val assetType: String,
        val assetDirectoryType: String,
        val assetFileType: String
    ) {
        companion object {
            val DEFAULT = Settings(
                wiredAssetMapperPackage = Asset::class.java.packageName,
                assetType = Asset::class.simpleName!!,
                assetDirectoryType = Asset.Directory::class.simpleName!!,
                assetFileType = Asset.File::class.simpleName!!
            )
        }
    }

    fun generate(
        fileName: String,
        packageName: String,
        assetDirectory: Asset.Directory
    ) = FileSpec
        .builder(packageName, fileName)
        .assetDirectory(assetDirectory)
        .build()

    private fun FileSpec.Builder.assetDirectory(assetDirectory: Asset.Directory) = addType(assetDirectoryType(assetDirectory))

    private fun assetDirectoryType(assetDirectory: Asset.Directory): TypeSpec {
        //val builder = TypeSpec.objectBuilder(assetDirectory.name)
        TODO()
    }
}