package nl.helicotech.wired

import java.io.File

sealed interface Asset {
    val path: File
}

data class AssetFile(
    val name: String,
    override val path: File,
    val hash: String,
) : Asset {
    override fun toString(): String = name

    val pathWithHash get() = "${path.parentFile.path}/${path.nameWithoutExtension}-$hash.${path.extension}"
}

interface AssetFolder : Asset {
    val assets: List<Asset>
}

fun Asset.traverse(): Sequence<AssetFile> = sequence {
    when (this@traverse) {
        is AssetFile -> yield(this@traverse)
        is AssetFolder -> {
            assets.forEach { yieldAll(it.traverse()) }
        }
    }
}

fun Asset.all(): List<AssetFile> = traverse().toList()
fun Iterable<Asset>.all(): List<AssetFile> = flatMap { it.all() }.distinct()