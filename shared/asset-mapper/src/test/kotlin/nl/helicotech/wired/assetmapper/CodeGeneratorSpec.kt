package nl.helicotech.wired.assetmapper

import io.kotest.core.spec.style.DescribeSpec

class CodeGeneratorSpec : DescribeSpec({
    describe("CodeGenerator") {

        val directory = AssetResolver().resolve(loadResource("asset-mapper/asset-folder-1"))
        val codeGenerator = CodeGenerator(directory)

        it("should generate a file") {
            val spec = codeGenerator.generate("Assets", "my.package.assets")
            val result = spec.toString()

            println(result)
        }
    }
})