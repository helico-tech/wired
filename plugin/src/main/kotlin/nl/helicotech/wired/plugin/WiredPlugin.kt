package nl.helicotech.wired.plugin

import nl.helicotech.wired.plugin.tasks.DownloadVendorDependenciesTask
import nl.helicotech.wired.plugin.tasks.GenerateTypedAssetsTask
import nl.helicotech.wired.plugin.tasks.InitializeDirectoriesTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.kotlin.dsl.get

abstract class WiredPlugin: Plugin<Project> {

    companion object {
        val GROUP = "wired"
    }

    override fun apply(target: Project): Unit = with(target) {

        val extension = extensions.create(
            WiredExtension.NAME,
            WiredExtension::class.java
        )

        tasks.register(
            DownloadVendorDependenciesTask.NAME,
            DownloadVendorDependenciesTask::class.java,
            extension
        )

        tasks.register(
            InitializeDirectoriesTask.NAME,
            InitializeDirectoriesTask::class.java,
            extension
        )

        tasks.register(
            GenerateTypedAssetsTask.NAME,
            GenerateTypedAssetsTask::class.java,
            extension
        )

        afterEvaluate {
            tasks.getByName(DownloadVendorDependenciesTask.NAME).dependsOn(tasks.getByName(InitializeDirectoriesTask.NAME))

            tasks.getByName(GenerateTypedAssetsTask.NAME).dependsOn(tasks.getByName(DownloadVendorDependenciesTask.NAME))
            tasks.getByName("processResources").dependsOn(tasks.getByName(DownloadVendorDependenciesTask.NAME))

            tasks.getByName("build").dependsOn(tasks.getByName(GenerateTypedAssetsTask.NAME))

            (tasks.getByName(GenerateTypedAssetsTask.NAME) as GenerateTypedAssetsTask).registerSourceSet()
        }
    }
}