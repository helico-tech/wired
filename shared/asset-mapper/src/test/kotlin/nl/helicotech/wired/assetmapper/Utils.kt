package nl.helicotech.wired.assetmapper

import java.io.File

inline fun <reified T> T.loadResource(name: String): File {
    return File(T::class.java.classLoader.getResource(name)!!.file)
}