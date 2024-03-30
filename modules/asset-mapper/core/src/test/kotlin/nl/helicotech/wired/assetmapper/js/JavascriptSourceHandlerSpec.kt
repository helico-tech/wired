package nl.helicotech.wired.assetmapper.js

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class JavascriptSourceHandlerSpec : DescribeSpec({

    describe("JavascriptSourceHandler") {

        describe("getSourceDependencies") {
            it("should resolve basic dependencies") {
                val source = listOf(
                    "import Duck from './duck.js';",
                    "import Cow from './subdirectory/cow.js';",
                    "import { libraryFunction } from '@namespace/company'",
                )

                val sourceHandler = JavascriptSourceHandler(source)

                val dependencies = sourceHandler.getSourceDependencies()

                dependencies shouldBe setOf("./duck.js", "./subdirectory/cow.js", "@namespace/company")
            }

            it("should resolve dependencies with double quotes") {
                val source = listOf(
                    "import \"dependency1.js\"",
                    "import \"dependency2.js\""
                )

                val sourceHandler = JavascriptSourceHandler(source)

                val dependencies = sourceHandler.getSourceDependencies()

                dependencies shouldBe setOf("dependency1.js", "dependency2.js")
            }

            it("should resolve dependencies with mixed quotes") {
                val source = listOf(
                    "import 'dependency1.js'",
                    "import \"dependency2.js\""
                )

                val sourceHandler = JavascriptSourceHandler(source)

                val dependencies = sourceHandler.getSourceDependencies()

                dependencies shouldBe setOf("dependency1.js", "dependency2.js")
            }

            it("should resolve dependencies on the same line") {
                val source = listOf("import 'dependency1.js'; import 'dependency2.js';")

                val sourceHandler = JavascriptSourceHandler(source)

                val dependencies = sourceHandler.getSourceDependencies()

                dependencies shouldBe setOf("dependency1.js", "dependency2.js")
            }

            it("should resolve relative dependencies") {
                val source = listOf(
                    "import './dependency1.js'",
                    "import '../dependency2.js'"
                )

                val sourceHandler = JavascriptSourceHandler(source)

                val dependencies = sourceHandler.getSourceDependencies()

                dependencies shouldBe setOf("./dependency1.js", "../dependency2.js")
            }

            it("should resolve namespaced dependencies") {
                val source = listOf(
                    "import 'namespace/dependency1.js'",
                    "import '@namespace/dependency2.js'"
                )

                val sourceHandler = JavascriptSourceHandler(source)

                val dependencies = sourceHandler.getSourceDependencies()

                dependencies shouldBe setOf("namespace/dependency1.js", "@namespace/dependency2.js")
            }

            it("should not resolve dependencies if there are none") {
                val source = listOf("console.log('import \"Hello, world!\"')")

                val sourceHandler = JavascriptSourceHandler(source)

                val dependencies = sourceHandler.getSourceDependencies()

                dependencies shouldBe emptySet()
            }

            it("should detect re-exports") {
                val source = listOf(
                    "export * from 'dependency1.js'",
                    "export { default } from 'dependency2.js'"
                )

                val sourceHandler = JavascriptSourceHandler(source)

                val dependencies = sourceHandler.getSourceDependencies()

                dependencies shouldBe setOf("dependency1.js", "dependency2.js")
            }
        }
    }
})