package nl.helicotech.wired.assetmapper

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.resource.resourceAsString
import io.kotest.matchers.shouldBe

class CodeGeneratorSpec : DescribeSpec({

    describe("CodeGenerator") {

        val container = mutableAssetContainer("src/test/resources/assets")

        it("should generate the root container") {
            val codeGenerator = CodeGenerator(
                assetContainer = container,
                packageName = "nl.helicotech.wired.assetmapper",
                fileName = "Assets"
            )

            val fileSpec = codeGenerator.generate()

            val result = fileSpec.toString()
            val expected = resourceAsString("/codegen/root-container.kt")
            result shouldBe expected
        }
    }
})