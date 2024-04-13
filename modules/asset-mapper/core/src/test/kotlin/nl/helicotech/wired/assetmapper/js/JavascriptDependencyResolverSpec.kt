package nl.helicotech.wired.assetmapper.js

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import nl.helicotech.wired.assetmapper.AssetContainer
import nl.helicotech.wired.assetmapper.addJavaScriptAsset
import nl.helicotech.wired.assetmapper.mutableAssetContainer
import kotlin.io.path.Path

class JavascriptDependencyResolverSpec : DescribeSpec({
    describe("JavascriptDependencyResolver") {

        val container = mutableAssetContainer("src/test/resources/dependency-resolver")

        container.addJavaScriptAsset(Path("app.js"), "123", null)
        container.addJavaScriptAsset(Path("duck.js"), "123", null)

        container.addJavaScriptAsset(Path("subdirectory/cow.js"), "123", null)
        container.addJavaScriptAsset(Path("vendors/@namespace/company/lib.js"), "123", "@namespace/company")

        it("should resolve dependencies") {
            val asset = requireNotNull(container.resolve(Path("./app.js")))

            val dependencies = JavascriptDependencyResolver.create(container).resolve(asset).toList()

            dependencies.size shouldBe 3

            dependencies[0].to.logicalPath.toString() shouldBe "duck.js"
            dependencies[0].from.logicalPath.toString() shouldBe "app.js"

            dependencies[1].to.logicalPath.toString() shouldBe "subdirectory/cow.js"
            dependencies[1].from.logicalPath.toString() shouldBe "app.js"

            dependencies[2].to.logicalPath.toString() shouldBe "vendors/@namespace/company/lib.js"
            dependencies[2].from.logicalPath.toString() shouldBe "app.js"
        }
    }
})