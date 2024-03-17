import nl.helicotech.wired.assetmapper.*
import java.io.File

typealias IgnoreRule = (File) -> Boolean

class AssetCollectionResolver(
    val ignoreRules: List<IgnoreRule> = DEFAULT_IGNORE_RULES,
    val hasher: FileHasher = SHA1FileHasher()
) {
    fun resolve(root: File): AssetCollection {
        require(root.exists() && root.isDirectory) { "Root does not exist or is not a directory" }

        val directory = resolveDirectory(root)
        return AssetCollection(listOf(root), directory.directories, directory.files)
    }

    private fun resolveDirectory(folder: File): AssetDirectory {
        require(folder.exists() && folder.isDirectory) { "Folder does not exist or is not a directory" }

        val folders = mutableListOf<AssetDirectory>()
        val files = mutableListOf<AssetFile>()

        folder.listFiles()?.filter { !ignoreRules.any { rule -> rule(it) }  }?.forEach {
            if (it.isDirectory) {
                folders.add(resolveDirectory(it))
            } else {
                files.add(AssetFile(it, hash = hasher.hash(it)))
            }
        }

        return AssetDirectory(folder, folders, files)
    }

    companion object {
        val DEFAULT_IGNORE_RULES: List<IgnoreRule> = listOf(
            { it.name.startsWith(".") },
        )
    }
}