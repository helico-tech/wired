package nl.helicotech.wired.plugin

import nl.helicotech.wired.plugin.vendors.DownloadVendorsTask
import org.gradle.api.Plugin
import org.gradle.api.Project

abstract class WiredPlugin: Plugin<Project> {

    companion object {
        val GROUP = "wired"
    }

    override fun apply(target: Project): Unit = with(target) {

        val extension = extensions.create(
            WiredExtension.NAME,
            WiredExtension::class.java
        )

        /*tasks.register(
            DownloadVendorDependenciesTask.NAME,
            DownloadVendorDependenciesTask::class.java,
            extension
        )*/

        /*tasks.register(
            InitializeDirectoriesTask.NAME,
            InitializeDirectoriesTask::class.java,
            extension
        )

        tasks.register(
            GenerateTypedAssetsTask.NAME,
            GenerateTypedAssetsTask::class.java,
            extension
        )*/

        afterEvaluate {
            if (extension.vendorConfiguration.vendors.isNotEmpty()) {
                registerVendorDownloadTask(target, extension)
            }
            //tasks.getByName(DownloadVendorDependenciesTask.NAME).dependsOn(tasks.getByName(InitializeDirectoriesTask.NAME))

            /*tasks.getByName(GenerateTypedAssetsTask.NAME) {
                //dependsOn(tasks.getByName(DownloadVendorDependenciesTask.NAME))

                doLast {
                    (this as GenerateTypedAssetsTask).registerSourceSet()
                }
            }*/

            // tasks.getByName("processResources").dependsOn(tasks.getByName(DownloadVendorDependenciesTask.NAME))

            /*tasks.getByName("compileKotlin") {
                dependsOn(tasks.getByName(GenerateTypedAssetsTask.NAME))
            }*/
        }
    }

    private fun registerVendorDownloadTask(target: Project, extension: WiredExtension) {
        target.tasks.register(
            DownloadVendorsTask.NAME,
            DownloadVendorsTask::class.java,
            extension
        )
    }
}