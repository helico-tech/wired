package nl.helicotech.wired.plugin

import nl.helicotech.wired.plugin.assetmapper.AssetMapperConfiguration
import nl.helicotech.wired.plugin.assetmapper.AssetMapperConfigurationImpl
import nl.helicotech.wired.plugin.vendors.VendorConfiguration
import nl.helicotech.wired.plugin.vendors.VendorConfigurationImpl
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.SourceSet.MAIN_SOURCE_SET_NAME
import org.gradle.api.tasks.SourceSetContainer
import java.io.File
import javax.inject.Inject

abstract class WiredExtension @Inject constructor(
    project: Project,
    objectFactory: ObjectFactory,
) {
    companion object {
        val NAME = "wired"
    }

    abstract val buildDirectory : Property<File>
    abstract val generatedDirectory : Property<File>
    abstract val generatedSourceDirectory : Property<File>
    abstract val generatedResourcesDirectory : Property<File>

    val assetMapperConfiguration : AssetMapperConfiguration by lazy { objectFactory.newInstance(AssetMapperConfigurationImpl::class.java, this) }
    val vendorConfiguration : VendorConfiguration by lazy { objectFactory.newInstance(VendorConfigurationImpl::class.java, this) }

    init {
        buildDirectory.convention(project.layout.buildDirectory.asFile.get().resolve(NAME))
        generatedDirectory.convention(buildDirectory.get().resolve("generated"))
        generatedSourceDirectory.convention(generatedDirectory.get().resolve("kotlin"))
        generatedResourcesDirectory.convention(generatedDirectory.get().resolve("resources"))
    }

    fun assetMapper(action: AssetMapperConfiguration.() -> Unit) {
        assetMapperConfiguration.action()
    }

    fun vendors(action: VendorConfiguration.() -> Unit) {
        vendorConfiguration.action()
    }
}