package org.patheloper.paper;

import org.patheloper.api.terrain.TerrainProvider;
import org.patheloper.api.wrapper.PathBlock;
import org.patheloper.api.wrapper.PathPosition;

public class PaperTerrainProvider implements TerrainProvider {
  @Override
  public PathBlock getBlock(PathPosition position) {
    PatheticPaper pp = PatheticPaper.getInstance();
    return pp.toPathBlock(pp.toLocation(position).getBlock());
  }
}
