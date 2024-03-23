package nl.helicotech.wired.plugin.vendors

import io.ktor.http.*

interface VendorDownloader {
    data class DownloadResult(
        val contents: String,
        val contentType: ContentType,
    ) {
        fun fileExtensions() = contentType.fileExtensions()
    }

    suspend fun download(packageName: String, version: String): DownloadResult
}