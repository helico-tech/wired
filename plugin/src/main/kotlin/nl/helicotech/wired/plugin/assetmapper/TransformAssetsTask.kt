package nl.helicotech.wired.plugin.assetmapper

import nl.helicotech.wired.assetmapper.AssetResolver
import nl.helicotech.wired.assetmapper.hashedFile
import nl.helicotech.wired.assetmapper.hashedName
import nl.helicotech.wired.assetmapper.traverseFiles
import nl.helicotech.wired.plugin.WiredExtension
import nl.helicotech.wired.plugin.WiredPlugin
import org.gradle.api.DefaultTask
import org.gradle.api.Project
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

    private val resolver = AssetResolver()

    @TaskAction
    fun run() {
        transformAssets()
    }

    fun registerResources() {
        val main = sourceSetContainer.getByName(MAIN_SOURCE_SET_NAME)
        main.resources.srcDir(extension.generatedResourcesDirectory.get())
    }

    fun removeResourcesFromProcessResourcesTask() {
        /*val assetDirectories = extension.assetMapperConfiguration.assetDirectories.map { project.file(it) }
        project.tasks.withType(ProcessResources::class.java).forEach {
            it.exclude {
                assetDirectories.any { assetDirectory -> it.file.canonicalPath.startsWith(assetDirectory.canonicalPath) }
            }
        }*/
    }

    private fun transformAssets() {
        /*extension.assetMapperConfiguration.assetDirectories.forEach { directory ->
            transformAsset(directory)
        }*/
    }

    private fun transformAsset(directory: File) {
        /*val rootDirectory = project.projectDir.resolve(directory)
        val rootAsset = resolver.resolve(rootDirectory)

        rootAsset.traverseFiles().forEach { asset ->

            val relativeFile = asset.hashedFile().relativeTo(rootDirectory.parentFile)
            val outputFile = extension.assetMapperConfiguration.generatedResourceDirectory.get().resolve(relativeFile)

            if (asset.file.isDirectory) {
                outputFile.mkdirs()
            } else {
                asset.file.copyTo(outputFile, overwrite = true)
            }
        }*/
    }
}