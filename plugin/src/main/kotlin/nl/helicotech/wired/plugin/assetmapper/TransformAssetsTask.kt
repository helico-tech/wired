package nl.helicotech.wired.plugin.assetmapper

import nl.helicotech.wired.assetmapper.*
import nl.helicotech.wired.plugin.WiredExtension
import nl.helicotech.wired.plugin.WiredPlugin
import nl.helicotech.wired.plugin.assetmapper.transformer.CssTransformer
import nl.helicotech.wired.plugin.assetmapper.transformer.Transformer
import nl.helicotech.wired.plugin.assetmapper.transformer.UnitTransformer
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.SourceSet.MAIN_SOURCE_SET_NAME
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskAction
import org.gradle.language.jvm.tasks.ProcessResources
import java.io.File
import javax.inject.Inject

abstract class TransformAssetsTask @Inject constructor(
    private val project: Project,
    private val sourceSetContainer: SourceSetContainer,
    private val extension: WiredExtension,
) : DefaultTask() {
    companion object {
        const val NAME = "transformAssets"
        const val DESCRIPTION = "Transforms assets"
    }

    init {
        group = WiredPlugin.GROUP
        description = DESCRIPTION

        this.dependsOn(GenerateTypedAssetsTask.NAME)

        project.tasks.withType(ProcessResources::class.java).forEach {
            it.dependsOn(this)
        }
    }

    @get:OutputFiles
    val outputFiles = mutableListOf<File>()

    private val resolver = AssetResolver()

    private val assets = mutableListOf<Asset.Directory>()

    private val transformers = mutableListOf(
        CssTransformer(assets),
        UnitTransformer(),
    )

    @TaskAction
    fun run() {
        resolveAssets()
        transformAssets()
    }

    fun registerResources() {
        val main = sourceSetContainer.getByName(MAIN_SOURCE_SET_NAME)
        main.resources.srcDir(extension.generatedResourcesDirectory.get())
    }

    fun removeResourcesFromProcessResourcesTask() {
        val assetDirectories = extension.assetMapperConfiguration.assetDirectories.get().map { project.file(it) }
        project.tasks.withType(ProcessResources::class.java).forEach {
            it.exclude {
                assetDirectories.any { assetDirectory -> it.file.canonicalPath.startsWith(assetDirectory.canonicalPath) }
            }
        }
    }

    private fun resolveAssets() {
        extension.assetMapperConfiguration.assetDirectories.get().forEach { directory ->
            val rootDirectory = project.projectDir.resolve(directory)
            val rootAsset = resolver.resolve(rootDirectory)

            assets.add(rootAsset)
        }
    }

    private fun transformAssets() {
        assets.forEach { asset ->
            transformAsset(asset)
        }
    }

    private fun transformAsset(rootAsset: Asset.Directory) {
        val rootDirectory = project.projectDir.resolve(rootAsset.file)

        rootAsset.traverseFiles().forEach { asset ->
            val transformer = transformers.firstOrNull { it.accepts(asset) } ?: throw IllegalStateException("No transformer found for asset: $asset")

            val relativeFile = asset.hashedFile().relativeTo(rootDirectory.parentFile)
            val outputFile = extension.assetMapperConfiguration.generatedResourceDirectory.get().file(relativeFile.path).asFile

            outputFile.parentFile.mkdirs()
            outputFile.createNewFile()
            outputFile.writeText(transformer.transform(asset))
            outputFiles.add(outputFile)
        }
    }
}