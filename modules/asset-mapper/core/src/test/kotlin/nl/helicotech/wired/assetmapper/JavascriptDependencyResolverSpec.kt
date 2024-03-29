package nl.helicotech.wired.assetmapper

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.ktor.http.*
import java.io.File

class JavascriptDependencyResolverSpec : DescribeSpec({

    describe("JavascriptDependencyResolver") {

        val resolver = JavascriptDependencyResolver()

        describe("accepts") {
            it("should only accept javascript files") {
                val asset = Asset(
                    sourceFile = File("src/test/resources/test-assets/text-asset.js"),
                    targetFile = File("/assets/text-asset.js"),
                    digest = "123456",
                    moduleName = null
                )
                resolver.accepts(asset) shouldBe true
            }

            it("should not accept non-javascript files") {
                val nonJavascriptContentTypes = listOf(
                    ContentType.Text.CSS.withCharset(Charsets.UTF_8),
                    ContentType.Text.Html.withCharset(Charsets.UTF_8),
                    ContentType.Application.Json,
                    ContentType.Text.Plain.withCharset(Charsets.UTF_8),
                    ContentType.Application.Xml
                )

                nonJavascriptContentTypes.forEach { contentType ->
                    val asset = Asset(
                        sourceFile = File("src/test/resources/test-assets/text-asset.txt"),
                        targetFile = File("/assets/text-asset.txt"),
                        digest = "123456",
                        moduleName = null
                    )
                    resolver.accepts(asset) shouldBe false
                }
            }
        }

        describe("resolve") {
            it("should resolve basic dependencies") {
                val source = listOf(
                    "import 'dependency1.js'",
                    "import 'dependency2.js'"
                )

                val dependencies = resolver.resolve(source)

                dependencies shouldBe setOf("dependency1.js", "dependency2.js")
            }

            it("should resolve dependencies with double quotes") {
                val source = listOf(
                    "import \"dependency1.js\"",
                    "import \"dependency2.js\""
                )

                val dependencies = resolver.resolve(source)

                dependencies shouldBe setOf("dependency1.js", "dependency2.js")
            }

            it("should resolve dependencies with mixed quotes") {
                val source = listOf(
                    "import 'dependency1.js'",
                    "import \"dependency2.js\""
                )

                val dependencies = resolver.resolve(source)

                dependencies shouldBe setOf("dependency1.js", "dependency2.js")
            }

            it("should resolve dependencies on the same line") {
                val source = listOf("import 'dependency1.js'; import 'dependency2.js';")

                val dependencies = resolver.resolve(source)

                dependencies shouldBe setOf("dependency1.js", "dependency2.js")
            }

            it("should resolve relative dependencies") {
                val source = listOf(
                    "import './dependency1.js'",
                    "import '../dependency2.js'"
                )

                val dependencies = resolver.resolve(source)

                dependencies shouldBe setOf("./dependency1.js", "../dependency2.js")
            }

            it("should resolve namespaced dependencies") {
                val source = listOf(
                    "import 'namespace/dependency1.js'",
                    "import '@namespace/dependency2.js'"
                )

                val dependencies = resolver.resolve(source)

                dependencies shouldBe setOf("namespace/dependency1.js", "@namespace/dependency2.js")
            }

            it("should not resolve dependencies if there are none") {
                val source = listOf("console.log('import \"Hello, world!\"')")

                val dependencies = resolver.resolve(source)

                dependencies shouldBe emptySet()
            }
        }
    }
})