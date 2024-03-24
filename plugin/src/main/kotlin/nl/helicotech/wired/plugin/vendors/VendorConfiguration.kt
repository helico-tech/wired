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