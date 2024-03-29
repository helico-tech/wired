package nl.helicotech.wired.assetmapper

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.resource.resourceAsString
import io.kotest.matchers.shouldBe
import java.io.File

class AssetManagerSpec : DescribeSpec({

    var assetManager = AssetManager()

    val resourceDir = File("src/test/resources")
    val testAssetsDir = resourceDir.resolve("test-assets")
    val testAssets2Dir = resourceDir.resolve("test-assets-2")

    beforeEach {
        assetManager = AssetManager()
    }

    describe("AssetManager") {
        describe("addAsset") {
            it("should fail when file does not exist") {
                val file = File("non-existing-file")
                val exception = shouldThrow<IllegalArgumentException> {
                    assetManager.addAsset(file)
                }
                exception.message shouldBe "Asset file does not exist: $file"
            }

            it("should fail when file is not a file") {
                val exception = shouldThrow<IllegalArgumentException> {
                    assetManager.addAsset(resourceDir)
                }
                exception.message shouldBe "Asset file is not a file: $resourceDir"
            }

            it("should fail when mount path does not start with a /") {
                val exception = shouldThrow<IllegalArgumentException> {
                    assetManager.addAsset(testAssetsDir.resolve("text-asset.txt"), "assets")
                }
                exception.message shouldBe "Mount path must start with a /: assets"
            }

            it("should fail when asset file is already added") {
                val assetFile = testAssetsDir.resolve("text-asset.txt")
                assetManager.addAsset(assetFile)
                val exception = shouldThrow<IllegalArgumentException> {
                    assetManager.addAsset(assetFile)
                }
                exception.message shouldBe "Asset file is already added: $assetFile"
            }

            it("should fail when target file is already in use") {
                val assetFile = testAssetsDir.resolve("text-asset.txt")
                assetManager.addAsset(assetFile)
                val exception = shouldThrow<IllegalArgumentException> {
                    assetManager.addAsset(testAssets2Dir.resolve("text-asset.txt"))
                }
                exception.message shouldBe "Target file is already in use: /assets/text-asset.txt"
            }

            it("should add asset") {
                val assetFile = testAssetsDir.resolve("text-asset.txt")
                assetManager.addAsset(assetFile)
                assetManager.assets.size shouldBe 1
                assetManager.assets.first().sourceFile shouldBe assetFile
                assetManager.assets.first().targetFile shouldBe File("/assets/text-asset.txt")
                assetManager.assets.first().digest shouldBe "da39a3ee5e6b4b0d3255bfef95601890afd80709"
            }

            it("should add multiple assets with different target files") {
                val assetFile1 = testAssetsDir.resolve("text-asset.txt")
                val assetFile2 = testAssets2Dir.resolve("text-asset.txt")
                assetManager.addAsset(assetFile1, "/assets")
                assetManager.addAsset(assetFile2, "/assets-2")
                assetManager.assets.size shouldBe 2
                assetManager.assets.map { it.sourceFile } shouldBe listOf(assetFile1, assetFile2)
                assetManager.assets.map { it.targetFile } shouldBe listOf(File("/assets/text-asset.txt"), File("/assets-2/text-asset.txt"))
            }
        }
    }

})