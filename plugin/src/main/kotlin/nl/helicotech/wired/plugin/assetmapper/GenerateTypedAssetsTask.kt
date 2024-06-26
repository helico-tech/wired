package nl.helicotech.wired.plugin.assetmapper

import nl.helicotech.wired.assetmapper.Asset
import nl.helicotech.wired.assetmapper.AssetResolver
import nl.helicotech.wired.plugin.WiredExtension
import nl.helicotech.wired.plugin.WiredPlugin
import nl.helicotech.wired.plugin.assetmapper.codegen.TypedAssetGenerator
import nl.helicotech.wired.plugin.vendors.DownloadVendorsTask
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.SourceSet.MAIN_SOURCE_SET_NAME
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.util.*
import javax.inject.Inject

abstract class GenerateTypedAssetsTask @Inject constructor(
    private val project: Project,
    private val sourceSetContainer: SourceSetContainer,
    private val extension: WiredExtension,
) : DefaultTask() {

    companion object {
        val NAME = "generateTypedAssets"
        val DESCRIPTION = "Generate typed assets"
    }

    @get:Nested
    abstract val configuration: Property<AssetMapperConfiguration>

    @get:OutputFiles
    val outputFiles = mutableListOf<File>()

    private val resolver = AssetResolver()

    init {
        group = WiredPlugin.GROUP
        description = DESCRIPTION

        configuration.convention(extension.assetMapperConfiguration)

        dependsOn(DownloadVendorsTask.NAME)

        project.tasks.getByName("compileKotlin").dependsOn(this)
    }

    @TaskAction
    fun run() {
        require(extension.assetMapperConfiguration.packageName.isPresent) { "packageName is required" }

        extension.assetMapperConfiguration.generatedSourceDirectory.get().asFile.listFiles()?.forEach { it.deleteRecursively() }

        extension.assetMapperConfiguration.assetDirectories.get().forEach { directory ->
            generateTypedAsset(directory)
        }
    }


    fun registerSourceSet() {
        val main = sourceSetContainer.getByName(MAIN_SOURCE_SET_NAME)
        (main.extensions.getByName("kotlin") as SourceDirectorySet).apply {
            srcDir(extension.generatedSourceDirectory.get())
        }
    }

    private fun generateTypedAsset(directory: File) {
        logger.info("Generating typed assets for {}", directory)

        val rootAsset = resolver.resolve(project.projectDir.resolve(directory))

        val rootObjectName = directory.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

        val outputFile = extension.assetMapperConfiguration.generatedSourceDirectory.get().dir("$rootObjectName.kt").asFile

        val generator = TypedAssetGenerator(
            rootAsset = rootAsset,
            rootDirectoryOverride = File(rootAsset.file.name)
        )

        val source = generator.generateString(
            packageName = extension.assetMapperConfiguration.packageName.get(),
            fileName = directory.name,
        )

        if (!outputFile.parentFile.exists()) {
            outputFile.parentFile.mkdirs()
        }

        outputFile.writeText(source)

        outputFiles.add(outputFile)
    }
}