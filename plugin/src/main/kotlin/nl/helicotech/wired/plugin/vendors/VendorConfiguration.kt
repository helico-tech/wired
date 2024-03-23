package nl.helicotech.wired.plugin.vendors

import nl.helicotech.wired.plugin.WiredExtension
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import java.io.File
import javax.inject.Inject

/*interface VendorConfiguration {
    companion object {
        val DEFAULT_VENDOR_DIRECTORY = "vendors"
    }

    val downloadDirectory: OutputDirectory

    val vendors : Set<Vendor>
    fun include(vendor: Vendor)
    fun include(packageName: String, version: String) = include(Vendor(packageName, version))
}

abstract class VendorConfigurationImpl @Inject constructor(
    extension: WiredExtension
): VendorConfiguration {
    init {
        //downloadDirectory.convention(extension.buildDirectory.get().resolve(VendorConfiguration.DEFAULT_VENDOR_DIRECTORY))
        downloadDirectory.
    }

    override val vendors = mutableSetOf<Vendor>()
    override fun include(vendor: Vendor) {
        vendors.add(vendor)
    }
}*/

abstract class VendorConfiguration @Inject constructor(
    extension: WiredExtension
) {
    companion object {
        val DEFAULT_VENDOR_DIRECTORY = "vendors"
    }

    @get:Input
    abstract val vendors: MapProperty<String, String>

    @get:OutputDirectory
    abstract val downloadDirectory: DirectoryProperty

    init {
        downloadDirectory.convention(extension.buildDirectory.dir(DEFAULT_VENDOR_DIRECTORY))
    }

    fun add(packageName: String, version: String) {
        vendors.put(packageName, version)
    }
}