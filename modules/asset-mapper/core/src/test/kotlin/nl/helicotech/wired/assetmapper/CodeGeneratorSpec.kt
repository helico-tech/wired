package nl.helicotech.wired.assetmapper

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.resource.resourceAsString
import io.kotest.matchers.shouldBe
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

        it("should add the dependencies to the assets") {
            val container = mutableAssetContainer("/dependency-resolver")

            container.addJavaScriptAsset(Path("app.js"), "123", null)
            container.addJavaScriptAsset(Path("duck.js"), "123", null)

            container.addJavaScriptAsset(Path("subdirectory/cow.js"), "123", null)
            container.addJavaScriptAsset(Path("vendors/@namespace/company/lib.js"), "123", "@namespace/company")

            val codeGenerator = createCodeGenerator(container)

            val fileSpec = codeGenerator.generate()

            val expected = """
                package nl.foo.bar

                import java.nio.`file`.Path
                import kotlin.collections.List
                import nl.helicotech.wired.assetmapper.Asset
                import nl.helicotech.wired.assetmapper.AssetContainer

                public object Dependency_resolver : AssetContainer {
                  public val App: Asset.JavaScript = Asset.JavaScript("", Path.of("app.js"), "123",
                      Dependency_resolver, listOf())

                  public val Duck: Asset.JavaScript = Asset.JavaScript("", Path.of("duck.js"), "123",
                      Dependency_resolver, listOf())

                  public override val logicalPath: Path = Path.of("/dependency-resolver")

                  public override var mountPath: Path? = null

                  public override val assets: List<Asset> = listOf(App, Duck, Subdirectory.Cow,
                      Vendors._namespace.Company.Lib)

                  public object Vendors {
                    public object _namespace {
                      public object Company {
                        public val Lib: Asset.JavaScript = Asset.JavaScript("@namespace/company",
                            Path.of("vendors/@namespace/company/lib.js"), "123", Dependency_resolver, listOf())
                      }
                    }
                  }

                  public object Subdirectory {
                    public val Cow: Asset.JavaScript = Asset.JavaScript("", Path.of("subdirectory/cow.js"), "123",
                        Dependency_resolver, listOf())
                  }
                }
                
            """.trimIndent()

            fileSpec.toString() shouldBe expected
        }
    }
})