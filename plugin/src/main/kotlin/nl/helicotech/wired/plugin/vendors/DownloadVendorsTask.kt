package nl.helicotech.wired.plugin.vendors

import kotlinx.coroutines.runBlocking
import nl.helicotech.wired.plugin.WiredExtension
import nl.helicotech.wired.plugin.WiredPlugin
import org.gradle.api.DefaultTask
import org.gradle.api.logging.LogLevel
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.File
import javax.inject.Inject

abstract class DownloadVendorsTask @Inject constructor(
    private val extension: WiredExtension,
) : DefaultTask() {

    companion object {
        val NAME = "downloadVendors"
        val DESCRIPTION = "Download vendors"
    }

    @get:Input
    abstract val client: Property<VendorDownloader>

    @get:Input
    abstract val configuration: Property<VendorConfiguration>

    init {
        group = WiredPlugin.GROUP
        description = DESCRIPTION

        client.convention(JsDelivrVendorDownloader())
        configuration.convention(extension.vendorConfiguration)
    }

    @TaskAction
    fun run() = runBlocking {
        clearVendors()
        downloadVendors()
    }

    private fun clearVendors() {
        if (configuration.get().downloadDirectory.get().exists()) {
            configuration.get().downloadDirectory.get().deleteRecursively()
        }
    }

    private suspend fun downloadVendors() {
        logger.log(LogLevel.INFO, "Downloading vendor dependencies")
        val vendors = configuration.get().vendors

        vendors.forEach {
            downloadVendorDependency(it)
        }
    }

    private suspend fun downloadVendorDependency(dependency: Vendor) {
        val downloadDirectory = configuration.get().downloadDirectory.get()
        logger.log(LogLevel.INFO, "Downloading: ${dependency.packageName}@${dependency.version}")

        val result = client.get().download(dependency)

        val fileName = generateFileName(dependency, result)

        val extension = result.fileExtensions().first()

        val outputFile = File(downloadDirectory.resolve(extension), fileName)

        outputFile.parentFile.mkdirs()

        outputFile.writeText(result.contents)
    }

    private fun generateFileName(dependency: Vendor, result: VendorDownloader.DownloadResult): String {
        return "${dependency.packageName}.${result.fileExtensions().first()}"
    }
}