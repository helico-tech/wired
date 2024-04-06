package nl.helicotech.wired.assetmapper

import java.nio.file.Path

interface AssetResolver {
    fun resolve(path: Path): Asset?

    fun resolveRelative(from: Asset, path: Path): Asset?

    fun resolveRelative(from: Path, path: Path): Asset?
}