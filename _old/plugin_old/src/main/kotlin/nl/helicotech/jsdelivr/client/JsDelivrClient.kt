package nl.helicotech.jsdelivr.client

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.gson.*

interface JsDelivrClient {

    companion object {
        operator fun invoke(): JsDelivrClient = JsDelivrKtorClient()
    }

    data class DownloadResult(
        val contents: String,
        val contentType: ContentType,
    )

    suspend fun downloadEsm(packageName: String, version: String): DownloadResult
}

class JsDelivrKtorClient(
    private val client: HttpClient,
    private val apiBaseUrl: String = "https://data.jsdelivr.com/v1",
    private val cdnBaseUrl: String = "https://cdn.jsdelivr.net/npm",
) : JsDelivrClient {

    companion object {
        operator fun invoke(): JsDelivrClient {
            val client = HttpClient(CIO) {
                install(ContentNegotiation) {
                    gson()
                }
            }
            return JsDelivrKtorClient(client)
        }
    }

    override suspend fun downloadEsm(packageName: String, version: String): JsDelivrClient.DownloadResult {
        val url = "$cdnBaseUrl/$packageName@$version/+esm"
        val response = client.get(url)

        if (response.status != HttpStatusCode.OK) {
            throw Exception("Failed to download ESM for $packageName@$version")
        }

        return JsDelivrClient.DownloadResult(
            contents = response.body(),
            contentType = response.contentType() ?: ContentType.Application.Any
        )
    }
}