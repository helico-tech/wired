package nl.helicotech.wired.assetmapper

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.io.File

class AssetCollectionResolverSpec : DescribeSpec({

    fun loadResource(name: String): File {
        return File(javaClass.classLoader.getResource(name)!!.file)
    }

    describe("AssetCollectionResolver") {
        val resolver = AssetCollectionResolver()

        it("should resolve a directory with files in it") {
            val root = resolver.resolve(loadResource("asset-mapper/asset-folder-1"))

            root.directories.size shouldBe 1
            root.files.size shouldBe 2

            root.directories[0].directories.size shouldBe 0
            root.directories[0].files.size shouldBe 0

            root.files[0].file.name shouldBe "javascript.js"
            root.files[1].file.name shouldBe "stylesheet.css"
        }
    }
})