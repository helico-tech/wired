package nl.helicotech.wired.assetmapper

import java.io.File

typealias IgnoreRule = (File) -> Boolean

class AssetResolver(
    val ignoreRules: List<IgnoreRule> = DEFAULT_IGNORE_RULES,
    val hasher: FileHasher = SHA1FileHasher()
) {
    fun resolve(root: File): Asset.Directory {
        require(root.exists() && root.isDirectory) { "Root does not exist or is not a directory" }

        val directory = resolveDirectory(root)
        return assetDirectory(root, directory.directories, directory.files)
    }

    private fun resolveDirectory(folder: File): Asset.Directory {
        require(folder.exists() && folder.isDirectory) { "Folder does not exist or is not a directory" }

        val folders = mutableListOf<Asset.Directory>()
        val files = mutableListOf<Asset.File>()

        folder.listFiles()?.filter { !ignoreRules.any { rule -> rule(it) }  }?.forEach {
            if (it.isDirectory) {
                folders.add(resolveDirectory(it))
            } else {
                files.add(assetFile(it, hash = hasher.hash(it)))
            }
        }

        return assetDirectory(folder, folders, files)
    }

    companion object {
        val DEFAULT_IGNORE_RULES: List<IgnoreRule> = listOf(
            { it.name.startsWith(".") },
        )
    }
}