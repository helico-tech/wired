package nl.helicotech.wired.vendors

import io.ktor.http.*

interface VendorDownloader {
    data class DownloadResult(
        val contents: String,
        val contentType: ContentType,
    )

    suspend fun download(vendor: Vendor): DownloadResult
}