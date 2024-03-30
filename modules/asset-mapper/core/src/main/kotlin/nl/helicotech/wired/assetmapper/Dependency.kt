package nl.helicotech.wired.assetmapper

data class Dependency(
    val from: Asset,
    val to: Asset,
    val logicalName: String
)