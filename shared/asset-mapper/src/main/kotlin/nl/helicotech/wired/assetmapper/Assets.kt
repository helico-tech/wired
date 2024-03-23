package nl.helicotech.wired.assetmapper

import io.ktor.http.*
import java.io.File

sealed interface Asset {

    val file: java.io.File

    interface Directory : Asset {
        val directories: List<Directory>
        val files: List<File>
    }

    interface File : Asset {
        val hash: String
        val contentType: ContentType
    }
}

fun assetDirectory(
    directory: File,
    directories: List<Asset.Directory> = emptyList(),
    files: List<Asset.File> = emptyList()
) : Asset.Directory {
    require(directory.exists() && directory.isDirectory) { "Root does not exist or is not a directory" }

    return object : Asset.Directory {
        override val file: File = directory
        override val directories = directories
        override val files = files
    }
}

fun assetFile(
    file: File,
    hash: String
) : Asset.File {
    require(file.exists() && file.isFile) { "File does not exist or is not a file" }

    return object : Asset.File {
        override val hash = hash
        override val file = file
        override val contentType = ContentType.defaultForFile(file)
    }
}

fun Asset.traverse(): Sequence<Asset> = sequence {
    yield(this@traverse)
    if (this@traverse is Asset.Directory) {
        yieldAll(directories.asSequence().flatMap { it.traverse() })
        yieldAll(files.asSequence())
    }
}

fun Asset.fileName() = this.file.name

fun Asset.traverseFiles(): Sequence<Asset.File> = traverse().filterIsInstance<Asset.File>()

fun Iterable<Asset>.traverseFiles(): Sequence<Asset.File> = asSequence().flatMap { it.traverseFiles() }

fun Asset.traverseDirectories(): Sequence<Asset.Directory> = traverse().filterIsInstance<Asset.Directory>()

fun Asset.File.hashedName(): String = "${file.nameWithoutExtension}-${hash}.${file.extension}"
fun Asset.File.hashedFile(): File = file.parentFile.resolve(hashedName())

fun Asset.File.url(): String = hashedFile().path

fun Asset.File.importMapKey(): String = file.parentFile.resolve(file.nameWithoutExtension).path

fun Asset.ofType(contentType: ContentType): Sequence<Asset.File> = traverseFiles().filter { it.contentType.match(contentType) }

fun Iterable<Asset>.ofType(contentType: ContentType): Sequence<Asset.File> = traverseFiles().filter { it.contentType.match(contentType) }