package nl.helicotech.wired.plugin.assetmapper

import nl.helicotech.wired.assetmapper.AssetResolver
import nl.helicotech.wired.plugin.WiredExtension
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import java.io.File
import javax.inject.Inject


abstract class AssetMapperConfiguration @Inject constructor(
    project: Project,
    extension: WiredExtension
)  {

    @get:Input
    abstract val packageName: Property<String>

    @get:InputFiles
    abstract val assetDirectories: SetProperty<File>

    @get:InputDirectory
    abstract val generatedSourceDirectory: DirectoryProperty

    @get:InputDirectory
    abstract val generatedResourceDirectory: DirectoryProperty

    init {
        val sourceDir = extension.generatedSourceDirectory.get().dir("asset-mapper")
        val resourceDir = extension.generatedResourcesDirectory.get().dir("asset-mapper")

        sourceDir.asFile.mkdirs()
        resourceDir.asFile.mkdirs()

        generatedSourceDirectory.convention(sourceDir)
        generatedResourceDirectory.convention(resourceDir)
    }

    fun add(directory: File) {
        assetDirectories.add(directory)
    }

    fun add(directory: String) {
        assetDirectories.add(File(directory))
    }
}