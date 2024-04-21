package nl.helicotech.wired.assetmapper

import java.nio.file.Path
import kotlin.io.path.toPath

fun Path.fromResources(): Path {
    val resource = requireNotNull(CodeGenerator::class.java.getResource(this.toString())) { "Resource not found: $this" }
    return resource.toURI().toPath()
}