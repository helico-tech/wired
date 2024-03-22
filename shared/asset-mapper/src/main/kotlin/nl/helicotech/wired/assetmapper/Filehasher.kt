package nl.helicotech.wired.assetmapper

import java.io.File

interface FileHasher {
    fun hash(file: File): String
}

class SHA1FileHasher: FileHasher {
    override fun hash(file: File): String {
        return file.inputStream().use {
            java.security.MessageDigest.getInstance("SHA-1").digest(it.readBytes()).joinToString("") {
                "%02x".format(it)
            }
        }
    }
}