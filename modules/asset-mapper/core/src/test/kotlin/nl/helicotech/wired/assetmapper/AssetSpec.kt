package nl.helicotech.wired.assetmapper

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.io.File

class AssetSpec : DescribeSpec({
    describe("Asset") {
        it("should have a mapped path") {
            val asset = Asset(
                sourceFile = File("src/test/resources/test-assets/text-asset.txt"),
                targetFile = File("/assets/text-asset.txt"),
                digest = "123456"
            )
            asset.mappedPath shouldBe "text-asset-123456.txt"
        }
    }
})