package nl.helicotech.wired.assetmapper

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.resource.resourceAsString
import io.kotest.matchers.shouldBe
import java.nio.file.Path
import kotlin.io.path.Path

class CodeGeneratorSpec : DescribeSpec({

    describe("CodeGenerator") {

        val createCodeGenerator : (container: AssetContainer) -> CodeGenerator = { assetContainer ->
            CodeGenerator(
                assetContainer = assetContainer,
                packageName = "nl.foo.bar",
                fileName = "Assets"
            )
        }

        it("should generate the root container") {
            val container = mutableAssetContainer("src/test/resources/assets")
            val codeGenerator = createCodeGenerator(container)
            val fileSpec = codeGenerator.generate()

            val result = fileSpec.toString()
            val expected = resourceAsString("/codegen/root-container.kt")
            result shouldBe expected
        }

        it("should show assets in the root container") {
            val container = mutableAssetContainer("src/test/resources/assets")
            container.addJavaScriptAsset(Path.of("main.js"), "123", null)
            container.addJavaScriptAsset(Path.of("module.js"), "456", "module")

            val codeGenerator = createCodeGenerator(container)
            val fileSpec = codeGenerator.generate()

            val result = fileSpec.toString()
            val expected = resourceAsString("/codegen/root-container-with-assets.kt")
            result shouldBe expected
        }

        it("should add the dependencies to the assets") {
            val container = mutableAssetContainer("src/test/resources/dependency-resolver")

            container.addJavaScriptAsset(Path("app.js"), "123", null)
            container.addJavaScriptAsset(Path("duck.js"), "123", null)

            container.addJavaScriptAsset(Path("subdirectory/cow.js"), "123", null)
            container.addJavaScriptAsset(Path("vendors/@namespace/company/lib.js"), "123", "@namespace/company")

            val codeGenerator = createCodeGenerator(container)
            val fileSpec = codeGenerator.generate()

            val result = fileSpec.toString()
            println(result)
        }
    }
})