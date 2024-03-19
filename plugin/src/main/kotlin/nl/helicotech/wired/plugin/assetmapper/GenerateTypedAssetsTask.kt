package nl.helicotech.wired.plugin.assetmapper

import nl.helicotech.wired.assetmapper.AssetResolver
import nl.helicotech.wired.assetmapper.CodeGenerator
import nl.helicotech.wired.plugin.WiredExtension
import nl.helicotech.wired.plugin.WiredPlugin
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.SourceDirectorySet
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

    init {
        group = WiredPlugin.GROUP
        description = DESCRIPTION
    }

    private val resolver = AssetResolver()

    @TaskAction
    fun run() {
        require(extension.assetMapperConfiguration.packageName.isPresent) { "packageName is required" }
        extension.assetMapperConfiguration.assetDirectories.forEach { directory ->
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
        logger.lifecycle("Generating typed assets for {}", directory)

        val rootAsset = resolver.resolve(project.projectDir.resolve(directory))

        val rootDirectory = extension.assetMapperConfiguration.generatedResourceDirectory.get()

        val rootObjectName = directory.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

        val outputFile = extension.assetMapperConfiguration.generatedSourceDirectory.get().resolve("$rootObjectName.kt")

        val generator = CodeGenerator(
            rootAsset = rootAsset,
            rootDirectoryOverride = rootDirectory.relativeTo(extension.generatedResourcesDirectory.get()),
            rootObjectNameOverride = rootObjectName
        )

        val source = generator.generateString(
            packageName = extension.assetMapperConfiguration.packageName.get(),
            fileName = directory.name,
        )

        if (!outputFile.parentFile.exists()) {
            outputFile.parentFile.mkdirs()
        }

        outputFile.writeText(source)
    }
}