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
        assetManager.addAsset(resourcesDir.resolve("./vendors/@namespace/company/lib.js"), "/assets/vendors/@namespace/company", "@namespace/company")

        val resolver = JavascriptDependencyResolver.create(assetManager)

        it("should resolve dependencies") {
            val asset = requireNotNull(assetManager.resolve("/assets/app.js"))

            val dependencies = resolver.resolve(asset).toList()

            dependencies.size shouldBe 3
            dependencies[0].to.targetFile.path shouldBe "/assets/duck.js"
            dependencies[0].from.targetFile.path shouldBe "/assets/app.js"
            dependencies[0].logicalName shouldBe "./duck.js"

            dependencies[1].to.targetFile.path shouldBe "/assets/subdirectory/cow.js"
            dependencies[1].from.targetFile.path shouldBe "/assets/app.js"
            dependencies[1].logicalName shouldBe "./subdirectory/cow.js"

            dependencies[2].to.targetFile.path shouldBe "/assets/vendors/@namespace/company/lib.js"
            dependencies[2].from.targetFile.path shouldBe "/assets/app.js"
            dependencies[2].logicalName shouldBe "@namespace/company"
        }
    }
})