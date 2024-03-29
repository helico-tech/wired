package nl.helicotech.wired.assetmapper

import java.io.InputStream


interface DependencyResolver : AssetHandler {
    fun resolve(source: InputStream): Set<String>
}

fun DependencyResolver.resolve(asset: Asset): Set<String> {
    require(accepts(asset)) { "Asset is not accepted by this resolver" }
    return resolve(asset.sourceFile.inputStream())
}

fun DependencyResolver.resolve(source: String): Set<String> {
    return resolve(source.byteInputStream())
}

fun DependencyResolver.resolve(lines: Collection<String>): Set<String> {
    return resolve(lines.joinToString("\n"))
}