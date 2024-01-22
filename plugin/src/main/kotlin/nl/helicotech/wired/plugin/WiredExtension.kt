package nl.helicotech.wired.plugin

import nl.helicotech.wired.plugin.data.Vendors
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.SourceSet.MAIN_SOURCE_SET_NAME
import org.gradle.api.tasks.SourceSetContainer
import java.io.File
import javax.inject.Inject

abstract class WiredExtension @Inject constructor(
    project: Project,
    objectFactory: ObjectFactory,
    sourceSetContainer: SourceSetContainer
) {
    companion object {
        val NAME = "wired"

        val DEFAULT_GENERATED_DIRECTORY = "generated/${NAME}"

        val DEFAULT_ASSETS_DIRECTORY = "assets"
        val DEFAULT_VENDOR_DIRECTORY = "vendor"
        val DEFAULT_JS_DIRECTORY = "js"
        val DEFAULT_CSS_DIRECTORY = "css"
    }

    abstract val assetsDirectory: Property<File>
    abstract val vendorDirectory: Property<File>
    abstract val jsDirectory: Property<File>
    abstract val cssDirectory: Property<File>
    abstract val generatedDirectory: Property<File>

    abstract val vendors: Property<Vendors>
    abstract val packageName: Property<String?>

    init {
        vendors.convention(objectFactory.newInstance(Vendors::class.java))

        val resourcesDirectory = sourceSetContainer.getByName(MAIN_SOURCE_SET_NAME).resources.srcDirs.first()

        assetsDirectory.convention(resourcesDirectory.resolve(DEFAULT_ASSETS_DIRECTORY))
        vendorDirectory.convention(assetsDirectory.get().resolve(DEFAULT_VENDOR_DIRECTORY))
        jsDirectory.convention(assetsDirectory.get().resolve(DEFAULT_JS_DIRECTORY))
        cssDirectory.convention(assetsDirectory.get().resolve(DEFAULT_CSS_DIRECTORY))

        generatedDirectory.convention(project.layout.buildDirectory.asFile.get().resolve(DEFAULT_GENERATED_DIRECTORY))
    }

    fun vendor(packageName: String, version: String) = vendors.get().vendor(packageName, version)
}