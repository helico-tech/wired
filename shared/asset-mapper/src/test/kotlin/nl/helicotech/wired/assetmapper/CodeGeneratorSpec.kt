package nl.helicotech.wired.assetmapper

import io.kotest.core.spec.style.DescribeSpec
import java.io.File

class CodeGeneratorSpec : DescribeSpec({
    describe("CodeGenerator") {

        val codeGenerator = CodeGenerator()

        it("should generate a file") {
            val directory = assetDirectory(directory = File("foo"))
            val spec = codeGenerator.generate("Assets", "my.package.assets", directory)
            val result = spec.toString()

            println(result)
        }
    }
})