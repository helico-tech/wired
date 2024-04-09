package nl.helicotech.wired.assetmapper

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import nl.helicotech.wired.assetmapper.js.JavascriptDependencyResolver
import java.io.File
import java.nio.file.Path

class RootDependencyResolverSpec : DescribeSpec({
    describe("RootDependencyResolver") {

        val container = AssetContainer.createMutable("src/test/resources/dependency-resolver")

        container.addJavaScriptAsset(Path.of("transient-1.js"), "123", null)
        container.addJavaScriptAsset(Path.of("transient-2.js"), "123", null)
        container.addJavaScriptAsset(Path.of("transient-3.js"), "123", null)

        container.addJavaScriptAsset(Path.of("circular-1.js"), "123", null)
        container.addJavaScriptAsset(Path.of("circular-2.js"), "123", null)
        container.addJavaScriptAsset(Path.of("circular-3.js"), "123", null)

        val resolver = RootDependencyResolver(
            container,
            JavascriptDependencyResolver
        )

        it("should resolve transient dependencies") {
            val asset = requireNotNull(container.resolve(Path.of("./transient-1.js")))

            val dependencies = resolver.resolve(asset).toList()

            dependencies.size shouldBe 2
            dependencies[0].to.logicalPath shouldBe Path.of("transient-2.js")
            dependencies[0].from.logicalPath
        }

        it("should detect circular1dependencies") {
            val asset = requireNotNull(container.resolve(Path.of("./circular-1.js")))

            val exception = shouldThrow<IllegalArgumentException> {
                resolver.resolve(asset)
            }

            exception.message shouldStartWith  "Circular dependency detected"
        }
    }
})