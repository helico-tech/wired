package nl.helicotech.wired.plugin

import nl.helicotech.wired.plugin.assetmapper.GenerateTypedAssetsTask
import nl.helicotech.wired.plugin.vendors.DownloadVendorsTask
import org.apache.tools.ant.types.spi.Provider
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider

abstract class WiredPlugin: Plugin<Project> {

    companion object {
        val GROUP = "wired"
    }

    override fun apply(target: Project): Unit = with(target) {

        val extension = extensions.create(
            WiredExtension.NAME,
            WiredExtension::class.java
        )

        val vendorDownloadTask = registerVendorDownloadTask(target, extension)
        val generateTypedAssetsTask = registerGenerateTypedAssetsTask(target, extension)

        afterEvaluate {

            tasks.getByName("compileKotlin").dependsOn(generateTypedAssetsTask)

            generateTypedAssetsTask.get().dependsOn(vendorDownloadTask)
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

    private fun registerVendorDownloadTask(target: Project, extension: WiredExtension): TaskProvider<DownloadVendorsTask> {
        return target.tasks.register(
            DownloadVendorsTask.NAME,
            DownloadVendorsTask::class.java,
            extension
        )
    }

    private fun registerGenerateTypedAssetsTask(target: Project, extension: WiredExtension): TaskProvider<GenerateTypedAssetsTask> {
        return target.tasks.register(
            GenerateTypedAssetsTask.NAME,
            GenerateTypedAssetsTask::class.java,
            extension
        )
    }
}