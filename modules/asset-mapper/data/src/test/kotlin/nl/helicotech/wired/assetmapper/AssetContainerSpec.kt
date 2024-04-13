package nl.helicotech.wired.assetmapper

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.ktor.http.*
import java.nio.file.Path

class AssetContainerSpec : DescribeSpec({

    describe("MutableAssetContainer") {

        it("should be able to be created") {
            val container = mutableAssetContainer("root")
            container.logicalPath shouldBe Path.of("root")
        }

        describe("when created") {
            val container = mutableAssetContainer("root")

            it("should have an empty list of assets") {
                container.assets.size shouldBe 0
            }

            describe("when adding an asset") {
                container.addGenericAsset(Path.of("test.txt"), "digest", ContentType.Text.Plain)

                it("should have one asset") {
                    container.assets.size shouldBe 1
                }

                it("should have the correct asset") {
                    container.assets[0].logicalPath shouldBe Path.of("test.txt")
                }

                it("should be able to resolve the asset") {
                    container.resolve(Path.of("./test.txt")) shouldBe container.assets[0]
                }
            }

            describe("when adding a sub container") {

                beforeEach {
                    container.assets.clear()
                }

                it("should be able to resolve an asset in the container") {
                    container.addGenericAsset(Path.of("sub/test2.txt"), "digest", ContentType.Text.Plain)
                    container.resolve(Path.of("./sub/test2.txt")) shouldBe container.assets[0]
                }

                it("should be able to resolve relative assets") {
                    val asset1 = container.addGenericAsset(Path.of("test.txt"), "digest", ContentType.Text.Plain)
                    val asset2 = container.addGenericAsset(Path.of("sub/test2.txt"), "digest", ContentType.Text.Plain)

                    val result1 = container.resolveRelative(Path.of("./sub/test2.txt"), Path.of("../test.txt"))
                    result1 shouldBe asset1

                    val result2 = container.resolveRelative(Path.of("./test.txt"), Path.of("./sub/test2.txt"))
                    result2 shouldBe asset2
                }

                it("should be able to resolve Javascript modules") {
                    val asset1 = container.addJavaScriptAsset(Path.of("test.js"), "digest", null)
                    val asset2 = container.addJavaScriptAsset(Path.of("@hotwired/turbo.js"), "digest", "@hotwired/turbo")
                    val asset3 = container.addJavaScriptAsset(Path.of("sub/@bootstrap/core.js"), "digest", "@bootstrap/core")

                    val result = container.resolveRelative(asset1, Path.of("@hotwired/turbo"))
                    result shouldBe asset2

                    val result2 = container.resolveRelative(asset1, Path.of("@bootstrap/core"))
                    result2 shouldBe asset3
                }
            }
        }
    }

})