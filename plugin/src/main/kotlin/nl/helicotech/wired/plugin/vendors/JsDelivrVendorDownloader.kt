package nl.helicotech.wired.plugin.vendors

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.gson.*

class JsDelivrVendorDownloader(
    private val client: HttpClient,
    private val cdnBaseUrl: String = "https://cdn.jsdelivr.net/npm",
) : VendorDownloader {

    companion object {
        operator fun invoke(): VendorDownloader {
            val client = HttpClient(CIO) {
                install(ContentNegotiation) {
                    gson()
                }
            }
            return JsDelivrVendorDownloader(client)
        }
    }

    override suspend fun download(vendor: Vendor): VendorDownloader.DownloadResult {
        val url = "$cdnBaseUrl/${vendor.packageName}@${vendor.version}/+esm"
        val response = client.get(url)

        if (response.status != HttpStatusCode.OK) {
            throw Exception("Failed to download ESM for ${vendor.packageName}@${vendor.version}")
        }

        return VendorDownloader.DownloadResult(
            contents = response.body(),
            contentType = response.contentType() ?: ContentType.Application.Any
        )
    }
}