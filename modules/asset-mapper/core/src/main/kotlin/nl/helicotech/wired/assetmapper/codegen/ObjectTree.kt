package nl.helicotech.wired.assetmapper.codegen

import com.squareup.kotlinpoet.NameAllocator
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.tag
import nl.helicotech.wired.assetmapper.AssetContainer
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.*

class ObjectTree(
    val assetContainer: AssetContainer
) {
    fun generate(): TypeSpec {
        val rootNameAllocator = NameAllocator()
        val rootObject = createObjectSpec(assetContainer.logicalPath, rootNameAllocator)
        return rootObject
    }

    private fun createObjectSpec(path: Path, nameAllocator: NameAllocator): TypeSpec {
        val name = nameAllocator.newName(path.name, path)
        val builder = TypeSpec.objectBuilder(name)
        builder.tag(nameAllocator)

        if (path.isDirectory()) {
            val childNameAllocator = nameAllocator.copy()
            Files.list(path).filter { it.isDirectory() }.forEach { subPath ->
                val spec = createObjectSpec(subPath, childNameAllocator)
                builder.addType(spec)
            }
        }

        return builder.build()
    }
}