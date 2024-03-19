package nl.helicotech.wired.plugin.assetmapper

import nl.helicotech.wired.assetmapper.Asset
import nl.helicotech.wired.assetmapper.AssetResolver
import org.gradle.api.Project
import java.io.File
import javax.inject.Inject

interface AssetMapperBuilder {
    val assetDirectories: Set<Asset.Directory>
    fun include(directory: File)
    fun include(directory: String) = include(File(directory))
}

abstract class AssetMapperBuilderImpl @Inject constructor(
    private val project: Project,
) : AssetMapperBuilder {

    private val assetResolver: AssetResolver = AssetResolver()

    override val assetDirectories = mutableSetOf<Asset.Directory>()
    override fun include(directory: File) {
        if (!directory.isAbsolute) {
            include(project.file(directory))
            return
        }

        require(directory.exists()) { "File $directory does not exist" }
        require(directory.isDirectory) { "Directory $directory is not a directory" }

        val assets = assetResolver.resolve(directory)
        project.logger.lifecycle("Adding asset directory {}", assets.file)
        assetDirectories.add(assets)
    }
}