package nl.foo.bar

import java.nio.`file`.Path
import nl.helicotech.wired.assetmapper.Asset
import nl.helicotech.wired.assetmapper.AssetContainer

public object Assets : AssetContainer {
  public override val logicalPath: Path = Path.of("src/test/resources/assets")

  public override var mountPath: Path? = null

  public val Main: Asset.JavaScript = Asset.JavaScript("", Path.of("main.js"), "123", Assets,
      listOf())

  public val Module: Asset.JavaScript = Asset.JavaScript("module", Path.of("module.js"), "456",
      Assets, listOf())
}
