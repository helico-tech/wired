package nl.helicotech.wired.assetmapper.js

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import nl.helicotech.wired.assetmapper.AssetManager
import java.io.File

class JavascriptDependencyResolverSpec : DescribeSpec({
    describe("JavascriptDependencyResolver") {

        val assetManager = AssetManager()

        val resourcesDir = File("src/test/resources/dependency-resolver")

        assetManager.addAsset(resourcesDir.resolve("app.js"), "/assets", null)
        assetManager.addAsset(resourcesDir.resolve("duck.js"), "/assets", null)
        assetManager.addAsset(resourcesDir.resolve("./subdirectory/cow.js"), "/assets/subdirectory", null)

        val resolver = JavascriptDependencyResolver.create(assetManager)

        it("should resolve dependencies") {
            val asset = requireNotNull(assetManager.resolve("/assets/app.js"))

            val dependencies = resolver.resolve(asset)

            dependencies.size shouldBe 2
            dependencies.first().to.targetFile.path shouldBe "/assets/duck.js"
            dependencies.first().from.targetFile.path shouldBe "/assets/app.js"
            dependencies.first().logicalName shouldBe "./duck.js"

            dependencies.last().to.targetFile.path shouldBe "/assets/subdirectory/cow.js"
            dependencies.last().from.targetFile.path shouldBe "/assets/app.js"
            dependencies.last().logicalName shouldBe "./subdirectory/cow.js"
        }
    }
})