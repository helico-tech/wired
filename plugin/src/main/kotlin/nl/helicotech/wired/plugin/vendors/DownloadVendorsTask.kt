package nl.helicotech.wired.plugin.vendors

import kotlinx.coroutines.runBlocking
import nl.helicotech.wired.plugin.WiredExtension
import nl.helicotech.wired.plugin.WiredPlugin
import org.gradle.api.DefaultTask
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.TaskAction
import java.io.File
import javax.inject.Inject

abstract class DownloadVendorsTask @Inject constructor(
    private val extension: WiredExtension,
    private val objectFactory: ObjectFactory
) : DefaultTask() {

    companion object {
        val NAME = "downloadVendors"
        val DESCRIPTION = "Download vendors"
    }

    private val client = JsDelivrVendorDownloader()

    @get:Nested
    abstract val configuration: Property<VendorConfiguration>

    @get:OutputFiles
    val outputFiles = mutableListOf<File>()

    init {
        group = WiredPlugin.GROUP
        description = DESCRIPTION

        configuration.convention(extension.vendorConfiguration)
    }

    @TaskAction
    fun run() = runBlocking {
        clearVendors()
        downloadVendors()
    }

    private fun clearVendors() {
        logger.lifecycle("Clearing vendor dependencies")
        if (configuration.get().downloadDirectory.get().asFile.exists()) {
            configuration.get().downloadDirectory.get().asFile.deleteRecursively()
        }
    }

    private suspend fun downloadVendors() {
        logger.lifecycle("Downloading vendor dependencies")
        val vendors = configuration.get().vendors

        vendors.get().forEach { (packageName, version) ->
            downloadVendorDependency(packageName, version)
        }
    }

    private suspend fun downloadVendorDependency(packageName: String, version: String) {
        logger.lifecycle("Downloading: ${packageName}@${version}")

        val downloadDirectory = configuration.get().downloadDirectory.get()

        val result = client.download(packageName, version)

        val fileName = generateFileName(packageName, result)

        val extension = result.fileExtensions().first()

        val outputFile = downloadDirectory.dir(extension).file(fileName).asFile

        outputFile.parentFile.mkdirs()

        outputFile.writeText(result.contents)

        outputFiles.add(outputFile)
    }

    private fun generateFileName(packageName: String, result: VendorDownloader.DownloadResult): String {
        return "${packageName}.${result.fileExtensions().first()}"
    }
}