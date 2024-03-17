package nl.helicotech.wired.assetmapper

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.io.File

class AssetResolverSpec : DescribeSpec({

    fun loadResource(name: String): File {
        return File(javaClass.classLoader.getResource(name)!!.file)
    }

    describe(AssetResolver::class.simpleName!!) {
        val resolver = AssetResolver()

        it("should resolve a directory with files in it") {
            val root = resolver.resolve(loadResource("asset-mapper/asset-folder-1"))

            root.directories.size shouldBe 1
            root.files.size shouldBe 2

            root.directories[0].directories.size shouldBe 0
            root.directories[0].files.size shouldBe 0

            root.files[0].file.name shouldBe "javascript.js"
            root.files[0].hash shouldBe "79f6e68bcfc3041cfe3a36d4f3683b745153f44a"

            root.files[1].file.name shouldBe "stylesheet.css"
            root.files[1].hash shouldBe "3b6fe7f23a0c5613628b4d395d4c5114c1699163"
        }
    }
})