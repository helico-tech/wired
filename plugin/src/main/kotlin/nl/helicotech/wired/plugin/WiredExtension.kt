package nl.helicotech.wired.plugin

import nl.helicotech.wired.plugin.assetmapper.AssetMapperConfiguration
import nl.helicotech.wired.plugin.vendors.VendorConfiguration
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

abstract class WiredExtension @Inject constructor(
    project: Project,
    objectFactory: ObjectFactory,
) {
    companion object {
        val NAME = "wired"
    }

    abstract val buildDirectory : DirectoryProperty
    abstract val generatedDirectory : DirectoryProperty
    abstract val generatedSourceDirectory : DirectoryProperty
    abstract val generatedResourcesDirectory : DirectoryProperty

    val vendorConfiguration : VendorConfiguration by lazy { objectFactory.newInstance(VendorConfiguration::class.java, this) }

    init {
        buildDirectory.convention(project.layout.buildDirectory.dir(NAME))
        generatedDirectory.convention(buildDirectory.dir("generated"))
        generatedSourceDirectory.convention(generatedDirectory.dir("kotlin"))
        generatedResourcesDirectory.convention(generatedDirectory.dir("resources"))
    }

    fun assetMapper(action: AssetMapperConfiguration.() -> Unit) {
        //assetMapperConfiguration.action()
    }

    fun vendors(action: VendorConfiguration.() -> Unit) {
        vendorConfiguration.action()
    }
}