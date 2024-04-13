package nl.foo.bar

import java.nio.`file`.Path
import nl.helicotech.wired.assetmapper.AssetContainer

public object Assets : AssetContainer {
  public override val logicalPath: Path = Path.of("src/test/resources/assets")

  public override val mountPath: Path? = null
}
