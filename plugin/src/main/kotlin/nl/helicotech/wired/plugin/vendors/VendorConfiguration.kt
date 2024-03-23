package nl.helicotech.wired.plugin.vendors

import nl.helicotech.wired.plugin.WiredExtension
import org.gradle.api.provider.Property
import java.io.File
import javax.inject.Inject

interface VendorConfiguration {
    companion object {
        val DEFAULT_VENDOR_DIRECTORY = "vendors"
    }

    val downloadDirectory: Property<File>

    val vendors : Set<Vendor>
    fun include(vendor: Vendor)
    fun include(packageName: String, version: String) = include(Vendor(packageName, version))
}

abstract class VendorConfigurationImpl @Inject constructor(
    extension: WiredExtension
): VendorConfiguration {
    init {
        downloadDirectory.convention(extension.buildDirectory.get().resolve(VendorConfiguration.DEFAULT_VENDOR_DIRECTORY))
    }

    override val vendors = mutableSetOf<Vendor>()
    override fun include(vendor: Vendor) {
        vendors.add(vendor)
    }
}