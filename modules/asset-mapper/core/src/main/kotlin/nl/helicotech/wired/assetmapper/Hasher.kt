package nl.helicotech.wired.assetmapper

import java.io.InputStream

fun interface Digester {
    fun digest(inputStream: InputStream): String

    companion object {
        val SHA1 = Digester { inputStream ->
            java.security.MessageDigest.getInstance("SHA-1").digest(inputStream.readBytes()).asString()
        }

        private fun ByteArray.asString() = joinToString("") { "%02x".format(it) }
    }
}
