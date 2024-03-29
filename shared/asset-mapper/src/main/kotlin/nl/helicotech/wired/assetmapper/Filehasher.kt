package nl.helicotech.wired.assetmapper

import java.io.File

interface Digester {
    fun digest(file: File): String
}

class SHA1FileHasher: Digester {
    override fun digest(file: File): String {
        return file.inputStream().use {
            java.security.MessageDigest.getInstance("SHA-1").digest(it.readBytes()).joinToString("") {
                "%02x".format(it)
            }
        }
    }
}