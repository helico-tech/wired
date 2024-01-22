package nl.helicotech.wired.plugin.codegen

import nl.helicotech.wired.plugin.codegen.TypedAssetsCodeGenerator
import java.io.File
import kotlin.test.Test


class TypedAssetsCodeGeneratorTests {

    @Test
    fun simple() {
        val generator = TypedAssetsCodeGenerator(
            packageName = "nl.helicotech.wired",
            fileName = "Assets",
            assetFolder = File("src/test/resources/assets")
        )

        val result = generator.generate()

        println(result)
    }
}