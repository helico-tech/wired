package nl.helicotech.wired.plugin.assetmapper

import nl.helicotech.wired.plugin.WiredExtension
import org.gradle.api.Project
import org.gradle.api.provider.Property
import java.io.File
import javax.inject.Inject

interface AssetMapperConfiguration {

    val packageName: Property<String>
    val generatedSourceDirectory: Property<File>
    val generatedResourceDirectory: Property<File>

    val assetDirectories: Set<File>
    fun include(directory: File)
    fun include(directory: String) = include(File(directory))
}

abstract class AssetMapperConfigurationImpl @Inject constructor(
    private val project: Project,
    private val extension: WiredExtension
) : AssetMapperConfiguration {

    override val assetDirectories = mutableSetOf<File>()

    init {
        generatedSourceDirectory.convention(extension.generatedSourceDirectory.get().resolve("asset-mapper"))
        generatedResourceDirectory.convention(extension.generatedResourcesDirectory.get().resolve("asset-mapper"))
    }

    override fun include(directory: File) {
        project.logger.lifecycle("Adding asset directory {}", directory)
        assetDirectories.add(directory)
    }
}