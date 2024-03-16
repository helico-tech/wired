package nl.helicotech.wired.plugin.tasks

import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import nl.helicotech.jsdelivr.client.JsDelivrClient
import nl.helicotech.wired.plugin.WiredExtension
import nl.helicotech.wired.plugin.WiredPlugin
import nl.helicotech.wired.plugin.data.Vendors
import org.gradle.api.DefaultTask
import org.gradle.api.logging.LogLevel
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskAction
import java.io.File
import javax.inject.Inject

abstract class DownloadVendorDependenciesTask @Inject constructor(
    private val extension: WiredExtension,
) : DefaultTask() {

    companion object {
        val NAME = "downloadVendorDependencies"
        val DESCRIPTION = "Download vendor dependencies"
    }

    @get:Input
    abstract val client: Property<JsDelivrClient>

    init {
        group = WiredPlugin.GROUP
        description = DESCRIPTION

        client.convention(JsDelivrClient())
    }

    @TaskAction
    fun run() = runBlocking {
        cleanVendorDirectory()
        downloadVendorDependencies()
    }

    private fun cleanVendorDirectory() {
        /*val vendorDirectory = extension.vendorDirectory.get()
        if (vendorDirectory.exists()) {
            logger.log(LogLevel.INFO, "Cleaning vendor directory")
            vendorDirectory.deleteRecursively()
        }

        vendorDirectory.mkdirs()*/
    }

    private suspend fun downloadVendorDependencies() {
        logger.log(LogLevel.INFO, "Downloading vendor dependencies")

        val vendors = extension.vendors.get().vendors
        vendors.forEach { dependency ->
            downloadVendorDependency(dependency)
        }
    }

    private suspend fun downloadVendorDependency(dependency: Vendors.Vendor) {
        logger.log(LogLevel.INFO, "Downloading: ${dependency.packageName}@${dependency.version}")

        val result = client.get().downloadEsm(dependency.packageName, dependency.version)

        val fileName = generateFileName(dependency, result.contentType)

        //val outputFile = File(extension.vendorDirectory.get(), fileName)

        //outputFile.parentFile.mkdirs()

        //outputFile.writeText(result.contents)
    }

    private fun generateFileName(dependency: Vendors.Vendor, contentType: ContentType): String {
        return "${dependency.packageName}.${contentType.fileExtensions().first()}"
    }
}