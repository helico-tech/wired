package nl.helicotech.wired.plugin.tasks

import nl.helicotech.wired.plugin.WiredExtension
import nl.helicotech.wired.plugin.WiredPlugin
import org.gradle.api.DefaultTask
import org.gradle.api.logging.LogLevel
import org.gradle.api.tasks.TaskAction
import java.io.File
import javax.inject.Inject

abstract class InitializeDirectoriesTask @Inject constructor(
    private val extension: WiredExtension,
) : DefaultTask() {
    companion object {
        val NAME = "initializeDirectories"
        val DESCRIPTION = "Initialize directories"
    }

    init {
        group = WiredPlugin.GROUP
        description = DESCRIPTION
    }

    @TaskAction
    fun run() {
        initializeDirectories()
    }

    private fun initializeDirectories() {
        initializeDirectory(extension.assetsDirectory.get())
        initializeDirectory(extension.jsDirectory.get())
        initializeDirectory(extension.cssDirectory.get())

        initializeDirectory(extension.vendorDirectory.get(), keep = false)
    }

    private fun initializeDirectory(directory: File, keep: Boolean = true) {
        logger.log(LogLevel.INFO, "Initializing directory: ${directory.absolutePath}")
        directory.mkdirs()

        if (keep) {
            val gitKeep = File(directory, ".gitkeep")
            gitKeep.createNewFile()
        }
    }
}