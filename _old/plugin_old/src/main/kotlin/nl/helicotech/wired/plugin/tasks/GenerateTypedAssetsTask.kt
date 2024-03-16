package nl.helicotech.wired.plugin.tasks

import nl.helicotech.wired.plugin.WiredExtension
import nl.helicotech.wired.plugin.WiredPlugin
import nl.helicotech.wired.plugin.codegen.TypedAssetsCodeGenerator
import org.gradle.api.DefaultTask
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.tasks.SourceSet.MAIN_SOURCE_SET_NAME
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.get
import javax.inject.Inject

abstract class GenerateTypedAssetsTask @Inject() constructor(
    private val extension: WiredExtension,
    private val sourceSetContainer: SourceSetContainer,
) : DefaultTask() {

    companion object {
        const val NAME = "generateTypedAssets"
        const val DESCRIPTION = "Generate typed assets"
    }

    init {
        group = WiredPlugin.GROUP
        description = DESCRIPTION
    }

    @TaskAction
    fun run() {
        /*val fileName = "Assets.kt"

        val assets = listOf(extension.vendorDirectory.get()) + extension.assetsDirectory.get().listFiles().orEmpty()

        val generator = TypedAssetsCodeGenerator(
            packageName = requireNotNull(extension.packageName.orNull) {  "Package name must be set" },
            fileName = fileName,
            assetFolders = assets.filter { !it.name.startsWith(".") }.toTypedArray()
        )

        val file = extension.generatedDirectory.get().resolve(fileName)
        if (file.exists()) {
            file.delete()
        } else {
            file.parentFile.mkdirs()
            file.createNewFile()
        }
        file.writeText(generator.generate().toString())*/
    }

    fun registerSourceSet() {
        val main = sourceSetContainer[MAIN_SOURCE_SET_NAME]
        (main.extensions["kotlin"] as SourceDirectorySet).apply {
            srcDir(extension.buildDirectory.get())
        }
    }
}