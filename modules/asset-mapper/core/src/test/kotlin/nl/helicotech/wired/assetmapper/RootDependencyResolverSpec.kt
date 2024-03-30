package nl.helicotech.wired.assetmapper

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import nl.helicotech.wired.assetmapper.js.JavascriptDependencyResolver
import java.io.File

class RootDependencyResolverSpec : DescribeSpec({
    describe("RootDependencyResolver") {

        val assetManager = AssetManager()

        val resourcesDir = File("src/test/resources/dependency-resolver")

        assetManager.addAsset(resourcesDir.resolve("transient-1.js"), "/assets", null)
        assetManager.addAsset(resourcesDir.resolve("transient-2.js"), "/assets", null)
        assetManager.addAsset(resourcesDir.resolve("transient-3.js"), "/assets", null)

        val resolver = RootDependencyResolver(
            assetManager,
            JavascriptDependencyResolver
        )

        it("should resolve transient dependencies") {
            val asset = requireNotNull(assetManager.resolve("/assets/transient-1.js"))

            val dependencies = resolver.resolve(asset).toList()

            dependencies.size shouldBe 2
            dependencies[0].to.targetFile.path shouldBe "/assets/transient-2.js"
            dependencies[0].from.targetFile.path

            dependencies[1].to.targetFile.path shouldBe "/assets/transient-3.js"
            dependencies[1].from.targetFile.path shouldBe "/assets/transient-2.js"
        }
    }
})