package nl.helicotech.wired.assetmapper

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.ktor.http.*
import java.io.File
import kotlin.text.Charsets.UTF_8

class AssetSpec : DescribeSpec({
    describe("Asset") {
        it("should have a mapped file") {
            val asset = Asset(
                sourceFile = File("src/test/resources/test-assets/text-asset.txt"),
                targetFile = File("/assets/text-asset.txt"),
                digest = "123456",
                moduleName = null
            )
            asset.mappedFile shouldBe File("/assets/text-asset-123456.txt")
        }

        it("should have a mapped file with a module") {
            val asset = Asset(
                sourceFile = File("src/test/resources/test-assets/text-asset.txt"),
                targetFile = File("/assets/text-asset.txt"),
                digest = "123456",
                moduleName = "module"
            )
            asset.mappedFile shouldBe File("/assets/text-asset-123456.txt")
        }

        it("should have correct content types") {
            val extensions = listOf("css", "html", "js", "json", "txt", "xml")

            val contentTypes = listOf(
                ContentType.Text.CSS.withCharset(UTF_8),
                ContentType.Text.Html.withCharset(UTF_8),
                ContentType.Application.JavaScript,
                ContentType.Application.Json,
                ContentType.Text.Plain.withCharset(UTF_8),
                ContentType.Application.Xml
            )

            extensions.zip(contentTypes).forEach { (extension, contentType) ->
                val asset = Asset(
                    sourceFile = File("src/test/resources/test-assets/text-asset.$extension"),
                    targetFile = File("/assets/text-asset.$extension"),
                    digest = "123456",
                    moduleName = null
                )
                asset.contentType shouldBe contentType
            }
        }
    }
})