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
    val vendorsDir = resourceDir.resolve("vendors")

    beforeEach {
        assetManager = AssetManager()
    }

    describe("AssetManager") {

        describe("addAsset") {
            it("should fail when file does not exist") {
                val file = File("non-existing-file")
                val exception = shouldThrow<IllegalArgumentException> {
                    assetManager.addAsset(file, "/assets", null)
                }
                exception.message shouldBe "Asset file does not exist: $file"
            }

            it("should fail when file is not a file") {
                val exception = shouldThrow<IllegalArgumentException> {
                    assetManager.addAsset(resourceDir, "/assets", null)
                }
                exception.message shouldBe "Asset file is not a file: $resourceDir"
            }

            it("should fail when mount path does not start with a /") {
                val exception = shouldThrow<IllegalArgumentException> {
                    assetManager.addAsset(testAssetsDir.resolve("text-asset.txt"), "assets", null)
                }
                exception.message shouldBe "Mount path must start with a /: assets"
            }

            it("should fail when asset file is already added") {
                val assetFile = testAssetsDir.resolve("text-asset.txt")
                assetManager.addAsset(assetFile, "/assets", null)
                val exception = shouldThrow<IllegalArgumentException> {
                    assetManager.addAsset(assetFile, "/assets", null)
                }
                exception.message shouldBe "Asset file is already added: $assetFile"
            }

            it("should fail when target file is already in use") {
                val assetFile = testAssetsDir.resolve("text-asset.txt")
                assetManager.addAsset(assetFile, "/assets", null)
                val exception = shouldThrow<IllegalArgumentException> {
                    assetManager.addAsset(testAssets2Dir.resolve("text-asset.txt"), "/assets", null)
                }
                exception.message shouldBe "Target file is already in use: /assets/text-asset.txt"
            }

            it("should add asset") {
                val assetFile = testAssetsDir.resolve("text-asset.txt")
                assetManager.addAsset(assetFile, "/assets", null)
                assetManager.assets.size shouldBe 1
                assetManager.assets.first().sourceFile shouldBe assetFile
                assetManager.assets.first().targetFile shouldBe File("/assets/text-asset.txt")
                assetManager.assets.first().digest shouldBe "da39a3ee5e6b4b0d3255bfef95601890afd80709"
            }

            it("should add multiple assets with different target files") {
                val assetFile1 = testAssetsDir.resolve("text-asset.txt")
                val assetFile2 = testAssets2Dir.resolve("text-asset.txt")
                assetManager.addAsset(assetFile1, "/assets", null)
                assetManager.addAsset(assetFile2, "/assets-2", null)
                assetManager.assets.size shouldBe 2
                assetManager.assets.map { it.sourceFile } shouldBe listOf(assetFile1, assetFile2)
                assetManager.assets.map { it.targetFile } shouldBe listOf(File("/assets/text-asset.txt"), File("/assets-2/text-asset.txt"))
            }
        }

        describe("resolve") {
            it("should return null when asset is not found") {
                assetManager.resolve("/assets/non-existing-asset.txt") shouldBe null
            }

            it("should return asset") {
                val assetFile = testAssetsDir.resolve("text-asset.txt")
                assetManager.addAsset(assetFile, "/assets", null)
                assetManager.resolve("/assets/text-asset.txt") shouldBe assetManager.assets.first()
            }

            it("should resolve modules") {
                val lib1 = vendorsDir.resolve("lib.js")
                val lib2 = vendorsDir.resolve("@namespace/lib.js")

                assetManager.addAsset(lib1, "/vendors", "lib")
                assetManager.addAsset(lib2, "/vendors/@namespace/lib", "@namespace/lib")

                assetManager.resolve("lib") shouldBe assetManager.assets.first()
                assetManager.resolve("@namespace/lib") shouldBe assetManager.assets.last()
            }
        }

        describe("resolveRelative") {
            it("should fail when asset is not managed") {
                val assetFile = testAssetsDir.resolve("text-asset.txt")
                assetManager.addAsset(assetFile, "/foo", null)
                val exception = shouldThrow<IllegalArgumentException> {
                    assetManager.resolveRelative("/assets/text-asset.txt", "./non-existing-asset.txt")
                }
                exception.message shouldBe "Asset is not managed: /assets/text-asset.txt"
            }

            it("should return null when asset is not found") {
                val assetFile = testAssetsDir.resolve("text-asset.txt")
                assetManager.addAsset(assetFile, "/assets", null)
                assetManager.resolveRelative("/assets/text-asset.txt", "./non-existing-asset.txt") shouldBe null
            }

            it("should return asset in other top level directory") {
                val assetFile1 = testAssetsDir.resolve("text-asset.txt")
                val assetFile2 = testAssets2Dir.resolve("text-asset.txt")
                assetManager.addAsset(assetFile1, "/assets", null)
                assetManager.addAsset(assetFile2, "/assets-2", null)
                assetManager.resolveRelative("/assets/text-asset.txt", "../assets-2/text-asset.txt") shouldBe assetManager.assets.last()
            }

            it("should return asset in other sub directory") {
                val assetFile1 = testAssetsDir.resolve("text-asset.txt")
                val assetFile2 = testAssets2Dir.resolve("text-asset.txt")
                assetManager.addAsset(assetFile1, "/assets", null)
                assetManager.addAsset(assetFile2, "/assets/sub-dir", null)
                assetManager.resolveRelative("/assets/text-asset.txt", "./sub-dir/text-asset.txt") shouldBe assetManager.assets.last()
            }

            it("should return asset in same directory") {
                val assetFile1 = testAssetsDir.resolve("text-asset.txt")
                val assetFile2 = testAssetsDir.resolve("text-asset-2.txt")
                assetManager.addAsset(assetFile1, "/assets", null)
                assetManager.addAsset(assetFile2, "/assets", null)
                assetManager.resolveRelative("/assets/text-asset.txt", "./text-asset-2.txt") shouldBe assetManager.assets.last()
            }
        }
    }

})