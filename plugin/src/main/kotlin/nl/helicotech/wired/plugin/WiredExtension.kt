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

    val assetMapperConfiguration : AssetMapperConfiguration by lazy { objectFactory.newInstance(AssetMapperConfigurationImpl::class.java) }
    val vendorConfiguration : VendorConfiguration by lazy { objectFactory.newInstance(VendorConfigurationImpl::class.java, this) }

    init {
        //vendors.convention(objectFactory.newInstance(Vendors::class.java))

        // val resourcesDirectory = sourceSetContainer.getByName(MAIN_SOURCE_SET_NAME).resources.srcDirs.first()

        buildDirectory.convention(project.layout.buildDirectory.asFile.get().resolve(NAME))
        //generatedDirectory.convention(buildDirectory.get().resolve(DEFAULT_GENERATED_DIRECTORY))
        //vendorDirectory.convention(buildDirectory.get().resolve(DEFAULT_ASSETS_DIRECTORY).resolve(DEFAULT_VENDOR_DIRECTORY))*/
    }

    fun assetMapper(action: AssetMapperConfiguration.() -> Unit) {
        assetMapperConfiguration.action()
    }

    fun vendors(action: VendorConfiguration.() -> Unit) {
        vendorConfiguration.action()
    }

    //fun vendor(packageName: String, version: String) = vendors.get().vendor(packageName, version)
}