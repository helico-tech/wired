package nl.helicotech.wired.plugin.data

abstract class Vendors {
    data class Vendor (
        val packageName: String,
        val version: String
    )

    val vendors = mutableListOf<Vendor>()

    fun vendor(packageName: String, version: String) {
        vendors.add(Vendor(packageName, version))
    }
}